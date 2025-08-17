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
package com.helger.http.header.specific;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.codec.RFC5234Helper;
import com.helger.base.debug.GlobalDebug;
import com.helger.base.string.StringHelper;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsList;
import com.helger.http.RFC7230Helper;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * Parser for RFC 7239 compliant "Forwarded" header values. This class can parse a forwarded-element
 * string into a {@link HttpForwardedHeaderHop} or multiple forwarded elements separated by commas
 * into a list of {@link HttpForwardedHeaderHop} objects. The syntax according to RFC 7239 is:
 *
 * <pre>
 * Forwarded         = 1#forwarded-element
 * forwarded-element = [ forwarded-pair ] *( ";" [ forwarded-pair ] )
 * forwarded-pair    = token "=" value
 * value             = token / quoted-string
 * </pre>
 *
 * Multiple forwarded elements are separated by commas to represent multiple hops.
 *
 * @author Philip Helger
 * @since 10.5.1
 */
@Immutable
public final class HttpForwardedHeaderParser
{
  /**
   * Internal helper class for parsing context.
   *
   * @author Philip Helger
   */
  private static final class ParseContext
  {
    private final char [] m_aInput;
    private int m_nPos = 0;

    ParseContext (@Nonnull final String sInput)
    {
      m_aInput = sInput.toCharArray ();
    }

    boolean hasMore ()
    {
      return m_nPos < m_aInput.length;
    }

    char getCurrentChar ()
    {
      return m_aInput[m_nPos];
    }

    void advance ()
    {
      m_nPos++;
    }

    @Nonnull
    String getErrorLocationDetails ()
    {
      final char c = hasMore () ? getCurrentChar () : 0;
      return "The problem is at index " +
             m_nPos +
             " (char '" +
             c +
             "' / " +
             (int) c +
             ") of text '" +
             new String (m_aInput) +
             "'";
    }
  }

  public static final String HTTP_HEADER_FORWARDED = "Forwarded";

  private static final Logger LOGGER = LoggerFactory.getLogger (HttpForwardedHeaderParser.class);

  private HttpForwardedHeaderParser ()
  {}

  /**
   * Skip whitespace characters.
   *
   * @param aContext
   *        The parsing context. May not be <code>null</code>.
   */
  private static void _skipWhitespace (@Nonnull final ParseContext aContext)
  {
    while (aContext.hasMore ())
    {
      final char c = aContext.getCurrentChar ();
      if (!RFC5234Helper.isWSP (c))
        break;
      aContext.advance ();
    }
  }

  /**
   * Parse a token according to RFC 7230.
   *
   * @param aContext
   *        The parsing context. May not be <code>null</code>.
   * @return The parsed token or <code>null</code> if parsing failed.
   */
  @Nullable
  private static String _parseToken (@Nonnull final ParseContext aContext)
  {
    final int nStart = aContext.m_nPos;

    while (aContext.hasMore ())
    {
      final char c = aContext.getCurrentChar ();
      if (!RFC7230Helper.isValidTokenChar (c))
        break;
      aContext.advance ();
    }

    if (aContext.m_nPos == nStart)
    {
      // No token characters found
      return null;
    }

    final String sToken = new String (aContext.m_aInput, nStart, aContext.m_nPos - nStart);

    if (GlobalDebug.isDebugMode ())
    {
      // Validate the complete token again
      if (!RFC7230Helper.isValidToken (sToken))
      {
        LOGGER.warn ("Found internal inconsistency parsing '" + sToken + "' as an RFC 7230 token");
        return null;
      }
    }

    return sToken;
  }

  /**
   * Expect a specific character at the current position.
   *
   * @param aContext
   *        The parsing context. May not be <code>null</code>.
   * @param cExpected
   *        The expected character.
   * @return <code>true</code> if the character was found and consumed, <code>false</code>
   *         otherwise.
   */
  private static boolean _expectChar (@Nonnull final ParseContext aContext, final char cExpected)
  {
    if (!aContext.hasMore () || aContext.getCurrentChar () != cExpected)
      return false;
    aContext.advance ();
    return true;
  }

