package dev.tehbrian.nobedexplosions.inject;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import dev.tehbrian.nobedexplosions.NoBedExplosions;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.Path;

public final class PluginModule extends AbstractModule {

  private final NoBedExplosions noBedExplosions;

  public PluginModule(final NoBedExplosions noBedExplosions) {
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
  public org.slf4j.Logger provideSLF4JLogger() {
    return this.noBedExplosions.getSLF4JLogger();
  }

  /**
   * @return the plugin's data folder
   */
  @Provides
  @Named("dataFolder")
  public Path provideDataFolder() {
    return this.noBedExplosions.getDataFolder().toPath();
  }

}
