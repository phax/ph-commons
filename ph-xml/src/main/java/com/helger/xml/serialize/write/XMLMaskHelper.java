/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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

import java.io.IOException;
import java.io.Writer;
import java.util.Set;

import com.helger.annotation.Nonempty;
import com.helger.annotation.Nonnegative;
import com.helger.annotation.style.PresentForCodeCoverage;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.annotation.style.ReturnsMutableObject;
import com.helger.base.CGlobal;
import com.helger.commons.collection.ArrayHelper;
import com.helger.commons.collection.impl.ICommonsOrderedSet;
import com.helger.commons.collection.impl.ICommonsSet;
import com.helger.commons.string.StringHelper;
import com.helger.commons.valueenforcer.ValueEnforcer;
import com.helger.xml.EXMLVersion;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * This class contains all the methods for masking XML content.
 *
 * @author Philip Helger
 */
public final class XMLMaskHelper
{
  private static final char TAB = '\t';
  private static final char CR = '\r';
  private static final char LF = '\n';
  private static final char DOUBLE_QUOTE = '"';
  private static final char AMPERSAND = '&';
  private static final char LT = '<';
  private static final char GT = '>';
  private static final char APOS = '\'';

  // XML 1.0
  private static final char [] MASK_ATTRIBUTE_VALUE_XML10_DQ = { TAB, LF, CR, DOUBLE_QUOTE, AMPERSAND, LT };
  private static final char [] MASK_ATTRIBUTE_VALUE_XML10_SQ = { TAB, LF, CR, APOS, AMPERSAND, LT };
  private static final char [] MASK_TEXT_XML10 = { CR,
                                                   AMPERSAND,
                                                   LT,
                                                   GT,
                                                   0x7f,
                                                   0x80,
                                                   0x81,
                                                   0x82,
                                                   0x83,
                                                   0x84,
                                                   0x85,
                                                   0x86,
                                                   0x87,
                                                   0x88,
                                                   0x89,
                                                   0x8a,
                                                   0x8b,
                                                   0x8c,
                                                   0x8d,
                                                   0x8e,
                                                   0x8f,
                                                   0x90,
                                                   0x91,
                                                   0x92,
                                                   0x93,
                                                   0x94,
                                                   0x95,
                                                   0x96,
                                                   0x97,
                                                   0x98,
                                                   0x99,
                                                   0x9a,
                                                   0x9b,
                                                   0x9c,
                                                   0x9d,
                                                   0x9e,
                                                   0x9f };

