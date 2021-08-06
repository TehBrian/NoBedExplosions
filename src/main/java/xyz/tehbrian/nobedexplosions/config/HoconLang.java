package xyz.tehbrian.nobedexplosions.config;

import com.google.inject.Inject;
import dev.tehbrian.tehlib.core.configurate.Lang;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.nio.file.Path;

public class HoconLang extends Lang<HoconConfigurateWrapper> {

    /**
     * @param logger the logger
     */
    @Inject
    public HoconLang(
            final @NonNull Logger logger
    ) {
        super(logger, new HoconConfigurateWrapper(logger, Path.of("lang.conf")));
    }

}
