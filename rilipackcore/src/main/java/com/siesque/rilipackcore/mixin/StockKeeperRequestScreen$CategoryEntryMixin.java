package com.siesque.rilipackcore.mixin;

import com.siesque.rilipackcore.stockticker.accessor.StockKeeperRequestScreen$CategoryEntryAccess;
import com.simibubi.create.content.logistics.stockTicker.StockKeeperRequestScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = StockKeeperRequestScreen.CategoryEntry.class, remap = false)
public abstract class StockKeeperRequestScreen$CategoryEntryMixin implements StockKeeperRequestScreen$CategoryEntryAccess {

    @Shadow private int y;

    @Shadow private int targetBECategory;

    @Shadow private boolean hidden;

    @Override
    public int getY() {
        return y;
    }

    @Override
    public int getTargetBECategory() {
        return targetBECategory;
    }

    @Override
    public boolean isHidden() {
        return hidden;
    }
}
