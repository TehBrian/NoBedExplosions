package xyz.tehbrian.nobedexplosions.config;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import dev.tehbrian.tehlib.core.configurate.AbstractConfig;
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
import java.util.Objects;

/**
 * Loads and holds values for {@code worlds.yml}.
 */
public final class WorldsConfig extends AbstractConfig<YamlConfigurateWrapper> {

  private final Logger logger;

  private final Map<String, World> worlds = new HashMap<>();

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
    this.configurateWrapper.load();
    // will not be null as we called #load()
    final CommentedConfigurationNode rootNode = Objects.requireNonNull(this.configurateWrapper.get());
    final String fileName = this.configurateWrapper.filePath().getFileName().toString();

    this.worlds.clear();

    for (final Map.Entry<Object, CommentedConfigurationNode> child : rootNode.node("worlds").childrenMap().entrySet()) {
      final String worldName = child.getKey().toString();
      final CommentedConfigurationNode worldNode = child.getValue();

      final @Nullable World world;
      try {
        world = worldNode.get(World.class);
      } catch (final SerializationException e) {
        this.logger.warn("Exception caught during deserialization for world {}", worldName);
        this.logger.warn("Skipping this world. Please check your {}", fileName);
        this.logger.warn("Printing stack trace:", e);
        continue;
      }

      if (world == null) {
        this.logger.warn("Deserialized world configuration for {} was null.", worldName);
        this.logger.warn("Skipping this world. Please check your {}", fileName);
        continue;
      }

      // Mode is annotated with for API ease-of-use purposes,
      // however still must validate it.
      if (world.anchor() != null && world.anchor().mode() == null) {
        this.logger.error("For world {}, anchor section exists but mode is null.", worldName);
        this.logger.warn("Skipping this world. Please check your {}", fileName);
        continue;
      }

      if (world.bed() != null && world.bed().mode() == null) {
        this.logger.error("For world {}, bed section exists but mode is null.", worldName);
        this.logger.warn("Skipping this world. Please check your {}", fileName);
        continue;
      }

      this.worlds.put(worldName, world);
      this.logger.info("Successfully loaded world configuration for {}", worldName);
    }
  }

  /**
   * @return the worlds
   */
  public Map<String, World> worlds() {
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
