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
package com.helger.commons.text.resourcebundle;

import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.annotation.PresentForCodeCoverage;

/**
 * Helper class to handle read-only property resource bundles reading only UTF-8
 * text strings.
 *
 * @author Philip Helger
 */
@Immutable
public final class Utf8ResourceBundle
{
  @PresentForCodeCoverage
  private static final Utf8ResourceBundle s_aInstance = new Utf8ResourceBundle ();

  private Utf8ResourceBundle ()
  {}

  @Nonnull
  private static ResourceBundle _createUtf8PropertyResourceBundle (@Nonnull final ResourceBundle aBundle)
  {
    if (!(aBundle instanceof PropertyResourceBundle))
      return aBundle;

    return new Utf8PropertyResourceBundle ((PropertyResourceBundle) aBundle);
  }

  @Nonnull
  public static ResourceBundle getBundle (@Nonnull final String sBaseName)
  {
    return _createUtf8PropertyResourceBundle (ResourceBundle.getBundle (sBaseName));
  }

  @Nonnull
  public static ResourceBundle getBundle (@Nonnull final String sBaseName, @Nonnull final Locale aLocale)
  {
    return _createUtf8PropertyResourceBundle (ResourceBundle.getBundle (sBaseName, aLocale));
  }

  @Nonnull
  public static ResourceBundle getBundle (@Nonnull final String sBaseName,
                                          @Nonnull final Locale aLocale,
                                          @Nonnull final ClassLoader aClassLoader)
  {
    return _createUtf8PropertyResourceBundle (ResourceBundle.getBundle (sBaseName, aLocale, aClassLoader));
  }
}
