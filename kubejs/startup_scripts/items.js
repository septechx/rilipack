StartupEvents.registry("item", (e) => {
  e.create("incomplete_lv_capacitor", "create:sequenced_assembly")
    .displayName("Incomplete LV Accumulator")
    .parentModel("immersiveengineering:block/metal_device/capacitor_lv");

  e.create("incomplete_mv_capacitor", "create:sequenced_assembly")
    .displayName("Incomplete MV Accumulator")
    .parentModel("immersiveengineering:block/metal_device/capacitor_mv");

  e.create("incomplete_hv_capacitor", "create:sequenced_assembly")
    .displayName("Incomplete HV Accumulator")
    .parentModel("immersiveengineering:block/metal_device/capacitor_hv");
});
