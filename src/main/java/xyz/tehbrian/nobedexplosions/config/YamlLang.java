package xyz.tehbrian.nobedexplosions.config;

import com.google.inject.Inject;
import dev.tehbrian.tehlib.core.configurate.Lang;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.nio.file.Path;

public class YamlLang extends Lang<YamlConfigurateWrapper> {

    /**
     * @param logger the logger
     */
    @Inject
    public YamlLang(
            final @NonNull Logger logger
    ) {
        super(logger, new YamlConfigurateWrapper(logger, Path.of("lang.yaml")));
    }

}
