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

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.PresentForCodeCoverage;

/**
 * The name character productions of XML 1.0 (5th edition) and Namespaces in XML 1.0:
 * <code>NameStartChar</code>, <code>NameChar</code>, <code>Name</code> and their colon-free
 * variants for <code>NCName</code>. All checks work on Unicode code points, so supplementary
 * characters (U+10000 to U+EFFFF) are handled correctly. Unpaired surrogates are never name
 * characters.
 *
 * @author Philip Helger
 * @since 12.6.0
 */
@Immutable
public final class XmlNameChars
{
  @PresentForCodeCoverage
  private static final XmlNameChars INSTANCE = new XmlNameChars ();

  private XmlNameChars ()
  {}

  /**
   * Check if the passed code point is a <code>NameStartChar</code> other than the colon, so a
   * valid first character of an <code>NCName</code>.
   *
   * @param nCodePoint
   *        The code point to check.
   * @return <code>true</code> if it is a <code>NameStartChar</code> other than the colon,
   *         <code>false</code> otherwise.
   */
  public static boolean isNCNameStartChar (final int nCodePoint)
  {
    if (nCodePoint < 0x80)
      return (nCodePoint >= 'a' && nCodePoint <= 'z') || (nCodePoint >= 'A' && nCodePoint <= 'Z') || nCodePoint == '_';
    return (nCodePoint >= 0xC0 && nCodePoint <= 0xD6) ||
           (nCodePoint >= 0xD8 && nCodePoint <= 0xF6) ||
           (nCodePoint >= 0xF8 && nCodePoint <= 0x2FF) ||
           (nCodePoint >= 0x370 && nCodePoint <= 0x37D) ||
           (nCodePoint >= 0x37F && nCodePoint <= 0x1FFF) ||
           (nCodePoint >= 0x200C && nCodePoint <= 0x200D) ||
           (nCodePoint >= 0x2070 && nCodePoint <= 0x218F) ||
           (nCodePoint >= 0x2C00 && nCodePoint <= 0x2FEF) ||
           (nCodePoint >= 0x3001 && nCodePoint <= 0xD7FF) ||
           (nCodePoint >= 0xF900 && nCodePoint <= 0xFDCF) ||
           (nCodePoint >= 0xFDF0 && nCodePoint <= 0xFFFD) ||
           (nCodePoint >= 0x10000 && nCodePoint <= 0xEFFFF);
  }

  /**
   * Check if the passed code point is a <code>NameChar</code> other than the colon, so a valid
   * character of an <code>NCName</code>.
   *
   * @param nCodePoint
   *        The code point to check.
   * @return <code>true</code> if it is a <code>NameChar</code> other than the colon,
   *         <code>false</code> otherwise.
   */
  public static boolean isNCNameChar (final int nCodePoint)
  {
    if (nCodePoint < 0x80)
      return (nCodePoint >= 'a' && nCodePoint <= 'z') ||
             (nCodePoint >= 'A' && nCodePoint <= 'Z') ||
             (nCodePoint >= '0' && nCodePoint <= '9') ||
             nCodePoint == '_' ||
             nCodePoint == '-' ||
             nCodePoint == '.';
    return nCodePoint == 0xB7 ||
           (nCodePoint >= 0x300 && nCodePoint <= 0x36F) ||
           (nCodePoint >= 0x203F && nCodePoint <= 0x2040) ||
           isNCNameStartChar (nCodePoint);
  }

  /**
   * Check if the passed code point is a <code>NameStartChar</code>, so a valid first character of
   * a <code>Name</code>.
   *
   * @param nCodePoint
   *        The code point to check.
   * @return <code>true</code> if it is a <code>NameStartChar</code>, <code>false</code> otherwise.
   */
  public static boolean isNameStartChar (final int nCodePoint)
  {
    return nCodePoint == ':' || isNCNameStartChar (nCodePoint);
  }

  /**
   * Check if the passed code point is a <code>NameChar</code>, so a valid character of a
   * <code>Name</code>.
   *
   * @param nCodePoint
   *        The code point to check.
   * @return <code>true</code> if it is a <code>NameChar</code>, <code>false</code> otherwise.
   */
  public static boolean isNameChar (final int nCodePoint)
  {
    return nCodePoint == ':' || isNCNameChar (nCodePoint);
  }

  private static boolean _isName (@NonNull final String sStr, final boolean bAllowColon)
  {
    final int nLen = sStr.length ();
    if (nLen == 0)
      return false;
    int i = 0;
    while (i < nLen)
    {
      final int nCP = sStr.codePointAt (i);
      if (nCP == ':')
      {
        if (!bAllowColon)
          return false;
      }
      else
        if (i == 0 ? !isNCNameStartChar (nCP) : !isNCNameChar (nCP))
          return false;
      i += Character.charCount (nCP);
    }
    return true;
  }

  /**
   * Check if the passed string matches the XML <code>Name</code> production: a
   * <code>NameStartChar</code> followed by any number of <code>NameChar</code>s.
   *
   * @param sStr
   *        The string to check. May be <code>null</code>.
   * @return <code>true</code> if it is a valid <code>Name</code>, <code>false</code> if it is
   *         <code>null</code>, empty or not a valid <code>Name</code>.
   */
  public static boolean isName (@Nullable final String sStr)
  {
    return sStr != null && _isName (sStr, true);
  }

  /**
   * Check if the passed string matches the <code>NCName</code> production of Namespaces in XML: a
   * <code>Name</code> without any colon.
   *
   * @param sStr
   *        The string to check. May be <code>null</code>.
   * @return <code>true</code> if it is a valid <code>NCName</code>, <code>false</code> if it is
   *         <code>null</code>, empty or not a valid <code>NCName</code>.
   */
  public static boolean isNCName (@Nullable final String sStr)
  {
    return sStr != null && _isName (sStr, false);
  }
}
