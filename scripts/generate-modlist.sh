#!/usr/bin/env bash

set -euo pipefail

output="modlist.md"

: >"$output"

for file in mods/*.toml; do
    [[ -e "$file" ]] || continue
    name=$(sed -n '1s/^name = "\(.*\)"$/\1/p' "$file")
    if [[ -n "$name" ]]; then
        printf '%s\n' "- [ ] $name" >>"$output"
    fi
done