  // XML 1.1
  private static final char [] MASK_ATTRIBUTE_VALUE_XML11_DQ = { 0x1,
                                                                 0x2,
                                                                 0x3,
                                                                 0x4,
                                                                 0x5,
                                                                 0x6,
                                                                 0x7,
                                                                 0x8,
                                                                 0x9,
                                                                 0xa,
                                                                 0xb,
                                                                 0xc,
                                                                 0xd,
                                                                 0xe,
                                                                 0xf,
                                                                 0x10,
                                                                 0x11,
                                                                 0x12,
                                                                 0x13,
                                                                 0x14,
                                                                 0x15,
                                                                 0x16,
                                                                 0x17,
                                                                 0x18,
                                                                 0x19,
                                                                 0x1a,
                                                                 0x1b,
                                                                 0x1c,
                                                                 0x1d,
                                                                 0x1e,
                                                                 0x1f,
                                                                 DOUBLE_QUOTE,
                                                                 AMPERSAND,
                                                                 LT };
  private static final char [] MASK_ATTRIBUTE_VALUE_XML11_SQ = { 0x1,
                                                                 0x2,
                                                                 0x3,
                                                                 0x4,
                                                                 0x5,
                                                                 0x6,
                                                                 0x7,
                                                                 0x8,
                                                                 0x9,
                                                                 0xa,
                                                                 0xb,
                                                                 0xc,
                                                                 0xd,
                                                                 0xe,
                                                                 0xf,
                                                                 0x10,
                                                                 0x11,
                                                                 0x12,
                                                                 0x13,
                                                                 0x14,
                                                                 0x15,
                                                                 0x16,
                                                                 0x17,
                                                                 0x18,
                                                                 0x19,
                                                                 0x1a,
                                                                 0x1b,
                                                                 0x1c,
                                                                 0x1d,
                                                                 0x1e,
                                                                 0x1f,
                                                                 APOS,
                                                                 AMPERSAND,
                                                                 LT };
  private static final char [] MASK_TEXT_XML11 = { 0x1,
                                                   0x2,
                                                   0x3,
                                                   0x4,
                                                   0x5,
                                                   0x6,
                                                   0x7,
                                                   0x8,
                                                   0xb,
                                                   0xc,
                                                   0xd,
                                                   0xe,
                                                   0xf,
                                                   0x10,
                                                   0x11,
                                                   0x12,
                                                   0x13,
                                                   0x14,
                                                   0x15,
                                                   0x16,
                                                   0x17,
                                                   0x18,
                                                   0x19,
                                                   0x1a,
                                                   0x1b,
                                                   0x1c,
                                                   0x1d,
                                                   0x1e,
                                                   0x1f,
                                                   AMPERSAND,
                                                   LT,
                                                   GT,
                                                   0x7f,
                                                   0x80,
                                                   0x81,
                                                   0x82,
                                                   0x83,
                                                   0x84,
                                                   0x85,
                                                   0x86,
                                                   0x87,
                                                   0x88,
                                                   0x89,
                                                   0x8a,
                                                   0x8b,
                                                   0x8c,
                                                   0x8d,
                                                   0x8e,
                                                   0x8f,
                                                   0x90,
                                                   0x91,
                                                   0x92,
                                                   0x93,
                                                   0x94,
                                                   0x95,
                                                   0x96,
                                                   0x97,
                                                   0x98,
                                                   0x99,
                                                   0x9a,
                                                   0x9b,
                                                   0x9c,
                                                   0x9d,
                                                   0x9e,
                                                   0x9f,
                                                   0x2028 };

  // HTML
  private static final char [] MASK_TEXT_HTML_DQ = { AMPERSAND,
                                                     DOUBLE_QUOTE,
                                                     LT,
                                                     GT,
                                                     APOS,
                                                     0x80,
                                                     0x81,
                                                     0x82,
                                                     0x83,
                                                     0x84,
                                                     0x85,
                                                     0x86,
                                                     0x87,
                                                     0x88,
                                                     0x89,
                                                     0x8a,
                                                     0x8b,
                                                     0x8c,
                                                     0x8d,
                                                     0x8e,
                                                     0x8f,
                                                     0x90,
                                                     0x91,
                                                     0x92,
                                                     0x93,
                                                     0x94,
                                                     0x95,
                                                     0x96,
                                                     0x97,
                                                     0x98,
                                                     0x99,
                                                     0x9a,
                                                     0x9b,
                                                     0x9c,
                                                     0x9d,
                                                     0x9e,
                                                     0x9f };
  private static final char [] MASK_TEXT_HTML_SQ = { AMPERSAND,
                                                     LT,
                                                     GT,
                                                     APOS,
                                                     0x80,
                                                     0x81,
                                                     0x82,
                                                     0x83,
                                                     0x84,
                                                     0x85,
                                                     0x86,
                                                     0x87,
                                                     0x88,
                                                     0x89,
                                                     0x8a,
                                                     0x8b,
                                                     0x8c,
                                                     0x8d,
                                                     0x8e,
                                                     0x8f,
                                                     0x90,
                                                     0x91,
                                                     0x92,
                                                     0x93,
                                                     0x94,
                                                     0x95,
                                                     0x96,
                                                     0x97,
                                                     0x98,
                                                     0x99,
                                                     0x9a,
                                                     0x9b,
                                                     0x9c,
                                                     0x9d,
                                                     0x9e,
                                                     0x9f };

