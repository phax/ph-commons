package com.helger.config;

import java.nio.charset.StandardCharsets;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.io.resource.URLResource;
import com.helger.commons.lang.ClassLoaderHelper;
import com.helger.config.source.MultiConfigurationSourceValueProvider;
import com.helger.config.source.envvar.ConfigurationSourceEnvVar;
import com.helger.config.source.file.ConfigurationSourceJson;
import com.helger.config.source.sysprop.ConfigurationSourceSystemProperty;

@Immutable
public final class ConfigFactory
{
  private static final IConfig DEFAULT_INSTANCE;
  static
  {
    final MultiConfigurationSourceValueProvider aMCSVP = new MultiConfigurationSourceValueProvider ();
    // Prio 400
    aMCSVP.addConfigurationSource (new ConfigurationSourceSystemProperty ());
    // Prio 300
    aMCSVP.addConfigurationSource (new ConfigurationSourceEnvVar ());
    // TODO
    MultiConfigurationSourceValueProvider.createForClassPath (ClassLoaderHelper.getDefaultClassLoader (),
                                                              "application.json",
                                                              aURL -> new ConfigurationSourceJson (new URLResource (aURL),
                                                                                                   StandardCharsets.UTF_8));
    DEFAULT_INSTANCE = Config.create (aMCSVP);
  }

  private ConfigFactory ()
  {}

  @Nonnull
  public static IConfig getDefaultConfig ()
  {
    return DEFAULT_INSTANCE;
  }
}
