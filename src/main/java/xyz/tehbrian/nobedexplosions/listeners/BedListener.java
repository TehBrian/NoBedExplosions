package xyz.tehbrian.nobedexplosions.listeners;

import com.google.inject.Inject;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.checkerframework.checker.nullness.qual.NonNull;
import xyz.tehbrian.nobedexplosions.config.ConfigConfig;
import xyz.tehbrian.nobedexplosions.config.WorldsConfig;

/**
 * Listens for bed-related events.
 */
public class BedListener implements Listener {

    private final BukkitAudiences audiences;
    private final ConfigConfig configConfig;
    private final WorldsConfig worldsConfig;

    /**
     * @param audiences    BukkitAudiences reference
     * @param configConfig Config reference
     * @param worldsConfig Worlds reference
     */
    @Inject
    public BedListener(
            @NonNull final BukkitAudiences audiences,
            @NonNull final ConfigConfig configConfig,
            @NonNull final WorldsConfig worldsConfig
    ) {
        this.audiences = audiences;
        this.configConfig = configConfig;
        this.worldsConfig = worldsConfig;
    }

    /**
     * When a player attempts to enter a bed.
     *
     * @param event the event
     */
    @EventHandler
    public void onBedEnter(final PlayerBedEnterEvent event) {
        if (!configConfig.enabled()) {
            return;
        }

        final Player player = event.getPlayer();
        final WorldsConfig.World worldConfig = worldsConfig.worlds().get(player.getWorld().getName());
        if (worldConfig == null) {
            return;
        }

        switch (worldConfig.bed().mode()) {
            case ALLOW -> {
                if (event.getBedEnterResult() == PlayerBedEnterEvent.BedEnterResult.NOT_POSSIBLE_HERE) {
                    event.setUseBed(Event.Result.ALLOW);
                }
            }
            case DENY -> event.setUseBed(Event.Result.DENY);
        }

        final Component message = worldConfig.bed().message();
        audiences.player(player).sendMessage(message);
    }

    /**
     * When a bed explodes.
     *
     * @param event the event
     */
    @EventHandler
    public void onBedExplode(final BlockExplodeEvent event) {
        if (!configConfig.enabled()) {
            return;
        }

        final Block block = event.getBlock();
        if (!Tag.BEDS.getValues().contains(block.getType())) {
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