  // XML 1.0
  private static final char [] [] MASK_ATTRIBUTE_VALUE_XML10_DQ_REPLACE = new char [MASK_ATTRIBUTE_VALUE_XML10_DQ.length] [];
  private static final char [] [] MASK_ATTRIBUTE_VALUE_XML10_SQ_REPLACE = new char [MASK_ATTRIBUTE_VALUE_XML10_SQ.length] [];
  private static final char [] [] MASK_TEXT_XML10_REPLACE = new char [MASK_TEXT_XML10.length] [];

  // XML 1.1
  private static final char [] [] MASK_ATTRIBUTE_VALUE_XML11_DQ_REPLACE = new char [MASK_ATTRIBUTE_VALUE_XML11_DQ.length] [];
  private static final char [] [] MASK_ATTRIBUTE_VALUE_XML11_SQ_REPLACE = new char [MASK_ATTRIBUTE_VALUE_XML11_SQ.length] [];
  private static final char [] [] MASK_TEXT_XML11_REPLACE = new char [MASK_TEXT_XML11.length] [];

  // HTML
  private static final char [] [] MASK_TEXT_HTML_DQ_REPLACE = new char [MASK_TEXT_HTML_DQ.length] [];
  private static final char [] [] MASK_TEXT_HTML_SQ_REPLACE = new char [MASK_TEXT_HTML_SQ.length] [];

  private static final char [] INT_HEX_UC = "0123456789ABCDEF".toCharArray ();

  @Nonnull
  public static String getXMLNumericReference (final char n)
  {
    final StringBuilder aSB = new StringBuilder (16);
    aSB.append ("&#x");
    // Don't use Integer.toString because it delivers lowercase chars
    if (n > 0xfff)
      aSB.append (INT_HEX_UC[(n >> 12) & 0xf]);
    if (n > 0xff)
      aSB.append (INT_HEX_UC[(n >> 8) & 0xf]);
    if (n > 0xf)
      aSB.append (INT_HEX_UC[(n >> 4) & 0xf]);
    aSB.append (INT_HEX_UC[n & 0xf]);
    aSB.append (';');
    return aSB.toString ();
  }

  /**
   * Get the entity reference for the specified character. This returns e.g.
   * &amp;lt; for '&lt;' etc. This method has special handling for &lt;, &gt;,
   * &amp;, &quot; and '. All other chars are encoded by their numeric value
   * (e.g. &amp;#200;)
   *
   * @param c
   *        Character to use.
   * @return The entity reference string. Never <code>null</code> nor empty.
   */
  @Nonnull
  @Nonempty
  public static String getXML10EntityReferenceString (final char c)
  {
    if (c == LT)
      return "&lt;";
    if (c == GT)
      return "&gt;";
    if (c == AMPERSAND)
      return "&amp;";
    if (c == DOUBLE_QUOTE)
      return "&quot;";
    if (c == APOS)
      return "&apos;";
    return getXMLNumericReference (c);
  }

  /**
   * Get the entity reference for the specified character. This returns e.g.
   * &amp;lt; for '&lt;' etc. This method has special handling for &lt;, &gt;,
   * &amp;, &quot; and '. All other chars are encoded by their numeric value
   * (e.g. &amp;#200;)
   *
   * @param c
   *        Character to use.
   * @return The entity reference string. Never <code>null</code> nor empty.
   */
  @Nonnull
  @Nonempty
  public static String getXML11EntityReferenceString (final char c)
  {
    if (c == LT)
      return "&lt;";
    if (c == GT)
      return "&gt;";
    if (c == AMPERSAND)
      return "&amp;";
    if (c == DOUBLE_QUOTE)
      return "&quot;";
    if (c == APOS)
      return "&apos;";
    if (c == '\u2028')
      return "\n";
    return getXMLNumericReference (c);
  }

