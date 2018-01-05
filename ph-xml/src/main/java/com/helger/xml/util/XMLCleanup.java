/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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
package com.helger.xml.util;

import javax.annotation.concurrent.Immutable;

import com.helger.commons.annotation.PresentForCodeCoverage;
import com.helger.xml.microdom.convert.MicroTypeConverterRegistry;
import com.helger.xml.schema.XMLSchemaCache;
import com.helger.xml.util.mime.MimeTypeInfoManager;

/**
 * The sole purpose of this class to clear all caches, that reside in this
 * library.
 *
 * @author Philip Helger
 */
@Immutable
public final class XMLCleanup
{
  @PresentForCodeCoverage
  private static final XMLCleanup s_aInstance = new XMLCleanup ();

  private XMLCleanup ()
  {}

  /**
   * Cleanup all custom caches contained in this library. Loaded SPI
   * implementations are not affected by this method!
   */
  public static void cleanup ()
  {
    if (MimeTypeInfoManager.isDefaultInstantiated ())
      MimeTypeInfoManager.getDefaultInstance ().reinitializeToDefault ();
    if (MicroTypeConverterRegistry.isInstantiated ())
      MicroTypeConverterRegistry.getInstance ().reinitialize ();
    if (XMLSchemaCache.isInstantiated ())
      XMLSchemaCache.getInstance ().clearCache ();
    XMLSchemaCache.clearPerClassLoaderCache ();
  }
}
