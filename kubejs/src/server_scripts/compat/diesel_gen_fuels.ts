ServerEvents.recipes((e) => {
  const tagFuel = __tagFuel(e);

  tagFuel("forge:diesel", 322);
  tagFuel("forge:kerosene", 208);
  tagFuel("forge:biofuel", 250);
  tagFuel("forge:gasoline", 322);
  tagFuel("forge:lpg", 212);
});

function __tagFuel(e: ServerEvents.RecipeEvent) {
  return (fluidTag: string, burnTime: number) => {
    e.custom({
      type: "immersiveengineering:generator_fuel",
      burnTime,
      fluidTag,
    });
  };
}
