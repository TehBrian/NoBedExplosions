package xyz.tehbrian.nobedexplosions.listeners;

import com.google.inject.Inject;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.checkerframework.checker.nullness.qual.NonNull;
import xyz.tehbrian.nobedexplosions.Util;
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
        if (!this.configConfig.enabled()) {
            return;
        }

        final Player player = event.getPlayer();
        final WorldsConfig.World worldConfig = this.worldsConfig.worlds().get(player.getWorld().getName());
        if (worldConfig == null) {
            return;
        }

        final WorldsConfig.World.Bed bedConfig = worldConfig.bed();
        if (bedConfig == null) {
            return;
        }

        switch (bedConfig.mode()) {
            case ALLOW -> event.setUseBed(Event.Result.ALLOW);
            case DENY -> {
                event.setUseBed(Event.Result.DENY);
                // We also have to cancel else the spawn will be set.
                event.setCancelled(true);
            }
            case DEFAULT -> event.setUseBed(Event.Result.DEFAULT);
            default -> {
            }
        }

        Util.sendMessageOrIgnore(this.audiences.player(player), bedConfig.message());
    }

}
