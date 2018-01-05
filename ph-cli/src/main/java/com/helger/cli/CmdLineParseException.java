/**
 * Original copyright by Apache Software Foundation
 * Copyright (C) 2017-2018 Philip Helger (www.helger.com)
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
package com.helger.cli;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Base class for commandline parse exceptions. Is a checked exception, as
 * invalid values are possible from the user side, so it is advisable to catch
 * them any way!
 *
 * @author Philip Helger
 */
public class CmdLineParseException extends Exception
{
  private final ECmdLineParseError m_eError;
  private final Option m_aOption;
  private final OptionGroup m_aOptionGroup;

  /**
   * Constructor.
   *
   * @param eError
   *        Error code. May not be <code>null</code>.
   * @param aOption
   *        Affected option. May not be <code>null</code>.
   * @param sMessage
   *        the detail message
   */
  public CmdLineParseException (@Nonnull final ECmdLineParseError eError,
                                @Nonnull final Option aOption,
                                @Nonnull final String sMessage)
  {
    super (sMessage);
    m_eError = eError;
    m_aOption = aOption;
    m_aOptionGroup = null;
  }

  /**
   * Constructor.
   *
   * @param eError
   *        Error code. May not be <code>null</code>.
   * @param aOptionGroup
   *        Affected option group. May not be <code>null</code>.
   * @param sMessage
   *        the detail message
   */
  public CmdLineParseException (@Nonnull final ECmdLineParseError eError,
                                @Nonnull final OptionGroup aOptionGroup,
                                @Nonnull final String sMessage)
  {
    super (sMessage);
    m_eError = eError;
    m_aOption = null;
    m_aOptionGroup = aOptionGroup;
  }

  @Nonnull
  public ECmdLineParseError getError ()
  {
    return m_eError;
  }

  @Nullable
  public Option getOption ()
  {
    return m_aOption;
  }

  @Nullable
  public OptionGroup getOptionGroup ()
  {
    return m_aOptionGroup;
  }
}
