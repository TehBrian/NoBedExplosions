package xyz.tehbrian.nobedexplosions.listeners;

import com.google.inject.Inject;
import io.papermc.paper.event.player.PlayerBedFailEnterEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import xyz.tehbrian.nobedexplosions.config.WorldsConfig;
import xyz.tehbrian.nobedexplosions.util.Util;

/**
 * Modifies bed functionality according to the plugin configuration
 * when a player attempts to enter a bed.
 */
public final class BedListener implements Listener {

    private final WorldsConfig worldsConfig;

    @Inject
    public BedListener(
            final @NonNull WorldsConfig worldsConfig
    ) {
        this.worldsConfig = worldsConfig;
    }

    @EventHandler
    public void onBedEnter(final PlayerBedEnterEvent event) {

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
                // we also have to cancel else the spawn will be set
                event.setCancelled(true);
            }
            case DEFAULT -> event.setUseBed(Event.Result.DEFAULT);
            default -> {
            }
        }

        Util.sendMessageOrIgnore(this.audiences.player(player), bedConfig.message());
    }

}
