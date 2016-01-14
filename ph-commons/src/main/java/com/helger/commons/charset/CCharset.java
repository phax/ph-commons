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
package com.helger.commons.charset;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.annotation.PresentForCodeCoverage;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Charset constants.
 *
 * @author Philip Helger
 */
@Immutable
@SuppressFBWarnings ("NP_NONNULL_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR")
public final class CCharset
{
  /**
   * The special ANSI charset to be used in all applications. This is
   * ISO-8859-1.
   */
  @Nonnull
  public static final String CHARSET_ISO_8859_1 = StandardCharsets.ISO_8859_1.name ();

  /**
   * The ISO-8859-1 charset object.
   */
  @Nonnull
  public static final Charset CHARSET_ISO_8859_1_OBJ = StandardCharsets.ISO_8859_1;

  /**
   * The special US ACSII charset to be used in all applications. This is
   * US-ASCII.
   */
  @Nonnull
  public static final String CHARSET_US_ASCII = StandardCharsets.US_ASCII.name ();

  /**
   * The US-ASCII charset object.
   */
  @Nonnull
  public static final Charset CHARSET_US_ASCII_OBJ = StandardCharsets.US_ASCII;

  /**
   * The special UTF-8 charset to be used in all applications.
   */
  @Nonnull
  public static final String CHARSET_UTF_8 = StandardCharsets.UTF_8.name ();

  /**
   * The UTF-8 charset object.
   */
  @Nonnull
  public static final Charset CHARSET_UTF_8_OBJ = StandardCharsets.UTF_8;

  /**
   * The special UTF-16 charset to be used in all applications.
   */
  @Nonnull
  public static final String CHARSET_UTF_16 = StandardCharsets.UTF_16.name ();

  /**
   * The UTF-16 charset object.
   */
  @Nonnull
  public static final Charset CHARSET_UTF_16_OBJ = StandardCharsets.UTF_16;

  /**
   * The special UTF-16BE charset to be used in all applications.
   */
  @Nonnull
  public static final String CHARSET_UTF_16BE = StandardCharsets.UTF_16BE.name ();

  /**
   * The UTF-16BE charset object.
   */
  @Nonnull
  public static final Charset CHARSET_UTF_16BE_OBJ = StandardCharsets.UTF_16BE;

  /**
   * The special UTF-16LE charset to be used in all applications.
   */
  @Nonnull
  public static final String CHARSET_UTF_16LE = StandardCharsets.UTF_16LE.name ();

  /**
   * The UTF-16LE charset object.
   */
  @Nonnull
  public static final Charset CHARSET_UTF_16LE_OBJ = StandardCharsets.UTF_16LE;

  /**
   * The default charset to be used in all applications. Currently this is
   * UTF-8.
   */
  @Nonnull
  public static final String DEFAULT_CHARSET = CHARSET_UTF_8;

  /**
   * The default charset object. Currently this is UTF-8.
   */
  @Nonnull
  public static final Charset DEFAULT_CHARSET_OBJ = CharsetManager.getCharsetFromName (DEFAULT_CHARSET);

  /**
   * The charset used by the ServiceLoader to read the service files.
   */
  @Nonnull
  public static final String CHARSET_SERVICE_LOADER = CHARSET_UTF_8;

  /**
   * The charset used by the ServiceLoader to read the service files.
   */
  @Nonnull
  public static final Charset CHARSET_SERVICE_LOADER_OBJ = CHARSET_UTF_8_OBJ;

  /**
   * The non-standard Windows 1252 charset name.
   */
  @Nonnull
  public static final String CHARSET_WINDOWS_1252 = "windows-1252";

  /**
   * The non-standard Windows 1252 charset object.
   */
  @Nonnull
  public static final Charset CHARSET_WINDOWS_1252_OBJ = CharsetManager.getCharsetFromName (CHARSET_WINDOWS_1252);

  @PresentForCodeCoverage
  private static final CCharset s_aInstance = new CCharset ();

  private CCharset ()
  {}
}
