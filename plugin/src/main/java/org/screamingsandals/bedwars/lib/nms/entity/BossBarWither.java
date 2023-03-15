package org.screamingsandals.bedwars.lib.nms.entity;

import org.bukkit.Location;
import org.bukkit.entity.Wither;
import org.screamingsandals.bedwars.lib.nms.accessors.EntityAccessor;
import org.screamingsandals.bedwars.lib.nms.accessors.EntityWitherAccessor;
import org.screamingsandals.bedwars.lib.nms.utils.ClassStorage;

public class BossBarWither extends FakeEntityNMS<Wither> {

    public BossBarWither(Location location) {
        super(construct(location));
        setInvisible(true);
        metadata(7, 0);
        metadata(8, (byte) 0);
        metadata(20, 890);
    }

    public static Object construct(Location location) {
        try {
            final Object nmsEntity = EntityWitherAccessor.getConstructor0()
                    .newInstance(ClassStorage.getHandle(location.getWorld()));
            ClassStorage.getMethod(EntityAccessor.getMethodSetLocation1()).invokeInstance(
                    nmsEntity,
                    location.getX(),
                    location.getY(),
                    location.getZ(),
                    location.getYaw(),
                    location.getPitch()
            );
            return nmsEntity;
        } catch (Throwable ignored) {
            return null;
        }
    }
}