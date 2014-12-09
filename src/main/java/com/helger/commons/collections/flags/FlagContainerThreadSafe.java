/**
 * Copyright (C) 2014 Philip Helger (www.helger.com)
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
package com.helger.commons.collections.flags;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import com.helger.commons.annotations.ReturnsMutableCopy;
import com.helger.commons.collections.attrs.IAttributeContainer;
import com.helger.commons.state.EChange;

/**
 * Base class for all kind of string-object mapping container. This
 * implementation is a thread-safe wrapper around {@link FlagContainer}!
 *
 * @author Philip Helger
 * @deprecated Use an {@link IAttributeContainer} instead.
 */
@ThreadSafe
@Deprecated
public class FlagContainerThreadSafe extends FlagContainer
{
  protected final ReadWriteLock m_aRWLock = new ReentrantReadWriteLock ();

  public FlagContainerThreadSafe ()
  {
    super ();
  }

  public FlagContainerThreadSafe (@Nonnull final Collection <String> aValues)
  {
    super (aValues);
  }

  public FlagContainerThreadSafe (@Nonnull final String... aValues)
  {
    super (aValues);
  }

  public FlagContainerThreadSafe (@Nonnull final IReadonlyFlagContainer aCont)
  {
    super (aCont);
  }

  @Override
  public boolean containsFlag (@Nullable final String sName)
  {
    m_aRWLock.readLock ().lock ();
    try
    {
      return super.containsFlag (sName);
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }

  @Override
  @Nonnull
  @ReturnsMutableCopy
  public Set <String> getAllFlags ()
  {
    m_aRWLock.readLock ().lock ();
    try
    {
      return super.getAllFlags ();
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }

  @Override
  @Nonnull
  public EChange addFlag (@Nonnull final String sName)
  {
    m_aRWLock.writeLock ().lock ();
    try
    {
      return super.addFlag (sName);
    }
    finally
    {
      m_aRWLock.writeLock ().unlock ();
    }
  }

  @Override
  @Nonnull
  public EChange removeFlag (@Nullable final String sName)
  {
    m_aRWLock.writeLock ().lock ();
    try
    {
      return super.removeFlag (sName);
    }
    finally
    {
      m_aRWLock.writeLock ().unlock ();
    }
  }

  @Override
  @Nonnegative
  public int getFlagCount ()
  {
    m_aRWLock.readLock ().lock ();
    try
    {
      return super.getFlagCount ();
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }

  @Override
  public boolean containsNoFlag ()
  {
    m_aRWLock.readLock ().lock ();
    try
    {
      return super.containsNoFlag ();
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
