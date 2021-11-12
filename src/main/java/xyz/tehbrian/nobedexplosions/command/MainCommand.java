package xyz.tehbrian.nobedexplosions.command;

import cloud.commandframework.ArgumentDescription;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.bukkit.BukkitCommandManager;
import cloud.commandframework.meta.CommandMeta;
import com.google.inject.Inject;
import dev.tehbrian.tehlib.core.cloud.AbstractCloudCommand;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.minimessage.template.TemplateResolver;
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
     * @param noBedExplosions injected
     * @param audiences       injected
     * @param langConfig      injected
     * @param worldsConfig    injected
     * @param configConfig    injected
     */
    @Inject
    public MainCommand(
            final @NonNull NoBedExplosions noBedExplosions,
            final @NonNull BukkitAudiences audiences,
            final @NonNull LangConfig langConfig,
            final @NonNull WorldsConfig worldsConfig,
            final @NonNull ConfigConfig configConfig
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
        final var main = commandManager.commandBuilder("nbe")
                .meta(CommandMeta.DESCRIPTION, "The main command for NBE.")
                .handler(c -> Util.sendMessages(
                        this.audiences.sender(c.getSender()),
                        this.langConfig.cl(
                                NodePath.path("nbe"),
                                TemplateResolver.pairs(Map.of("version", this.noBedExplosions.getDescription().getVersion()))
                        )
                ));

        final var reload = main.literal("reload", ArgumentDescription.of("Reloads the plugin's config."))
                .permission(Constants.Permissions.RELOAD)
                .handler(c -> {
                    if (this.noBedExplosions.loadConfiguration()) {
                        this.audiences.sender(c.getSender()).sendMessage(this.langConfig.c(NodePath.path("nbe-reload", "successful")));
                    } else {
                        this.audiences.sender(c.getSender()).sendMessage(this.langConfig.c(NodePath.path("nbe-reload", "unsuccessful")));
                    }
                });

        final var info = main.literal("info", ArgumentDescription.of("Shows info for a world."))
                .permission(Constants.Permissions.INFO)
                .argument(StringArgument
                        .<CommandSender>newBuilder("world")
                        .single()
                        .withSuggestionsProvider((c, in) -> List.copyOf(this.worldsConfig.worlds().keySet()))
                        .asOptional()
                        .build())
                .handler(c -> {
                    final @NonNull CommandSender sender = c.getSender();
                    final @NonNull Audience senderAudience = this.audiences.sender(sender);

                    final @NonNull String worldName;
                    if (sender instanceof Player player) {
                        worldName = c.<String>getOptional("world").orElse(player.getWorld().getName());
                    } else {
                        // I am aware that there's a chance of NPE here, but let's just hope to heck that people have at least *one* world.
                        worldName = c.<String>getOptional("world").orElse(sender.getServer().getWorlds().get(0).getName());
                    }

                    final WorldsConfig.@Nullable World worldConfig = this.worldsConfig.worlds().get(worldName);

                    if (worldConfig == null) {
                        senderAudience.sendMessage(this.langConfig.c(
                                NodePath.path("nbe-info", "no-world-config"),
                                TemplateResolver.pairs(Map.of("world", worldName))
                        ));
                        return;
                    }

                    senderAudience.sendMessage(this.langConfig.c(
                            NodePath.path("nbe-info", "header"),
                            TemplateResolver.pairs(Map.of("world", worldName))
                    ));

                    final WorldsConfig.World.Bed bed = worldConfig.bed();
                    if (bed != null) {
                        final Map<String, String> replacements = new HashMap<>();
                        replacements.put("bed_mode", bed.mode().name());
                        replacements.put("bed_message", bed.message() == null ? "" : bed.message());
                        Util.sendMessages(
                                senderAudience,
                                this.langConfig.cl(NodePath.path("nbe-info", "bed"), TemplateResolver.pairs(replacements))
                        );
                    }

                    final WorldsConfig.World.Anchor anchor = worldConfig.anchor();
                    if (anchor != null) {
                        final Map<String, String> replacements = new HashMap<>();
                        replacements.put("anchor_mode", anchor.mode().name());
                        replacements.put("anchor_message", anchor.message() == null ? "" : anchor.message());
                        Util.sendMessages(
                                senderAudience,
                                this.langConfig.cl(NodePath.path("nbe-info", "anchor"), TemplateResolver.pairs(replacements))
                        );
                    }
                });

        commandManager.command(main)
                .command(reload)
                .command(info);
    }

}
