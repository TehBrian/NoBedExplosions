package xyz.tehbrian.nobedexplosions.inject;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import xyz.tehbrian.nobedexplosions.NoBedExplosions;

/**
 * Guice module that provides bindings for the plugin's instances.
 */
public class PluginModule extends AbstractModule {

    private final NoBedExplosions noBedExplosions;

    /**
     * @param noBedExplosions NoBedExplosions reference
     */
    public PluginModule(final @NonNull NoBedExplosions noBedExplosions) {
        this.noBedExplosions = noBedExplosions;
    }

    @Override
    protected void configure() {
        bind(NoBedExplosions.class).toInstance(this.noBedExplosions);
        bind(JavaPlugin.class).toInstance(this.noBedExplosions);
    }

    /**
     * Provides the plugin's {@code Logger}.
     *
     * @return the plugin's {@code Logger}
     */
    @Provides
    public @NonNull Logger provideSLF4JLogger() {
        return LogManager.getLogger(this.noBedExplosions.getLogger().getName());
    }

    /**
     * Provides the instance of {@code BukkitAudiences}.
     *
     * @return the instance of {@code BukkitAudiences}
     */
    @Provides
    @Singleton
    public @NonNull BukkitAudiences provideAudiences() {
        return BukkitAudiences.create(this.noBedExplosions);
    }

}
