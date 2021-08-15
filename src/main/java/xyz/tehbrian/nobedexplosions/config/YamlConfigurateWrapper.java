package xyz.tehbrian.nobedexplosions.config;

import dev.tehbrian.tehlib.core.configurate.ConfigurateWrapper;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.nio.file.Path;

public class YamlConfigurateWrapper extends ConfigurateWrapper<YamlConfigurationLoader> {

    public YamlConfigurateWrapper(
            @NonNull final Logger logger,
            @NonNull final Path filePath
    ) {
        super(logger, filePath, YamlConfigurationLoader.builder()
                .path(filePath)
                .build());
    }

}
