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
package com.helger.xml.serialize.write;

import java.nio.CharBuffer;
import java.util.BitSet;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.PresentForCodeCoverage;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.string.XmlNameChars;
import com.helger.collection.commons.CommonsLinkedHashSet;
import com.helger.collection.commons.ICommonsOrderedSet;

/**
 * This class contains XML utility methods for character handling.
 *
 * @author Philip Helger
 */
@Immutable
public final class XMLCharHelper
{
  // This is: Character.MAX_VALUE - Character.MIN_VALUE + 1
  private static final int BIT_SET_LENGTH = 0x10000;

  /**
   * This class is responsible for initialising the XML 1.0 character sets.
   *
   * @author Philip Helger
   */
  private static final class XML10
  {
    /**
     * This construct ensures, that this character set is only initialised when accessed
     *
     * @author Philip Helger
     */
    private static final class Singleton
    {
      static final XML10 XML10_INSTANCE = new XML10 ();
    }

    private static final Logger LOGGER = LoggerFactory.getLogger (XMLCharHelper.XML10.class);

    /** This is used for XML 1.0 text, CDATA and attribute value */
    private final BitSet m_aInvalidValueChar = new BitSet (BIT_SET_LENGTH);

    private XML10 ()
    {
      if (LOGGER.isDebugEnabled ())
        LOGGER.debug ("Initialising XML 1.0 character table");
      for (int c = Character.MIN_VALUE; c <= Character.MAX_VALUE; ++c)
      {
        m_aInvalidValueChar.set (c,
                                 (c >= 0x0 && c <= 0x8) ||
                                    (c >= 0xb && c <= 0xc) ||
                                    (c >= 0xe && c <= 0x1f) ||
                                    // Surrogate chars
                                    // (c >= 0xd800 && c <= 0xdfff) ||
                                    (c >= 0xfffe && c <= 0xffff));
      }
    }
  }

  /**
   * This class is responsible for initialising the XML 1.1 character sets.
   *
   * @author Philip Helger
   */
  private static final class XML11
  {
    /**
     * This construct ensures, that this character set is only initialised when accessed
     *
     * @author Philip Helger
     */
    private static final class Singleton
    {
      static final XML11 XML11_INSTANCE = new XML11 ();
    }

    private static final Logger LOGGER = LoggerFactory.getLogger (XMLCharHelper.XML11.class);

    /** This is used for XML 1.1 text values */
    private final BitSet m_aInvalidTextValueChar = new BitSet (BIT_SET_LENGTH);
    /** This is used for XML 1.1 CDATA values */
    private final BitSet m_aInvalidCDATValueChar = new BitSet (BIT_SET_LENGTH);
    /** This is used for XML 1.1 CDATA and attribute values */
    private final BitSet m_aInvalidAttrValueChar = new BitSet (BIT_SET_LENGTH);

    private XML11 ()
    {
      if (LOGGER.isDebugEnabled ())
        LOGGER.debug ("Initialising XML 1.1 character table");
      for (int c = Character.MIN_VALUE; c <= Character.MAX_VALUE; ++c)
      {
        m_aInvalidTextValueChar.set (c,
                                     (c == 0x0) ||
                                        (c > Character.MAX_SURROGATE && c <= 0xdfff) ||
                                        (c >= 0xfffe && c <= 0xffff));
        m_aInvalidCDATValueChar.set (c,
                                     (c >= 0x0 && c <= 0x8) ||
                                        (c >= 0xb && c <= 0xc) ||
                                        (c >= 0xe && c <= 0x1f) ||
                                        (c >= 0x7f && c <= 0x9f) ||
                                        // Surrogate chars
                                        // (c >= 0xd800 && c <= 0xdfff)
                                        // ||
                                        (c >= 0xfffe && c <= 0xffff));
        m_aInvalidAttrValueChar.set (c,
                                     (c == 0x0) ||
                                        (c >= 0x7f && c <= 0x84) ||
                                        (c >= 0x86 && c <= 0x9f) ||
                                        // Surrogate chars
                                        // (c >= 0xd800 && c <= 0xdfff) ||
                                        (c >= 0xfffe && c <= 0xffff));
      }
    }
  }

  /**
   * This class is responsible for initialising the HTML character sets.
   *
   * @author Philip Helger
   */
  private static final class HTML
  {
    /**
     * This construct ensures, that this character set is only initialised when accessed
     *
     * @author Philip Helger
     */
    private static final class Singleton
    {
      static final HTML HTML_INSTANCE = new HTML ();
    }

    private static final Logger LOGGER = LoggerFactory.getLogger (XMLCharHelper.HTML.class);

    /** For all HTML values */
    private final BitSet m_aInvalidChar = new BitSet (BIT_SET_LENGTH);

    private HTML ()
    {
      if (LOGGER.isDebugEnabled ())
        LOGGER.debug ("Initialising HTML character table");
      for (int c = Character.MIN_VALUE; c <= Character.MAX_VALUE; ++c)
      {
        /**
         * Source: http://www.w3.org/TR/REC-html40/sgml/sgmldecl.html with sanity handling for 0x80
         * - 0x9f
         */
        m_aInvalidChar.set (c,
                            (c >= 0x0 && c <= 0x8) ||
                               (c >= 0xb && c <= 0xc) ||
                               (c >= 0xe && c <= 0x1f) ||
                               (c == 0x7f) ||
                               // (c >= 0x80 && c <= 0x9f) ||
                               // Surrogate chars
                               // (c >= 0xd800 && c <= 0xdfff) ||
                               (c >= 0xfffe && c <= 0xffff));
      }
    }
  }

  @PresentForCodeCoverage
  private static final XMLCharHelper INSTANCE = new XMLCharHelper ();

  private XMLCharHelper ()
  {}

  /**
   * Check if the passed character is invalid for an element or attribute name on the first position
   *
   * @param eXMLVersion
   *        XML version to be used. May not be <code>null</code>.
   * @param c
   *        char to check
   * @return <code>true</code> if the char is invalid
   */
  @SuppressWarnings ("removal")
  public static boolean isInvalidXMLNameStartChar (@NonNull final EXMLSerializeVersion eXMLVersion, final int c)
  {
    return switch (eXMLVersion)
    {
      // XML 1.0 (5th edition) and XML 1.1 share the same name characters
      case XML_10, XML_11 -> !XmlNameChars.isNCNameStartChar (c);
      case HTML -> HTML.Singleton.HTML_INSTANCE.m_aInvalidChar.get (c);
      default -> throw new IllegalArgumentException ("Unsupported XML version " + eXMLVersion + "!");
    };
  }

