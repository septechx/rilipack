declare namespace ItemEvents {
  type ItemTooltipEvent = {
    add(item: string | string[] | RegExp, tooltip: string): void;
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
      input: Ingredient;
    }
    | {
      output: Ingredient;
    };

  type SequencedAssemblyStep = unknown;

  type SequencedAssemblyBuilder = {
    transitionalItem(item: Ingredient): SequencedAssemblyBuilder;
    loops(loops: number): SequencedAssemblyBuilder;
  };

  type CreateRecipesAPI = {
    filling(
      result: Ingredient,
      ingredients: Ingredient[],
    ): SequencedAssemblyStep;
    deploying(
      result: Ingredient,
      ingredient: Ingredient,
    ): SequencedAssemblyStep;
    pressing(results: Ingredient[], input: Ingredient): void;
    cutting(results: Ingredient[], input: Ingredient): void;
    crushing(results: Ingredient[], input: Ingredient): void;
    mixing(
      ingredients: Ingredient[],
      outputs: Ingredient[],
    ): {
      superheated(): void;
      heated(): void;
    };
    mechanical_crafting(
      result: Ingredient,
      pattern: string[],
      keys: Record<string, Ingredient>,
    ): void;
    sequenced_assembly(
      outputs: Ingredient[],
      input: Ingredient,
      steps: SequencedAssemblyStep[],
    ): SequencedAssemblyBuilder;
  };

  type VeinBuilder = {
    placement(x: number, y: number, z: number): VeinBuilder;
    biomeWhitelist(biome: string): VeinBuilder;
    id(id: string): VeinBuilder;
  };

  type CreateOrExcavationRecipesAPI = {
    vein(name: string, result: Ingredient): VeinBuilder;
    drilling(
      result: Ingredient,
      vein: string,
      chance: number,
    ): {
      id(id: string): void;
    };
  };

  type TFMGRecipesAPI = {
    casting(result: Ingredient, input: Ingredient, ticks: number): void;
  };

  type IEBlueprintCategory = "components";

  type IERecipesAPI = {
    blueprint(
      result: Ingredient,
      ingredients: Ingredient[],
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
      result: Ingredient,
      pattern: string[],
      keys: Record<string, Ingredient>,
    ): void;
    shapeless(result: Ingredient, ingredients: Ingredient[]): void;
    remove(filter: RecipeFilter): void;
    custom(recipe: Object): void;
  };
  /**
   * Server
   */
  export function recipes(callback: (e: RecipeEvent) => void): void;

  type TagEvent = {
    add(tag: string, item: Ingredient): void;
    remove(tag: string, item: Ingredient): void;
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
  static of(id: string, count?: number): Item;
  withChance(chance: number): Item;
}

declare class Fluid {
  static of(id: string, amount?: number): Fluid;
}

declare type Ingredient = string | Item | Fluid;

// @ts-expect-error
declare const global: {
  REMOVALS: Record<string, string[]>;
  forEachItem: (
    map: Record<string, string[]>,
    f: (ident: string) => void,
  ) => void;
};
