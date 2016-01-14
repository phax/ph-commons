/**
 * Copyright (C) 2014-2016 Philip Helger (www.helger.com)
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlSchema;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.ELockType;
import com.helger.commons.annotation.IsLocked;
import com.helger.commons.annotation.Singleton;
import com.helger.commons.cache.AbstractNotifyingCache;
import com.helger.commons.debug.GlobalDebug;
import com.helger.commons.lang.GenericReflection;
import com.helger.commons.state.EChange;

/**
 * Specific cache class for JAXB context elements. This is helpful, as the JAXB
 * context creation is a very time consuming task.
 *
 * @author Philip Helger
 */
@ThreadSafe
@Singleton
public final class JAXBContextCache extends AbstractNotifyingCache <JAXBContextCacheKey, JAXBContext>
{
  private static final class SingletonHolder
  {
    static final JAXBContextCache s_aInstance = new JAXBContextCache ();
  }

  private static final Logger s_aLogger = LoggerFactory.getLogger (JAXBContextCache.class);

  private static boolean s_bDefaultInstantiated = false;

  private JAXBContextCache ()
  {
    super (JAXBContextCache.class.getName ());
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

  @Override
  @Nullable
  @IsLocked (ELockType.WRITE)
  public JAXBContext getValueToCache (@Nullable final JAXBContextCacheKey aCacheKey)
  {
    if (aCacheKey == null)
      return null;

    final Package aPackage = aCacheKey.getPackage ();
    final ClassLoader aClassLoader = aCacheKey.getClassLoader ();

    if (GlobalDebug.isDebugMode ())
      s_aLogger.info ("Creating JAXB context for package " +
                      aPackage.getName () +
                      (aClassLoader == null ? "" : " using ClassLoader " + aClassLoader));

    try
    {
      // When using "-npa" on JAXB no package-info class is created!
      if (aPackage.getAnnotation (XmlSchema.class) == null &&
          GenericReflection.getClassFromNameSafe (aPackage.getName () + ".ObjectFactory") == null)
      {
        s_aLogger.warn ("The package " +
                        aPackage.getName () +
                        " does not seem to be JAXB generated! Trying to create a JAXBContext anyway.");
      }

      return JAXBContext.newInstance (aPackage.getName (), aClassLoader);
    }
    catch (final JAXBException ex)
    {
      final String sMsg = "Failed to create JAXB context for package '" +
                          aPackage.getName () +
                          "'" +
                          (aClassLoader == null ? "" : " using ClassLoader " + aClassLoader);
      s_aLogger.error (sMsg + ": " + ex.getMessage ());
      throw new IllegalArgumentException (sMsg, ex);
    }
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
   * @return Never <code>null</code>.
   */
  @Nonnull
  public JAXBContext getFromCache (@Nonnull final Class <?> aClass)
  {
    return getFromCache (aClass, (ClassLoader) null);
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
   * @param aClassLoader
   *        Class loader to use. May be <code>null</code> in which case the
   *        default class loader is used.
   * @return Never <code>null</code>.
   */
  @Nonnull
  public JAXBContext getFromCache (@Nonnull final Class <?> aClass, @Nullable final ClassLoader aClassLoader)
  {
    ValueEnforcer.notNull (aClass, "Class");

    final Package aPackage = aClass.getPackage ();
    if (aPackage.getAnnotation (XmlSchema.class) != null)
    {
      // Redirect to cached version
      return getFromCache (aPackage, aClassLoader);
    }

    // E.g. an internal class - try anyway!
    if (GlobalDebug.isDebugMode ())
      s_aLogger.info ("Creating JAXB context for class " + aClass.getName ());

    if (aClassLoader != null)
      s_aLogger.warn ("Package " +
                      aPackage.getName () +
                      " does not seem to be JAXB generated. Therefore a new JAXBContext is created and the provided ClassLoader is ignored!");

    try
    {
      return JAXBContext.newInstance (aClass);
    }
    catch (final JAXBException ex)
    {
      final String sMsg = "Failed to create JAXB context for class '" + aClass.getName () + "'";
      s_aLogger.error (sMsg + ": " + ex.getMessage ());
      throw new IllegalArgumentException (sMsg, ex);
    }
  }

  @Nonnull
  public EChange removeFromCache (@Nonnull final Package aPackage)
  {
    return removeFromCache (new JAXBContextCacheKey (aPackage, null));
  }
}
