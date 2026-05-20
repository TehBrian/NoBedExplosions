package dev.tehbrian.nobedexplosions.config;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import dev.tehbrian.agna.configurate.AbstractConfig;
import org.bukkit.NamespacedKey;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Loads and holds values for {@code worlds.yml}.
 */
public final class WorldsConfig extends AbstractConfig<YamlConfigurateWrapper> {

  private final Logger logger;

  private final Map<NamespacedKey, World> worlds = new HashMap<>();

  @Inject
  public WorldsConfig(final @Named("dataFolder") Path dataFolder, final Logger logger) {
    super(new YamlConfigurateWrapper(dataFolder.resolve("worlds.yml"), YamlConfigurationLoader.builder()
        .path(dataFolder.resolve("worlds.yml"))
        .defaultOptions(opts -> opts.implicitInitialization(false))
        .build()));
    this.logger = logger;
  }

  @Override
  public void load() throws ConfigurateException {
    this.wrapper.load();
    final CommentedConfigurationNode rootNode = this.wrapper.rootNode();
    final String fileName = this.wrapper.path().getFileName().toString();

    this.worlds.clear();

    for (final Map.Entry<Object, CommentedConfigurationNode> child : rootNode.node("worlds").childrenMap().entrySet()) {
      final NamespacedKey worldKey = NamespacedKey.fromString(child.getKey().toString());

      if (worldKey == null) {
        this.logger.warn("Could not parse world key from `{}`.", child.getKey());
        this.logger.warn("Skipping this world. Please check your `{}`.", fileName);
        continue;
      }

      final CommentedConfigurationNode worldNode = child.getValue();

      final @Nullable World world;
      try {
        world = worldNode.get(World.class);
      } catch (final SerializationException e) {
        this.logger.warn("Exception caught during deserialization for world `{}`.", worldKey);
        this.logger.warn("Skipping this world. Please check your `{}`.", fileName);
        this.logger.warn("Printing stack trace:", e);
        continue;
      }

      if (world == null) {
        this.logger.warn("Deserialized world configuration for `{}` was null.", worldKey);
        this.logger.warn("Skipping this world. Please check your `{}`.", fileName);
        continue;
      }

      //noinspection ConstantValue - NonNull for external use, but we must validate it here.
      if (world.anchor() != null && world.anchor().mode() == null) {
        this.logger.error("For world `{}`, anchor section exists but mode is null.", worldKey);
        this.logger.warn("Skipping this world. Please check your `{}`.", fileName);
        continue;
      }

      //noinspection ConstantValue - NonNull for external use, but we must validate it here.
      if (world.bed() != null && world.bed().mode() == null) {
        this.logger.error("For world `{}`, bed section exists but mode is null.", worldKey);
        this.logger.warn("Skipping this world. Please check your `{}`.", fileName);
        continue;
      }

      this.worlds.put(worldKey, world);
      this.logger.info("Successfully loaded world configuration for `{}`.", worldKey);
    }
  }

  /**
   * @return the worlds
   */
  public Map<NamespacedKey, World> worlds() {
    return this.worlds;
  }

  @ConfigSerializable
  public record World(@Nullable Bed bed,
                      @Nullable Anchor anchor) {

    @ConfigSerializable
    public record Anchor(Mode mode,
                         @Nullable String message) {

      public enum Mode {
        DENY,
        DEFAULT,
      }

    }

    @ConfigSerializable
    public record Bed(Mode mode,
                      @Nullable String message) {

      public enum Mode {
        ALLOW,
        DENY,
        DEFAULT,
        EXPLODE,
      }

    }

  }

}
