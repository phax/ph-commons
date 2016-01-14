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
package com.helger.lesscommons.i18n;

import java.util.function.IntPredicate;

import javax.annotation.CheckForSigned;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.PresentForCodeCoverage;
import com.helger.commons.regex.RegExHelper;

/**
 * General utilities for dealing with Unicode characters
 *
 * @author Apache Abdera
 */
@Immutable
public final class CodepointHelper
{
  @PresentForCodeCoverage
  private static final CodepointHelper s_aInstance = new CodepointHelper ();

  private CodepointHelper ()
  {}

  /**
   * @param aChars
   *        char array
   * @param cLow
   *        Low index
   * @param cHigh
   *        high index
   * @return <code>true</code> if all the characters in chars are within the set
   *         [low,high]
   */
  public static boolean inRange (@Nonnull final char [] aChars, final char cLow, final char cHigh)
  {
    for (final char c : aChars)
      if (c < cLow || c > cHigh)
        return false;
    return true;
  }

  /**
   * @param aChars
   *        char array
   * @param nLow
   *        Low index
   * @param nHigh
   *        high index
   * @return <code>true</code> if all the characters in chars are within the set
   *         [low,high]
   */
  public static boolean inRange (final char [] aChars, final int nLow, final int nHigh)
  {
    for (int i = 0; i < aChars.length; i++)
    {
      final char n = aChars[i];
      final int c = Character.isHighSurrogate (n) &&
                    i + 1 < aChars.length &&
                    Character.isLowSurrogate (aChars[i + 1]) ? Character.toCodePoint (n, aChars[i++]) : (int) n;
      if (c < nLow || c > nHigh)
        return false;
    }
    return true;
  }

  /**
   * @param nCodepoint
   *        codepoint
   * @param nLow
   *        Low index
   * @param nHigh
   *        high index
   * @return <code>true</code> if the codepoint is within the set [low,high]
   */
  public static boolean inRange (final int nCodepoint, final int nLow, final int nHigh)
  {
    return nCodepoint >= nLow && nCodepoint <= nHigh;
  }

  /**
   * @param nCodepoint
   *        Codepoint
   * @return Get the high surrogate for a particular unicode codepoint
   */
  public static char getHighSurrogate (final int nCodepoint)
  {
    return Character.isSupplementaryCodePoint (nCodepoint) ? (char) ((Character.MIN_HIGH_SURROGATE -
                                                                      (Character.MIN_SUPPLEMENTARY_CODE_POINT >> 10)) +
                                                                     (nCodepoint >> 10))
                                                           : 0;
  }

  /**
   * @param nCodepoint
   *        Codepoint
   * @return Get the low surrogate for a particular unicode codepoint
   */
  public static char getLowSurrogate (final int nCodepoint)
  {
    return Character.isSupplementaryCodePoint (nCodepoint) ? (char) (0xDC00 + (nCodepoint & 0x3FF)) : (char) nCodepoint;
  }

  /**
   * @param aSeq
   *        source sequence
   * @param nIndex
   *        index
   * @return the codepoint at the given location, automatically dealing with
   *         surrogate pairs
   */
  @Nonnull
  public static Codepoint codepointAt (@Nonnull final CharSequence aSeq, final int nIndex)
  {
    final char c = aSeq.charAt (nIndex);
    if (c < Character.MIN_HIGH_SURROGATE || c > Character.MAX_LOW_SURROGATE)
      return new Codepoint (c);
    if (Character.isHighSurrogate (c))
    {
      if (aSeq.length () != nIndex)
      {
        final char low = aSeq.charAt (nIndex + 1);
        if (Character.isLowSurrogate (low))
          return new Codepoint (c, low);
      }
    }
    else
      if (Character.isLowSurrogate (c))
      {
        if (nIndex >= 1)
        {
          final char high = aSeq.charAt (nIndex - 1);
          if (Character.isHighSurrogate (high))
            return new Codepoint (high, c);
        }
      }
    return new Codepoint (c);
  }

  /**
   * Insert a codepoint into the buffer, automatically dealing with surrogate
   * pairs
   *
   * @param aSeq
   *        source sequence
   * @param nIndex
   *        index
   * @param aCodepoint
   *        codepoint to be inserted
   */
  public static void insert (final CharSequence aSeq, final int nIndex, @Nonnull final Codepoint aCodepoint)
  {
    insert (aSeq, nIndex, aCodepoint.getValue ());
  }

