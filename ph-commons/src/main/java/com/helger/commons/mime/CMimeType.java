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
package com.helger.commons.mime;

import javax.annotation.concurrent.Immutable;

import com.helger.commons.annotation.PresentForCodeCoverage;

/**
 * Contains a collection of well-known constant MIME types.
 *
 * @author Philip Helger
 */
@Immutable
public final class CMimeType
{
  /** Word document. */
  public static final IMimeType APPLICATION_MS_WORD = EMimeContentType.APPLICATION.buildMimeType ("msword");

  /** Word 2007 document */
  public static final IMimeType APPLICATION_MS_WORD_2007 = EMimeContentType.APPLICATION.buildMimeType ("vnd.openxmlformats-officedocument.wordprocessingml.document");

  /** Excel document. */
  public static final IMimeType APPLICATION_MS_EXCEL = EMimeContentType.APPLICATION.buildMimeType ("vnd.ms-excel");

  /** Excel 2007 document */
  public static final IMimeType APPLICATION_MS_EXCEL_2007 = EMimeContentType.APPLICATION.buildMimeType ("vnd.openxmlformats-officedocument.spreadsheetml.sheet");

  /** PowerPoint document. */
  public static final IMimeType APPLICATION_MS_POWERPOINT = EMimeContentType.APPLICATION.buildMimeType ("vnd.ms-powerpoint");

  /** PowerPoint 2007 document. */
  public static final IMimeType APPLICATION_MS_POWERPOINT_2007 = EMimeContentType.APPLICATION.buildMimeType ("vnd.openxmlformats-officedocument.presentationml.presentation");

  /** Any byte stream. */
  public static final IMimeType APPLICATION_OCTET_STREAM = EMimeContentType.APPLICATION.buildMimeType ("octet-stream");

  /** PDF document. */
  public static final IMimeType APPLICATION_PDF = EMimeContentType.APPLICATION.buildMimeType ("pdf");

  /** Downloadable document. Special MimeType not used with filenames! */
  public static final IMimeType APPLICATION_FORCE_DOWNLOAD = EMimeContentType.APPLICATION.buildMimeType ("force-download");

  /** ZIP document. */
  public static final IMimeType APPLICATION_ZIP = EMimeContentType.APPLICATION.buildMimeType ("zip");

  /** JSON document. */
  public static final IMimeType APPLICATION_JSON = EMimeContentType.APPLICATION.buildMimeType ("json");

  /** Atom XML feed. */
  public static final IMimeType APPLICATION_ATOM_XML = EMimeContentType.APPLICATION.buildMimeType ("atom+xml");

  /** RSS XML feed. */
  public static final IMimeType APPLICATION_RSS_XML = EMimeContentType.APPLICATION.buildMimeType ("rss+xml");

  /** Shockwave/Flash */
  public static final IMimeType APPLICATION_SHOCKWAVE_FLASH = EMimeContentType.APPLICATION.buildMimeType ("x-shockwave-flash");

  /** Java applet */
  public static final IMimeType APPLICATION_JAVA_APPLET = EMimeContentType.APPLICATION.buildMimeType ("java-applet");

  /** XML document. */
  public static final IMimeType APPLICATION_XML = EMimeContentType.APPLICATION.buildMimeType ("xml");

  /** For URL posting. Not used in filenames! */
  public static final IMimeType APPLICATION_X_WWW_FORM_URLENCODED = EMimeContentType.APPLICATION.buildMimeType ("x-www-form-urlencoded");

  /** For MP3 files */
  public static final IMimeType AUDIO_MP3 = EMimeContentType.AUDIO.buildMimeType ("mpeg");

  /** Bitmap image. */
  public static final IMimeType IMAGE_BMP = EMimeContentType.IMAGE.buildMimeType ("bmp");

  /** GIF image. */
  public static final IMimeType IMAGE_GIF = EMimeContentType.IMAGE.buildMimeType ("gif");

  /** Icon image. */
  public static final IMimeType IMAGE_ICON = EMimeContentType.IMAGE.buildMimeType ("icon");

  /** JPEG image. */
  public static final IMimeType IMAGE_JPG = EMimeContentType.IMAGE.buildMimeType ("jpeg");

  /** PNG image. */
  public static final IMimeType IMAGE_PNG = EMimeContentType.IMAGE.buildMimeType ("png");

  /** TIFF image. */
  public static final IMimeType IMAGE_TIFF = EMimeContentType.IMAGE.buildMimeType ("tiff");

  /** Photoshop image. */
  public static final IMimeType IMAGE_PSD = EMimeContentType.IMAGE.buildMimeType ("vnd.adobe.photoshop");

  /** WebP image. */
  public static final IMimeType IMAGE_WEBP = EMimeContentType.IMAGE.buildMimeType ("webp");

  /** Icon image. */
  public static final IMimeType IMAGE_X_ICON = EMimeContentType.IMAGE.buildMimeType ("x-icon");

  /** For HTML upload forms. Not used for filenames! */
  public static final IMimeType MULTIPART_FORMDATA = EMimeContentType.MULTIPART.buildMimeType ("form-data");

  /** CSV document. */
  public static final IMimeType TEXT_CSV = EMimeContentType.TEXT.buildMimeType ("csv");

  /** HTML document. */
  public static final IMimeType TEXT_HTML = EMimeContentType.TEXT.buildMimeType ("html");

  /** HTML5 sandboxed document. */
  public static final IMimeType TEXT_HTML_SANDBOXED = EMimeContentType.TEXT.buildMimeType ("html-sandboxed");

  /** JavaScript document. */
  public static final IMimeType TEXT_JAVASCRIPT = EMimeContentType.TEXT.buildMimeType ("javascript");

  /** Plain text document. */
  public static final IMimeType TEXT_PLAIN = EMimeContentType.TEXT.buildMimeType ("plain");

  /** XML document. */
  public static final IMimeType TEXT_XML = EMimeContentType.TEXT.buildMimeType ("xml");

  /** CSS style-sheet document. */
  public static final IMimeType TEXT_CSS = EMimeContentType.TEXT.buildMimeType ("css");

  /** Mozilla CSP */
  public static final IMimeType TEXT_CONTENT_SECURITY_POLICY = EMimeContentType.TEXT.buildMimeType ("x-content-security-policy");

  /** The character used to separate content type and sub type: '/' */
  public static final char SEPARATOR_CONTENTTYPE_SUBTYPE = '/';

  /** The separator of between parameters: ';' */
  public static final char SEPARATOR_PARAMETER = ';';

  /** The separator between parameters names and parameter values: '=' */
  public static final char SEPARATOR_PARAMETER_NAME_VALUE = '=';

  /** The special "charset" MIME type parameter name */
  public static final String PARAMETER_NAME_CHARSET = "charset";

  /**
   * The default quoting algorithm to be used:
   * {@link EMimeQuoting#QUOTED_STRING}
   */
  public static final EMimeQuoting DEFAULT_QUOTING = EMimeQuoting.QUOTED_STRING;

  @PresentForCodeCoverage
  private static final CMimeType s_aInstance = new CMimeType ();

  private CMimeType ()
  {}
}
