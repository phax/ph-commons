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
package com.helger.commons.cleanup;

import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.misc.PresentForCodeCoverage;
import com.helger.commons.compare.CollatorHelper;
import com.helger.commons.equals.EqualsImplementationRegistry;
import com.helger.commons.gfx.ImageDataManager;
import com.helger.commons.hashcode.HashCodeImplementationRegistry;
import com.helger.commons.lang.ClassHierarchyCache;
import com.helger.commons.lang.EnumHelper;
import com.helger.commons.locale.LocaleCache;
import com.helger.commons.locale.LocaleHelper;
import com.helger.commons.locale.country.CountryCache;
import com.helger.commons.locale.language.LanguageCache;
import com.helger.commons.mime.MimeTypeDeterminator;
import com.helger.commons.regex.RegExCache;
import com.helger.commons.serialize.convert.SerializationConverterRegistry;
import com.helger.commons.statistics.StatisticsManager;
import com.helger.commons.system.SystemProperties;
import com.helger.commons.text.resolve.DefaultTextResolver;
import com.helger.commons.text.resourcebundle.ResourceBundleHelper;
import com.helger.commons.thirdparty.ThirdPartyModuleRegistry;
import com.helger.commons.typeconvert.TypeConverterRegistry;
import com.helger.commons.url.URLProtocolRegistry;

/**
 * The sole purpose of this class to clear all caches, that reside in this
 * library.
 *
 * @author Philip Helger
 */
@Immutable
public final class CommonsCleanup
{
  @PresentForCodeCoverage
  private static final CommonsCleanup INSTANCE = new CommonsCleanup ();

  private CommonsCleanup ()
  {}

  /**
   * Cleanup all custom caches contained in this library. Loaded SPI
   * implementations are not affected by this method!
   */
  public static void cleanup ()
  {
    // Reinitialize singletons to the default values
    if (LocaleCache.isInstantiated ())
      LocaleCache.getInstance ().reinitialize ();
    if (CountryCache.isInstantiated ())
      CountryCache.getInstance ().reinitialize ();
    if (LanguageCache.isInstantiated ())
      LanguageCache.getInstance ().reinitialize ();
    if (SerializationConverterRegistry.isInstantiated ())
      SerializationConverterRegistry.getInstance ().reinitialize ();
    if (MimeTypeDeterminator.isInstantiated ())
      MimeTypeDeterminator.getInstance ().reinitialize ();
    if (ThirdPartyModuleRegistry.isInstantiated ())
      ThirdPartyModuleRegistry.getInstance ().reinitialize ();
    if (TypeConverterRegistry.isInstantiated ())
      TypeConverterRegistry.getInstance ().reinitialize ();
    if (URLProtocolRegistry.isInstantiated ())
      URLProtocolRegistry.getInstance ().reinitialize ();
    if (EqualsImplementationRegistry.isInstantiated ())
      EqualsImplementationRegistry.getInstance ().reinitialize ();
    if (HashCodeImplementationRegistry.isInstantiated ())
      HashCodeImplementationRegistry.getInstance ().reinitialize ();

    // Clear caches
    if (DefaultTextResolver.isInstantiated ())
      DefaultTextResolver.getInstance ().clearCache ();
    EnumHelper.clearCache ();
    ResourceBundleHelper.clearCache ();
    if (RegExCache.isInstantiated ())
      RegExCache.getInstance ().clearCache ();
    CollatorHelper.clearCache ();
    LocaleHelper.clearCache ();
    StatisticsManager.clearCache ();
    SystemProperties.clearWarnedPropertyNames ();
    if (ImageDataManager.isInstantiated ())
      ImageDataManager.getInstance ().clearCache ();

    // Clean this one last as it is used in equals and hashCode implementations!
    ClassHierarchyCache.clearCache ();
  }
}