  /**
   * Check if the passed character is invalid for an element or attribute name after the first
   * position
   *
   * @param eXMLVersion
   *        XML version to be used. May not be <code>null</code>.
   * @param c
   *        char to check
   * @return <code>true</code> if the char is invalid
   */
  @SuppressWarnings ("removal")
  public static boolean isInvalidXMLNameChar (@NonNull final EXMLSerializeVersion eXMLVersion, final int c)
  {
    return switch (eXMLVersion)
    {
      // XML 1.0 (5th edition) and XML 1.1 share the same name characters
      case XML_10, XML_11 -> !XmlNameChars.isNCNameChar (c);
      case HTML -> HTML.Singleton.HTML_INSTANCE.m_aInvalidChar.get (c);
      default -> throw new IllegalArgumentException ("Unsupported XML version " + eXMLVersion + "!");
    };
  }

  /**
   * Get the code point at the given index of the passed character sequence, not combining a high
   * surrogate with a low surrogate at or after the passed limit. This is the
   * {@link CharSequence} counterpart of {@link Character#codePointAt(char[], int, int)}.
   *
   * @param aCS
   *        The character sequence to read from. May not be <code>null</code>.
   * @param nIndex
   *        The index to read from. Must be &ge; 0 and &lt; nLimit.
   * @param nLimit
   *        The index after the last character that may be read.
   * @return The code point at the given index.
   */
  private static int _getCodePointAt (@NonNull final CharSequence aCS,
                                      @Nonnegative final int nIndex,
                                      @Nonnegative final int nLimit)
  {
    final char c1 = aCS.charAt (nIndex);
    if (Character.isHighSurrogate (c1) && nIndex + 1 < nLimit)
    {
      final char c2 = aCS.charAt (nIndex + 1);
      if (Character.isLowSurrogate (c2))
        return Character.toCodePoint (c1, c2);
    }
    return c1;
  }

  /**
   * Check if the passed character sequence contains an invalid XML name character within the
   * specified range.
   *
   * @param eXMLVersion
   *        XML version to be used. May not be <code>null</code>.
   * @param aCS
   *        The character sequence to check. May not be <code>null</code>.
   * @param nOfs
   *        The offset into the character sequence to start checking. Must be &ge; 0.
   * @param nLen
   *        The number of characters to check. Must be &ge; 0.
   * @return <code>true</code> if the specified range contains at least one invalid XML name
   *         character.
   */
  private static boolean _containsInvalidXMLNameChar (@NonNull final EXMLSerializeVersion eXMLVersion,
                                                      @NonNull final CharSequence aCS,
                                                      @Nonnegative final int nOfs,
                                                      @Nonnegative final int nLen)
  {
    // Iterate code points, so that supplementary characters are checked as a whole
    final int nEnd = nOfs + nLen;
    int i = nOfs;
    while (i < nEnd)
    {
      final int nCP = _getCodePointAt (aCS, i, nEnd);
      final boolean bInvalid = i == nOfs ? isInvalidXMLNameStartChar (eXMLVersion, nCP)
                                         : isInvalidXMLNameChar (eXMLVersion, nCP);
      if (bInvalid)
        return true;
      i += Character.charCount (nCP);
    }
    return false;
  }

  /**
   * Check if the passed string contains an invalid XML name character.
   *
   * @param eXMLVersion
   *        XML version to be used. May not be <code>null</code>.
   * @param s
   *        The string to check. May be <code>null</code>.
   * @return <code>true</code> if the string contains at least one invalid XML name character.
   */
  public static boolean containsInvalidXMLNameChar (@NonNull final EXMLSerializeVersion eXMLVersion,
                                                    @Nullable final String s)
  {
    return s != null && s.length () > 0 && _containsInvalidXMLNameChar (eXMLVersion, s, 0, s.length ());
  }

  /**
   * Check if the passed character array contains an invalid XML name character.
   *
   * @param eXMLVersion
   *        XML version to be used. May not be <code>null</code>.
   * @param aChars
   *        The character array to check. May be <code>null</code>.
   * @return <code>true</code> if the array contains at least one invalid XML name character.
   */
  public static boolean containsInvalidXMLNameChar (@NonNull final EXMLSerializeVersion eXMLVersion,
                                                    final char @Nullable [] aChars)
  {
    return aChars != null && aChars.length > 0 && containsInvalidXMLNameChar (eXMLVersion, aChars, 0, aChars.length);
  }

  /**
   * Check if the passed character array contains an invalid XML name character within the specified
   * range.
   *
   * @param eXMLVersion
   *        XML version to be used. May not be <code>null</code>.
   * @param aChars
   *        The character array to check. May be <code>null</code>.
   * @param nOfs
   *        The offset into the array to start checking. Must be &ge; 0.
   * @param nLen
   *        The number of characters to check. Must be &ge; 0.
   * @return <code>true</code> if the specified range contains at least one invalid XML name
   *         character.
   */
  public static boolean containsInvalidXMLNameChar (@NonNull final EXMLSerializeVersion eXMLVersion,
                                                    final char @Nullable [] aChars,
                                                    @Nonnegative final int nOfs,
                                                    @Nonnegative final int nLen)
  {
    return aChars != null &&
           nLen > 0 &&
           _containsInvalidXMLNameChar (eXMLVersion, CharBuffer.wrap (aChars), nOfs, nLen);
  }

  /**
   * Get all invalid XML name characters from the passed character sequence within the specified
   * range.
   *
   * @param eXMLVersion
   *        XML version to be used. May not be <code>null</code>.
   * @param aCS
   *        The character sequence to check. May not be <code>null</code>.
   * @param nOfs
   *        The offset into the character sequence to start checking. Must be &ge; 0.
   * @param nLen
   *        The number of characters to check. Must be &ge; 0.
   * @return A non-<code>null</code> set of all invalid characters.
   */
  @NonNull
  @ReturnsMutableCopy
  private static ICommonsOrderedSet <Character> _getAllInvalidXMLNameChars (@NonNull final EXMLSerializeVersion eXMLVersion,
                                                                            @NonNull final CharSequence aCS,
                                                                            @Nonnegative final int nOfs,
                                                                            @Nonnegative final int nLen)
  {
    final ICommonsOrderedSet <Character> ret = new CommonsLinkedHashSet <> ();
    // Iterate code points, so that supplementary characters are checked as a whole
    final int nEnd = nOfs + nLen;
    int i = nOfs;
    while (i < nEnd)
    {
      final int nCP = _getCodePointAt (aCS, i, nEnd);
      final boolean bInvalid = i == nOfs ? isInvalidXMLNameStartChar (eXMLVersion, nCP)
                                         : isInvalidXMLNameChar (eXMLVersion, nCP);
      if (bInvalid)
      {
        // An invalid supplementary character is reported with both of its surrogate chars
        for (final char c : Character.toChars (nCP))
          ret.add (Character.valueOf (c));
      }
      i += Character.charCount (nCP);
    }
    return ret;
  }

