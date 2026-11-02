package com.siesque.rilipackcore.multiblock.pattern;

import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class BlockWorldState implements IBlockWorldState {
    @Getter
    private final Level world;
    @Getter
    private final BlockPos pos;
    private final Direction facing;
    private final Direction upDirection;
    private final boolean isFlipped;
    private final MatchContext matchContext;
    private BlockState blockState;
    private BlockEntity blockEntity;

    public BlockWorldState(Level world, BlockPos pos, Direction facing, Direction upDirection, boolean isFlipped, MatchContext matchContext) {
        this.world = world;
        this.pos = pos;
        this.facing = facing;
        this.upDirection = upDirection;
        this.isFlipped = isFlipped;
        this.matchContext = matchContext;
    }

    @Override
    public BlockState getBlockState() {
        if (blockState == null) {
            blockState = world.getBlockState(pos);
        }
        return blockState;
    }

    @Override
    public BlockEntity getBlockEntity() {
        if (blockEntity == null) {
            blockEntity = world.getBlockEntity(pos);
        }
        return blockEntity;
    }

    @Override
    public MatchContext getMatchContext() {
        return matchContext;
    }

    @Override
    public Direction getFacing() {
        return facing;
    }

    @Override
    public Direction getUpDirection() {
        return upDirection;
    }

    @Override
    public boolean isFlipped() {
        return isFlipped;
    }
}