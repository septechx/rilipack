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
  [
    "createdieselgenerators:gasoline",
    "createdieselgenerators:diesel",
    "tfmg:gasoline",
    "tfmg:diesel",
  ].forEach((fuel, i) => {
    event
      .create(`mm:steel_${i}`)
      .structureId("mm:blast_forge")
      .ticks(400)
      .input({
        type: "mm:input/consume",
        ingredient: {
          type: "mm:item",
          tag: "forge:ingots/iron",
          count: 1,
        },
      })
      .input({
        type: "mm:input/consume",
        ingredient: {
          type: "mm:item",
          tag: "forge:dusts/coal_coke",
          count: 1,
        },
      })
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
        ingredient: {
          type: "mm:item",
          item: "immersiveengineering:ingot_steel",
          count: 1,
        },
      });
  });
});