  /**
   * Get the entity reference for the specified character. This returns e.g.
   * <code>&amp;lt;</code> for '<code>&lt;</code>' etc. This method has special
   * handling for &lt;, &gt;, &amp; and &quot;. All other chars are encoded by
   * their numeric value (e.g. <code>&amp;#200;</code>). In contrast to
   * {@link #getXML10EntityReferenceString(char)} this method does not handle
   * <code>&amp;apos;</code>
   *
   * @param c
   *        Character to use.
   * @return The entity reference string. Never <code>null</code> nor empty.
   */
  @Nonnull
  @Nonempty
  public static String getHTMLEntityReferenceString (final char c)
  {
    if (c == LT)
      return "&lt;";
    if (c == GT)
      return "&gt;";
    if (c == AMPERSAND)
      return "&amp;";
    if (c == DOUBLE_QUOTE)
      return "&quot;";
    // Use of &apos; in XHTML should generally be avoided for compatibility
    // reasons. &#39; or &#x0027; may be used instead.
    return "&#" + (int) c + ";";
  }

  static
  {
    // XML 1.0
    for (int i = 0; i < MASK_ATTRIBUTE_VALUE_XML10_DQ.length; ++i)
      MASK_ATTRIBUTE_VALUE_XML10_DQ_REPLACE[i] = getXML10EntityReferenceString (MASK_ATTRIBUTE_VALUE_XML10_DQ[i]).toCharArray ();
    for (int i = 0; i < MASK_ATTRIBUTE_VALUE_XML10_SQ.length; ++i)
      MASK_ATTRIBUTE_VALUE_XML10_SQ_REPLACE[i] = getXML10EntityReferenceString (MASK_ATTRIBUTE_VALUE_XML10_SQ[i]).toCharArray ();
    for (int i = 0; i < MASK_TEXT_XML10.length; ++i)
      MASK_TEXT_XML10_REPLACE[i] = getXML10EntityReferenceString (MASK_TEXT_XML10[i]).toCharArray ();

    // XML 1.1
    for (int i = 0; i < MASK_ATTRIBUTE_VALUE_XML11_DQ.length; ++i)
      MASK_ATTRIBUTE_VALUE_XML11_DQ_REPLACE[i] = getXML11EntityReferenceString (MASK_ATTRIBUTE_VALUE_XML11_DQ[i]).toCharArray ();
    for (int i = 0; i < MASK_ATTRIBUTE_VALUE_XML11_SQ.length; ++i)
      MASK_ATTRIBUTE_VALUE_XML11_SQ_REPLACE[i] = getXML11EntityReferenceString (MASK_ATTRIBUTE_VALUE_XML11_SQ[i]).toCharArray ();
    for (int i = 0; i < MASK_TEXT_XML11.length; ++i)
      MASK_TEXT_XML11_REPLACE[i] = getXML11EntityReferenceString (MASK_TEXT_XML11[i]).toCharArray ();

    // HTML
    for (int i = 0; i < MASK_TEXT_HTML_DQ.length; ++i)
      MASK_TEXT_HTML_DQ_REPLACE[i] = getHTMLEntityReferenceString (MASK_TEXT_HTML_DQ[i]).toCharArray ();
    for (int i = 0; i < MASK_TEXT_HTML_SQ.length; ++i)
      MASK_TEXT_HTML_SQ_REPLACE[i] = getHTMLEntityReferenceString (MASK_TEXT_HTML_SQ[i]).toCharArray ();
  }

  @PresentForCodeCoverage
  private static final XMLMaskHelper INSTANCE = new XMLMaskHelper ();

  private XMLMaskHelper ()
  {}

