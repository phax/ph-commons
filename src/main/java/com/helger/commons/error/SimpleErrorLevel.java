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
public class SimpleErrorLevel implements IErrorLevel
{
  private final String m_sID;
  private final int m_nNumericLevel;

  public SimpleErrorLevel (@Nonnull @Nonempty final String sID, @Nonnegative final int nNumericLevel)
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

  /*
   * Only SUCCESS is considered to be a success
   */
  public boolean isSuccess ()
  {
    return isEqualSevereThan (EErrorLevel.SUCCESS);
  }

  /*
   * Everything except SUCCESS is considered a failure!
   */
  public boolean isFailure ()
  {
    return !isSuccess ();
  }

  /**
   * @return <code>true</code> if the severity of this item is &ge; than
   *         {@link EErrorLevel#ERROR}.
   */
  public boolean isError ()
  {
    return isMoreOrEqualSevereThan (EErrorLevel.ERROR);
  }

  /**
   * @return <code>true</code> if the severity of this item is &lt; than
   *         {@link EErrorLevel#ERROR}.
   */
  public boolean isNoError ()
  {
    return isLessSevereThan (EErrorLevel.ERROR);
  }

  @Nonnegative
  public int getNumericLevel ()
  {
    return m_nNumericLevel;
  }

  public boolean isEqualSevereThan (@Nonnull final IErrorLevel aErrorLevel)
  {
    return getNumericLevel () == aErrorLevel.getNumericLevel ();
  }

  public boolean isLessSevereThan (@Nonnull final IErrorLevel aErrorLevel)
  {
    return getNumericLevel () < aErrorLevel.getNumericLevel ();
  }

  public boolean isLessOrEqualSevereThan (@Nonnull final IErrorLevel aErrorLevel)
  {
    return getNumericLevel () <= aErrorLevel.getNumericLevel ();
  }

  public boolean isMoreSevereThan (@Nonnull final IErrorLevel aErrorLevel)
  {
    return getNumericLevel () > aErrorLevel.getNumericLevel ();
  }

  public boolean isMoreOrEqualSevereThan (@Nonnull final IErrorLevel aErrorLevel)
  {
    return getNumericLevel () >= aErrorLevel.getNumericLevel ();
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final SimpleErrorLevel rhs = (SimpleErrorLevel) o;
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
