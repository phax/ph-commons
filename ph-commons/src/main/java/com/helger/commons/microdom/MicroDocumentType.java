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
package com.helger.commons.microdom;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.string.ToStringGenerator;

/**
 * Default implementation of the {@link IMicroDocumentType} interface.
 *
 * @author Philip Helger
 */
public final class MicroDocumentType extends AbstractMicroNode implements IMicroDocumentType
{
  private final String m_sQualifiedName;
  private final String m_sPublicID;
  private final String m_sSystemID;

  public MicroDocumentType (@Nonnull final IMicroDocumentType rhs)
  {
    this (rhs.getQualifiedName (), rhs.getPublicID (), rhs.getSystemID ());
  }

  public MicroDocumentType (@Nonnull final String sQualifiedName,
                            @Nullable final String sPublicID,
                            @Nullable final String sSystemID)
  {
    ValueEnforcer.notEmpty (sQualifiedName, "QualifiedName");
    // publicID is null if an inline DTD is contained
    // systemID is also null if an inline DTD is contained

    m_sQualifiedName = sQualifiedName;
    m_sPublicID = sPublicID;
    m_sSystemID = sSystemID;
  }

  @Nonnull
  public EMicroNodeType getType ()
  {
    return EMicroNodeType.DOCUMENT_TYPE;
  }

  @Nonnull
  public String getNodeName ()
  {
    return "#doctype";
  }

  @Nonnull
  public String getQualifiedName ()
  {
    return m_sQualifiedName;
  }

  @Nullable
  public String getPublicID ()
  {
    return m_sPublicID;
  }

  @Nullable
  public String getSystemID ()
  {
    return m_sSystemID;
  }

  @Nonnull
  public IMicroDocumentType getClone ()
  {
    return new MicroDocumentType (this);
  }

  public boolean isEqualContent (@Nullable final IMicroNode o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final MicroDocumentType rhs = (MicroDocumentType) o;
    return m_sQualifiedName.equals (rhs.m_sQualifiedName) &&
           EqualsHelper.equals (m_sPublicID, rhs.m_sPublicID) &&
           EqualsHelper.equals (m_sSystemID, rhs.m_sSystemID);
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ())
                            .append ("qualifiedName", m_sQualifiedName)
                            .append ("publicID", m_sPublicID)
                            .append ("systemID", m_sSystemID)
                            .toString ();
  }
}
