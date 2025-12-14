declare namespace PlayerEvents {
  type InventoryChangedEvent = {
    getEntity(): Player;
    getItem(): Item;
    getSlot(): number;
  };

  /**
   * Server
   */
  export function inventoryChanged(
    callback: (e: InventoryChangedEvent) => void,
  ): void;
}

declare namespace LootJS {
  class LootEntry {
    public static of(item: ItemLike): Promise<LootEntry>;
    public randomChance(chance: number): LootEntry;
  }

  type BlockLootModifierBuilder = {
    removeLoot(ingredient: Ingredient): BlockLootModifierBuilder;
    addLoot(ingredient: ItemLike): BlockLootModifierBuilder;
    addAlternativesLoot(...loot: LootEntry[]): BlockLootModifierBuilder;
    replaceLoot(from: ItemLike, to: ItemLike): BlockLootModifierBuilder;
    replaceLoot(
      from: ItemLike,
      to: ItemLike,
      preserveCount: boolean,
    ): BlockLootModifierBuilder;
  };

  type ModifiersEvent = {
    addBlockLootModifier(id: ItemLike): BlockLootModifierBuilder;
  };

  /**
   * Server
   */
  export function modifiers(callback: (e: ModifiersEvent) => void): void;
}

declare namespace ItemEvents {
  type ItemTooltipEvent = {
    add(item: string | string[] | RegExp, tooltip: string | Text): void;
  };

  /**
   * Client
   */
  export function tooltip(callback: (e: ItemTooltipEvent) => void): void;
}

declare namespace JEIEvents {
  type HideItemsEvent = {
    hide(item: string): void;
  };

  /**
   * Client
   */
  export function hideItems(callback: (e: HideItemsEvent) => void): void;
}

declare namespace MMEvents {
  interface BlockBuilder<Builder> {
    name(name: string): Builder;
    type(type: string): Builder;
  }

  type ControllerBuilder = BlockBuilder<ControllerBuilder>;
  type RegisterControllersEvent = {
    create(id: string): ControllerBuilder;
  };
  /**
   * Startup
   */
  export function registerControllers(
    callback: (event: RegisterControllersEvent) => void,
  ): void;

  type PortCallbackBuilder = {
    rows(rows: number): PortCallbackBuilder;
    columns(cols: number): PortCallbackBuilder;
    slotCapacity(cap: number): PortCallbackBuilder;
  };
  type PortBuilder = BlockBuilder<PortBuilder> & {
    controllerId(id: string): PortBuilder;
    config(type: string, callback: (type: PortCallbackBuilder) => void): void;
  };
  type RegisterPortsEvent = {
    create(id: string): PortBuilder;
  };
  /**
   * Startup
   */
  export function registerPorts(
    callback: (event: RegisterPortsEvent) => void,
  ): void;

  type ExtraBlockBuilder = BlockBuilder<ExtraBlockBuilder>;
  type RegisterExtraBlocksEvent = {
    create(id: string): ExtraBlockBuilder;
  };
  /**
   * Startup
   */
  export function registerExtraBlocks(
    callback: (event: RegisterExtraBlocksEvent) => void,
  ): void;

  type MultiblockPort =
    | {
      block: string;
    }
    | {
      port: string;
      input?: boolean;
    };
  type LayoutBuilder = {
    layer(pattern: string[]): LayoutBuilder;
    key(key: string, block: MultiblockPort): LayoutBuilder;
  };
  type MultiblockBuilder = {
    controllerId(id: string): MultiblockBuilder;
    name(name: string): MultiblockBuilder;
    layout(callback: (a: LayoutBuilder) => void): MultiblockBuilder;
  };
  type CreateStructuresEvent = {
    create(id: string): MultiblockBuilder;
  };
  /**
   * Server
   */
  export function createStructures(
    callback: (event: CreateStructuresEvent) => void,
  ): void;

  type ProcessIngredient =
    | {
      type: "mm:item";
      item: string;
      count: number;
    }
    | {
      type: "mm:item";
      tag: string;
      count: number;
    }
    | {
      type: "mm:fluid";
      fluid: string;
      amount: number;
    };
  type ProcessOutput = {
    type: "mm:output/simple";
    ingredient: ProcessIngredient;
  };
  type ProcessInput = {
    type: "mm:input/consume";
    ingredient: ProcessIngredient;
  };
  type ProcessBuilder = {
    structureId(id: string): ProcessBuilder;
    ticks(ticks: number): ProcessBuilder;
    input(input: ProcessInput): ProcessBuilder;
    output(output: ProcessOutput): ProcessBuilder;
  };
  type CreateProcessesEvent = {
    create(id: string): ProcessBuilder;
  };
  /**
   * Server
   */
  export function createProcesses(
    callback: (event: CreateProcessesEvent) => void,
  ): void;
}