  /**
   * Get all invalid XML name characters from the passed string.
   *
   * @param eXMLVersion
   *        XML version to be used. May not be <code>null</code>.
   * @param s
   *        The string to check. May be <code>null</code>.
   * @return <code>null</code> if the input is <code>null</code> or empty, a non-<code>null</code>
   *         set of all invalid characters otherwise.
   */
  @Nullable
  @ReturnsMutableCopy
  public static ICommonsOrderedSet <Character> getAllInvalidXMLNameChars (@NonNull final EXMLSerializeVersion eXMLVersion,
                                                                          @Nullable final String s)
  {
    return s == null || s.length () == 0 ? null : _getAllInvalidXMLNameChars (eXMLVersion, s, 0, s.length ());
  }

  /**
   * Get all invalid XML name characters from the passed character array.
   *
   * @param eXMLVersion
   *        XML version to be used. May not be <code>null</code>.
   * @param aChars
   *        The character array to check. May be <code>null</code>.
   * @return <code>null</code> if the input is <code>null</code> or empty, a non-<code>null</code>
   *         set of all invalid characters otherwise.
   */
  @Nullable
  @ReturnsMutableCopy
  public static ICommonsOrderedSet <Character> getAllInvalidXMLNameChars (@NonNull final EXMLSerializeVersion eXMLVersion,
                                                                          final char @Nullable [] aChars)
  {
    return aChars == null || aChars.length == 0 ? null
                                                : getAllInvalidXMLNameChars (eXMLVersion, aChars, 0, aChars.length);
  }

  /**
   * Get all invalid XML name characters from the passed character array within the specified range.
   *
   * @param eXMLVersion
   *        XML version to be used. May not be <code>null</code>.
   * @param aChars
   *        The character array to check. May be <code>null</code>.
   * @param nOfs
   *        The offset into the array to start checking. Must be &ge; 0.
   * @param nLen
   *        The number of characters to check. Must be &ge; 0.
   * @return <code>null</code> if the input is <code>null</code> or empty, a non-<code>null</code>
   *         set of all invalid characters otherwise.
   */
  @Nullable
  @ReturnsMutableCopy
  public static ICommonsOrderedSet <Character> getAllInvalidXMLNameChars (@NonNull final EXMLSerializeVersion eXMLVersion,
                                                                          final char @Nullable [] aChars,
                                                                          @Nonnegative final int nOfs,
                                                                          @Nonnegative final int nLen)
  {
    if (aChars == null || nLen <= 0)
      return null;

    return _getAllInvalidXMLNameChars (eXMLVersion, CharBuffer.wrap (aChars), nOfs, nLen);
  }

  /**
   * Check if the passed character is invalid for a text node.
   *
   * @param eXMLVersion
   *        XML version to be used. May not be <code>null</code>.
   * @param c
   *        char to check
   * @return <code>true</code> if the char is invalid
   */
  @SuppressWarnings ("removal")
  public static boolean isInvalidXMLTextChar (@NonNull final EXMLSerializeVersion eXMLVersion, final int c)
  {
    return switch (eXMLVersion)
    {
      case XML_10 -> XML10.Singleton.XML10_INSTANCE.m_aInvalidValueChar.get (c);
      case XML_11 -> XML11.Singleton.XML11_INSTANCE.m_aInvalidTextValueChar.get (c);
      case HTML -> HTML.Singleton.HTML_INSTANCE.m_aInvalidChar.get (c);
      default -> throw new IllegalArgumentException ("Unsupported XML version " + eXMLVersion + "!");
    };
  }

  /**
   * Check if the passed character sequence contains an invalid XML text character within
   * the specified range.
   *
   * @param eXMLVersion
   *        XML version to be used. May not be <code>null</code>.
   * @param aCS
   *        The character sequence to check. May not be <code>null</code>.
   * @param nOfs
   *        The offset into the character sequence to start checking. Must be &ge; 0.
   * @param nLen
   *        The number of characters to check. Must be &ge; 0.
   * @return <code>true</code> if the specified range contains at least one invalid XML text
   *         character.
   */
  private static boolean _containsInvalidXMLTextChar (@NonNull final EXMLSerializeVersion eXMLVersion,
                                                      @NonNull final CharSequence aCS,
                                                      @Nonnegative final int nOfs,
                                                      @Nonnegative final int nLen)
  {
    for (int i = 0; i < nLen; ++i)
    {
      final char c = aCS.charAt (nOfs + i);
      if (isInvalidXMLTextChar (eXMLVersion, c))
        return true;
    }
    return false;
  }

  /**
   * Check if the passed string contains an invalid XML text character.
   *
   * @param eXMLVersion
   *        XML version to be used. May not be <code>null</code>.
   * @param s
   *        The string to check. May be <code>null</code>.
   * @return <code>true</code> if the string contains at least one invalid XML text character.
   */
  public static boolean containsInvalidXMLTextChar (@NonNull final EXMLSerializeVersion eXMLVersion,
                                                    @Nullable final String s)
  {
    return s != null && s.length () > 0 && _containsInvalidXMLTextChar (eXMLVersion, s, 0, s.length ());
  }

  /**
   * Check if the passed character array contains an invalid XML text character.
   *
   * @param eXMLVersion
   *        XML version to be used. May not be <code>null</code>.
   * @param aChars
   *        The character array to check. May be <code>null</code>.
   * @return <code>true</code> if the array contains at least one invalid XML text character.
   */
  public static boolean containsInvalidXMLTextChar (@NonNull final EXMLSerializeVersion eXMLVersion,
                                                    final char @Nullable [] aChars)
  {
    return aChars != null && aChars.length > 0 && containsInvalidXMLTextChar (eXMLVersion, aChars, 0, aChars.length);
  }

