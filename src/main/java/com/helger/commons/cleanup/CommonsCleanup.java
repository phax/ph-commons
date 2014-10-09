/**
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
package com.helger.commons.cleanup;

import javax.annotation.concurrent.Immutable;

import com.helger.commons.SystemProperties;
import com.helger.commons.annotations.PresentForCodeCoverage;
import com.helger.commons.compare.CollatorUtils;
import com.helger.commons.equals.EqualsImplementationRegistry;
import com.helger.commons.gfx.ImageDataManager;
import com.helger.commons.hash.HashCodeImplementationRegistry;
import com.helger.commons.jaxb.JAXBContextCache;
import com.helger.commons.lang.ClassHierarchyCache;
import com.helger.commons.lang.EnumHelper;
import com.helger.commons.locale.LocaleCache;
import com.helger.commons.locale.LocaleUtils;
import com.helger.commons.locale.country.CountryCache;
import com.helger.commons.microdom.convert.MicroTypeConverterRegistry;
import com.helger.commons.mime.MimeTypeDeterminator;
import com.helger.commons.mime.MimeTypeInfoManager;
import com.helger.commons.regex.RegExPool;
import com.helger.commons.serialize.convert.SerializationConverterRegistry;
import com.helger.commons.stats.StatisticsManager;
import com.helger.commons.text.resolve.DefaultTextResolver;
import com.helger.commons.text.resource.ResourceBundleUtils;
import com.helger.commons.thirdparty.ThirdPartyModuleRegistry;
import com.helger.commons.typeconvert.TypeConverterRegistry;
import com.helger.commons.url.URLProtocolRegistry;
import com.helger.commons.xml.schema.XMLSchemaCache;

/**
 * The sole purpose of this class to clear all caches, that reside in this
 * library.
 *
 * @author Philip Helger
 */
@Immutable
public final class CommonsCleanup
{
  @SuppressWarnings ("unused")
  @PresentForCodeCoverage
  private static final CommonsCleanup s_aInstance = new CommonsCleanup ();

  private CommonsCleanup ()
  {}

  /**
   * Cleanup all custom caches contained in this library. Loaded SPI
   * implementations are not affected by this method!
   */
  public static void cleanup ()
  {
    // Reset caches to the default values
    LocaleCache.resetCache ();
    CountryCache.resetCache ();
    MimeTypeDeterminator.resetCache ();
    if (MimeTypeInfoManager.isDefaultInstantiated ())
      MimeTypeInfoManager.getDefaultInstance ().resetCacheToDefault ();
    if (MicroTypeConverterRegistry.isInstantiated ())
      MicroTypeConverterRegistry.getInstance ().reinitialize ();
    if (SerializationConverterRegistry.isInstantiated ())
      SerializationConverterRegistry.getInstance ().reinitialize ();
    if (ThirdPartyModuleRegistry.isInstantiated ())
      ThirdPartyModuleRegistry.getInstance ().reinitialize ();
    if (TypeConverterRegistry.isInstantiated ())
      TypeConverterRegistry.getInstance ().reinitialize ();
    if (URLProtocolRegistry.isInstantiated ())
      URLProtocolRegistry.getInstance ().reinitialize ();

    // Clear caches
    ImageDataManager.clearCache ();
    DefaultTextResolver.clearCache ();
    EnumHelper.clearCache ();
    ResourceBundleUtils.clearCache ();
    RegExPool.clearPatternCache ();
    CollatorUtils.clearCache ();
    LocaleUtils.clearCache ();
    if (JAXBContextCache.isInstantiated ())
      JAXBContextCache.getInstance ().clearCache ();
    if (XMLSchemaCache.isInstantiated ())
      XMLSchemaCache.getInstance ().clearCache ();
    StatisticsManager.clearCache ();
    if (EqualsImplementationRegistry.isInstantiated ())
      EqualsImplementationRegistry.getInstance ().clearCache ();
    HashCodeImplementationRegistry.clearCache ();
    SystemProperties.clearWarnedPropertyNames ();

    // Clean this one last as it is used in equals and hashCode implementations!
    ClassHierarchyCache.clearCache ();
  }
}
