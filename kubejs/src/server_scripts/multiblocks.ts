MMEvents.createStructures((event) => {
  event
    .create("mm:blast_forge")
    .controllerId("mm:blast_forge_controller")
    .name("Blast Forge")
    .layout((a) => {
      a.layer(["VVV", "VVV", "VVV"])
        .layer(["SFS", "I I", "SCS"])
        .layer(["SSS", "SSS", "SSS"])
        .key("S", {
          block: "immersiveengineering:blastbrick",
        })
        .key("F", {
          port: "mm:blast_forge_fluid",
          input: true,
        })
        .key("I", {
          port: "mm:blast_forge_item",
        })
        .key("V", {
          block: "mm:blast_forge_vent",
        });
    });
});

MMEvents.createProcesses((event) => {
  blastForgeRecipes(event)
    .recipe(
      "steel_ingot",
      {
        type: "mm:item",
        item: "immersiveengineering:ingot_steel",
        count: 1,
      },
      [
        {
          type: "mm:item",
          tag: "forge:ingots/iron",
          count: 1,
        },
        {
          type: "mm:item",
          tag: "forge:dusts/coal_coke",
          count: 1,
        },
      ],
      400,
    )
    .recipe(
      "netherite_ingot",
      {
        type: "mm:item",
        item: "minecraft:netherite_ingot",
        count: 1,
      },
      [
        {
          type: "mm:item",
          tag: "forge:ingots/netherite_scrap",
          count: 3,
        },
        {
          type: "mm:item",
          tag: "forge:ingots/gold",
          count: 6,
        },
      ],
      600,
    )
    .recipe(
      "silicon_boule",
      {
        type: "mm:item",
        item: "kubejs:silicon_boule",
        count: 1,
      },
      [
        {
          type: "mm:item",
          tag: "forge:ingots/silicon",
          count: 32,
        },
      ],
      800,
    )
    .build();
});

function blastForgeRecipes(event: MMEvents.CreateProcessesEvent) {
  const recipes: ((fuel: string, i: number) => void)[] = [];

  function builder(
    name: string,
    output: MMEvents.ProcessIngredient,
    inputs: MMEvents.ProcessIngredient[],
    processingTime: number,
  ) {
    recipes.push((fuel: string, i: number) => {
      let recipe_builder = event
        .create(`mm:${name}_${i}`)
        .structureId("mm:blast_forge")
        .ticks(processingTime)
        .input({
          type: "mm:input/consume",
          ingredient: {
            type: "mm:fluid",
            fluid: fuel,
            amount: 150,
          },
        })
        .output({
          type: "mm:output/simple",
          ingredient: output,
        });

      inputs.forEach((input) => {
        recipe_builder = recipe_builder.input({
          type: "mm:input/consume",
          ingredient: input,
        });
      });
    });

    return {
      recipe: builder,
      build,
    };
  }

  function build() {
    [
      "createdieselgenerators:gasoline",
      "createdieselgenerators:diesel",
      "tfmg:gasoline",
      "tfmg:diesel",
    ].forEach((fuel, i) => {
      recipes.forEach((recipe) => recipe(fuel, i));
    });
  }

  return {
    recipe: builder,
    build,
  };
}
