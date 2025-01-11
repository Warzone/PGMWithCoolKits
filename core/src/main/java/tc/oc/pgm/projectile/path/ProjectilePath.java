package tc.oc.pgm.projectile.path;

import org.bukkit.util.Vector;

public interface ProjectilePath {
    Vector getPositionAtProgress(int progress);
}
