PlayerEvents.inventoryChanged((e) => {
  if (e.getSlot() !== 6 || e.getItem() !== Item.of("minecraft:elytra")) return;

  const player = e.getEntity();

  player.setItemSlot(4, Item.getEmpty());

  if (!player.addItem(Item.of("minecraft:elytra"))) {
    player.drop(Item.of("minecraft:elytra"), false);
  }
});
