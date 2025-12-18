ServerEvents.recipes((e) => {
  const c = e.recipes.create;
  const coe = e.recipes.createoreexcavation;
  const ctfmg = e.recipes.tfmg;

  const sequencedAssembly = __sequencedAssembly(c);

  c.mixing(
    ["minecraft:obsidian"],
    [Fluid.of("minecraft:water", 1000), Fluid.of("minecraft:lava", 1000)],
  );

  c.mixing(
    [Fluid.of("tfmg:liquid_silicon", 48)],
    ["#forge:gems/quartz"],
  ).superheated();

  c.mixing(
    [Fluid.of("tfmg:napalm", 250)],
    [
      "#forge:ingots/aluminum",
      Fluid.of("createdieselgenerators:gasoline", 1000),
    ],
  );

  c.mixing(
    [Fluid.of("kubejs:molten_obsidian_silicon_alloy", 144)],
    ["minecraft:obsidian", "#forge:ingots/silicon"],
  ).superheated();

  c.mixing(
    [Item.of("hostilenetworks:prediction_matrix", 8)],
    ["#forge:ingots/gold", "#forge:ingots/iron", "#forge:clay", "#forge:clay"],
  ).heated();

  c.filling("actuallyadditions:rice_slimeball", [
    Fluid.of("minecraft:water", 250),
    "actuallyadditions:rice_dough",
  ]);

  c.crushing(
    [
      Item.of("minecraft:blaze_powder", 4),
      Item.of("immersiveengineering:dust_sulfur").withChance(0.5),
    ],
    "minecraft:blaze_rod",
  );

  c.pressing(["quark:obsidian_pressure_plate"], "minecraft:obsidian");

  c.mechanical_crafting(
    "minecraft:enchanting_table",
    [" B ", "DTD", "POP", "OOO"],
    {
      P: "#forge:plates/obsidian",
      B: "minecraft:book",
      D: "#forge:gems/diamond",
      T: "create:railway_casing",
      O: "minecraft:obsidian",
    },
  );

  c.cutting([Item.of("laserio:logic_chip", 24)], "kubejs:logic_chip_wafer");
  c.cutting([Item.of("kubejs:silicon_wafer", 16)], "kubejs:silicon_boule");

  sequencedAssembly(
    "kubejs:incomplete_logic_chip_wafer",
    "kubejs:logic_chip_wafer",
    "kubejs:silicon_wafer",
    [
      [c.deploying, "tfmg:transistor_item"],
      [c.deploying, "#forge:wires/electrum"],
      [c.deploying, "tfmg:transistor_item"],
      [c.deploying, "#forge:wires/electrum"],
    ],
    8,
  );

  sequencedAssembly(
    "kubejs:incomplete_lv_capacitor",
    "immersiveengineering:capacitor_lv",
    "tfmg:steel_fluid_tank",
    [
      [c.deploying, "#forge:plates/lead"],
      [c.deploying, "tfmg:capacitor_item"],
      [c.deploying, "#forge:plates/steel"],
      [c.filling, Fluid.of("immersiveengineering:redstone_acid", 500)],
    ],
    8,
  );

  sequencedAssembly(
    "kubejs:incomplete_mv_capacitor",
    "immersiveengineering:capacitor_mv",
    "immersiveengineering:capacitor_lv",
    [
      [c.deploying, "#forge:plates/nickel"],
      [c.deploying, "tfmg:capacitor_item"],
      [c.deploying, "#forge:plates/steel"],
      [c.filling, Fluid.of("immersiveengineering:redstone_acid", 500)],
    ],
    8,
  );

  sequencedAssembly(
    "kubejs:incomplete_hv_capacitor",
    "immersiveengineering:capacitor_hv",
    "immersiveengineering:capacitor_mv",
    [
      [c.deploying, "#forge:plates/aluminum"],
      [c.deploying, "tfmg:capacitor_item"],
      [c.deploying, "#forge:ingots/hop_graphite"],
      [c.filling, Fluid.of("immersiveengineering:redstone_acid", 500)],
    ],
    8,
  );

  sequencedAssembly(
    "kubejs:incomplete_explorers_compass",
    "explorerscompass:explorerscompass",
    "minecraft:recovery_compass",
    [
      [c.deploying, "cyclic:gem_obsidian"],
      [c.deploying, "tfmg:copper_sulfate"],
      [c.deploying, "tfmg:steel_mechanism"],
      [c.filling, Fluid.of("kubejs:impure_mana", 200)],
    ],
    4,
  );

  sequencedAssembly(
    "tfmg:unfinished_steel_mechanism",
    "tfmg:steel_mechanism",
    "#forge:plates/steel",
    [
      [c.deploying, "tfmg:steel_cogwheel"],
      [c.deploying, "#forge:plates/nickel"],
      [c.deploying, "tfmg:large_steel_cogwheel"],
      [c.deploying, "#forge:plates/lead"],
      [c.deploying, "tfmg:screw"],
      [c.deploying, "tfmg:screwdriver"],
    ],
    2,
  );

  coe
    .vein(JSON.stringify({ text: "Bauxite vein" }), "tfmg:bauxite_powder")
    .placement(1024, 128, 64825185)
    .biomeWhitelist("minecraft:is_overworld")
    .id("kubejs:bauxite_vein");

  coe
    .drilling("tfmg:bauxite_powder", "kubejs:bauxite_vein", 100)
    .id("kubejs:bauxite_vein1");

  ctfmg.casting(
    Fluid.of("kubejs:molten_obsidian_silicon_alloy", 144),
    "cyclic:gem_obsidian",
    300,
  );

  ctfmg.casting(Fluid.of("create:honey", 250), "minecraft:honeycomb", 50);
});

function __sequencedAssembly(event: ServerEvents.CreateRecipesAPI) {
  return (
    transitionalItem: ItemLike,
    output: ItemLike,
    input: ItemLike,
    steps: [typeof event.deploying | typeof event.filling, ItemLike | null][],
    loops: number,
  ) =>
    event
      .sequenced_assembly(
        [output],
        input,
        steps.map(([fn, arg]) =>
          fn(
            transitionalItem,
            // @ts-expect-error
            arg == null ? transitionalItem : [transitionalItem, arg],
          ),
        ),
      )
      .transitionalItem(transitionalItem)
      .loops(loops);
}
