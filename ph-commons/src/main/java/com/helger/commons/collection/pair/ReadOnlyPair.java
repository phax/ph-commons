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
package com.helger.commons.collection.pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.string.ToStringGenerator;

/**
 * A generic pair class. It wraps two objects of arbitrary type. If you just
 * want to wrap a single object, look at class
 * {@link com.helger.commons.wrapper.Wrapper}.
 *
 * @author Philip Helger
 * @param <DATA1TYPE>
 *        First type.
 * @param <DATA2TYPE>
 *        Second type.
 */
@Immutable
public final class ReadOnlyPair <DATA1TYPE, DATA2TYPE> implements IPair <DATA1TYPE, DATA2TYPE>
{
  private final DATA1TYPE m_aFirst;
  private final DATA2TYPE m_aSecond;

  public <T2 extends DATA1TYPE, U2 extends DATA2TYPE> ReadOnlyPair (@Nullable final T2 aFirst,
                                                                    @Nullable final U2 aSecond)
  {
    m_aFirst = aFirst;
    m_aSecond = aSecond;
  }

  public ReadOnlyPair (@Nonnull final IPair <? extends DATA1TYPE, ? extends DATA2TYPE> rhs)
  {
    ValueEnforcer.notNull (rhs, "Pair");
    m_aFirst = rhs.getFirst ();
    m_aSecond = rhs.getSecond ();
  }

  @Nullable
  public DATA1TYPE getFirst ()
  {
    return m_aFirst;
  }

  @Nullable
  public DATA2TYPE getSecond ()
  {
    return m_aSecond;
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final ReadOnlyPair <?, ?> rhs = (ReadOnlyPair <?, ?>) o;
    return EqualsHelper.equals (m_aFirst, rhs.m_aFirst) && EqualsHelper.equals (m_aSecond, rhs.m_aSecond);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aFirst).append (m_aSecond).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (null).append ("first", m_aFirst).append ("second", m_aSecond).toString ();
  }

  @Nonnull
  public static <T, U> ReadOnlyPair <T, U> create (@Nullable final T aFirst, @Nullable final U aSecond)
  {
    return new ReadOnlyPair <T, U> (aFirst, aSecond);
  }
}
