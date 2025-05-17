package tc.oc.pgm.projectile;

import java.time.Duration;
import java.util.List;
import org.bukkit.entity.Entity;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.Nullable;
import tc.oc.pgm.api.filter.Filter;
import tc.oc.pgm.features.SelfIdentifyingFeatureDefinition;
import tc.oc.pgm.util.material.BlockMaterialData;

public class ProjectileDefinition extends SelfIdentifyingFeatureDefinition {
  protected @Nullable String name;
  protected @Nullable Double damage;
  protected @Nullable Float power;
  protected double velocity;
  protected ClickAction clickAction;
  protected ProjectileEntity projectile;
  protected List<PotionEffect> potion;
  protected Filter destroyFilter;
  protected Duration coolDown;
  protected boolean throwable;
  protected boolean precise;
  protected BlockMaterialData blockMaterial;
  protected float scale;
  protected boolean solidBlockCollision;
  protected Duration maxTravelTime;

  public ProjectileDefinition(
      @Nullable String id,
      @Nullable String name,
      @Nullable Double damage,
      @Nullable Float power,
      double velocity,
      ClickAction clickAction,
      ProjectileEntity entity,
      List<PotionEffect> potion,
      Filter destroyFilter,
      Duration coolDown,
      boolean throwable,
      boolean precise,
      BlockMaterialData blockMaterial,
      float scale,
      boolean solidBlockCollision,
      Duration maxTravelTime) {
    super(id);
    this.name = name;
    this.damage = damage;
    this.power = power;
    this.velocity = velocity;
    this.clickAction = clickAction;
    this.projectile = entity;
    this.potion = potion;
    this.destroyFilter = destroyFilter;
    this.coolDown = coolDown;
    this.throwable = throwable;
    this.precise = precise;
    this.blockMaterial = blockMaterial;
    this.scale = scale;
    this.solidBlockCollision = solidBlockCollision;
    this.maxTravelTime = maxTravelTime;
  }

  public static sealed class ProjectileEntity {
    public static final class RealEntity extends ProjectileEntity {
      public final Class<? extends Entity> entityType;

      public RealEntity(Class<? extends Entity> entityType) {
        this.entityType = entityType;
      }
    }

    public static final class AbstractEntity extends ProjectileEntity {
      public final AbstractEntityType entityType;

      public AbstractEntity(AbstractEntityType entityType) {
        this.entityType = entityType;
      }
    }

    enum AbstractEntityType {
      BLOCK
    }
  }

  public @Nullable String getName() {
    return name;
  }
}
