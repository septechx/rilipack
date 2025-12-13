ServerEvents.tags("item", (e) => {
  e.add("forge:dusts/saltpeter", "tfmg:nitrate_dust");
  e.removeAll("apotheosis:boon_drops");
});

ServerEvents.tags("fluid", (e) => {
  [
    "createaddition:seed_oil",
    "createaddition:flowing_seed_oil",
    "createaddition:bioethanol",
    "createaddition:flowing_bioethanol",
    "cyclic:biomass",
    "cyclic:biomass_flowing",
    "cyclic:honey",
    "cyclic:honey_flowing",
    "cyclic:xpjuice",
    "cyclic:xpjuice_flowing",
  ].forEach((item) => {
    e.remove("minecraft:water", item);
  });
});
