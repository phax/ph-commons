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
package com.helger.xml.ls;

import com.helger.annotation.concurrent.Immutable;
import com.helger.base.string.ToStringGenerator;
import com.helger.commons.equals.EqualsHelperExt;
import com.helger.commons.hashcode.HashCodeGenerator;

import jakarta.annotation.Nullable;

/**
 * This class encapsulates all data provided for resource resolving.
 *
 * @author Philip Helger
 */
@Immutable
public class LSResourceData
{
  private final String m_sType;
  private final String m_sNamespaceURI;
  private final String m_sPublicID;
  private final String m_sSystemID;
  private final String m_sBaseURI;

  public LSResourceData (@Nullable final String sType,
                         @Nullable final String sNamespaceURI,
                         @Nullable final String sPublicId,
                         @Nullable final String sSystemId,
                         @Nullable final String sBaseURI)
  {
    m_sType = sType;
    m_sNamespaceURI = sNamespaceURI;
    m_sPublicID = sPublicId;
    m_sSystemID = sSystemId;
    m_sBaseURI = sBaseURI;
  }

  @Nullable
  public String getType ()
  {
    return m_sType;
  }

  @Nullable
  public String getNamespaceURI ()
  {
    return m_sNamespaceURI;
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

  @Nullable
  public String getBaseURI ()
  {
    return m_sBaseURI;
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final LSResourceData rhs = (LSResourceData) o;
    return EqualsHelperExt.extEquals (m_sType, rhs.m_sType) &&
           EqualsHelperExt.extEquals (m_sNamespaceURI, rhs.m_sNamespaceURI) &&
           EqualsHelperExt.extEquals (m_sPublicID, rhs.m_sPublicID) &&
           EqualsHelperExt.extEquals (m_sSystemID, rhs.m_sSystemID) &&
           EqualsHelperExt.extEquals (m_sBaseURI, rhs.m_sBaseURI);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_sType)
                                       .append (m_sNamespaceURI)
                                       .append (m_sPublicID)
                                       .append (m_sSystemID)
                                       .append (m_sBaseURI)
                                       .getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("type", m_sType)
                                       .append ("namespaceURI", m_sNamespaceURI)
                                       .append ("publicId", m_sPublicID)
                                       .append ("systemId", m_sSystemID)
                                       .append ("baseURI", m_sBaseURI)
                                       .getToString ();
  }
}