declare namespace StartupEvents {
  type ItemRegistryEvent = {
    create(id: string, parent?: string): ItemRegistryEvent;
    displayName(name: string): ItemRegistryEvent;
    parentModel(model: string): ItemRegistryEvent;
    texture(texture: string): ItemRegistryEvent;
  };
  /**
   * Startup
   */
  export function registry(
    type: "item",
    callback: (e: ItemRegistryEvent) => void,
  ): void;

  type FluidRegistryEvent = {
    create(id: string): FluidRegistryEvent;
    thickTexture(color: number): FluidRegistryEvent;
    thinTexture(color: number): FluidRegistryEvent;
    bucketColor(color: number): FluidRegistryEvent;
    displayName(name: string): FluidRegistryEvent;
  };
  /**
   * Startup
   */
  export function registry(
    type: "fluid",
    callback: (e: FluidRegistryEvent) => void,
  ): void;
}

declare namespace ServerEvents {
  type RecipeFilter =
    | {
      id: string;
    }
    | {
      input: ItemLike;
    }
    | {
      output: ItemLike;
    };

  type SequencedAssemblyStep = unknown;

  type SequencedAssemblyBuilder = {
    transitionalItem(item: ItemLike): SequencedAssemblyBuilder;
    loops(loops: number): SequencedAssemblyBuilder;
  };

  type CreateRecipesAPI = {
    filling(result: ItemLike, ingredients: ItemLike[]): SequencedAssemblyStep;
    deploying(result: ItemLike, ingredient: ItemLike): SequencedAssemblyStep;
    pressing(results: ItemLike[], input: ItemLike): void;
    cutting(results: ItemLike[], input: ItemLike): void;
    crushing(results: ItemLike[], input: ItemLike): void;
    mixing(
      ingredients: ItemLike[],
      outputs: ItemLike[],
    ): {
      superheated(): void;
      heated(): void;
    };
    mechanical_crafting(
      result: ItemLike,
      pattern: string[],
      keys: Record<string, ItemLike>,
    ): void;
    sequenced_assembly(
      outputs: ItemLike[],
      input: ItemLike,
      steps: SequencedAssemblyStep[],
    ): SequencedAssemblyBuilder;
  };

  type VeinBuilder = {
    placement(x: number, y: number, z: number): VeinBuilder;
    biomeWhitelist(biome: string): VeinBuilder;
    id(id: string): VeinBuilder;
  };

  type CreateOrExcavationRecipesAPI = {
    vein(name: string, result: ItemLike): VeinBuilder;
    drilling(
      result: ItemLike,
      vein: string,
      chance: number,
    ): {
      id(id: string): void;
    };
  };

  type TFMGRecipesAPI = {
    casting(result: ItemLike, input: ItemLike, ticks: number): void;
  };

  type IEBlueprintCategory = "components";

  type IERecipesAPI = {
    blueprint(
      result: ItemLike,
      ingredients: ItemLike[],
      category: IEBlueprintCategory,
    ): void;
  };

  type RecipesAPI = {
    create: CreateRecipesAPI;
    createoreexcavation: CreateOrExcavationRecipesAPI;
    tfmg: TFMGRecipesAPI;
    immersiveengineering: IERecipesAPI;
  };

  type RecipeEvent = {
    recipes: RecipesAPI;

    replaceInput(
      filter: RecipeFilter,
      toReplace: string,
      replacement: string,
    ): void;
    shaped(
      result: ItemLike,
      pattern: string[],
      keys: Record<string, ItemLike>,
    ): void;
    shapeless(result: ItemLike, ingredients: ItemLike[]): void;
    remove(filter: RecipeFilter): void;
    custom(recipe: Object): void;
  };
  /**
   * Server
   */
  export function recipes(callback: (e: RecipeEvent) => void): void;

  type TagEvent = {
    add(tag: string, item: ItemLike): void;
    remove(tag: string, item: ItemLike): void;
    removeAll(tag: string): void;
  };
  /**
   * Server
   */
  export function tags(
    type: "item" | "fluid",
    callback: (e: TagEvent) => void,
  ): void;
}

declare class Item {
  public static of(id: string, count?: number): Item;
  public static getEmpty(): Item;
  public withChance(chance: number): Item;
}

declare class Fluid {
  public static of(id: string, amount?: number): Fluid;
}

declare class Ingredient {
  public static all: Ingredient;
}

declare class Player {
  /**
   * Adds the stack to the first empty slot in the player's inventory. Returns false if it's not possible to
   * place the entire stack in the inventory.
   */
  public addItem(item: Item): boolean;
  public drop(item: Item, includeThrowerName: boolean): void;
  public setItemSlot(slot: number, item: Item): void;
}

declare class Text {
  public static of(text: string): Text;
  public red(): Text;
}

declare type ItemLike = string | Item | Fluid;

// @ts-expect-error
declare const global: {
  REMOVALS: Record<string, string[]>;
  forEachItem: (
    map: Record<string, string[]>,
    f: (ident: string) => void,
  ) => void;
};
