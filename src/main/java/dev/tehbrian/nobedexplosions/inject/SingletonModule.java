package dev.tehbrian.nobedexplosions.inject;

import com.google.inject.AbstractModule;
import dev.tehbrian.nobedexplosions.config.LangConfig;
import dev.tehbrian.nobedexplosions.config.WorldsConfig;

public final class SingletonModule extends AbstractModule {

  @Override
  protected void configure() {
    this.bind(LangConfig.class).asEagerSingleton();
    this.bind(WorldsConfig.class).asEagerSingleton();
  }

}
