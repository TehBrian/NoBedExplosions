package xyz.tehbrian.nobedexplosions.command;

import cloud.commandframework.bukkit.BukkitCommandManager.BrigadierFailureException;
import cloud.commandframework.bukkit.CloudBukkitCapabilities;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.paper.PaperCommandManager;
import com.google.inject.Inject;
import dev.tehbrian.tehlib.paper.cloud.PaperCloudService;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import xyz.tehbrian.nobedexplosions.NoBedExplosions;

import java.util.function.Function;

public class CommandService extends PaperCloudService<CommandSender> {

    private final NoBedExplosions noBedExplosions;
    private final Logger logger;

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
     * @throws IllegalStateException     if {@link #commandManager} is already instantiated
     * @throws Exception                 if something goes wrong during instantiation
     * @throws BrigadierFailureException if Brigadier fails to be registered
     */
    public void init() throws Exception {
        if (this.commandManager != null) {
            throw new IllegalStateException("The CommandManager is already instantiated.");
        }

        this.commandManager = new PaperCommandManager<>(
                this.noBedExplosions,
                CommandExecutionCoordinator.simpleCoordinator(),
                Function.identity(),
                Function.identity()
        );

        if (this.commandManager.queryCapability(CloudBukkitCapabilities.BRIGADIER)) {
            this.commandManager.registerBrigadier();
            this.logger.info("Successfully initialized Brigadier support.");
        }
    }

}
