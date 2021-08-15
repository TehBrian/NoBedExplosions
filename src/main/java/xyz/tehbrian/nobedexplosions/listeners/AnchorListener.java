package xyz.tehbrian.nobedexplosions.listeners;

import com.google.inject.Inject;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.RespawnAnchor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import xyz.tehbrian.nobedexplosions.config.YamlLang;

/**
 * Listens for anchor-related events.
 */
public class AnchorListener implements Listener {

    private final JavaPlugin javaPlugin;
    private final BukkitAudiences audiences;
    private final YamlLang lang;

    /**
     * @param javaPlugin JavaPlugin reference
     * @param audiences  BukkitAudiences reference
     * @param lang       Lang reference
     */
    @Inject
    public AnchorListener(
            @NonNull final JavaPlugin javaPlugin,
            @NonNull final BukkitAudiences audiences,
            @NonNull final YamlLang lang
    ) {
        this.javaPlugin = javaPlugin;
        this.audiences = audiences;
        this.lang = lang;
    }

    /**
     * Fired when a player interacts with an anchor.
     *
     * @param event the event
     */
    @EventHandler
    public void onAnchorInteract(final PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }
        final Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }
        if (block.getType() != Material.RESPAWN_ANCHOR) {
            return;
        }
        final RespawnAnchor anchor = (RespawnAnchor) block.getBlockData();

        if (event.getMaterial() == Material.GLOWSTONE && anchor.getCharges() < anchor.getMaximumCharges()) {
            return;
        }

        final FileConfiguration config = javaPlugin.getConfig();
        if (!config.getBoolean("enabled")) {
            return;
        }

        final Player player = event.getPlayer();
        final ConfigurationSection worldSettings = config.getConfigurationSection("worlds." + player.getWorld().getName());
        if (worldSettings == null) {
            return;
        }

        /*
        final Mode mode = Mode.valueOf(Objects.requireNonNull(worldSettings.getString("mode")).trim().toUpperCase(Locale.ENGLISH));
        switch (mode) {
            case ALLOW:
                javaPlugin.getLogger().severe("ALLOW mode is currently not working, sorry about that. Will fix ASAP.");
                /*
                event.setCancelled(true);

                // Fake setting the spawn location.
                Location possibleLoc = block.getLocation();
                if (anchor.getCharges() > 0
                        && player.getBedSpawnLocation() != null
                        && !(player.getBedSpawnLocation().equals(possibleLoc))) {
                    player.setBedSpawnLocation(possibleLoc, true);
                    player.sendMessage("Respawn point set");
                    player.playSound(block.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_SET_SPAWN, SoundCategory.BLOCKS, 1, 1);
                }
                 *//*
                break;
            case DENY:
                event.setCancelled(true);

                final String denyMsg = MessageUtils.color(worldSettings.getString("deny_msg"));
                if (denyMsg != null && !denyMsg.isEmpty()) {
                    player.sendMessage(denyMsg);
                }
                break
                ;
                */
    }

    /**
     * When an anchor explodes.
     *
     * @param event the event
     */
    @EventHandler
    public void onAnchorExplode(final BlockExplodeEvent event) {
        final Block block = event.getBlock();
        if (block.getType() != Material.RESPAWN_ANCHOR) {
            return;
        }

        final FileConfiguration config = javaPlugin.getConfig();
        if (!config.getBoolean("enabled")) {
            return;
        }

        final ConfigurationSection worldSettings = config.getConfigurationSection("worlds." + block.getWorld().getName());
        if (worldSettings == null) {
            return;
        }

        if (worldSettings.getBoolean("disable_all_explosions")) {
            event.setCancelled(true);
        }
    }

}