  @Nullable
  @ReturnsMutableObject ("internal use only")
  private static char [] _findSourceMap (@Nonnull final EXMLSerializeVersion eXMLVersion,
                                         @Nonnull final EXMLCharMode eXMLCharMode)
  {
    switch (eXMLVersion)
    {
      case XML_10:
        switch (eXMLCharMode)
        {
          case ATTRIBUTE_VALUE_DOUBLE_QUOTES:
            return MASK_ATTRIBUTE_VALUE_XML10_DQ;
          case ATTRIBUTE_VALUE_SINGLE_QUOTES:
            return MASK_ATTRIBUTE_VALUE_XML10_SQ;
          case TEXT:
            return MASK_TEXT_XML10;
          default:
            break;
        }
        break;
      case XML_11:
        switch (eXMLCharMode)
        {
          case ATTRIBUTE_VALUE_DOUBLE_QUOTES:
            return MASK_ATTRIBUTE_VALUE_XML11_DQ;
          case ATTRIBUTE_VALUE_SINGLE_QUOTES:
            return MASK_ATTRIBUTE_VALUE_XML11_SQ;
          case TEXT:
            return MASK_TEXT_XML11;
          default:
            break;
        }
        break;
      case HTML:
        switch (eXMLCharMode)
        {
          case ATTRIBUTE_VALUE_SINGLE_QUOTES:
            return MASK_TEXT_HTML_SQ;
          case ATTRIBUTE_VALUE_DOUBLE_QUOTES:
          case TEXT:
            return MASK_TEXT_HTML_DQ;
          default:
            break;
        }
        break;
      default:
        throw new IllegalArgumentException ("Unsupported XML version " + eXMLVersion + "!");
    }
    return null;
  }

  @Nullable
  @ReturnsMutableObject ("internal use only")
  private static char [] [] _findReplaceMap (@Nonnull final EXMLSerializeVersion eXMLVersion,
                                             @Nonnull final EXMLCharMode eXMLCharMode)
  {
    switch (eXMLVersion)
    {
      case XML_10:
        switch (eXMLCharMode)
        {
          case ATTRIBUTE_VALUE_DOUBLE_QUOTES:
            return MASK_ATTRIBUTE_VALUE_XML10_DQ_REPLACE;
          case ATTRIBUTE_VALUE_SINGLE_QUOTES:
            return MASK_ATTRIBUTE_VALUE_XML10_SQ_REPLACE;
          case TEXT:
            return MASK_TEXT_XML10_REPLACE;
          default:
            break;
        }
        break;
      case XML_11:
        switch (eXMLCharMode)
        {
          case ATTRIBUTE_VALUE_DOUBLE_QUOTES:
            return MASK_ATTRIBUTE_VALUE_XML11_DQ_REPLACE;
          case ATTRIBUTE_VALUE_SINGLE_QUOTES:
            return MASK_ATTRIBUTE_VALUE_XML11_SQ_REPLACE;
          case TEXT:
            return MASK_TEXT_XML11_REPLACE;
          default:
            break;
        }
        break;
      case HTML:
        switch (eXMLCharMode)
        {
          case ATTRIBUTE_VALUE_SINGLE_QUOTES:
            return MASK_TEXT_HTML_SQ_REPLACE;
          case ATTRIBUTE_VALUE_DOUBLE_QUOTES:
          case TEXT:
            return MASK_TEXT_HTML_DQ_REPLACE;
          default:
            break;
        }
        break;
      default:
        throw new IllegalArgumentException ("Unsupported XML version " + eXMLVersion + "!");
    }
    return null;
  }

