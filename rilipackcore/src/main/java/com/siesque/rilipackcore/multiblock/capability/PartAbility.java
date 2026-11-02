package com.siesque.rilipackcore.multiblock.capability;

import com.siesque.rilipackcore.multiblock.part.IMultiPart;
import lombok.Getter;

public enum PartAbility implements IPartAbility {
    ENERGY_INPUT("energy_input"),
    ENERGY_OUTPUT("energy_output"),
    ITEM_INPUT("item_input"),
    ITEM_OUTPUT("item_output"),
    FLUID_INPUT("fluid_input"),
    FLUID_OUTPUT("fluid_output"),
    PASSTHROUGH("passthrough");

    @Getter
    private final String name;

    PartAbility(String name) {
        this.name = name;
    }

    @Override
    public boolean isDefaultFor(IMultiPart part) {
        return false;
    }
}