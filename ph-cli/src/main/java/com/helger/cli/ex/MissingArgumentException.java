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

import javax.annotation.Nonnull;

import com.helger.cli.Option;

/**
 * Thrown when an option requiring an argument is not provided with an argument.
 */
public class MissingArgumentException extends CommandLineParseException
{
  /** The option requiring additional arguments */
  private final Option m_aOption;

  /**
   * Construct a new <code>MissingArgumentException</code> with the specified
   * detail message.
   *
   * @param aOption
   *        the option requiring an argument
   * @since 1.2
   */
  public MissingArgumentException (@Nonnull final Option aOption)
  {
    super ("Missing argument for option: " + aOption.getKey ());
    m_aOption = aOption;
  }

  /**
   * Return the option requiring an argument that wasn't provided on the command
   * line.
   *
   * @return the related option
   * @since 1.2
   */
  @Nonnull
  public Option getOption ()
  {
    return m_aOption;
  }
}
