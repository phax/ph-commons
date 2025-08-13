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
package com.helger.json.valueserializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.GuardedBy;
import com.helger.annotation.concurrent.ThreadSafe;
import com.helger.commons.collection.impl.CommonsWeakHashMap;
import com.helger.commons.collection.impl.ICommonsMap;
import com.helger.commons.concurrent.SimpleReadWriteLock;
import com.helger.commons.lang.ClassLoaderHelper;
import com.helger.commons.lang.ServiceLoaderHelper;
import com.helger.commons.string.ToStringGenerator;
import com.helger.commons.valueenforcer.ValueEnforcer;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * Registry that determines the {@link IJsonValueSerializer} object to be used
 * for certain classes.
 *
 * @author Philip Helger
 */
@ThreadSafe
public final class JsonValueSerializerRegistry implements IJsonValueSerializerRegistry
{
  private static final Logger LOGGER = LoggerFactory.getLogger (JsonValueSerializerRegistry.class);
  private static final JsonValueSerializerRegistry INSTANCE = new JsonValueSerializerRegistry ();

  private final SimpleReadWriteLock m_aRWLock = new SimpleReadWriteLock ();

  // WeakHashMap because key is a class
  @GuardedBy ("m_aRWLock")
  private final ICommonsMap <Class <?>, IJsonValueSerializer> m_aMap = new CommonsWeakHashMap <> ();

  static
  {
    // Use default class loader
    INSTANCE.reinitialize ((ClassLoader) null);
  }

  private JsonValueSerializerRegistry ()
  {}

  /**
   * @return The singleton instance of this class. Never <code>null</code>.
   */
  @Nonnull
  public static JsonValueSerializerRegistry getInstance ()
  {
    return INSTANCE;
  }

  public void registerJsonValueSerializer (@Nonnull final Class <?> aClass, @Nonnull final IJsonValueSerializer aValueSerializer)
  {
    ValueEnforcer.notNull (aClass, "Class");
    ValueEnforcer.notNull (aValueSerializer, "ValueSerializer");

    m_aRWLock.writeLocked ( () -> {
      // The class should not already be registered
      if (m_aMap.containsKey (aClass))
        throw new IllegalArgumentException ("An IJsonValueSerializer for class " + aClass + " is already registered!");

      // register the class
      m_aMap.put (aClass, aValueSerializer);
    });
  }

  @Nullable
  public IJsonValueSerializer getJsonValueSerializer (@Nullable final Class <?> aSrcClass)
  {
    return m_aRWLock.readLockedGet ( () -> m_aMap.get (aSrcClass));
  }

  @Nonnegative
  public int getRegisteredJsonValueSerializerCount ()
  {
    return m_aRWLock.readLockedInt (m_aMap::size);
  }

  public void reinitialize (@Nullable final ClassLoader aClassLoader)
  {
    m_aRWLock.writeLocked (m_aMap::clear);

    // Register all json value serializer
    for (final IJsonValueSerializerRegistrarSPI aSPI : ServiceLoaderHelper.getAllSPIImplementations (IJsonValueSerializerRegistrarSPI.class,
                                                                                                     aClassLoader != null ? aClassLoader
                                                                                                                          : ClassLoaderHelper.getDefaultClassLoader ()))
      aSPI.registerJsonValueSerializer (this);

    if (LOGGER.isDebugEnabled ())
      LOGGER.debug (INSTANCE.getRegisteredJsonValueSerializerCount () + " JSON value serializers registered");
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("map", m_aMap).getToString ();
  }
}
