package xyz.tehbrian.nobedexplosions.inject;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;
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
   * @return the plugin's SLF4J logger
   */
  @Provides
  public org.slf4j.@NonNull Logger provideSLF4JLogger() {
    return this.noBedExplosions.getSLF4JLogger();
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
