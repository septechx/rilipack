package com.siesque.rilipackcore.multiblock.cleanroom;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CleanroomRulesTest {

    @Test
    void acceptsOnlyOddSizesWithinBounds() {
        int[] valid = {3, 5, 7, 9};
        for (int size : valid) {
            assertTrue(CleanroomRules.isValidSize(size), size + " should be valid");
        }

        int[] invalid = {-1, 0, 1, 2, 4, 6, 8, 10, 11, 12};
        for (int size : invalid) {
            assertFalse(CleanroomRules.isValidSize(size), size + " should be invalid");
        }
    }

    @Test
    void requiresBothHorizontalDimensionsToBeValid() {
        assertTrue(CleanroomRules.isValidHorizontalSize(3, 3));
        assertTrue(CleanroomRules.isValidHorizontalSize(5, 7));
        assertTrue(CleanroomRules.isValidHorizontalSize(9, 3));

        assertFalse(CleanroomRules.isValidHorizontalSize(3, 4));
        assertFalse(CleanroomRules.isValidHorizontalSize(4, 3));
        assertFalse(CleanroomRules.isValidHorizontalSize(11, 3));
        assertFalse(CleanroomRules.isValidHorizontalSize(3, 1));
    }

    @Test
    void controllerMustBeExactlyCentered() {
        assertTrue(CleanroomRules.isCentered(1, 1, 1, 1));
        assertTrue(CleanroomRules.isCentered(3, 3, 4, 4));

        assertFalse(CleanroomRules.isCentered(1, 2, 1, 1));
        assertFalse(CleanroomRules.isCentered(1, 1, 1, 2));
        assertFalse(CleanroomRules.isCentered(2, 1, 2, 1));
    }

    @Test
    void wallHeightMustBeWithinBounds() {
        for (int height = 3; height <= 7; height++) {
            assertTrue(CleanroomRules.isValidWallHeight(height), height + " should be valid");
        }

        assertFalse(CleanroomRules.isValidWallHeight(0));
        assertFalse(CleanroomRules.isValidWallHeight(2));
        assertFalse(CleanroomRules.isValidWallHeight(8));
    }

    @Test
    void interiorOffsetsSkipTheOuterShell() {
        // A 3x3 room has one interior block, directly below the controller.
        assertEquals(0, CleanroomRules.firstInteriorOffset(1));
        assertEquals(1, CleanroomRules.interiorWidth(1, 1));
        assertEquals(1, CleanroomRules.interiorDepth(1, 1));

        // A 5x5 room has three interior blocks in each direction.
        assertEquals(1, CleanroomRules.firstInteriorOffset(2));
        assertEquals(3, CleanroomRules.interiorWidth(2, 2));
        assertEquals(3, CleanroomRules.interiorDepth(2, 2));

        assertEquals(3, CleanroomRules.firstInteriorOffset(4));
        assertEquals(7, CleanroomRules.interiorWidth(4, 4));
    }

    @Test
    void interiorNeverReportsNegativeDimensions() {
        assertEquals(0, CleanroomRules.firstInteriorOffset(0));
        assertEquals(0, CleanroomRules.interiorWidth(0, 0));
        assertEquals(0, CleanroomRules.interiorDepth(0, 0));
        assertEquals(0, CleanroomRules.interiorHeight(0));
    }

    @Test
    void interiorHeightMatchesWallHeight() {
        assertEquals(3, CleanroomRules.interiorHeight(3));
        assertEquals(7, CleanroomRules.interiorHeight(7));
    }

    @Test
    void cleanlinessThresholdAndClamping() {
        assertFalse(CleanroomRules.isClean(0));
        assertFalse(CleanroomRules.isClean(94));
        assertTrue(CleanroomRules.isClean(95));
        assertTrue(CleanroomRules.isClean(100));

        assertEquals(0, CleanroomRules.clampCleanAmount(-5));
        assertEquals(42, CleanroomRules.clampCleanAmount(42));
        assertEquals(100, CleanroomRules.clampCleanAmount(140));
    }

    @Test
    void fullCleanlinessHalvesTheEnergyDraw() {
        assertEquals(400, CleanroomRules.energyPerTick(0));
        assertEquals(400, CleanroomRules.energyPerTick(50));
        assertEquals(400, CleanroomRules.energyPerTick(99));
        assertEquals(200, CleanroomRules.energyPerTick(100));

        assertEquals(20_000, CleanroomRules.energyPerCycle(0));
        assertEquals(20_000, CleanroomRules.energyPerCycle(99));
        assertEquals(10_000, CleanroomRules.energyPerCycle(100));
    }

    @Test
    void cycleLengthIsFiftyTicks() {
        assertEquals(50, CleanroomRules.CYCLE_TICKS);
        assertEquals(CleanroomRules.energyPerTick(0) * 50, CleanroomRules.energyPerCycle(0));
    }
}
