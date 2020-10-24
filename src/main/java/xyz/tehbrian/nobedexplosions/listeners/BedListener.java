package xyz.tehbrian.nobedexplosions.listeners;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import xyz.tehbrian.nobedexplosions.NoBedExplosions;
import xyz.tehbrian.nobedexplosions.util.MessageUtils;

import java.util.Locale;
import java.util.Objects;

public class BedListener implements Listener {

    private final NoBedExplosions main;

    public BedListener(NoBedExplosions main) {
        this.main = main;
    }

    @EventHandler
    public void onBedEnter(PlayerBedEnterEvent event) {
        FileConfiguration config = main.getConfig();
        if (!config.getBoolean("enabled")) return;

        Player player = event.getPlayer();
        ConfigurationSection worldSettings = config.getConfigurationSection("worlds." + player.getWorld().getName());
        if (worldSettings == null) return;

        Mode mode = Mode.valueOf(Objects.requireNonNull(worldSettings.getString("mode")).trim().toUpperCase(Locale.ENGLISH));
        switch (mode) {
            case ALLOW:
                if (event.getBedEnterResult() == PlayerBedEnterEvent.BedEnterResult.NOT_POSSIBLE_HERE) {
                    event.setUseBed(Event.Result.ALLOW);
                }
                break;
            case DENY:
                event.setUseBed(Event.Result.DENY);

                String denyMsg = MessageUtils.color(worldSettings.getString("deny_msg"));
                if (denyMsg != null && !denyMsg.isEmpty()) {
                    event.getPlayer().sendMessage(denyMsg);
                }
                break;
        }
    }

    private enum Mode {
        ALLOW,
        DENY,
    }
}
