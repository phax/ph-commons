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
package com.helger.base.name;

import com.helger.annotation.Nonempty;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.hashcode.HashCodeGenerator;

import jakarta.annotation.Nonnull;

public final class MockHasName implements IHasName, Comparable <MockHasName>
{
  private final String m_sName;

  public MockHasName (final int nID)
  {
    this (Integer.toString (nID));
  }

  public MockHasName (@Nonnull @Nonempty final String sName)
  {
    m_sName = ValueEnforcer.notEmpty (sName, "Name");
  }

  @Nonnull
  @Nonempty
  public String getName ()
  {
    return m_sName;
  }

  public int compareTo (final MockHasName o)
  {
    return m_sName.compareTo (o.m_sName);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final MockHasName rhs = (MockHasName) o;
    return m_sName.equals (rhs.m_sName);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_sName).getHashCode ();
  }
}