  /**
   * Insert a codepoint into the buffer, automatically dealing with surrogate
   * pairs
   *
   * @param aSeq
   *        source sequence
   * @param nIndex
   *        index
   * @param nCodepoint
   *        codepoint to be inserted
   */
  public static void insert (@Nonnull final CharSequence aSeq, final int nIndex, final int nCodepoint)
  {
    if (!(aSeq instanceof StringBuilder) && !(aSeq instanceof StringBuffer))
    {
      insert (new StringBuilder (aSeq), nIndex, nCodepoint);
    }
    else
    {
      int nI = nIndex;
      if (nI > 0 && nI < aSeq.length ())
      {
        final char ch = aSeq.charAt (nI);
        final boolean low = Character.isLowSurrogate (ch);
        if (low && Character.isHighSurrogate (aSeq.charAt (nI - 1)))
        {
          nI--;
        }
      }
      if (aSeq instanceof StringBuffer)
        ((StringBuffer) aSeq).insert (nI, getAsCharArray (nCodepoint));
      else
        ((StringBuilder) aSeq).insert (nI, getAsCharArray (nCodepoint));
    }
  }

  /**
   * Set the character at a given location, automatically dealing with surrogate
   * pairs
   *
   * @param aSeq
   *        source sequence
   * @param nIndex
   *        index
   * @param aCodepoint
   *        codepoint to be set
   */
  public static void setChar (@Nonnull final CharSequence aSeq, final int nIndex, @Nonnull final Codepoint aCodepoint)
  {
    setChar (aSeq, nIndex, aCodepoint.getValue ());
  }

  /**
   * Set the character at a given location, automatically dealing with surrogate
   * pairs
   *
   * @param aSeq
   *        source sequence
   * @param nIndex
   *        index
   * @param nCodepoint
   *        codepoint to be set
   */
  public static void setChar (@Nonnull final CharSequence aSeq, final int nIndex, final int nCodepoint)
  {
    if (!(aSeq instanceof StringBuilder) && !(aSeq instanceof StringBuffer))
    {
      setChar (new StringBuilder (aSeq), nIndex, nCodepoint);
    }
    else
    {
      int l = 1;
      int nI = nIndex;
      final char ch = aSeq.charAt (nI);
      final boolean high = Character.isHighSurrogate (ch);
      final boolean low = Character.isLowSurrogate (ch);
      if (high || low)
      {
        if (high && (nI + 1) < aSeq.length () && Character.isLowSurrogate (aSeq.charAt (nI + 1)))
          l++;
        else
        {
          if (low && nI > 0 && Character.isHighSurrogate (aSeq.charAt (nI - 1)))
          {
            nI--;
            l++;
          }
        }
      }
      if (aSeq instanceof StringBuffer)
        ((StringBuffer) aSeq).replace (nI, nI + l, getAsString (nCodepoint));
      else
        ((StringBuilder) aSeq).replace (nI, nI + l, getAsString (nCodepoint));
    }
  }

  /**
   * @param aSeq
   *        source sequence
   * @return the total number of codepoints in the buffer. Each surrogate pair
   *         counts as a single codepoint
   */
  @Nonnegative
  public static int length (@Nonnull final CharSequence aSeq)
  {
    return length (new CodepointIteratorCharSequence (aSeq));
  }

  /**
   * @param aArray
   *        source array
   * @return the total number of codepoints in the buffer. Each surrogate pair
   *         counts as a single codepoint
   */
  @Nonnegative
  public static int length (@Nonnull final char [] aArray)
  {
    return length (new CodepointIteratorCharArray (aArray));
  }

  @Nonnegative
  public static int length (@Nonnull final AbstractCodepointIterator aIter)
  {
    int n = 0;
    while (aIter.hasNext ())
    {
      aIter.next ();
      n++;
    }
    return n;
  }

  /**
   * @param nCodepoint
   *        codepoint
   * @return the char[] representation of the codepoint, automatically dealing
   *         with surrogate pairs
   */
  @Nonnull
  @Nonempty
  public static char [] getAsCharArray (final int nCodepoint)
  {
    return Character.toChars (nCodepoint);
  }

  /**
   * @param nCodepoint
   *        codepoint
   * @return the String representation of the codepoint, automatically dealing
   *         with surrogate pairs
   */
  @Nonnull
  @Nonempty
  public static String getAsString (final int nCodepoint)
  {
    return new String (getAsCharArray (nCodepoint));
  }