  /**
   * Check if a character is valid to be escaped inside a quoted string.
   *
   * @param c
   *        The character to check.
   * @return <code>true</code> if valid, <code>false</code> otherwise.
   */
  private static boolean _isValidEscapedChar (final char c)
  {
    // According to RFC 7230, only certain characters can be escaped
    return RFC5234Helper.isWSP (c) || RFC5234Helper.isVChar (c);
  }

  /**
   * Check if a character is valid inside a quoted string.
   *
   * @param c
   *        The character to check.
   * @return <code>true</code> if valid, <code>false</code> otherwise.
   */
  private static boolean _isValidQuotedStringChar (final char c)
  {
    // Allow most printable ASCII characters except quote and backslash
    // Control characters (0-31 and 127) are not allowed
    return RFC5234Helper.isWSP (c) ||
           (RFC5234Helper.isVChar (c) && !RFC5234Helper.isDQuote (c) && !RFC7230Helper.isBackslash (c));
  }

  /**
   * Parse a quoted-string according to RFC 7230.
   *
   * @param aContext
   *        The parsing context. May not be <code>null</code>.
   * @return The parsed quoted string content (without quotes) or <code>null</code> if parsing
   *         failed.
   */
  @Nullable
  private static String _parseQuotedString (@Nonnull final ParseContext aContext)
  {
    if (!aContext.hasMore () || !RFC5234Helper.isDQuote (aContext.getCurrentChar ()))
      return null;

    // Skip opening quote
    aContext.advance ();

    final StringBuilder aSB = new StringBuilder ();
    while (aContext.hasMore ())
    {
      final char c = aContext.getCurrentChar ();
      if (RFC5234Helper.isDQuote (c))
      {
        // End of quoted string
        aContext.advance ();
        return aSB.toString ();
      }
      else
        if (RFC7230Helper.isBackslash (c))
        {
          // Escape sequence
          aContext.advance ();
          if (!aContext.hasMore ())
          {
            // Incomplete escape sequence
            LOGGER.warn ("Found incomplete escape sequence in HTTP 'Forwarded' header value parsing. " +
                         aContext.getErrorLocationDetails ());
            return null;
          }

          final char cEscaped = aContext.getCurrentChar ();
          if (_isValidEscapedChar (cEscaped))
          {
            aSB.append (cEscaped);
            aContext.advance ();
          }
          else
          {
            // Invalid escape sequence
            LOGGER.warn ("Found invalid character (" +
                         (int) cEscaped +
                         ") in escape sequence in HTTP 'Forwarded' header value parsing. " +
                         aContext.getErrorLocationDetails ());
            return null;
          }
        }
        else
          if (_isValidQuotedStringChar (c))
          {
            aSB.append (c);
            aContext.advance ();
          }
          else
          {
            // Invalid character in quoted string
            LOGGER.warn ("Found invalid character (" +
                         (int) c +
                         ") in quoted string of HTTP 'Forwarded' header value parsing. " +
                         aContext.getErrorLocationDetails ());
            return null;
          }
    }

    // Unterminated quoted string
    return null;
  }

  /**
   * Parse a value (either a token or a quoted-string).
   *
   * @param aContext
   *        The parsing context. May not be <code>null</code>.
   * @return The parsed value or <code>null</code> if parsing failed.
   */
  @Nullable
  private static String _parseValue (@Nonnull final ParseContext aContext)
  {
    if (!aContext.hasMore ())
      return null;

    final char c = aContext.getCurrentChar ();
    if (RFC5234Helper.isDQuote (c))
    {
      // Parse quoted-string
      return _parseQuotedString (aContext);
    }
    // Parse token
    return _parseToken (aContext);
  }

  enum EPairParsingResult
  {
    SUCCESS,
    ERROR,
    EOI
  }

