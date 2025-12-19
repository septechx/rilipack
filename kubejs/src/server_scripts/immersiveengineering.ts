ServerEvents.recipes((e) => {
  const blueprintRecipe = __blueprintRecipe(e);

  blueprintRecipe(
    "components",
    { tag: "forge:plates/steel", count: 2 },
    ["tfmg:magnetic_alloy_ingot"],
    "immersiveengineering:component_steel",
  );

  blueprintRecipe(
    "components",
    { tag: "forge:plates/iron", count: 2 },
    ["tfmg:magnetic_alloy_ingot"],
    "immersiveengineering:component_iron",
  );
});

function __blueprintRecipe(e: ServerEvents.RecipeEvent) {
  return (
    category: "components",
    base: { tag: string; count: number },
    ingredients: string[],
    result: string,
  ) => {
    e.custom({
      type: "immersiveengineering:blueprint",
      category,
      inputs: (
        [
          {
            base_ingredient: {
              tag: base.tag,
            },
            count: base.count,
          },
        ] as Object[]
      ).concat(
        ingredients.map((i) => ({
          item: i,
        })),
      ),
      result: {
        item: result,
      },
    });
  };
}
