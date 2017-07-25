/**
 * Original copyright by Apache Software Foundation
 * Copyright (C) 2017 Philip Helger (www.helger.com)
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
package com.helger.cli.ex;

import javax.annotation.Nullable;

/**
 * Exception thrown during parsing signalling an unrecognized option was seen.
 */
public class UnrecognizedOptionException extends CommandLineParseException
{
  /** The unrecognized option */
  private final String m_sOption;

  /**
   * Construct a new <code>UnrecognizedArgumentException</code> with the
   * specified option and detail message.
   *
   * @param sMessage
   *        the detail message
   * @param sOption
   *        the unrecognized option
   * @since 1.2
   */
  public UnrecognizedOptionException (final String sMessage, @Nullable final String sOption)
  {
    super (sMessage);
    m_sOption = sOption;
  }

  /**
   * Returns the unrecognized option.
   *
   * @return the related option
   * @since 1.2
   */
  @Nullable
  public String getOption ()
  {
    return m_sOption;
  }
}
