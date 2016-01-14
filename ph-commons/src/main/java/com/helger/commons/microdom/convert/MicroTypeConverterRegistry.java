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
package com.helger.commons.microdom.convert;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Singleton;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.concurrent.SimpleReadWriteLock;
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
@Singleton
public final class MicroTypeConverterRegistry implements IMicroTypeConverterRegistry
{
  private static final class SingletonHolder
  {
    static final MicroTypeConverterRegistry s_aInstance = new MicroTypeConverterRegistry ();
  }

  private static final Logger s_aLogger = LoggerFactory.getLogger (MicroTypeConverterRegistry.class);

  private static boolean s_bDefaultInstantiated = false;

  private final SimpleReadWriteLock m_aRWLock = new SimpleReadWriteLock ();

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
    final MicroTypeConverterRegistry ret = SingletonHolder.s_aInstance;
    s_bDefaultInstantiated = true;
    return ret;
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

    m_aRWLock.writeLocked ( () -> {
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
    });
  }

  @Nullable
  public IMicroTypeConverter getConverterToMicroElement (@Nullable final Class <?> aSrcClass)
  {
    return m_aRWLock.readLocked ( () -> m_aMap.get (aSrcClass));
  }

  @Nullable
  public IMicroTypeConverter getConverterToNative (@Nonnull final Class <?> aDstClass)
  {
    ValueEnforcer.notNull (aDstClass, "DestClass");

    return m_aRWLock.readLocked ( () -> {
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
              if (s_aLogger.isTraceEnabled ())
                s_aLogger.trace ("Using micro type converter " +
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
    });
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
    final Map <Class <?>, IMicroTypeConverter> aCopy = m_aRWLock.readLocked ( () -> CollectionHelper.newMap (m_aMap));

    // And iterate the copy
    for (final Map.Entry <Class <?>, IMicroTypeConverter> aEntry : aCopy.entrySet ())
      if (aCallback.call (aEntry.getKey (), aEntry.getValue ()).isBreak ())
        break;
  }

  @Nonnegative
  public int getRegisteredMicroTypeConverterCount ()
  {
    return m_aRWLock.readLocked ( () -> m_aMap.size ());
  }

  public void reinitialize ()
  {
    m_aRWLock.writeLocked ( () -> {
      m_aMap.clear ();

      // Register all custom micro type converter
      // Must be in writeLock to ensure no reads happen during initialization
      for (final IMicroTypeConverterRegistrarSPI aSPI : ServiceLoaderHelper.getAllSPIImplementations (IMicroTypeConverterRegistrarSPI.class))
      {
        if (s_aLogger.isDebugEnabled ())
          s_aLogger.debug ("Calling registerMicroTypeConverter on " + aSPI.getClass ().getName ());
        aSPI.registerMicroTypeConverter (this);
      }
    });

    if (s_aLogger.isDebugEnabled ())
      s_aLogger.debug (getRegisteredMicroTypeConverterCount () + " micro type converters registered");
  }
}
