package com.siesque.rilipackcore.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.siesque.rilipackcore.Rilipackcore;
import com.siesque.rilipackcore.stockticker.ClientConfig;
import com.simibubi.create.content.logistics.BigItemStack;
import com.simibubi.create.content.logistics.stockTicker.StockKeeperRequestMenu;
import com.simibubi.create.content.logistics.stockTicker.StockKeeperRequestScreen;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.menu.AbstractSimiContainerScreen;
import net.createmod.catnip.data.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = StockKeeperRequestScreen.class)
public abstract class StockKeeperRequestScreenMixin extends AbstractSimiContainerScreen<StockKeeperRequestMenu> {

    @Unique
    private static final ResourceLocation STOCK_KEEPER_PATCH = Rilipackcore.rl("textures/gui/stock_keeper_patch.png");

    @Shadow
    @Final
    private static AllGuiTextures HEADER;
    @Shadow
    @Final
    private static AllGuiTextures BODY;
    @Shadow
    @Final
    private static AllGuiTextures FOOTER;
    @Shadow
    public EditBox searchBox;
    @Shadow
    int windowHeight;

    public StockKeeperRequestScreenMixin(StockKeeperRequestMenu container, Inventory inv, Component title) {
        super(container, inv, title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void init_tail(CallbackInfo ci) {
        searchBox.setFocused(true);
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    public void mouseClicked(double pMouseX, double pMouseY, int pButton, CallbackInfoReturnable<Boolean> cir) {
        boolean lmbClicked = pButton == GLFW.GLFW_MOUSE_BUTTON_LEFT;

        Pair<Integer, Integer> ezLocation = create_Ez_Stock_Ticker$getEzLocation();
        if (lmbClicked && pMouseX >= ezLocation.getFirst() && pMouseX <= ezLocation.getFirst() + 16
                && pMouseY >= ezLocation.getSecond() && pMouseY <= ezLocation.getSecond() + 7) {
            boolean newValue = !ClientConfig.isEzStockTickerEnabled;
            ClientConfig.setIsEzStockTickerEnabled(newValue);
            playUiSound(SoundEvents.UI_BUTTON_CLICK.value(), 1, 1);
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "mouseClicked", at = @At(value = "FIELD", target = "Lcom/simibubi/create/content/logistics/BigItemStack;count:I", ordinal = 1, shift = At.Shift.BY, by = -2))
    private void mouseClickedTransferInject(double pMouseX, double pMouseY, int pButton,
            CallbackInfoReturnable<Boolean> cir, @Local(name = "current") int current,
            @Local(name = "existingOrder") BigItemStack existingOrder, @Local(name = "transfer") LocalIntRef transfer,
            @Local(name = "rmb") boolean rmb, @Local(name = "entry") BigItemStack entry) {
        boolean isEnabled = ClientConfig.isEzStockTickerEnabled;
        if (rmb && isEnabled && entry.stack.isEmpty()) {
            transfer.set(existingOrder.count == 1 ? 1 : current / 2);
        }
    }

    @Inject(method = "mouseScrolled", at = @At(value = "FIELD", target = "Lcom/simibubi/create/content/logistics/BigItemStack;count:I", ordinal = 1, shift = At.Shift.BY, by = -2))
    private void mouseScrolled_removeItems(double mouseX, double mouseY, double delta,
            CallbackInfoReturnable<Boolean> cir, @Local(name = "existingOrder") BigItemStack existingOrder,
            @Local(name = "current") int current, @Local(name = "transfer") LocalIntRef transfer,
            @Local(name = "entry") BigItemStack entry) {
        boolean isEnabled = ClientConfig.isEzStockTickerEnabled;
        if (isEnabled) {
            int stackSnapping = hasControlDown() ? 10 : Math.max(1, entry.stack.getMaxStackSize() / 4);

            if (hasShiftDown() || hasControlDown()) {
                int target = ((Math.floorDiv(current, stackSnapping) - 1) * stackSnapping);
                target = Math.max(1, target);
                transfer.set(current - target);
            } else {

                int target = current - transfer.get();
                if (target < 1) {
                    transfer.set(current - 1);
                }
            }
        }
    }

    @Inject(method = "mouseScrolled", at = @At(value = "FIELD", target = "Lcom/simibubi/create/content/logistics/stockTicker/StockKeeperRequestScreen;blockEntity:Lcom/simibubi/create/content/logistics/stockTicker/StockTickerBlockEntity;", shift = At.Shift.BY, by = -2))
    private void mouseScrolled_addItems(double mouseX, double mouseY, double delta, CallbackInfoReturnable<Boolean> cir,
            @Local(name = "existingOrder") BigItemStack existingOrder, @Local(name = "current") int current,
            @Local(name = "transfer") LocalIntRef transfer, @Local(name = "entry") BigItemStack entry) {
        boolean isEnabled = ClientConfig.isEzStockTickerEnabled;
        if (isEnabled) {
            int stackSnapping = hasControlDown() ? 10 : Math.max(1, entry.stack.getMaxStackSize() / 4);

            if (hasShiftDown() || hasControlDown()) {
                int target = ((Math.floorDiv(current, stackSnapping) + 1) * stackSnapping);
                target = Math.max(1, target);
                transfer.set(target - current);
            }
        }
    }

    @Inject(method = "renderBg", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/foundation/gui/AllGuiTextures;render(Lnet/minecraft/client/gui/GuiGraphics;II)V", ordinal = 2, shift = At.Shift.AFTER))
    protected void renderForeground(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY, CallbackInfo ci) {
        boolean isEzEnabled = ClientConfig.isEzStockTickerEnabled;
        Pair<Integer, Integer> ezLocation = create_Ez_Stock_Ticker$getEzLocation();
        int v = isEzEnabled ? 7 : 0;
        graphics.blit(STOCK_KEEPER_PATCH, ezLocation.getFirst(), ezLocation.getSecond(), 0, v, 16, 7);
    }

    @Inject(method = "renderForeground", at = @At("TAIL"), remap = false)
    protected void renderForeground(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        Pair<Integer, Integer> ezLocation = create_Ez_Stock_Ticker$getEzLocation();

        if (mouseX >= ezLocation.getFirst() && mouseX <= ezLocation.getFirst() + 16 && mouseY >= ezLocation.getSecond()
                && mouseY <= ezLocation.getSecond() + 7) {
            graphics.renderComponentTooltip(font, ClientConfig.isEzStockTickerEnabled ? List.of(
                    Component.translatable("ez_stock_ticker.gui.enabled").withStyle(ChatFormatting.GREEN),
                    Component.translatable("ez_stock_ticker.gui.enabled_description_1")
                            .withStyle(ChatFormatting.DARK_GRAY),
                    Component.translatable("ez_stock_ticker.gui.enabled_description_2")
                            .withStyle(ChatFormatting.DARK_GRAY),
                    Component.translatable("ez_stock_ticker.gui.click_to_toggle").withStyle(ChatFormatting.GRAY))
                    : List.of(
                            Component.translatable("ez_stock_ticker.gui.disabled").withStyle(ChatFormatting.RED),
                            Component.translatable("ez_stock_ticker.gui.disabled_description_1")
                                    .withStyle(ChatFormatting.DARK_GRAY),
                            Component.translatable("ez_stock_ticker.gui.disabled_description_2")
                                    .withStyle(ChatFormatting.DARK_GRAY),
                            Component.translatable("ez_stock_ticker.gui.click_to_toggle")
                                    .withStyle(ChatFormatting.GRAY)),
                    mouseX, mouseY);
        }
    }

    @Unique
    private Pair<Integer, Integer> create_Ez_Stock_Ticker$getEzLocation() {
        int x = getGuiLeft();
        int y = getGuiTop() + HEADER.getHeight() + FOOTER.getHeight();

        for (int i = 0; i < (windowHeight - HEADER.getHeight() - FOOTER.getHeight()) / BODY.getHeight(); i++) {
            y += BODY.getHeight();
        }

        int ezTooltipX = x + 13;
        int ezTooltipY = y - 13;
        return Pair.of(ezTooltipX, ezTooltipY);
    }
}