  /**
   * Check if the passed character array contains an invalid XML text character within the specified
   * range.
   *
   * @param eXMLVersion
   *        XML version to be used. May not be <code>null</code>.
   * @param aChars
   *        The character array to check. May be <code>null</code>.
   * @param nOfs
   *        The offset into the array to start checking. Must be &ge; 0.
   * @param nLen
   *        The number of characters to check. Must be &ge; 0.
   * @return <code>true</code> if the specified range contains at least one invalid XML text
   *         character.
   */
  public static boolean containsInvalidXMLTextChar (@NonNull final EXMLSerializeVersion eXMLVersion,
                                                    final char @Nullable [] aChars,
                                                    @Nonnegative final int nOfs,
                                                    @Nonnegative final int nLen)
  {
    return aChars != null &&
           nLen > 0 &&
           _containsInvalidXMLTextChar (eXMLVersion, CharBuffer.wrap (aChars), nOfs, nLen);
  }

  /**
   * Get all invalid XML text characters from the passed character sequence within the
   * specified range.
   *
   * @param eXMLVersion
   *        XML version to be used. May not be <code>null</code>.
   * @param aCS
   *        The character sequence to check. May not be <code>null</code>.
   * @param nOfs
   *        The offset into the character sequence to start checking. Must be &ge; 0.
   * @param nLen
   *        The number of characters to check. Must be &ge; 0.
   * @return A non-<code>null</code> set of all invalid characters.
   */
  @NonNull
  @ReturnsMutableCopy
  private static ICommonsOrderedSet <Character> _getAllInvalidXMLTextChars (@NonNull final EXMLSerializeVersion eXMLVersion,
                                                                            @NonNull final CharSequence aCS,
                                                                            @Nonnegative final int nOfs,
                                                                            @Nonnegative final int nLen)
  {
    final ICommonsOrderedSet <Character> ret = new CommonsLinkedHashSet <> ();
    for (int i = 0; i < nLen; ++i)
    {
      final char c = aCS.charAt (nOfs + i);
      if (isInvalidXMLTextChar (eXMLVersion, c))
        ret.add (Character.valueOf (c));
    }
    return ret;
  }

  /**
   * Get all invalid XML text characters from the passed string.
   *
   * @param eXMLVersion
   *        XML version to be used. May not be <code>null</code>.
   * @param s
   *        The string to check. May be <code>null</code>.
   * @return <code>null</code> if the input is <code>null</code> or empty, a non-<code>null</code>
   *         set of all invalid characters otherwise.
   */
  @Nullable
  @ReturnsMutableCopy
  public static ICommonsOrderedSet <Character> getAllInvalidXMLTextChars (@NonNull final EXMLSerializeVersion eXMLVersion,
                                                                          @Nullable final String s)
  {
    return s == null || s.length () == 0 ? null
                                         : _getAllInvalidXMLTextChars (eXMLVersion, s, 0, s.length ());
  }

  /**
   * Get all invalid XML text characters from the passed character array.
   *
   * @param eXMLVersion
   *        XML version to be used. May not be <code>null</code>.
   * @param aChars
   *        The character array to check. May be <code>null</code>.
   * @return <code>null</code> if the input is <code>null</code> or empty, a non-<code>null</code>
   *         set of all invalid characters otherwise.
   */
  @Nullable
  @ReturnsMutableCopy
  public static ICommonsOrderedSet <Character> getAllInvalidXMLTextChars (@NonNull final EXMLSerializeVersion eXMLVersion,
                                                                          final char @Nullable [] aChars)
  {
    return aChars == null || aChars.length == 0 ? null
                                                : getAllInvalidXMLTextChars (eXMLVersion, aChars, 0, aChars.length);
  }

  /**
   * Get all invalid XML text characters from the passed character array within the specified range.
   *
   * @param eXMLVersion
   *        XML version to be used. May not be <code>null</code>.
   * @param aChars
   *        The character array to check. May be <code>null</code>.
   * @param nOfs
   *        The offset into the array to start checking. Must be &ge; 0.
   * @param nLen
   *        The number of characters to check. Must be &ge; 0.
   * @return <code>null</code> if the input is <code>null</code> or empty, a non-<code>null</code>
   *         set of all invalid characters otherwise.
   */
  @Nullable
  @ReturnsMutableCopy
  public static ICommonsOrderedSet <Character> getAllInvalidXMLTextChars (@NonNull final EXMLSerializeVersion eXMLVersion,
                                                                          final char @Nullable [] aChars,
                                                                          @Nonnegative final int nOfs,
                                                                          @Nonnegative final int nLen)
  {
    if (aChars == null || nLen <= 0)
      return null;

    return _getAllInvalidXMLTextChars (eXMLVersion, CharBuffer.wrap (aChars), nOfs, nLen);
  }

  /**
   * Check if the passed character is invalid for a CDATA node.
   *
   * @param eXMLVersion
   *        XML version to be used. May not be <code>null</code>.
   * @param c
   *        char to check
   * @return <code>true</code> if the char is invalid
   */
  @SuppressWarnings ("removal")
  public static boolean isInvalidXMLCDATAChar (@NonNull final EXMLSerializeVersion eXMLVersion, final int c)
  {
    return switch (eXMLVersion)
    {
      case XML_10 -> XML10.Singleton.XML10_INSTANCE.m_aInvalidValueChar.get (c);
      case XML_11 -> XML11.Singleton.XML11_INSTANCE.m_aInvalidCDATValueChar.get (c);
      case HTML -> HTML.Singleton.HTML_INSTANCE.m_aInvalidChar.get (c);
      default -> throw new IllegalArgumentException ("Unsupported XML version " + eXMLVersion + "!");
    };
  }

  /**
   * Check if the passed character sequence contains an invalid XML CDATA character within
   * the specified range.
   *
   * @param eXMLVersion
   *        XML version to be used. May not be <code>null</code>.
   * @param aCS
   *        The character sequence to check. May not be <code>null</code>.
   * @param nOfs
   *        The offset into the character sequence to start checking. Must be &ge; 0.
   * @param nLen
   *        The number of characters to check. Must be &ge; 0.
   * @return <code>true</code> if the specified range contains at least one invalid XML CDATA
   *         character.
   */
  private static boolean _containsInvalidXMLCDATAChar (@NonNull final EXMLSerializeVersion eXMLVersion,
                                                       @NonNull final CharSequence aCS,
                                                       @Nonnegative final int nOfs,
                                                       @Nonnegative final int nLen)
  {
    for (int i = 0; i < nLen; ++i)
    {
      final char c = aCS.charAt (nOfs + i);
      if (isInvalidXMLCDATAChar (eXMLVersion, c))
        return true;
    }
    return false;
  }