  // Left-to-right embedding
  public static final char LRE = 0x202A;
  // Right-to-left embedding
  public static final char RLE = 0x202B;
  // Left-to-right override
  public static final char LRO = 0x202D;
  // Right-to-left override
  public static final char RLO = 0x202E;
  // Left-to-right mark
  public static final char LRM = 0x200E;
  // Right-to-left mark
  public static final char RLM = 0x200F;
  // Pop directional formatting
  public static final char PDF = 0x202C;

  /**
   * Removes leading and trailing bidi controls from the string
   *
   * @param sStr
   *        Source string
   * @return the modified string
   */
  @Nullable
  public static String stripBidi (@Nullable final String sStr)
  {
    if (sStr == null || sStr.length () <= 1)
      return sStr;

    String ret = sStr;
    if (isBidi (ret.charAt (0)))
      ret = ret.substring (1);
    if (isBidi (ret.charAt (ret.length () - 1)))
      ret = ret.substring (0, ret.length () - 1);
    return ret;
  }

  /**
   * Removes bidi controls from within a string
   *
   * @param sStr
   *        Source string
   * @return the modified string
   */
  @Nonnull
  public static String stripBidiInternal (@Nonnull final String sStr)
  {
    return RegExHelper.stringReplacePattern ("[\u202A\u202B\u202D\u202E\u200E\u200F\u202C]", sStr, "");
  }

  @Nonnull
  private static String _wrap (final String s, final char c1, final char c2)
  {
    final StringBuilder buf = new StringBuilder (s);
    if (buf.length () > 1)
    {
      if (buf.charAt (0) != c1)
        buf.insert (0, c1);
      if (buf.charAt (buf.length () - 1) != c2)
        buf.append (c2);
    }
    return buf.toString ();
  }

  /**
   * Wrap the string with the specified bidi control
   *
   * @param sStr
   *        source string
   * @param cChar
   *        source char
   * @return The wrapped string
   */
  @Nullable
  public static String wrapBidi (@Nullable final String sStr, final char cChar)
  {
    switch (cChar)
    {
      case RLE:
        return _wrap (sStr, RLE, PDF);
      case RLO:
        return _wrap (sStr, RLO, PDF);
      case LRE:
        return _wrap (sStr, LRE, PDF);
      case LRO:
        return _wrap (sStr, LRO, PDF);
      case RLM:
        return _wrap (sStr, RLM, RLM);
      case LRM:
        return _wrap (sStr, LRM, LRM);
      default:
        return sStr;
    }
  }

  /**
   * @param aCodepoint
   *        codepoint
   * @return <code>true</code> if the codepoint is a digit
   */
  public static boolean isDigit (@Nonnull final Codepoint aCodepoint)
  {
    return isDigit (aCodepoint.getValue ());
  }

  /**
   * @param nCodepoint
   *        codepoint
   * @return <code>true</code> if the codepoint is a digit
   */
  public static boolean isDigit (final int nCodepoint)
  {
    return Character.isDigit (nCodepoint);
  }

  /**
   * @param aCodepoint
   *        codepoint
   * @return <code>true</code> if the codepoint is part of the ASCII alphabet
   *         (a-z, A-Z)
   */
  public static boolean isAlpha (@Nonnull final Codepoint aCodepoint)
  {
    return isAlpha (aCodepoint.getValue ());
  }

  /**
   * @param nCodepoint
   *        codepoint
   * @return <code>true</code> if the codepoint is part of the ASCII alphabet
   *         (a-z, A-Z)
   */
  public static boolean isAlpha (final int nCodepoint)
  {
    return Character.isLetter (nCodepoint);
  }

  /**
   * @param aCodepoint
   *        codepoint
   * @return <code>true</code> if isAlpha and isDigit both return
   *         <code>true</code>
   */
  public static boolean isAlphaDigit (@Nonnull final Codepoint aCodepoint)
  {
    return isAlphaDigit (aCodepoint.getValue ());
  }

  /**
   * @param nCodepoint
   *        codepoint
   * @return <code>true</code> if isAlpha and isDigit both return
   *         <code>true</code>
   */
  public static boolean isAlphaDigit (final int nCodepoint)
  {
    return Character.isLetterOrDigit (nCodepoint);
  }

