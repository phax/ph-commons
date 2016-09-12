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

import javax.annotation.Nullable;

import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.error.location.ErrorLocation;
import com.helger.commons.hashcode.HashCodeGenerator;

/**
 * @author Philip Helger
 * @deprecated Replaced with {@link ErrorLocation}
 */
@Deprecated
public class ResourceLocation extends ErrorLocation implements IResourceLocation
{
  private final String m_sField;

  public ResourceLocation (@Nullable final String sResourceID)
  {
    this (sResourceID, ILLEGAL_NUMBER, ILLEGAL_NUMBER, null);
  }

  public ResourceLocation (@Nullable final String sResourceID, final String sField)
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
    super (sResourceID, nLineNumber, nColumnNumber);
    m_sField = sField;
  }

  @Nullable
  public String getField ()
  {
    return m_sField;
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (!super.equals (o))
      return false;
    final ResourceLocation rhs = (ResourceLocation) o;
    return EqualsHelper.equals (m_sField, rhs.m_sField);
  }

  @Override
  public int hashCode ()
  {
    return HashCodeGenerator.getDerived (super.hashCode ()).append (m_sField).getHashCode ();
  }
}
