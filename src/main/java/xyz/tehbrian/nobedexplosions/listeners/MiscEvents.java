package xyz.tehbrian.nobedexplosions.listeners;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import xyz.tehbrian.nobedexplosions.NoBedExplosions;
import xyz.tehbrian.nobedexplosions.util.MessageUtils;

import java.util.Objects;

@SuppressWarnings("unused")
public class MiscEvents implements Listener {

    private final NoBedExplosions main;

    public MiscEvents(NoBedExplosions main) {
        this.main = main;
    }

    @EventHandler
    public void onBedEnter(PlayerBedEnterEvent event) {
        FileConfiguration config = main.getConfig();

        if (!config.getBoolean("enabled")) return;
        if (event.getBedEnterResult() != PlayerBedEnterEvent.BedEnterResult.NOT_POSSIBLE_HERE) return;

        if (config.getBoolean("allow_sleep")) {
            event.setUseBed(Event.Result.ALLOW);
        } else {
            event.setCancelled(true);

            String deniedMsg = Objects.requireNonNull(config.getString("denied_msg"));
            if (!deniedMsg.isEmpty()) {
                event.getPlayer().sendMessage(MessageUtils.color(deniedMsg));
            }
        }
    }
}
