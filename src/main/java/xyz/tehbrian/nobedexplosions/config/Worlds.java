package xyz.tehbrian.nobedexplosions.config;

import com.google.inject.Inject;
import dev.tehbrian.tehlib.core.configurate.AbstractConfig;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.configurate.CommentedConfigurationNode;

import java.nio.file.Path;

/**
 * Stores values in-memory for {@code worlds.yml}.
 */
public class Worlds extends AbstractConfig<YamlConfigurateWrapper> {

    /**
     * @param logger the logger
     */
    @Inject
    public Worlds(
            final @NonNull Logger logger
    ) {
        super(logger, new YamlConfigurateWrapper(logger, Path.of("worlds.yaml")));
    }

    @Override
    public void load() {
        this.configurateWrapper.load();
        final CommentedConfigurationNode rootNode = this.configurateWrapper.get();

        this.logger.info("Successfully loaded all values for world.yaml!");
    }

}
