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
package com.helger.commons.mutable;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.CDefault;
import com.helger.commons.compare.CompareUtils;
import com.helger.commons.hash.HashCodeGenerator;
import com.helger.commons.state.EChange;
import com.helger.commons.string.ToStringGenerator;

/**
 * Object wrapper around a boolean so that it can be passed a final object but
 * is mutable.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public final class MutableBoolean implements IMutable <MutableBoolean>
{
  /** The default value if the default constructor is used. */
  public static final boolean DEFAULT_VALUE = CDefault.DEFAULT_BOOLEAN;

  private boolean m_bValue;

  public MutableBoolean ()
  {
    this (DEFAULT_VALUE);
  }

  public MutableBoolean (@Nonnull final Boolean aValue)
  {
    this (aValue.booleanValue ());
  }

  public MutableBoolean (final boolean bValue)
  {
    m_bValue = bValue;
  }

  public boolean booleanValue ()
  {
    return m_bValue;
  }

  @Nonnull
  public Boolean getAsBoolean ()
  {
    return Boolean.valueOf (m_bValue);
  }

  @Nonnull
  public EChange set (final boolean bValue)
  {
    if (m_bValue == bValue)
      return EChange.UNCHANGED;
    m_bValue = bValue;
    return EChange.CHANGED;
  }

  public int compareTo (@Nonnull final MutableBoolean rhs)
  {
    return CompareUtils.compareFalseBeforeTrue (m_bValue, rhs.m_bValue);
  }

  @Nonnull
  public MutableBoolean getClone ()
  {
    return new MutableBoolean (m_bValue);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final MutableBoolean rhs = (MutableBoolean) o;
    return m_bValue == rhs.m_bValue;
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_bValue).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("value", m_bValue).toString ();
  }
}
