package xyz.tehbrian.nobedexplosions.listeners;

import com.google.inject.Inject;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.RespawnAnchor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.checkerframework.checker.nullness.qual.NonNull;
import xyz.tehbrian.nobedexplosions.Util;
import xyz.tehbrian.nobedexplosions.config.ConfigConfig;
import xyz.tehbrian.nobedexplosions.config.WorldsConfig;

/**
 * Listens for anchor-related events.
 */
public class AnchorListener implements Listener {

    private final BukkitAudiences audiences;
    private final ConfigConfig configConfig;
    private final WorldsConfig worldsConfig;

    /**
     * @param audiences    BukkitAudiences reference
     * @param configConfig Config reference
     * @param worldsConfig Worlds reference
     */
    @Inject
    public AnchorListener(
            @NonNull final BukkitAudiences audiences,
            @NonNull final ConfigConfig configConfig,
            @NonNull final WorldsConfig worldsConfig
    ) {
        this.audiences = audiences;
        this.configConfig = configConfig;
        this.worldsConfig = worldsConfig;
    }

    /**
     * Modifies anchor functionality according to the plugin configuration
     * when a player interacts with an anchor.
     *
     * @param event the event
     */
    @EventHandler
    public void onAnchorInteract(final PlayerInteractEvent event) {
        if (!this.configConfig.enabled()) {
            return;
        }

        final Player player = event.getPlayer();
        final WorldsConfig.World worldConfig = this.worldsConfig.worlds().get(player.getWorld().getName());
        if (worldConfig == null) {
            return;
        }

        final WorldsConfig.World.Anchor anchorConfig = worldConfig.anchor();
        if (anchorConfig == null) {
            return;
        }

        final Block block = event.getClickedBlock();
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK
                || event.getHand() != EquipmentSlot.HAND
                || block == null
                || block.getType() != Material.RESPAWN_ANCHOR) {
            return;
        }

        final RespawnAnchor anchor = (RespawnAnchor) block.getBlockData();

        // If the held block is glowstone and at anchor is at max charge
        // OR if the held block is not glowstone and there is any charge at all.
        if ((event.getMaterial() == Material.GLOWSTONE && anchor.getCharges() >= anchor.getMaximumCharges())
                || (event.getMaterial() != Material.GLOWSTONE && anchor.getCharges() > 0)) {
            switch (anchorConfig.mode()) {
                case DENY -> event.setCancelled(true);
                case DEFAULT -> event.setCancelled(false);
                default -> {
                }
            }

            Util.sendMessageOrIgnore(this.audiences.player(player), anchorConfig.message());
        }
    }

}
