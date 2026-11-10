/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.GuardedBy;
import com.helger.annotation.concurrent.ThreadSafe;
import com.helger.annotation.misc.Singleton;
import com.helger.base.concurrent.SimpleReadWriteLock;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.reflection.GenericReflection;
import com.helger.base.spi.ServiceLoaderHelper;
import com.helger.collection.commons.CommonsHashMap;
import com.helger.collection.commons.CommonsWeakHashMap;
import com.helger.collection.commons.ICommonsMap;
import com.helger.typeconvert.util.ClassHierarchyCache;

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
    private static final SerializationConverterRegistry INSTANCE = new SerializationConverterRegistry ();
  }

  private static final Logger LOGGER = LoggerFactory.getLogger (SerializationConverterRegistry.class);

  private static volatile boolean s_bDefaultInstantiated = false;

  private final SimpleReadWriteLock m_aRWLock = new SimpleReadWriteLock ();

  // WeakHashMap because key is a class. Note: every access to a WeakHashMap (including get and
  // size) may expunge stale entries and therefore structurally modify it - that is why all
  // accesses must happen under the write lock
  @GuardedBy ("m_aRWLock")
  private final ICommonsMap <Class <?>, ISerializationConverter <?>> m_aMap = new CommonsWeakHashMap <> ();

  private SerializationConverterRegistry ()
  {
    reinitialize ();
  }

  /**
   * @return <code>true</code> if the singleton instance has been created, <code>false</code>
   *         otherwise.
   */
  public static boolean isInstantiated ()
  {
    return s_bDefaultInstantiated;
  }

  /**
   * @return The singleton instance of this class. Never <code>null</code>.
   */
  @NonNull
  public static SerializationConverterRegistry getInstance ()
  {
    final SerializationConverterRegistry ret = SingletonHolder.INSTANCE;
    s_bDefaultInstantiated = true;
    return ret;
  }

  /**
   * {@inheritDoc}
   */
  public <T> void registerSerializationConverter (@NonNull final Class <T> aClass,
                                                  @NonNull final ISerializationConverter <T> aConverter)
  {
    ValueEnforcer.notNull (aClass, "Class");
    ValueEnforcer.notNull (aConverter, "Converter");
    ValueEnforcer.isFalse (Serializable.class.isAssignableFrom (aClass),
                           () -> "The provided " + aClass.toString () + " is already implementing Serializable!");

    m_aRWLock.writeLocked (() -> {
      // The class should not already be registered
      if (m_aMap.containsKey (aClass))
        throw new IllegalArgumentException ("A serialization converter for class " + aClass + " is already registered!");

      // Only register the class itself - sub classes are found via the class hierarchy in
      // getConverter
      m_aMap.put (aClass, aConverter);
      if (LOGGER.isDebugEnabled ())
        LOGGER.debug ("Registered serialization converter for '" + aClass.toString () + "'");
    });
  }

  /**
   * Get the serialization converter for the specified class. If no exact match is found, the
   * converter of the closest registered super class or interface is used. Super classes have
   * precedence over interfaces.
   *
   * @param aDstClass
   *        The class to find a converter for. May be <code>null</code>.
   * @param <T>
   *        The type of the class
   * @return The converter, or <code>null</code> if no converter was found.
   */
  @Nullable
  public <T> ISerializationConverter <T> getConverter (@Nullable final Class <T> aDstClass)
  {
    // Write lock, because WeakHashMap.get modifies the map
    return m_aRWLock.writeLockedGet (() -> {
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
              if (LOGGER.isDebugEnabled ())
                LOGGER.debug ("Using serialization converter " +
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
   * Iterate all registered serialization converters. For informational purposes only.
   *
   * @param aCallback
   *        The callback invoked for all iterations.
   */
  public void iterateAllRegisteredSerializationConverters (@NonNull final ISerializationConverterCallback aCallback)
  {
    // Create a static (non weak) copy of the map
    // Write lock, because copying the WeakHashMap modifies the map
    final Map <Class <?>, ISerializationConverter <?>> aCopy = m_aRWLock.writeLockedGet (() -> new CommonsHashMap <> (m_aMap));

    // And iterate the copy
    for (final Map.Entry <Class <?>, ISerializationConverter <?>> aEntry : aCopy.entrySet ())
      if (aCallback.call (aEntry.getKey (), aEntry.getValue ()).isBreak ())
        break;
  }

  /**
   * @return The number of currently registered serialization converters. Always &ge; 0.
   */
  @Nonnegative
  public int getRegisteredSerializationConverterCount ()
  {
    // Write lock, because WeakHashMap.size modifies the map
    return m_aRWLock.writeLockedInt (m_aMap::size);
  }

  /**
   * Clear all registered converters and re-register them from the SPI implementations.
   */
  public void reinitialize ()
  {
    m_aRWLock.writeLocked (m_aMap::clear);

    // Register all custom micro type converter
    for (final ISerializationConverterRegistrarSPI aSPI : ServiceLoaderHelper.getAllSPIImplementations (ISerializationConverterRegistrarSPI.class))
      aSPI.registerSerializationConverter (this);

    if (LOGGER.isDebugEnabled ())
      LOGGER.debug (getRegisteredSerializationConverterCount () + " serialization converters registered");
  }
}
