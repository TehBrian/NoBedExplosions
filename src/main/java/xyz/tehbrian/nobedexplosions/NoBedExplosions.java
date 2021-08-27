package xyz.tehbrian.nobedexplosions;

import cloud.commandframework.bukkit.BukkitCommandManager;
import com.google.inject.Guice;
import com.google.inject.Injector;
import io.papermc.lib.PaperLib;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import xyz.tehbrian.nobedexplosions.command.CommandService;
import xyz.tehbrian.nobedexplosions.command.MainCommand;
import xyz.tehbrian.nobedexplosions.config.ConfigConfig;
import xyz.tehbrian.nobedexplosions.config.LangConfig;
import xyz.tehbrian.nobedexplosions.config.WorldsConfig;
import xyz.tehbrian.nobedexplosions.inject.CommandModule;
import xyz.tehbrian.nobedexplosions.inject.ConfigModule;
import xyz.tehbrian.nobedexplosions.inject.PluginModule;
import xyz.tehbrian.nobedexplosions.listeners.AnchorListener;
import xyz.tehbrian.nobedexplosions.listeners.BedListener;

import java.util.logging.Level;

/**
 * The main class for NoBedExplosions.
 */
public final class NoBedExplosions extends JavaPlugin {

    /**
     * The Guice injector.
     */
    private @MonotonicNonNull Injector injector;

    /**
     * Called when the plugin is enabled.
     */
    @Override
    public void onEnable() {
        PaperLib.suggestPaper(this);

        /* Guice */
        try {
            this.injector = Guice.createInjector(
                    new PluginModule(this),
                    new ConfigModule(),
                    new CommandModule()
            );
        } catch (final Exception e) {
            this.getLogger().severe("Something went wrong while creating the Guice injector.");
            this.getLogger().severe("Disabling plugin.");
            this.getLogger().severe("Printing stack trace, please send this to the developers:");
            this.getLogger().log(Level.SEVERE, e.getMessage(), e);
            this.disableSelf();
            return;
        }

        /* Config */
        this.saveResource("config.yml", false);
        this.saveResource("lang.yml", false);
        this.saveResource("worlds.yml", false);

        this.injector.getInstance(ConfigConfig.class).load();
        this.injector.getInstance(LangConfig.class).load();
        this.injector.getInstance(WorldsConfig.class).load();

        /* Listeners */
        registerListeners(
                this.injector.getInstance(AnchorListener.class),
                this.injector.getInstance(BedListener.class)
        );

        /* Commands */
        final @NonNull CommandService commandService = this.injector.getInstance(CommandService.class);
        commandService.init();

        final @Nullable BukkitCommandManager<CommandSender> commandManager = commandService.get();
        if (commandManager == null) {
            this.getLogger().severe("The CommandService was null after initialization!");
            this.getLogger().severe("Disabling plugin.");
            this.disableSelf();
            return;
        }

        this.injector.getInstance(MainCommand.class).register(commandManager);
    }

    /**
     * Called when the plugin is disabled.
     */
    @Override
    public void onDisable() {
        this.injector.getInstance(BukkitAudiences.class).close();
    }

    /**
     * Registers {@code listeners} as event handlers.
     *
     * @param listeners the {@code Listener}s to register
     */
    private void registerListeners(final Listener... listeners) {
        final PluginManager manager = getServer().getPluginManager();
        for (final Listener listener : listeners) {
            manager.registerEvents(listener, this);
        }
    }

    /**
     * Disables this plugin.
     */
    public void disableSelf() {
        this.getServer().getPluginManager().disablePlugin(this);
    }

}
