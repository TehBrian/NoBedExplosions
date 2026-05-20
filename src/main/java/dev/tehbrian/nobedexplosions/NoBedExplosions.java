package dev.tehbrian.nobedexplosions;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.PaperCommandManager;
import com.google.inject.Guice;
import com.google.inject.Injector;
import dev.tehbrian.nobedexplosions.config.LangConfig;
import dev.tehbrian.nobedexplosions.config.WorldsConfig;
import dev.tehbrian.nobedexplosions.inject.PluginModule;
import dev.tehbrian.nobedexplosions.inject.SingletonModule;
import dev.tehbrian.nobedexplosions.listener.AnchorListener;
import dev.tehbrian.nobedexplosions.listener.BedListener;
import dev.tehbrian.agna.configurate.Config;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.spongepowered.configurate.ConfigurateException;

import java.util.List;

import static dev.tehbrian.agna.paper.PluginUtils.disableSelf;
import static dev.tehbrian.agna.paper.PluginUtils.registerListeners;
import static dev.tehbrian.agna.paper.PluginUtils.saveResourceSilently;

/**
 * The main class for the NoBedExplosions plugin.
 */
public final class NoBedExplosions extends JavaPlugin {

  private @MonotonicNonNull PaperCommandManager<CommandSourceStack> commandManager;
  private @MonotonicNonNull Injector injector;

  @Override
  public void onEnable() {
    try {
      this.injector = Guice.createInjector(
          new PluginModule(this),
          new SingletonModule()
      );
    } catch (final Exception e) {
      this.getSLF4JLogger().error("Something went wrong while creating the Guice injector.");
      this.getSLF4JLogger().error("Disabling plugin.");
      disableSelf(this);
      this.getSLF4JLogger().error("Printing stack trace, please send this to the developers:", e);
      return;
    }

    if (!this.loadConfiguration()) {
      disableSelf(this);
      return;
    }
    if (!this.setupCommands()) {
      disableSelf(this);
      return;
    }

    this.setupListeners();
  }

  /**
   * Loads the plugin's configuration. If an exception is caught, logs the
   * error and returns false.
   *
   * @return whether it was successful
   */
  public boolean loadConfiguration() {
    saveResourceSilently(this, "lang.yml");
    saveResourceSilently(this, "config.yml");
    saveResourceSilently(this, "worlds.yml");

    final List<Config> configsToLoad = List.of(
        this.injector.getInstance(LangConfig.class),
        this.injector.getInstance(WorldsConfig.class)
    );

    for (final Config config : configsToLoad) {
      try {
        config.load();
      } catch (final ConfigurateException e) {
        this.getSLF4JLogger().error(
            "Exception caught during config load for {}",
            config.wrapper().path()
        );
        this.getSLF4JLogger().error("Please check your config.");
        this.getSLF4JLogger().error("Printing stack trace:", e);
        return false;
      }
    }

    this.getSLF4JLogger().info("Successfully loaded configuration.");
    return true;
  }

  /**
   * @return whether it was successful
   */
  private boolean setupCommands() {
    if (this.commandManager != null) {
      throw new IllegalStateException("The CommandManager is already instantiated.");
    }


    try {
      this.commandManager = PaperCommandManager
          .builder()
          .executionCoordinator(ExecutionCoordinator.simpleCoordinator())
          .buildOnEnable(this);
    } catch (final Exception e) {
      this.getSLF4JLogger().error("Failed to create the CommandManager.");
      this.getSLF4JLogger().error("Printing stack trace, please send this to the developers:", e);
      return false;
    }

    this.injector.getInstance(MainCommand.class).register(this.commandManager);

    return true;
  }

  private void setupListeners() {
    registerListeners(
        this,
        this.injector.getInstance(AnchorListener.class),
        this.injector.getInstance(BedListener.class)
    );
  }

}
