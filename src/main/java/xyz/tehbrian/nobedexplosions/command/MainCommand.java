package xyz.tehbrian.nobedexplosions.command;

import cloud.commandframework.ArgumentDescription;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.bukkit.BukkitCommandManager;
import cloud.commandframework.meta.CommandMeta;
import com.google.inject.Inject;
import dev.tehbrian.tehlib.core.cloud.AbstractCloudCommand;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.NodePath;
import xyz.tehbrian.nobedexplosions.NoBedExplosions;
import xyz.tehbrian.nobedexplosions.config.LangConfig;
import xyz.tehbrian.nobedexplosions.config.WorldsConfig;
import xyz.tehbrian.nobedexplosions.util.Permissions;

import java.util.List;

public final class MainCommand extends AbstractCloudCommand<CommandSender, BukkitCommandManager<CommandSender>> {

    private final NoBedExplosions noBedExplosions;
    private final LangConfig langConfig;
    private final WorldsConfig worldsConfig;

    @Inject
    public MainCommand(
            final @NonNull NoBedExplosions noBedExplosions,
            final @NonNull LangConfig langConfig,
            final @NonNull WorldsConfig worldsConfig
    ) {
        this.noBedExplosions = noBedExplosions;
        this.langConfig = langConfig;
        this.worldsConfig = worldsConfig;
    }

    @Override
    public void register(final @NonNull BukkitCommandManager<CommandSender> commandManager) {
        final var main = commandManager.commandBuilder("nbe")
                .meta(CommandMeta.DESCRIPTION, "The main command for NBE.")
                .handler(c -> c.getSender().sendMessage(this.langConfig.c(
                                NodePath.path("nbe"),
                                Placeholder.unparsed("version", this.noBedExplosions.getDescription().getVersion())
                        ))
                );

        final var reload = main.literal("reload", ArgumentDescription.of("Reloads the plugin's config."))
                .permission(Permissions.RELOAD)
                .handler(c -> {
                    if (this.noBedExplosions.loadConfiguration()) {
                        c.getSender().sendMessage(this.langConfig.c(NodePath.path("nbe-reload", "successful")));
                    } else {
                        c.getSender().sendMessage(this.langConfig.c(NodePath.path("nbe-reload", "unsuccessful")));
                    }
                });

        final var info = main.literal("info", ArgumentDescription.of("Shows info for a world."))
                .permission(Permissions.INFO)
                .argument(StringArgument
                        .<CommandSender>newBuilder("world")
                        .single()
                        .withSuggestionsProvider((c, in) -> List.copyOf(this.worldsConfig.worlds().keySet()))
                        .asOptional()
                        .build())
                .handler(c -> {
                    final @NonNull CommandSender sender = c.getSender();

                    final @NonNull String worldName;
                    if (sender instanceof Player player) {
                        worldName = c.<String>getOptional("world").orElse(player.getWorld().getName());
                    } else {
                        // I am aware that there's a chance of NPE here, but let's just hope to heck that people have at least *one* world.
                        worldName = c.<String>getOptional("world").orElse(sender.getServer().getWorlds().get(0).getName());
                    }

                    final WorldsConfig.@Nullable World worldConfig = this.worldsConfig.worlds().get(worldName);

                    if (worldConfig == null) {
                        sender.sendMessage(this.langConfig.c(
                                NodePath.path("nbe-info", "no-world-config"),
                                Placeholder.unparsed("world", worldName)
                        ));
                        return;
                    }

                    sender.sendMessage(this.langConfig.c(
                            NodePath.path("nbe-info", "header"),
                            Placeholder.unparsed("world", worldName)
                    ));

                    final WorldsConfig.World.Bed bed = worldConfig.bed();
                    if (bed != null) {
                        final var resolver = TagResolver.resolver(
                                Placeholder.unparsed("bed_mode", bed.mode().name()),
                                Placeholder.unparsed("bed_message", bed.message() == null ? "" : bed.message())
                        );

                        sender.sendMessage(this.langConfig.c(
                                NodePath.path("nbe-info", "bed"),
                                resolver
                        ));
                    }

                    final WorldsConfig.World.Anchor anchor = worldConfig.anchor();
                    if (anchor != null) {
                        final var resolver = TagResolver.resolver(
                                Placeholder.unparsed("anchor_mode", anchor.mode().name()),
                                Placeholder.unparsed("anchor_message", anchor.message() == null ? "" : anchor.message())
                        );

                        sender.sendMessage(this.langConfig.c(
                                NodePath.path("nbe-info", "anchor"),
                                resolver
                        ));
                    }
                });

        commandManager.command(main)
                .command(reload)
                .command(info);
    }

}
