package com.siesque.rilipackcore.mixin;

import com.siesque.rilipackcore.block.entity.capability.CleanroomInfo;
import com.siesque.rilipackcore.block.entity.capability.ICleanroomAware;
import com.siesque.rilipackcore.createoptical.IFocusingRecipe;
import com.siesque.rilipackcore.multiblock.cleanroom.CleanroomType;
import net.lpcamors.optical.blocks.beam_focuser.BeamFocuserBlockEntity;
import net.lpcamors.optical.blocks.beam_focuser.BeamFocuserHelper;
import net.lpcamors.optical.blocks.optical_source.BeamHelper;
import net.lpcamors.optical.recipes.FocusingRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(BeamFocuserBlockEntity.class)
@Implements(@Interface(iface = ICleanroomAware.class, prefix = "cleanroom$"))
public class BeamFocuserBlockEntityMixin {
    @Unique
    private CleanroomInfo rilipackcore$cleanroomInfo;

    public CleanroomInfo cleanroom$getCleanroomInfo() {
        return rilipackcore$cleanroomInfo;
    }

    public void cleanroom$setCleanroomInfo(CleanroomInfo info) {
        this.rilipackcore$cleanroomInfo = info;
    }

    @Redirect(
            method = "whenItemHeld",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/lpcamors/optical/blocks/beam_focuser/BeamFocuserHelper;canBeProcessed(Lnet/minecraft/world/level/Level;Lnet/minecraftforge/items/wrapper/RecipeWrapper;Lnet/lpcamors/optical/blocks/optical_source/BeamHelper$BeamType;)Ljava/util/Optional;",
                    remap = false
            ),
            remap = false
    )
    private Optional<FocusingRecipe> canBeProcessedRedirectWhenItemHeld(Level world, RecipeWrapper w, BeamHelper.BeamType beamType) {
        return rilipackcore$canBeProcessedRedirect(world, w, beamType);
    }

    @Redirect(
            method = "onItemReceived",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/lpcamors/optical/blocks/beam_focuser/BeamFocuserHelper;canBeProcessed(Lnet/minecraft/world/level/Level;Lnet/minecraftforge/items/wrapper/RecipeWrapper;Lnet/lpcamors/optical/blocks/optical_source/BeamHelper$BeamType;)Ljava/util/Optional;",
                    remap = false
            ),
            remap = false
    )
    private Optional<FocusingRecipe> canBeProcessedRedirectOnItemReceived(Level world, RecipeWrapper w, BeamHelper.BeamType beamType) {
        return rilipackcore$canBeProcessedRedirect(world, w, beamType);
    }

    @Unique
    private Optional<FocusingRecipe> rilipackcore$canBeProcessedRedirect(Level world, RecipeWrapper w, BeamHelper.BeamType beamType) {
        Optional<FocusingRecipe> recipe = BeamFocuserHelper.canBeProcessed(world, w, beamType);

        if (recipe.isPresent()) {
            boolean requiresCleanroom = ((IFocusingRecipe) recipe.get()).getRequireCleanroom();
            if (requiresCleanroom) {
                CleanroomInfo info = cleanroom$getCleanroomInfo();
                if (info == null || !info.isClean()) {
                    return Optional.empty();
                }
            }
        }

        return recipe;
    }

    @Inject(
            method = "read",
            at = @At("TAIL"),
            remap = false
    )
    private void rilipackcore$readCleanroomInfo(
            CompoundTag compound,
            boolean clientPacket,
            CallbackInfo ci
    ) {
        if (compound.contains("CleanroomInfo")) {
            CompoundTag infoTag = compound.getCompound("CleanroomInfo");
            this.rilipackcore$cleanroomInfo = new CleanroomInfo(
                    BlockPos.of(infoTag.getLong("ControllerPos")),
                    CleanroomType.valueOf(infoTag.getString("CleanroomType")),
                    infoTag.getInt("Cleanliness")
            );
        }
    }

    @Inject(
            method = "write",
            at = @At("TAIL"),
            remap = false
    )
    private void rilipackcore$writeCleanroomInfo(
            CompoundTag compound,
            boolean clientPacket,
            CallbackInfo ci
    ) {
        if (this.rilipackcore$cleanroomInfo != null) {
            CompoundTag infoTag = new CompoundTag();
            infoTag.putLong("ControllerPos", this.rilipackcore$cleanroomInfo.getControllerPos().asLong());
            infoTag.putString("CleanroomType", this.rilipackcore$cleanroomInfo.getCleanroomType().name());
            infoTag.putInt("Cleanliness", this.rilipackcore$cleanroomInfo.getCleanliness());
            compound.put("CleanroomInfo", infoTag);
        }
    }
}
