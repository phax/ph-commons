/**
 * Copyright (C) 2014-2021 Philip Helger (www.helger.com)
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
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.annotation.XmlSchema;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Singleton;
import com.helger.commons.cache.Cache;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.debug.GlobalDebug;
import com.helger.commons.state.EChange;

/**
 * Specific cache class for JAXB context elements. This is helpful, as the JAXB
 * context creation is a very time consuming task.
 *
 * @author Philip Helger
 */
@ThreadSafe
@Singleton
public final class JAXBContextCache extends Cache <JAXBContextCacheKey, JAXBContext>
{
  private static final class SingletonHolder
  {
    static final JAXBContextCache s_aInstance = new JAXBContextCache ();
  }

  private static final AtomicBoolean SILENT_MODE = new AtomicBoolean (GlobalDebug.DEFAULT_SILENT_MODE);

  private static boolean s_bDefaultInstantiated = false;

  /**
   * @return <code>true</code> if logging is disabled, <code>false</code> if it
   *         is enabled.
   * @since 9.4.0
   */
  public static boolean isSilentMode ()
  {
    return SILENT_MODE.get ();
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
    return SILENT_MODE.getAndSet (bSilentMode);
  }

  private JAXBContextCache ()
  {
    super (aCacheKey -> aCacheKey.createJAXBContext (isSilentMode ()), 500, JAXBContextCache.class.getName ());
  }

  public static boolean isInstantiated ()
  {
    return s_bDefaultInstantiated;
  }

  @Nonnull
  public static JAXBContextCache getInstance ()
  {
    final JAXBContextCache ret = SingletonHolder.s_aInstance;
    s_bDefaultInstantiated = true;
    return ret;
  }

  /**
   * Special overload with package and default {@link ClassLoader}.
   *
   * @param aPackage
   *        Package to load. May not be <code>null</code>.
   * @return <code>null</code> if package is <code>null</code>.
   */
  @Nullable
  public JAXBContext getFromCache (@Nonnull final Package aPackage)
  {
    return getFromCache (aPackage, (ClassLoader) null);
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
   */
  @Nullable
  public JAXBContext getFromCache (@Nonnull final Package aPackage, @Nullable final ClassLoader aClassLoader)
  {
    return getFromCache (new JAXBContextCacheKey (aPackage, aClassLoader));
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
   */
  @Nullable
  public JAXBContext getFromCache (@Nonnull final Class <?> aClass)
  {
    return getFromCache (aClass, (ClassLoader) null);
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
   */
  @Nullable
  public JAXBContext getFromCache (@Nonnull final Class <?> aClass, @Nullable final ClassLoader aClassLoader)
  {
    ValueEnforcer.notNull (aClass, "Class");

    final Package aPackage = aClass.getPackage ();
    if (aPackage.getAnnotation (XmlSchema.class) != null)
    {
      // Redirect to cached version
      return getFromCache (aPackage, aClassLoader);
    }

    return getFromCache (new CommonsArrayList <> (aClass), (Map <String, ?>) null);
  }

  /**
   * Get the {@link JAXBContext} from existing {@link Class} objects.
   *
   * @param aClasses
   *        The classes for which the JAXB context is to be created. May not be
   *        <code>null</code> nor empty.
   * @return May be <code>null</code>.
   * @since v9.4.2
   */
  @Nullable
  public JAXBContext getFromCache (@Nonnull final ICommonsList <Class <?>> aClasses)
  {
    return getFromCache (aClasses, (Map <String, ?>) null);
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
   */
  @Nullable
  public JAXBContext getFromCache (@Nonnull final ICommonsList <Class <?>> aClasses, @Nullable final Map <String, ?> aProperties)
  {
    ValueEnforcer.notEmptyNoNullValue (aClasses, "Classes");
    return getFromCache (new JAXBContextCacheKey (aClasses, aProperties));
  }

  @Nonnull
  public EChange removeFromCache (@Nonnull final Package aPackage)
  {
    return removeFromCache (aPackage, (ClassLoader) null);
  }

  @Nonnull
  public EChange removeFromCache (@Nonnull final Package aPackage, @Nullable final ClassLoader aClassLoader)
  {
    return removeFromCache (new JAXBContextCacheKey (aPackage, aClassLoader));
  }

  @Nonnull
  public EChange removeFromCache (@Nonnull final ICommonsList <Class <?>> aClasses)
  {
    return removeFromCache (aClasses, (Map <String, ?>) null);
  }

  @Nonnull
  public EChange removeFromCache (@Nonnull final ICommonsList <Class <?>> aClasses, @Nullable final Map <String, ?> aProperties)
  {
    return removeFromCache (new JAXBContextCacheKey (aClasses, aProperties));
  }
}