  public static boolean isHex (final int nCodepoint)
  {
    return isDigit (nCodepoint) || inRange (nCodepoint, 'a', 'f') || inRange (nCodepoint, 'A', 'F');
  }

  /**
   * @param aCodepoint
   *        codepoint
   * @return <code>true</code> if the codepoint is a bidi control character
   */
  public static boolean isBidi (@Nonnull final Codepoint aCodepoint)
  {
    return isBidi (aCodepoint.getValue ());
  }

  /**
   * @param nCodepoint
   *        codepoint
   * @return <code>true</code> if the codepoint is a bidi control character
   */
  public static boolean isBidi (final int nCodepoint)
  {
    return nCodepoint == LRM ||
           nCodepoint == RLM ||
           nCodepoint == LRE ||
           nCodepoint == RLE ||
           nCodepoint == LRO ||
           nCodepoint == RLO ||
           nCodepoint == PDF;
  }

  @CheckForSigned
  public static int getIndex (@Nonnull final int [] aCodepointSet, final int nValue)
  {
    int nStart = 0, nEnd = aCodepointSet.length;
    while (nEnd - nStart > 8)
    {
      final int i = (nEnd + nStart) >>> 1;
      nStart = aCodepointSet[i] <= nValue ? i : nStart;
      nEnd = aCodepointSet[i] > nValue ? i : nEnd;
    }
    while (nStart < nEnd)
    {
      if (nValue < aCodepointSet[nStart])
        break;
      nStart++;
    }
    return nStart == nEnd ? -1 : nStart - 1;
  }

  /**
   * Treats the specified int array as an Inversion Set and returns
   * <code>true</code> if the value is located within the set. This will only
   * work correctly if the values in the int array are monotonically increasing
   *
   * @param aCodepointSet
   *        Source set
   * @param value
   *        Value to check
   * @return <code>true</code> if the value is located within the set
   */
  public static boolean inverseSetContains (@Nonnull final int [] aCodepointSet, final int value)
  {
    int nStart = 0, nEnd = aCodepointSet.length;
    while (nEnd - nStart > 8)
    {
      final int i = (nEnd + nStart) >>> 1;
      nStart = aCodepointSet[i] <= value ? i : nStart;
      nEnd = aCodepointSet[i] > value ? i : nEnd;
    }
    while (nStart < nEnd)
    {
      if (value < aCodepointSet[nStart])
        break;
      nStart++;
    }
    return ((nStart - 1) & 1) == 0;
  }

  public static boolean isPctEnc (final int codepoint)
  {
    return codepoint == '%' || isDigit (codepoint) || inRange (codepoint, 'A', 'F') || inRange (codepoint, 'a', 'f');
  }

  public static boolean isMark (final int codepoint)
  {
    return codepoint == '-' ||
           codepoint == '_' ||
           codepoint == '.' ||
           codepoint == '!' ||
           codepoint == '~' ||
           codepoint == '*' ||
           codepoint == '\\' ||
           codepoint == '\'' ||
           codepoint == '(' ||
           codepoint == ')';
  }

  public static boolean isUnreserved (final int codepoint)
  {
    return isAlphaDigit (codepoint) || codepoint == '-' || codepoint == '.' || codepoint == '_' || codepoint == '~';
  }

  public static boolean isReserved (final int codepoint)
  {
    return codepoint == '$' ||
           codepoint == '&' ||
           codepoint == '+' ||
           codepoint == ',' ||
           codepoint == '/' ||
           codepoint == ':' ||
           codepoint == ';' ||
           codepoint == '=' ||
           codepoint == '?' ||
           codepoint == '@' ||
           codepoint == '[' ||
           codepoint == ']';
  }

  public static boolean isGenDelim (final int codepoint)
  {
    return codepoint == '#' ||
           codepoint == '/' ||
           codepoint == ':' ||
           codepoint == '?' ||
           codepoint == '@' ||
           codepoint == '[' ||
           codepoint == ']';
  }

  public static boolean isSubDelim (final int codepoint)
  {
    return codepoint == '!' ||
           codepoint == '$' ||
           codepoint == '&' ||
           codepoint == '\'' ||
           codepoint == '(' ||
           codepoint == ')' ||
           codepoint == '*' ||
           codepoint == '+' ||
           codepoint == ',' ||
           codepoint == ';' ||
           codepoint == '=' ||
           codepoint == '\\';
  }

