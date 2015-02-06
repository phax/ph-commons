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
package com.helger.commons.lang;

import java.text.DateFormatSymbols;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.annotations.PresentForCodeCoverage;

/**
 * A small helper class, that constructs {@link DateFormatSymbols} objects in
 * the best suitable way. For Java &lt; 1.6 it is to use
 * "new DateFormatSymbols (Locale)". For Java &ge; 1.6
 * "DateFormatSymbols.getInstance (Locale)" is more suitable!
 *
 * @author Philip Helger
 */
@Immutable
public final class DateFormatSymbolsFactory
{
  @PresentForCodeCoverage
  private static final DateFormatSymbolsFactory s_aInstance = new DateFormatSymbolsFactory ();

  private DateFormatSymbolsFactory ()
  {}

  @Nonnull
  public static DateFormatSymbols getInstance (final Locale aLocale)
  {
    // IFJDK5
    // return new DateFormatSymbols (aLocale);
    // ELSE
    return DateFormatSymbols.getInstance (aLocale);
    // ENDIF
  }
}
