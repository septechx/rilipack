package com.siesque.rilipackcore.multiblock.cleanroom;

import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.siesque.rilipackcore.block.RilipackcoreBlocks;
import com.siesque.rilipackcore.block.entity.capability.CleanroomInfo;
import com.siesque.rilipackcore.block.entity.capability.ICleanroomAware;
import com.siesque.rilipackcore.block.multiblock.FilterBlock;
import com.siesque.rilipackcore.multiblock.controller.BaseMultiblockController;
import com.siesque.rilipackcore.multiblock.pattern.BlockPattern;
import com.siesque.rilipackcore.multiblock.pattern.FactoryBlockPattern;
import com.siesque.rilipackcore.multiblock.pattern.Predicates;
import com.siesque.rilipackcore.multiblock.pattern.TraceabilityPredicate;
import com.siesque.rilipackcore.multiblock.pattern.util.RelativeDirection;
import com.siesque.rilipackcore.tag.RilipackcoreTags;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.energy.IEnergyStorage;

import java.util.*;

public class CleanroomController extends BaseMultiblockController {
    public static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(CleanroomController.class,
            BaseMultiblockController.MANAGED_FIELD_HOLDER);
    private static final int RESCAN_INTERVAL = 100;
    @Persisted
    @DescSynced
    protected int lDist = (CleanroomRules.MIN_SIZE - 1) / 2;
    @Persisted
    @DescSynced
    protected int rDist = (CleanroomRules.MIN_SIZE - 1) / 2;
    @Persisted
    @DescSynced
    protected int bDist = (CleanroomRules.MIN_SIZE - 1) / 2;
    @Persisted
    @DescSynced
    protected int fDist = (CleanroomRules.MIN_SIZE - 1) / 2;
    @Persisted
    @DescSynced
    protected int hDist = CleanroomRules.MIN_WALL_HEIGHT;
    @Persisted
    @DescSynced
    protected CleanroomType cleanroomType = null;
    @Persisted
    protected int progress = 0;
    @Persisted
    protected int energySpent = 0;
    @Persisted
    protected int energyRequired = 0;
    @Persisted
    @DescSynced
    protected int cleanAmount;
    private Map<BlockPos, ICleanroomAware> cleanroomAwareBlocks = new HashMap<>();
    private Set<BlockPos> lastPartPositions = Set.of();
    @Persisted
    private int rescanTick = 0;

