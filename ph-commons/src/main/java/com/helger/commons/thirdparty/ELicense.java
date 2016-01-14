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
package com.helger.commons.thirdparty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.lang.EnumHelper;
import com.helger.commons.version.Version;

/**
 * Represents a bunch of Open Source licenses regularly used.
 *
 * @author Philip Helger
 */
public enum ELicense implements ILicense
{
  // Apache
  APACHE1 ("apache1", "Apache License", new Version (1, 0), "http://www.apache.org/licenses/LICENSE-1.0.txt"),
  APACHE2 ("apache2", "Apache License", new Version (2, 0), "http://www.apache.org/licenses/LICENSE-2.0.txt"),
  // Mozilla
  MPL10 ("mpl10", "Mozilla Public License", new Version (1, 0), "http://www.mozilla.org/MPL/1.0/"),
  MPL11 ("mpl11", "Mozilla Public License", new Version (1, 1), "http://www.mozilla.org/MPL/1.1/"),
  MPL20 ("mpl20", "Mozilla Public License Version 2.0", new Version (2, 0), "http://www.mozilla.org/MPL/2.0/"),
  // BSD/MIT
  // BSD 2-Clause "Simplified" or "FreeBSD" license
  BSD ("bsd", "BSD 2-Clause License", null, "http://opensource.org/licenses/BSD-2-Clause"),
  // BSD 3-Clause "New" or "Revised" license
  BSD3 ("bsd3", "BSD 3-Clause License", null, "http://opensource.org/licenses/BSD-3-Clause"),
  MIT ("mit", "MIT License", null, "http://opensource.org/licenses/MIT"),
  // GNU General Public License (GPL)
  GPL10 ("gpl10", "GNU General Public License", new Version (1, 0), "http://www.gnu.org/licenses/gpl-1.0"),
  GPL20 ("gpl20", "GNU General Public License", new Version (2, 0), "http://www.gnu.org/licenses/gpl-2.0"),
  GPL30 ("gpl30", "GNU General Public License", new Version (3, 0), "http://www.gnu.org/licenses/gpl-3.0"),
  // GNU Lesser General Public License (LGPL)
  LGPL20 ("lgpl20", "GNU Library General Public License", new Version (2,
                                                                       0), "http://www.gnu.org/licenses/old-licenses/lgpl-2.0.html"),
  LGPL21 ("lgpl21", "GNU Lesser General Public License", new Version (2,
                                                                      1), "http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html"),
  LGPL30 ("lgpl30", "GNU Lesser General Public License", new Version (3, 0), "http://www.gnu.org/licenses/lgpl.html"),
  // GNU Affero General Public License (AGPL)
  AGPL30 ("agpl30", "GNU Affero General Public License", new Version (3, 0), "http://www.gnu.org/licenses/agpl-3.0"),
  // GNU Free Documentation License (FDL)
  FDL11 ("fdl11", "GNU Free Documentation License", new Version (1, 1), "http://www.gnu.org/licenses/fdl-1.1"),
  FDL12 ("fdl12", "GNU Free Documentation License", new Version (1, 2), "http://www.gnu.org/licenses/fdl-1.2"),
  FDL13 ("fdl13", "GNU Free Documentation License", new Version (1, 3), "http://www.gnu.org/licenses/fdl-1.3"),
  // Eclipse Public License
  EPL10 ("epl10", "Eclipse Public License", new Version (1, 0), "http://www.eclipse.org/legal/epl-v10.html"),
  // EUPL
  EUPL11 ("eupl11", "The European Union Public License", new Version (1,
                                                                      1), "https://joinup.ec.europa.eu/software/page/eupl/licence-eupl"),
  EUPL12 ("eupl12", "The European Union Public License", new Version (1,
                                                                      2), "https://joinup.ec.europa.eu/software/page/eupl/licence-eupl"),
  // Creative commons
  CCBY10 ("ccby10", "Creative Commons Attributation", new Version (1,
                                                                   0), "http://creativecommons.org/licenses/by/1.0/"),
  CCBY20 ("ccby20", "Creative Commons Attributation", new Version (2,
                                                                   0), "http://creativecommons.org/licenses/by/2.0/"),
  CCBY25 ("ccby25", "Creative Commons Attributation", new Version (2,
                                                                   5), "http://creativecommons.org/licenses/by/2.5/"),
  CCBY30 ("ccby30", "Creative Commons Attributation", new Version (3,
                                                                   0), "http://creativecommons.org/licenses/by/3.0/"),
  CCBY40 ("ccby40", "Creative Commons Attributation", new Version (4,
                                                                   0), "http://creativecommons.org/licenses/by/4.0/"),
  // Public domain
  PUBLIC_DOMAIN ("pd", "Public Domain", null, null);

  private final String m_sID;
  private final String m_sName;
  private final Version m_aVersion;
  private final String m_sURL;

  /**
   * Create a custom license.
   *
   * @param sID
   *        The ID of the license.
   * @param sName
   *        The name of the license.
   * @param aVersion
   *        The version of the license.
   * @param sURL
   *        The URL of the license.
   */
  private ELicense (@Nonnull @Nonempty final String sID,
                    @Nonnull @Nonempty final String sName,
                    @Nullable final Version aVersion,
                    @Nullable final String sURL)
  {
    m_sID = sID;
    m_sName = sName;
    m_aVersion = aVersion;
    m_sURL = sURL;
  }

  @Nonnull
  @Nonempty
  public String getID ()
  {
    return m_sID;
  }

  @Nonnull
  @Nonempty
  public String getDisplayName ()
  {
    return m_sName;
  }

  @Nullable
  public Version getVersion ()
  {
    return m_aVersion;
  }

  @Nullable
  public String getURL ()
  {
    return m_sURL;
  }

  @Nullable
  public static ELicense getFromIDOrNull (@Nullable final String sID)
  {
    return EnumHelper.getFromIDOrNull (ELicense.class, sID);
  }
}
