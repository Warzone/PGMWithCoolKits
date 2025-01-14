package tc.oc.pgm.util.nms;

import java.util.List;
import java.util.UUID;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;
import tc.oc.pgm.util.material.BlockMaterialData;
import tc.oc.pgm.util.platform.Platform;
import tc.oc.pgm.util.skin.Skin;

public interface NMSHacks {
  NMSHacks NMS_HACKS = Platform.get(NMSHacks.class);

  void skipFireworksLaunch(Firework firework);

  boolean isCraftItemArrowEntity(PlayerPickupItemEvent item);

  void freezeEntity(Entity entity);

  void setFireballDirection(Fireball entity, Vector direction);

  long getMonotonicTime(World world);

  void resumeServer();

  Inventory createFakeInventory(Player viewer, Inventory realInventory);

  List<Block> getBlocks(Chunk bukkitChunk, Material material);

  void setSkullMetaOwner(SkullMeta meta, String name, UUID uuid, Skin skin);

  World createWorld(String worldName, World.Environment env, boolean terrain, long seed);

  boolean canMineBlock(BlockMaterialData blockMaterial, Player player);

  void resetDimension(World world);

  void cleanupWorld(World world);

  void cleanupPlayer(Player player);

  double getTPS();

  void postToMainThread(Plugin plugin, boolean priority, Runnable task);

  int getMaxWorldSize(World world);

  int allocateEntityId();

  boolean isDisplayEntity(Entity entity);

  void setInterpolationDurationDisplayEntity(Entity entity, int duration);

  void setInterpolationDelayDisplayEntity(Entity entity, int delay);

  boolean isDisplayEntity(Class<? extends Entity> entity);

  void setBlockDisplayBlock(Entity entity, Material block);

  void centerBlockDisplayTransformationMatrix(Entity entity, float pitch, float yaw);
}
