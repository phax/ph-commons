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
package com.helger.commons.supplementary.test.java;

import java.nio.charset.Charset;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.compare.IComparator;
import com.helger.commons.system.SystemHelper;
import com.helger.commons.system.SystemProperties;

public final class MainJavaEncodingInfo
{
  private static final Logger LOGGER = LoggerFactory.getLogger (MainJavaEncodingInfo.class);

  private MainJavaEncodingInfo ()
  {}

  public static void main (final String [] args)
  {
    for (final Map.Entry <String, String> aEntry : SystemProperties.getAllProperties ()
                                                                   .getSortedByKey (IComparator.getComparatorCollating (Locale.US))
                                                                   .entrySet ())
      LOGGER.info (aEntry.getKey () + " == " + aEntry.getValue ());
    LOGGER.info ("Default Locale: " + SystemHelper.getSystemLocale ());
    LOGGER.info ("All locales:");
    for (final Locale aLocale : Locale.getAvailableLocales ())
      LOGGER.info ("  " + aLocale);
    LOGGER.info ("All charsets:");
    for (final Map.Entry <String, Charset> aEntry : Charset.availableCharsets ().entrySet ())
      LOGGER.info ("  " + aEntry.getKey () + " -- " + aEntry.getValue ().displayName (Locale.US));
  }
}
