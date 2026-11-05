package com.siesque.rilipackcore.block;

import com.siesque.rilipackcore.Rilipackcore;
import com.siesque.rilipackcore.block.multiblock.CasingBlock;
import com.siesque.rilipackcore.block.multiblock.CleanroomControllerBlock;
import com.siesque.rilipackcore.block.multiblock.EnergyInputHatch;
import com.siesque.rilipackcore.block.multiblock.FilterBlock;
import com.siesque.rilipackcore.item.RilipackcoreItems;
import com.siesque.rilipackcore.multiblock.cleanroom.CleanroomType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DoubleHighBlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public class RilipackcoreBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS,
            Rilipackcore.MODID);

    // Cleanroom
    public static final RegistryObject<Block> CLEANROOM_CONTROLLER = registerBlock("cleanroom_controller",
            () -> new CleanroomControllerBlock(Block.Properties.of()
                    .mapColor(Blocks.IRON_BLOCK.defaultMapColor())
                    .strength(5.0f, 6.0f)
                    .requiresCorrectToolForDrops()));

    public static final RegistryObject<Block> PLASCRETE_CASING = registerBlock("plascrete_casing",
            () -> new CasingBlock(Block.Properties.of()
                    .mapColor(Blocks.IRON_BLOCK.defaultMapColor())
                    .strength(5.0f, 6.0f)
                    .requiresCorrectToolForDrops()));

    public static final RegistryObject<Block> CLEANROOM_FILTER = registerBlock("cleanroom_filter",
            () -> new FilterBlock(Block.Properties.of()
                    .mapColor(Blocks.IRON_BLOCK.defaultMapColor())
                    .strength(5.0f, 6.0f)
                    .requiresCorrectToolForDrops(), CleanroomType.CLEANROOM));

    public static final RegistryObject<Block> CLEANROOM_FILTER_STERILE = registerBlock("cleanroom_filter_sterile",
            () -> new FilterBlock(Block.Properties.of()
                    .mapColor(Blocks.GOLD_BLOCK.defaultMapColor())
                    .strength(5.0f, 6.0f)
                    .requiresCorrectToolForDrops(), CleanroomType.CLEANROOM_STERILE));

    public static final RegistryObject<Block> ENERGY_INPUT_HATCH = registerBlock("energy_input_hatch",
            () -> new EnergyInputHatch(Block.Properties.of()
                    .mapColor(Blocks.IRON_BLOCK.defaultMapColor())
                    .strength(5.0f, 6.0f)
                    .requiresCorrectToolForDrops()));

    public static final RegistryObject<Block> CLEANROOM_DOOR = registerBlock("cleanroom_door",
            () -> new DoorBlock(Block.Properties.of()
                    .mapColor(Blocks.IRON_BLOCK.defaultMapColor())
                    .strength(5.0f, 6.0f)
                    .requiresCorrectToolForDrops()
                    .noOcclusion(), BlockSetType.IRON),
            DoubleHighBlockItem::new);
    // Cleanroom

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        return registerBlock(name, block, BlockItem::new);
    }

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block,
            BiFunction<Block, Item.Properties, Item> itemFactory) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        RilipackcoreItems.ITEMS.register(name, () -> itemFactory.apply(toReturn.get(), new Item.Properties()));
        return toReturn;
    }

    public static void register() {
        BLOCKS.register(Rilipackcore.EVENT_BUS);
    }
}
