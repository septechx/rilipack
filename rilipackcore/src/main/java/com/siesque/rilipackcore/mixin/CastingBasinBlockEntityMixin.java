package com.siesque.rilipackcore.mixin;

import com.drmangotea.tfmg.content.machinery.metallurgy.casting_basin.CastingBasinBlockEntity;
import com.drmangotea.tfmg.recipes.CastingRecipe;
import com.simibubi.create.foundation.fluid.SmartFluidTank;
import net.createmod.catnip.animation.LerpedFloat;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CastingBasinBlockEntity.class)
public abstract class CastingBasinBlockEntityMixin {
    @Shadow(remap = false)
    public FluidTank tank;
    @Shadow(remap = false)
    public LazyOptional<IFluidHandler> fluidCapability;
    @Shadow(remap = false)
    public CastingRecipe recipe;

    @Shadow(remap = false)
    protected abstract void onFluidChanged(FluidStack stack);

    @Shadow(remap = false)
    public abstract void findRecipe();

    @Inject(method = "<init>(Lnet/minecraft/world/level/block/entity/BlockEntityType;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V", remap = false, at = @At("RETURN"))
    private void onConstructed(BlockEntityType<?> type, BlockPos pos, BlockState state, CallbackInfo ci) {
        this.tank = new SmartFluidTank(1000, this::onFluidChanged);
        this.fluidCapability = LazyOptional.of(() -> this.tank);
    }

    @Redirect(method = "tick", remap = false, at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fluids/capability/templates/FluidTank;getSpace()I"))
    private int redirectGetSpace(FluidTank targetTank) {
        if (targetTank.getFluidAmount() > 0 && (this.recipe == null || !this.recipe.getIngrenient().test(targetTank.getFluid()))) {
            this.findRecipe();
        }

        if (this.recipe != null) {
            int required = this.recipe.getIngrenient().getRequiredAmount();
            if (required > 0) {
                if (targetTank.getFluidAmount() >= required) {
                    // Treat the tank as "full" for recipe purposes once it holds
                    // enough fluid for the current recipe
                    return 0;
                }
                // The tank may be physically full but still not hold enough for
                // this recipe; report a nonzero space so the recipe cannot start
                return Math.max(1, targetTank.getSpace());
            }
        }

        return targetTank.getSpace();
    }

    @Redirect(method = "tick", remap = false, at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fluids/capability/templates/FluidTank;setFluid(Lnet/minecraftforge/fluids/FluidStack;)V"))
    private void redirectSetFluid(FluidTank targetTank, FluidStack stack) {
        // if the original code attempts to set EMPTY after recipe completion, drain
        // only required amount
        if (stack == null || stack.isEmpty()) {
            if (this.recipe != null) {
                int required = this.recipe.getIngrenient().getRequiredAmount();
                if (required > 0) {
                    targetTank.drain(required, IFluidHandler.FluidAction.EXECUTE);
                    return;
                }
            }
            // fallback: set empty as before
            targetTank.setFluid(FluidStack.EMPTY);
            return;
        }

        // non-empty set, keep original behavior
        targetTank.setFluid(stack);
    }

    @Redirect(method = "tick", remap = false, at = @At(value = "INVOKE", target = "Lnet/createmod/catnip/animation/LerpedFloat;chase(DDLnet/createmod/catnip/animation/LerpedFloat$Chaser;)Lnet/createmod/catnip/animation/LerpedFloat;"))
    private LerpedFloat redirectChase(LerpedFloat instance, double value, double speed, LerpedFloat.Chaser chaser) {
        if (this.recipe == null) {
            instance.chase(value, speed, chaser);
            return instance;
        }

        int required = this.recipe.getIngrenient().getRequiredAmount();
        double target = required > 0 ? Math.min(required - 1, value) : value;
        instance.chase(target, speed, chaser);
        return instance;
    }
}
