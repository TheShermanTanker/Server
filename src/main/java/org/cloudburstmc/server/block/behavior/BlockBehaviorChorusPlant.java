package org.cloudburstmc.server.block.behavior;

import org.cloudburstmc.api.block.Block;
import org.cloudburstmc.api.item.ItemStack;
import org.cloudburstmc.api.util.data.BlockColor;

public class BlockBehaviorChorusPlant extends BlockBehaviorTransparent {


    @Override
    public ItemStack[] getDrops(Block block, ItemStack hand) {
        return new ItemStack[0];
    }

    @Override
    public BlockColor getColor(Block block) {
        return BlockColor.PURPLE_BLOCK_COLOR;
    }
}
