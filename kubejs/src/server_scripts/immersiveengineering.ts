ServerEvents.recipes((e) => {
  const ie = e.recipes.immersiveengineering;

  ie.blueprint(
    "immersiveengineering:component_steel",
    ["#forge:plates/steel", "#forge:plates/steel", "tfmg:magnetic_alloy_ingot"],
    "components",
  );

  ie.blueprint(
    "immersiveengineering:component_iron",
    ["#forge:plates/iron", "#forge:plates/iron", "tfmg:magnetic_alloy_ingot"],
    "components",
  );
});
