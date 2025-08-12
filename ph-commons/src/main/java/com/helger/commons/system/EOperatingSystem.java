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
package com.helger.commons.system;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.Nonempty;
import com.helger.commons.name.IHasDisplayName;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * This enumeration is used to determine the currently running Operating system.
 *
 * @author Philip Helger
 */
public enum EOperatingSystem implements IHasDisplayName
{
  /**
   * The value indicating the AIX operating system.
   */
  AIX ("AIX", true),

  /**
   * The value indicating the FreeBSD operating system.
   */
  FREEBSD ("FreeBSD", true),

  /**
   * The value indicating the HP-UX operating system.
   */
  HPUX ("HP-UX", true),

  /**
   * The value indicating the Linux operating system.
   */
  LINUX ("Linux", true),

  /**
   * The value indicating the Mac OS X operating system.
   */
  MACOS ("Mac OS X", true),

  /**
   * The value indicating the Solaris operating system.
   */
  SOLARIS ("Solaris", true),

  /**
   * The value indicating the Windows operating system.
   */
  WINDOWS ("Windows", false),

  /**
   * The value indicating the z/OS operating system.
   */
  ZOS ("z/OS", true),

  /**
   * The value indicating an unknown operating system.
   */
  UNKNOWN ("Unknown", false);

  private static final Logger LOGGER = LoggerFactory.getLogger (EOperatingSystem.class);

  /** The current OS. */
  private static final EOperatingSystem INSTANCE;

  static
  {
    final String sCurrentOSName = getCurrentOSName ();
    INSTANCE = forName (sCurrentOSName);
    if (INSTANCE == UNKNOWN)
      LOGGER.error ("Failed to resolve operating system from name '" + sCurrentOSName + "'!!!");
  }

  /** The human-readable name for this operating system. */
  private final String m_sDisplayName;

  private final boolean m_bUnixBased;

  /**
   * Creates a new operating system value with the provided name.
   *
   * @param sDisplayName
   *        The human-readable name for the operating system.
   * @param bUnixBased
   *        <code>true</code> if this OS is Unix based, <code>false</code> if
   *        not
   */
  EOperatingSystem (@Nonnull @Nonempty final String sDisplayName, final boolean bUnixBased)
  {
    m_sDisplayName = sDisplayName;
    m_bUnixBased = bUnixBased;
  }

  @Nonnull
  @Nonempty
  public String getDisplayName ()
  {
    return m_sDisplayName;
  }

  /**
   * Indicates whether the provided operating system is UNIX-based. UNIX-based
   * operating systems include:
   * <ul>
   * <li>AIX</li>
   * <li>FreeBSD</li>
   * <li>HP-UX</li>
   * <li>Linux</li>
   * <li>Mac OS X</li>
   * <li>Solaris</li>
   * </ul>
   *
   * @return <code>true</code> if the provided operating system is UNIX-based,
   *         or <code>false</code> if not.
   */
  public boolean isUnixBased ()
  {
    return m_bUnixBased;
  }

  /**
   * Indicates whether the provided operating system is Windows-based.
   * Windows-based operating systems include:
   * <ul>
   * <li>Windows</li>
   * </ul>
   *
   * @return <code>true</code> if the provided operating system is
   *         Windows-based, or <code>false</code> if not.
   */
  public boolean isWindowsBased ()
  {
    return this == WINDOWS;
  }

  /**
   * Indicates whether the provided operating system is MacOS-based. MacOS-based
   * operating systems include:
   * <ul>
   * <li>MacOS</li>
   * </ul>
   *
   * @return <code>true</code> if the provided operating system is MacOS-based,
   *         or <code>false</code> if not.
   */
  public boolean isMacOSBased ()
  {
    return this == MACOS;
  }

  /**
   * @return <code>true</code> if the this OS is the current OS.
   */
  public boolean isCurrentOS ()
  {
    return this == getCurrentOS ();
  }

  /**
   * @return The newline mode matching this operating system.
   */
  @Nonnull
  public ENewLineMode getNewLineMode ()
  {
    if (isWindowsBased ())
      return ENewLineMode.WINDOWS;
    if (isMacOSBased ())
      return ENewLineMode.MAC;
    return ENewLineMode.UNIX;
  }

  /**
   * Retrieves the operating system for the provided name. The name provided
   * should come from the <code>os.name</code> system property.
   *
   * @param sOSName
   *        The name for which to retrieve the corresponding operating system.
   * @return The operating system for the provided name. If the operating system
   *         could not be determined, {@link #UNKNOWN} is returned and never
   *         <code>null</code>.
   */
  @Nonnull
  public static EOperatingSystem forName (@Nullable final String sOSName)
  {
    if (sOSName == null)
      return UNKNOWN;

    final String sLowerName = sOSName.toLowerCase (Locale.US);
    if (sLowerName.contains ("solaris") || sLowerName.contains ("sunos"))
      return SOLARIS;
    if (sLowerName.contains ("linux"))
      return LINUX;
    if (sLowerName.contains ("hp-ux") || sLowerName.contains ("hp ux") || sLowerName.contains ("hpux"))
      return HPUX;
    if (sLowerName.contains ("aix"))
      return AIX;
    if (sLowerName.contains ("windows"))
      return WINDOWS;
    if (sLowerName.contains ("freebsd") || sLowerName.contains ("free bsd"))
      return FREEBSD;
    if (sLowerName.contains ("macos") || sLowerName.contains ("mac os"))
      return MACOS;
    if (sLowerName.contains ("z/os"))
      return ZOS;
    return UNKNOWN;
  }

  /**
   * @return The current OS. Never <code>null</code>.
   */
  @Nonnull
  public static EOperatingSystem getCurrentOS ()
  {
    return INSTANCE;
  }

  /**
   * @return The name of the current operating system.
   */
  @Nullable
  public static String getCurrentOSName ()
  {
    return SystemProperties.getOsName ();
  }

  /**
   * @return The version of the current operating system.
   */
  @Nullable
  public static String getCurrentOSVersion ()
  {
    return SystemProperties.getOsVersion ();
  }
}
