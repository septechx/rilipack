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
    const botania_opts = ModOptions{
        .name = "botania-neoforge-1.21.1",
        .path = "Botania",
        .version = "455-SNAPSHOT",
        .jar_dir = "NeoForge/build/libs",
    };
    const hexcasting_opts = ModOptions{
        .name = "hexcasting-neoforge-1.21.1",
        .path = "HexMod",
        .version = "0.12.0-devel",
        .jar_dir = "Neoforge/build/libs",
    };
    const mctimmersivetechnology_opts = ModOptions{
        .name = "MCT-ImmersiveTechnology-1.21.1",
        .path = "MCT-Immersive-Technology",
        .version = "3.0.0-b*-alpha",
    };
    const immersiveconvergence_opts = ModOptions{
        .name = "ImmersiveConvergence-1.21.1",
        .path = "ImmersiveConvergence-21",
        .version = "2.0.0-b*-beta",
    };

    const rilipackcore = BuildMod.create(b, rilipackcore_opts);
    const tfmgcastingfix = BuildMod.create(b, tfmgcastingfix_opts);
    const botania = BuildMod.create(b, botania_opts);
    const hexcasting = BuildMod.create(b, hexcasting_opts);
    const mctimmersivetechnology = BuildMod.create(b, mctimmersivetechnology_opts);
    const immersiveconvergence = BuildMod.create(b, immersiveconvergence_opts);

    const rilipackcore_copy = CopyModJar.create(b, rilipackcore_opts);
    const tfmgcastingfix_copy = CopyModJar.create(b, tfmgcastingfix_opts);
    const botania_copy = CopyModJar.create(b, botania_opts);
    const hexcasting_copy = CopyModJar.create(b, hexcasting_opts);
    const mctimmersivetechnology_copy = CopyModJar.create(b, mctimmersivetechnology_opts);
    const immersiveconvergence_copy = CopyModJar.create(b, immersiveconvergence_opts);

    rilipackcore_copy.step.dependOn(&rilipackcore.step);
    tfmgcastingfix_copy.step.dependOn(&tfmgcastingfix.step);
    botania_copy.step.dependOn(&botania.step);
    hexcasting_copy.step.dependOn(&hexcasting.step);
    immersiveconvergence_copy.step.dependOn(&immersiveconvergence.step);
    mctimmersivetechnology_copy.step.dependOn(&mctimmersivetechnology.step);
    mctimmersivetechnology.step.dependOn(&immersiveconvergence.step);

    const mods_copy = b.step("mods-copy", "Copy mod jars to mods folder");
    mods_copy.dependOn(&rilipackcore_copy.step);
    mods_copy.dependOn(&tfmgcastingfix_copy.step);
    mods_copy.dependOn(&botania_copy.step);
    mods_copy.dependOn(&hexcasting_copy.step);
    mods_copy.dependOn(&mctimmersivetechnology_copy.step);
    mods_copy.dependOn(&immersiveconvergence_copy.step);

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
    jar_dir: []const u8 = "build/libs",
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
    version: []const u8,
    jar_dir: []const u8,

    pub const base_id: std.Build.Step.Id = .custom;

    pub fn create(owner: *std.Build, options: ModOptions) *CopyModJar {
        const mod_path = owner.path(options.path);

        const jar_dir = owner.pathJoin(&[_][]const u8{
            mod_path.getPath(owner),
            options.jar_dir,
        });

        const copy_jar = owner.allocator.create(CopyModJar) catch @panic("OOM");
        copy_jar.* = .{
            .step = std.Build.Step.init(.{
                .id = base_id,
                .name = owner.fmt("{s}-copy", .{options.name}),
                .owner = owner,
                .makeFn = make,
            }),
            .mod_name = owner.dupe(options.name),
            .version = owner.dupe(options.version),
            .jar_dir = owner.dupe(jar_dir),
        };

        return copy_jar;
    }

    fn isExcludedJar(name: []const u8) bool {
        const excluded_suffixes = [_][]const u8{
            "-sources.jar",
            "-javadoc.jar",
            "-datagen.jar",
            "-dev-shadow.jar",
            "-dev.jar",
            "-shaded.jar",
            "-shadow.jar",
            "-classes.jar",
            "-test.jar",
        };

        for (excluded_suffixes) |suffix| {
            if (std.mem.endsWith(u8, name, suffix))
                return true;
        }

        return false;
    }

    fn getBuildNumber(version: []const u8) ?u64 {
        // Expecting something like:
        // 3.0.0-b1090-alpha
        //
        // Find "-b", then parse the digits following it.
        const b_pos = std.mem.indexOf(u8, version, "-b") orelse return null;
        const build_start = b_pos + 2;

        var build_end = build_start;
        while (build_end < version.len and std.ascii.isDigit(version[build_end])) {
            build_end += 1;
        }

        if (build_end == build_start)
            return null;

        return std.fmt.parseInt(
            u64,
            version[build_start..build_end],
            10,
        ) catch null;
    }

    fn getVersionPrefix(version: []const u8) []const u8 {
        // "3.0.0-b*-alpha" -> "3.0.0-b"
        const wildcard = std.mem.indexOfScalar(u8, version, '*') orelse return version;
        return version[0..wildcard];
    }

    fn getVersionSuffix(version: []const u8) []const u8 {
        // "3.0.0-b*-alpha" -> "-alpha"
        const wildcard = std.mem.indexOfScalar(u8, version, '*') orelse return "";

        return version[wildcard + 1 ..];
    }

    fn isCandidateJar(
        mod_name: []const u8,
        version: []const u8,
        name: []const u8,
    ) ?u64 {
        if (!std.mem.startsWith(u8, name, mod_name))
            return null;

        const rest = name[mod_name.len..];

        if (rest.len == 0 or rest[0] != '-')
            return null;

        if (!std.mem.endsWith(u8, name, ".jar"))
            return null;

        if (isExcludedJar(name))
            return null;

        const version_prefix = getVersionPrefix(version);
        const version_suffix = getVersionSuffix(version);

        // Strip:
        //   <mod-name>-
        // leaving:
        //   3.0.0-b1090-alpha.jar
        const version_name = rest[1..];

        if (!std.mem.startsWith(u8, version_name, version_prefix))
            return null;

        if (version_suffix.len != 0) {
            const version_without_ext = version_name[0 .. version_name.len - ".jar".len];

            if (!std.mem.endsWith(u8, version_without_ext, version_suffix))
                return null;
        }

        // The wildcard is the build number, so parse it.
        // Without a wildcard there is nothing to sort by — accept the match.
        if (std.mem.indexOfScalar(u8, version, '*') == null)
            return 0;
        return getBuildNumber(version_name);
    }
    fn make(step: *std.Build.Step, options: std.Build.Step.MakeOptions) !void {
        const b = step.owner;
        const io = b.graph.io;
        const copy_jar: *CopyModJar = @fieldParentPtr("step", step);

        var source_dir = std.Io.Dir.cwd().openDir(io, copy_jar.jar_dir, .{
            .iterate = true,
        }) catch |err| {
            return step.fail(
                "failed to open jar directory '{s}': {s}",
                .{ copy_jar.jar_dir, @errorName(err) },
            );
        };
        defer source_dir.close(io);

        var newest_name: ?[]u8 = null;
        var newest_build: u64 = 0;

        var iter = source_dir.iterate();
        while (try iter.next(io)) |entry| {
            const build_number = isCandidateJar(
                copy_jar.mod_name,
                copy_jar.version,
                entry.name,
            ) orelse continue;

            if (newest_name == null or build_number > newest_build) {
                if (newest_name) |old| b.allocator.free(old);
                newest_name = b.allocator.dupe(
                    u8,
                    entry.name,
                ) catch return step.fail("OOM duplicating jar name", .{});
                newest_build = build_number;
            }
        }

        const jar_name = newest_name orelse {
            return step.fail(
                "no matching jar found for {s} version {s} in {s}",
                .{
                    copy_jar.mod_name,
                    copy_jar.version,
                    copy_jar.jar_dir,
                },
            );
        };

        const output_dir_path = "mods";

        std.Io.Dir.cwd().createDirPath(io, output_dir_path) catch |err| {
            return step.fail(
                "failed to create '{s}': {s}",
                .{ output_dir_path, @errorName(err) },
            );
        };

        var output_dir = std.Io.Dir.cwd().openDir(io, output_dir_path, .{
            .iterate = true,
        }) catch |err| {
            return step.fail(
                "failed to open '{s}': {s}",
                .{ output_dir_path, @errorName(err) },
            );
        };
        defer output_dir.close(io);

        source_dir.copyFile(
            jar_name,
            output_dir,
            jar_name,
            io,
            .{},
        ) catch |err| {
            return step.fail(
                "failed to copy '{s}' to '{s}': {s}",
                .{ jar_name, output_dir_path, @errorName(err) },
            );
        };

        _ = options;
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
