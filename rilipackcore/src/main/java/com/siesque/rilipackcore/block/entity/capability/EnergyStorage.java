package com.siesque.rilipackcore.block.entity.capability;

import com.lowdragmc.lowdraglib.syncdata.IManaged;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.FieldManagedStorage;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import lombok.Getter;
import net.minecraftforge.energy.IEnergyStorage;

public class EnergyStorage implements IEnergyStorage, IManaged {
    public static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(EnergyStorage.class);
    @Getter
    protected final FieldManagedStorage syncStorage = new FieldManagedStorage(this);
    protected Runnable onChange;
    @Persisted
    @DescSynced
    protected long energy;
    @Persisted
    @DescSynced
    protected long capacity;
    @Persisted
    protected long maxReceive;
    @Persisted
    protected long maxExtract;

    public EnergyStorage(long capacity, long maxTransfer) {
        this(capacity, maxTransfer, maxTransfer, 0);
    }

    public EnergyStorage(long capacity, long maxReceive, long maxExtract, long energy) {
        this.capacity = capacity;
        this.maxReceive = maxReceive;
        this.maxExtract = maxExtract;
        this.energy = energy;
        this.syncStorage.init();
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    public void onChanged() {
        if (onChange != null) onChange.run();
    }

    public EnergyStorage setOnChanged(Runnable onChange) {
        this.onChange = onChange;
        return this;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        long energyReceived = Math.min(this.capacity - this.energy, Math.min(this.maxReceive, maxReceive));
        if (!simulate && energyReceived > 0) {
            this.energy += energyReceived;
            onChanged();
        }
        return (int) energyReceived;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        long energyExtracted = Math.min(this.energy, Math.min(this.maxExtract, maxExtract));
        if (!simulate && energyExtracted > 0) {
            this.energy -= energyExtracted;
            onChanged();
        }
        return (int) energyExtracted;
    }

    @Override
    public int getEnergyStored() {
        return (int) this.energy;
    }

    @Override
    public int getMaxEnergyStored() {
        return (int) this.capacity;
    }

    @Override
    public boolean canExtract() {
        return this.maxExtract > 0;
    }

    @Override
    public boolean canReceive() {
        return this.maxReceive > 0;
    }

    public void setEnergy(long energy) {
        this.energy = energy;
        onChanged();
    }

    public void setCapacity(long capacity) {
        this.capacity = capacity;
        onChanged();
    }
}