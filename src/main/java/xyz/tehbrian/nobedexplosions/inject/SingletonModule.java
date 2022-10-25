package xyz.tehbrian.nobedexplosions.inject;

import com.google.inject.AbstractModule;
import xyz.tehbrian.nobedexplosions.command.CommandService;
import xyz.tehbrian.nobedexplosions.config.LangConfig;
import xyz.tehbrian.nobedexplosions.config.WorldsConfig;

public final class SingletonModule extends AbstractModule {

  @Override
  protected void configure() {
    this.bind(LangConfig.class).asEagerSingleton();
    this.bind(WorldsConfig.class).asEagerSingleton();

    this.bind(CommandService.class).asEagerSingleton();
  }

}
