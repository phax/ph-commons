/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonempty;
import com.helger.annotation.misc.DevelopersNote;
import com.helger.base.id.IHasID;
import com.helger.base.lang.EnumHelper;
import com.helger.xml.EXMLVersion;

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
  @SuppressWarnings ("removal")
  @Deprecated (forRemoval = true, since = "12.1.1")
  @DevelopersNote ("See https://github.com/w3ctag/obsoletion/issues/6 for details")
  XML_11("xml11", EXMLVersion.XML_11),

  /** HTML4 and XHTML */
  HTML ("html", null);

  private final String m_sID;
  private final EXMLVersion m_eXMLVersion;

  EXMLSerializeVersion (@NonNull @Nonempty final String sID, @Nullable final EXMLVersion eXMLVersion)
  {
    m_sID = sID;
    m_eXMLVersion = eXMLVersion;
  }

  @NonNull
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
   * @return <code>null</code> if no XML version is required, the respective version string
   *         otherwise.
   */
  @Nullable
  public String getXMLVersionString ()
  {
    return m_eXMLVersion == null ? null : m_eXMLVersion.getVersion ();
  }

  /**
   * Get the {@link EXMLSerializeVersion} from the specified {@link EXMLVersion} .
   *
   * @param eXMLVersion
   *        XML version to query. May not be <code>null</code>.
   * @return Never <code>null</code>.
   */
  @SuppressWarnings ("removal")
  @NonNull
  public static EXMLSerializeVersion getFromXMLVersionOrThrow (@NonNull final EXMLVersion eXMLVersion)
  {
    return switch (eXMLVersion)
    {
      case XML_10 -> EXMLSerializeVersion.XML_10;
      case XML_11 -> EXMLSerializeVersion.XML_11;
      default -> throw new IllegalStateException ("Unsupported XML version " + eXMLVersion);
    };
  }

  @Nullable
  public static EXMLSerializeVersion getFromIDOrNull (@Nullable final String sID)
  {
    return EnumHelper.getFromIDOrNull (EXMLSerializeVersion.class, sID);
  }
}
