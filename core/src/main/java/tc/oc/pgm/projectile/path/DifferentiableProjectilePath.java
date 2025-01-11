package tc.oc.pgm.projectile.path;

import org.bukkit.util.Vector;

public interface DifferentiableProjectilePath extends ProjectilePath {
    Vector getNormalizedTangent(int progress);
}