  /**
   * Check if the passed string contains an invalid XML CDATA character.
   *
   * @param eXMLVersion
   *        XML version to be used. May not be <code>null</code>.
   * @param s
   *        The string to check. May be <code>null</code>.
   * @return <code>true</code> if the string contains at least one invalid XML CDATA character.
   */
  public static boolean containsInvalidXMLCDATAChar (@NonNull final EXMLSerializeVersion eXMLVersion,
                                                     @Nullable final String s)
  {
    return s != null && s.length () > 0 && _containsInvalidXMLCDATAChar (eXMLVersion, s, 0, s.length ());
  }

  /**
   * Check if the passed character array contains an invalid XML CDATA character.
   *
   * @param eXMLVersion
   *        XML version to be used. May not be <code>null</code>.
   * @param aChars
   *        The character array to check. May be <code>null</code>.
   * @return <code>true</code> if the array contains at least one invalid XML CDATA character.
   */
  public static boolean containsInvalidXMLCDATAChar (@NonNull final EXMLSerializeVersion eXMLVersion,
                                                     final char @Nullable [] aChars)
  {
    return aChars != null && aChars.length > 0 && containsInvalidXMLCDATAChar (eXMLVersion, aChars, 0, aChars.length);
  }

  /**
   * Check if the passed character array contains an invalid XML CDATA character within the
   * specified range.
   *
   * @param eXMLVersion
   *        XML version to be used. May not be <code>null</code>.
   * @param aChars
   *        The character array to check. May be <code>null</code>.
   * @param nOfs
   *        The offset into the array to start checking. Must be &ge; 0.
   * @param nLen
   *        The number of characters to check. Must be &ge; 0.
   * @return <code>true</code> if the specified range contains at least one invalid XML CDATA
   *         character.
   */
  public static boolean containsInvalidXMLCDATAChar (@NonNull final EXMLSerializeVersion eXMLVersion,
                                                     final char @Nullable [] aChars,
                                                     @Nonnegative final int nOfs,
                                                     @Nonnegative final int nLen)
  {
    return aChars != null &&
           nLen > 0 &&
           _containsInvalidXMLCDATAChar (eXMLVersion, CharBuffer.wrap (aChars), nOfs, nLen);
  }

  /**
   * Get all invalid XML CDATA characters from the passed character sequence within the
   * specified range.
   *
   * @param eXMLVersion
   *        XML version to be used. May not be <code>null</code>.
   * @param aCS
   *        The character sequence to check. May not be <code>null</code>.
   * @param nOfs
   *        The offset into the character sequence to start checking. Must be &ge; 0.
   * @param nLen
   *        The number of characters to check. Must be &ge; 0.
   * @return A non-<code>null</code> set of all invalid characters.
   */
  @NonNull
  @ReturnsMutableCopy
  private static ICommonsOrderedSet <Character> _getAllInvalidXMLCDATAChars (@NonNull final EXMLSerializeVersion eXMLVersion,
                                                                             @NonNull final CharSequence aCS,
                                                                             @Nonnegative final int nOfs,
                                                                             @Nonnegative final int nLen)
  {
    final ICommonsOrderedSet <Character> ret = new CommonsLinkedHashSet <> ();
    for (int i = 0; i < nLen; ++i)
    {
      final char c = aCS.charAt (nOfs + i);
      if (isInvalidXMLCDATAChar (eXMLVersion, c))
        ret.add (Character.valueOf (c));
    }
    return ret;
  }

  /**
   * Get all invalid XML CDATA characters from the passed string.
   *
   * @param eXMLVersion
   *        XML version to be used. May not be <code>null</code>.
   * @param s
   *        The string to check. May be <code>null</code>.
   * @return <code>null</code> if the input is <code>null</code> or empty, a non-<code>null</code>
   *         set of all invalid characters otherwise.
   */
  @Nullable
  @ReturnsMutableCopy
  public static ICommonsOrderedSet <Character> getAllInvalidXMLCDATAChars (@NonNull final EXMLSerializeVersion eXMLVersion,
                                                                           @Nullable final String s)
  {
    return s == null || s.length () == 0 ? null
                                         : _getAllInvalidXMLCDATAChars (eXMLVersion, s, 0, s.length ());
  }

  /**
   * Get all invalid XML CDATA characters from the passed character array.
   *
   * @param eXMLVersion
   *        XML version to be used. May not be <code>null</code>.
   * @param aChars
   *        The character array to check. May be <code>null</code>.
   * @return <code>null</code> if the input is <code>null</code> or empty, a non-<code>null</code>
   *         set of all invalid characters otherwise.
   */
  @Nullable
  @ReturnsMutableCopy
  public static ICommonsOrderedSet <Character> getAllInvalidXMLCDATAChars (@NonNull final EXMLSerializeVersion eXMLVersion,
                                                                           final char @Nullable [] aChars)
  {
    return aChars == null || aChars.length == 0 ? null
                                                : getAllInvalidXMLCDATAChars (eXMLVersion, aChars, 0, aChars.length);
  }

  /**
   * Get all invalid XML CDATA characters from the passed character array within the specified
   * range.
   *
   * @param eXMLVersion
   *        XML version to be used. May not be <code>null</code>.
   * @param aChars
   *        The character array to check. May be <code>null</code>.
   * @param nOfs
   *        The offset into the array to start checking. Must be &ge; 0.
   * @param nLen
   *        The number of characters to check. Must be &ge; 0.
   * @return <code>null</code> if the input is <code>null</code> or empty, a non-<code>null</code>
   *         set of all invalid characters otherwise.
   */
  @Nullable
  @ReturnsMutableCopy
  public static ICommonsOrderedSet <Character> getAllInvalidXMLCDATAChars (@NonNull final EXMLSerializeVersion eXMLVersion,
                                                                           final char @Nullable [] aChars,
                                                                           @Nonnegative final int nOfs,
                                                                           @Nonnegative final int nLen)
  {
    if (aChars == null || nLen <= 0)
      return null;

    return _getAllInvalidXMLCDATAChars (eXMLVersion, CharBuffer.wrap (aChars), nOfs, nLen);
  }

