package dev.tehbrian.nobedexplosions;

import com.google.inject.Guice;
import com.google.inject.Injector;
import dev.tehbrian.agna.paper.UpdateChecker;
import dev.tehbrian.agna.paper.configurate.ConfigLoader;
import dev.tehbrian.agna.paper.configurate.ConfigLoader.Loadable;
import dev.tehbrian.nobedexplosions.config.ConfigConfig;
import dev.tehbrian.nobedexplosions.config.LangConfig;
import dev.tehbrian.nobedexplosions.config.WorldsConfig;
import dev.tehbrian.nobedexplosions.inject.PluginModule;
import dev.tehbrian.nobedexplosions.inject.SingletonModule;
import dev.tehbrian.nobedexplosions.listener.AnchorListener;
import dev.tehbrian.nobedexplosions.listener.BedListener;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.incendo.cloud.paper.PaperCommandManager;
import org.incendo.cloud.paper.util.sender.Source;

import java.util.List;

import static dev.tehbrian.agna.paper.PluginUtils.disableSelf;
import static dev.tehbrian.agna.paper.PluginUtils.registerListeners;
import static org.incendo.cloud.execution.ExecutionCoordinator.simpleCoordinator;
import static org.incendo.cloud.paper.util.sender.PaperSimpleSenderMapper.simpleSenderMapper;

/**
 * The main class for the NoBedExplosions plugin.
 */
public final class NoBedExplosions extends JavaPlugin {

	private static final int BSTATS_PLUGIN_ID = 31554;

	private @MonotonicNonNull PaperCommandManager<Source> commandManager;
	private @MonotonicNonNull Injector injector;

	@Override
	public void onEnable() {
		try {
			this.injector = Guice.createInjector(
					new PluginModule(this),
					new SingletonModule()
			);
		} catch (final Exception e) {
			this.getSLF4JLogger().error("Something went wrong while creating the injector. Disabling plugin");
			disableSelf(this);
			this.getSLF4JLogger().error("Printing stack trace. Please send this to the developers", e);
			return;
		}

		if (!this.loadConfiguration()) {
			disableSelf(this);
			return;
		}

		if (!this.initCommands()) {
			disableSelf(this);
			return;
		}

		this.initListeners();

		// initialize bStats.
		Metrics _ = new Metrics(this, BSTATS_PLUGIN_ID);

		new UpdateChecker(this, "nobedexplosions").checkForUpdates();
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
	private boolean initCommands() {
		if (this.commandManager != null) {
			throw new IllegalStateException("The CommandManager is already instantiated.");
		}

		this.commandManager = PaperCommandManager
				.builder(simpleSenderMapper())
				.executionCoordinator(simpleCoordinator())
				.buildOnEnable(this);

		this.injector.getInstance(MainCommand.class).register(this.commandManager);

		return true;
	}

	private void initListeners() {
		registerListeners(
				this,
				this.injector.getInstance(AnchorListener.class),
				this.injector.getInstance(BedListener.class)
		);
	}

}
