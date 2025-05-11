package tc.oc.pgm.projectile.path;

import org.bukkit.util.Vector;

public class ParabolicProjectilePath implements ProjectilePath {
  private final Vector initialVelocity;
  private final double step;
  private final double gravity;
  private final double drag = 0.99;

  public ParabolicProjectilePath(Vector initialVelocity, double step, double gravity) {
    this.initialVelocity = initialVelocity.clone();
    this.step = step;
    this.gravity = gravity;
  }

  @Override
  public Vector getPositionAtProgress(int progress) {
    Vector position = new Vector();
    Vector velocity = initialVelocity.clone();

    for (int i = 0; i < progress; i++) {
      velocity.multiply(drag);
      velocity.setY(velocity.getY() - gravity - Math.abs(velocity.getY() * 0.02));
      position.add(velocity.clone().multiply(step));
    }

    return position;
  }
}