  /**
   * Check if the passed character is invalid for a attribute value node.
   *
   * @param eXMLVersion
   *        XML version to be used. May not be <code>null</code>.
   * @param c
   *        char to check
   * @return <code>true</code> if the char is invalid
   */
  @SuppressWarnings ("removal")
  public static boolean isInvalidXMLAttributeValueChar (@NonNull final EXMLSerializeVersion eXMLVersion, final int c)
  {
    return switch (eXMLVersion)
    {
      case XML_10 -> XML10.Singleton.XML10_INSTANCE.m_aInvalidValueChar.get (c);
      case XML_11 -> XML11.Singleton.XML11_INSTANCE.m_aInvalidAttrValueChar.get (c);
      case HTML -> HTML.Singleton.HTML_INSTANCE.m_aInvalidChar.get (c);
      default -> throw new IllegalArgumentException ("Unsupported XML version " + eXMLVersion + "!");
    };
  }

  /**
   * Check if the passed character sequence contains an invalid XML attribute value character within
   * the specified range.
   *
   * @param eXMLVersion
   *        XML version to be used. May not be <code>null</code>.
   * @param aCS
   *        The character sequence to check. May not be <code>null</code>.
   * @param nOfs
   *        The offset into the character sequence to start checking. Must be &ge; 0.
   * @param nLen
   *        The number of characters to check. Must be &ge; 0.
   * @return <code>true</code> if the specified range contains at least one invalid XML attribute value
   *         character.
   */
  private static boolean _containsInvalidXMLAttributeValueChar (@NonNull final EXMLSerializeVersion eXMLVersion,
                                                                @NonNull final CharSequence aCS,
                                                                @Nonnegative final int nOfs,
                                                                @Nonnegative final int nLen)
  {
    for (int i = 0; i < nLen; ++i)
    {
      final char c = aCS.charAt (nOfs + i);
      if (isInvalidXMLAttributeValueChar (eXMLVersion, c))
        return true;
    }
    return false;
  }

  /**
   * Check if the passed string contains an invalid XML attribute value character.
   *
   * @param eXMLVersion
   *        XML version to be used. May not be <code>null</code>.
   * @param s
   *        The string to check. May be <code>null</code>.
   * @return <code>true</code> if the string contains at least one invalid XML attribute value
   *         character.
   */
  public static boolean containsInvalidXMLAttributeValueChar (@NonNull final EXMLSerializeVersion eXMLVersion,
                                                              @Nullable final String s)
  {
    return s != null &&
           s.length () > 0 &&
           _containsInvalidXMLAttributeValueChar (eXMLVersion, s, 0, s.length ());
  }

  /**
   * Check if the passed character array contains an invalid XML attribute value character.
   *
   * @param eXMLVersion
   *        XML version to be used. May not be <code>null</code>.
   * @param aChars
   *        The character array to check. May be <code>null</code>.
   * @return <code>true</code> if the array contains at least one invalid XML attribute value
   *         character.
   */
  public static boolean containsInvalidXMLAttributeValueChar (@NonNull final EXMLSerializeVersion eXMLVersion,
                                                              final char @Nullable [] aChars)
  {
    return aChars != null &&
           aChars.length > 0 &&
           containsInvalidXMLAttributeValueChar (eXMLVersion, aChars, 0, aChars.length);
  }

  /**
   * Check if the passed character array contains an invalid XML attribute value character within
   * the specified range.
   *
   * @param eXMLVersion
   *        XML version to be used. May not be <code>null</code>.
   * @param aChars
   *        The character array to check. May be <code>null</code>.
   * @param nOfs
   *        The offset into the array to start checking. Must be &ge; 0.
   * @param nLen
   *        The number of characters to check. Must be &ge; 0.
   * @return <code>true</code> if the specified range contains at least one invalid XML attribute
   *         value character.
   */
  public static boolean containsInvalidXMLAttributeValueChar (@NonNull final EXMLSerializeVersion eXMLVersion,
                                                              final char @Nullable [] aChars,
                                                              @Nonnegative final int nOfs,
                                                              @Nonnegative final int nLen)
  {
    return aChars != null &&
           nLen > 0 &&
           _containsInvalidXMLAttributeValueChar (eXMLVersion, CharBuffer.wrap (aChars), nOfs, nLen);
  }

  /**
   * Get all invalid XML attribute value characters from the passed character sequence within the
   * specified range.
   *
   * @param eXMLVersion
   *        XML version to be used. May not be <code>null</code>.
   * @param aCS
   *        The character sequence to check. May not be <code>null</code>.
   * @param nOfs
   *        The offset into the character sequence to start checking. Must be &ge; 0.
   * @param nLen
   *        The number of characters to check. Must be &ge; 0.
   * @return A non-<code>null</code> set of all invalid characters.
   */
  @NonNull
  @ReturnsMutableCopy
  private static ICommonsOrderedSet <Character> _getAllInvalidXMLAttributeValueChars (@NonNull final EXMLSerializeVersion eXMLVersion,
                                                                                      @NonNull final CharSequence aCS,
                                                                                      @Nonnegative final int nOfs,
                                                                                      @Nonnegative final int nLen)
  {
    final ICommonsOrderedSet <Character> ret = new CommonsLinkedHashSet <> ();
    for (int i = 0; i < nLen; ++i)
    {
      final char c = aCS.charAt (nOfs + i);
      if (isInvalidXMLAttributeValueChar (eXMLVersion, c))
        ret.add (Character.valueOf (c));
    }
    return ret;
  }

  /**
   * Get all invalid XML attribute value characters from the passed string.
   *
   * @param eXMLVersion
   *        XML version to be used. May not be <code>null</code>.
   * @param s
   *        The string to check. May be <code>null</code>.
   * @return <code>null</code> if the input is <code>null</code> or empty, a non-<code>null</code>
   *         set of all invalid characters otherwise.
   */
  @Nullable
  @ReturnsMutableCopy
  public static ICommonsOrderedSet <Character> getAllInvalidXMLAttributeValueChars (@NonNull final EXMLSerializeVersion eXMLVersion,
                                                                                    @Nullable final String s)
  {
    return s == null || s.length () == 0 ? null
                                         : _getAllInvalidXMLAttributeValueChars (eXMLVersion, s, 0, s.length ());
  }

