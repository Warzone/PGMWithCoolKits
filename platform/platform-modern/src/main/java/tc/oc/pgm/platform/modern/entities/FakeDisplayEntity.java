package tc.oc.pgm.platform.modern.entities;

import org.bukkit.Location;
import org.bukkit.util.Vector;
import tc.oc.pgm.util.nms.packets.FakeEntity;
import tc.oc.pgm.util.nms.packets.Packet;

import static tc.oc.pgm.util.nms.Packets.ENTITIES;

public class FakeDisplayEntity extends FakeEntity.Impl {
    private boolean needsInitialRotationAdjusted;

    public FakeDisplayEntity(int entityId) {
        super(entityId);
    }

    @Override
    public boolean needsInitialRotationAdjusted() {
        return true;
    }

    @Override
    public Packet spawn(Location location, Vector velocity) {
        return ENTITIES.spawnRealAndValidBlockEntity(location, entityId());
    }

    public boolean isNeedsInitialRotationAdjusted() {
        return needsInitialRotationAdjusted;
    }

    public void setNeedsInitialRotationAdjusted(boolean needsInitialRotationAdjusted) {
        this.needsInitialRotationAdjusted = needsInitialRotationAdjusted;
    }
}