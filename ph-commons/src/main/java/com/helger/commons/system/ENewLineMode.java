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
package com.helger.commons.system;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.Nonempty;

/**
 * Determines the different newline modes for th different operating systems.
 *
 * @author Philip Helger
 */
public enum ENewLineMode
{
  /** Unix style - just \n (0x0A) */
  UNIX ("\n"),
  /** Mac style - just \r (0x0D) */
  MAC ("\r"),
  /** Windows style - \r\n (0x0D 0x0A) */
  WINDOWS ("\r\n");

  /** The default newline mode as indicated by the system properties */
  public static final ENewLineMode DEFAULT = getFromTextOrDefault (SystemProperties.getLineSeparator (),
                                                                   EOperatingSystem.getCurrentOS ().getNewLineMode ());

  private final String m_sText;

  private ENewLineMode (@Nonnull @Nonempty final String sText)
  {
    m_sText = sText;
  }

  /**
   * @return The textual representation of the new line in this mode. Neither
   *         <code>null</code> nor empty.
   */
  @Nonnull
  @Nonempty
  public String getText ()
  {
    return m_sText;
  }

  /**
   * @return <code>true</code> if this a Linux/Unix newline mode
   */
  public boolean isUnix ()
  {
    return this == UNIX;
  }

  /**
   * @return <code>true</code> if this a Mac newline mode
   */
  public boolean isMac ()
  {
    return this == MAC;
  }

  /**
   * @return <code>true</code> if this a Windows newline mode
   */
  public boolean isWindows ()
  {
    return this == WINDOWS;
  }

  @Nullable
  public static ENewLineMode getFromTextOrDefault (@Nullable final String sText, @Nullable final ENewLineMode eDefault)
  {
    if (sText != null && sText.length () > 0)
      for (final ENewLineMode e : values ())
        if (e.m_sText.equals (sText))
          return e;
    return eDefault;
  }

  @Nullable
  public static ENewLineMode getFromTextOrNull (@Nullable final String sText)
  {
    return getFromTextOrDefault (sText, null);
  }
}
