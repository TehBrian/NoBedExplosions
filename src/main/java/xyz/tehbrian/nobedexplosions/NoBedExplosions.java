package xyz.tehbrian.nobedexplosions;

import cloud.commandframework.bukkit.BukkitCommandManager;
import com.google.inject.Guice;
import com.google.inject.Injector;
import dev.tehbrian.tehlib.core.configurate.Config;
import dev.tehbrian.tehlib.paper.TehPlugin;
import io.papermc.lib.PaperLib;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurateException;
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

import java.util.List;

/**
 * The main class for the NoBedExplosions plugin.
 */
public final class NoBedExplosions extends TehPlugin {

    private @MonotonicNonNull Injector injector;

    @Override
    public void onEnable() {
        PaperLib.suggestPaper(this);

        try {
            this.injector = Guice.createInjector(
                    new PluginModule(this),
                    new ConfigModule(),
                    new CommandModule()
            );
        } catch (final Exception e) {
            this.getLog4JLogger().error("Something went wrong while creating the Guice injector.");
            this.getLog4JLogger().error("Disabling plugin.");
            this.disableSelf();
            this.getLog4JLogger().error("Printing stack trace, please send this to the developers:", e);
            return;
        }

        if (!this.loadConfiguration()) {
            this.disableSelf();
            return;
        }
        if (!this.setupCommands()) {
            this.disableSelf();
            return;
        }

        this.setupListeners();
    }

    @Override
    public void onDisable() {
        this.injector.getInstance(BukkitAudiences.class).close();
    }

    /**
     * Loads the plugin's configuration. If an exception is caught, logs the
     * error and returns false.
     *
     * @return whether it was successful
     */
    public boolean loadConfiguration() {
        this.saveResourceSilently("lang.yml");
        this.saveResourceSilently("config.yml");
        this.saveResourceSilently("worlds.yml");

        final List<Config> configsToLoad = List.of(
                this.injector.getInstance(LangConfig.class),
                this.injector.getInstance(ConfigConfig.class),
                this.injector.getInstance(WorldsConfig.class)
        );

        for (final Config config : configsToLoad) {
            try {
                config.load();
            } catch (final ConfigurateException e) {
                this.getLog4JLogger().error("Exception caught during config load for {}", config.configurateWrapper().filePath());
                this.getLog4JLogger().error("Please check your config.");
                this.getLog4JLogger().error("Printing stack trace:", e);
                return false;
            }
        }

        this.getLog4JLogger().info("Successfully loaded configuration.");
        return true;
    }

    /**
     * @return whether it was successful
     */
    private boolean setupCommands() {
        final @NonNull CommandService commandService = this.injector.getInstance(CommandService.class);
        try {
            commandService.init();
        } catch (final BukkitCommandManager.BrigadierFailureException e) {
            this.getLog4JLogger().error("Failed to register Brigadier support.");
            this.getLog4JLogger().error("Printing stack trace, please send this to the developers:", e);
            return false;
        } catch (final Exception e) {
            this.getLog4JLogger().error("Failed to create the CommandManager.");
            this.getLog4JLogger().error("Printing stack trace, please send this to the developers:", e);
            return false;
        }

        final @Nullable BukkitCommandManager<CommandSender> commandManager = commandService.get();
        if (commandManager == null) {
            this.getLog4JLogger().error("The CommandService was null after initialization!");
            return false;
        }

        this.injector.getInstance(MainCommand.class).register(commandManager);

        return true;
    }

    private void setupListeners() {
        this.registerListeners(
                this.injector.getInstance(AnchorListener.class),
                this.injector.getInstance(BedListener.class)
        );
    }

    /**
     * Copied from Paper.
     *
     * @return the log4j logger
     */
    public @NonNull Logger getLog4JLogger() {
        return LogManager.getLogger(this.getLogger().getName());
    }

}
