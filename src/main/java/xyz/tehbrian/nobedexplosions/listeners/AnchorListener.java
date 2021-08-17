package xyz.tehbrian.nobedexplosions.listeners;

import com.google.inject.Inject;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.RespawnAnchor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.checkerframework.checker.nullness.qual.NonNull;
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
     * Fired when a player interacts with an anchor.
     *
     * @param event the event
     */
    @EventHandler
    public void onAnchorInteract(final PlayerInteractEvent event) {
        if (!configConfig.enabled()) {
            return;
        }

        final Player player = event.getPlayer();
        final WorldsConfig.World worldConfig = worldsConfig.worlds().get(player.getWorld().getName());
        if (worldConfig == null) {
            return;
        }

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK
                || event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        final Block block = event.getClickedBlock();
        if (block == null
                || block.getType() != Material.RESPAWN_ANCHOR) {
            return;
        }

        final RespawnAnchor anchor = (RespawnAnchor) block.getBlockData();
        if (event.getMaterial() == Material.GLOWSTONE
                && anchor.getCharges() < anchor.getMaximumCharges()) {
            return;
        }

        switch (worldConfig.anchor().mode()) {
            case ALLOW -> {
                //event.setCancelled(false);
            }
            case DENY -> {
                event.setCancelled(true);
            }
        }

        final Component message = worldConfig.bed().message();
        audiences.player(player).sendMessage(message);
    }

    /**
     * When an anchor explodes.
     *
     * @param event the event
     */
    @EventHandler
    public void onAnchorExplode(final BlockExplodeEvent event) {
        if (!configConfig.enabled()) {
            return;
        }

        final Block block = event.getBlock();
        if (block.getType() != Material.RESPAWN_ANCHOR) {
            return;
        }

        final WorldsConfig.World worldConfig = worldsConfig.worlds().get(block.getWorld().getName());
        if (worldConfig == null) {
            return;
        }

        if (worldConfig.bed().disableAllExplosions()) {
            event.setCancelled(true);
        }
    }

}
