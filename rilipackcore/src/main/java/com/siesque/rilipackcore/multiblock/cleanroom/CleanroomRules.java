package com.siesque.rilipackcore.multiblock.cleanroom;

public final class CleanroomRules {
    public static final int MIN_SIZE = 3;
    public static final int MAX_SIZE = 10;
    public static final int MIN_WALL_HEIGHT = 3;
    public static final int MAX_WALL_HEIGHT = 7;

    public static final int CLEAN_AMOUNT_THRESHOLD = 95;
    public static final int MAX_CLEAN_AMOUNT = 100;

    public static final int CYCLE_TICKS = 50;
    public static final int ENERGY_PER_TICK = 400;
    public static final int ENERGY_PER_TICK_WHEN_CLEAN = 200;

    private CleanroomRules() {
    }

    public static boolean isValidSize(int size) {
        return size >= MIN_SIZE && size <= MAX_SIZE && (size & 1) == 1;
    }

    public static boolean isValidHorizontalSize(int width, int depth) {
        return isValidSize(width) && isValidSize(depth);
    }

    public static boolean isCentered(int left, int right, int front, int back) {
        return left == right && front == back;
    }

    public static boolean isValidWallHeight(int height) {
        return height >= MIN_WALL_HEIGHT && height <= MAX_WALL_HEIGHT;
    }

    public static int firstInteriorOffset(int wallDistance) {
        return Math.max(0, wallDistance - 1);
    }

    public static int interiorWidth(int left, int right) {
        return Math.max(0, left + right - 1);
    }

    public static int interiorDepth(int front, int back) {
        return Math.max(0, front + back - 1);
    }

    public static int interiorHeight(int wallHeight) {
        return Math.max(0, wallHeight);
    }

    public static boolean isClean(int cleanAmount) {
        return cleanAmount >= CLEAN_AMOUNT_THRESHOLD;
    }

    public static int clampCleanAmount(int cleanAmount) {
        return Math.max(0, Math.min(MAX_CLEAN_AMOUNT, cleanAmount));
    }

    public static int energyPerTick(int cleanAmount) {
        return cleanAmount >= MAX_CLEAN_AMOUNT ? ENERGY_PER_TICK_WHEN_CLEAN : ENERGY_PER_TICK;
    }

    public static int energyPerCycle(int cleanAmount) {
        return energyPerTick(cleanAmount) * CYCLE_TICKS;
    }
}
