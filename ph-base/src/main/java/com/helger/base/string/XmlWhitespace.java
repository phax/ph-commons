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
package com.helger.base.string;

import org.jspecify.annotations.Nullable;

import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.PresentForCodeCoverage;

/**
 * Helper methods for XML whitespace as defined by the <code>S</code> production of XML 1.0:
 * <code>#x20 | #x9 | #xD | #xA</code>. Unlike {@link Character#isWhitespace(char)} or
 * {@link String#trim()} no other characters are considered whitespace. The methods implement the
 * XML Schema <code>whiteSpace</code> facet values "replace" and "collapse" as well as the XPath
 * function <code>fn:normalize-space</code>.<br>
 * All String returning methods return the same String instance if nothing needs to be changed.
 *
 * @author Philip Helger
 * @since 12.5.4
 */
@Immutable
public final class XmlWhitespace
{
  @PresentForCodeCoverage
  private static final XmlWhitespace INSTANCE = new XmlWhitespace ();

  private XmlWhitespace ()
  {}

  /**
   * Check if the passed character is XML whitespace.
   *
   * @param c
   *        The character to check.
   * @return <code>true</code> if it is one of space (#x20), tab (#x9), carriage return (#xD) or
   *         line feed (#xA), <code>false</code> otherwise.
   */
  public static boolean isXmlWhitespace (final char c)
  {
    return c == ' ' || c == '\t' || c == '\n' || c == '\r';
  }

  /**
   * Check if the passed code point is XML whitespace.
   *
   * @param nCodePoint
   *        The code point to check.
   * @return <code>true</code> if it is one of space (#x20), tab (#x9), carriage return (#xD) or
   *         line feed (#xA), <code>false</code> otherwise.
   */
  public static boolean isXmlWhitespace (final int nCodePoint)
  {
    return nCodePoint == ' ' || nCodePoint == '\t' || nCodePoint == '\n' || nCodePoint == '\r';
  }

  /**
   * Check if the passed character sequence consists of XML whitespace only. This is consistent with
   * {@link StringHelper#isAllWhitespace(CharSequence)} in that <code>null</code> and empty input
   * returns <code>false</code>.
   *
   * @param aCS
   *        The character sequence to check. May be <code>null</code>.
   * @return <code>true</code> if the input contains at least one character and all characters are
   *         XML whitespace, <code>false</code> otherwise.
   */
  public static boolean isAllXmlWhitespace (@Nullable final CharSequence aCS)
  {
    if (aCS == null)
      return false;
    final int nLen = aCS.length ();
    if (nLen == 0)
      return false;
    for (int i = 0; i < nLen; ++i)
      if (!isXmlWhitespace (aCS.charAt (i)))
        return false;
    return true;
  }

  /**
   * Remove all leading and trailing XML whitespace. Unlike {@link String#trim()} only the four XML
   * whitespace characters are removed and not all control characters.
   *
   * @param sStr
   *        The string to trim. May be <code>null</code>.
   * @return <code>null</code> if the input is <code>null</code>, the same string instance if there
   *         is no leading or trailing XML whitespace, the trimmed string otherwise.
   */
  @Nullable
  public static String trimXmlWhitespace (@Nullable final String sStr)
  {
    if (sStr == null)
      return null;
    final int nLen = sStr.length ();
    int nStart = 0;
    while (nStart < nLen && isXmlWhitespace (sStr.charAt (nStart)))
      nStart++;
    int nEnd = nLen;
    while (nEnd > nStart && isXmlWhitespace (sStr.charAt (nEnd - 1)))
      nEnd--;
    return nStart == 0 && nEnd == nLen ? sStr : sStr.substring (nStart, nEnd);
  }

  /**
   * Replace each XML whitespace character (tab, carriage return, line feed) with a space. This is
   * the XML Schema <code>whiteSpace</code> facet value "replace". The length of the string is not
   * changed.
   *
   * @param sStr
   *        The string to modify. May be <code>null</code>.
   * @return <code>null</code> if the input is <code>null</code>, the same string instance if it
   *         contains no tab, carriage return or line feed, the modified string otherwise.
   */
  @Nullable
  public static String replaceXmlWhitespace (@Nullable final String sStr)
  {
    if (sStr == null)
      return null;
    final int nLen = sStr.length ();
    int i = 0;
    while (i < nLen)
    {
      final char c = sStr.charAt (i);
      if (c == '\t' || c == '\n' || c == '\r')
        break;
      i++;
    }
    if (i == nLen)
      return sStr;

    final char [] aChars = sStr.toCharArray ();
    for (; i < nLen; ++i)
    {
      final char c = aChars[i];
      if (c == '\t' || c == '\n' || c == '\r')
        aChars[i] = ' ';
    }
    return new String (aChars);
  }

  /**
   * Remove leading and trailing XML whitespace and replace each inner sequence of XML whitespace
   * characters with a single space. This is the XML Schema <code>whiteSpace</code> facet value
   * "collapse" and the XPath function <code>fn:normalize-space</code>.
   *
   * @param sStr
   *        The string to modify. May be <code>null</code>.
   * @return <code>null</code> if the input is <code>null</code>, the same string instance if it is
   *         already collapsed, a substring if only leading or trailing whitespace needs to be
   *         removed, the modified string otherwise.
   */
  @Nullable
  public static String collapseXmlWhitespace (@Nullable final String sStr)
  {
    if (sStr == null)
      return null;
    final int nLen = sStr.length ();
    int nStart = 0;
    while (nStart < nLen && isXmlWhitespace (sStr.charAt (nStart)))
      nStart++;
    int nEnd = nLen;
    while (nEnd > nStart && isXmlWhitespace (sStr.charAt (nEnd - 1)))
      nEnd--;

    // Find the first whitespace that is not a single space; sStr[nEnd - 1] is no whitespace, so
    // i + 1 stays in range
    int i = nStart;
    while (i < nEnd)
    {
      final char c = sStr.charAt (i);
      if (isXmlWhitespace (c) && (c != ' ' || isXmlWhitespace (sStr.charAt (i + 1))))
        break;
      i++;
    }
    if (i == nEnd)
    {
      // Already collapsed apart from leading and trailing whitespace
      return nStart == 0 && nEnd == nLen ? sStr : sStr.substring (nStart, nEnd);
    }

    // sStr[nStart, i) is collapsed and does not end with whitespace; append the runs of other
    // characters at once, separated by one space
    final StringBuilder aSB = new StringBuilder (nEnd - nStart);
    aSB.append (sStr, nStart, i);
    while (i < nEnd)
    {
      // Skip the whitespace; sStr[nEnd - 1] is no whitespace
      while (isXmlWhitespace (sStr.charAt (i)))
        i++;
      final int nRunStart = i;
      while (i < nEnd && !isXmlWhitespace (sStr.charAt (i)))
        i++;
      aSB.append (' ').append (sStr, nRunStart, i);
    }
    return aSB.toString ();
  }
}
