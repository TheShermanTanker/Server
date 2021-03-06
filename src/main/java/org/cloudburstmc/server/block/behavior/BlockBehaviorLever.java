package org.cloudburstmc.server.block.behavior;

import com.nukkitx.math.vector.Vector3f;
import org.cloudburstmc.api.block.*;
import org.cloudburstmc.api.event.block.BlockRedstoneEvent;
import org.cloudburstmc.api.item.ItemStack;
import org.cloudburstmc.api.player.Player;
import org.cloudburstmc.api.util.Direction;
import org.cloudburstmc.api.util.data.BlockColor;
import org.cloudburstmc.api.util.data.LeverDirection;
import org.cloudburstmc.server.level.CloudLevel;
import org.cloudburstmc.server.level.Sound;
import org.cloudburstmc.server.registry.CloudBlockRegistry;
import org.cloudburstmc.server.registry.CloudItemRegistry;

public class BlockBehaviorLever extends FloodableBlockBehavior {

    @Override
    public boolean canBeActivated(Block block) {
        return true;
    }


    @Override
    public ItemStack toItem(Block block) {
        return CloudItemRegistry.get().getItem(block.getState().getType().getDefaultState());
    }

    @Override
    public ItemStack[] getDrops(Block block, ItemStack hand) {
        return new ItemStack[]{toItem(block)};
    }

    public boolean isPowerOn(BlockState state) {
        return state.ensureTrait(BlockTraits.IS_OPEN);
    }

    @Override
    public boolean onActivate(Block block, ItemStack item, Player player) {
        var level = (CloudLevel) block.getLevel();
        var state = block.getState();

        boolean powerOn = isPowerOn(state);
        level.getServer().getEventManager().fire(new BlockRedstoneEvent(block, powerOn ? 15 : 0, powerOn ? 0 : 15));


        block.set(state.toggleTrait(BlockTraits.IS_OPEN));
        level.addSound(block.getPosition(), Sound.RANDOM_CLICK, 0.8f, powerOn ? 0.5f : 0.58f);

        var face = state.ensureTrait(BlockTraits.LEVER_DIRECTION).getDirection();

        level.updateAroundRedstone(face.getOpposite().getOffset(block.getPosition()), powerOn ? null : face);
        return true;
    }

    @Override
    public int onUpdate(Block block, int type) {
        if (type == CloudLevel.BLOCK_UPDATE_NORMAL) {
            Direction direction = block.getState().ensureTrait(BlockTraits.LEVER_DIRECTION).getDirection().getOpposite();
            if (!block.getSide(direction).getState().inCategory(BlockCategory.SOLID)) {
                block.getLevel().useBreakOn(block.getPosition());
            }
        }
        return 0;
    }

    @Override
    public boolean place(ItemStack item, Block block, Block target, Direction face, Vector3f clickPos, Player player) {
        if (target.getState().getBehavior().isNormalBlock(target)) {
            return placeBlock(block, CloudBlockRegistry.get().getBlock(BlockTypes.LEVER)
                    .withTrait(
                            BlockTraits.LEVER_DIRECTION,
                            LeverDirection.forDirection(face, player.getHorizontalDirection())
                    )
            );
        }
        return false;
    }

    @Override
    public boolean onBreak(Block block, ItemStack item) {
        super.onBreak(block, item);

        var state = block.getState();
        if (isPowerOn(state)) {
            Direction face = state.ensureTrait(BlockTraits.LEVER_DIRECTION).getDirection();
            block.getLevel().updateAround(face.getOpposite().getOffset(block.getPosition()));
        }
        return true;
    }

    @Override
    public int getWeakPower(Block block, Direction side) {
        return isPowerOn(block.getState()) ? 15 : 0;
    }

    public int getStrongPower(Block block, Direction side) {
        var state = block.getState();
        return !isPowerOn(state) ? 0 : state.ensureTrait(BlockTraits.LEVER_DIRECTION).getDirection() == side ? 15 : 0;
    }


    @Override
    public BlockColor getColor(Block block) {
        return BlockColor.AIR_BLOCK_COLOR;
    }


}
