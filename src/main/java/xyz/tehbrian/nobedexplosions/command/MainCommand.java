package xyz.tehbrian.nobedexplosions.command;

import cloud.commandframework.ArgumentDescription;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.bukkit.BukkitCommandManager;
import com.google.inject.Inject;
import dev.tehbrian.tehlib.core.cloud.AbstractCloudCommand;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.NodePath;
import xyz.tehbrian.nobedexplosions.Constants;
import xyz.tehbrian.nobedexplosions.NoBedExplosions;
import xyz.tehbrian.nobedexplosions.Util;
import xyz.tehbrian.nobedexplosions.config.ConfigConfig;
import xyz.tehbrian.nobedexplosions.config.LangConfig;
import xyz.tehbrian.nobedexplosions.config.WorldsConfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class MainCommand extends AbstractCloudCommand<CommandSender, BukkitCommandManager<CommandSender>> {

    private final NoBedExplosions noBedExplosions;
    private final BukkitAudiences audiences;
    private final LangConfig langConfig;
    private final WorldsConfig worldsConfig;
    private final ConfigConfig configConfig;

    /**
     * @param noBedExplosions NoBedExplosions reference
     * @param audiences       BukkitAudiences reference
     * @param langConfig      LangConfig reference
     * @param worldsConfig    WorldsConfig reference
     * @param configConfig    ConfigConfig reference
     */
    @Inject
    public MainCommand(
            @NonNull final NoBedExplosions noBedExplosions,
            @NonNull final BukkitAudiences audiences,
            @NonNull final LangConfig langConfig,
            @NonNull final WorldsConfig worldsConfig,
            @NonNull final ConfigConfig configConfig
    ) {
        this.noBedExplosions = noBedExplosions;
        this.audiences = audiences;
        this.langConfig = langConfig;
        this.worldsConfig = worldsConfig;
        this.configConfig = configConfig;
    }

    /**
     * Register the command.
     *
     * @param commandManager the command manager
     */
    @Override
    public void register(final @NonNull BukkitCommandManager<CommandSender> commandManager) {
        final var main = commandManager.commandBuilder("nbe", ArgumentDescription.of("The main command for NBE."))
                .handler(c -> Util.sendMessages(
                        this.audiences.sender(c.getSender()),
                        this.langConfig.cl(NodePath.path("nbe"), Map.of("version", this.noBedExplosions.getDescription().getVersion()))
                ));

        final var reload = main.literal("reload", ArgumentDescription.of("Reloads the plugin's config."))
                .permission(Constants.Permissions.RELOAD)
                .handler(c -> {
                    this.configConfig.load();
                    this.langConfig.load();
                    this.worldsConfig.load();
                    this.audiences.sender(c.getSender()).sendMessage(this.langConfig.c(NodePath.path("nbe-reload")));
                });

        final var info = main.literal("info", ArgumentDescription.of("Shows info for a world."))
                .permission(Constants.Permissions.INFO)
                .argument(StringArgument
                        .<CommandSender>newBuilder("world")
                        .single()
                        .withSuggestionsProvider((c, in) -> List.copyOf(this.worldsConfig.worlds().keySet()))
                        .build())
                // TODO: make this available to console if a world name is specified
                .senderType(Player.class)
                .handler(c -> {
                    final @NonNull Player sender = (Player) c.getSender();
                    final @NonNull Audience senderAudience = this.audiences.sender(sender);

                    final @NonNull String worldName = c.<String>getOptional("world").orElse(sender.getWorld().getName());
                    final WorldsConfig.@Nullable World worldConfig = this.worldsConfig.worlds().get(worldName);

                    if (worldConfig == null) {
                        senderAudience.sendMessage(this.langConfig.c(
                                NodePath.path("nbe-info", "no-world-config"),
                                Map.of("world", worldName)
                        ));
                        return;
                    }

                    senderAudience.sendMessage(this.langConfig.c(NodePath.path("nbe-info", "header"), Map.of("world", worldName)));

                    final WorldsConfig.World.Bed bed = worldConfig.bed();
                    if (bed != null) {
                        final Map<String, String> replacements = new HashMap<>();
                        replacements.put("bed_mode", bed.mode().name());
                        replacements.put("bed_message", bed.message() == null ? "" : bed.message());
                        Util.sendMessages(senderAudience, this.langConfig.cl(NodePath.path("nbe-info", "bed"), replacements));
                    }

                    final WorldsConfig.World.Anchor anchor = worldConfig.anchor();
                    if (anchor != null) {
                        final Map<String, String> replacements = new HashMap<>();
                        replacements.put("anchor_mode", anchor.mode().name());
                        replacements.put("anchor_message", anchor.message() == null ? "" : anchor.message());
                        Util.sendMessages(senderAudience, this.langConfig.cl(NodePath.path("nbe-info", "anchor"), replacements));
                    }
                });

        commandManager.command(main)
                .command(reload)
                .command(info);
    }

}
