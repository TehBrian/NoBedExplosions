package xyz.tehbrian.nobedexplosions.config;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import dev.tehbrian.tehlib.core.configurate.AbstractConfig;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.CommentedConfigurationNode;

import java.nio.file.Path;

/**
 * Loads and holds values for {@code config.yml}.
 */
public final class ConfigConfig extends AbstractConfig<YamlConfigurateWrapper> {

    private boolean enabled;

    /**
     * @param logger     the logger
     * @param dataFolder the data folder
     */
    @Inject
    public ConfigConfig(
            final @NotNull Logger logger,
            final @NotNull @Named("dataFolder") Path dataFolder
    ) {
        super(logger, new YamlConfigurateWrapper(logger, dataFolder.resolve("config.yml")));
    }

    @Override
    public void load() {
        this.configurateWrapper.load();
        final CommentedConfigurationNode rootNode = this.configurateWrapper.get();

        this.enabled = rootNode.node("enabled").getBoolean();

        this.logger.info("Successfully loaded configuration file {}", this.configurateWrapper.filePath().getFileName().toString());
    }

    /**
     * @return whether the plugin functionality should be enabled
     */
    public boolean enabled() {
        return this.enabled;
    }

}