  /**
   * Get all invalid XML attribute value characters from the passed character array.
   *
   * @param eXMLVersion
   *        XML version to be used. May not be <code>null</code>.
   * @param aChars
   *        The character array to check. May be <code>null</code>.
   * @return <code>null</code> if the input is <code>null</code> or empty, a non-<code>null</code>
   *         set of all invalid characters otherwise.
   */
  @Nullable
  @ReturnsMutableCopy
  public static ICommonsOrderedSet <Character> getAllInvalidXMLAttributeValueChars (@NonNull final EXMLSerializeVersion eXMLVersion,
                                                                                    final char @Nullable [] aChars)
  {
    return aChars == null || aChars.length == 0 ? null
                                                : getAllInvalidXMLAttributeValueChars (eXMLVersion,
                                                                                       aChars,
                                                                                       0,
                                                                                       aChars.length);
  }

  /**
   * Get all invalid XML attribute value characters from the passed character array within the
   * specified range.
   *
   * @param eXMLVersion
   *        XML version to be used. May not be <code>null</code>.
   * @param aChars
   *        The character array to check. May be <code>null</code>.
   * @param nOfs
   *        The offset into the array to start checking. Must be &ge; 0.
   * @param nLen
   *        The number of characters to check. Must be &ge; 0.
   * @return <code>null</code> if the input is <code>null</code> or empty, a non-<code>null</code>
   *         set of all invalid characters otherwise.
   */
  @Nullable
  @ReturnsMutableCopy
  public static ICommonsOrderedSet <Character> getAllInvalidXMLAttributeValueChars (@NonNull final EXMLSerializeVersion eXMLVersion,
                                                                                    final char @Nullable [] aChars,
                                                                                    @Nonnegative final int nOfs,
                                                                                    @Nonnegative final int nLen)
  {
    if (aChars == null || nLen <= 0)
      return null;

    return _getAllInvalidXMLAttributeValueChars (eXMLVersion, CharBuffer.wrap (aChars), nOfs, nLen);
  }

  /**
   * Check if the passed character sequence contains an invalid XML character for the given
   * character mode within the specified range.
   *
   * @param eXMLVersion
   *        XML version to be used. May not be <code>null</code>.
   * @param eXMLCharMode
   *        The XML character mode to check against. May not be <code>null</code>.
   * @param aCS
   *        The character sequence to check. May not be <code>null</code>.
   * @param nOfs
   *        The offset into the character sequence to start checking. Must be &ge; 0.
   * @param nLen
   *        The number of characters to check. Must be &ge; 0.
   * @return <code>true</code> if the specified range contains at least one invalid XML character
   *         for the given mode.
   */
  private static boolean _containsInvalidXMLChar (@NonNull final EXMLSerializeVersion eXMLVersion,
                                                  @NonNull final EXMLCharMode eXMLCharMode,
                                                  @NonNull final CharSequence aCS,
                                                  @Nonnegative final int nOfs,
                                                  @Nonnegative final int nLen)
  {
    return switch (eXMLCharMode)
    {
      case ELEMENT_NAME, ATTRIBUTE_NAME -> _containsInvalidXMLNameChar (eXMLVersion, aCS, nOfs, nLen);
      case ATTRIBUTE_VALUE_DOUBLE_QUOTES, ATTRIBUTE_VALUE_SINGLE_QUOTES -> _containsInvalidXMLAttributeValueChar (eXMLVersion,
                                                                                                                  aCS,
                                                                                                                  nOfs,
                                                                                                                  nLen);
      case TEXT -> _containsInvalidXMLTextChar (eXMLVersion, aCS, nOfs, nLen);
      case CDATA -> _containsInvalidXMLCDATAChar (eXMLVersion, aCS, nOfs, nLen);
      default -> throw new IllegalArgumentException ("Unsupported XML character mode " + eXMLCharMode + "!");
    };
  }

  /**
   * Check if the passed string contains an invalid XML character for the given character mode.
   *
   * @param eXMLVersion
   *        XML version to be used. May not be <code>null</code>.
   * @param eXMLCharMode
   *        The XML character mode to check against. May not be <code>null</code>.
   * @param s
   *        The string to check. May be <code>null</code>.
   * @return <code>true</code> if the string contains at least one invalid XML character for the
   *         given mode.
   */
  public static boolean containsInvalidXMLChar (@NonNull final EXMLSerializeVersion eXMLVersion,
                                                @NonNull final EXMLCharMode eXMLCharMode,
                                                @Nullable final String s)
  {
    return s != null &&
           s.length () > 0 &&
           _containsInvalidXMLChar (eXMLVersion, eXMLCharMode, s, 0, s.length ());
  }

  /**
   * Check if the passed character array contains an invalid XML character for the given character
   * mode.
   *
   * @param eXMLVersion
   *        XML version to be used. May not be <code>null</code>.
   * @param eXMLCharMode
   *        The XML character mode to check against. May not be <code>null</code>.
   * @param aChars
   *        The character array to check. May be <code>null</code>.
   * @return <code>true</code> if the array contains at least one invalid XML character for the
   *         given mode.
   */
  public static boolean containsInvalidXMLChar (@NonNull final EXMLSerializeVersion eXMLVersion,
                                                @NonNull final EXMLCharMode eXMLCharMode,
                                                final char @Nullable [] aChars)
  {
    return aChars != null &&
           aChars.length > 0 &&
           containsInvalidXMLChar (eXMLVersion, eXMLCharMode, aChars, 0, aChars.length);
  }

  /**
   * Check if the passed character array contains an invalid XML character for the given character
   * mode within the specified range.
   *
   * @param eXMLVersion
   *        XML version to be used. May not be <code>null</code>.
   * @param eXMLCharMode
   *        The XML character mode to check against. May not be <code>null</code>.
   * @param aChars
   *        The character array to check. May be <code>null</code>.
   * @param nOfs
   *        The offset into the array to start checking. Must be &ge; 0.
   * @param nLen
   *        The number of characters to check. Must be &ge; 0.
   * @return <code>true</code> if the specified range contains at least one invalid XML character
   *         for the given mode.
   */
  public static boolean containsInvalidXMLChar (@NonNull final EXMLSerializeVersion eXMLVersion,
                                                @NonNull final EXMLCharMode eXMLCharMode,
                                                final char @Nullable [] aChars,
                                                @Nonnegative final int nOfs,
                                                @Nonnegative final int nLen)
  {
    return switch (eXMLCharMode)
    {
      case ELEMENT_NAME, ATTRIBUTE_NAME -> containsInvalidXMLNameChar (eXMLVersion, aChars, nOfs, nLen);
      case ATTRIBUTE_VALUE_DOUBLE_QUOTES, ATTRIBUTE_VALUE_SINGLE_QUOTES -> containsInvalidXMLAttributeValueChar (eXMLVersion,
                                                                                                                 aChars,
                                                                                                                 nOfs,
                                                                                                                 nLen);
      case TEXT -> containsInvalidXMLTextChar (eXMLVersion, aChars, nOfs, nLen);
      case CDATA -> containsInvalidXMLCDATAChar (eXMLVersion, aChars, nOfs, nLen);
      default -> throw new IllegalArgumentException ("Unsupported XML character mode " + eXMLCharMode + "!");
    };
  }

