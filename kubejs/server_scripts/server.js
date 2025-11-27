// priority: 0

ServerEvents.recipes((e) => {
  global.forEachItem(global.REMOVALS, (ident) => e.remove({ output: ident }));
});
