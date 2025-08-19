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
package com.helger.xml.serialize.write;

import com.helger.annotation.Nonempty;
import com.helger.base.id.IHasID;
import com.helger.base.lang.EnumHelper;
import com.helger.xml.EXMLVersion;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * XML serialization version.
 *
 * @author Philip Helger
 */
public enum EXMLSerializeVersion implements IHasID <String>
{
  /** XML 1.0 */
  XML_10 ("xml10", EXMLVersion.XML_10),

  /** XML 1.1 */
  XML_11 ("xml11", EXMLVersion.XML_11),

  /** HTML4 and XHTML */
  HTML ("html", null);

  private final String m_sID;
  private final EXMLVersion m_eXMLVersion;

  EXMLSerializeVersion (@Nonnull @Nonempty final String sID, @Nullable final EXMLVersion eXMLVersion)
  {
    m_sID = sID;
    m_eXMLVersion = eXMLVersion;
  }

  @Nonnull
  @Nonempty
  public String getID ()
  {
    return m_sID;
  }

  /**
   * @return <code>true</code> if this is an XML version (1.0 or 1.1)
   */
  public boolean isXML ()
  {
    return this == XML_10 || this == XML_11;
  }

  /**
   * @return <code>true</code> if this is HTML (HTML or XHTML)
   */
  public boolean isHTML ()
  {
    return this == HTML;
  }

  @Nullable
  public EXMLVersion getXMLVersionOrDefault (@Nullable final EXMLVersion eDefault)
  {
    // May be null for HTML so use the provided default
    return m_eXMLVersion == null ? eDefault : m_eXMLVersion;
  }

  /**
   * @return <code>null</code> if no XML version is required, the respective
   *         version string otherwise.
   */
  @Nullable
  public String getXMLVersionString ()
  {
    return m_eXMLVersion == null ? null : m_eXMLVersion.getVersion ();
  }

  /**
   * Get the {@link EXMLSerializeVersion} from the specified {@link EXMLVersion}
   * .
   *
   * @param eXMLVersion
   *        XML version to query. May not be <code>null</code>.
   * @return Never <code>null</code>.
   */
  @Nonnull
  public static EXMLSerializeVersion getFromXMLVersionOrThrow (@Nonnull final EXMLVersion eXMLVersion)
  {
    switch (eXMLVersion)
    {
      case XML_10:
        return EXMLSerializeVersion.XML_10;
      case XML_11:
        return EXMLSerializeVersion.XML_11;
      default:
        throw new IllegalStateException ("Unsupported XML version " + eXMLVersion);
    }
  }

  @Nullable
  public static EXMLSerializeVersion getFromIDOrNull (@Nullable final String sID)
  {
    return EnumHelper.getFromIDOrNull (EXMLSerializeVersion.class, sID);
  }
}
