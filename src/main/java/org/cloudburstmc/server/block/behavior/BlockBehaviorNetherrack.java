package org.cloudburstmc.server.block.behavior;

import org.cloudburstmc.api.block.Block;
import org.cloudburstmc.api.util.data.BlockColor;

public class BlockBehaviorNetherrack extends BlockBehaviorSolid {

    @Override
    public BlockColor getColor(Block state) {
        return BlockColor.NETHERRACK_BLOCK_COLOR;
    }


}
