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
package com.helger.commons.url;

import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import com.helger.base.equals.ValueEnforcer;
import com.helger.commons.codec.DecodeException;
import com.helger.commons.codec.URLCodec;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

public final class URLCoder
{
  /** Default URL charset is UTF-8 */
  public static final Charset CHARSET_URL_OBJ = StandardCharsets.UTF_8;

  private static final URLCodec URL_CODEC = new URLCodec ();

  private URLCoder ()
  {}

  /**
   * URL-decode the passed value automatically handling charset issues. The used char set is
   * determined by {@link #CHARSET_URL_OBJ}.
   *
   * @param sValue
   *        The value to be decoded. May not be <code>null</code>.
   * @return The decoded value.
   * @throws IllegalArgumentException
   *         if something goes wrong
   * @see #urlDecode(String, Charset)
   */
  @Nonnull
  public static String urlDecode (@Nonnull final String sValue)
  {
    return urlDecode (sValue, CHARSET_URL_OBJ);
  }

  /**
   * URL-decode the passed value automatically handling charset issues. The implementation uses
   * {@link URLCodec} to do the hard work.
   *
   * @param sValue
   *        The value to be decoded. May not be <code>null</code>.
   * @param aCharset
   *        The charset to use. May not be <code>null</code>.
   * @return The decoded value.
   * @throws IllegalArgumentException
   *         if something goes wrong
   * @see URLDecoder#decode(String, String)
   */
  @Nonnull
  public static String urlDecode (@Nonnull final String sValue, @Nonnull final Charset aCharset)
  {
    ValueEnforcer.notNull (sValue, "Value");
    try
    {
      return URL_CODEC.getDecodedAsString (sValue, aCharset);
    }
    catch (final DecodeException ex)
    {
      throw new IllegalArgumentException (ex);
    }
  }

  /**
   * URL-decode the passed value automatically handling charset issues. The used char set is
   * determined by {@link #CHARSET_URL_OBJ}.
   *
   * @param sValue
   *        The value to be decoded. May not be <code>null</code>.
   * @return The decoded value.
   * @see #urlDecode(String, Charset)
   * @since 9.4.1
   */
  @Nullable
  public static String urlDecodeOrNull (@Nonnull final String sValue)
  {
    return urlDecodeOrNull (sValue, CHARSET_URL_OBJ);
  }

  /**
   * URL-decode the passed value automatically handling charset issues. The implementation uses
   * {@link URLCodec} to do the hard work.
   *
   * @param sValue
   *        The value to be decoded. May be <code>null</code>.
   * @param aCharset
   *        The charset to use. May not be <code>null</code>.
   * @return The decoded value or <code>null</code>.
   * @see URLDecoder#decode(String, String)
   * @since 9.4.1
   */
  @Nullable
  public static String urlDecodeOrNull (@Nullable final String sValue, @Nonnull final Charset aCharset)
  {
    return urlDecodeOrDefault (sValue, aCharset, null);
  }

  /**
   * URL-decode the passed value automatically handling charset issues. The used char set is
   * determined by {@link #CHARSET_URL_OBJ}.
   *
   * @param sValue
   *        The value to be decoded. May not be <code>null</code>.
   * @param sDefault
   *        The default value to be returned if decoding fails.
   * @return The decoded value or the default.
   * @see #urlDecode(String, Charset)
   * @since 9.4.1
   */
  @Nullable
  public static String urlDecodeOrDefault (@Nonnull final String sValue, @Nullable final String sDefault)
  {
    return urlDecodeOrDefault (sValue, CHARSET_URL_OBJ, sDefault);
  }

  /**
   * URL-decode the passed value automatically handling charset issues. The implementation uses
   * {@link URLCodec} to do the hard work.
   *
   * @param sValue
   *        The value to be decoded. May be <code>null</code>.
   * @param aCharset
   *        The charset to use. May not be <code>null</code>.
   * @param sDefault
   *        The default value to be returned if decoding fails.
   * @return The decoded value or the default.
   * @see URLDecoder#decode(String, String)
   * @since 9.4.1
   */
  @Nullable
  public static String urlDecodeOrDefault (@Nullable final String sValue,
                                           @Nonnull final Charset aCharset,
                                           @Nullable final String sDefault)
  {
    if (sValue != null)
      try
      {
        return URL_CODEC.getDecodedAsString (sValue, aCharset);
      }
      catch (final DecodeException ex)
      {
        // Fall through
      }
    return sDefault;
  }

  /**
   * URL-encode the passed value automatically handling charset issues. The used char set is
   * determined by {@link #CHARSET_URL_OBJ}.
   *
   * @param sValue
   *        The value to be encoded. May not be <code>null</code>.
   * @return The encoded value.
   */
  @Nonnull
  public static String urlEncode (@Nonnull final String sValue)
  {
    return urlEncode (sValue, CHARSET_URL_OBJ);
  }

  /**
   * URL-encode the passed value automatically handling charset issues. This is a ripped, optimized
   * version of URLEncoder.encode but without the UnsupportedEncodingException.
   *
   * @param sValue
   *        The value to be encoded. May not be <code>null</code>.
   * @param aCharset
   *        The charset to use. May not be <code>null</code>.
   * @return The encoded value.
   */
  @Nonnull
  public static String urlEncode (@Nonnull final String sValue, @Nonnull final Charset aCharset)
  {
    ValueEnforcer.notNull (sValue, "Value");
    return URL_CODEC.getEncodedAsString (sValue, aCharset);
  }
}
