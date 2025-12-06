ServerEvents.recipes((e) => {
  global.forEachItem(global.REMOVALS, (ident) => e.remove({ output: ident }));
});
