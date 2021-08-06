package xyz.tehbrian.nobedexplosions.config;

import dev.tehbrian.tehlib.core.configurate.ConfigurateWrapper;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

import java.nio.file.Path;

public class HoconConfigurateWrapper extends ConfigurateWrapper<HoconConfigurationLoader> {

    public HoconConfigurateWrapper(
            @NonNull final Logger logger,
            @NonNull final Path filePath
    ) {
        super(logger, filePath, HoconConfigurationLoader.builder()
                .path(filePath)
                .build());
    }

}
