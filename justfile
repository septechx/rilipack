[working-directory: 'kubejs']
@setup-ts:
  pnpm install

[working-directory: 'kubejs']
@compile-ts: setup-ts
  pnpm run build

@export-curseforge: refresh
  packwiz curseforge export

@export-modrinth: refresh
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

@refresh: compile-ts
  packwiz refresh

add mod:
  packwiz curseforge add {{mod}}
