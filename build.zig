const std = @import("std");

pub fn build(b: *std.Build) void {
    const rilipackcore_opts = ModOptions{
        .name = "rilipackcore",
        .path = "rilipackcore",
        .version = "0.1.0",
    };
    const tfmgcastingfix_opts = ModOptions{
        .name = "tfmgcastingfix",
        .path = "TFMGCastingFix",
        .version = "0.1.1+mc1.20.1",
    };
    const createezstotick_opts = ModOptions{
        .name = "create_ez_stock_ticker",
        .path = "CreateEzStockTickerBackported",
        .version = "1.0.6+mc1.20.1",
    };

    const rilipackcore = BuildMod.create(b, rilipackcore_opts);
    const tfmgcastingfix = BuildMod.create(b, tfmgcastingfix_opts);
    const createezstotick = BuildMod.create(b, createezstotick_opts);

    const rilipackcore_meta = GenerateModMeta.create(b, rilipackcore_opts);
    const tfmgcastingfix_meta = GenerateModMeta.create(b, tfmgcastingfix_opts);
    const createezstotick_meta = GenerateModMeta.create(b, createezstotick_opts);

    rilipackcore_meta.step.dependOn(&rilipackcore.step);
    tfmgcastingfix_meta.step.dependOn(&tfmgcastingfix.step);
    createezstotick_meta.step.dependOn(&createezstotick.step);

    const mods_meta = b.step("mods-meta", "Generate mod metadata");
    mods_meta.dependOn(&rilipackcore_meta.step);
    mods_meta.dependOn(&tfmgcastingfix_meta.step);
    mods_meta.dependOn(&createezstotick_meta.step);

    const install_js_deps = addInstallJsDeps(b);
    const ts = addCompileTs(b);
    ts.step.dependOn(&install_js_deps.step);

    const kubejs = b.step("kubejs", "Build kubejs scripts");
    kubejs.dependOn(&ts.step);

    const pw_refresh = addPwRefresh(b);
    pw_refresh.step.dependOn(kubejs);
    pw_refresh.step.dependOn(mods_meta);

    const refresh = b.step("refresh", "Refresh the pack");
    refresh.dependOn(&pw_refresh.step);

    const build_step = b.getInstallStep();
    build_step.dependOn(refresh);
}

const ModOptions = struct {
    name: []const u8,
    path: []const u8,
    version: []const u8,
};

const BuildMod = struct {
    step: std.Build.Step,
    mod_name: []const u8,
    mod_path: std.Build.LazyPath,
    gradlew_path: std.Build.LazyPath,

    pub const base_id: std.Build.Step.Id = .custom;

    pub fn create(owner: *std.Build, options: ModOptions) *BuildMod {
        const mod_path = owner.path(options.path);

        const build_mod = owner.allocator.create(BuildMod) catch @panic("OOM");
        build_mod.* = .{
            .step = std.Build.Step.init(.{
                .id = base_id,
                .name = options.name,
                .owner = owner,
                .makeFn = make,
            }),
            .mod_name = owner.dupe(options.name),
            .mod_path = mod_path.dupe(owner),
            .gradlew_path = mod_path.path(owner, "gradlew").dupe(owner),
        };
        return build_mod;
    }

    fn make(step: *std.Build.Step, options: std.Build.Step.MakeOptions) !void {
        const b = step.owner;
        const build_mod: *BuildMod = @fieldParentPtr("step", step);

        const gradlew_run = b.addSystemCommand(&[_][]const u8{
            build_mod.gradlew_path.getPath(b),
            "--no-daemon",
            "build",
        });
        gradlew_run.addPrefixedDirectoryArg("-p", build_mod.mod_path);

        gradlew_run.step.make(options) catch |err| {
            return step.fail("gradle build failed: {s}", .{@errorName(err)});
        };
    }
};

