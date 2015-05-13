/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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
package com.helger.commons.codec.impl;

import java.nio.charset.Charset;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotations.Nonempty;
import com.helger.commons.charset.CCharset;
import com.helger.commons.charset.CharsetManager;
import com.helger.commons.codec.AbstractByteArrayCodec;
import com.helger.commons.codec.DecoderException;
import com.helger.commons.codec.EncoderException;
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
public abstract class AbstractRFC1522Codec extends AbstractByteArrayCodec
{
  /** Separator. */
  protected static final char SEP = '?';

  /** Prefix. */
  protected static final String POSTFIX = "?=";

  /** Postfix. */
  protected static final String PREFIX = "=?";

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
   * @param aCharset
   *        a charset to be used
   * @return RFC 1522 compliant "encoded-word"
   * @throws EncoderException
   *         thrown if there is an error condition during the Encoding process.
   * @see <a
   *      href="http://download.oracle.com/javase/6/docs/api/java/nio/charset/Charset.html">Standard
   *      charsets</a>
   */
  @Nullable
  protected String getEncodedText (@Nullable final String sText, @Nonnull final Charset aCharset) throws EncoderException
  {
    if (sText == null)
      return null;

    final byte [] aEncodedData = getEncoded (CharsetManager.getAsBytes (sText, aCharset));

    final StringBuilder aSB = new StringBuilder ();
    aSB.append (PREFIX)
       .append (aCharset.name ())
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
   * @param sText
   *        a string to decode
   * @return A new decoded String or {@code null} if the input is {@code null}.
   * @throws DecoderException
   *         thrown if there is an error condition during the decoding process.
   */
  @Nullable
  public String getDecodedText (@Nullable final String sText) throws DecoderException
  {
    if (sText == null)
      return null;

    if (!sText.startsWith (PREFIX) || !sText.endsWith (POSTFIX))
      throw new DecoderException ("RFC 1522 violation: malformed encoded content");

    final int nTerminator = sText.length () - 2;
    int nFrom = 2;
    int nTo = sText.indexOf (SEP, nFrom);
    if (nTo == nTerminator)
      throw new DecoderException ("RFC 1522 violation: charset token not found");
    final String sCharset = sText.substring (nFrom, nTo);
    if (StringHelper.hasNoText (sCharset))
      throw new DecoderException ("RFC 1522 violation: charset not specified");
    final Charset aCharset = CharsetManager.getCharsetFromNameOrNull (sCharset);
    if (aCharset == null)
      throw new DecoderException ("Failed to resolve charset '" + sCharset + "'");
    nFrom = nTo + 1;
    nTo = sText.indexOf (SEP, nFrom);
    if (nTo == nTerminator)
      throw new DecoderException ("RFC 1522 violation: encoding token not found");
    final String sEncoding = sText.substring (nFrom, nTo);
    if (!getRFC1522Encoding ().equalsIgnoreCase (sEncoding))
      throw new DecoderException ("This codec cannot decode " + sEncoding + " encoded content");
    nFrom = nTo + 1;
    nTo = sText.indexOf (SEP, nFrom);
    byte [] aData = CharsetManager.getAsBytes (sText.substring (nFrom, nTo), CCharset.CHARSET_US_ASCII_OBJ);
    aData = getDecoded (aData);
    return CharsetManager.getAsString (aData, aCharset);
  }
}
