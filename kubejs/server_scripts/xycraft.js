ServerEvents.recipes((e) => {
  e.custom({
    type: "xycraft_machines:extractor",
    adjacent: [],
    catalyst: {
      fluid_type: "minecraft:water",
      predicate_type: "xycraft_core:fluid_type_rule",
    },
    item_result: {
      Count: 1,
      id: "minecraft:dirt",
    },
    target: {
      block: "minecraft:dirt",
      predicate_type: "xycraft_core:block_rule",
    },
    ticks: 30,
    valid_directions: ["up", "down", "north", "south", "east", "west"],
  });
});
