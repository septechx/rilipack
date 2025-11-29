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

  c.filling("actuallyadditions:rice_slimeball", [
    Fluid.of("minecraft:water", 250),
    "actuallyadditions:rice_dough",
  ]);

  sequencedAssembly(
    "kubejs:incomplete_lv_capacitor",
    ["immersiveengineering:capacitor_lv"],
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
    ["immersiveengineering:capacitor_mv"],
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
    ["immersiveengineering:capacitor_hv"],
    "immersiveengineering:capacitor_mv",
    [
      [c.deploying, "#forge:plates/aluminum"],
      [c.deploying, "tfmg:capacitor_item"],
      [c.deploying, "#forge:ingots/hop_graphite"],
      [c.filling, Fluid.of("immersiveengineering:redstone_acid", 500)],
    ],
    8,
  );

  coe
    .vein(JSON.stringify({ text: "Bauxite vein" }), "tfmg:bauxite_powder")
    .placement(1024, 128, 64825185)
    .biomeWhitelist("minecraft:is_overworld")
    .id("kubejs:bauxite_vein");

  coe
    .drilling("tfmg:bauxite_powder", "kubejs:bauxite_vein", 100)
    .id("kubejs:bauxite_vein1");
});

function __sequencedAssembly(event) {
  return (transitionalItem, output, input, steps, loops) =>
    event
      .sequenced_assembly(
        output,
        input,
        steps.map(([fn, arg]) =>
          fn(
            transitionalItem,
            arg == null ? transitionalItem : [transitionalItem, arg],
          ),
        ),
      )
      .transitionalItem(transitionalItem)
      .loops(loops);
}
