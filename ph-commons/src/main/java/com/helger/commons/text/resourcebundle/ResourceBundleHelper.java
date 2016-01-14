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
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.PropertyKey;
import javax.annotation.concurrent.Immutable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.annotation.PresentForCodeCoverage;
import com.helger.commons.lang.ClassLoaderHelper;

/**
 * Resource bundle utility methods
 *
 * @author Philip Helger
 */
@Immutable
public final class ResourceBundleHelper
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (ResourceBundleHelper.class);

  @PresentForCodeCoverage
  private static final ResourceBundleHelper s_aInstance = new ResourceBundleHelper ();

  private ResourceBundleHelper ()
  {}

  @Nullable
  public static ResourceBundle getResourceBundle (@Nonnull final String sBundleName,
                                                  @Nonnull final Locale aContentLocale)
  {
    return getResourceBundle (sBundleName, aContentLocale, ClassLoaderHelper.getDefaultClassLoader ());
  }

  @Nullable
  public static ResourceBundle getResourceBundle (@Nonnull final String sBundleName,
                                                  @Nonnull final Locale aContentLocale,
                                                  @Nonnull final ClassLoader aClassLoader)
  {
    try
    {
      return ResourceBundle.getBundle (sBundleName, aContentLocale, aClassLoader);
    }
    catch (final MissingResourceException ex)
    {
      return null;
    }
  }

  @Nullable
  public static ResourceBundle getUtf8ResourceBundle (@Nonnull final String sBundleName,
                                                      @Nonnull final Locale aContentLocale)
  {
    return getUtf8ResourceBundle (sBundleName, aContentLocale, ClassLoaderHelper.getDefaultClassLoader ());
  }

  @Nullable
  public static ResourceBundle getUtf8ResourceBundle (@Nonnull final String sBundleName,
                                                      @Nonnull final Locale aContentLocale,
                                                      @Nonnull final ClassLoader aClassLoader)
  {
    try
    {
      return Utf8ResourceBundle.getBundle (sBundleName, aContentLocale, aClassLoader);
    }
    catch (final MissingResourceException ex)
    {
      return null;
    }
  }

  @Nullable
  public static String getString (@Nullable final ResourceBundle aResourceBundle,
                                  @Nonnull @PropertyKey final String sKey)
  {
    if (aResourceBundle != null)
      try
      {
        return aResourceBundle.getString (sKey);
      }
      catch (final MissingResourceException ex)
      {
        // Fall through
      }
    return null;
  }

  @Nullable
  public static String getString (@Nonnull final String sBundleName,
                                  @Nonnull final Locale aContentLocale,
                                  @Nonnull @PropertyKey final String sKey)
  {
    return getString (getResourceBundle (sBundleName, aContentLocale), sKey);
  }

  @Nullable
  public static String getString (@Nonnull final String sBundleName,
                                  @Nonnull final Locale aContentLocale,
                                  @Nonnull @PropertyKey final String sKey,
                                  @Nonnull final ClassLoader aClassLoader)
  {
    return getString (getResourceBundle (sBundleName, aContentLocale, aClassLoader), sKey);
  }

  @Nullable
  public static String getUtf8String (@Nonnull final String sBundleName,
                                      @Nonnull final Locale aContentLocale,
                                      @Nonnull @PropertyKey final String sKey)
  {
    return getString (getUtf8ResourceBundle (sBundleName, aContentLocale), sKey);
  }

  @Nullable
  public static String getUtf8String (@Nonnull final String sBundleName,
                                      @Nonnull final Locale aContentLocale,
                                      @Nonnull @PropertyKey final String sKey,
                                      @Nonnull final ClassLoader aClassLoader)
  {
    return getString (getUtf8ResourceBundle (sBundleName, aContentLocale, aClassLoader), sKey);
  }

  /**
   * Clear the complete resource bundle cache using the default class loader!
   */
  public static void clearCache ()
  {
    clearCache (ClassLoaderHelper.getDefaultClassLoader ());
  }

  /**
   * Clear the complete resource bundle cache using the specified class loader!
   *
   * @param aClassLoader
   *        The class loader to be used. May not be <code>null</code>.
   */
  public static void clearCache (@Nonnull final ClassLoader aClassLoader)
  {
    ResourceBundle.clearCache (aClassLoader);
    if (s_aLogger.isDebugEnabled ())
      s_aLogger.debug ("Cache was cleared: " + ResourceBundle.class.getName () + "; classloader=" + aClassLoader);
  }
}
