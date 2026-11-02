package com.siesque.rilipackcore.mixin;

import com.google.gson.JsonObject;
import com.siesque.rilipackcore.createoptical.IFocusingRecipe;
import net.lpcamors.optical.recipes.FocusingRecipe;
import net.minecraft.network.FriendlyByteBuf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FocusingRecipe.class)
public final class FocusingRecipeMixin implements IFocusingRecipe {
    @Unique
    private static final String REQUIRE_CLEANROOM_KEY = "require_cleanroom";
    @Unique
    private boolean rilipackcore$requireCleanroom = false;

    @Override
    public boolean getRequireCleanroom() {
        return this.rilipackcore$requireCleanroom;
    }

    @Unique
    private boolean rilipackcore$readRequireCleanroom(JsonObject jsonObject) {
        if (jsonObject.has(REQUIRE_CLEANROOM_KEY) && jsonObject.get(REQUIRE_CLEANROOM_KEY).isJsonPrimitive()) {
            return jsonObject.get(REQUIRE_CLEANROOM_KEY).getAsBoolean();
        }
        return false;
    }

    @Unique
    private boolean rilipackcore$readRequireCleanroom(FriendlyByteBuf buffer) {
        return buffer.readBoolean();
    }

    @Inject(
            method = "writeAdditional(Lcom/google/gson/JsonObject;)V",
            at = @At("TAIL"),
            remap = false
    )
    public void onWriteAdditional(JsonObject json, CallbackInfo ci) {
        if (this.rilipackcore$requireCleanroom) {
            json.addProperty(REQUIRE_CLEANROOM_KEY, true);
        }
    }

    @Inject(
            method = "writeAdditional(Lnet/minecraft/network/FriendlyByteBuf;)V",
            at = @At("TAIL"),
            remap = false
    )
    public void onWriteAdditional(FriendlyByteBuf buffer, CallbackInfo ci) {
        buffer.writeBoolean(this.rilipackcore$requireCleanroom);
    }

    @Inject(
            method = "readAdditional(Lcom/google/gson/JsonObject;)V",
            at = @At("TAIL"),
            remap = false
    )
    public void onReadAdditional(JsonObject json, CallbackInfo ci) {
        this.rilipackcore$requireCleanroom = this.rilipackcore$readRequireCleanroom(json);
    }

    @Inject(
            method = "readAdditional(Lnet/minecraft/network/FriendlyByteBuf;)V",
            at = @At("TAIL"),
            remap = false
    )
    public void onReadAdditional(FriendlyByteBuf buffer, CallbackInfo ci) {
        this.rilipackcore$requireCleanroom = this.rilipackcore$readRequireCleanroom(buffer);
    }
}
