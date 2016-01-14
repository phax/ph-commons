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
package com.helger.commons.aggregate;

import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.string.StringHelper;
import com.helger.commons.string.ToStringGenerator;

/**
 * A simple combinator that concatenates 2 strings using a common separator.
 *
 * @author Philip Helger
 */
@Immutable
public final class AggregatorStringWithSeparator implements IAggregator <String, String>
{
  private final String m_sSep;

  public AggregatorStringWithSeparator (@Nonnull final String sSep)
  {
    m_sSep = ValueEnforcer.notNull (sSep, "Separator");
  }

  @Nonnull
  public String getSeparator ()
  {
    return m_sSep;
  }

  @Nonnull
  public String aggregate (@Nonnull final Collection <String> aObjects)
  {
    return StringHelper.getImploded (m_sSep, aObjects);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final AggregatorStringWithSeparator rhs = (AggregatorStringWithSeparator) o;
    return m_sSep.equals (rhs.m_sSep);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_sSep).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("sep", m_sSep).toString ();
  }
}
