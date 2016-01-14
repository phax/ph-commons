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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.string.StringHelper;
import com.helger.commons.string.ToStringGenerator;

/**
 * Default implementation of the {@link IResourceLocation} interface. The
 * implementation is immutable.
 *
 * @author Philip Helger
 */
@Immutable
public class ResourceLocation implements IResourceLocation
{
  private final String m_sResourceID;
  private final int m_nLineNumber;
  private final int m_nColumnNumber;
  private final String m_sField;

  public ResourceLocation (@Nullable final String sResourceID)
  {
    this (sResourceID, (String) null);
  }

  public ResourceLocation (@Nullable final String sResourceID, @Nullable final String sField)
  {
    this (sResourceID, ILLEGAL_NUMBER, ILLEGAL_NUMBER, sField);
  }

  public ResourceLocation (@Nullable final String sResourceID, final int nLineNumber, final int nColumnNumber)
  {
    this (sResourceID, nLineNumber, nColumnNumber, null);
  }

  public ResourceLocation (@Nullable final String sResourceID,
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
    if (StringHelper.hasText (m_sResourceID))
      ret += m_sResourceID;
    if (m_nLineNumber != ILLEGAL_NUMBER)
    {
      if (m_nColumnNumber != ILLEGAL_NUMBER)
        ret += "(" + m_nLineNumber + ":" + m_nColumnNumber + ")";
      else
        ret += "(" + m_nLineNumber + ":?)";
    }
    if (StringHelper.hasText (m_sField))
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
    final ResourceLocation rhs = (ResourceLocation) o;
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
