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
import com.siesque.rilipackcore.multiblock.capability.IO;
import com.siesque.rilipackcore.multiblock.capability.PartAbility;
import com.siesque.rilipackcore.multiblock.cleanroom.CleanroomController;
import com.siesque.rilipackcore.multiblock.part.IMultiPart;
import lombok.Getter;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CleanroomControllerEntity extends BlockEntity
        implements IMultiPart, IAsyncAutoSyncBlockEntity, IAutoPersistBlockEntity, IManaged, IRPCBlockEntity {
    public static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            CleanroomControllerEntity.class);
    @Getter
    protected final FieldManagedStorage syncStorage = new FieldManagedStorage(this);

    @Persisted
    @DescSynced
    @Getter
    private final CleanroomController controller;

    public CleanroomControllerEntity(BlockPos pos, BlockState state) {
        super(RilipackcoreBlockEntities.CLEANROOM_CONTROLLER.get(), pos, state);
        this.controller = new CleanroomController(this);
        this.syncStorage.init();
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

    public void tick() {
        if (controller != null) {
            controller.tick();
        }
    }

    @Override
    public BlockEntity self() {
        return this;
    }

    @Override
    public PartAbility[] getAbilities() {
        return new PartAbility[0];
    }

    @Override
    public IO getIO() {
        return IO.NONE;
    }

    @Override
    public boolean isController() {
        return true;
    }

    @Override
    public BlockPos getPos() {
        return worldPosition;
    }

    public List<Component> getDisplayText() {
        return controller != null ? controller.getDisplayText() : List.of();
    }
}
