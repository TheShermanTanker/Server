package org.cloudburstmc.server.entity.hostile;

import org.cloudburstmc.api.entity.EntityType;
import org.cloudburstmc.api.entity.Smiteable;
import org.cloudburstmc.api.entity.hostile.Phantom;
import org.cloudburstmc.api.item.ItemStack;
import org.cloudburstmc.api.item.ItemTypes;
import org.cloudburstmc.api.level.Location;
import org.cloudburstmc.server.registry.CloudItemRegistry;

/**
 * Created by PetteriM1
 */
public class EntityPhantom extends EntityHostile implements Phantom, Smiteable {

    public EntityPhantom(EntityType<Phantom> type, Location location) {
        super(type, location);
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        this.setMaxHealth(20);
    }

    @Override
    public float getWidth() {
        return 0.9f;
    }

    @Override
    public float getHeight() {
        return 0.5f;
    }

    @Override
    public String getName() {
        return "Phantom";
    }

    @Override
    public ItemStack[] getDrops() {
        return new ItemStack[]{CloudItemRegistry.get().getItem(ItemTypes.PHANTOM_MEMBRANE)};
    }

    @Override
    public boolean isUndead() {
        return true;
    }

}