  /**
   * Convert the passed set to an array
   *
   * @param aChars
   *        Character set to use. May not be <code>null</code>.
   * @return A new array with the same length as the source set.
   */
  @Nonnull
  @ReturnsMutableCopy
  private static char [] _getAsCharArray (@Nonnull final Set <Character> aChars)
  {
    ValueEnforcer.notNull (aChars, "Chars");

    final char [] ret = new char [aChars.size ()];
    int nIndex = 0;
    for (final Character aChar : aChars)
      ret[nIndex++] = aChar.charValue ();
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  private static char [] [] _createEmptyReplacement (@Nonnull final char [] aSrcMap)
  {
    final char [] [] ret = new char [aSrcMap.length] [];
    for (int i = 0; i < aSrcMap.length; ++i)
      ret[i] = ArrayHelper.EMPTY_CHAR_ARRAY;
    return ret;
  }

  @Nonnull
  public static char [] getMaskedXMLText (@Nonnull final EXMLSerializeVersion eXMLVersion,
                                          @Nonnull final EXMLCharMode eXMLCharMode,
                                          @Nonnull final EXMLIncorrectCharacterHandling eIncorrectCharHandling,
                                          @Nullable final String s)
  {
    if (StringHelper.hasNoText (s))
      return ArrayHelper.EMPTY_CHAR_ARRAY;

    char [] aChars = s.toCharArray ();

    // 1. do incorrect character handling
    if (eIncorrectCharHandling.isTestRequired ())
    {
      if (XMLCharHelper.containsInvalidXMLChar (eXMLVersion, eXMLCharMode, aChars))
      {
        final ICommonsSet <Character> aAllInvalidChars = XMLCharHelper.getAllInvalidXMLChars (eXMLVersion,
                                                                                              eXMLCharMode,
                                                                                              aChars);
        eIncorrectCharHandling.notifyOnInvalidXMLCharacter (s, aAllInvalidChars);
        if (eIncorrectCharHandling.isReplaceWithNothing ())
        {
          final char [] aSrcMap = _getAsCharArray (aAllInvalidChars);
          final char [] [] aDstMap = _createEmptyReplacement (aSrcMap);
          aChars = StringHelper.replaceMultiple (s, aSrcMap, aDstMap);
        }
      }
    }

    // 2. perform entity replacements if necessary
    final char [] aSrcMap = _findSourceMap (eXMLVersion, eXMLCharMode);
    if (aSrcMap == null)
    {
      // Nothing to replace
      return aChars;
    }
    final char [] [] aDstMap = _findReplaceMap (eXMLVersion, eXMLCharMode);
    return StringHelper.replaceMultiple (aChars, aSrcMap, aDstMap);
  }

  @Nonnegative
  public static int getMaskedXMLTextLength (@Nonnull final EXMLVersion eXMLVersion,
                                            @Nonnull final EXMLCharMode eXMLCharMode,
                                            @Nonnull final EXMLIncorrectCharacterHandling eIncorrectCharHandling,
                                            @Nullable final String s)
  {
    return getMaskedXMLTextLength (EXMLSerializeVersion.getFromXMLVersionOrThrow (eXMLVersion),
                                   eXMLCharMode,
                                   eIncorrectCharHandling,
                                   s);
  }

  @Nonnegative
  public static int getMaskedXMLTextLength (@Nonnull final EXMLSerializeVersion eXMLVersion,
                                            @Nonnull final EXMLCharMode eXMLCharMode,
                                            @Nonnull final EXMLIncorrectCharacterHandling eIncorrectCharHandling,
                                            @Nullable final String s)
  {
    if (StringHelper.hasNoText (s))
      return 0;

    char [] aChars = s.toCharArray ();

    // 1. do incorrect character handling
    if (eIncorrectCharHandling.isTestRequired () &&
        XMLCharHelper.containsInvalidXMLChar (eXMLVersion, eXMLCharMode, aChars))
    {
      final ICommonsSet <Character> aAllInvalidChars = XMLCharHelper.getAllInvalidXMLChars (eXMLVersion,
                                                                                            eXMLCharMode,
                                                                                            aChars);
      eIncorrectCharHandling.notifyOnInvalidXMLCharacter (s, aAllInvalidChars);
      if (eIncorrectCharHandling.isReplaceWithNothing ())
      {
        final char [] aSrcMap = _getAsCharArray (aAllInvalidChars);
        final char [] [] aDstMap = _createEmptyReplacement (aSrcMap);
        aChars = StringHelper.replaceMultiple (s, aSrcMap, aDstMap);
      }
    }

    // 2. perform entity replacements if necessary
    final char [] aSrcMap = _findSourceMap (eXMLVersion, eXMLCharMode);
    final int ret;
    if (aSrcMap != null)
    {
      final char [] [] aDstMap = _findReplaceMap (eXMLVersion, eXMLCharMode);
      final int nResLen = StringHelper.getReplaceMultipleResultLength (aChars, aSrcMap, aDstMap);
      ret = nResLen == CGlobal.ILLEGAL_UINT ? aChars.length : nResLen;
    }
    else
    {
      // Nothing to replace
      ret = aChars.length;
    }
    // Reminder: Surrogate characters count as 2
    return ret;
  }

  public static void maskXMLTextTo (@Nonnull final EXMLSerializeVersion eXMLVersion,
                                    @Nonnull final EXMLCharMode eXMLCharMode,
                                    @Nonnull final EXMLIncorrectCharacterHandling eIncorrectCharHandling,
                                    @Nullable final String s,
                                    @Nonnull final Writer aWriter) throws IOException
  {
    if (StringHelper.hasText (s))
      maskXMLTextTo (eXMLVersion, eXMLCharMode, eIncorrectCharHandling, s.toCharArray (), 0, s.length (), aWriter);
  }

  public static void maskXMLTextTo (@Nonnull final EXMLSerializeVersion eXMLVersion,
                                    @Nonnull final EXMLCharMode eXMLCharMode,
                                    @Nonnull final EXMLIncorrectCharacterHandling eIncorrectCharHandling,
                                    @Nonnull final char [] aSrcText,
                                    @Nonnegative final int nOfs,
                                    @Nonnegative final int nLen,
                                    @Nonnull final Writer aWriter) throws IOException
  {
    if (nLen == 0)
      return;

    char [] aChars = aSrcText;
    int nRealOfs = nOfs;
    int nRealLen = nLen;

    // 1. do incorrect character handling
    if (eIncorrectCharHandling.isTestRequired ())
    {
      if (XMLCharHelper.containsInvalidXMLChar (eXMLVersion, eXMLCharMode, aChars, nRealOfs, nRealLen))
      {
        final ICommonsOrderedSet <Character> aAllInvalidChars = XMLCharHelper.getAllInvalidXMLChars (eXMLVersion,
                                                                                                     eXMLCharMode,
                                                                                                     aChars,
                                                                                                     nRealOfs,
                                                                                                     nRealLen);
        // Here we can convert, because this part should not be called very
        // often
        final String s = new String (aSrcText, nRealOfs, nRealLen);
        eIncorrectCharHandling.notifyOnInvalidXMLCharacter (s, aAllInvalidChars);
        if (eIncorrectCharHandling.isReplaceWithNothing ())
        {
          final char [] aSrcMap = _getAsCharArray (aAllInvalidChars);
          final char [] [] aDstMap = _createEmptyReplacement (aSrcMap);

          aChars = StringHelper.replaceMultiple (s, aSrcMap, aDstMap);
          nRealOfs = 0;
          nRealLen = aChars.length;
          if (nRealLen == 0)
          {
            // Nothing left after replacement
            return;
          }
        }
      }
    }

    // 2. perform entity replacements if necessary
    final char [] aSrcMap = _findSourceMap (eXMLVersion, eXMLCharMode);
    if (aSrcMap == null)
    {
      // Nothing to replace
      // TODO write code points as XML entities
      aWriter.write (aChars, nRealOfs, nRealLen);
    }
    else
    {
      final char [] [] aDstMap = _findReplaceMap (eXMLVersion, eXMLCharMode);
      // TODO write code points as XML entities
      StringHelper.replaceMultipleTo (aChars, nRealOfs, nRealLen, aSrcMap, aDstMap, aWriter);
    }
  }
}
