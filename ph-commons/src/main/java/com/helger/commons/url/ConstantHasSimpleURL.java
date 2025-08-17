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
package com.helger.commons.url;

import com.helger.annotation.concurrent.Immutable;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.equals.EqualsHelper;
import com.helger.base.hashcode.HashCodeGenerator;
import com.helger.base.tostring.ToStringGenerator;

import jakarta.annotation.Nonnull;

/**
 * Implementation of {@link IHasSimpleURL} that has a constant URL.
 *
 * @author Philip Helger
 */
@Immutable
public class ConstantHasSimpleURL implements IHasSimpleURL
{
  private final ISimpleURL m_aURL;

  public ConstantHasSimpleURL (@Nonnull final ISimpleURL aURL)
  {
    m_aURL = ValueEnforcer.notNull (aURL, "URL");
  }

  @Nonnull
  public ISimpleURL getSimpleURL ()
  {
    return m_aURL;
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final ConstantHasSimpleURL rhs = (ConstantHasSimpleURL) o;
    return EqualsHelper.equals (m_aURL, rhs.m_aURL);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aURL).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (null).append ("URL", m_aURL).getToString ();
  }
}
