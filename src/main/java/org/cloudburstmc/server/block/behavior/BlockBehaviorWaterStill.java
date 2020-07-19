package org.cloudburstmc.server.block.behavior;

import org.cloudburstmc.server.block.BlockFactory;
import org.cloudburstmc.server.block.BlockState;
import org.cloudburstmc.server.utils.Identifier;

import static org.cloudburstmc.server.block.BlockTypes.WATER;

/**
 * author: Angelic47
 * Nukkit Project
 */
public class BlockBehaviorWaterStill extends BlockBehaviorWater {

    protected BlockBehaviorWaterStill(Identifier id, Identifier flowingId, Identifier stationaryId) {
        super(id, flowingId, stationaryId);
    }

    protected BlockBehaviorWaterStill(Identifier flowingId, Identifier stationaryId) {
        this(stationaryId, flowingId, stationaryId);
    }

    @Override
    public BlockState getBlock(int meta) {
        return BlockState.get(WATER, meta);
    }

    public static BlockFactory factory(Identifier flowingId) {
        return id -> new BlockBehaviorWaterStill(flowingId, id);
    }
}
