/**
 * Copyright (C) 2006-2014 phloc systems (www.phloc.com)
 * Copyright (C) 2014 Philip Helger (www.helger.com)
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

import java.text.DecimalFormatSymbols;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.annotations.PresentForCodeCoverage;

/**
 * A small helper class, that constructs {@link DecimalFormatSymbols} objects in
 * the best suitable way. For Java < 1.6 it is to use
 * "new DecimalFormatSymbols (Locale)". For Java >= 1.6
 * "DecimalFormatSymbols.getInstance (Locale)" is more suitable!
 * 
 * @author Philip Helger
 */
@Immutable
public final class DecimalFormatSymbolsFactory
{
  @PresentForCodeCoverage
  @SuppressWarnings ("unused")
  private static final DecimalFormatSymbolsFactory s_aInstance = new DecimalFormatSymbolsFactory ();

  private DecimalFormatSymbolsFactory ()
  {}

  @Nonnull
  public static DecimalFormatSymbols getInstance (final Locale aLocale)
  {
    // IFJDK5
    // return new DecimalFormatSymbols (aLocale);
    // ELSE
    return DecimalFormatSymbols.getInstance (aLocale);
    // ENDIF
  }
}
