[working-directory: 'kubejs']
@setup-ts:
  pnpm install

[working-directory: 'kubejs']
@compile-ts: setup-ts
  pnpm run build

@export-curseforge: compile-ts
  packwiz curseforge export

@export-modrinth: compile-ts
  packwiz modrinth export

[working-directory: 'kubejs']
@clean-ts:
  rm -rf node_modules
  rm -rf client_scripts
  rm -rf server_scripts
  rm -rf startup_scripts

@clean-out:
  rm -rf *.zip
  rm -rf *.mrpack

@clean: clean-ts clean-out

@refresh:
  packwiz refresh