  /**
   * Parse an optional forwarded-pair.
   *
   * @param aContext
   *        The parsing context. May not be <code>null</code>.
   * @param aResult
   *        The result list to add the pair to. May not be <code>null</code>.
   * @return <code>true</code> if parsing succeeded, <code>false</code> otherwise.
   */
  private static EPairParsingResult _parseOptionalPair (@Nonnull final ParseContext aContext,
                                                        @Nonnull final HttpForwardedHeaderHop aResult)
  {
    _skipWhitespace (aContext);

    if (!aContext.hasMore ())
    {
      // Empty pair is allowed
      return EPairParsingResult.EOI;
    }

    // Parse token
    final String sToken = _parseToken (aContext);
    if (sToken == null)
    {
      // It's e.g. an empty token (as in ";;")
      return EPairParsingResult.EOI;
    }

    _skipWhitespace (aContext);

    // Expect '='
    if (!_expectChar (aContext, '='))
      return EPairParsingResult.ERROR;

    _skipWhitespace (aContext);

    // Parse value (token or quoted-string)
    final String sValue = _parseValue (aContext);
    if (sValue == null)
      return EPairParsingResult.ERROR;

    _skipWhitespace (aContext);

    // Add the pair to the result
    try
    {
      aResult.addPair (sToken, sValue);
      return EPairParsingResult.SUCCESS;
    }
    catch (final RuntimeException ex)
    {
      // Invalid token according to RFC 7230
      LOGGER.error ("Failed to store HTTP 'Forwarded' pair '" +
                    sToken +
                    "' and '" +
                    sValue +
                    "'. Technical details: " +
                    ex.getMessage ());
      return EPairParsingResult.ERROR;
    }
  }

  /**
   * Parse a forwarded-element string according to RFC 7239.
   *
   * @param sForwardedElement
   *        The forwarded-element string to parse. May be <code>null</code>.
   * @return A new {@link HttpForwardedHeaderHop} containing the parsed pairs, or <code>null</code>
   *         if parsing failed or the input was invalid.
   */
  @Nullable
  public static HttpForwardedHeaderHop parseSingleHop (@Nullable final String sForwardedElement)
  {
    final HttpForwardedHeaderHop ret = new HttpForwardedHeaderHop ();
    if (StringHelper.isEmpty (sForwardedElement))
    {
      // Empty string returns empty list
      return ret;
    }

    final String sTrimmed = sForwardedElement.trim ();
    if (sTrimmed.isEmpty ())
    {
      // Empty but valid
      return ret;
    }

    try
    {
      final ParseContext aContext = new ParseContext (sTrimmed);

      // Parse first pair (mandatory)
      if (_parseOptionalPair (aContext, ret) != EPairParsingResult.SUCCESS)
        return null;

      // Parse subsequent pairs preceded by semicolons
      while (aContext.hasMore ())
      {
        _skipWhitespace (aContext);
        if (!_expectChar (aContext, ';'))
        {
          LOGGER.warn ("Expected a ';' as separator when parsing HTTP 'Forwarded' header value. " +
                       aContext.getErrorLocationDetails ());
          return null;
        }
        _skipWhitespace (aContext);

        // Any additional pair is optional - so no need to break
        if (_parseOptionalPair (aContext, ret) == EPairParsingResult.ERROR)
          return null;
      }

      return ret;
    }
    catch (final Exception ex)
    {
      // Any parsing error results in null return
      LOGGER.error ("Failed to parse HTTP 'Forwarded' header value '" + sTrimmed + "'", ex);
      return null;
    }
  }

