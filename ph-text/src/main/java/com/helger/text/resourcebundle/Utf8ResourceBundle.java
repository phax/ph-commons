/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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
package com.helger.text.resourcebundle;

import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.jspecify.annotations.NonNull;

import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.PresentForCodeCoverage;

/**
 * Helper class to handle read-only property resource bundles reading only UTF-8 text strings.
 *
 * @author Philip Helger
 */
@Immutable
public final class Utf8ResourceBundle
{
  @PresentForCodeCoverage
  private static final Utf8ResourceBundle INSTANCE = new Utf8ResourceBundle ();

  private Utf8ResourceBundle ()
  {}

  @NonNull
  private static ResourceBundle _createUtf8PropertyResourceBundle (@NonNull final ResourceBundle aBundle)
  {
    if (aBundle instanceof final PropertyResourceBundle aPropResBundle)
      return new Utf8PropertyResourceBundle (aPropResBundle);

    return aBundle;
  }

  /**
   * Get a UTF-8 resource bundle for the given base name using the default
   * locale.
   *
   * @param sBaseName
   *        The base name of the resource bundle. May not be <code>null</code>.
   * @return The resource bundle. Never <code>null</code>.
   */
  @NonNull
  public static ResourceBundle getBundle (@NonNull final String sBaseName)
  {
    return _createUtf8PropertyResourceBundle (ResourceBundle.getBundle (sBaseName, Locale.getDefault ()));
  }

  /**
   * Get a UTF-8 resource bundle for the given base name and locale.
   *
   * @param sBaseName
   *        The base name of the resource bundle. May not be <code>null</code>.
   * @param aLocale
   *        The locale to use. May not be <code>null</code>.
   * @return The resource bundle. Never <code>null</code>.
   */
  @NonNull
  public static ResourceBundle getBundle (@NonNull final String sBaseName, @NonNull final Locale aLocale)
  {
    return _createUtf8PropertyResourceBundle (ResourceBundle.getBundle (sBaseName, aLocale));
  }

  /**
   * Get a UTF-8 resource bundle for the given base name, locale and class
   * loader.
   *
   * @param sBaseName
   *        The base name of the resource bundle. May not be <code>null</code>.
   * @param aLocale
   *        The locale to use. May not be <code>null</code>.
   * @param aClassLoader
   *        The class loader to use. May not be <code>null</code>.
   * @return The resource bundle. Never <code>null</code>.
   */
  @NonNull
  public static ResourceBundle getBundle (@NonNull final String sBaseName,
                                          @NonNull final Locale aLocale,
                                          @NonNull final ClassLoader aClassLoader)
  {
    return _createUtf8PropertyResourceBundle (ResourceBundle.getBundle (sBaseName, aLocale, aClassLoader));
  }
}
