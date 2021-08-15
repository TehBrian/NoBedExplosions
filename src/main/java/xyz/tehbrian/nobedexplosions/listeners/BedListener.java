package xyz.tehbrian.nobedexplosions.listeners;

import com.google.inject.Inject;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import xyz.tehbrian.nobedexplosions.config.YamlLang;

/**
 * Listens for bed-related events.
 */
public class BedListener implements Listener {

    private final JavaPlugin javaPlugin;
    private final BukkitAudiences audiences;
    private final YamlLang lang;

    /**
     * @param javaPlugin JavaPlugin reference
     * @param audiences  BukkitAudiences reference
     * @param lang       Lang reference
     */
    @Inject
    public BedListener(
            @NonNull final JavaPlugin javaPlugin,
            @NonNull final BukkitAudiences audiences,
            @NonNull final YamlLang lang
    ) {
        this.javaPlugin = javaPlugin;
        this.audiences = audiences;
        this.lang = lang;
    }

    /**
     * When a player attempts to enter a bed.
     *
     * @param event the event
     */
    @EventHandler
    public void onBedEnter(final PlayerBedEnterEvent event) {
        final FileConfiguration config = this.javaPlugin.getConfig();
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
                if (event.getBedEnterResult() == PlayerBedEnterEvent.BedEnterResult.NOT_POSSIBLE_HERE) {
                    event.setUseBed(Event.Result.ALLOW);
                }
                break;
            case DENY:
                event.setUseBed(Event.Result.DENY);

                final String denyMsg = MessageUtils.color(worldSettings.getString("deny_msg"));
                if (denyMsg != null && !denyMsg.isEmpty()) {
                    player.sendMessage(denyMsg);
                }
                break;
        }
         */
    }

    /**
     * When a bed explodes.
     *
     * @param event the event
     */
    @EventHandler
    public void onBedExplode(final BlockExplodeEvent event) {
        final Block block = event.getBlock();
        if (!Tag.BEDS.getValues().contains(block.getType())) {
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
