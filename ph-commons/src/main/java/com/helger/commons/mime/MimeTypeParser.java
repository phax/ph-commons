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
package com.helger.commons.mime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.Nonempty;
import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.PresentForCodeCoverage;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.array.ArrayHelper;
import com.helger.base.enforcer.ValueEnforcer;
import com.helger.base.string.Strings;
import com.helger.commons.codec.DecodeException;
import com.helger.commons.codec.RFC2616Codec;
import com.helger.commons.string.StringHelper;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * This class handles the String parsing of MIME types.
 *
 * @author Philip Helger
 */
@Immutable
public final class MimeTypeParser
{
  private static final Logger LOGGER = LoggerFactory.getLogger (MimeTypeParser.class);
  private static final char [] TSPECIAL = { '(',
                                            ')',
                                            '<',
                                            '>',
                                            '@',
                                            ',',
                                            ';',
                                            ':',
                                            '\\',
                                            '"',
                                            '/',
                                            '[',
                                            ']',
                                            '?',
                                            '=' };

  @PresentForCodeCoverage
  private static final MimeTypeParser INSTANCE = new MimeTypeParser ();

  private MimeTypeParser ()
  {}

  /**
   * @return A copy of the array with all TSpecial chars. Never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static char [] getAllTSpecialChars ()
  {
    return ArrayHelper.getCopy (TSPECIAL);
  }

  /**
   * Check if the passed character is a special character according to RFC 2045 chapter 5.1
   *
   * @param c
   *        The character to check
   * @return <code>true</code> if the character is a special character, <code>false</code>
   *         otherwise.
   */
  public static boolean isTSpecialChar (final char c)
  {
    return ArrayHelper.contains (TSPECIAL, c);
  }

  /**
   * Check if the passed character is a valid token character. According to RFC 2045 this can be
   * <em>any (US-ASCII) CHAR except SPACE, CTLs, or tspecials</em>
   *
   * @param c
   *        The character to check.
   * @return <code>true</code> if the passed character is a valid token character,
   *         <code>false</code> otherwise
   */
  public static boolean isTokenChar (final char c)
  {
    // SPACE: 32
    // CTLs: 0-31, 127
    return c > 32 && c < 127 && !isTSpecialChar (c);
  }

  /**
   * Check if the passed string is a valid MIME token by checking that the length is at least 1 and
   * all chars match the {@link #isTokenChar(char)} condition.
   *
   * @param sToken
   *        The token to be checked. May be <code>null</code>.
   * @return <code>true</code> if the passed string is valid token, <code>false</code> otherwise.
   */
  public static boolean isToken (@Nullable final String sToken)
  {
    // Check length
    if (Strings.isEmpty (sToken))
      return false;

    // Check that all chars are token chars
    final char [] aChars = sToken.toCharArray ();
    for (final char c : aChars)
      if (!isTokenChar (c))
        return false;
    return true;
  }

