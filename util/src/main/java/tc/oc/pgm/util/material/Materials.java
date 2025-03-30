package tc.oc.pgm.util.material;

import static org.bukkit.Material.*;
import static tc.oc.pgm.util.attribute.AttributeUtils.ATTRIBUTE_UTILS;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import tc.oc.pgm.util.bukkit.BukkitUtils;

public interface Materials {

  // Constants used across PGM for materials that changed name in newer versions
  Material STILL_WATER = parse("STATIONARY_WATER", "LEGACY_STATIONARY_WATER");
  Material STILL_LAVA = parse("STATIONARY_LAVA", "LEGACY_STATIONARY_LAVA");

  Material WOOL = parse("WOOL", "LEGACY_WOOL");
  Material STAINED_GLASS_PANE = parse("STAINED_GLASS_PANE", "LEGACY_STAINED_GLASS_PANE");
  Material SHORT_GRASS = parse("LONG_GRASS", "SHORT_GRASS");

  Material WEB = parse("WEB", "COBWEB");
  Material LILY_PAD = parse("WATER_LILY", "LILY_PAD");
  Material SIGN = parse("SIGN", "OAK_SIGN");
  Material SKULL = parse("SKULL_ITEM", "SKELETON_SKULL");
  Material PLAYER_HEAD = parse("SKULL_ITEM", "PLAYER_HEAD");
  Material WOOD_DOOR = parse("WOOD_DOOR", "OAK_DOOR");
  Material BOOK_AND_QUILL = parse("BOOK_AND_QUILL", "WRITABLE_BOOK");
  Material EYE_OF_ENDER = parse("EYE_OF_ENDER", "ENDER_EYE");
  Material FIREWORK = parse("FIREWORK", "FIREWORK_ROCKET");
  Material WATCH = parse("WATCH", "CLOCK");
  Material DYE = parse("INK_SACK", "BLACK_DYE");
  Material IRON_DOOR = parse("IRON_DOOR_BLOCK", "IRON_DOOR");
  Material RAW_FISH = parse("RAW_FISH", "COD");
  Material WORKBENCH = parse("WORKBENCH", "CRAFTING_TABLE");
  Material SOIL = parse("SOIL", "FARMLAND");
  Material MOVING_PISTON = parse("PISTON_MOVING_PIECE", "MOVING_PISTON");
  Material PISTON_HEAD = parse("PISTON_EXTENSION", "PISTON_HEAD");

  MaterialMatcher WEAPONS = MaterialMatcher.builder()
      .addAll(m -> m.name().endsWith("_SWORD")
          || m.name().endsWith("_AXE")
          || m.name().endsWith("_PICKAXE")
          || m.name().endsWith("_SHOVEL")
          || m.name().endsWith("_HOE"))
      .addAll(BOW, FLINT_AND_STEEL, SHEARS, STICK)
      .addNullable(Material.getMaterial("TRIDENT"))
      .addNullable(Material.getMaterial("MACE"))
      .build();

  MaterialMatcher DOOR_ITEMS = MaterialMatcher.builder()
      .addAll(m -> m.name().contains("_DOOR") && !m.isBlock())
      .build();

  // A set of item types which, when used to interact with the match environment by non-playing
  // users, can potentially cause client-server de-sync
  MaterialMatcher FORBIDDEN_OBSERVER_TYPES = MaterialMatcher.builder()
      .add(DOOR_ITEMS)
      .addAll(Materials.LILY_PAD, Material.BUCKET, Material.LAVA_BUCKET, Material.WATER_BUCKET)
      .build();

  MaterialMatcher SOLID_EXCLUSIONS = MaterialMatcher.builder()
      .add(parse("SIGN_POST", "LEGACY_SIGN_POST")) // on modern, it's just *_SIGN
      .addAll(m -> m.name().endsWith("_PLATE") || m.name().endsWith("_SIGN"))
      .build();

  static Material parse(String... names) {
    return BukkitUtils.parse(Material::valueOf, names);
  }

