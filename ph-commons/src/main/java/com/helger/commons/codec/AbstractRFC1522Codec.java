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
package com.helger.commons.codec;

import java.nio.charset.Charset;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.charset.CCharset;
import com.helger.commons.charset.CharsetManager;
import com.helger.commons.string.StringHelper;

/**
 * Implements methods common to all codecs defined in RFC 1522.
 * <p>
 * <a href="http://www.ietf.org/rfc/rfc1522.txt">RFC 1522</a> describes
 * techniques to allow the encoding of non-ASCII text in various portions of a
 * RFC 822 [2] message header, in a manner which is unlikely to confuse existing
 * message handling software.
 * <p>
 * This class is immutable and thread-safe.
 *
 * @see <a href="http://www.ietf.org/rfc/rfc1522.txt">MIME (Multipurpose
 *      Internet Mail Extensions) Part Two: Message Header Extensions for
 *      Non-ASCII Text</a>
 */
public abstract class AbstractRFC1522Codec implements IByteArrayCodec
{
  /** Separator. */
  protected static final char SEP = '?';

  /** Prefix. */
  protected static final String PREFIX = "=?";

  /** Postfix. */
  protected static final String POSTFIX = "?=";

  /**
   * Returns the codec name (referred to as encoding in the RFC 1522).
   *
   * @return name of the codec
   */
  @Nonnull
  @Nonempty
  protected abstract String getRFC1522Encoding ();

  /**
   * Applies an RFC 1522 compliant encoding scheme to the given string of text
   * with the given charset.
   * <p>
   * This method constructs the "encoded-word" header common to all the RFC 1522
   * codecs and then invokes {@link #getEncoded(byte [])} method of a concrete
   * class to perform the specific encoding.
   *
   * @param sText
   *        a string to encode
   * @param aSourceCharset
   *        a charset to be used
   * @return RFC 1522 compliant "encoded-word"
   * @throws EncodeException
   *         thrown if there is an error condition during the Encoding process.
   * @see <a href=
   *      "http://download.oracle.com/javase/6/docs/api/java/nio/charset/Charset.html">
   *      Standard charsets</a>
   */
  @Nullable
  protected String getEncodedText (@Nullable final String sText,
                                   @Nonnull final Charset aSourceCharset) throws EncodeException
  {
    ValueEnforcer.notNull (aSourceCharset, "SourceCharset");
    if (sText == null)
      return null;

    final byte [] aEncodedData = getEncoded (CharsetManager.getAsBytes (sText, aSourceCharset));

    final StringBuilder aSB = new StringBuilder ();
    aSB.append (PREFIX)
       .append (aSourceCharset.name ())
       .append (SEP)
       .append (getRFC1522Encoding ())
       .append (SEP)
       .append (CharsetManager.getAsString (aEncodedData, CCharset.CHARSET_US_ASCII_OBJ))
       .append (POSTFIX);
    return aSB.toString ();
  }

  /**
   * Applies an RFC 1522 compliant decoding scheme to the given string of text.
   * <p>
   * This method processes the "encoded-word" header common to all the RFC 1522
   * codecs and then invokes {@link #getDecoded(byte [])} method of a concrete
   * class to perform the specific decoding.
   *
   * @param sEncodedText
   *        a string to decode
   * @return A new decoded String or {@code null} if the input is {@code null}.
   * @throws DecodeException
   *         thrown if there is an error condition during the decoding process.
   */
  @Nullable
  public String getDecodedText (@Nullable final String sEncodedText) throws DecodeException
  {
    if (sEncodedText == null)
      return null;

    ValueEnforcer.isTrue (sEncodedText.startsWith (PREFIX),
                          "RFC 1522 violation: malformed encoded content. Prefix missing.");
    ValueEnforcer.isTrue (sEncodedText.endsWith (POSTFIX),
                          "RFC 1522 violation: malformed encoded content. Postfix missing.");

    int nFrom = PREFIX.length ();
    final int nTerminator = sEncodedText.length () - POSTFIX.length ();

    // Read charset
    int nTo = sEncodedText.indexOf (SEP, nFrom);
    if (nTo == nTerminator)
      throw new DecodeException ("RFC 1522 violation: charset token not found");
    final String sDestCharset = sEncodedText.substring (nFrom, nTo);
    if (StringHelper.hasNoText (sDestCharset))
      throw new DecodeException ("RFC 1522 violation: charset not specified");
    final Charset aDestCharset = CharsetManager.getCharsetFromNameOrNull (sDestCharset);
    if (aDestCharset == null)
      throw new DecodeException ("Failed to resolve charset '" + sDestCharset + "'");

    // Read encoding
    nFrom = nTo + 1;
    nTo = sEncodedText.indexOf (SEP, nFrom);
    if (nTo == nTerminator)
      throw new DecodeException ("RFC 1522 violation: encoding token not found");
    final String sEncoding = sEncodedText.substring (nFrom, nTo);
    if (!getRFC1522Encoding ().equalsIgnoreCase (sEncoding))
      throw new DecodeException ("This codec cannot decode '" + sEncoding + "' encoded content");

    // Read encoded data
    nFrom = nTo + 1;
    nTo = sEncodedText.indexOf (SEP, nFrom);
    final byte [] aEncodedBytes = CharsetManager.getAsBytes (sEncodedText.substring (nFrom, nTo),
                                                             CCharset.CHARSET_US_ASCII_OBJ);
    final byte [] aDecodedBytes = getDecoded (aEncodedBytes);
    return CharsetManager.getAsString (aDecodedBytes, aDestCharset);
  }
}
