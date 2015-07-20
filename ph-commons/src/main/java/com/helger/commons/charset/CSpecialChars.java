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
package com.helger.commons.charset;

import javax.annotation.concurrent.Immutable;

import com.helger.commons.annotation.PresentForCodeCoverage;

/**
 * These constants only exist to work around the common file encoding problem
 * with Cp1252/UTF-8. This file should only be stored as UTF-8!
 *
 * @author Philip Helger
 */
@Immutable
public final class CSpecialChars
{
  /** Special character ä */
  public static final char AUML_LC = '\u00E4';
  /** Special character ä */
  public static final String AUML_LC_STR = Character.toString (AUML_LC);
  /** Special character Ä */
  public static final char AUML_UC = '\u00C4';
  /** Special character Ä */
  public static final String AUML_UC_STR = Character.toString (AUML_UC);
  /** Special character ö */
  public static final char OUML_LC = '\u00F6';
  /** Special character ö */
  public static final String OUML_LC_STR = Character.toString (OUML_LC);
  /** Special character Ö */
  public static final char OUML_UC = '\u00D6';
  /** Special character Ö */
  public static final String OUML_UC_STR = Character.toString (OUML_UC);
  /** Special character ü */
  public static final char UUML_LC = '\u00FC';
  /** Special character ü */
  public static final String UUML_LC_STR = Character.toString (UUML_LC);
  /** Special character Ü */
  public static final char UUML_UC = '\u00DC';
  /** Special character Ü */
  public static final String UUML_UC_STR = Character.toString (UUML_UC);
  /** Special character ß */
  public static final char SZLIG = '\u00DF';
  /** Special character ß */
  public static final String SZLIG_STR = Character.toString (SZLIG);
  /** Special character € */
  public static final char EURO = '\u20ac';
  /** Special character € */
  public static final String EURO_STR = Character.toString (EURO);
  /** Special character © */
  public static final char COPYRIGHT = '\u00a9';
  /** Special character © */
  public static final String COPYRIGHT_STR = Character.toString (COPYRIGHT);

  @PresentForCodeCoverage
  private static final CSpecialChars s_aInstance = new CSpecialChars ();

  private CSpecialChars ()
  {}
}
