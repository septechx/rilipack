package com.siesque.rilipackcore.multiblock.cleanroom;

import com.siesque.rilipackcore.block.entity.capability.ICleanroomAware;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.ArrayList;
import java.util.List;

public class CleanroomAwareScanner {

    public static List<ICleanroomAware> scanInterior(Level level, BlockPos controllerPos,
                                                     Direction frontFacing, int lDist, int rDist, int fDist, int bDist, int hDist) {

        List<ICleanroomAware> found = new ArrayList<>();

        Direction left = frontFacing.getCounterClockWise();
        Direction right = left.getOpposite();
        Direction back = frontFacing.getOpposite();

        BlockPos start = controllerPos
                .relative(left, lDist)
                .relative(frontFacing, fDist);

        int width = Math.max(0, lDist + rDist);
        int depth = Math.max(0, fDist + bDist);

        for (int w = 0; w < width; w++) {
            for (int h = 0; h < hDist; h++) {
                for (int d = 0; d < depth; d++) {
                    BlockPos pos = start
                            .relative(right, w)
                            .relative(Direction.DOWN, h)
                            .relative(back, d);

                    BlockEntity be = level.getBlockEntity(pos);
                    if (be instanceof ICleanroomAware aware) {
                        found.add(aware);
                    }
                }
            }
        }

        return found;
    }
}
