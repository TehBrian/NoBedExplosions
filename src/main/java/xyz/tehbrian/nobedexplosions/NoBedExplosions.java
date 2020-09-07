package xyz.tehbrian.nobedexplosions;

import org.bukkit.plugin.java.JavaPlugin;
import xyz.tehbrian.nobedexplosions.commands.ReloadCommand;
import xyz.tehbrian.nobedexplosions.listeners.BedListener;

public final class NoBedExplosions extends JavaPlugin {

    @Override
    public void onEnable() {
        getConfig().options().copyDefaults(true);
        getConfig().options().copyHeader(true);
        saveDefaultConfig();

        getCommand("reloadnbe").setExecutor(new ReloadCommand(this));

        getServer().getPluginManager().registerEvents(new BedListener(this), this);
    }
}

