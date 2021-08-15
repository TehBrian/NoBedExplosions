package xyz.tehbrian.nobedexplosions.command;

import cloud.commandframework.ArgumentDescription;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.bukkit.BukkitCommandManager;
import com.google.inject.Inject;
import dev.tehbrian.tehlib.core.cloud.AbstractCloudCommand;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.configurate.NodePath;
import xyz.tehbrian.nobedexplosions.Constants;
import xyz.tehbrian.nobedexplosions.config.YamlLang;

import java.util.Optional;

public final class MainCommand extends AbstractCloudCommand<CommandSender, BukkitCommandManager<CommandSender>> {

    private final JavaPlugin javaPlugin;
    private final BukkitAudiences audiences;
    private final YamlLang lang;

    /**
     * @param javaPlugin JavaPlugin reference
     * @param audiences  BukkitAudiences reference
     * @param lang       Lang reference
     */
    @Inject
    public MainCommand(
            @NonNull final JavaPlugin javaPlugin,
            @NonNull final BukkitAudiences audiences,
            @NonNull final YamlLang lang
    ) {
        this.javaPlugin = javaPlugin;
        this.audiences = audiences;
        this.lang = lang;
    }

    @Override
    public void register(final @NonNull BukkitCommandManager<CommandSender> commandManager) {
        final var main = commandManager.commandBuilder("nbe", ArgumentDescription.of("The main command for NBE."))
                .handler(c -> this.audiences.sender(c.getSender()).sendMessage(this.lang.c(NodePath.path("nbe"))));

        final var reload = main.literal("reload", ArgumentDescription.of("Reloads the plugin's config."))
                .permission(Constants.Permissions.RELOAD)
                .handler(c -> {
                    this.javaPlugin.reloadConfig();
                    this.audiences.sender(c.getSender()).sendMessage(this.lang.c(NodePath.path("nbe_reload")));
                });

        // TODO: make this available to console if a world name is specified
        final var info = main.literal("info", ArgumentDescription.of("Shows info for a world."))
                .permission(Constants.Permissions.INFO)
                .argument(StringArgument.optional("world"))
                .senderType(Player.class)
                .handler(c -> {
                    final Player sender = (Player) c.getSender();
                    final @NonNull Optional<String> worldName = c.getOptional("world");

                    final World world;
                    if (worldName.isPresent()) {
                        world = sender.getServer().getWorld(worldName.get());
                    } else {
                        world = sender.getWorld();
                    }

                    this.audiences.sender(sender).sendMessage(this.lang.c(NodePath.path("nbe_info")));
                });

        commandManager.command(main)
                .command(reload)
                .command(info);
    }

}
