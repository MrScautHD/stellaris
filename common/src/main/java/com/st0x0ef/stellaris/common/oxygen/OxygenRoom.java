package com.st0x0ef.stellaris.common.oxygen;

import com.st0x0ef.stellaris.common.blocks.entities.machines.OxygenDistributorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;

import java.util.*;

public class OxygenRoom {
    private final BlockPos distributorPos;
    private final Set<BlockPos> oxygenatedPositions;
    private final Queue<BlockPos> positionsToCheck;
    private final ServerLevel level;

    private static final int HALF_ROOM_SIZE = 16;

    public OxygenRoom(ServerLevel level, BlockPos distributorPos) {
        this.distributorPos = distributorPos;
        this.oxygenatedPositions = new LinkedHashSet<>();
        this.positionsToCheck = new LinkedList<>();
        this.level = level;
    }

    public BlockPos getDistributorPosition() {
        return distributorPos;
    }

    public void updateOxygenRoom() {
        BlockPos[] sidePos = new BlockPos[] {distributorPos.above(), distributorPos.below(), distributorPos.east(), distributorPos.west(), distributorPos.south(), distributorPos.north()};
        for (BlockPos pos : sidePos) {
            if (isAirBlock(pos)) {
                positionsToCheck.clear();
                positionsToCheck.offer(pos);
                Set<BlockPos> visited = new HashSet<>();
                DimensionOxygenManager dimensionManager = GlobalOxygenManager.getInstance().getOrCreateDimensionManager(level);

                while (!positionsToCheck.isEmpty()) {
                    BlockPos currentPos = positionsToCheck.poll();
                    if (visited.add(currentPos) && isAirBlock(currentPos)) {
                        if (addOxygenatedPosition(currentPos)) {
                            if (isOnBorderBox(currentPos)) {
                                dimensionManager.addRoomToCheckIfOpen(currentPos, this);
                            }

                            for (Direction direction : Direction.values()) {
                                positionsToCheck.offer(currentPos.relative(direction));
                            }
                        }
                    }
                }
            }
        }
    }

    public void removeOxygenInRoom() {
        oxygenatedPositions.clear();
    }

    private boolean isOnBorderBox(BlockPos pos) {
        int dx = Math.abs(pos.getX() - distributorPos.getX());
        int dy = Math.abs(pos.getY() - distributorPos.getY());
        int dz = Math.abs(pos.getZ() - distributorPos.getZ());
        return dx == HALF_ROOM_SIZE || dy == HALF_ROOM_SIZE || dz == HALF_ROOM_SIZE;
    }

    public boolean hasOxygenAt(BlockPos pos) {
        return oxygenatedPositions.contains(pos);
    }

    private boolean isAirBlock(BlockPos pos) {
        return level.getBlockState(pos).isAir();
    }

    private boolean addOxygenatedPosition(BlockPos pos) {
        if (level.getBlockEntity(distributorPos) instanceof OxygenDistributorBlockEntity distributor && distributor.useOxygenAndEnergy()) {
            oxygenatedPositions.add(pos);
            return true;
        }

        return false;
    }
}
