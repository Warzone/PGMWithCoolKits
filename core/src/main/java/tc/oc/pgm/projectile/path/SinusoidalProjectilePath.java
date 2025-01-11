package tc.oc.pgm.projectile.path;

import org.bukkit.util.Vector;

public class SinusoidalProjectilePath implements DifferentiableProjectilePath {
    private final Vector direction;
    private final Vector normal;
    private final int step;
    private final int oscillationStep;

    public SinusoidalProjectilePath(final Vector direction, final int step, final int oscillationStep) {
        this.direction = direction;
        this.step = step;
        this.oscillationStep = oscillationStep;

        // rotate around y-axis ?
        this.normal = new Vector(-1.0 * direction.getZ(), direction.getY(), direction.getX()).normalize();
    }

    @Override
    public Vector getNormalizedTangent(int progress) {
        // calculate tangent line using derivative
        // profit?
        return null;
    }


    @Override
    public Vector getPositionAtProgress(int progress) {
        final Vector linear = direction.clone().multiply(step * progress);
        final double wavelength = 2.0;
        linear.add(new Vector(0.0, Math.sin((step * progress) / wavelength), 0.0));
        return linear;
    }
}
