package xyz.tehbrian.nobedexplosions.config;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import dev.tehbrian.tehlib.core.configurate.AbstractConfig;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;

import java.nio.file.Path;
import java.util.Objects;

/**
 * Loads and holds values for {@code config.yml}.
 */
public final class ConfigConfig extends AbstractConfig<YamlConfigurateWrapper> {

    private boolean enabled;

    /**
     * @param dataFolder the data folder
     */
    @Inject
    public ConfigConfig(final @NonNull @Named("dataFolder") Path dataFolder) {
        super(new YamlConfigurateWrapper(dataFolder.resolve("config.yml")));
    }

    @Override
    public void load() throws ConfigurateException {
        this.configurateWrapper.load();
        final @NonNull CommentedConfigurationNode rootNode = Objects.requireNonNull(this.configurateWrapper.get()); // will not be null as we called #load()
        this.enabled = rootNode.node("enabled").getBoolean(true);
    }

    /**
     * @return whether the plugin functionality should be enabled
     */
    public boolean enabled() {
        return this.enabled;
    }

}