  /**
   * Get all invalid XML characters for the given character mode from the passed character
   * sequence within the specified range.
   *
   * @param eXMLVersion
   *        XML version to be used. May not be <code>null</code>.
   * @param eXMLCharMode
   *        The XML character mode to check against. May not be <code>null</code>.
   * @param aCS
   *        The character sequence to check. May not be <code>null</code>.
   * @param nOfs
   *        The offset into the character sequence to start checking. Must be &ge; 0.
   * @param nLen
   *        The number of characters to check. Must be &ge; 0.
   * @return A non-<code>null</code> set of all invalid characters.
   */
  @NonNull
  @ReturnsMutableCopy
  private static ICommonsOrderedSet <Character> _getAllInvalidXMLChars (@NonNull final EXMLSerializeVersion eXMLVersion,
                                                                        @NonNull final EXMLCharMode eXMLCharMode,
                                                                        @NonNull final CharSequence aCS,
                                                                        @Nonnegative final int nOfs,
                                                                        @Nonnegative final int nLen)
  {
    return switch (eXMLCharMode)
    {
      case ELEMENT_NAME, ATTRIBUTE_NAME -> _getAllInvalidXMLNameChars (eXMLVersion, aCS, nOfs, nLen);
      case ATTRIBUTE_VALUE_DOUBLE_QUOTES, ATTRIBUTE_VALUE_SINGLE_QUOTES -> _getAllInvalidXMLAttributeValueChars (eXMLVersion,
                                                                                                                 aCS,
                                                                                                                 nOfs,
                                                                                                                 nLen);
      case TEXT -> _getAllInvalidXMLTextChars (eXMLVersion, aCS, nOfs, nLen);
      case CDATA -> _getAllInvalidXMLCDATAChars (eXMLVersion, aCS, nOfs, nLen);
      default -> throw new IllegalArgumentException ("Unsupported XML character mode " + eXMLCharMode + "!");
    };
  }

  /**
   * Get all invalid XML characters for the given character mode from the passed string.
   *
   * @param eXMLVersion
   *        XML version to be used. May not be <code>null</code>.
   * @param eXMLCharMode
   *        The XML character mode to check against. May not be <code>null</code>.
   * @param s
   *        The string to check. May be <code>null</code>.
   * @return <code>null</code> if the input is <code>null</code> or empty, a non-<code>null</code>
   *         set of all invalid characters otherwise.
   */
  @Nullable
  @ReturnsMutableCopy
  public static ICommonsOrderedSet <Character> getAllInvalidXMLChars (@NonNull final EXMLSerializeVersion eXMLVersion,
                                                                      @NonNull final EXMLCharMode eXMLCharMode,
                                                                      @Nullable final String s)
  {
    return s == null || s.length () == 0 ? null
                                         : _getAllInvalidXMLChars (eXMLVersion, eXMLCharMode, s, 0, s.length ());
  }

  /**
   * Get all invalid XML characters for the given character mode from the passed character array.
   *
   * @param eXMLVersion
   *        XML version to be used. May not be <code>null</code>.
   * @param eXMLCharMode
   *        The XML character mode to check against. May not be <code>null</code>.
   * @param aChars
   *        The character array to check. May be <code>null</code>.
   * @return <code>null</code> if the input is <code>null</code> or empty, a non-<code>null</code>
   *         set of all invalid characters otherwise.
   */
  @Nullable
  @ReturnsMutableCopy
  public static ICommonsOrderedSet <Character> getAllInvalidXMLChars (@NonNull final EXMLSerializeVersion eXMLVersion,
                                                                      @NonNull final EXMLCharMode eXMLCharMode,
                                                                      final char @Nullable [] aChars)
  {
    return aChars == null || aChars.length == 0 ? null
                                                : getAllInvalidXMLChars (eXMLVersion,
                                                                         eXMLCharMode,
                                                                         aChars,
                                                                         0,
                                                                         aChars.length);
  }

  /**
   * Get all invalid XML characters for the given character mode from the passed character array
   * within the specified range.
   *
   * @param eXMLVersion
   *        XML version to be used. May not be <code>null</code>.
   * @param eXMLCharMode
   *        The XML character mode to check against. May not be <code>null</code>.
   * @param aChars
   *        The character array to check. May be <code>null</code>.
   * @param nOfs
   *        The offset into the array to start checking. Must be &ge; 0.
   * @param nLen
   *        The number of characters to check. Must be &ge; 0.
   * @return <code>null</code> if the input is <code>null</code> or empty, a non-<code>null</code>
   *         set of all invalid characters otherwise.
   */
  @Nullable
  @ReturnsMutableCopy
  public static ICommonsOrderedSet <Character> getAllInvalidXMLChars (@NonNull final EXMLSerializeVersion eXMLVersion,
                                                                      @NonNull final EXMLCharMode eXMLCharMode,
                                                                      final char @Nullable [] aChars,
                                                                      @Nonnegative final int nOfs,
                                                                      @Nonnegative final int nLen)
  {
    return switch (eXMLCharMode)
    {
      case ELEMENT_NAME, ATTRIBUTE_NAME -> getAllInvalidXMLNameChars (eXMLVersion, aChars, nOfs, nLen);
      case ATTRIBUTE_VALUE_DOUBLE_QUOTES, ATTRIBUTE_VALUE_SINGLE_QUOTES -> getAllInvalidXMLAttributeValueChars (eXMLVersion,
                                                                                                                aChars,
                                                                                                                nOfs,
                                                                                                                nLen);
      case TEXT -> getAllInvalidXMLTextChars (eXMLVersion, aChars, nOfs, nLen);
      case CDATA -> getAllInvalidXMLCDATAChars (eXMLVersion, aChars, nOfs, nLen);
      default -> throw new IllegalArgumentException ("Unsupported XML character mode " + eXMLCharMode + "!");
    };
  }
}
