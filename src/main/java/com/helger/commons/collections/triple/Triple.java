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
package com.helger.commons.collections.triple;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.ICloneable;
import com.helger.commons.ValueEnforcer;
import com.helger.commons.equals.EqualsUtils;
import com.helger.commons.hash.HashCodeGenerator;
import com.helger.commons.state.EChange;
import com.helger.commons.string.ToStringGenerator;

/**
 * A generic writable triple class. It wraps three objects of arbitrary type. If
 * you only want two objects, look at the class
 * {@link com.helger.commons.collections.pair.Pair}. If you just want to wrap a
 * single object, look at class {@link com.helger.commons.mutable.Wrapper}.
 *
 * @author Philip Helger
 * @param <DATA1TYPE>
 *        First type.
 * @param <DATA2TYPE>
 *        Second type.
 * @param <DATA3TYPE>
 *        Third type.
 */
@NotThreadSafe
public final class Triple <DATA1TYPE, DATA2TYPE, DATA3TYPE> implements IMutableTriple <DATA1TYPE, DATA2TYPE, DATA3TYPE>, ICloneable <Triple <DATA1TYPE, DATA2TYPE, DATA3TYPE>>
{
  private DATA1TYPE m_aFirst;
  private DATA2TYPE m_aSecond;
  private DATA3TYPE m_aThird;

  public <T2 extends DATA1TYPE, U2 extends DATA2TYPE, V2 extends DATA3TYPE> Triple (@Nullable final T2 aFirst,
                                                                                    @Nullable final U2 aSecond,
                                                                                    @Nullable final V2 aThird)
  {
    m_aFirst = aFirst;
    m_aSecond = aSecond;
    m_aThird = aThird;
  }

  public Triple (@Nonnull final IReadonlyTriple <? extends DATA1TYPE, ? extends DATA2TYPE, ? extends DATA3TYPE> rhs)
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

  @Nonnull
  public EChange setFirst (@Nullable final DATA1TYPE aFirst)
  {
    if (EqualsUtils.equals (aFirst, m_aFirst))
      return EChange.UNCHANGED;
    m_aFirst = aFirst;
    return EChange.CHANGED;
  }

  @Nullable
  public DATA2TYPE getSecond ()
  {
    return m_aSecond;
  }

  @Nonnull
  public EChange setSecond (@Nullable final DATA2TYPE aSecond)
  {
    if (EqualsUtils.equals (aSecond, m_aSecond))
      return EChange.UNCHANGED;
    m_aSecond = aSecond;
    return EChange.CHANGED;
  }

  @Nullable
  public DATA3TYPE getThird ()
  {
    return m_aThird;
  }

  @Nonnull
  public EChange setThird (@Nullable final DATA3TYPE aThird)
  {
    if (EqualsUtils.equals (aThird, m_aThird))
      return EChange.UNCHANGED;
    m_aThird = aThird;
    return EChange.CHANGED;
  }

  @Nonnull
  public Triple <DATA1TYPE, DATA2TYPE, DATA3TYPE> getClone ()
  {
    return new Triple <DATA1TYPE, DATA2TYPE, DATA3TYPE> (this);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final Triple <?, ?, ?> rhs = (Triple <?, ?, ?>) o;
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
  public static <T, U, V> Triple <T, U, V> create (@Nullable final T aFirst,
                                                   @Nullable final U aSecond,
                                                   @Nullable final V aThird)
  {
    return new Triple <T, U, V> (aFirst, aSecond, aThird);
  }
}
