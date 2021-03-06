package org.cloudburstmc.server.block.behavior;

import com.nukkitx.math.vector.Vector3f;
import org.cloudburstmc.api.block.*;
import org.cloudburstmc.api.event.block.BlockFadeEvent;
import org.cloudburstmc.api.item.ItemStack;
import org.cloudburstmc.api.item.ItemTypes;
import org.cloudburstmc.api.player.Player;
import org.cloudburstmc.api.util.Direction;
import org.cloudburstmc.api.util.data.BlockColor;
import org.cloudburstmc.server.level.CloudLevel;
import org.cloudburstmc.server.registry.CloudBlockRegistry;
import org.cloudburstmc.server.registry.CloudItemRegistry;

public class BlockBehaviorSnowLayer extends BlockBehaviorFallable {

    @Override
    public boolean place(ItemStack item, Block block, Block target, Direction face, Vector3f clickPos, Player player) {
        BlockState down = block.down().getState();
        if (down.inCategory(BlockCategory.SOLID)) {
            placeBlock(block, CloudBlockRegistry.get().getBlock(BlockTypes.SNOW_LAYER));
            return true;
        }
        return false;
    }

    @Override
    public int onUpdate(Block block, int type) {
        super.onUpdate(block, type);
        if (type == CloudLevel.BLOCK_UPDATE_RANDOM) {
            if (((CloudLevel) block.getLevel()).getBlockLightAt(block.getX(), block.getY(), block.getZ()) >= 10) {
                BlockFadeEvent event = new BlockFadeEvent(block, BlockStates.AIR);
                block.getLevel().getServer().getEventManager().fire(event);
                if (!event.isCancelled()) {
                    block.set(event.getNewState());
                }
                return CloudLevel.BLOCK_UPDATE_NORMAL;
            }
        }
        return 0;
    }

    @Override
    public ItemStack toItem(Block block) {
        return CloudItemRegistry.get().getItem(ItemTypes.SNOWBALL);
    }

    @Override
    public BlockColor getColor(Block block) {
        return BlockColor.SNOW_BLOCK_COLOR;
    }

//    @Override //TODO: bounding box
//    protected AxisAlignedBB recalculateBoundingBox() {
//        return null;
//    }
}


