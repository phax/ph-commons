/*
 * Copyright (C) 2014-2023 Philip Helger (www.helger.com)
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
package com.helger.jaxb;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.annotation.Singleton;
import com.helger.commons.cache.Cache;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.debug.GlobalDebug;
import com.helger.commons.log.ConditionalLogger;
import com.helger.commons.log.IHasConditionalLogger;
import com.helger.commons.state.EChange;

import jakarta.xml.bind.JAXBContext;

/**
 * Specific cache class for JAXB context elements. This is helpful, as the JAXB
 * context creation is a very time consuming task.
 *
 * @author Philip Helger
 */
@ThreadSafe
@Singleton
public final class JAXBContextCache extends Cache <JAXBContextCacheKey, JAXBContext> implements IHasConditionalLogger
{
  private static final class SingletonHolder
  {
    static final JAXBContextCache INSTANCE = new JAXBContextCache ();
  }

  private static final Logger LOGGER = LoggerFactory.getLogger (JAXBContextCache.class);
  private static final ConditionalLogger CONDLOG = new ConditionalLogger (LOGGER, !GlobalDebug.DEFAULT_SILENT_MODE);

  private static boolean s_bDefaultInstantiated = false;

  /**
   * @return <code>true</code> if logging is disabled, <code>false</code> if it
   *         is enabled.
   * @since 9.4.0
   */
  public static boolean isSilentMode ()
  {
    return CONDLOG.isDisabled ();
  }

  /**
   * Enable or disable certain regular log messages.
   *
   * @param bSilentMode
   *        <code>true</code> to disable logging, <code>false</code> to enable
   *        logging
   * @return The previous value of the silent mode.
   * @since 9.4.0
   */
  public static boolean setSilentMode (final boolean bSilentMode)
  {
    return !CONDLOG.setEnabled (!bSilentMode);
  }

  private JAXBContextCache ()
  {
    super (aCacheKey -> aCacheKey.createJAXBContext (CONDLOG), 500, JAXBContextCache.class.getName ());
  }

  public static boolean isInstantiated ()
  {
    return s_bDefaultInstantiated;
  }

  @Nonnull
  public static JAXBContextCache getInstance ()
  {
    final JAXBContextCache ret = SingletonHolder.INSTANCE;
    s_bDefaultInstantiated = true;
    return ret;
  }

  /**
   * Special overload with package and default {@link ClassLoader}.
   *
   * @param aPackage
   *        Package to load. May not be <code>null</code>.
   * @return <code>null</code> if package is <code>null</code>.
   * @deprecated Use the {@link JAXBContextCacheKey} factory methods instead
   */
  @Nullable
  @Deprecated (forRemoval = true, since = "11.0.4")
  public JAXBContext getFromCache (@Nonnull final Package aPackage)
  {
    return getFromCache (JAXBContextCacheKey.createForPackage (aPackage));
  }

  /**
   * Special overload with package and {@link ClassLoader}. In this case the
   * resulting value is NOT cached!
   *
   * @param aPackage
   *        Package to load. May not be <code>null</code>.
   * @param aClassLoader
   *        Class loader to use. May be <code>null</code> in which case the
   *        default class loader is used.
   * @return <code>null</code> if package is <code>null</code>.
   * @deprecated Use the {@link JAXBContextCacheKey} factory methods instead
   */
  @Nullable
  @Deprecated (forRemoval = true, since = "11.0.4")
  public JAXBContext getFromCache (@Nonnull final Package aPackage, @Nullable final ClassLoader aClassLoader)
  {
    return getFromCache (JAXBContextCacheKey.createForPackage (aPackage, aClassLoader));
  }

