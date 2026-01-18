set dotenv-load

# Default recipe
@export-curseforge: refresh
  packwiz curseforge export

[working-directory: 'rilipackcore']
@build-rilipackcore:
  ./gradlew build

[working-directory: 'TFMGCastingFix']
@build-tfmgcastingfix:
  ./gradlew build

[working-directory: 'CreateEzStockTickerBackported']
@build-createezstocktickerbackported:
  ./gradlew build

@build-mods: build-rilipackcore build-tfmgcastingfix build-createezstocktickerbackported

[working-directory: 'kubejs']
@setup-ts:
  pnpm install

[working-directory: 'kubejs']
@compile-ts: setup-ts
  pnpm run build

@refresh: compile-ts build-mods
  echo ""
  packwiz refresh

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

@add mod:
  packwiz curseforge add {{mod}}

@instance-reload: refresh
  if [ -z "$INSTANCE_PATH" ]; then echo "Please set the INSTANCE_PATH environment variable in .env to the path of your instance"; exit 1; fi

  echo "\nReloading instance at $INSTANCE_PATH"

  rm -rf "$INSTANCE_PATH/kubejs/data"
  cp -r kubejs/data "$INSTANCE_PATH/kubejs/data"

  rm -rf "$INSTANCE_PATH/kubejs/server_scripts"
  rm -rf "$INSTANCE_PATH/kubejs/client_scripts"
  rm -rf "$INSTANCE_PATH/kubejs/startup_scripts"
  cp -r kubejs/server_scripts "$INSTANCE_PATH/kubejs/server_scripts"
  cp -r kubejs/client_scripts "$INSTANCE_PATH/kubejs/client_scripts"
  cp -r kubejs/startup_scripts "$INSTANCE_PATH/kubejs/startup_scripts"

  echo "Instance reloaded successfully!"
