package xyz.tehbrian.nobedexplosions.config;

import com.google.inject.Inject;
import dev.tehbrian.tehlib.core.configurate.AbstractConfig;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Stores values in-memory for {@code worlds.yml}.
 */
public class WorldsConfig extends AbstractConfig<YamlConfigurateWrapper> {

    private final @NonNull Map<@NonNull String, @NonNull World> worlds = new HashMap<>();

    /**
     * @param logger the logger
     */
    @Inject
    public WorldsConfig(
            final @NonNull Logger logger
    ) {
        super(logger, new YamlConfigurateWrapper(logger, Path.of("worlds.yaml")));
    }

    // TODO: configurate object mapper thingy
    @Override
    public void load() {
        this.configurateWrapper.load();
        final CommentedConfigurationNode rootNode = this.configurateWrapper.get();

        for (final ConfigurationNode worldNode : rootNode.node("worlds").childrenList()) {
            final var name = Objects.requireNonNull(worldNode.key()).toString();

            final var bedNode = worldNode.node("bed");
            final World.Mode bedMode;
            try {
                bedMode = bedNode.node("mode").get(World.Mode.class);
            } catch (final SerializationException e) {
                e.printStackTrace();
                return;
            }

            final var anchorNode = worldNode.node("anchor");
            final World.Mode anchorMode;
            try {
                anchorMode = anchorNode.node("mode").get(World.Mode.class);
            } catch (final SerializationException e) {
                e.printStackTrace();
                return;
            }

            this.worlds.put(name, new World(
                    new World.Bed(
                            bedMode,
                            miniMessageElseNull(bedNode.node("message").getString()),
                            bedNode.node("set_spawn").getBoolean(),
                            bedNode.node("disable_all_explosions").getBoolean()
                    ),
                    new World.Anchor(
                            anchorMode,
                            miniMessageElseNull(anchorNode.node("message").getString()),
                            anchorNode.node("set_spawn").getBoolean(),
                            anchorNode.node("disable_all_explosions").getBoolean()
                    )
            ));
        }

        this.logger.info("Successfully loaded all values for world.yaml!");
    }

    /**
     * Gets the worlds.
     *
     * @return the worlds
     */
    public @NonNull Map<@NonNull String, @NonNull World> worlds() {
        return worlds;
    }

    /**
     * A small helper function that parses a string using MiniMessage or, if
     * the string is blank or null, returns null.
     *
     * @param string the message to parse
     * @return the component parsed by MiniMessage
     */
    private @Nullable Component miniMessageElseNull(final @Nullable String string) {
        if (string == null || string.isBlank()) {
            return null;
        }
        return MiniMessage.get().parse(string);
    }

    public static record World(Bed bed,
                               Anchor anchor) {

        public enum Mode {
            ALLOW,
            DENY,
        }

        public static record Anchor(Mode mode,
                                    Component message,
                                    boolean setSpawn,
                                    boolean disableAllExplosions) {

        }

        public static record Bed(Mode mode,
                                 Component message,
                                 boolean setSpawn,
                                 boolean disableAllExplosions) {

        }

    }

}
