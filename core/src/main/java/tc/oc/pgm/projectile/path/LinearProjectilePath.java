package tc.oc.pgm.projectile.path;

import org.bukkit.util.Vector;

public class LinearProjectilePath implements ProjectilePath {
    private final Vector direction;
    private final double step;

    public LinearProjectilePath(Vector direction, double step) {
        this.direction = direction;
        this.step = step;
    }

    @Override
    public Vector getPositionAtProgress(int progress) {
        return this.direction.clone().multiply(progress * step);
    }
}