  /**
   * Replacement for Integer#parseInt, but with key differences that tailor it to material parsing.
   * Since text will be non-numbers, fail quickly to a -1 instead of a costly exception. Any string
   * not sized 1, 2 or 3 chars, can safely be assumed not an id, and go straight to -1.
   *
   * @param text The possible material id to parse
   * @return the material id as int, or -1 if not a numeric id
   */
  static int materialId(String text) {
    return switch (text.length()) {
      case 1 -> Character.digit(text.charAt(0), 10);
      case 2 -> {
        int a = Character.digit(text.charAt(0), 10);
        if (a == -1) yield -1;
        int b = Character.digit(text.charAt(1), 10);
        yield Math.min(a, b) == -1 ? -1 : ((a * 10) + b);
      }
      case 3 -> {
        int a = Character.digit(text.charAt(0), 10);
        if (a == -1) yield -1;
        int b = Character.digit(text.charAt(1), 10);
        int c = Character.digit(text.charAt(2), 10);
        yield Math.min(b, c) == -1 ? -1 : ((a * 10) + b) * 10 + c;
      }
      default -> -1;
    };
  }

  static boolean isWeapon(Material material) {
    return material != null && WEAPONS.matches(material);
  }

  static boolean isSolid(Material material) {
    return material != null && material.isSolid() && !SOLID_EXCLUSIONS.matches(material);
  }

  static boolean itemsSimilar(ItemStack first, ItemStack second, boolean skipDur) {
    if (first == second) {
      return true;
    }
    if (second == null
        || first == null
        || !first.getType().equals(second.getType())
        || (!skipDur && first.getDurability() != second.getDurability())) {
      return false;
    }
    final boolean hasMeta1 = first.hasItemMeta();
    final boolean hasMeta2 = second.hasItemMeta();
    if (!hasMeta1 && !hasMeta2) return true;

    ItemMeta meta1 = hasMeta1 ? first.getItemMeta() : null;
    ItemMeta meta2 = hasMeta2 ? second.getItemMeta() : null;

    if (hasMeta1 && hasMeta2 && meta1.hasAttributeModifiers() && meta2.hasAttributeModifiers()) {
      if (ATTRIBUTE_UTILS.attributesEqual(meta1, meta2)) {
        // If attributes match, strip them not to affect the comparison.
        // This is done because attributes have a random UUID in t hem that make them never equal
        meta1 = meta1.clone();
        meta2 = meta2.clone();
        ATTRIBUTE_UTILS.stripAttributes(meta1);
        ATTRIBUTE_UTILS.stripAttributes(meta2);
      } else {
        return false;
      }
    }

    return Bukkit.getItemFactory().equals(meta1, meta2);
  }

  static boolean isSolid(MaterialData material) {
    return isSolid(material.getItemType());
  }

  @SuppressWarnings("deprecation")
  static boolean isSolid(BlockState block) {
    return isSolid(block.getType());
  }

  static boolean isWater(Material material) {
    return material == Material.WATER || material == STILL_WATER;
  }

  static boolean isWater(MaterialData material) {
    return isWater(material.getItemType());
  }

  static boolean isWater(Location location) {
    return isWater(location.getBlock().getType());
  }

  @SuppressWarnings("deprecation")
  static boolean isWater(BlockState block) {
    return isWater(block.getType());
  }

  static boolean isLava(Material material) {
    return material == Material.LAVA || material == STILL_LAVA;
  }

  static boolean isLava(MaterialData material) {
    return isLava(material.getItemType());
  }

  static boolean isLava(Location location) {
    return isLava(location.getBlock().getType());
  }

  @SuppressWarnings("deprecation")
  static boolean isLava(BlockState block) {
    return isLava(block.getType());
  }

  static boolean isLiquid(Material material) {
    return isWater(material) || isLava(material);
  }

  static boolean isClimbable(Material material) {
    return material == Material.LADDER || material == Material.VINE;
  }

  static boolean isClimbable(Location location) {
    return isClimbable(location.getBlock().getType());
  }

  static boolean isBucket(ItemStack bucket) {
    return isBucket(bucket.getType());
  }

  static boolean isBucket(Material bucket) {
    return bucket == Material.BUCKET
        || bucket == Material.LAVA_BUCKET
        || bucket == Material.WATER_BUCKET
        || bucket == Material.MILK_BUCKET;
  }

  static int amount(@Nullable ItemStack stack) {
    return stack == null || stack.getType() == Material.AIR ? 0 : stack.getAmount();
  }

  @Contract(value = "null -> true", pure = true)
  static boolean isNothing(@Nullable ItemStack stack) {
    return amount(stack) == 0;
  }

  static Material materialInBucket(ItemStack bucket) {
    return materialInBucket(bucket.getType());
  }

  static Material materialInBucket(Material bucket) {
    return switch (bucket) {
      case BUCKET, MILK_BUCKET -> Material.AIR;
      case LAVA_BUCKET -> Material.LAVA;
      case WATER_BUCKET -> Material.WATER;
      default -> throw new IllegalArgumentException(bucket + " is not a bucket");
    };
  }
}
