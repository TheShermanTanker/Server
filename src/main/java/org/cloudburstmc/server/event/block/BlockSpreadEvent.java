package org.cloudburstmc.server.event.block;

import org.cloudburstmc.server.block.BlockState;
import org.cloudburstmc.server.event.Cancellable;
import org.cloudburstmc.server.event.HandlerList;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BlockSpreadEvent extends BlockFormEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final BlockState source;

    public BlockSpreadEvent(BlockState blockState, BlockState source, BlockState newState) {
        super(blockState, newState);
        this.source = source;
    }

    public BlockState getSource() {
        return source;
    }
}
