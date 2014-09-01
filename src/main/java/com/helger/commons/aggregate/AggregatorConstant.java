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
package com.helger.commons.aggregate;

import java.util.Collection;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.equals.EqualsUtils;
import com.helger.commons.hash.HashCodeGenerator;
import com.helger.commons.string.ToStringGenerator;

/**
 * Always returns <code>null</code>.
 * 
 * @author Philip Helger
 */
@Immutable
public final class AggregatorConstant <SRCTYPE, DSTTYPE> implements IAggregator <SRCTYPE, DSTTYPE>
{
  private final DSTTYPE m_aValue;

  public AggregatorConstant (@Nullable final DSTTYPE aValue)
  {
    m_aValue = aValue;
  }

  @Nullable
  public DSTTYPE aggregate (@Nullable final Collection <SRCTYPE> aResults)
  {
    return m_aValue;
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (!(o instanceof AggregatorConstant <?, ?>))
      return false;
    final AggregatorConstant <?, ?> rhs = (AggregatorConstant <?, ?>) o;
    return EqualsUtils.equals (m_aValue, rhs.m_aValue);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aValue).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("value", m_aValue).toString ();
  }
}
