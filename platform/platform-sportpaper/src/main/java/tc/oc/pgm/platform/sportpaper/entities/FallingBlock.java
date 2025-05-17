package tc.oc.pgm.platform.sportpaper.entities;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import tc.oc.pgm.util.nms.packets.BlockEntity;

public record FallingBlock(Entity entity) implements BlockEntity {
    @Override
    public boolean isDisplayEntity() {
        return false;
    }

    @Override
    public void align(float pitch, float yaw, float scale) { }

    @Override
    public void setBlock(Material block) { }

    @Override
    public void setTeleportationDuration(int duration) { }
}
