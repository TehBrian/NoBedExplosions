package xyz.tehbrian.nobedexplosions;

import cloud.commandframework.bukkit.BukkitCommandManager;
import com.google.inject.Guice;
import com.google.inject.Injector;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.NonNull;
import xyz.tehbrian.nobedexplosions.command.CommandService;
import xyz.tehbrian.nobedexplosions.command.MainCommand;
import xyz.tehbrian.nobedexplosions.inject.CommandModule;
import xyz.tehbrian.nobedexplosions.inject.PluginModule;
import xyz.tehbrian.nobedexplosions.listeners.AnchorListener;
import xyz.tehbrian.nobedexplosions.listeners.BedListener;

import java.util.Objects;

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
        this.injector = Guice.createInjector(
                new PluginModule(this),
                new CommandModule()
        );

        registerListeners(
                this.injector.getInstance(BedListener.class),
                this.injector.getInstance(AnchorListener.class)
        );

        final var commandService = this.injector.getInstance(CommandService.class);
        commandService.init();
        final @NonNull BukkitCommandManager<CommandSender> commandManager = Objects.requireNonNull(commandService.get());

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

}
