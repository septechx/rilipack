import fs from "fs/promises";
import esbuild from "esbuild";
import { Chalk } from "chalk";

const chalk = new Chalk();
const config: esbuild.BuildOptions = {
  entryPoints: await findSourceFiles("./src"),
  target: "ES6",
  bundle: true,
  platform: "neutral",
  outdir: ".",
  supported: {
    "object-extensions": false,
    "array-spread": false,
  },
};

switch (process.argv[2]) {
  case "--watch":
    dev();
    break;
  default:
    build();
    break;
}

async function build() {
  console.log(chalk.blue("esbuild: building..."));
  const start = performance.now();

  const result = await esbuild.build(config);

  if (result.errors.length > 0) {
    console.error(chalk.red("esbuild: build failed."));
    console.error(result.errors);
    process.exit(1);
  }

  console.log(
    chalk.green(
      `esbuild: build succeeded in ${(performance.now() - start).toFixed(2)}ms.`,
    ),
  );
}

async function dev() {
  try {
    const ctx = await esbuild.context(config);
    await ctx.watch();
    console.log(chalk.blue("esbuild: watching for changes..."));
  } catch (e) {
    console.error(chalk.red("esbuild: build failed."));
    console.error(e);
    process.exit(1);
  }
}

async function findSourceFiles(dir: string) {
  const files: string[] = [];

  for await (const entry of await fs.opendir(dir)) {
    const filePath = dir + "/" + entry.name;

    if (entry.isDirectory()) {
      files.push(...(await findSourceFiles(filePath)));
    } else {
      if (filePath.endsWith(".d.ts")) {
        continue;
      }

      files.push(filePath);
    }
  }

  return files;
}
