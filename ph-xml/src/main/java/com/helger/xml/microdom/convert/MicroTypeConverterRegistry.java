/*
 * Copyright (C) 2014-2024 Philip Helger (www.helger.com)
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
package com.helger.xml.microdom.convert;

import java.lang.ref.WeakReference;
import java.util.Map;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Singleton;
import com.helger.commons.collection.impl.CommonsWeakHashMap;
import com.helger.commons.collection.impl.ICommonsMap;
import com.helger.commons.concurrent.SimpleReadWriteLock;
import com.helger.commons.lang.ClassHierarchyCache;
import com.helger.commons.lang.GenericReflection;
import com.helger.commons.lang.ServiceLoaderHelper;

/**
 * A utility class for converting objects from and to
 * {@link com.helger.xml.microdom.IMicroElement}.<br>
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
    private static final MicroTypeConverterRegistry INSTANCE = new MicroTypeConverterRegistry ();
  }

  public static final boolean DEFAULT_USE_CLASS_HIERARCHY = false;
  private static final Logger LOGGER = LoggerFactory.getLogger (MicroTypeConverterRegistry.class);

  private static boolean s_bDefaultInstantiated = false;

  private final SimpleReadWriteLock m_aRWLock = new SimpleReadWriteLock ();

  // WeakHashMap because key is a class
  private final ICommonsMap <Class <?>, IMicroTypeConverter <?>> m_aMap = new CommonsWeakHashMap <> ();
  private boolean m_bUseClassHierarchy = DEFAULT_USE_CLASS_HIERARCHY;

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
    final MicroTypeConverterRegistry ret = SingletonHolder.INSTANCE;
    s_bDefaultInstantiated = true;
    return ret;
  }

  public boolean isUseClassHierarchy ()
  {
    return m_bUseClassHierarchy;
  }

  public void setUseClassHierarchy (final boolean bUseClassHierarchy)
  {
    if (m_bUseClassHierarchy != bUseClassHierarchy)
    {
      m_bUseClassHierarchy = bUseClassHierarchy;
      // Must re-initialize so that all registrations are performed
      reinitialize ();
    }
  }

  public <T> void registerMicroElementTypeConverter (@Nonnull final Class <T> aClass, @Nonnull final IMicroTypeConverter <T> aConverter)
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
  private <T> void _registerMicroElementTypeConverter (@Nonnull final Class <T> aClass, @Nonnull final IMicroTypeConverter <T> aConverter)
  {
    ValueEnforcer.notNull (aClass, "Class");
    ValueEnforcer.notNull (aConverter, "Converter");

    m_aRWLock.writeLocked ( () -> {
      // The main class should not already be registered
      if (m_aMap.containsKey (aClass))
        throw new IllegalArgumentException ("A micro type converter for class " + aClass + " is already registered!");

      if (m_bUseClassHierarchy)
      {
        // Automatically register the class, and all parent classes/interfaces
        for (final WeakReference <Class <?>> aCurWRSrcClass : ClassHierarchyCache.getClassHierarchyIterator (aClass))
        {
          final Class <?> aCurSrcClass = aCurWRSrcClass.get ();
          if (aCurSrcClass != null)
            if (!m_aMap.containsKey (aCurSrcClass))
            {
              m_aMap.put (aCurSrcClass, aConverter);
              if (LOGGER.isDebugEnabled ())
                LOGGER.debug ("Registered micro type converter for '" + aCurSrcClass.toString () + "'");
            }
        }
      }
      else
      {
        m_aMap.put (aClass, aConverter);
        if (LOGGER.isDebugEnabled ())
          LOGGER.debug ("Registered micro type converter for '" + aClass.toString () + "'");
      }
    });
  }

  @Nullable
  public <T> IMicroTypeConverter <T> getConverterToMicroElement (@Nullable final Class <T> aSrcClass)
  {
    return GenericReflection.uncheckedCast (m_aRWLock.readLockedGet ( () -> m_aMap.get (aSrcClass)));
  }

  @Nullable
  public <T> IMicroTypeConverter <T> getConverterToNative (@Nonnull final Class <T> aDstClass)
  {
    ValueEnforcer.notNull (aDstClass, "DestClass");

    final IMicroTypeConverter <?> ret2 = m_aRWLock.readLockedGet ( () -> {
      // Check for an exact match first
      IMicroTypeConverter <?> ret = m_aMap.get (aDstClass);
      if (ret != null)
      {
        if (LOGGER.isTraceEnabled ())
          LOGGER.trace ("Using micro type converter " + ret + " for class " + aDstClass + " based on direct match");
      }
      else
        if (m_bUseClassHierarchy)
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
                if (LOGGER.isTraceEnabled ())
                  LOGGER.trace ("Using micro type converter " + ret + " for class " + aDstClass + " based on " + aCurDstClass);
                break;
              }
            }
          }
        }
      return ret;
    });
    return GenericReflection.uncheckedCast (ret2);
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
    // Create a static copy of the map (HashMap not weak!)
    final ICommonsMap <Class <?>, IMicroTypeConverter <?>> aCopy = m_aRWLock.readLockedGet (m_aMap::getClone);

    // And iterate the copy
    for (final Map.Entry <Class <?>, IMicroTypeConverter <?>> aEntry : aCopy.entrySet ())
      if (aCallback.call (aEntry.getKey (), aEntry.getValue ()).isBreak ())
        break;
  }

  @Nonnegative
  public int getRegisteredMicroTypeConverterCount ()
  {
    return m_aRWLock.readLockedInt (m_aMap::size);
  }

  public void reinitialize ()
  {
    m_aRWLock.writeLocked ( () -> {
      m_aMap.clear ();

      // Register all custom micro type converter
      // Must be in writeLock to ensure no reads happen during initialization
      for (final IMicroTypeConverterRegistrarSPI aSPI : ServiceLoaderHelper.getAllSPIImplementations (IMicroTypeConverterRegistrarSPI.class))
      {
        if (LOGGER.isDebugEnabled ())
          LOGGER.debug ("Calling registerMicroTypeConverter on " + aSPI.getClass ().getName ());
        aSPI.registerMicroTypeConverter (this);
      }
    });

    if (LOGGER.isDebugEnabled ())
      LOGGER.debug (getRegisteredMicroTypeConverterCount () + " micro type converters registered");
  }
}
