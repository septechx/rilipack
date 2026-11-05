package com.siesque.rilipackcore.block.entity.capability;

import com.siesque.rilipackcore.multiblock.cleanroom.CleanroomRules;
import com.siesque.rilipackcore.multiblock.cleanroom.CleanroomType;
import net.minecraft.core.BlockPos;

public class CleanroomInfo {
    private final BlockPos controllerPos;
    private final CleanroomType cleanroomType;
    private final int cleanliness;

    public CleanroomInfo(BlockPos controllerPos, CleanroomType cleanroomType, int cleanliness) {
        this.controllerPos = controllerPos;
        this.cleanroomType = cleanroomType;
        this.cleanliness = cleanliness;
    }

    public BlockPos getControllerPos() {
        return controllerPos;
    }

    public CleanroomType getCleanroomType() {
        return cleanroomType;
    }

    public int getCleanliness() {
        return cleanliness;
    }

    public boolean isClean() {
        return CleanroomRules.isClean(cleanliness);
    }
}
