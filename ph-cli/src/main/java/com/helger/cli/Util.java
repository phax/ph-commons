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
package com.helger.cli;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;

/**
 * Contains useful helper methods for classes within this package.
 */
final class Util
{
  static
  {
    ValueEnforcer.isTrue (CommandLineParser.PREFIX_LONG_OPT.length () == 2, "Internal inconsistency");
    ValueEnforcer.isTrue (CommandLineParser.PREFIX_SHORT_OPT.length () == 1, "Internal inconsistency");
  }

  private Util ()
  {}

  /**
   * Remove the hyphens from the beginning of <code>str</code> and return the
   * new String.
   *
   * @param sStr
   *        The string from which the hyphens should be removed.
   * @return the new String.
   */
  @Nullable
  static String stripLeadingHyphens (@Nullable final String sStr)
  {
    if (sStr == null)
      return null;
    if (sStr.startsWith (CommandLineParser.PREFIX_LONG_OPT))
      return sStr.substring (2, sStr.length ());
    if (sStr.startsWith (CommandLineParser.PREFIX_SHORT_OPT))
      return sStr.substring (1, sStr.length ());
    return sStr;
  }

  /**
   * Remove the leading and trailing quotes from <code>str</code>. E.g. if str
   * is '"one two"', then 'one two' is returned.
   *
   * @param sStr
   *        The string from which the leading and trailing quotes should be
   *        removed.
   * @return The string without the leading and trailing quotes.
   */
  @Nonnull
  static String stripLeadingAndTrailingQuotes (@Nonnull final String sStr)
  {
    final int nLen = sStr.length ();
    if (nLen > 1 && sStr.startsWith ("\"") && sStr.endsWith ("\""))
    {
      final String sInBetween = sStr.substring (1, nLen - 1);
      if (sInBetween.indexOf ('"') < 0)
        return sInBetween;
    }
    return sStr;
  }
}
