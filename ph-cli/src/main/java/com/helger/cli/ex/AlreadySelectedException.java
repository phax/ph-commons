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
import com.helger.cli.OptionGroup;

/**
 * Thrown when more than one option in an option group has been provided.
 */
public class AlreadySelectedException extends CommandLineParseException
{
  /** The option group selected. */
  private final OptionGroup m_aGroup;

  /** The option that triggered the exception. */
  private final Option m_aOption;

  /**
   * Construct a new <code>AlreadySelectedException</code> for the specified
   * option group.
   *
   * @param aGroup
   *        the option group already selected
   * @param aOption
   *        the option that triggered the exception
   * @since 1.2
   */
  public AlreadySelectedException (@Nonnull final OptionGroup aGroup, @Nonnull final Option aOption)
  {
    super ("The option '" +
           aOption.getKey () +
           "' was specified but an option from this group " +
           "has already been selected: '" +
           aGroup.getSelected () +
           "'");
    m_aGroup = aGroup;
    m_aOption = aOption;
  }

  /**
   * Returns the option group where another option has been selected.
   *
   * @return the related option group
   * @since 1.2
   */
  @Nonnull
  public OptionGroup getOptionGroup ()
  {
    return m_aGroup;
  }

  /**
   * Returns the option that was added to the group and triggered the exception.
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
