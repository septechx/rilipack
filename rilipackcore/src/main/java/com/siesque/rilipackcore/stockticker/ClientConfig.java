package com.siesque.rilipackcore.stockticker;

import com.siesque.rilipackcore.Rilipackcore;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = Rilipackcore.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.BooleanValue IS_EZ_STOCK_TICKER_ENABLED = BUILDER
            .comment("Enable or disable Ez Stock Ticker functionality")
            .define("ez_stock_ticker_enabled", true);

    public static final ForgeConfigSpec CONFIG_SPEC = BUILDER.build();

    public static boolean isEzStockTickerEnabled;

    public static void setIsEzStockTickerEnabled(boolean enabled) {
        isEzStockTickerEnabled = enabled;

        if (CONFIG_SPEC.isLoaded()) {
            IS_EZ_STOCK_TICKER_ENABLED.set(enabled);
            IS_EZ_STOCK_TICKER_ENABLED.save();
        }
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        if (event.getConfig().getSpec() == CONFIG_SPEC) {
            isEzStockTickerEnabled = IS_EZ_STOCK_TICKER_ENABLED.get();
        }
    }
}
