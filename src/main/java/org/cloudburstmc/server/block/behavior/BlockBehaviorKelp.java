package org.cloudburstmc.server.block.behavior;

import com.nukkitx.math.vector.Vector3f;
import org.cloudburstmc.api.block.Block;
import org.cloudburstmc.api.block.BlockCategory;
import org.cloudburstmc.api.block.BlockState;
import org.cloudburstmc.api.block.BlockTraits;
import org.cloudburstmc.api.event.block.BlockGrowEvent;
import org.cloudburstmc.api.item.ItemStack;
import org.cloudburstmc.api.item.ItemTypes;
import org.cloudburstmc.api.player.Player;
import org.cloudburstmc.api.util.Direction;
import org.cloudburstmc.api.util.data.DyeColor;
import org.cloudburstmc.server.CloudServer;
import org.cloudburstmc.server.level.CloudLevel;
import org.cloudburstmc.server.level.particle.BoneMealParticle;
import org.cloudburstmc.server.registry.CloudItemRegistry;

import java.util.concurrent.ThreadLocalRandom;

import static org.cloudburstmc.api.block.BlockTraits.FLUID_LEVEL;
import static org.cloudburstmc.api.block.BlockTraits.KELP_AGE;
import static org.cloudburstmc.api.block.BlockTypes.*;
import static org.cloudburstmc.api.util.Direction.DOWN;

public class BlockBehaviorKelp extends FloodableBlockBehavior {

    @Override
    public boolean place(ItemStack item, Block block, Block target, Direction face, Vector3f clickPos, Player player) {
        var state = block.getState();

        if (state.getType() != WATER && state.getType() != FLOWING_WATER) {
            return false;
        }

        int waterLevel = state.ensureTrait(FLUID_LEVEL);
        if (waterLevel != 0) {
            return false;
        }

        Block down = block.getSide(DOWN);
        BlockState downState = down.getState();
        if ((downState.getType() != KELP && !downState.getBehavior().isSolid(downState))
                || downState.getType() == MAGMA || downState.getType() == ICE || downState.getType() == SOUL_SAND) {
            return false;
        }

//        if (waterDamage == 8) { //TODO: check
//            block.getLevel().setBlock(block.getPosition(), 1, BlockRegistry.get().getBlock(FLOWING_WATER), true, false);
//        }

        if (downState.getType() == KELP && downState.ensureTrait(KELP_AGE) != 24) {
            block.set(downState.withTrait(KELP_AGE, 24), true, true);
        }
        BlockState newState = block.getState().withTrait(KELP_AGE, ThreadLocalRandom.current().nextInt(25));
        placeBlock(block, newState);
        return true;
    }

    @Override
    public int onUpdate(Block block, int type) {
        if (type == CloudLevel.BLOCK_UPDATE_NORMAL) {
            var liquid = block.getExtra();
            Integer waterDamage = liquid.ensureTrait(FLUID_LEVEL);

            if (waterDamage == null || waterDamage != 0 || liquid.ensureTrait(BlockTraits.IS_FLOWING)) {
                block.getLevel().useBreakOn(block.getPosition());
                return type;
            }

            Block down = block.getSide(DOWN);
            BlockState downState = down.getState();
            if ((downState.getType() != KELP && !downState.inCategory(BlockCategory.SOLID))
                    || downState.getType() == MAGMA || downState.getType() == ICE || downState.getType() == SOUL_SAND) {
                block.getLevel().useBreakOn(block.getPosition());
                return type;
            }

//            if (waterDamage == 8) { //TODO: check
//                block.getLevel().setBlock(block.getPosition(), 1, BlockRegistry.get().getBlock(FLOWING_WATER), true, false);
//            }
            return type;
        } else if (type == CloudLevel.BLOCK_UPDATE_RANDOM) {
            if (ThreadLocalRandom.current().nextInt(100) <= 14) {
                grow(block);
            }
            return type;
        }
        return super.onUpdate(block, type);
    }

    public boolean grow(Block block) {
        int age = block.getState().ensureTrait(KELP_AGE);
        if (age < 25) {
            var up = block.up();
            var upState = up.getState();
            if (upState.getType() == WATER || upState.getType() == FLOWING_WATER) {
                int fluidLevel = upState.ensureTrait(FLUID_LEVEL);

                if (fluidLevel != 0 || upState.ensureTrait(BlockTraits.IS_FLOWING)) {
                    return false;
                }

                BlockState grown = block.getState().incrementTrait(KELP_AGE);
                BlockGrowEvent ev = new BlockGrowEvent(block, grown);
                CloudServer.getInstance().getEventManager().fire(ev);
                if (!ev.isCancelled()) {
                    block.set(block.getState().withTrait(KELP_AGE, 25), true);

                    placeBlock(up, ev.getNewState());
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean onBreak(Block block, ItemStack item) {
        Block down = block.down();

        if (down.getState().getType() == KELP) {
            down.set(block.getState().withTrait(KELP_AGE, ThreadLocalRandom.current().nextInt(25)), true);
        }
        super.onBreak(block, item);
        return true;
    }

    @Override
    public boolean onActivate(Block block, ItemStack item, Player player) {
        if (item.getType() == ItemTypes.DYE && item.getMetadata(DyeColor.class) == DyeColor.WHITE) { //Bone Meal
            var level = block.getLevel();
            int x = block.getX();
            int z = block.getZ();
            for (int y = block.getY() + 1; y < 255; y++) {
                var above = level.getBlockState(x, y, z);
                var blockAbove = above.getType();
                if (blockAbove == KELP) {
                    continue;
                }

                if (blockAbove == WATER || blockAbove == FLOWING_WATER) {
                    int waterData = above.ensureTrait(FLUID_LEVEL);
                    if (waterData == 0 && !above.ensureTrait(BlockTraits.IS_FLOWING)) {
                        var highestKelp = level.getBlock(x, y - 1, z);
                        if (grow(highestKelp)) {
                            ((CloudLevel) level).addParticle(new BoneMealParticle(block.getPosition()));

                            if (player != null && !player.isCreative()) {
                                player.getInventory().decrementHandCount();
                            }
                            return false;
                        }
                    }
                }

                return false;
            }

            return true;
        }

        return false;
    }

    @Override
    public ItemStack toItem(Block block) {
        return CloudItemRegistry.get().getItem(ItemTypes.KELP);
    }


    @Override
    public boolean canBeActivated(Block block) {
        return true;
    }
}
