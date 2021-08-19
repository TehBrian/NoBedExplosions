package xyz.tehbrian.nobedexplosions.inject;

import com.google.inject.AbstractModule;
import xyz.tehbrian.nobedexplosions.config.ConfigConfig;
import xyz.tehbrian.nobedexplosions.config.LangConfig;
import xyz.tehbrian.nobedexplosions.config.WorldsConfig;

/**
 * Guice module which provides bindings for the configs.
 */
public class ConfigModule extends AbstractModule {

    /**
     * Binds the configs as eager singletons.
     */
    @Override
    protected void configure() {
        this.bind(ConfigConfig.class).asEagerSingleton();
        this.bind(LangConfig.class).asEagerSingleton();
        this.bind(WorldsConfig.class).asEagerSingleton();
    }

}
