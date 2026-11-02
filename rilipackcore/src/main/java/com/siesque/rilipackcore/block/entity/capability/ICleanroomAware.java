package com.siesque.rilipackcore.block.entity.capability;

import com.siesque.rilipackcore.multiblock.cleanroom.CleanroomType;

public interface ICleanroomAware {
    CleanroomInfo getCleanroomInfo();
    void setCleanroomInfo(CleanroomInfo info);

    default boolean isInCleanroom() {
        return getCleanroomInfo() != null;
    }

    default boolean isInCleanCleanroom() {
        CleanroomInfo info = getCleanroomInfo();
        return info != null && info.isClean();
    }

    default CleanroomType getCleanroomType() {
        CleanroomInfo info = getCleanroomInfo();
        return info != null ? info.getCleanroomType() : null;
    }

    default int getCleanliness() {
        CleanroomInfo info = getCleanroomInfo();
        return info != null ? info.getCleanliness() : 0;
    }
}