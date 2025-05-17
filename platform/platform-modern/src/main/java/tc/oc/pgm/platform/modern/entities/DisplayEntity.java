package tc.oc.pgm.platform.modern.entities;

import org.bukkit.Material;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Entity;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import tc.oc.pgm.util.nms.packets.BlockEntity;

public record DisplayEntity(Entity entity) implements BlockEntity {
    @Override
    public boolean isDisplayEntity() {
        return true;
    }

    public void align(float pitch, float yaw, float scale) {
        final Matrix4f translation = new Matrix4f().translate(
                new Vector3f(
                        -0.5f * scale,
                        -0.5f * scale,
                        -0.5f * scale
                ));

        final Matrix4f rotationMatrix = new Matrix4f();
        final Quaternionf rotation = new Quaternionf();
        rotation.rotateLocalX((float) Math.toRadians(-1 * pitch));
        rotation.rotateLocalY((float) Math.toRadians(180 - yaw));
        rotation.get(rotationMatrix);

        final Matrix4f scaleMatrix = new Matrix4f().scale(scale);
        final Matrix4f transformationMatrix = rotationMatrix.mul(translation.mul(scaleMatrix));
        ((BlockDisplay) entity).setTransformationMatrix(
                transformationMatrix
        );
    }

    @Override
    public void setBlock(Material block) {
        ((BlockDisplay) entity).setBlock(block.createBlockData());
    }

    @Override
    public void setTeleportationDuration(int duration) {
        ((BlockDisplay) entity).setTeleportDuration(duration);
    }
}