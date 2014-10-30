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
package com.helger.commons.collections.triple;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.equals.EqualsUtils;
import com.helger.commons.hash.HashCodeGenerator;
import com.helger.commons.string.ToStringGenerator;

/**
 * A generic triple class. It wraps three objects of arbitrary type. If you only
 * want two objects, look at the class
 * {@link com.helger.commons.collections.pair.ReadonlyPair}. If you just want to
 * wrap a single object, look at class
 * {@link com.helger.commons.mutable.Wrapper} .
 *
 * @author Philip Helger
 * @param <DATA1TYPE>
 *        First type.
 * @param <DATA2TYPE>
 *        Second type.
 * @param <DATA3TYPE>
 *        Third type.
 */
@Immutable
public final class ReadonlyTriple <DATA1TYPE, DATA2TYPE, DATA3TYPE> implements IReadonlyTriple <DATA1TYPE, DATA2TYPE, DATA3TYPE>
{
  private final DATA1TYPE m_aFirst;
  private final DATA2TYPE m_aSecond;
  private final DATA3TYPE m_aThird;

  public <T2 extends DATA1TYPE, U2 extends DATA2TYPE, V2 extends DATA3TYPE> ReadonlyTriple (@Nullable final T2 aFirst,
                                                                                            @Nullable final U2 aSecond,
                                                                                            @Nullable final V2 aThird)
  {
    m_aFirst = aFirst;
    m_aSecond = aSecond;
    m_aThird = aThird;
  }

  public ReadonlyTriple (@Nonnull final IReadonlyTriple <? extends DATA1TYPE, ? extends DATA2TYPE, ? extends DATA3TYPE> rhs)
  {
    ValueEnforcer.notNull (rhs, "Triple");
    m_aFirst = rhs.getFirst ();
    m_aSecond = rhs.getSecond ();
    m_aThird = rhs.getThird ();
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

  @Nullable
  public DATA3TYPE getThird ()
  {
    return m_aThird;
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final ReadonlyTriple <?, ?, ?> rhs = (ReadonlyTriple <?, ?, ?>) o;
    return EqualsUtils.equals (m_aFirst, rhs.m_aFirst) &&
           EqualsUtils.equals (m_aSecond, rhs.m_aSecond) &&
           EqualsUtils.equals (m_aThird, rhs.m_aThird);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aFirst).append (m_aSecond).append (m_aThird).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (null).append ("first", m_aFirst)
                                       .append ("second", m_aSecond)
                                       .append ("third", m_aThird)
                                       .toString ();
  }

  @Nonnull
  public static <T, U, V> ReadonlyTriple <T, U, V> create (@Nullable final T aFirst,
                                                           @Nullable final U aSecond,
                                                           @Nullable final V aThird)
  {
    return new ReadonlyTriple <T, U, V> (aFirst, aSecond, aThird);
  }
}
