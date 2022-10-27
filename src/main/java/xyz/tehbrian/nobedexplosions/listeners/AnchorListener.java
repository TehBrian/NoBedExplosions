package xyz.tehbrian.nobedexplosions.listeners;

import com.google.inject.Inject;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.RespawnAnchor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.checkerframework.checker.nullness.qual.Nullable;
import xyz.tehbrian.nobedexplosions.config.WorldsConfig;
import xyz.tehbrian.nobedexplosions.util.Util;

/**
 * Modifies anchor functionality according to the plugin configuration
 * when a player interacts with an anchor.
 */
public final class AnchorListener implements Listener {

  private final WorldsConfig worldsConfig;

  @Inject
  public AnchorListener(
      final WorldsConfig worldsConfig
  ) {
    this.worldsConfig = worldsConfig;
  }

  @EventHandler
  public void onAnchorInteract(final PlayerInteractEvent event) {
    final Player player = event.getPlayer();
    final WorldsConfig.World.@Nullable Anchor anchorConfig = this.getAnchorConfig(player);
    if (anchorConfig == null) {
      return;
    }

    final Block block = event.getClickedBlock();
    if (event.getAction() != Action.RIGHT_CLICK_BLOCK
        || event.getHand() != EquipmentSlot.HAND
        || block == null
        || block.getType() != Material.RESPAWN_ANCHOR
        || !(block.getBlockData() instanceof RespawnAnchor anchor)) {
      return;
    }

    if (this.willExplode(anchor, event.getMaterial())) {
      switch (anchorConfig.mode()) {
        case DENY -> event.setCancelled(true);
        case DEFAULT -> event.setCancelled(false);
        default -> {
        }
      }

      Util.sendMessageOrIgnore(player, anchorConfig.message());
    }
  }

  private WorldsConfig.World.@Nullable Anchor getAnchorConfig(final Player player) {
    final WorldsConfig.World worldConfig = this.worldsConfig.worlds().get(player.getWorld().getName());
    if (worldConfig == null) {
      return null;
    }

    return worldConfig.anchor();
  }

  private boolean willExplode(final RespawnAnchor anchor, final Material heldMaterial) {
    // if the held block is glowstone and anchor has max charge
    // OR
    // if the held block is not glowstone and anchor has any charge at all
    return (heldMaterial == Material.GLOWSTONE && anchor.getCharges() >= anchor.getMaximumCharges())
        || (heldMaterial != Material.GLOWSTONE && anchor.getCharges() > 0);
  }

}
