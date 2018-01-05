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

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.base64.Base64;

/**
 * Identical to the Base64 encoding defined by
 * <a href="http://www.ietf.org/rfc/rfc1521.txt">RFC 1521</a> and allows a
 * character set to be specified.
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
public class RFC1522BCodec extends AbstractRFC1522Codec
{
  /**
   * Default constructor with the UTF-8 charset.
   */
  public RFC1522BCodec ()
  {
    this (StandardCharsets.UTF_8);
  }

  /**
   * Constructor which allows for the selection of a default charset
   *
   * @param aCharset
   *        the default string charset to use.
   */
  public RFC1522BCodec (@Nonnull final Charset aCharset)
  {
    super (aCharset);
  }

  @Override
  protected String getRFC1522Encoding ()
  {
    return "B";
  }

  @Override
  @Nullable
  @ReturnsMutableCopy
  protected byte [] getEncoded (@Nullable final byte [] aDecodedBuffer,
                                @Nonnegative final int nOfs,
                                @Nonnegative final int nLen)
  {
    return Base64.safeEncodeBytesToBytes (aDecodedBuffer, nOfs, nLen);
  }

  @Override
  @Nullable
  @ReturnsMutableCopy
  protected byte [] getDecoded (@Nullable final byte [] aEncodedBuffer,
                                @Nonnegative final int nOfs,
                                @Nonnegative final int nLen)
  {
    return Base64.safeDecode (aEncodedBuffer, nOfs, nLen);
  }

}
