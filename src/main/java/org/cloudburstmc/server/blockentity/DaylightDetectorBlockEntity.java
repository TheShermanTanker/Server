package org.cloudburstmc.server.blockentity;

import com.nukkitx.math.vector.Vector3i;
import org.cloudburstmc.api.block.BlockType;
import org.cloudburstmc.api.block.BlockTypes;
import org.cloudburstmc.api.blockentity.BlockEntityType;
import org.cloudburstmc.api.blockentity.DaylightDetector;
import org.cloudburstmc.api.level.chunk.Chunk;

public class DaylightDetectorBlockEntity extends BaseBlockEntity implements DaylightDetector {


    public DaylightDetectorBlockEntity(BlockEntityType<?> type, Chunk chunk, Vector3i position) {
        super(type, chunk, position);
    }

    @Override
    public boolean isValid() {
        BlockType blockId = getBlockState().getType();
        return blockId == BlockTypes.DAYLIGHT_DETECTOR || blockId == BlockTypes.DAYLIGHT_DETECTOR_INVERTED;
    }

}
