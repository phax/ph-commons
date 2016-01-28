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
package com.helger.commons.supplementary.test.java;

import java.nio.charset.Charset;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.compare.ISerializableComparator;
import com.helger.commons.system.SystemHelper;

public final class MainJavaEncodingInfo
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (MainJavaEncodingInfo.class);

  public static void main (final String [] args)
  {
    for (final Map.Entry <Object, Object> aEntry : CollectionHelper.getSortedByKey (System.getProperties (),
                                                                                    ISerializableComparator.getComparatorCollating (Object::toString,
                                                                                                                                    Locale.US))
                                                                   .entrySet ())
      s_aLogger.info (aEntry.getKey () + " == " + aEntry.getValue ());
    s_aLogger.info ("Default Locale: " + SystemHelper.getSystemLocale ());
    s_aLogger.info ("All locales:");
    for (final Locale aLocale : Locale.getAvailableLocales ())
      s_aLogger.info ("  " + aLocale);
    s_aLogger.info ("All charsets:");
    for (final Map.Entry <String, Charset> aEntry : Charset.availableCharsets ().entrySet ())
      s_aLogger.info ("  " + aEntry.getKey () + " -- " + aEntry.getValue ().displayName ());
  }
}