  public static boolean isPchar (final int codepoint)
  {
    return isUnreserved (codepoint) ||
           codepoint == ':' ||
           codepoint == '@' ||
           codepoint == '&' ||
           codepoint == '=' ||
           codepoint == '+' ||
           codepoint == '$' ||
           codepoint == ',';
  }

  public static boolean isPath (final int codepoint)
  {
    return isPchar (codepoint) || codepoint == ';' || codepoint == '/' || codepoint == '%' || codepoint == ',';
  }

  public static boolean isPathNoDelims (final int codepoint)
  {
    return isPath (codepoint) && !isGenDelim (codepoint);
  }

  public static boolean isScheme (final int codepoint)
  {
    return isAlphaDigit (codepoint) || codepoint == '+' || codepoint == '-' || codepoint == '.';
  }

  public static boolean isUserInfo (final int codepoint)
  {
    return isUnreserved (codepoint) || isSubDelim (codepoint) || isPctEnc (codepoint);
  }

  public static boolean isQuery (final int codepoint)
  {
    return isPchar (codepoint) || codepoint == ';' || codepoint == '/' || codepoint == '?' || codepoint == '%';
  }

  public static boolean isFragment (final int codepoint)
  {
    return isPchar (codepoint) || codepoint == '/' || codepoint == '?' || codepoint == '%';
  }

  public static boolean is_ucschar (final int codepoint)
  {
    return inRange (codepoint, '\u00A0', '\uD7FF') ||
           inRange (codepoint, '\uF900', '\uFDCF') ||
           inRange (codepoint, '\uFDF0', '\uFFEF') ||
           inRange (codepoint, 0x10000, 0x1FFFD) ||
           inRange (codepoint, 0x20000, 0x2FFFD) ||
           inRange (codepoint, 0x30000, 0x3FFFD) ||
           inRange (codepoint, 0x40000, 0x4FFFD) ||
           inRange (codepoint, 0x50000, 0x5FFFD) ||
           inRange (codepoint, 0x60000, 0x6FFFD) ||
           inRange (codepoint, 0x70000, 0x7FFFD) ||
           inRange (codepoint, 0x80000, 0x8FFFD) ||
           inRange (codepoint, 0x90000, 0x9FFFD) ||
           inRange (codepoint, 0xA0000, 0xAFFFD) ||
           inRange (codepoint, 0xB0000, 0xBFFFD) ||
           inRange (codepoint, 0xC0000, 0xCFFFD) ||
           inRange (codepoint, 0xD0000, 0xDFFFD) ||
           inRange (codepoint, 0xE1000, 0xEFFFD);
  }

  public static boolean is_iprivate (final int codepoint)
  {
    return inRange (codepoint, '\uE000', '\uF8FF') ||
           inRange (codepoint, 0xF0000, 0xFFFFD) ||
           inRange (codepoint, 0x100000, 0x10FFFD);
  }

  public static boolean is_iunreserved (final int codepoint)
  {
    return isAlphaDigit (codepoint) || isMark (codepoint) || is_ucschar (codepoint);
  }

  public static boolean is_ipchar (final int codepoint)
  {
    return is_iunreserved (codepoint) ||
           isSubDelim (codepoint) ||
           codepoint == ':' ||
           codepoint == '@' ||
           codepoint == '&' ||
           codepoint == '=' ||
           codepoint == '+' ||
           codepoint == '$';
  }

  public static boolean is_ipath (final int codepoint)
  {
    return is_ipchar (codepoint) || codepoint == ';' || codepoint == '/' || codepoint == '%' || codepoint == ',';
  }

  public static boolean is_ipathnodelims (final int codepoint)
  {
    return is_ipath (codepoint) && !isGenDelim (codepoint);
  }

  public static boolean is_iquery (final int codepoint)
  {
    return is_ipchar (codepoint) ||
           is_iprivate (codepoint) ||
           codepoint == ';' ||
           codepoint == '/' ||
           codepoint == '?' ||
           codepoint == '%';
  }

  public static boolean is_ifragment (final int codepoint)
  {
    return is_ipchar (codepoint) || is_iprivate (codepoint) || codepoint == '/' || codepoint == '?' || codepoint == '%';
  }

  public static boolean is_iregname (final int codepoint)
  {
    return is_iunreserved (codepoint) ||
           codepoint == '!' ||
           codepoint == '$' ||
           codepoint == '&' ||
           codepoint == '\'' ||
           codepoint == '(' ||
           codepoint == ')' ||
           codepoint == '*' ||
           codepoint == '+' ||
           codepoint == ',' ||
           codepoint == ';' ||
           codepoint == '=' ||
           codepoint == '"';
  }

