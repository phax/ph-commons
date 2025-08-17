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
package com.helger.base.id.factory;

import java.util.concurrent.atomic.AtomicLong;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.ThreadSafe;
import com.helger.base.hashcode.HashCodeGenerator;
import com.helger.base.tostring.ToStringGenerator;

/**
 * An implementation of {@link ILongIDFactory} using a single static
 * {@link AtomicLong}.
 *
 * @author Philip Helger
 */
@ThreadSafe
public final class MemoryStaticLongIDFactory implements ILongIDFactory
{
  /** The default start ID to use. */
  public static final long INITIAL_ID = 10000L;
  private static final AtomicLong ID = new AtomicLong (INITIAL_ID);

  public MemoryStaticLongIDFactory ()
  {}

  @Nonnegative
  public long getNewID ()
  {
    return getNewStaticID ();
  }

  @Nonnegative
  public static long getNewStaticID ()
  {
    return ID.getAndIncrement ();
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    return true;
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).getToString ();
  }
}
