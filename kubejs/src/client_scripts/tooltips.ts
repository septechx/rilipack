ItemEvents.tooltip((e) => {
  e.add(
    "minecraft:elytra",
    Text.of(
      "This item has been disabled. Used only as a crafting ingredient.",
    ).red(),
  );
});
