package xyz.tehbrian.nobedexplosions.config;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import dev.tehbrian.tehlib.core.configurate.AbstractConfig;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;
import xyz.tehbrian.nobedexplosions.NoBedExplosions;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Loads and holds values for {@code worlds.yml}.
 */
public final class WorldsConfig extends AbstractConfig<YamlConfigurateWrapper> {

    private final NoBedExplosions noBedExplosions;

    private final @NonNull Map<@NonNull String, @NonNull World> worlds = new HashMap<>();

    /**
     * @param noBedExplosions injected
     * @param logger          injected
     * @param dataFolder      injected
     */
    @Inject
    public WorldsConfig(
            final @NotNull Logger logger,
            final @NotNull @Named("dataFolder") Path dataFolder,
            final @NotNull NoBedExplosions noBedExplosions
    ) {
        super(logger, new YamlConfigurateWrapper(logger, dataFolder.resolve("worlds.yml"), YamlConfigurationLoader.builder()
                .path(dataFolder.resolve("worlds.yml"))
                .defaultOptions(opts -> opts.implicitInitialization(false))
                .build()));
        this.noBedExplosions = noBedExplosions;
    }

    @Override
    public void load() {
        this.worlds.clear();

        this.configurateWrapper.load();
        final CommentedConfigurationNode rootNode = this.configurateWrapper.get();
        final String fileName = this.configurateWrapper.filePath().getFileName().toString();

        for (final Map.Entry<Object, CommentedConfigurationNode> child : rootNode.node("worlds").childrenMap().entrySet()) {
            final @NonNull String worldName = child.getKey().toString();
            this.logger.info("Loading world configuration for {}..", worldName);
            final CommentedConfigurationNode worldNode = child.getValue();

            final @Nullable World world;
            try {
                world = worldNode.get(World.class);
            } catch (final SerializationException e) {
                this.logger.warn("Exception caught during configuration deserialization for world {}.", worldName);
                this.logger.warn("Skipping this world. Please check your {}.", fileName);
                this.logger.warn("Printing stack trace:", e);
                continue;
            }

            if (world == null) {
                this.logger.warn("Deserialized world configuration for {} was null!", worldName);
                this.logger.warn("Skipping this world. Please check your {} config file.", fileName);
                continue;
            }

            // Mode is annotated with @NonNull for API ease-of-use purposes,
            // however we must still validate it.
            //noinspection ConstantConditions
            if (world.anchor() != null && world.anchor().mode() == null) {
                this.logger.error("For world {}, anchor section exists but mode is null.", worldName);
                this.logger.warn("Skipping this world. Please check your {} config file.", fileName);
                continue;
            }

            //noinspection ConstantConditions
            if (world.bed() != null && world.bed().mode() == null) {
                this.logger.error("For world {}, bed section exists but mode is null.", worldName);
                this.logger.warn("Skipping this world. Please check your {} config file.", fileName);
                continue;
            }

            this.worlds.put(worldName, world);
            this.logger.info("Successfully added world configuration for {}.", worldName);
        }

        this.logger.info("Successfully loaded configuration file {}.", fileName);
    }

    /**
     * @return the worlds
     */
    public @NonNull Map<@NonNull String, @NonNull World> worlds() {
        return this.worlds;
    }

    @ConfigSerializable
    public static record World(@Nullable Bed bed,
                               @Nullable Anchor anchor) {

        @ConfigSerializable
        public static record Anchor(@NonNull Mode mode,
                                    @Nullable String message) {

            public enum Mode {
                DENY,
                DEFAULT,
            }

        }

        @ConfigSerializable
        public static record Bed(@NonNull Mode mode,
                                 @Nullable String message) {

            public enum Mode {
                ALLOW,
                DENY,
                DEFAULT,
            }

        }

    }

}
