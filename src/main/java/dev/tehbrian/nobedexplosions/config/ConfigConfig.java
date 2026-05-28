package dev.tehbrian.nobedexplosions.config;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import dev.tehbrian.agna.configurate.AbstractRawConfig;

import java.nio.file.Path;

public class ConfigConfig extends AbstractRawConfig<YamlConfigurateWrapper> {

  /**
   * @param dataFolder the data folder
   */
  @Inject
  public ConfigConfig(final @Named("dataFolder") Path dataFolder) {
    super(new YamlConfigurateWrapper(dataFolder.resolve("config.yml")));
  }

}
