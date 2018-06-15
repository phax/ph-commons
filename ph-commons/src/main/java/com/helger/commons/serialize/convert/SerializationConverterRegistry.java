/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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
package com.helger.commons.serialize.convert;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.Map;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Singleton;
import com.helger.commons.collection.impl.CommonsHashMap;
import com.helger.commons.collection.impl.CommonsWeakHashMap;
import com.helger.commons.collection.impl.ICommonsMap;
import com.helger.commons.concurrent.SimpleReadWriteLock;
import com.helger.commons.lang.ClassHierarchyCache;
import com.helger.commons.lang.GenericReflection;
import com.helger.commons.lang.ServiceLoaderHelper;

/**
 * The registry that keeps the mappings for serialization converters.
 *
 * @author Philip Helger
 */
@ThreadSafe
@Singleton
public final class SerializationConverterRegistry implements ISerializationConverterRegistry
{
  private static final class SingletonHolder
  {
    private static final SerializationConverterRegistry s_aInstance = new SerializationConverterRegistry ();
  }

  private static final Logger s_aLogger = LoggerFactory.getLogger (SerializationConverterRegistry.class);

  private static boolean s_bDefaultInstantiated = false;

  private final SimpleReadWriteLock m_aRWLock = new SimpleReadWriteLock ();

  // WeakHashMap because key is a class
  @GuardedBy ("m_aRWLock")
  private final ICommonsMap <Class <?>, ISerializationConverter <?>> m_aMap = new CommonsWeakHashMap <> ();

  private SerializationConverterRegistry ()
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
  public static SerializationConverterRegistry getInstance ()
  {
    final SerializationConverterRegistry ret = SingletonHolder.s_aInstance;
    s_bDefaultInstantiated = true;
    return ret;
  }

  public <T> void registerSerializationConverter (@Nonnull final Class <T> aClass,
                                                  @Nonnull final ISerializationConverter <T> aConverter)
  {
    ValueEnforcer.notNull (aClass, "Class");
    ValueEnforcer.notNull (aConverter, "Converter");
    ValueEnforcer.isFalse (Serializable.class.isAssignableFrom (aClass),
                           () -> "The provided " + aClass.toString () + " is already implementing Serializable!");

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
              s_aLogger.debug ("Registered serialization converter for '" + aCurSrcClass.toString () + "'");
          }
      }
    });
  }

  @Nullable
  public <T> ISerializationConverter <T> getConverter (@Nullable final Class <T> aDstClass)
  {
    return m_aRWLock.readLocked ( () -> {
      // Check for an exact match first
      ISerializationConverter <?> ret = m_aMap.get (aDstClass);
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
                s_aLogger.debug ("Using serialization converter " +
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
      return GenericReflection.uncheckedCast (ret);
    });
  }

  /**
   * Iterate all registered serialization converters. For informational purposes
   * only.
   *
   * @param aCallback
   *        The callback invoked for all iterations.
   */
  public void iterateAllRegisteredSerializationConverters (@Nonnull final ISerializationConverterCallback aCallback)
  {
    // Create a static (non weak) copy of the map
    final Map <Class <?>, ISerializationConverter <?>> aCopy = m_aRWLock.readLocked ( () -> new CommonsHashMap <> (m_aMap));

    // And iterate the copy
    for (final Map.Entry <Class <?>, ISerializationConverter <?>> aEntry : aCopy.entrySet ())
      if (aCallback.call (aEntry.getKey (), aEntry.getValue ()).isBreak ())
        break;
  }

  @Nonnegative
  public int getRegisteredSerializationConverterCount ()
  {
    return m_aRWLock.readLocked (m_aMap::size);
  }

  public void reinitialize ()
  {
    m_aRWLock.writeLocked (m_aMap::clear);

    // Register all custom micro type converter
    for (final ISerializationConverterRegistrarSPI aSPI : ServiceLoaderHelper.getAllSPIImplementations (ISerializationConverterRegistrarSPI.class))
      aSPI.registerSerializationConverter (this);

    if (s_aLogger.isDebugEnabled ())
      s_aLogger.debug (getRegisteredSerializationConverterCount () + " serialization converters registered");
  }
}
