// priority: 0

JEIEvents.hideItems((e) => {
  global.forEachItem(global.REMOVALS, (ident) => e.hide(ident));
});