  private static void _parseAndAddParameters (@Nonnull final MimeType aMimeType,
                                              @Nonnull @Nonempty final String sParameters,
                                              @Nonnull final EMimeQuoting eQuotingAlgorithm) throws MimeTypeParserException
  {
    if (eQuotingAlgorithm.isQuotedString ())
    {
      final char cSep = EMimeQuoting.QUOTED_STRING_SEPARATOR_CHAR;
      final char cMask = EMimeQuoting.QUOTED_STRING_MASK_CHAR;
      final char [] aParamChars = sParameters.toCharArray ();
      final int nMax = aParamChars.length;
      int nIndex = 0;
      while (true)
      {
        final int nNameStartIndex = nIndex;
        while (nIndex < nMax && MimeTypeParser.isTokenChar (aParamChars[nIndex]))
          ++nIndex;

        final String sParameterName = sParameters.substring (nNameStartIndex, nIndex);
        if (sParameterName.length () == 0)
          break;

        // Search separator char
        while (nIndex < nMax && aParamChars[nIndex] != CMimeType.SEPARATOR_PARAMETER_NAME_VALUE)
          ++nIndex;

        if (nIndex == nMax)
          throw new MimeTypeParserException ("Missing parameter name/value separator");

        // Skip separator char
        ++nIndex;

        // Search the start of the value
        while (nIndex < nMax && aParamChars[nIndex] != cSep && !isTokenChar (aParamChars[nIndex]))
          ++nIndex;

        final StringBuilder aSB = new StringBuilder ();
        if (nIndex < nMax)
        {
          if (aParamChars[nIndex] == cSep)
          {
            // Quoted string!

            // Skip opening separator char
            ++nIndex;
            for (; nIndex < nMax; ++nIndex)
            {
              final char c = aParamChars[nIndex];
              if (c == cSep)
              {
                // End of quoted string
                break;
              }
              if (c == cMask)
              {
                // Unmask char
                if (nIndex == nMax - 1)
                  throw new MimeTypeParserException ("Illegal masking found at end of: " + sParameters);
                aSB.append (aParamChars[++nIndex]);
              }
              else
                aSB.append (c);
            }
            if (nIndex == nMax)
              throw new MimeTypeParserException ("Missing closing separator in quoted value");
            // Skip closing separator
            ++nIndex;
          }
          else
          {
            // Token-only parameter value
            while (nIndex < nMax && MimeTypeParser.isTokenChar (aParamChars[nIndex]))
            {
              aSB.append (aParamChars[nIndex]);
              ++nIndex;
            }
          }
        }
        final String sParameterValue = aSB.toString ();
        try
        {
          aMimeType.addParameter (sParameterName, sParameterValue);
        }
        catch (final Exception ex)
        {
          throw new MimeTypeParserException ("Failed to add parameter '" +
                                             sParameterName +
                                             "' with value '" +
                                             sParameterValue +
                                             "'",
                                             ex);
        }
        // Search for separator of next parameter
        while (nIndex < nMax && aParamChars[nIndex] != CMimeType.SEPARATOR_PARAMETER)
          ++nIndex;
        if (nIndex == nMax)
        {
          // End of string
          break;
        }
        // Another parameter is present
        ++nIndex;

        // Skip until next name
        while (nIndex < nMax && !MimeTypeParser.isTokenChar (aParamChars[nIndex]))
          ++nIndex;

        // Semicolon at the end - resilience
        if (false)
          if (nIndex == nMax)
            throw new MimeTypeParserException ("Another parameter was indicated but none was found: " + sParameters);
      }
    }
    else
    {
      // Split all parameters the easy way. This works only if the ';' and the
      // '=' are encoded into other characters and not part of the string
      // representation
      final String [] aParams = StringHelper.getExplodedArray (CMimeType.SEPARATOR_PARAMETER, sParameters);
      for (final String sParameter : aParams)
        if (sParameter.length () > 0)
        {
          // Split each parameter into name and value
          final String [] aParamItems = StringHelper.getExplodedArray (CMimeType.SEPARATOR_PARAMETER_NAME_VALUE,
                                                                       sParameter,
                                                                       2);
          if (aParamItems.length != 2)
            throw new MimeTypeParserException ("MimeType Parameter without name/value separator found: '" +
                                               sParameter +
                                               "'");

          final String sParameterName = aParamItems[0].trim ();
          String sParameterValue = aParamItems[1].trim ();
          try
          {
            // Manually unescape the string
            sParameterValue = eQuotingAlgorithm.getUnquotedString (sParameterValue);
          }
          catch (final DecodeException ex)
          {
            throw new MimeTypeParserException ("Failed to unquote the string '" + sParameterValue + "'", ex);
          }
          try
          {
            aMimeType.addParameter (sParameterName, sParameterValue);
          }
          catch (final Exception ex)
          {
            throw new MimeTypeParserException ("Failed to add parameter '" +
                                               sParameterName +
                                               "' with value '" +
                                               sParameterValue +
                                               "'",
                                               ex);
          }
        }
    }
  }

  /**
   * Try to convert the string representation of a MIME type to an object. The default quoting
   * algorithm {@link CMimeType#DEFAULT_QUOTING} is used to unquote strings.
   *
   * @param sMimeType
   *        The string representation to be converted. May be <code>null</code>.
   * @return <code>null</code> if the parsed string is empty.
   * @throws MimeTypeParserException
   *         In case of an error
   */
  @Nullable
  public static MimeType parseMimeType (@Nullable final String sMimeType) throws MimeTypeParserException
  {
    return parseMimeType (sMimeType, CMimeType.DEFAULT_QUOTING);
  }