  /**
   * Split a forwarded header value into individual forwarded elements by commas, while respecting
   * quoted strings that may contain commas.
   *
   * @param sHeaderValue
   *        The header value to split. May not be <code>null</code>.
   * @return A list of individual forwarded elements.
   */
  @Nonnull
  private static ICommonsList <String> _splitForwardedHops (@Nonnull final String sHeaderValue)
  {
    final ICommonsList <String> ret = new CommonsArrayList <> ();

    final ParseContext aContext = new ParseContext (sHeaderValue);
    final StringBuilder aCurrentElement = new StringBuilder ();

    while (aContext.hasMore ())
    {
      final char c = aContext.getCurrentChar ();

      if (c == ',')
      {
        // Found a comma - end of current element
        final String sElement = aCurrentElement.toString ().trim ();
        if (sElement.length () > 0)
        {
          ret.add (sElement);
        }
        aCurrentElement.setLength (0);
        aContext.advance ();
      }
      else
        if (RFC5234Helper.isDQuote (c))
        {
          // Found a quote - need to consume the entire quoted string
          aCurrentElement.append (c);
          aContext.advance ();

          while (aContext.hasMore ())
          {
            final char cQuoted = aContext.getCurrentChar ();
            aCurrentElement.append (cQuoted);
            aContext.advance ();

            if (RFC5234Helper.isDQuote (cQuoted))
            {
              // End of quoted string
              break;
            }
            else
              if (RFC7230Helper.isBackslash (cQuoted) && aContext.hasMore ())
              {
                // Escape sequence - consume next character as well
                final char cEscaped = aContext.getCurrentChar ();
                aCurrentElement.append (cEscaped);
                aContext.advance ();
              }
          }
        }
        else
        {
          // Regular character
          aCurrentElement.append (c);
          aContext.advance ();
        }
    }

    // Add the last element
    final String sLastElement = aCurrentElement.toString ().trim ();
    if (sLastElement.length () > 0)
      ret.add (sLastElement);

    return ret;
  }

  /**
   * Parse a complete Forwarded header value that may contain multiple forwarded elements separated
   * by commas according to RFC 7239. Each forwarded element represents one hop in the request
   * chain.
   *
   * @param sForwardedHeaderValue
   *        The complete Forwarded header value to parse. May be <code>null</code>.
   * @return A list of {@link HttpForwardedHeaderHop} objects representing each hop, or
   *         <code>null</code> if parsing failed. An empty list is returned for empty but valid
   *         input.
   */
  @Nullable
  @ReturnsMutableCopy
  public static ICommonsList <HttpForwardedHeaderHop> parseMultipleHops (@Nullable final String sForwardedHeaderValue)
  {
    final ICommonsList <HttpForwardedHeaderHop> ret = new CommonsArrayList <> ();
    if (StringHelper.isEmpty (sForwardedHeaderValue))
    {
      // Empty string returns empty list
      return ret;
    }

    final String sTrimmed = sForwardedHeaderValue.trim ();
    if (sTrimmed.isEmpty ())
    {
      // Empty but valid
      return ret;
    }

    try
    {
      // Split by commas to get individual forwarded elements
      // Note: We need to be careful about commas inside quoted strings
      final ICommonsList <String> aHops = _splitForwardedHops (sTrimmed);
      for (final String sHop : aHops)
      {
        final HttpForwardedHeaderHop aParsedHop = parseSingleHop (sHop);
        if (aParsedHop == null)
        {
          // Parsing failed for one element
          LOGGER.warn ("Failed to parse single hop of HTTP 'Forwarded' header value: '" + sHop + "'");
          return null;
        }
        if (aParsedHop.isNotEmpty ())
          ret.add (aParsedHop);
      }

      return ret;
    }
    catch (final Exception ex)
    {
      // Any parsing error results in null return
      LOGGER.error ("Failed to parse HTTP 'Forwarded' header value '" + sTrimmed + "'", ex);
      return null;
    }
  }

  /**
   * Parse a complete Forwarded header value that may contain multiple forwarded elements separated
   * by commas according to RFC 7239. Each forwarded element represents one hop in the request
   * chain. Return only the last (and most relevant hop)
   *
   * @param sForwardedHeaderValue
   *        The complete Forwarded header value to parse. May be <code>null</code>.
   * @return A {@link HttpForwardedHeaderHop} object representing the last hop, or <code>null</code>
   *         if parsing failed.
   */
  @Nullable
  @ReturnsMutableCopy
  public static HttpForwardedHeaderHop parseAndGetLastHop (@Nullable final String sForwardedHeaderValue)
  {
    final var aHops = parseMultipleHops (sForwardedHeaderValue);
    return aHops == null ? null : aHops.getLastOrNull ();
  }
}
