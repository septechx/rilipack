LootJS.modifiers((e) => {
  const replaceOreDrop = __replaceOreDrop(e);

  replaceOreDrop(
    "occultism:silver_ore",
    "occultism:raw_silver",
    "immersiveengineering:raw_silver",
  );
  replaceOreDrop(
    "occultism:silver_ore_deepslate",
    "occultism:raw_silver",
    "immersiveengineering:raw_silver",
  );
  replaceOreDrop(
    "xycraft_world:aluminum_ore_stone",
    "xycraft_world:raw_aluminum",
    "immersiveengineering:raw_aluminum",
  );
  replaceOreDrop(
    "xycraft_world:aluminum_ore_deepslate",
    "xycraft_world:raw_aluminum",
    "immersiveengineering:raw_aluminum",
  );
  replaceOreDrop(
    "xycraft_world:aluminum_ore_kivi",
    "xycraft_world:raw_aluminum",
    "immersiveengineering:raw_aluminum",
  );
  replaceOreDrop(
    "tfmg:lead_ore",
    "tfmg:raw_lead",
    "immersiveengineering:raw_lead",
  );
  replaceOreDrop(
    "tfmg:deepslate_lead_ore",
    "tfmg:raw_lead",
    "immersiveengineering:raw_lead",
  );
  replaceOreDrop(
    "mekanism:lead_ore",
    "mekanism:raw_lead",
    "immersiveengineering:raw_lead",
  );
  replaceOreDrop(
    "mekanism:deepslate_lead_ore",
    "mekanism:raw_lead",
    "immersiveengineering:raw_lead",
  );
  replaceOreDrop(
    "tfmg:nickel_ore",
    "tfmg:raw_nickel",
    "immersiveengineering:raw_nickel",
  );
  replaceOreDrop(
    "tfmg:deepslate_nickel_ore",
    "tfmg:raw_nickel",
    "immersiveengineering:raw_nickel",
  );
  replaceOreDrop(
    "mekanism:uranium_ore",
    "mekanism:raw_uranium",
    "immersiveengineering:raw_uranium",
  );
  replaceOreDrop(
    "mekanism:deepslate_uranium_ore",
    "mekanism:raw_uranium",
    "immersiveengineering:raw_uranium",
  );
});

function __replaceOreDrop(event: LootJS.ModifiersEvent) {
  return (ore: ItemLike, from: ItemLike, to: ItemLike) => {
    event.addBlockLootModifier(ore).replaceLoot(from, to, true);
  };
}
