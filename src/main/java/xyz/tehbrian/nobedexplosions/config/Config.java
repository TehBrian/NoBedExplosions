package xyz.tehbrian.nobedexplosions.config;

import com.google.inject.Inject;
import dev.tehbrian.tehlib.core.configurate.AbstractConfig;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.CommentedConfigurationNode;

import java.nio.file.Path;

/**
 * Stores values in-memory from {@code config.yml}.
 */
public class Config extends AbstractConfig<HoconConfigurateWrapper> {

    private boolean enabled;

    /**
     * @param logger the plugin logger
     */
    @Inject
    public Config(@NotNull final Logger logger) {
        super(logger, new HoconConfigurateWrapper(logger, Path.of("config.yml")));
    }

    @Override
    public void load() {
        this.configurateWrapper.load();
        final CommentedConfigurationNode rootNode = this.configurateWrapper.get();

        this.enabled = rootNode.node("enabled").getBoolean();

        this.logger.info("Successfully loaded all values for config.yml!");
    }

    /**
     * Gets whether the plugin is enabled.
     *
     * @return whether the plugin is enabled
     */
    public boolean enabled() {
        return this.enabled;
    }

}
