package xyz.tehbrian.nobedexplosions.command;

import cloud.commandframework.bukkit.BukkitCommandManager;
import cloud.commandframework.bukkit.CloudBukkitCapabilities;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import com.google.inject.Inject;
import dev.tehbrian.tehlib.core.cloud.AbstractCloudService;
import org.apache.logging.log4j.Logger;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import xyz.tehbrian.nobedexplosions.NoBedExplosions;

import java.util.function.Function;

public class CommandService extends AbstractCloudService<CommandSender, BukkitCommandManager<CommandSender>> {

    private final NoBedExplosions noBedExplosions;
    private final Logger logger;

    /**
     * @param noBedExplosions NoBedExplosions reference
     * @param logger          Logger reference
     */
    @Inject
    public CommandService(
            final @NonNull NoBedExplosions noBedExplosions,
            final @NonNull Logger logger
    ) {
        this.noBedExplosions = noBedExplosions;
        this.logger = logger;
    }

    /**
     * Instantiates {@link #commandManager}.
     *
     * @throws IllegalStateException if {@link #commandManager} is already instantiated
     */
    public void init() throws IllegalStateException {
        if (this.commandManager != null) {
            throw new IllegalStateException("The CommandManager is already instantiated.");
        }

        try {
            this.commandManager = new BukkitCommandManager<>(
                    this.noBedExplosions,
                    CommandExecutionCoordinator.simpleCoordinator(),
                    Function.identity(),
                    Function.identity()
            );
        } catch (final Exception e) {
            this.logger.error("Failed to create the CommandManager.");
            this.logger.error("Disabling plugin.");
            this.logger.error("Printing stack trace, please send this to the developers:", e);
            this.noBedExplosions.disableSelf();
            return;
        }

        if (this.commandManager.queryCapability(CloudBukkitCapabilities.BRIGADIER)) {
            try {
                this.commandManager.registerBrigadier();
                this.logger.info("Successfully initialized Brigadier support!");
            } catch (final BukkitCommandManager.BrigadierFailureException e) {
                this.logger.warn("Failed to initialize Brigadier support.");
                this.logger.warn("Printing stack trace, please send this to the developers:", e);
            }
        }
    }

}
