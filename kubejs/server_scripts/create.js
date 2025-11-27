ServerEvents.recipes((e) => {
  const c = e.recipes.create;
  const coe = e.recipes.createoreexcavation;

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

  coe
    .vein(JSON.stringify({ text: "Bauxite vein" }), "tfmg:bauxite_powder")
    .placement(1024, 128, 64825185)
    .biomeWhitelist("minecraft:is_overworld")
    .id("kubejs:bauxite_vein");

  coe
    .drilling("tfmg:bauxite_powder", "kubejs:bauxite_vein", 100)
    .id("kubejs:bauxite_vein1");
});
