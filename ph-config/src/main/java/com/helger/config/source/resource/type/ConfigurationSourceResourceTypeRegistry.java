/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
 * philip[at]helger[dot]com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.helger.config.source.resource.type;

import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.Nonempty;
import com.helger.annotation.concurrent.GuardedBy;
import com.helger.annotation.concurrent.ThreadSafe;
import com.helger.base.concurrent.SimpleReadWriteLock;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.spi.ServiceLoaderHelper;
import com.helger.base.string.StringHelper;
import com.helger.collection.commons.CommonsHashMap;
import com.helger.collection.commons.ICommonsMap;
import com.helger.config.source.resource.AbstractConfigurationSourceResource;
import com.helger.io.resource.IReadableResource;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * Global registry mapping from a file extension to a factory to read these configuration sources.
 *
 * @author Philip Helger
 */
@ThreadSafe
public final class ConfigurationSourceResourceTypeRegistry
{
  private static final class SingletonHolder
  {
    private static final ConfigurationSourceResourceTypeRegistry INSTANCE = new ConfigurationSourceResourceTypeRegistry ();
  }

  private static final Logger LOGGER = LoggerFactory.getLogger (ConfigurationSourceResourceTypeRegistry.class);
  private static boolean s_bDefaultInstantiated = false;

  private final SimpleReadWriteLock m_aRWLock = new SimpleReadWriteLock ();
  @GuardedBy ("m_aRWLock")
  private final ICommonsMap <String, Function <IReadableResource, AbstractConfigurationSourceResource>> m_aMap = new CommonsHashMap <> ();

  private ConfigurationSourceResourceTypeRegistry ()
  {
    _reinitialize ();
  }

  public static boolean isInstantiated ()
  {
    return s_bDefaultInstantiated;
  }

  @Nonnull
  public static ConfigurationSourceResourceTypeRegistry getInstance ()
  {
    final ConfigurationSourceResourceTypeRegistry ret = SingletonHolder.INSTANCE;
    s_bDefaultInstantiated = true;
    return ret;
  }

  private void _reinitialize ()
  {
    m_aRWLock.writeLocked ( () -> {
      // Register all custom resource types.
      // Must be in writeLock to ensure no reads happen during initialization
      for (final IConfigurationSourceResourceTypeRegistrarSPI aSPI : ServiceLoaderHelper.getAllSPIImplementations (IConfigurationSourceResourceTypeRegistrarSPI.class))
      {
        if (LOGGER.isDebugEnabled ())
          LOGGER.debug ("Calling registerResourceType on " + aSPI.getClass ().getName ());
        aSPI.registerResourceType (this);
      }

      if (LOGGER.isDebugEnabled ())
        LOGGER.debug (m_aMap.size () + " resource types are registered");
    });
  }

  public void register (@Nonnull @Nonempty final String sFileExt,
                        @Nonnull final Function <IReadableResource, AbstractConfigurationSourceResource> aFactory)
  {
    ValueEnforcer.notEmpty (sFileExt, "FileExt");
    ValueEnforcer.isFalse ( () -> sFileExt.startsWith ("."),
                            () -> "The file extension '" + sFileExt + "' must not start with a dot");
    ValueEnforcer.notNull (aFactory, "Factory");

    m_aRWLock.writeLocked ( () -> {
      if (m_aMap.containsKey (sFileExt))
        throw new IllegalStateException ("The configuration source file extension '" +
                                         sFileExt +
                                         "' is already registered");
      m_aMap.put (sFileExt, aFactory);
    });
  }

  @Nullable
  public Function <IReadableResource, AbstractConfigurationSourceResource> getFactoryOfFileExtension (@Nullable final String sFileExt)
  {
    if (StringHelper.isEmpty (sFileExt))
      return null;

    return m_aRWLock.readLockedGet ( () -> m_aMap.get (sFileExt));
  }

  @Nonnull
  public Function <IReadableResource, AbstractConfigurationSourceResource> getFactoryOfFileExtensionOrFallback (@Nullable final String sFileExt,
                                                                                                                @Nullable final String sFileExtFallback)
  {
    var ret = getFactoryOfFileExtension (sFileExt);
    if (ret == null)
      ret = getFactoryOfFileExtension (sFileExtFallback);
    if (ret == null)
      throw new IllegalStateException ("Failed to resolve factory for '" +
                                       sFileExt +
                                       "' and '" +
                                       sFileExtFallback +
                                       "'");
    return ret;
  }

  public void reinitialize ()
  {
    if (LOGGER.isDebugEnabled ())
      LOGGER.debug ("Reinitializing " + getClass ().getName ());

    _reinitialize ();
  }
}
