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

  e.create("incomplete_explorers_compass", "create:sequenced_assembly")
    .displayName("Incomplete Explorer's Compass")
    .texture("explorerscompass:item/explorerscompass_00");

  e.create("incomplete_logic_chip_wafer", "create:sequenced_assembly")
    .displayName("Incomplete Logic Chip Wafer")
    .texture("kubejs:item/logic_chip_wafer");

  e.create("logic_chip_wafer")
    .displayName("Logic Chip Wafer")
    .texture("kubejs:item/logic_chip_wafer");
});
