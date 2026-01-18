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

    const rilipackcore_copy = CopyModJar.create(b, rilipackcore_opts);
    const tfmgcastingfix_copy = CopyModJar.create(b, tfmgcastingfix_opts);
    const createezstotick_copy = CopyModJar.create(b, createezstotick_opts);

    rilipackcore_copy.step.dependOn(&rilipackcore.step);
    tfmgcastingfix_copy.step.dependOn(&tfmgcastingfix.step);
    createezstotick_copy.step.dependOn(&createezstotick.step);

    const mods_copy = b.step("mods-copy", "Copy mod jars to mods folder");
    mods_copy.dependOn(&rilipackcore_copy.step);
    mods_copy.dependOn(&tfmgcastingfix_copy.step);
    mods_copy.dependOn(&createezstotick_copy.step);

    const install_js_deps = addInstallJsDeps(b);
    const ts = addCompileTs(b);
    ts.step.dependOn(&install_js_deps.step);

    const kubejs = b.step("kubejs", "Build kubejs scripts");
    kubejs.dependOn(&ts.step);

    const pw_refresh = addPwRefresh(b);
    pw_refresh.step.dependOn(kubejs);
    pw_refresh.step.dependOn(mods_copy);

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

const CopyModJar = struct {
    step: std.Build.Step,
    mod_name: []const u8,
    mod_path: std.Build.LazyPath,
    mod_version: []const u8,
    output_dir: []const u8,

    pub const base_id: std.Build.Step.Id = .custom;

    pub fn create(owner: *std.Build, options: ModOptions) *CopyModJar {
        const mod_path = owner.path(options.path);

        const copy_jar = owner.allocator.create(CopyModJar) catch @panic("OOM");
        copy_jar.* = .{
            .step = std.Build.Step.init(.{
                .id = base_id,
                .name = owner.fmt("{s}-copy", .{options.name}),
                .owner = owner,
                .makeFn = make,
            }),
            .mod_name = owner.dupe(options.name),
            .mod_path = mod_path.dupe(owner),
            .mod_version = owner.dupe(options.version),
            .output_dir = owner.dupe("mods"),
        };

        return copy_jar;
    }

    fn make(step: *std.Build.Step, options: std.Build.Step.MakeOptions) !void {
        _ = options;
        const b = step.owner;
        const copy_jar: *CopyModJar = @fieldParentPtr("step", step);

        const filename = b.fmt("{s}-{s}.jar", .{ copy_jar.mod_name, copy_jar.mod_version });
        const source_path = b.pathJoin(&[_][]const u8{
            copy_jar.mod_path.getPath(b),
            "build",
            "libs",
            filename,
        });

        const dest_path = b.pathJoin(&[_][]const u8{
            copy_jar.output_dir,
            filename,
        });

        std.fs.cwd().copyFile(source_path, std.fs.cwd(), dest_path, .{}) catch |err| {
            return step.fail("failed to copy jar from {s} to {s}: {s}", .{
                source_path,
                dest_path,
                @errorName(err),
            });
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
