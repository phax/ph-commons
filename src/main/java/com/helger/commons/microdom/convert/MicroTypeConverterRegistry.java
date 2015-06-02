/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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
package com.helger.commons.microdom.convert;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.GlobalDebug;
import com.helger.commons.ValueEnforcer;
import com.helger.commons.collections.CollectionHelper;
import com.helger.commons.lang.ClassHierarchyCache;
import com.helger.commons.lang.ServiceLoaderHelper;

/**
 * A utility class for converting objects from and to
 * {@link com.helger.commons.microdom.IMicroElement}.<br>
 * The functionality is a special case of the
 * {@link com.helger.commons.typeconvert.TypeConverterRegistry} as we need a
 * parameter for conversion in this case.
 *
 * @author Philip Helger
 */
@ThreadSafe
public final class MicroTypeConverterRegistry implements IMicroTypeConverterRegistry
{
  private static final class SingletonHolder
  {
    static final MicroTypeConverterRegistry s_aInstance = new MicroTypeConverterRegistry ();
  }

  private static final Logger s_aLogger = LoggerFactory.getLogger (MicroTypeConverterRegistry.class);

  private static boolean s_bDefaultInstantiated = false;

  private final ReadWriteLock m_aRWLock = new ReentrantReadWriteLock ();

  // WeakHashMap because key is a class
  private final Map <Class <?>, IMicroTypeConverter> m_aMap = new WeakHashMap <Class <?>, IMicroTypeConverter> ();

  private MicroTypeConverterRegistry ()
  {
    reinitialize ();
  }

  public static boolean isInstantiated ()
  {
    return s_bDefaultInstantiated;
  }

  /**
   * @return The singleton instance of this class. Never <code>null</code>.
   */
  @Nonnull
  public static MicroTypeConverterRegistry getInstance ()
  {
    s_bDefaultInstantiated = true;
    return SingletonHolder.s_aInstance;
  }

  public void registerMicroElementTypeConverter (@Nonnull final Class <?> aClass,
                                                 @Nonnull final IMicroTypeConverter aConverter)
  {
    _registerMicroElementTypeConverter (aClass, aConverter);
  }

  /**
   * Register type converters from and to XML (IMicroElement). This method is
   * private to avoid later modification of the available type converters,
   * because this may lead to unexpected results.
   *
   * @param aClass
   *        The class to be registered.
   * @param aConverter
   *        The type converter from and to XML
   */
  private void _registerMicroElementTypeConverter (@Nonnull final Class <?> aClass,
                                                   @Nonnull final IMicroTypeConverter aConverter)
  {
    ValueEnforcer.notNull (aClass, "Class");
    ValueEnforcer.notNull (aConverter, "Converter");

    m_aRWLock.writeLock ().lock ();
    try
    {
      // The main class should not already be registered
      if (m_aMap.containsKey (aClass))
        throw new IllegalArgumentException ("A micro type converter for class " + aClass + " is already registered!");

      // Automatically register the class, and all parent classes/interfaces
      for (final WeakReference <Class <?>> aCurWRSrcClass : ClassHierarchyCache.getClassHierarchyIterator (aClass))
      {
        final Class <?> aCurSrcClass = aCurWRSrcClass.get ();
        if (aCurSrcClass != null)
          if (!m_aMap.containsKey (aCurSrcClass))
          {
            m_aMap.put (aCurSrcClass, aConverter);
            if (s_aLogger.isDebugEnabled ())
              s_aLogger.debug ("Registered micro type converter for '" + aCurSrcClass.toString () + "'");
          }
      }
    }
    finally
    {
      m_aRWLock.writeLock ().unlock ();
    }
  }

  @Nullable
  public IMicroTypeConverter getConverterToMicroElement (@Nullable final Class <?> aSrcClass)
  {
    m_aRWLock.readLock ().lock ();
    try
    {
      return m_aMap.get (aSrcClass);
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }

  @Nullable
  public IMicroTypeConverter getConverterToNative (@Nonnull final Class <?> aDstClass)
  {
    ValueEnforcer.notNull (aDstClass, "DestClass");

    m_aRWLock.readLock ().lock ();
    try
    {
      // Check for an exact match first
      IMicroTypeConverter ret = m_aMap.get (aDstClass);
      if (ret == null)
      {
        // No exact match found - try fuzzy
        for (final WeakReference <Class <?>> aCurWRDstClass : ClassHierarchyCache.getClassHierarchyIterator (aDstClass))
        {
          final Class <?> aCurDstClass = aCurWRDstClass.get ();
          if (aCurDstClass != null)
          {
            ret = m_aMap.get (aCurDstClass);
            if (ret != null)
            {
              if (s_aLogger.isDebugEnabled ())
                s_aLogger.debug ("Using micro type converter " +
                                 ret +
                                 " for class " +
                                 aDstClass +
                                 " based on " +
                                 aCurDstClass);
              break;
            }
          }
        }
      }
      return ret;
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }

  /**
   * Iterate all registered micro type converters. For informational purposes
   * only.
   *
   * @param aCallback
   *        The callback invoked for all iterations.
   */
  public void iterateAllRegisteredMicroTypeConverters (@Nonnull final IMicroTypeConverterCallback aCallback)
  {
    // Create a copy of the map
    Map <Class <?>, IMicroTypeConverter> aCopy;
    m_aRWLock.readLock ().lock ();
    try
    {
      aCopy = CollectionHelper.newMap (m_aMap);
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }

    // And iterate the copy
    for (final Map.Entry <Class <?>, IMicroTypeConverter> aEntry : aCopy.entrySet ())
      if (aCallback.call (aEntry.getKey (), aEntry.getValue ()).isBreak ())
        break;
  }

  @Nonnegative
  public int getRegisteredMicroTypeConverterCount ()
  {
    m_aRWLock.readLock ().lock ();
    try
    {
      return m_aMap.size ();
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }

  public void reinitialize ()
  {
    m_aRWLock.writeLock ().lock ();
    try
    {
      m_aMap.clear ();
    }
    finally
    {
      m_aRWLock.writeLock ().unlock ();
    }

    // Register all custom micro type converter
    for (final IMicroTypeConverterRegistrarSPI aSPI : ServiceLoaderHelper.getAllSPIImplementations (IMicroTypeConverterRegistrarSPI.class))
      aSPI.registerMicroTypeConverter (this);

    if (GlobalDebug.isDebugMode ())
      s_aLogger.info (getRegisteredMicroTypeConverterCount () + " micro type converters registered");
  }
}
