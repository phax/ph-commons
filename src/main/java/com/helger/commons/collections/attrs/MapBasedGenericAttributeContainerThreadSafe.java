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
package com.helger.commons.collections.attrs;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import com.helger.commons.annotations.ReturnsMutableCopy;
import com.helger.commons.state.EChange;

/**
 * Base class for all kind of any-any mapping container. This implementation is
 * a thread-safe wrapper around {@link MapBasedGenericAttributeContainer}!
 *
 * @author Philip Helger
 * @param <KEYTYPE>
 *        Key type
 * @param <VALUETYPE>
 *        Value type
 */
@ThreadSafe
public class MapBasedGenericAttributeContainerThreadSafe <KEYTYPE, VALUETYPE> extends MapBasedGenericAttributeContainer <KEYTYPE, VALUETYPE>
{
  protected final ReadWriteLock m_aRWLock = new ReentrantReadWriteLock ();

  public MapBasedGenericAttributeContainerThreadSafe ()
  {
    super ();
  }

  public MapBasedGenericAttributeContainerThreadSafe (@Nonnull final Map <? extends KEYTYPE, ? extends VALUETYPE> aMap)
  {
    super (aMap);
  }

  public MapBasedGenericAttributeContainerThreadSafe (@Nonnull final IGenericAttributeContainer <? extends KEYTYPE, ? extends VALUETYPE> aCont)
  {
    super (aCont);
  }

  @Override
  public boolean containsAttribute (@Nullable final KEYTYPE aName)
  {
    m_aRWLock.readLock ().lock ();
    try
    {
      return super.containsAttribute (aName);
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }

  @Override
  @Nonnull
  @ReturnsMutableCopy
  public Map <KEYTYPE, VALUETYPE> getAllAttributes ()
  {
    m_aRWLock.readLock ().lock ();
    try
    {
      return super.getAllAttributes ();
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }

  @Override
  @Nullable
  public VALUETYPE getAttributeObject (@Nullable final KEYTYPE aName)
  {
    m_aRWLock.readLock ().lock ();
    try
    {
      return super.getAttributeObject (aName);
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }

  @Override
  @Nonnull
  public EChange setAttribute (@Nonnull final KEYTYPE aName, @Nullable final VALUETYPE aValue)
  {
    m_aRWLock.writeLock ().lock ();
    try
    {
      return super.setAttribute (aName, aValue);
    }
    finally
    {
      m_aRWLock.writeLock ().unlock ();
    }
  }

  @Override
  @Nonnull
  public EChange removeAttribute (@Nullable final KEYTYPE aName)
  {
    m_aRWLock.writeLock ().lock ();
    try
    {
      return super.removeAttribute (aName);
    }
    finally
    {
      m_aRWLock.writeLock ().unlock ();
    }
  }

  @Override
  @Nonnull
  @ReturnsMutableCopy
  public Set <KEYTYPE> getAllAttributeNames ()
  {
    m_aRWLock.readLock ().lock ();
    try
    {
      return super.getAllAttributeNames ();
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }

  @Override
  @Nonnull
  @ReturnsMutableCopy
  public Collection <VALUETYPE> getAllAttributeValues ()
  {
    m_aRWLock.readLock ().lock ();
    try
    {
      return super.getAllAttributeValues ();
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }

  @Override
  @Nonnegative
  public int getAttributeCount ()
  {
    m_aRWLock.readLock ().lock ();
    try
    {
      return super.getAttributeCount ();
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }

  @Override
  public boolean containsNoAttribute ()
  {
    m_aRWLock.readLock ().lock ();
    try
    {
      return super.containsNoAttribute ();
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }

  @Override
  @Nonnull
  public EChange clear ()
  {
    m_aRWLock.writeLock ().lock ();
    try
    {
      return super.clear ();
    }
    finally
    {
      m_aRWLock.writeLock ().unlock ();
    }
  }

  @Override
  @Nonnull
  public MapBasedGenericAttributeContainerThreadSafe <KEYTYPE, VALUETYPE> getClone ()
  {
    return new MapBasedGenericAttributeContainerThreadSafe <KEYTYPE, VALUETYPE> (this);
  }

  @Override
  public boolean equals (final Object o)
  {
    return super.equals (o);
  }

  @Override
  public int hashCode ()
  {
    return super.hashCode ();
  }
}