const GenerateModMeta = struct {
    step: std.Build.Step,
    mod_name: []const u8,
    mod_path: std.Build.LazyPath,
    mod_path_rel: []const u8,
    mod_version: []const u8,
    output_path: []const u8,

    pub const base_id: std.Build.Step.Id = .custom;

    pub fn create(owner: *std.Build, options: ModOptions) *GenerateModMeta {
        const mod_path = owner.path(options.path);

        const gen_meta = owner.allocator.create(GenerateModMeta) catch @panic("OOM");
        gen_meta.* = .{
            .step = std.Build.Step.init(.{
                .id = base_id,
                .name = owner.fmt("{s}-meta", .{options.name}),
                .owner = owner,
                .makeFn = make,
            }),
            .mod_name = owner.dupe(options.name),
            .mod_path = mod_path.dupe(owner),
            .mod_path_rel = owner.dupe(options.path),
            .mod_version = owner.dupe(options.version),
            .output_path = owner.dupe(owner.fmt("mods/{s}.pw.toml", .{options.name})),
        };

        return gen_meta;
    }

    fn make(step: *std.Build.Step, options: std.Build.Step.MakeOptions) !void {
        _ = options;
        const b = step.owner;
        const gen_meta: *GenerateModMeta = @fieldParentPtr("step", step);

        const filename = b.fmt("{s}-{s}.jar", .{ gen_meta.mod_name, gen_meta.mod_version });
        const jar_abs_path = b.pathJoin(&[_][]const u8{
            gen_meta.mod_path.getPath(b),
            "build",
            "libs",
            filename,
        });

        const jar_rel_path = b.pathJoin(&[_][]const u8{
            gen_meta.mod_path_rel,
            "build",
            "libs",
            filename,
        });

        var file = std.fs.cwd().openFile(jar_abs_path, .{}) catch |err| {
            return step.fail("failed to open mod jar for {s} at {s}: {s}", .{
                gen_meta.mod_name,
                jar_abs_path,
                @errorName(err),
            });
        };
        defer file.close();
        const data = file.readToEndAlloc(b.allocator, std.math.maxInt(usize)) catch |err| {
            return step.fail("failed to read jar file: {s}", .{@errorName(err)});
        };
        defer b.allocator.free(data);

        var hasher = std.crypto.hash.sha2.Sha256.init(.{});
        hasher.update(data);
        var digest: [32]u8 = undefined;
        hasher.final(&digest);

        var buf: [64]u8 = undefined;
        var fbs = std.io.fixedBufferStream(&buf);
        const writer = fbs.writer();
        for (digest) |byte| {
            writer.print("{x:0>2}", .{byte}) catch unreachable;
        }
        const hex_digest = fbs.getWritten();

        const content = try std.mem.join(b.allocator, "\n", &[_][]const u8{
            b.fmt("name = \"{s}\"", .{gen_meta.mod_name}),
            b.fmt("filename = \"{s}\"", .{filename}),
            "side = \"both\"",
            "",
            "[download]",
            b.fmt("url = \"./{s}\"", .{jar_rel_path}),
            "hash-format = \"sha256\"",
            b.fmt("hash = \"{s}\"", .{hex_digest}),
        });
        defer b.allocator.free(content);

        std.fs.cwd().writeFile(.{
            .sub_path = gen_meta.output_path,
            .data = content,
        }) catch |err| {
            return step.fail("failed to write metadata file: {s}", .{@errorName(err)});
        };
    }
};

fn addInstallJsDeps(b: *std.Build) *std.Build.Step.Run {
    const pnpm_install = b.addSystemCommand(&[_][]const u8{ "pnpm", "-C", "kubejs", "install" });
    pnpm_install.step.name = "install-js-deps";
    return pnpm_install;
}

fn addCompileTs(b: *std.Build) *std.Build.Step.Run {
    const pnpm_build = b.addSystemCommand(&[_][]const u8{ "pnpm", "-C", "kubejs", "run", "build" });
    pnpm_build.step.name = "compile-ts";
    return pnpm_build;
}

fn addPwRefresh(b: *std.Build) *std.Build.Step.Run {
    const refresh = b.addSystemCommand(&[_][]const u8{ "packwiz", "refresh" });
    refresh.step.name = "pw-refresh";
    return refresh;
}
