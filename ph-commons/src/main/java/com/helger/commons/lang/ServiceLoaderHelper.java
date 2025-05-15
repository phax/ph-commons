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
package com.helger.commons.lang;

import java.util.ServiceLoader;

import com.helger.annotation.Nonnull;
import com.helger.annotation.Nullable;
import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.IsSPIImplementation;
import com.helger.annotation.style.IsSPIInterface;
import com.helger.annotation.style.ReturnsMutableCopy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.cache.AnnotationUsageCache;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;

/**
 * {@link ServiceLoader} helper class.
 *
 * @author boris
 * @author Philip Helger
 */
@Immutable
public final class ServiceLoaderHelper
{
  public static final AnnotationUsageCache CACHE_INTERFACE = new AnnotationUsageCache (IsSPIInterface.class);
  public static final AnnotationUsageCache CACHE_IMPLEMENTATION = new AnnotationUsageCache (IsSPIImplementation.class);
  private static final Logger LOGGER = LoggerFactory.getLogger (ServiceLoaderHelper.class);

  private ServiceLoaderHelper ()
  {}

  /**
   * Uses the {@link ServiceLoader} to load all SPI implementations of the
   * passed class
   *
   * @param <T>
   *        The implementation type to be loaded
   * @param aSPIClass
   *        The SPI interface class. May not be <code>null</code>.
   * @return A list of all currently available plugins
   */
  @Nonnull
  @ReturnsMutableCopy
  public static <T> ICommonsList <T> getAllSPIImplementations (@Nonnull final Class <T> aSPIClass)
  {
    return getAllSPIImplementations (aSPIClass, ClassLoaderHelper.getDefaultClassLoader (), null);
  }

  /**
   * Uses the {@link ServiceLoader} to load all SPI implementations of the
   * passed class
   *
   * @param <T>
   *        The implementation type to be loaded
   * @param aSPIClass
   *        The SPI interface class. May not be <code>null</code>.
   * @param aClassLoader
   *        The class loader to use for the SPI loader. May not be
   *        <code>null</code>.
   * @return A list of all currently available plugins
   */
  @Nonnull
  @ReturnsMutableCopy
  public static <T> ICommonsList <T> getAllSPIImplementations (@Nonnull final Class <T> aSPIClass,
                                                               @Nonnull final ClassLoader aClassLoader)
  {
    return getAllSPIImplementations (aSPIClass, aClassLoader, null);
  }

  /**
   * Uses the {@link ServiceLoader} to load all SPI implementations of the
   * passed class
   *
   * @param <T>
   *        The implementation type to be loaded
   * @param aSPIClass
   *        The SPI interface class. May not be <code>null</code>.
   * @param aLogger
   *        An optional logger to use. May be <code>null</code>.
   * @return A list of all currently available plugins
   */
  @Nonnull
  @ReturnsMutableCopy
  public static <T> ICommonsList <T> getAllSPIImplementations (@Nonnull final Class <T> aSPIClass,
                                                               @Nullable final Logger aLogger)
  {
    return getAllSPIImplementations (aSPIClass, ClassLoaderHelper.getDefaultClassLoader (), aLogger);
  }

