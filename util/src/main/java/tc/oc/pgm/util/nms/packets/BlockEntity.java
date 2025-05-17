package tc.oc.pgm.util.nms.packets;

import org.bukkit.Material;
import org.bukkit.entity.Entity;

public interface BlockEntity {
    boolean isDisplayEntity();
    Entity entity();
    // Entity must have an actual rotation of 0 before invoking this
    // as this merely modifies the transformation matrix, otherwise it may appear offset
    void align(float pitch, float yaw, float scale);
    void setBlock(Material block);
    void setTeleportationDuration(int duration);
}