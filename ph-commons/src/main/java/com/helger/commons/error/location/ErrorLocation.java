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
package com.helger.commons.error.location;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.string.ToStringGenerator;

/**
 * Default implementation of the {@link IErrorLocation} interface. The
 * implementation is immutable.
 *
 * @author Philip Helger
 */
@Immutable
public class ErrorLocation implements IErrorLocation
{
  /** A constant representing no location */
  public static final ErrorLocation NO_LOCATION = new ErrorLocation (null, ILLEGAL_NUMBER, ILLEGAL_NUMBER, null);

  private final String m_sResourceID;
  private final int m_nLineNumber;
  private final int m_nColumnNumber;
  private final String m_sField;

  public ErrorLocation (@Nullable final String sResourceID,
                        final int nLineNumber,
                        final int nColumnNumber,
                        @Nullable final String sField)
  {
    m_sResourceID = sResourceID;
    m_nLineNumber = nLineNumber;
    m_nColumnNumber = nColumnNumber;
    m_sField = sField;
  }

  @Nullable
  public String getResourceID ()
  {
    return m_sResourceID;
  }

  public int getLineNumber ()
  {
    return m_nLineNumber;
  }

  public int getColumnNumber ()
  {
    return m_nColumnNumber;
  }

  @Nullable
  public String getField ()
  {
    return m_sField;
  }

  @Nonnull
  public String getAsString ()
  {
    String ret = "";
    if (hasResourceID ())
      ret += m_sResourceID;

    if (hasLineNumber ())
    {
      if (hasColumnNumber ())
        ret += "(" + m_nLineNumber + ":" + m_nColumnNumber + ")";
      else
        ret += "(" + m_nLineNumber + ":?)";
    }

    if (hasField ())
    {
      if (ret.length () > 0)
        ret += " @ ";
      ret += m_sField;
    }
    return ret;
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final ErrorLocation rhs = (ErrorLocation) o;
    return EqualsHelper.equals (m_sResourceID, rhs.m_sResourceID) &&
           m_nLineNumber == rhs.m_nLineNumber &&
           m_nColumnNumber == rhs.m_nColumnNumber &&
           EqualsHelper.equals (m_sField, rhs.m_sField);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_sResourceID)
                                       .append (m_nLineNumber)
                                       .append (m_nColumnNumber)
                                       .append (m_sField)
                                       .getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("resourceID", m_sResourceID)
                                       .append ("lineNumber", m_nLineNumber)
                                       .append ("columnNumber", m_nColumnNumber)
                                       .appendIfNotNull ("field", m_sField)
                                       .toString ();
  }
}