  public static boolean is_ipliteral (final int codepoint)
  {
    return isHex (codepoint) || codepoint == ':' || codepoint == '[' || codepoint == ']';
  }

  public static boolean is_ihost (final int codepoint)
  {
    return is_iregname (codepoint) || is_ipliteral (codepoint);
  }

  public static boolean is_regname (final int codepoint)
  {
    return isUnreserved (codepoint) ||
           codepoint == '!' ||
           codepoint == '$' ||
           codepoint == '&' ||
           codepoint == '\'' ||
           codepoint == '(' ||
           codepoint == ')' ||
           codepoint == '*' ||
           codepoint == '+' ||
           codepoint == ',' ||
           codepoint == ';' ||
           codepoint == '=' ||
           codepoint == '"';
  }

  public static boolean is_iuserinfo (final int codepoint)
  {
    return is_iunreserved (codepoint) ||
           codepoint == ';' ||
           codepoint == ':' ||
           codepoint == '&' ||
           codepoint == '=' ||
           codepoint == '+' ||
           codepoint == '$' ||
           codepoint == ',';
  }

  public static boolean is_iserver (final int codepoint)
  {
    return is_iuserinfo (codepoint) ||
           is_iregname (codepoint) ||
           isAlphaDigit (codepoint) ||
           codepoint == '.' ||
           codepoint == ':' ||
           codepoint == '@' ||
           codepoint == '[' ||
           codepoint == ']' ||
           codepoint == '%' ||
           codepoint == '-';
  }

  /**
   * Verifies a sequence of codepoints using the specified filter
   *
   * @param aIter
   *        Codepointer iterator
   * @param aFilter
   *        filter
   */
  public static void verify (final AbstractCodepointIterator aIter, final IntPredicate aFilter)
  {
    final CodepointIteratorRestricted rci = aIter.restrict (aFilter, false);
    while (rci.hasNext ())
      rci.next ();
  }

  /**
   * Verifies a sequence of codepoints using the specified filter
   *
   * @param aIter
   *        codepoint iterator
   * @param eProfile
   *        profile to use
   */
  public static void verify (final AbstractCodepointIterator aIter, @Nonnull final ECodepointProfile eProfile)
  {
    verify (aIter, eProfile.getFilter ());
  }

  /**
   * Verifies a sequence of codepoints using the specified profile
   *
   * @param aArray
   *        char array
   * @param eProfile
   *        profile to use
   */
  public static void verify (@Nullable final char [] aArray, @Nonnull final ECodepointProfile eProfile)
  {
    if (aArray != null)
      verify (new CodepointIteratorCharArray (aArray), eProfile);
  }

  /**
   * Verifies a sequence of codepoints using the specified profile
   *
   * @param sStr
   *        String
   * @param eProfile
   *        profile to use
   */
  public static void verify (@Nullable final String sStr, @Nonnull final ECodepointProfile eProfile)
  {
    if (sStr != null)
      verify (new CodepointIteratorCharSequence (sStr), eProfile);
  }

  /**
   * Verifies a sequence of codepoints using the specified filter
   *
   * @param aIter
   *        Codepoint iterator
   * @param aFilter
   *        Filter to use
   */
  public static void verifyNot (final ICodepointIterator aIter, final IntPredicate aFilter)
  {
    final CodepointIteratorRestricted rci = aIter.restrict (aFilter, false, true);
    while (rci.hasNext ())
      rci.next ();
  }

  /**
   * Verifies a sequence of codepoints using the specified profile
   *
   * @param aIter
   *        Codepoint iterator
   * @param eProfile
   *        profile to use
   */
  public static void verifyNot (final ICodepointIterator aIter, @Nonnull final ECodepointProfile eProfile)
  {
    final CodepointIteratorRestricted rci = aIter.restrict (eProfile.getFilter (), false, true);
    while (rci.hasNext ())
      rci.next ();
  }

  /**
   * Verifies a sequence of codepoints using the specified profile
   *
   * @param aArray
   *        char array
   * @param eProfile
   *        profile to use
   */
  public static void verifyNot (final char [] aArray, @Nonnull final ECodepointProfile eProfile)
  {
    verifyNot (new CodepointIteratorCharArray (aArray), eProfile);
  }
}