  /**
   * Uses the {@link ServiceLoader} to load all SPI implementations of the
   * passed class
   *
   * @param <T>
   *        The implementation type to be loaded
   * @param aSPIClass
   *        The SPI interface class. May not be <code>null</code>.
   * @param aClassLoader
   *        The class loader to use for the SPI loader. May not be
   *        <code>null</code>.
   * @param aLogger
   *        An optional logger to use. May be <code>null</code>.
   * @return A collection of all currently available plugins. Never
   *         <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static <T> ICommonsList <T> getAllSPIImplementations (@Nonnull final Class <T> aSPIClass,
                                                               @Nonnull final ClassLoader aClassLoader,
                                                               @Nullable final Logger aLogger)
  {
    ValueEnforcer.notNull (aSPIClass, "SPIClass");
    ValueEnforcer.notNull (aClassLoader, "ClassLoader");

    final Logger aRealLogger = aLogger != null ? aLogger : LOGGER;

    if (aRealLogger.isTraceEnabled ())
      aRealLogger.trace ("Trying to retrieve all SPI implementations of " + aSPIClass);

    if (!CACHE_INTERFACE.hasAnnotation (aSPIClass))
      LOGGER.warn (aSPIClass + " should have the @IsSPIInterface annotation");

    final ServiceLoader <T> aServiceLoader = ServiceLoader.<T> load (aSPIClass, aClassLoader);
    final ICommonsList <T> ret = new CommonsArrayList <> ();

    for (final T aInstance : aServiceLoader)
    {
      try
      {
        if (!CACHE_IMPLEMENTATION.hasAnnotation (aInstance))
          LOGGER.warn (aInstance + " should have the @IsSPIImplementation annotation");
        ret.add (aInstance);
      }
      catch (final Exception ex)
      {
        aRealLogger.error ("Unable to load an SPI implementation of " + aSPIClass, ex);
      }
    }
    // @formatter:on

    if (aRealLogger.isDebugEnabled ())
      aRealLogger.debug ("Finished retrieving all " + ret.size () + " SPI implementations of " + aSPIClass);

    return ret;
  }

  /**
   * Uses the {@link ServiceLoader} to load all SPI implementations of the
   * passed class and return only the first instance.
   *
   * @param <T>
   *        The implementation type to be loaded
   * @param aSPIClass
   *        The SPI interface class. May not be <code>null</code>.
   * @return A collection of all currently available plugins. Never
   *         <code>null</code>.
   */
  @Nullable
  public static <T> T getFirstSPIImplementation (@Nonnull final Class <T> aSPIClass)
  {
    return getFirstSPIImplementation (aSPIClass, ClassLoaderHelper.getDefaultClassLoader (), null);
  }

  /**
   * Uses the {@link ServiceLoader} to load all SPI implementations of the
   * passed class and return only the first instance.
   *
   * @param <T>
   *        The implementation type to be loaded
   * @param aSPIClass
   *        The SPI interface class. May not be <code>null</code>.
   * @param aClassLoader
   *        The class loader to use for the SPI loader. May not be
   *        <code>null</code>.
   * @return A collection of all currently available plugins. Never
   *         <code>null</code>.
   */
  @Nullable
  public static <T> T getFirstSPIImplementation (@Nonnull final Class <T> aSPIClass,
                                                 @Nonnull final ClassLoader aClassLoader)
  {
    return getFirstSPIImplementation (aSPIClass, aClassLoader, null);
  }

  /**
   * Uses the {@link ServiceLoader} to load all SPI implementations of the
   * passed class and return only the first instance.
   *
   * @param <T>
   *        The implementation type to be loaded
   * @param aSPIClass
   *        The SPI interface class. May not be <code>null</code>.
   * @param aLogger
   *        An optional logger to use. May be <code>null</code>.
   * @return A collection of all currently available plugins. Never
   *         <code>null</code>.
   */
  @Nullable
  public static <T> T getFirstSPIImplementation (@Nonnull final Class <T> aSPIClass, @Nullable final Logger aLogger)
  {
    return getFirstSPIImplementation (aSPIClass, ClassLoaderHelper.getDefaultClassLoader (), aLogger);
  }

  /**
   * Uses the {@link ServiceLoader} to load all SPI implementations of the
   * passed class and return only the first instance.
   *
   * @param <T>
   *        The implementation type to be loaded
   * @param aSPIClass
   *        The SPI interface class. May not be <code>null</code>.
   * @param aClassLoader
   *        The class loader to use for the SPI loader. May not be
   *        <code>null</code>.
   * @param aLogger
   *        An optional logger to use. May be <code>null</code>.
   * @return A collection of all currently available plugins. Never
   *         <code>null</code>.
   */
  @Nullable
  public static <T> T getFirstSPIImplementation (@Nonnull final Class <T> aSPIClass,
                                                 @Nonnull final ClassLoader aClassLoader,
                                                 @Nullable final Logger aLogger)
  {
    final Logger aRealLogger = aLogger != null ? aLogger : LOGGER;
    final ICommonsList <T> aAll = getAllSPIImplementations (aSPIClass, aClassLoader, aRealLogger);
    if (aAll.isEmpty ())
    {
      // No SPI implementation found
      return null;
    }
    if (aAll.size () > 1)
    {
      // More than one implementation found
      aRealLogger.warn ("Requested only one SPI implementation of " +
                        aSPIClass +
                        " but found " +
                        aAll.size () +
                        " - using the first one. Details: " +
                        aAll);
    }
    return aAll.getFirstOrNull ();
  }
}
