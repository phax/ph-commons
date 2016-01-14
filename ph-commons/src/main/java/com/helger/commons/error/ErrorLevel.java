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
package com.helger.commons.error;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.string.ToStringGenerator;

/**
 * A standalone implementation of the {@link IErrorLevel} interface.
 *
 * @author Philip Helger
 */
@Immutable
public class ErrorLevel implements IErrorLevel
{
  private final String m_sID;
  private final int m_nNumericLevel;

  public ErrorLevel (@Nonnull @Nonempty final String sID, @Nonnegative final int nNumericLevel)
  {
    m_sID = ValueEnforcer.notEmpty (sID, "ID");
    m_nNumericLevel = nNumericLevel;
  }

  @Nonnull
  @Nonempty
  public String getID ()
  {
    return m_sID;
  }

  @Nonnegative
  public int getNumericLevel ()
  {
    return m_nNumericLevel;
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final ErrorLevel rhs = (ErrorLevel) o;
    return m_sID.equals (rhs.m_sID) && m_nNumericLevel == rhs.m_nNumericLevel;
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_sID).append (m_nNumericLevel).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("ID", m_sID).append ("numericLevel", m_nNumericLevel).toString ();
  }
}
