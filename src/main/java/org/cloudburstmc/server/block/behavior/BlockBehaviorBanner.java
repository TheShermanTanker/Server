package org.cloudburstmc.server.block.behavior;

import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.nbt.NbtMap;
import com.nukkitx.nbt.NbtMapBuilder;
import org.cloudburstmc.api.block.Block;
import org.cloudburstmc.api.block.BlockState;
import org.cloudburstmc.api.block.BlockTraits;
import org.cloudburstmc.api.block.BlockTypes;
import org.cloudburstmc.api.blockentity.Banner;
import org.cloudburstmc.api.blockentity.BlockEntity;
import org.cloudburstmc.api.item.ItemIds;
import org.cloudburstmc.api.item.ItemStack;
import org.cloudburstmc.api.item.ItemTypes;
import org.cloudburstmc.api.player.Player;
import org.cloudburstmc.api.util.Direction;
import org.cloudburstmc.api.util.data.BlockColor;
import org.cloudburstmc.api.util.data.CardinalDirection;
import org.cloudburstmc.api.util.data.DyeColor;
import org.cloudburstmc.server.blockentity.BannerBlockEntity;
import org.cloudburstmc.server.item.CloudItemStack;
import org.cloudburstmc.server.item.CloudItemStackBuilder;
import org.cloudburstmc.server.item.ItemUtils;
import org.cloudburstmc.server.level.CloudLevel;
import org.cloudburstmc.server.level.chunk.CloudChunk;
import org.cloudburstmc.server.math.NukkitMath;
import org.cloudburstmc.server.registry.BlockEntityRegistry;
import org.cloudburstmc.server.registry.CloudBlockRegistry;
import org.cloudburstmc.server.registry.CloudItemRegistry;

import static org.cloudburstmc.api.block.BlockTypes.AIR;
import static org.cloudburstmc.api.block.BlockTypes.WALL_BANNER;
import static org.cloudburstmc.api.blockentity.BlockEntityTypes.BANNER;

public class BlockBehaviorBanner extends BlockBehaviorTransparent {

    @Override
    public boolean place(ItemStack item, Block block, Block target, Direction face, Vector3f clickPos, Player player) {
        if (face != Direction.DOWN) {
            BlockState banner;
            if (face == Direction.UP) {
                banner = CloudBlockRegistry.get().getBlock(BlockTypes.STANDING_BANNER)
                        .withTrait(
                                BlockTraits.CARDINAL_DIRECTION,
                                CardinalDirection.values()[NukkitMath.floorDouble(((player.getYaw() + 180) * 16 / 360) + 0.5) & 0x0f]
                        );
            } else {
                banner = CloudBlockRegistry.get().getBlock(WALL_BANNER)
                        .withTrait(BlockTraits.FACING_DIRECTION, face);
            }

            block.set(banner, true);

            NbtMap tag = ((CloudItemStack) item).getDataTag();
            ((BannerBlockEntity) BlockEntityRegistry.get().newEntity(BANNER, (CloudChunk) block.getChunk(), block.getPosition())).loadAdditionalData(tag);

            return true;
        }
        return false;
    }

    @Override
    public int onUpdate(Block block, int type) {
        if (type == CloudLevel.BLOCK_UPDATE_NORMAL) {
            if (block.down().getState().getType() == AIR) {
                block.getLevel().useBreakOn(block.getPosition());

                return CloudLevel.BLOCK_UPDATE_NORMAL;
            }
        }

        return 0;
    }

    @Override
    public ItemStack toItem(Block block) {
        BlockEntity blockEntity = block.getLevel().getBlockEntity(block.getPosition());
        var builder = new CloudItemStackBuilder();
        builder.itemType(ItemTypes.BANNER);
        if (blockEntity instanceof Banner) {
            BannerBlockEntity banner = (BannerBlockEntity) blockEntity;

            NbtMapBuilder tag = NbtMap.builder();
            banner.saveAdditionalData(tag);

            return ItemUtils.deserializeItem(ItemIds.BANNER, (short) banner.getBase().getDyeData(), 1, tag.build());
        } else {
            return CloudItemRegistry.get().getItem(ItemTypes.BANNER);
        }
    }

    @Override
    public BlockColor getColor(Block block) {
        return this.getDyeColor(block).getColor();
    }

    public DyeColor getDyeColor(Block block) {
        BlockEntity blockEntity = block.getLevel().getBlockEntity(block.getPosition());

        if (blockEntity instanceof Banner) {
            return ((Banner) blockEntity).getBase();
        }

        return DyeColor.WHITE;
    }


}
