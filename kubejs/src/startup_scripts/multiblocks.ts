MMEvents.registerControllers((e) => {
  e.create("blast_forge_controller")
    .name("Blast Forge Controller")
    .type("mm:machine");
});

MMEvents.registerPorts((e) => {
  const fluid_port = __fluid_port(e);
  const item_port = __item_port(e);

  fluid_port(
    "blast_forge_fluid",
    "Blast Forge Fluid",
    "mm:blast_forge_controller",
    1,
    1,
    1000,
  );

  item_port(
    "blast_forge_item",
    "Blast Forge Item",
    "mm:blast_forge_controller",
    3,
    3,
  );
});

MMEvents.registerExtraBlocks((e) => {
  e.create("blast_forge_vent").name("Blast Forge Vent").type("mm:vent");
});

function __fluid_port(e: MMEvents.RegisterPortsEvent) {
  return (
    id: string,
    name: string,
    controllerId: string,
    rows: number,
    cols: number,
    cap: number,
  ) => {
    e.create(id)
      .name(name)
      .controllerId(controllerId)
      .config("mm:fluid", (c) => {
        c.rows(rows).columns(cols).slotCapacity(cap);
      });
  };
}

function __item_port(e: MMEvents.RegisterPortsEvent) {
  return (
    id: string,
    name: string,
    controllerId: string,
    rows: number,
    cols: number,
  ) => {
    e.create(id)
      .name(name)
      .controllerId(controllerId)
      .config("mm:item", (c) => {
        c.rows(rows).columns(cols);
      });
  };
}
