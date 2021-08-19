package xyz.tehbrian.nobedexplosions.command;

import cloud.commandframework.bukkit.BukkitCommandManager;
import cloud.commandframework.bukkit.CloudBukkitCapabilities;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import com.google.inject.Inject;
import dev.tehbrian.tehlib.core.cloud.AbstractCloudService;
import org.apache.logging.log4j.Logger;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.function.Function;

public class CommandService extends AbstractCloudService<CommandSender, BukkitCommandManager<CommandSender>> {

    private final JavaPlugin javaPlugin;
    private final Logger logger;

    /**
     * @param javaPlugin JavaPlugin reference
     * @param logger     Logger reference
     */
    @Inject
    public CommandService(
            final @NonNull JavaPlugin javaPlugin,
            final @NonNull Logger logger
    ) {
        this.javaPlugin = javaPlugin;
        this.logger = logger;
    }

    /**
     * Instantiates {@link #commandManager}.
     *
     * @throws IllegalStateException if {@link #commandManager} is already instantiated
     */
    public void init() throws IllegalStateException {
        if (this.commandManager != null) {
            throw new IllegalStateException("The CommandManager is already instantiated!");
        }

        try {
            this.commandManager = new BukkitCommandManager<>(
                    this.javaPlugin,
                    CommandExecutionCoordinator.simpleCoordinator(),
                    Function.identity(),
                    Function.identity()
            );
        } catch (final Exception e) {
            this.logger.error("Failed to create the BukkitCommandManager.");
            this.logger.error("Something went very wrong. Get support from the plugin's author.");
            this.logger.error("Disabling plugin.");
            this.javaPlugin.getServer().getPluginManager().disablePlugin(this.javaPlugin);
        }

        if (this.commandManager.queryCapability(CloudBukkitCapabilities.BRIGADIER)) {
            try {
                this.commandManager.registerBrigadier();
                this.logger.info("Initialized Brigadier support!");
            } catch (final BukkitCommandManager.BrigadierFailureException e) {
                this.logger.warn("Failed to initialize Brigadier support: " + e.getMessage());
            }
        }
    }

}
