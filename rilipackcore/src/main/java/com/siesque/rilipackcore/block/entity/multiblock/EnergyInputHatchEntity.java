package com.siesque.rilipackcore.block.entity.multiblock;

import com.lowdragmc.lowdraglib.syncdata.IManaged;
import com.lowdragmc.lowdraglib.syncdata.IManagedStorage;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.blockentity.IAsyncAutoSyncBlockEntity;
import com.lowdragmc.lowdraglib.syncdata.blockentity.IAutoPersistBlockEntity;
import com.lowdragmc.lowdraglib.syncdata.blockentity.IRPCBlockEntity;
import com.lowdragmc.lowdraglib.syncdata.field.FieldManagedStorage;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.siesque.rilipackcore.block.entity.RilipackcoreBlockEntities;
import com.siesque.rilipackcore.block.entity.capability.EnergyStorage;
import com.siesque.rilipackcore.block.multiblock.EnergyInputHatch;
import com.siesque.rilipackcore.multiblock.capability.IO;
import com.siesque.rilipackcore.multiblock.capability.PartAbility;
import com.siesque.rilipackcore.multiblock.part.IMultiPart;
import lombok.Getter;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class EnergyInputHatchEntity extends BlockEntity implements IMultiPart, IAsyncAutoSyncBlockEntity, IAutoPersistBlockEntity, IManaged, IRPCBlockEntity {
    public static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(EnergyInputHatchEntity.class);
    @Getter
    protected final FieldManagedStorage syncStorage = new FieldManagedStorage(this);

    @Persisted
    @DescSynced
    @Getter
    private final EnergyStorage energyStorage;
    private final LazyOptional<IEnergyStorage> energyCapability;

    public EnergyInputHatchEntity(BlockPos pos, BlockState state) {
        super(RilipackcoreBlockEntities.ENERGY_INPUT_HATCH.get(), pos, state);
        this.energyStorage = new EnergyStorage(100_000, 512).setOnChanged(this::setChanged);
        this.energyCapability = LazyOptional.of(() -> energyStorage);
        this.syncStorage.init();
    }

    public static void tick(Level level, BlockPos pos, BlockState state, EnergyInputHatchEntity entity) {
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    public void onChanged() {
        setChanged();
    }

    @Override
    public IManagedStorage getRootStorage() {
        return syncStorage;
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY) {
            Direction facing = getBlockState().getValue(EnergyInputHatch.FACING);
            if (side == null || side == facing) {
                return energyCapability.cast();
            }
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        energyCapability.invalidate();
    }

    // IMultiPart implementation
    @Override
    public BlockEntity self() {
        return this;
    }

    @Override
    public PartAbility[] getAbilities() {
        return new PartAbility[]{PartAbility.ENERGY_INPUT};
    }

    @Override
    public IO getIO() {
        return IO.INPUT;
    }

    @Override
    public boolean isController() {
        return false;
    }

    @Override
    public BlockPos getPos() {
        return worldPosition;
    }
}