  /**
   * Get the {@link JAXBContext} from an existing {@link Class} object. If the
   * class's owning package is a valid JAXB package, this method redirects to
   * {@link #getFromCache(Package)} otherwise a new JAXB context is created and
   * NOT cached.
   *
   * @param aClass
   *        The class for which the JAXB context is to be created. May not be
   *        <code>null</code>.
   * @return May be <code>null</code>.
   * @deprecated Use the {@link JAXBContextCacheKey} factory methods instead
   */
  @Nullable
  @Deprecated (forRemoval = true, since = "11.0.4")
  public JAXBContext getFromCache (@Nonnull final Class <?> aClass)
  {
    return getFromCache (JAXBContextCacheKey.createForClass (aClass));
  }

  /**
   * Get the {@link JAXBContext} from an existing {@link Class} object. If the
   * class's owning package is a valid JAXB package, this method redirects to
   * {@link #getFromCache(Package)}.
   *
   * @param aClass
   *        The class for which the JAXB context is to be created. May not be
   *        <code>null</code>.
   * @param aClassLoader
   *        Class loader to use. May be <code>null</code> in which case the
   *        default class loader is used.
   * @return May be <code>null</code>.
   * @deprecated Use the {@link JAXBContextCacheKey} factory methods instead
   */
  @Nullable
  @Deprecated (forRemoval = true, since = "11.0.4")
  public JAXBContext getFromCache (@Nonnull final Class <?> aClass, @Nullable final ClassLoader aClassLoader)
  {
    return getFromCache (JAXBContextCacheKey.createForClass (aClass, aClassLoader));
  }

  /**
   * Get the {@link JAXBContext} from existing {@link Class} objects.
   *
   * @param aClasses
   *        The classes for which the JAXB context is to be created. May not be
   *        <code>null</code> nor empty.
   * @return May be <code>null</code>.
   * @since v9.4.2
   * @deprecated Use the {@link JAXBContextCacheKey} factory methods instead
   */
  @Nullable
  @Deprecated (forRemoval = true, since = "11.0.4")
  public JAXBContext getFromCache (@Nonnull final ICommonsList <Class <?>> aClasses)
  {
    return getFromCache (JAXBContextCacheKey.createForClasses (aClasses));
  }

  /**
   * Get the {@link JAXBContext} from existing {@link Class} objects and
   * optional JAXB Context properties.
   *
   * @param aClasses
   *        The classes for which the JAXB context is to be created. May not be
   *        <code>null</code> nor empty.
   * @param aProperties
   *        JAXB context properties. May be <code>null</code>.
   * @return May be <code>null</code>.
   * @since v9.4.2
   * @deprecated Use the {@link JAXBContextCacheKey} factory methods instead
   */
  @Nullable
  @Deprecated (forRemoval = true, since = "11.0.4")
  public JAXBContext getFromCache (@Nonnull final ICommonsList <Class <?>> aClasses,
                                   @Nullable final Map <String, ?> aProperties)
  {
    return getFromCache (JAXBContextCacheKey.createForClasses (aClasses, aProperties));
  }

  @Nonnull
  @Deprecated (forRemoval = true, since = "11.0.4")
  public EChange removeFromCache (@Nonnull final Package aPackage)
  {
    return removeFromCache (JAXBContextCacheKey.createForPackage (aPackage));
  }

  @Nonnull
  @Deprecated (forRemoval = true, since = "11.0.4")
  public EChange removeFromCache (@Nonnull final Package aPackage, @Nullable final ClassLoader aClassLoader)
  {
    return removeFromCache (JAXBContextCacheKey.createForPackage (aPackage, aClassLoader));
  }

  @Nonnull
  @Deprecated (forRemoval = true, since = "11.0.4")
  public EChange removeFromCache (@Nonnull final ICommonsList <Class <?>> aClasses)
  {
    return removeFromCache (JAXBContextCacheKey.createForClasses (aClasses));
  }

  @Nonnull
  @Deprecated (forRemoval = true, since = "11.0.4")
  public EChange removeFromCache (@Nonnull final ICommonsList <Class <?>> aClasses,
                                  @Nullable final Map <String, ?> aProperties)
  {
    return removeFromCache (JAXBContextCacheKey.createForClasses (aClasses, aProperties));
  }
}
