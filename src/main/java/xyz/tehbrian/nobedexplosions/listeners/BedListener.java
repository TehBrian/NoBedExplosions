package xyz.tehbrian.nobedexplosions.listeners;

import com.google.inject.Inject;
import io.papermc.paper.event.player.PlayerBedFailEnterEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
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
      final WorldsConfig worldsConfig
  ) {
    this.worldsConfig = worldsConfig;
  }

  @EventHandler
  public void onBedEnter(final PlayerBedEnterEvent event) {
    final Player player = event.getPlayer();
    final WorldsConfig.World.@Nullable Bed bedConfig = this.getBedConfig(player);
    if (bedConfig == null) {
      return;
    }

    switch (bedConfig.mode()) {
      case ALLOW -> event.setUseBed(Event.Result.ALLOW);
      case DENY, EXPLODE -> event.setUseBed(Event.Result.DENY);
      case DEFAULT -> event.setUseBed(Event.Result.DEFAULT);
      default -> {
      }
    }

    Util.sendMessageOrIgnore(player, bedConfig.message());
  }

  @EventHandler
  public void onBedEnterFail(final PlayerBedFailEnterEvent event) {
    final WorldsConfig.World.@Nullable Bed bedConfig = this.getBedConfig(event.getPlayer());
    if (bedConfig == null) {
      return;
    }

    switch (bedConfig.mode()) {
      case DENY -> event.setWillExplode(false);
      case EXPLODE -> event.setWillExplode(true);
      default -> {
      }
    }
  }

  private WorldsConfig.World.@Nullable Bed getBedConfig(final Player player) {
    final WorldsConfig.World worldConfig = this.worldsConfig.worlds().get(player.getWorld().getName());
    if (worldConfig == null) {
      return null;
    }

    return worldConfig.bed();
  }

}
