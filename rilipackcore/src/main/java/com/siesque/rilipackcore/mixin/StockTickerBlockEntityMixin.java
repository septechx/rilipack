package com.siesque.rilipackcore.mixin;

import com.siesque.rilipackcore.stockticker.accessor.StockTickerBlockEntityAccess;
import com.simibubi.create.content.logistics.stockTicker.StockTickerBlockEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(value = StockTickerBlockEntity.class, remap = false)
public class StockTickerBlockEntityMixin implements StockTickerBlockEntityAccess {

    @Shadow
    protected List<ItemStack> categories;

    public List<ItemStack> getCategories() {
        return categories;
    }
}
