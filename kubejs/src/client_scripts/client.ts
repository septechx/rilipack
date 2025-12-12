const HIDDEN_ITEMS = {
  create_factory_logistics: ["incomplete_fluid_mechanism"],
  kubejs: [
    "incomplete_lv_capacitor",
    "incomplete_mv_capacitor",
    "incomplete_hv_capacitor",
    "incomplete_explorers_compass",
    "incomplete_logic_chip_wafer",
  ],
};

JEIEvents.hideItems((e) => {
  global.forEachItem(global.REMOVALS, (ident) => e.hide(ident));
  global.forEachItem(HIDDEN_ITEMS, (ident) => e.hide(ident));
});