    public CleanroomController(Object blockEntity) {
        super((BlockEntity) blockEntity);
        this.cleanAmount = 0;
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    public void tick() {
        super.tick();

        if (getLevel() != null && !getLevel().isClientSide && isFormed()) {
            if (progress <= 0 || energyRequired <= 0) {
                energyRequired = CleanroomRules.energyPerCycle(cleanAmount);
                energySpent = 0;
            }

            int remaining = energyRequired - energySpent;
            int toExtract = Math.min(CleanroomRules.energyPerTick(cleanAmount), remaining);
            if (toExtract > 0 && getEnergyContainers() != null) {
                int extracted = 0;
                for (IEnergyStorage storage : getEnergyContainers()) {
                    extracted += storage.extractEnergy(toExtract - extracted, false);
                    if (extracted >= toExtract)
                        break;
                }
                energySpent += extracted;
            }

            progress++;
            if (progress >= CleanroomRules.CYCLE_TICKS) {
                adjustCleanAmount(energySpent >= energyRequired ? 1 : -1);
                progress = 0;
                energySpent = 0;
                energyRequired = 0;
            }

            rescanTick++;
            if (rescanTick >= RESCAN_INTERVAL) {
                rescanCleanroomAwareBlocks();
                rescanTick = 0;
            }
        }
    }

    @Override
    public BlockPattern getPattern() {
        if (getLevel() != null)
            updateStructureDimensions();

        if (lDist <= 0 || rDist <= 0 || fDist <= 0 || bDist <= 0 || hDist <= 0) {
            return FactoryBlockPattern.start(RelativeDirection.UP, RelativeDirection.BACK, RelativeDirection.RIGHT)
                    .aisle("X")
                    .where('X', Predicates.custom(state -> false))
                    .build();
        }

        String[] floorLayer = buildFloorLayer(lDist, rDist, fDist, bDist);
        String[][] wallLayers = buildWallLayers(lDist, rDist, fDist, bDist, hDist);
        String[] ceilingLayer = buildCeilingLayer(lDist, rDist, fDist, bDist);

        FactoryBlockPattern pattern = FactoryBlockPattern
                .start(RelativeDirection.UP, RelativeDirection.BACK, RelativeDirection.RIGHT)
                .aisle(floorLayer);

        for (String[] wallLayer : wallLayers) {
            pattern.aisle(wallLayer);
        }

        return pattern.aisle(ceilingLayer)
                .where('C', Predicates.controller(this))
                .where('F', Predicates.states(getFilterBlocks()))
                .where('S',
                        Predicates.blocks(RilipackcoreBlocks.PLASCRETE_CASING.get(),
                                RilipackcoreBlocks.ENERGY_INPUT_HATCH.get())) // Frame/Borders
                .where('W',
                        Predicates
                                .blocks(RilipackcoreBlocks.PLASCRETE_CASING.get(),
                                        RilipackcoreBlocks.ENERGY_INPUT_HATCH.get())
                                .or(Predicates.doors(RilipackcoreTags.Blocks.CLEANROOM_DOORS))) // Walls
                .where('E',
                        Predicates.blocks(RilipackcoreBlocks.PLASCRETE_CASING.get(),
                                RilipackcoreBlocks.ENERGY_INPUT_HATCH.get())) // Floor
                .where(' ', innerPredicate())
                .build();
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();

        Block block = getMultiblockState().matchContext().get("filterBlock", Block.class);
        CleanroomType newType = block instanceof FilterBlock filterBlock
                ? filterBlock.getCleanroomType()
                : CleanroomType.CLEANROOM;

        Set<BlockPos> partPositions = getMultiblockState().getPartsMap() == null
                ? Set.of()
                : Set.copyOf(getMultiblockState().getPartsMap().keySet());

        boolean typeChanged = newType != this.cleanroomType;
        boolean partsChanged = !partPositions.equals(this.lastPartPositions);

        this.cleanroomType = newType;
        this.lastPartPositions = partPositions;

        if (typeChanged || partsChanged) {
            rescanCleanroomAwareBlocks();
        }
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        this.cleanroomType = null;
        this.lastPartPositions = Set.of();
        this.progress = 0;
        this.energySpent = 0;
        this.energyRequired = 0;
        notifyCleanroomAwareBlocksInvalid();
    }

    private void updateStructureDimensions() {
        Level world = getLevel();
        if (world == null)
            return;

        Direction front = getFrontFacing();
        Direction back = front.getOpposite();
        Direction left = front.getCounterClockWise();
        Direction right = left.getOpposite();

        BlockPos.MutableBlockPos lPos = getPos().mutable();
        BlockPos.MutableBlockPos rPos = getPos().mutable();
        BlockPos.MutableBlockPos fPos = getPos().mutable();
        BlockPos.MutableBlockPos bPos = getPos().mutable();

        int foundL = 0, foundR = 0, foundB = 0, foundF = 0, foundH = 0;

        // Scan Horizontally (Ceiling layer)
        // We look for the casing border. Inside should be filters.
        for (int i = 1; i <= CleanroomRules.MAX_SIZE / 2 + 1; i++) {
            if (foundL == 0 && isBlockBorder(world, lPos, left))
                foundL = i;
            if (foundR == 0 && isBlockBorder(world, rPos, right))
                foundR = i;
            if (foundB == 0 && isBlockBorder(world, bPos, back))
                foundB = i;
            if (foundF == 0 && isBlockBorder(world, fPos, front))
                foundF = i;
        }

        int width = foundL + foundR + 1;
        int depth = foundF + foundB + 1;

        if (!CleanroomRules.isValidHorizontalSize(width, depth)
                || !CleanroomRules.isCentered(foundL, foundR, foundF, foundB)) {
            clearStructureDimensions();
            return;
        }

        // Scan Vertically (Downwards)
        BlockPos.MutableBlockPos hPos = getPos().mutable();

        for (int i = 1; i <= CleanroomRules.MAX_WALL_HEIGHT + 2; i++) {
            // We are at ceiling level. Move down `i` blocks.
            // If i=1, we are at first wall block.
            // We need to find the floor.
            BlockState state = world.getBlockState(hPos.move(Direction.DOWN));
            if (isBlockFloor(state)) {
                foundH = i - 1; // Height of the wall gap
                break;
            }
        }

        if (!CleanroomRules.isValidWallHeight(foundH)) {
            clearStructureDimensions();
            return;
        }

        // Only update fields if the scan was successful and valid
        if (this.lDist != foundL || this.rDist != foundR || this.fDist != foundF || this.bDist != foundB
                || this.hDist != foundH) {
            this.lDist = foundL;
            this.rDist = foundR;
            this.fDist = foundF;
            this.bDist = foundB;
            this.hDist = foundH;
            onChanged();
        }
    }

    private void clearStructureDimensions() {
        if (this.lDist != 0 || this.rDist != 0 || this.fDist != 0 || this.bDist != 0 || this.hDist != 0) {
            this.lDist = 0;
            this.rDist = 0;
            this.fDist = 0;
            this.bDist = 0;
            this.hDist = 0;
            onChanged();
        }
    }

    private boolean isBlockBorder(Level world, BlockPos.MutableBlockPos pos, Direction direction) {
        BlockState state = world.getBlockState(pos.move(direction));
        return state.getBlock() == RilipackcoreBlocks.PLASCRETE_CASING.get()
                || state.getBlock() == RilipackcoreBlocks.ENERGY_INPUT_HATCH.get();
    }

    private boolean isBlockFloor(BlockState state) {
        return state.getBlock() == RilipackcoreBlocks.PLASCRETE_CASING.get()
                || state.getBlock() == RilipackcoreBlocks.ENERGY_INPUT_HATCH.get();
    }

    private String[] buildFloorLayer(int l, int r, int f, int b) {
        // Floor is solid 'E'
        String[] layer = new String[f + b + 1];
        Arrays.fill(layer, "E".repeat(Math.max(0, l + r + 1)));
        return layer;
    }

    private String[][] buildWallLayers(int l, int r, int f, int b, int h) {
        String[][] layers = new String[h][f + b + 1];
        for (int k = 0; k < h; k++) {
            for (int j = 0; j < f + b + 1; j++) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < l + r + 1; i++) {
                    // Walls are on the border
                    if (i == 0 || i == l + r || j == 0 || j == f + b) {
                        sb.append('W');
                    } else {
                        sb.append(' '); // Air inside
                    }
                }
                layers[k][j] = sb.toString();
            }
        }
        return layers;
    }

    private String[] buildCeilingLayer(int l, int r, int f, int b) {
        String[] layer = new String[f + b + 1];
        for (int j = 0; j < layer.length; j++) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < l + r + 1; i++) {
                if (i == 0 || i == l + r || j == 0 || j == f + b) {
                    sb.append('S'); // Border
                } else if (i == l && j == f) {
                    sb.append('C'); // Controller
                } else {
                    sb.append('F'); // Filter
                }
            }
            layer[j] = sb.toString();
        }
        return layer;
    }

    private TraceabilityPredicate innerPredicate() {
        return Predicates.custom(state -> true);
    }

    private BlockState[] getFilterBlocks() {
        Block block1 = RilipackcoreBlocks.CLEANROOM_FILTER.get();
        Block block2 = RilipackcoreBlocks.CLEANROOM_FILTER_STERILE.get();

        return new BlockState[] {
                block1.defaultBlockState(),
                block2.defaultBlockState(),
        };
    }

    public void adjustCleanAmount(int amount) {
        int old = this.cleanAmount;
        this.cleanAmount = CleanroomRules.clampCleanAmount(this.cleanAmount + amount);
        if (old != this.cleanAmount) {
            onChanged();
            notifyCleanroomAwareBlocksUpdate();
        }
    }

    public boolean isClean() {
        return CleanroomRules.isClean(this.cleanAmount);
    }

    private void rescanCleanroomAwareBlocks() {
        Level level = getLevel();
        if (level == null || !isFormed())
            return;

        List<ICleanroomAware> found = CleanroomAwareScanner.scanInterior(
                level, getPos(), frontFacing, lDist, rDist, fDist, bDist, hDist);

        CleanroomInfo info = getCurrentCleanroomInfo();

        Map<BlockPos, ICleanroomAware> newMap = new HashMap<>();

        for (ICleanroomAware aware : found) {
            BlockEntity be = (BlockEntity) aware;
            aware.setCleanroomInfo(info);
            newMap.put(be.getBlockPos(), aware);
        }

        for (BlockPos pos : cleanroomAwareBlocks.keySet()) {
            if (!newMap.containsKey(pos)) {
                ICleanroomAware aware = cleanroomAwareBlocks.get(pos);
                aware.setCleanroomInfo(null);
            }
        }

        cleanroomAwareBlocks = newMap;
    }

    private void notifyCleanroomAwareBlocksInvalid() {
        for (ICleanroomAware aware : cleanroomAwareBlocks.values()) {
            aware.setCleanroomInfo(null);
        }
        cleanroomAwareBlocks.clear();
    }

    private void notifyCleanroomAwareBlocksUpdate() {
        CleanroomInfo info = getCurrentCleanroomInfo();

        cleanroomAwareBlocks.entrySet().removeIf(entry -> {
            BlockEntity be = (BlockEntity) entry.getValue();
            if (be.isRemoved()) {
                return true;
            }
            entry.getValue().setCleanroomInfo(info);
            return false;
        });
    }

    public CleanroomInfo getCurrentCleanroomInfo() {
        if (!isFormed() || cleanroomType == null) {
            return null;
        }
        return new CleanroomInfo(getPos(), cleanroomType, cleanAmount);
    }

    @Override
    public List<Component> getDisplayText() {
        List<Component> text = new ArrayList<>();

        if (isFormed()) {
            if (cleanroomType != null) {
                text.add(Component.translatable(cleanroomType.getTranslationKey()));
            }

            if (isClean()) {
                text.add(Component.translatable("rilipackcore.multiblock.cleanroom.clean_state"));
            } else {
                text.add(Component.translatable("rilipackcore.multiblock.cleanroom.dirty_state"));
            }

            text.add(Component.translatable("rilipackcore.multiblock.cleanroom.clean_amount", cleanAmount));
            text.add(Component.translatable("rilipackcore.multiblock.dimensions",
                    lDist + rDist + 1, hDist + 2, fDist + bDist + 1));
        } else {
            text.add(Component.translatable("rilipackcore.multiblock.invalid_structure")
                    .withStyle(ChatFormatting.RED));

            if (getMultiblockState() != null && getMultiblockState().errorMessage() != null) {
                text.add(getMultiblockState().errorMessage());
            }
        }

        return text;
    }
}
