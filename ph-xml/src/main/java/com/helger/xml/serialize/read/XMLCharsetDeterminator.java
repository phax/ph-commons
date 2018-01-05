/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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
package com.helger.xml.serialize.read;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.charset.CharsetHelper;
import com.helger.commons.charset.CharsetHelper.InputStreamAndCharset;
import com.helger.commons.charset.EUnicodeBOM;
import com.helger.commons.collection.impl.CommonsHashSet;
import com.helger.commons.collection.impl.ICommonsSet;
import com.helger.commons.io.stream.NonBlockingByteArrayInputStream;

/**
 * XML charset determinator based on a byte array.
 *
 * @author Philip Helger
 * @since 9.0.0
 */
@Immutable
public final class XMLCharsetDeterminator
{
  public static final Charset FALLBACK_CHARSET = StandardCharsets.UTF_8;
  private static final ICommonsSet <Charset> XML_CHARSETS = new CommonsHashSet <> ();
  static
  {
    for (final Charset c : CharsetHelper.getAllCharsets ().values ())
    {
      // Charset must be able to encode!
      // The special names failed on Windows 10, JDK 1.8.0_131
      // This set also is proved on Travis with Ubuntu Trusty and JDK 1.8.0_131
      if (c.canEncode () &&
          !c.name ().equals ("JIS_X0212-1990") &&
          !c.name ().equals ("x-IBM300") &&
          !c.name ().equals ("x-IBM834") &&
          !c.name ().equals ("x-JIS0208") &&
          !c.name ().equals ("x-MacDingbat") &&
          !c.name ().equals ("x-MacSymbol"))
        XML_CHARSETS.add (c);
    }
  }
  private static final Charset CHARSET_UTF_32BE = Charset.forName ("UTF-32BE");
  private static final Charset CHARSET_UTF_32LE = Charset.forName ("UTF-32LE");
  private static final Charset CHARSET_EBCDIC = Charset.forName ("Cp1047");
  private static final Charset CHARSET_IBM290 = Charset.forName ("IBM290");

  private static final byte [] CS_UTF32_BE = new byte [] { 0, 0, 0, 0x3c };
  private static final byte [] CS_UTF32_LE = new byte [] { 0x3c, 0, 0, 0 };
  private static final byte [] CS_UTF16_BE = new byte [] { 0, 0x3c, 0, 0x3f };
  private static final byte [] CS_UTF16_LE = new byte [] { 0x3c, 0, 0x3f, 0 };
  private static final byte [] CS_UTF8 = new byte [] { 0x3c, 0x3f, 0x78, 0x6d };
  private static final byte [] CS_EBCDIC = new byte [] { 0x4c, 0x6f, (byte) 0xa7, (byte) 0x94 };
  private static final byte [] CS_IBM290 = new byte [] { 0x4c, 0x6f, (byte) 0xb7, (byte) 0x75 };

  private XMLCharsetDeterminator ()
  {}

  /**
   * @return A mutable Set with all charsets that can be used for the charset
   *         determination. Never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static ICommonsSet <Charset> getAllSupportedCharsets ()
  {
    return XML_CHARSETS.getClone ();
  }

  /**
   * Use a Reader to parse the XML declaration specified by the byte array,
   * starting at the specified offset using the provided charset.
   *
   * @param aBytes
   *        Byte array to read. Maximum length is 4 + 4096
   * @param nOfs
   *        Offset to start parsing. Usually between 0 and 4 (depending on an
   *        eventually present BOM)
   * @param aParseCharset
   *        The basic charset determined by BOM or byte array matching for
   *        parsing the byte array.
   * @return <code>null</code> if no charset was found!
   * @throws UncheckedIOException
   *         if something goes wrong
   */
  @Nullable
  private static Charset _parseXMLEncoding (@Nonnull final byte [] aBytes,
                                            @Nonnegative final int nOfs,
                                            @Nonnull final Charset aParseCharset)
  {
    try (final NonBlockingByteArrayInputStream aIS = new NonBlockingByteArrayInputStream (aBytes,
                                                                                          nOfs,
                                                                                          aBytes.length - nOfs);
         final Reader aReader = new InputStreamReader (aIS, aParseCharset))
    {
      final StringBuilder aSB = new StringBuilder ();
      int c;
      final int nMaxByteOfs = nOfs + 4096;
      while ((c = aReader.read ()) != -1)
      {
        aSB.append ((char) c);
        if (c == '>' && aIS.getPosition () >= nMaxByteOfs)
        {
          // Stop at first '>' as this will end the <?xml..?> stuff or after
          // 4096 bytes
          break;
        }
      }
      final int nMaxChars = aSB.length ();
      int nStartIndex = aSB.indexOf ("encoding");
      if (nStartIndex > 0)
      {
        nStartIndex += "encoding".length ();
        // Skip spaces
        while (nStartIndex < nMaxChars && Character.isWhitespace (aSB.charAt (nStartIndex)))
          nStartIndex++;
        // Expect '='
        if (nStartIndex < nMaxChars && aSB.charAt (nStartIndex) == '=')
        {
          nStartIndex++;
          // Skip spaces
          while (nStartIndex < nMaxChars && Character.isWhitespace (aSB.charAt (nStartIndex)))
            nStartIndex++;
          if (nStartIndex < nMaxChars)
          {
            // Expect quote character
            final char cQuote = aSB.charAt (nStartIndex);
            // Ü is IBM1026 hack for double quote
            if (cQuote == '"' || cQuote == '\'' || cQuote == 'Ü')
            {
              nStartIndex++;
              // Find matching closing quote
              final int nEndIndex = aSB.indexOf (Character.toString (cQuote), nStartIndex);
              if (nEndIndex > 0)
              {
                // Yeah
                final String sEncoding = aSB.substring (nStartIndex, nEndIndex).trim ();
                return Charset.forName (sEncoding);
              }
            }
          }
        }
      }
      // No encoding could be found
      return null;
    }
    catch (final IOException ex)
    {
      throw new UncheckedIOException (ex);
    }
  }

