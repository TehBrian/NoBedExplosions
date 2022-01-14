package xyz.tehbrian.nobedexplosions.command;

import cloud.commandframework.bukkit.BukkitCommandManager;
import cloud.commandframework.bukkit.BukkitCommandManager.BrigadierFailureException;
import cloud.commandframework.bukkit.CloudBukkitCapabilities;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import com.google.inject.Inject;
import dev.tehbrian.tehlib.core.cloud.AbstractCloudService;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import xyz.tehbrian.nobedexplosions.NoBedExplosions;

import java.util.function.Function;

public class CommandService extends AbstractCloudService<CommandSender, BukkitCommandManager<CommandSender>> {

    private final NoBedExplosions noBedExplosions;

    /**
     * @param noBedExplosions injected
     */
    @Inject
    public CommandService(
            final @NonNull NoBedExplosions noBedExplosions
    ) {
        this.noBedExplosions = noBedExplosions;
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

        this.commandManager = new BukkitCommandManager<>(
                this.noBedExplosions,
                CommandExecutionCoordinator.simpleCoordinator(),
                Function.identity(),
                Function.identity()
        );

        if (this.commandManager.queryCapability(CloudBukkitCapabilities.BRIGADIER)) {
            this.commandManager.registerBrigadier();
            this.noBedExplosions.getLog4JLogger().info("Successfully initialized Brigadier support.");
        }
    }

}
