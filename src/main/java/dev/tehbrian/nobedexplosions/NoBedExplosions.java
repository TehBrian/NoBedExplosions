package dev.tehbrian.nobedexplosions;

import dev.tehbrian.agna.paper.UpdateChecker;
import dev.tehbrian.agna.paper.configurate.ConfigLoader;
import dev.tehbrian.agna.paper.configurate.ConfigLoader.Loadable;
import dev.tehbrian.nobedexplosions.config.ConfigConfig;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bstats.bukkit.Metrics;
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

  private static final int BSTATS_PLUGIN_ID = 31554;

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

    // initialize bStats.
    Metrics _ = new Metrics(this, BSTATS_PLUGIN_ID);

    new UpdateChecker(this, "nobedexplosions").checkForUpdates();

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
    return new ConfigLoader(this).load(List.of(
        Loadable.ofVersioned("config.yml", this.injector.getInstance(ConfigConfig.class), 1),
        Loadable.ofVersioned("lang.yml", this.injector.getInstance(LangConfig.class), 1),
        Loadable.ofVersioned("worlds.yml", this.injector.getInstance(WorldsConfig.class), 1)
    ));
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
