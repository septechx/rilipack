set dotenv-load

@build:
  zig build

@curseforge: build
  packwiz curseforge export

@modrinth: build
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

@clean-mods:
  rm -rf rilipackcore/build
  rm -rf TFMGCastingFix/build
  rm -rf CreateEzStockTickerBackported/build
  rm -f mods/rilipackcore-*.jar
  rm -f mods/tfmgcastingfix-*.jar
  rm -f mods/create_ez_stock_ticker-*.jar

@clean: clean-ts clean-out clean-mods

@add mod:
  packwiz curseforge add {{mod}}

@reload: build
  if [ -z "$INSTANCE_PATH" ]; then echo "Please set the INSTANCE_PATH environment variable in .env to the path of your instance"; exit 1; fi

  echo "\nReloading instance at $INSTANCE_PATH"

  rm -rf "$INSTANCE_PATH/kubejs/data"
  cp -r kubejs/data "$INSTANCE_PATH/kubejs/data"

  rm -rf "$INSTANCE_PATH/kubejs/assets"
  cp -r kubejs/assets "$INSTANCE_PATH/kubejs/assets"

  rm -rf "$INSTANCE_PATH/kubejs/server_scripts"
  rm -rf "$INSTANCE_PATH/kubejs/client_scripts"
  rm -rf "$INSTANCE_PATH/kubejs/startup_scripts"
  cp -r kubejs/server_scripts "$INSTANCE_PATH/kubejs/server_scripts"
  cp -r kubejs/client_scripts "$INSTANCE_PATH/kubejs/client_scripts"
  cp -r kubejs/startup_scripts "$INSTANCE_PATH/kubejs/startup_scripts"

  echo "Instance reloaded successfully!"
