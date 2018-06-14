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

/**
 * Validates an Option string.
 *
 * @since 1.1
 */
final class OptionValidator
{
  private OptionValidator ()
  {}

  /**
   * Validates whether <code>opt</code> is a permissible Option shortOpt. The
   * rules that specify if the <code>opt</code> is valid are:
   * <ul>
   * <li>a single character <code>opt</code> that is either ' '(special case),
   * '?', '@' or a letter</li>
   * <li>a multi character <code>opt</code> that only contains letters.</li>
   * </ul>
   * <p>
   * In case {@code opt} is {@code null} no further validation is performed.
   *
   * @param sOpt
   *        The option string to validate, may not be <code>null</code>.
   * @throws IllegalArgumentException
   *         if the Option is not valid.
   */
  static void validateShortOption (@Nonnull final String sOpt)
  {
    // handle the single character opt
    if (sOpt.length () == 1)
    {
      final char ch = sOpt.charAt (0);

      if (!_isValidShortOpt (ch))
        throw new IllegalArgumentException ("Illegal option name '" + ch + "'");
    }
    else
    {
      // handle the multi character opt
      for (final char ch : sOpt.toCharArray ())
        if (!_isValidChar (ch))
        {
          throw new IllegalArgumentException ("The option '" +
                                              sOpt +
                                              "' contains the illegal " +
                                              "character '" +
                                              ch +
                                              "'");
        }
    }
  }

  /**
   * Returns whether the specified character is a valid Option.
   *
   * @param c
   *        the option to validate
   * @return true if <code>c</code> is a letter, '?' or '@', otherwise false.
   */
  private static boolean _isValidShortOpt (final char c)
  {
    return _isValidChar (c) || c == '?' || c == '@';
  }

  /**
   * Returns whether the specified character is a valid character.
   *
   * @param c
   *        the character to validate
   * @return true if <code>c</code> is a letter.
   */
  private static boolean _isValidChar (final char c)
  {
    return Character.isJavaIdentifierPart (c);
  }
}
