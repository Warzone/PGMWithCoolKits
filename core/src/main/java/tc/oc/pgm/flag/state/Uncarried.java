package tc.oc.pgm.flag.state;

import static net.kyori.adventure.text.Component.translatable;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Banner;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.Nullable;
import tc.oc.pgm.api.PGM;
import tc.oc.pgm.api.event.BlockTransformEvent;
import tc.oc.pgm.api.party.Party;
import tc.oc.pgm.api.player.MatchPlayer;
import tc.oc.pgm.flag.Flag;
import tc.oc.pgm.flag.FlagMatchModule;
import tc.oc.pgm.flag.Post;
import tc.oc.pgm.flag.event.FlagPickupEvent;
import tc.oc.pgm.hologram.Hologram;
import tc.oc.pgm.hologram.HologramMatchModule;
import tc.oc.pgm.util.block.BlockStates;
import tc.oc.pgm.util.bukkit.Sounds;
import tc.oc.pgm.util.material.Materials;

/** Base class for flag states in which the banner is placed on the ground somewhere as a block */
public abstract class Uncarried extends Spawned {

  protected final Location location;
  protected final BlockState oldBlock;
  protected final BlockState oldBase;
  protected final Hologram hologram;
  private @Nullable MatchPlayer pickingUp;

  public Uncarried(Flag flag, Post post, @Nullable Location location) {
    super(flag, post);
    if (location == null) location = flag.getReturnPoint(post);
    this.location = new Location(
        location.getWorld(),
        location.getBlockX() + 0.5,
        location.getBlockY(),
        location.getBlockZ() + 0.5,
        location.getYaw(),
        location.getPitch());

    Block block = this.location.getBlock();
    if (block.getState() instanceof Banner) {
      // Banner may already be here at match start
      this.oldBlock = BlockStates.cloneWithMaterial(block, Material.AIR);
    } else {
      this.oldBlock = block.getState();
    }
    this.oldBase = block.getRelative(BlockFace.DOWN).getState();
    this.hologram = flag.getMatch()
        .needModule(HologramMatchModule.class)
        .createHologram(this.location.clone().add(0, 1.7, 0), flag.getComponentName(), false);
  }

  @Override
  public Location getLocation() {
    return this.location;
  }

  protected void placeBanner() {
    if (!this.flag.canDropOn(oldBase)) {
      oldBase.getBlock().setType(Material.SEA_LANTERN, false);
    } else if (Materials.isWater(oldBase.getType())) {
      oldBase.getBlock().setType(Material.ICE, false);
    }

    if (!flag.getBannerData().placeStanding(this.location)) {
      PGM.get().getGameLogger().severe("Failed to place banner at " + this.location);
    }

    this.hologram.show();
  }

  protected void breakBanner() {
    this.hologram.hide();
    oldBase.update(true, false);
    oldBlock.update(true, false);
  }

  @Override
  public void enterState() {
    super.enterState();
    this.placeBanner();
  }

  @Override
  public void leaveState() {
    this.breakBanner();
    super.leaveState();
  }

  @Override
  public boolean isCarrying(MatchPlayer player) {
    // This allows CarryingFlagFilter to match and cancel the pickup before it actually happens
    return player == this.pickingUp || super.isCarrying(player);
  }

  @Override
  public boolean isCarrying(Party party) {
    return (this.pickingUp != null && party == this.pickingUp.getParty())
        || super.isCarrying(party);
  }

  protected boolean pickupFlag(MatchPlayer carrier) {
    try {
      this.pickingUp = carrier;
      FlagPickupEvent event = new FlagPickupEvent(this.flag, carrier, this.location);
      this.flag.getMatch().callEvent(event);
      if (event.isCancelled()) return false;
    } finally {
      this.pickingUp = null;
    }

    this.flag.playStatusSound(Sounds.FLAG_PICKUP_OWN, Sounds.FLAG_PICKUP);
    this.flag.touch(carrier.getParticipantState());

    this.flag.transition(new Carried(this.flag, this.post, carrier, this.location));

    return true;
  }

  protected boolean inPickupRange(Player player) {
    Location playerLoc = player.getLocation();
    Location flagLoc = this.getLocation();

    if (playerLoc.getY() < flagLoc.getY() + 2
        && (playerLoc.getY() >= flagLoc.getY() - (player.isOnGround() ? 1 : 0.7))) {
      double dx = playerLoc.getX() - flagLoc.getX();
      double dz = playerLoc.getZ() - flagLoc.getZ();

      if (dx * dx + dz * dz <= 1) {
        return true;
      }
    }

    return false;
  }

  protected boolean canPickup(MatchPlayer player) {
    if (this.pickingUp != null) return false; // Prevent infinite recursion

    for (Flag flag : this.flag.getMatch().getModule(FlagMatchModule.class).getFlags()) {
      if (flag.isCarrying(player)) return false;
    }

    return this.flag.canPickup(player, this.post);
  }

  @Override
  public void onEvent(PlayerMoveEvent event) {
    super.onEvent(event);
    MatchPlayer player = this.flag.getMatch().getPlayer(event.getPlayer());
    if (player == null || !player.canInteract() || player.getBukkit().isDead()) return;

    if (this.inPickupRange(player.getBukkit()) && this.canPickup(player)) {
      this.pickupFlag(player);
    }
  }

  @Override
  public void onEvent(BlockTransformEvent event) {
    super.onEvent(event);

    Block block = event.getOldState().getBlock();
    Block flagBlock = this.location.getBlock();

    if (block.equals(flagBlock) || block.equals(flagBlock.getRelative(BlockFace.UP))) {
      event.setCancelled(translatable("flag.cannotBreakFlag"));
    } else if (block.equals(flagBlock.getRelative(BlockFace.DOWN))) {
      event.setCancelled(translatable("flag.cannotBreakBlockUnder"));
    }
  }
}
