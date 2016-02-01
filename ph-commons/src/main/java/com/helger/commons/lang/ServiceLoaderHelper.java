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
package com.helger.commons.lang;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.ReturnsMutableCopy;

/**
 * {@link ServiceLoader} helper class.
 *
 * @author boris
 * @author Philip Helger
 */
@Immutable
public final class ServiceLoaderHelper
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (ServiceLoaderHelper.class);

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
  public static <T> List <T> getAllSPIImplementations (@Nonnull final Class <T> aSPIClass)
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
  public static <T> List <T> getAllSPIImplementations (@Nonnull final Class <T> aSPIClass,
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
  public static <T> List <T> getAllSPIImplementations (@Nonnull final Class <T> aSPIClass,
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
  public static <T> List <T> getAllSPIImplementations (@Nonnull final Class <T> aSPIClass,
                                                       @Nonnull final ClassLoader aClassLoader,
                                                       @Nullable final Logger aLogger)
  {
    ValueEnforcer.notNull (aSPIClass, "SPIClass");
    ValueEnforcer.notNull (aClassLoader, "ClassLoader");

    final Logger aRealLogger = aLogger != null ? aLogger : s_aLogger;

    if (aRealLogger.isTraceEnabled ())
      aRealLogger.trace ("Trying to retrieve all SPI implementations of " + aSPIClass);

    final ServiceLoader <T> aServiceLoader = ServiceLoader.<T> load (aSPIClass, aClassLoader);
    final List <T> ret = new ArrayList <> ();

    // We use the iterator to be able to catch exceptions thrown
    // when loading SPI implementations (e.g. the SPI implementation class does
    // not exist)
    final Iterator <T> aIterator = aServiceLoader.iterator ();
    while (aIterator.hasNext ())
    {
      try
      {
        ret.add (aIterator.next ());
      }
      catch (final Throwable t)
      {
        aRealLogger.error ("Unable to load an SPI implementation of " + aSPIClass, t);
      }
    }

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
    final Logger aRealLogger = aLogger != null ? aLogger : s_aLogger;
    final List <T> aAll = getAllSPIImplementations (aSPIClass, aClassLoader, aRealLogger);
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
    return aAll.get (0);
  }
}
