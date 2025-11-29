// priority: 0

const HIDDEN_ITEMS = {
  create_factory_logistics: ["incomplete_fluid_mechanism"],
  kubejs: [
    "incomplete_lv_capacitor",
    "incomplete_mv_capacitor",
    "incomplete_hv_capacitor",
  ],
};

JEIEvents.hideItems((e) => {
  global.forEachItem(global.REMOVALS, (ident) => e.hide(ident));
  global.forEachItem(HIDDEN_ITEMS, (ident) => e.hide(ident));
});
