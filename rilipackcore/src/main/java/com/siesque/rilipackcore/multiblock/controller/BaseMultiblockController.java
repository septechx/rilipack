package com.siesque.rilipackcore.multiblock.controller;

import com.lowdragmc.lowdraglib.syncdata.IManaged;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.FieldManagedStorage;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.siesque.rilipackcore.multiblock.capability.IO;
import com.siesque.rilipackcore.multiblock.capability.PartAbility;
import com.siesque.rilipackcore.multiblock.part.IMultiPart;
import com.siesque.rilipackcore.multiblock.pattern.BlockPattern;
import com.siesque.rilipackcore.multiblock.state.MultiblockState;
import lombok.Getter;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;

import java.util.*;

public abstract class BaseMultiblockController implements IMultiblockController, IManaged {
    public static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            BaseMultiblockController.class);
    @Getter
    protected final FieldManagedStorage syncStorage = new FieldManagedStorage(this);

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    public void onChanged() {
        blockEntity.setChanged();
    }

    protected static final int VALIDATION_INTERVAL = 20;
    protected final BlockEntity blockEntity;
    @Persisted
    @DescSynced
    @Getter
    protected boolean isFormed = false;
    @Persisted
    @DescSynced
    @Getter
    protected Direction frontFacing;
    @Getter
    protected MultiblockState multiblockState;
    @Persisted
    protected int validationTick;
    @Getter
    protected List<IEnergyStorage> energyContainers;
    @Getter
    protected Collection<IMultiPart> parts;

    public BaseMultiblockController(BlockEntity blockEntity) {
        this.blockEntity = blockEntity;
        this.frontFacing = Direction.NORTH;
        this.syncStorage.init();
    }

    @Override
    public void tick() {
        Level level = getLevel();
        if (level == null || level.isClientSide)
            return;

        if (multiblockState == null) {
            validateStructure();
            validationTick = 0;
        } else {
            validationTick++;
            if (validationTick >= VALIDATION_INTERVAL) {
                validateStructure();
                validationTick = 0;
            }
        }
    }

    protected void validateStructure() {
        BlockPattern pattern = getPattern();
        MultiblockState newState = pattern.match(getLevel(), getPos(), frontFacing, Direction.NORTH, false);

        if (newState.formed()) {
            boolean wasFormed = this.isFormed;
            this.multiblockState = newState;
            this.isFormed = true;
            if (!wasFormed) {
                onChanged();
            }
            onStructureFormed();
        } else {
            if (isFormed) {
                this.isFormed = false;
                onStructureInvalid();
                onChanged();
            }
            this.multiblockState = newState;
        }
    }

    @Override
    public void onStructureFormed() {
        this.parts = multiblockState.getParts() != null ? new ArrayList<>(multiblockState.getParts())
                : Collections.emptyList();
        initializeAbilities();
    }

    @Override
    public void onStructureInvalid() {
        this.parts = null;
        this.energyContainers = null;
    }

    protected void initializeAbilities() {
        List<IEnergyStorage> containers = new ArrayList<>();
        if (parts == null)
            return;

        for (IMultiPart part : parts) {
            if (part.getIO() == IO.NONE || part.getIO() == IO.OUTPUT)
                continue;
            if (Arrays.stream(part.getAbilities())
                    .anyMatch(ability -> ability.getName().equals(PartAbility.ENERGY_INPUT.getName()))) {
                BlockEntity be = part.self();
                LazyOptional<IEnergyStorage> energyOpt = be.getCapability(ForgeCapabilities.ENERGY);
                energyOpt.ifPresent(containers::add);
            }
        }
        this.energyContainers = containers;
    }

    public void setFrontFacing(Direction facing) {
        this.frontFacing = facing;
    }

    @Override
    public List<Component> getDisplayText() {
        List<Component> text = new ArrayList<>();
        if (isFormed()) {
            text.add(Component.literal("Structure Formed"));
        } else {
            text.add(Component.literal("Invalid Structure").withStyle(ChatFormatting.RED));
        }
        return text;
    }

    @Override
    public BlockPos getPos() {
        return blockEntity.getBlockPos();
    }

    @Override
    public Level getLevel() {
        return blockEntity.getLevel();
    }

    @Override
    public BlockEntity getBlockEntity() {
        return blockEntity;
    }
}