  /**
   * Try to convert the string representation of a MIME type to an object. The default quoting
   * algorithm {@link CMimeType#DEFAULT_QUOTING} is used to un-quote strings.
   *
   * @param sMimeType
   *        The string representation to be converted. May be <code>null</code>.
   * @param eQuotingAlgorithm
   *        The quoting algorithm to be used to un-quote parameter values. May not be
   *        <code>null</code>.
   * @return <code>null</code> if the parsed string is empty.
   * @throws MimeTypeParserException
   *         In case of an error
   */
  @Nullable
  public static MimeType parseMimeType (@Nullable final String sMimeType, @Nonnull final EMimeQuoting eQuotingAlgorithm)
                                                                                                                         throws MimeTypeParserException
  {
    ValueEnforcer.notNull (eQuotingAlgorithm, "QuotingAlgorithm");

    // Trim
    final String sRealMimeType = StringHelper.trim (sMimeType);

    // No content -> no mime type
    if (Strings.isEmpty (sRealMimeType))
      return null;

    // Special case use sometimes from within browsers
    if (sRealMimeType.equals ("*"))
    {
      // Interpret as "*/*"
      return new MimeType (EMimeContentType._STAR, "*");
    }
    // Find the separator between content type and sub type ("/")
    final int nSlashIndex = sRealMimeType.indexOf (CMimeType.SEPARATOR_CONTENTTYPE_SUBTYPE);
    if (nSlashIndex < 0)
      throw new MimeTypeParserException ("MimeType '" + sRealMimeType + "' is missing the main '/' separator char");

    // Use the main content type
    final String sContentType = sRealMimeType.substring (0, nSlashIndex).trim ();
    final EMimeContentType eContentType = EMimeContentType.getFromIDOrNull (sContentType);
    if (eContentType == null)
      throw new MimeTypeParserException ("MimeType '" +
                                         sRealMimeType +
                                         "' uses an unknown content type '" +
                                         sContentType +
                                         "'");

    // Extract the rest (sub type + parameters)
    final String sRest = sRealMimeType.substring (nSlashIndex + 1);
    final int nSemicolonIndex = sRest.indexOf (CMimeType.SEPARATOR_PARAMETER);
    final String sContentSubType;
    final String sParameters;
    if (nSemicolonIndex >= 0)
    {
      sContentSubType = sRest.substring (0, nSemicolonIndex).trim ();
      // everything after the first ';' as parameters
      sParameters = sRest.substring (nSemicolonIndex + 1).trim ();
    }
    else
    {
      sContentSubType = sRest.trim ();
      sParameters = null;
    }
    if (Strings.isEmpty (sContentSubType))
      throw new MimeTypeParserException ("MimeType '" +
                                         sRealMimeType +
                                         "' uses an empty content sub type '" +
                                         sRealMimeType +
                                         "'");

    final MimeType ret = new MimeType (eContentType, sContentSubType);
    if (Strings.isNotEmpty (sParameters))
    {
      // We have parameters to extract
      _parseAndAddParameters (ret, sParameters, eQuotingAlgorithm);
    }
    return ret;
  }

  /**
   * Try to convert the string representation of a MIME type to an object. The default quoting
   * algorithm {@link CMimeType#DEFAULT_QUOTING} is used to unquote strings. Compared to
   * {@link #parseMimeType(String)} this method swallows all {@link MimeTypeParserException} and
   * simply returns <code>null</code>. Additionally if the string is RFC 2616 encoded, it is decoded
   * before parsing (since 9.4.6).
   *
   * @param sMimeType
   *        The string representation to be converted. May be <code>null</code>.
   * @return <code>null</code> if the parsed string is empty or not a valid mime type.
   */
  @Nullable
  public static MimeType safeParseMimeType (@Nullable final String sMimeType)
  {
    return safeParseMimeType (sMimeType, CMimeType.DEFAULT_QUOTING);
  }

  /**
   * Try to convert the string representation of a MIME type to an object. The default quoting
   * algorithm {@link CMimeType#DEFAULT_QUOTING} is used to unquote strings. Compared to
   * {@link #parseMimeType(String, EMimeQuoting)} this method swallows all
   * {@link MimeTypeParserException} and simply returns <code>null</code>. Additionally if the
   * string is RFC 2616 encoded, it is decoded before parsing (since 9.4.6).
   *
   * @param sMimeType
   *        The string representation to be converted. May be <code>null</code>.
   * @param eQuotingAlgorithm
   *        The quoting algorithm to be used to un-quote parameter values. May not be
   *        <code>null</code>.
   * @return <code>null</code> if the parsed string is empty or not a valid mime type.
   * @since 9.4.6
   */
  @Nullable
  public static MimeType safeParseMimeType (@Nullable final String sMimeType,
                                            @Nonnull final EMimeQuoting eQuotingAlgorithm)
  {
    String sRealMimeType = sMimeType;
    if (RFC2616Codec.isMaybeEncoded (sRealMimeType))
    {
      // Check if it is encoded with double quotes
      try
      {
        sRealMimeType = new RFC2616Codec ().getDecodedAsString (sRealMimeType);
      }
      catch (final DecodeException ex)
      {
        // Ignore and continue with the original one
      }
    }
    try
    {
      return parseMimeType (sRealMimeType, eQuotingAlgorithm);
    }
    catch (final MimeTypeParserException ex)
    {
      if ("*".equals (sRealMimeType))
        return new MimeType (EMimeContentType._STAR, "*");
    }
    LOGGER.warn ("Unparsable MIME type '" + sMimeType + "'");
    return null;
  }
}
