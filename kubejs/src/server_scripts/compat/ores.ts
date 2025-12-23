LootJS.modifiers((e) => {
  const replaceDrop = __replaceDrop(e);

  replaceDrop(
    "occultism:silver_ore",
    "occultism:raw_silver",
    "immersiveengineering:raw_silver",
  );
  replaceDrop(
    "occultism:silver_ore_deepslate",
    "occultism:raw_silver",
    "immersiveengineering:raw_silver",
  );
  replaceDrop(
    "xycraft_world:aluminum_ore_stone",
    "xycraft_world:raw_aluminum",
    "immersiveengineering:raw_aluminum",
  );
  replaceDrop(
    "xycraft_world:aluminum_ore_deepslate",
    "xycraft_world:raw_aluminum",
    "immersiveengineering:raw_aluminum",
  );
  replaceDrop(
    "xycraft_world:aluminum_ore_kivi",
    "xycraft_world:raw_aluminum",
    "immersiveengineering:raw_aluminum",
  );
  replaceDrop(
    "tfmg:lead_ore",
    "tfmg:raw_lead",
    "immersiveengineering:raw_lead",
  );
  replaceDrop(
    "tfmg:deepslate_lead_ore",
    "tfmg:raw_lead",
    "immersiveengineering:raw_lead",
  );
  replaceDrop(
    "mekanism:lead_ore",
    "mekanism:raw_lead",
    "immersiveengineering:raw_lead",
  );
  replaceDrop(
    "mekanism:deepslate_lead_ore",
    "mekanism:raw_lead",
    "immersiveengineering:raw_lead",
  );
  replaceDrop(
    "tfmg:nickel_ore",
    "tfmg:raw_nickel",
    "immersiveengineering:raw_nickel",
  );
  replaceDrop(
    "tfmg:deepslate_nickel_ore",
    "tfmg:raw_nickel",
    "immersiveengineering:raw_nickel",
  );
  replaceDrop(
    "mekanism:uranium_ore",
    "mekanism:raw_uranium",
    "immersiveengineering:raw_uranium",
  );
  replaceDrop(
    "mekanism:deepslate_uranium_ore",
    "mekanism:raw_uranium",
    "immersiveengineering:raw_uranium",
  );
});

function __replaceDrop(event: LootJS.ModifiersEvent) {
  return (ore: ItemLike, from: ItemLike, to: ItemLike) => {
    event.addBlockLootModifier(ore).replaceLoot(from, to, true);
  };
}
