const REMOVALS = {
  immersiveengineering: [
    "blastfurnace/steel_block",
    "blastfurnace/steel",
    "crafting/treated_wood_horizontal",
    "crafting/rs_engineering",
    "crafting/component_steel",
    "crafting/component_iron",
    "crafting/heavy_engineering",
  ],
  tfmg: [
    "industrial_blasting/steel",
    "industrial_blasting/steel_from_dust",
    "industrial_blasting/steel_from_raw_iron",
    "industrial_blasting/silicon",
    "vat_machine_recipe/arc_furnace_steel",
    "filling/hardened_planks",
    "crafting/kinetics/empty_circuit_board",
  ],
  jaopca: [
    "create.material_to_plate.plastic",
    "immersiveengineering.material_to_plate_hammer.plastic",
    "immersiveengineering.material_to_plate.plastic",
  ],
};

ServerEvents.recipes((e) => {
  global.forEachItem(REMOVALS, (ident) => e.remove({ id: ident }));
});