  /**
   * Byte array match method
   *
   * @param aSrcBytes
   *        The bytes read.
   * @param nSrcOffset
   *        The offset within read bytes to start searching
   * @param aCmpBytes
   *        The encoding specific bytes to check.
   * @return <code>true</code> if the bytes match, <code>false</code> otherwise.
   */
  private static boolean _match (@Nonnull final byte [] aSrcBytes,
                                 @Nonnegative final int nSrcOffset,
                                 @Nonnull final byte [] aCmpBytes)
  {
    final int nEnd = aCmpBytes.length;
    for (int i = 0; i < nEnd; ++i)
      if (aSrcBytes[nSrcOffset + i] != aCmpBytes[i])
        return false;
    return true;
  }

  /**
   * Determine the XML charset
   *
   * @param aBytes
   *        XML byte representation
   * @return <code>null</code> if no charset was found. In that case you might
   *         wanna try UTF-8 as the fallback.
   */
  @Nullable
  public static Charset determineXMLCharset (@Nonnull final byte [] aBytes)
  {
    ValueEnforcer.notNull (aBytes, "Bytes");

    Charset aParseCharset = null;
    int nSearchOfs = 0;

    if (aBytes.length > 0)
    {
      // Check if a BOM is present
      // Read at maximum 4 bytes (max BOM bytes)
      try (NonBlockingByteArrayInputStream aIS = new NonBlockingByteArrayInputStream (aBytes,
                                                                                      0,
                                                                                      Math.min (EUnicodeBOM.getMaximumByteCount (),
                                                                                                aBytes.length)))
      {
        // Check for BOM first
        final InputStreamAndCharset aISC = CharsetHelper.getInputStreamAndCharsetFromBOM (aIS);
        if (aISC.hasBOM ())
        {
          // A BOM was found, but not necessarily a charset could uniquely be
          // identified - skip the
          // BOM bytes and continue determination from there
          nSearchOfs = aISC.getBOM ().getByteCount ();
        }

        if (aISC.hasCharset ())
        {
          // A BOM was found, and that BOM also has a unique charset assigned
          aParseCharset = aISC.getCharset ();
        }
      }
    }

    // No charset found and enough bytes left?
    if (aParseCharset == null && aBytes.length - nSearchOfs >= 4)
      if (_match (aBytes, nSearchOfs, CS_UTF32_BE))
        aParseCharset = CHARSET_UTF_32BE;
      else
        if (_match (aBytes, nSearchOfs, CS_UTF32_LE))
          aParseCharset = CHARSET_UTF_32LE;
        else
          if (_match (aBytes, nSearchOfs, CS_UTF16_BE))
            aParseCharset = StandardCharsets.UTF_16BE;
          else
            if (_match (aBytes, nSearchOfs, CS_UTF16_LE))
              aParseCharset = StandardCharsets.UTF_16LE;
            else
              if (_match (aBytes, nSearchOfs, CS_UTF8))
                aParseCharset = StandardCharsets.UTF_8;
              else
                if (_match (aBytes, nSearchOfs, CS_EBCDIC))
                  aParseCharset = CHARSET_EBCDIC;
                else
                  if (_match (aBytes, nSearchOfs, CS_IBM290))
                    aParseCharset = CHARSET_IBM290;

    if (aParseCharset == null)
    {
      // Fallback charset is always UTF-8
      aParseCharset = FALLBACK_CHARSET;
    }

    // Now read with a reader
    return _parseXMLEncoding (aBytes, nSearchOfs, aParseCharset);
  }
}
