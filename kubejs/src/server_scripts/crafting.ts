ServerEvents.recipes((e) => {
  const replaceInput = __replaceInput(e);

  e.replaceInput(
    { id: "easy_villagers:auto_trader" },
    "minecraft:netherite_ingot",
    "create:precision_mechanism",
  );

  e.replaceInput(
    { id: "immersiveengineering:crafting/blastbrick" },
    "minecraft:magma_block",
    "tfmg:fireproof_bricks",
  );

  e.replaceInput(
    { id: "tfmg:crafting/kinetics/coke_oven" },
    "create:industrial_iron_block",
    "#forge:sheetmetals/steel",
  );

  e.replaceInput(
    { id: "cookingforblockheads:toaster" },
    "minecraft:lava_bucket",
    "create:blaze_burner",
  );

  e.replaceInput(
    { id: "cookingforblockheads:crafting_book" },
    "minecraft:diamond",
    "#forge:plates/brass",
  );

  e.replaceInput(
    { id: "immersiveengineering:crafting/coil_lv" },
    "minecraft:iron_ingot",
    "tfmg:magnetic_alloy_ingot",
  );

  e.replaceInput(
    { id: "immersiveengineering:crafting/coil_mv" },
    "minecraft:iron_ingot",
    "tfmg:magnetic_alloy_ingot",
  );

  e.replaceInput(
    { id: "immersiveengineering:crafting/coil_hv" },
    "minecraft:iron_ingot",
    "tfmg:magnetic_alloy_ingot",
  );

  e.replaceInput(
    { id: "dankstorage:dank_1" },
    "minecraft:barrel",
    "create:item_vault",
  );

  e.replaceInput(
    { id: "tfmg:crafting/kinetics/casting_basin" },
    "tfmg:cast_iron_pipe",
    "tfmg:steel_pipe",
  );

  replaceInput("actuallyadditions:rice", "#forge:crops/rice");
  replaceInput("tfmg:hardened_planks", "#forge:treated_wood");
  replaceInput("cyclic:biomass", "#forge:fuels/bio");
  replaceInput("tfmg:nitrate_dust", "#forge:dusts/saltpeter");
  replaceInput(
    "cyclic:obsidian_pressure_plate",
    "quark:obsidian_pressure_plate",
  );
  replaceInput(
    "immersiveengineering:component_electronic_adv",
    "tfmg:circuit_board",
  );

  e.shaped(Item.of("mm:blast_forge_controller"), ["BMB", "POP", "BMB"], {
    B: "immersiveengineering:blastbrick",
    M: "create:precision_mechanism",
    P: "#forge:plates/cast_iron",
    O: "tfmg:blast_furnace_output",
  });

  e.shaped(Item.of("mm:blast_forge_fluid_input"), ["BBB", "BVB", "BBB"], {
    B: "immersiveengineering:blastbrick",
    V: "minecraft:bucket",
  });

  e.shaped(Item.of("mm:blast_forge_item_input"), ["BBB", "BCB", "BBB"], {
    B: "immersiveengineering:blastbrick",
    C: "#forge:chests",
  });

  e.shapeless(Item.of("mm:blast_forge_item_output"), [
    "mm:blast_forge_item_input",
  ]);

  e.shapeless(Item.of("mm:blast_forge_item_input"), [
    "mm:blast_forge_item_output",
  ]);

  e.shapeless(Item.of("tfmg:fireproof_brick", 4), ["tfmg:fireproof_bricks"]);

  e.shaped(Item.of("mm:blast_forge_vent", 3), ["BBB", "PPP", "BBB"], {
    B: "immersiveengineering:blastbrick",
    P: "#forge:plates/cast_iron",
  });

  e.shaped(
    Item.of("immersiveengineering:rs_engineering", 4),
    ["SPS", "RMR", "SPS"],
    {
      S: "#forge:sheetmetals/iron",
      P: "#forge:ingots/plastic",
      R: "#forge:dusts/redstone",
      M: "tfmg:magnetic_alloy_ingot",
    },
  );

  e.shaped(
    Item.of("immersiveengineering:heavy_engineering", 4),
    ["SES", "CMC", "SES"],
    {
      S: "#forge:sheetmetals/steel",
      E: "#forge:ingots/electrum",
      C: "immersiveengineering:component_steel",
      M: "tfmg:steel_mechanism",
    },
  );

  e.shaped(
    Item.of("immersiveengineering:component_steel"),
    ["S S", " M ", "S S"],
    {
      S: "#forge:plates/steel",
      M: "tfmg:magnetic_alloy_ingot",
    },
  );

  e.shaped(
    Item.of("immersiveengineering:component_iron"),
    ["S S", " M ", "S S"],
    {
      S: "#forge:plates/iron",
      M: "tfmg:magnetic_alloy_ingot",
    },
  );

  e.shaped(Item.of("tfmg:empty_circuit_board"), ["   ", "DCD", "PPP"], {
    D: "immersiveengineering:plate_duroplast",
    C: "#forge:dyes/green",
    P: "#forge:ingots/plastic",
  });

  e.shaped(Item.of("cyclic:disenchanter"), [" C ", "BTB", "PRP"], {
    C: "cyclic:gem_obsidian",
    B: "#forge:plates/brass",
    T: "minecraft:enchanting_table",
    P: "quark:obsidian_pressure_plate",
    R: "create_enchantment_industry:experience_rotor",
  });

  e.shaped(Item.of("tfmg:cast_iron_chemical_vat", 3), ["CCC", "LTL", "CCC"], {
    C: "#forge:plates/cast_iron",
    L: "#forge:plates/lead",
    T: "tfmg:cast_iron_fluid_tank",
  });

  e.shaped(Item.of("tfmg:steel_chemical_vat", 3), ["CCC", "LTL", "CCC"], {
    C: "#forge:plates/steel",
    L: "#forge:plates/nickel",
    T: "tfmg:steel_fluid_tank",
  });

  e.shaped(Item.of("tfmg:fireproof_chemical_vat", 3), ["BRB", "CSC", "BRB"], {
    B: "tfmg:fireproof_bricks",
    R: "#forge:ingots/rubber",
    C: "tfmg:circuit_board",
    S: "tfmg:steel_chemical_vat",
  });
});

function __replaceInput(e: ServerEvents.RecipeEvent) {
  return (toReplace: string, replacement: string) => {
    e.replaceInput({ input: toReplace }, toReplace, replacement);
  };
}
