package dev.tehbrian.nobedexplosions;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.NamespacedKey;
import org.incendo.cloud.component.CommandComponent;
import org.incendo.cloud.paper.PaperCommandManager;
import com.google.inject.Inject;
import dev.tehbrian.nobedexplosions.config.LangConfig;
import dev.tehbrian.nobedexplosions.config.WorldsConfig;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.NodePath;

import static org.incendo.cloud.bukkit.parser.NamespacedKeyParser.namespacedKeyParser;
import static org.incendo.cloud.description.Description.description;
import static org.incendo.cloud.suggestion.SuggestionProvider.blockingStrings;

public final class MainCommand {

  private final NoBedExplosions noBedExplosions;
  private final LangConfig langConfig;
  private final WorldsConfig worldsConfig;

  @Inject
  public MainCommand(
      final NoBedExplosions noBedExplosions,
      final LangConfig langConfig,
      final WorldsConfig worldsConfig
  ) {
    this.noBedExplosions = noBedExplosions;
    this.langConfig = langConfig;
    this.worldsConfig = worldsConfig;
  }

  public void register(final PaperCommandManager<CommandSourceStack> commandManager) {
    final var main = commandManager.commandBuilder("nbe")
        .commandDescription(description("The main command for NBE."))
        .handler(c -> c.sender().getSender().sendMessage(this.langConfig.c(
                NodePath.path("nbe"),
                Placeholder.unparsed("version", this.noBedExplosions.getPluginMeta().getVersion())
            ))
        );

    final var reload = main.literal("reload", description("Reloads the plugin's config."))
        .permission(Permission.RELOAD)
        .handler(c -> {
          if (this.noBedExplosions.loadConfiguration()) {
            c.sender().getSender().sendMessage(this.langConfig.c(NodePath.path("nbe-reload", "successful")));
          } else {
            c.sender().getSender().sendMessage(this.langConfig.c(NodePath.path("nbe-reload", "unsuccessful")));
          }
        });

    final var info = main.literal("info", description("Shows info for a world."))
        .permission(Permission.INFO)
        .argument(CommandComponent
            .builder("world", namespacedKeyParser())
            .suggestionProvider(blockingStrings((_, _) -> this.worldsConfig
                .worlds()
                .keySet()
                .stream()
                .map(NamespacedKey::toString)
                .toList()))
            .optional()
        )
        .handler(c -> {
          final CommandSender sender = c.sender().getSender();

          final NamespacedKey worldKey;
          if (sender instanceof final Player player) {
            worldKey = c.<NamespacedKey>optional("world").orElse(player.getWorld().getKey());
          } else {
            // If there somehow isn't a single world loaded, they deserve an NSEE.
            worldKey = c.<NamespacedKey>optional("world").orElse(sender.getServer().getWorlds().getFirst().getKey());
          }

          final WorldsConfig.@Nullable World worldConfig = this.worldsConfig.worlds().get(worldKey);

          if (worldConfig == null) {
            sender.sendMessage(this.langConfig.c(
                NodePath.path("nbe-info", "no-world-config"),
                Placeholder.unparsed("world", worldKey.toString())
            ));
            return;
          }

          sender.sendMessage(this.langConfig.c(
              NodePath.path("nbe-info", "header"),
              Placeholder.unparsed("world", worldKey.toString())
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
