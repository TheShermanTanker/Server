package org.cloudburstmc.server.block.behavior;

import org.cloudburstmc.api.block.Block;
import org.cloudburstmc.api.entity.Entity;
import org.cloudburstmc.api.item.ItemStack;
import org.cloudburstmc.api.item.ItemTypes;
import org.cloudburstmc.api.util.data.BlockColor;
import org.cloudburstmc.server.registry.CloudItemRegistry;

public class BlockBehaviorCobweb extends FloodableBlockBehavior {


    @Override
    public void onEntityCollide(Block block, Entity entity) {
        entity.resetFallDistance();
    }

    @Override
    public ItemStack[] getDrops(Block block, ItemStack hand) {
        var behavior = hand.getBehavior();
        if (behavior.isShears() || behavior.isSword()) {
            return new ItemStack[]{
                    CloudItemRegistry.get().getItem(ItemTypes.STRING)
            };
        } else {
            return new ItemStack[0];
        }
    }

    @Override
    public BlockColor getColor(Block block) {
        return BlockColor.CLOTH_BLOCK_COLOR;
    }


}
