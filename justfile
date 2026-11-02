set dotenv-load

mod_version := `sed -n 's/^mod_version=//p' rilipackcore/gradle.properties`

build: kubejs mod refresh

kubejs:
  pnpm -C kubejs install
  pnpm -C kubejs run build

mod:
  ./rilipackcore/gradlew --no-daemon -p rilipackcore build
  cp "rilipackcore/build/libs/rilipackcore-{{mod_version}}.jar" mods/

refresh:
  packwiz refresh

curseforge: build
  packwiz curseforge export

modrinth: build
  packwiz modrinth export

[working-directory: 'kubejs']
clean-ts:
  rm -rf node_modules
  rm -rf client_scripts
  rm -rf server_scripts
  rm -rf startup_scripts

clean-out:
  rm -rf *.zip
  rm -rf *.mrpack

clean-mods:
  rm -rf rilipackcore/build
  rm -f mods/rilipackcore-*.jar

clean: clean-ts clean-out clean-mods

add mod:
  packwiz curseforge add {{mod}}

reload: build
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
