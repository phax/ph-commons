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
package com.helger.commons.id.factory;

import java.util.concurrent.atomic.AtomicLong;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.ThreadSafe;
import com.helger.base.enforcer.ValueEnforcer;
import com.helger.base.hashcode.HashCodeGenerator;
import com.helger.base.string.ToStringGenerator;

/**
 * A default implementation for non-negative in-memory IDs.
 *
 * @author Philip Helger
 */
@ThreadSafe
public final class MemoryLongIDFactory implements ILongIDFactory
{
  /** The default start ID to use. */
  public static final long DEFAULT_START_ID = 10000L;

  private final AtomicLong m_aID;

  public MemoryLongIDFactory ()
  {
    // new IDs start at 10000
    this (DEFAULT_START_ID);
  }

  public MemoryLongIDFactory (@Nonnegative final long nStartID)
  {
    ValueEnforcer.isGE0 (nStartID, "StartID");
    m_aID = new AtomicLong (nStartID);
  }

  public long getNewID ()
  {
    return m_aID.getAndIncrement ();
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    // AtomicInteger does not implement equals and hashCode!
    final MemoryLongIDFactory rhs = (MemoryLongIDFactory) o;
    return m_aID.get () == rhs.m_aID.get ();
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aID.get ()).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("ID", m_aID).getToString ();
  }
}
