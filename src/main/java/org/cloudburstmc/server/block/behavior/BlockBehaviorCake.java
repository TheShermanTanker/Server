package org.cloudburstmc.server.block.behavior;

import com.nukkitx.math.vector.Vector3f;
import org.cloudburstmc.api.block.Block;
import org.cloudburstmc.api.block.BlockTraits;
import org.cloudburstmc.api.block.BlockTypes;
import org.cloudburstmc.api.item.ItemStack;
import org.cloudburstmc.api.item.ItemTypes;
import org.cloudburstmc.api.player.Player;
import org.cloudburstmc.api.util.Direction;
import org.cloudburstmc.api.util.data.BlockColor;
import org.cloudburstmc.server.item.food.Food;
import org.cloudburstmc.server.level.CloudLevel;
import org.cloudburstmc.server.player.CloudPlayer;
import org.cloudburstmc.server.registry.CloudItemRegistry;

public class BlockBehaviorCake extends BlockBehaviorTransparent {

    @Override
    public boolean canBeActivated(Block block) {
        return true;
    }


//    @Override
//    public float getMinX() {
//        return this.getX() + (1 + getMeta() * 2) / 16f;
//    }
//
//    @Override
//    public float getMinY() {
//        return this.getY();
//    }
//
//    @Override
//    public float getMinZ() {
//        return this.getZ() + 0.0625f;
//    }
//
//    @Override
//    public float getMaxX() {
//        return this.getX() - 0.0625f + 1;
//    }
//
//    @Override
//    public float getMaxY() {
//        return this.getY() + 0.5f;
//    }
//
//    @Override
//    public float getMaxZ() {
//        return this.getZ() - 0.0625f + 1;
//    }

    @Override
    public boolean place(ItemStack item, Block block, Block target, Direction face, Vector3f clickPos, Player player) {
        if (block.down().getState().getType() != BlockTypes.AIR) {
            placeBlock(block, item);

            return true;
        }
        return false;
    }

    @Override
    public int onUpdate(Block block, int type) {
        if (type == CloudLevel.BLOCK_UPDATE_NORMAL) {
            if (block.down().getState().getType() == BlockTypes.AIR) {
                removeBlock(block, true);

                return CloudLevel.BLOCK_UPDATE_NORMAL;
            }
        }

        return 0;
    }

    @Override
    public ItemStack[] getDrops(Block block, ItemStack hand) {
        return new ItemStack[0];
    }

    @Override
    public ItemStack toItem(Block block) {
        return CloudItemRegistry.get().getItem(ItemTypes.CAKE);
    }

    @Override
    public boolean onActivate(Block block, ItemStack item, Player p) {
        CloudPlayer player = (CloudPlayer) p;
        if (player != null && player.getFoodData().getLevel() < player.getFoodData().getMaxLevel()) {
            int counter = block.getState().ensureTrait(BlockTraits.BITE_COUNTER);

            if (counter < 6) {
                counter++;
            }

            if (counter >= 6) {
                removeBlock(block, true);
            } else {
                Food.getByRelative(CloudItemRegistry.get().getItem(block.getState())).eatenBy(player);
                block.set(block.getState().withTrait(BlockTraits.BITE_COUNTER, counter), true);
            }

            return true;
        }
        return false;
    }

    @Override
    public BlockColor getColor(Block block) {
        return BlockColor.AIR_BLOCK_COLOR;
    }

    public int getComparatorInputOverride(Block block) {
        return (7 - block.getState().ensureTrait(BlockTraits.BITE_COUNTER)) << 1;
    }


}
