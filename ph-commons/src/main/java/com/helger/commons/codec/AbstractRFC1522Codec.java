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
package com.helger.commons.codec;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.charset.CharsetHelper;
import com.helger.commons.serialize.convert.SerializationConverter;
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
public abstract class AbstractRFC1522Codec implements ICodec <String>
{
  /** Separator. */
  protected static final char SEP = '?';

  /** Prefix. */
  protected static final String PREFIX = "=?";

  /** Postfix. */
  protected static final String POSTFIX = "?=";

  /**
   * The default charset used for string decoding and encoding.
   */
  private transient Charset m_aCharset;

  /**
   * Constructor which allows for the selection of a default charset
   *
   * @param aCharset
   *        the default string charset to use.
   */
  protected AbstractRFC1522Codec (@Nonnull final Charset aCharset)
  {
    m_aCharset = ValueEnforcer.notNull (aCharset, "Charset");
  }

  private void writeObject (@Nonnull final ObjectOutputStream aOOS) throws IOException
  {
    aOOS.defaultWriteObject ();
    SerializationConverter.writeConvertedObject (m_aCharset, aOOS);
  }

  private void readObject (@Nonnull final ObjectInputStream aOIS) throws IOException, ClassNotFoundException
  {
    aOIS.defaultReadObject ();
    m_aCharset = SerializationConverter.readConvertedObject (aOIS, Charset.class);
  }

  @Nonnull
  public Charset getCharset ()
  {
    return m_aCharset;
  }

  /**
   * Returns the codec name (referred to as encoding in the RFC 1522).
   *
   * @return name of the codec
   */
  @Nonnull
  @Nonempty
  protected abstract String getRFC1522Encoding ();

  @Nullable
  @ReturnsMutableCopy
  protected abstract byte [] getEncoded (@Nullable final byte [] aDecodedBuffer,
                                         @Nonnegative final int nOfs,
                                         @Nonnegative final int nLen);

  @Nullable
  @ReturnsMutableCopy
  protected abstract byte [] getDecoded (@Nullable final byte [] aEncodedBuffer,
                                         @Nonnegative final int nOfs,
                                         @Nonnegative final int nLen);

  /**
   * Applies an RFC 1522 compliant encoding scheme to the given string of text
   * with the given charset.
   * <p>
   * This method constructs the "encoded-word" header common to all the RFC 1522
   * codecs and then invokes #getEncoded(byte []) method of a concrete class to
   * perform the specific encoding.
   *
   * @param sText
   *        a string to encode
   * @return RFC 1522 compliant "encoded-word"
   * @throws EncodeException
   *         thrown if there is an error condition during the Encoding process.
   * @see <a href=
   *      "http://download.oracle.com/javase/6/docs/api/java/nio/charset/Charset.html">
   *      Standard charsets</a>
   */
  @Nullable
  public String getEncoded (@Nullable final String sText)
  {
    if (sText == null)
      return null;

    final byte [] aDecodedBuffer = sText.getBytes (m_aCharset);
    final byte [] aEncodedData = getEncoded (aDecodedBuffer, 0, aDecodedBuffer.length);

    final StringBuilder aSB = new StringBuilder ();
    aSB.append (PREFIX)
       .append (m_aCharset.name ())
       .append (SEP)
       .append (getRFC1522Encoding ())
       .append (SEP)
       .append (StringHelper.decodeBytesToChars (aEncodedData, StandardCharsets.US_ASCII))
       .append (POSTFIX);
    return aSB.toString ();
  }

  /**
   * Applies an RFC 1522 compliant decoding scheme to the given string of text.
   * <p>
   * This method processes the "encoded-word" header common to all the RFC 1522
   * codecs and then invokes #getDecoded(byte []) method of a concrete class to
   * perform the specific decoding.
   *
   * @param sEncodedText
   *        a string to decode
   * @return A new decoded String or {@code null} if the input is {@code null}.
   * @throws DecodeException
   *         thrown if there is an error condition during the decoding process.
   */
  @Nullable
  public String getDecoded (@Nullable final String sEncodedText)
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
    final Charset aDestCharset = CharsetHelper.getCharsetFromNameOrNull (sDestCharset);
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
    final byte [] aEncodedBytes = sEncodedText.substring (nFrom, nTo).getBytes (StandardCharsets.US_ASCII);
    final byte [] aDecodedBytes = getDecoded (aEncodedBytes, 0, aEncodedBytes.length);
    return new String (aDecodedBytes, aDestCharset);
  }
}
