package org.cloudburstmc.server.block.behavior;

import org.cloudburstmc.api.block.Block;
import org.cloudburstmc.api.util.data.BlockColor;

public class BlockBehaviorStairsCobblestone extends BlockBehaviorStairs {
    @Override
    public BlockColor getColor(Block block) {
        return BlockColor.STONE_BLOCK_COLOR;
    }

}
