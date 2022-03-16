package xyz.tehbrian.nobedexplosions.inject;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.apache.logging.log4j.Logger;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import xyz.tehbrian.nobedexplosions.NoBedExplosions;

import java.nio.file.Path;

public final class PluginModule extends AbstractModule {

    private final NoBedExplosions noBedExplosions;

    public PluginModule(final @NonNull NoBedExplosions noBedExplosions) {
        this.noBedExplosions = noBedExplosions;
    }

    @Override
    protected void configure() {
        this.bind(NoBedExplosions.class).toInstance(this.noBedExplosions);
        this.bind(JavaPlugin.class).toInstance(this.noBedExplosions);
    }

    /**
     * @return the plugin's Log4J logger
     */
    @Provides
    public @NonNull Logger provideLog4JLogger() {
        return this.noBedExplosions.getLog4JLogger();
    }

    /**
     * @return the instance of {@code BukkitAudiences}
     */
    @Provides
    @Singleton
    public @NonNull BukkitAudiences provideAudiences() {
        return BukkitAudiences.create(this.noBedExplosions);
    }

    /**
     * @return the plugin's data folder
     */
    @Provides
    @Named("dataFolder")
    public @NonNull Path provideDataFolder() {
        return this.noBedExplosions.getDataFolder().toPath();
    }

}
