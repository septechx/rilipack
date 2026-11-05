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
    protected int energy;
    @Persisted
    @DescSynced
    protected int capacity;
    @Persisted
    protected int maxReceive;
    @Persisted
    protected int maxExtract;

    public EnergyStorage(int capacity, int maxTransfer) {
        this(capacity, maxTransfer, maxTransfer, 0);
    }

    public EnergyStorage(int capacity, int maxReceive, int maxExtract, int energy) {
        this.capacity = Math.max(0, capacity);
        this.maxReceive = Math.max(0, maxReceive);
        this.maxExtract = Math.max(0, maxExtract);
        this.energy = Math.max(0, Math.min(energy, this.capacity));
        this.syncStorage.init();
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    public void onChanged() {
        if (onChange != null)
            onChange.run();
    }

    public EnergyStorage setOnChanged(Runnable onChange) {
        this.onChange = onChange;
        return this;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        int energyReceived = Math.min(this.capacity - this.energy, Math.min(this.maxReceive, Math.max(0, maxReceive)));
        if (!simulate && energyReceived > 0) {
            this.energy += energyReceived;
            onChanged();
        }
        return Math.max(0, energyReceived);
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        int energyExtracted = Math.min(this.energy, Math.min(this.maxExtract, Math.max(0, maxExtract)));
        if (!simulate && energyExtracted > 0) {
            this.energy -= energyExtracted;
            onChanged();
        }
        return Math.max(0, energyExtracted);
    }

    @Override
    public int getEnergyStored() {
        return this.energy;
    }

    @Override
    public int getMaxEnergyStored() {
        return this.capacity;
    }

    @Override
    public boolean canExtract() {
        return this.maxExtract > 0;
    }

    @Override
    public boolean canReceive() {
        return this.maxReceive > 0;
    }

    public void setEnergy(int energy) {
        this.energy = Math.max(0, Math.min(energy, this.capacity));
        onChanged();
    }

    public void setCapacity(int capacity) {
        this.capacity = Math.max(0, capacity);
        this.energy = Math.min(this.energy, this.capacity);
        onChanged();
    }
}
