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
package com.helger.xml.resourcebundle;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.jspecify.annotations.NonNull;

import com.helger.annotation.style.CodingStyleguideUnaware;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.classloader.ClassLoaderHelper;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.io.stream.StreamHelper;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsList;
import com.helger.io.resource.URLResource;

/**
 * Special {@link java.util.ResourceBundle.Control} to handle XML files
 *
 * @author Philip Helger
 */
public final class XMLResourceBundleControl extends ResourceBundle.Control
{
  private static final String FORMAT_XML = "xml";
  private static final ICommonsList <String> FORMATS = new CommonsArrayList <> (FORMAT_XML);

  @Override
  @ReturnsMutableCopy
  @CodingStyleguideUnaware
  public List <String> getFormats (@NonNull final String sBaseName)
  {
    ValueEnforcer.notNull (sBaseName, "BaseName");
    return FORMATS.getClone ();
  }

  @Override
  public ResourceBundle newBundle (@NonNull final String sBaseName,
                                   @NonNull final Locale aLocale,
                                   @NonNull final String sFormat,
                                   @NonNull final ClassLoader aClassLoader,
                                   final boolean bReload) throws IOException
  {
    ValueEnforcer.notNull (sBaseName, "BaseName");
    ValueEnforcer.notNull (aLocale, "Locale");
    ValueEnforcer.notNull (sFormat, "Format");
    ValueEnforcer.notNull (aClassLoader, "ClassLoader");

    // We can only handle XML
    if (sFormat.equals (FORMAT_XML))
    {
      final String sBundleName = toBundleName (sBaseName, aLocale);
      final String sResourceName = toResourceName (sBundleName, sFormat);
      final URL aResourceUrl = ClassLoaderHelper.getResource (aClassLoader, sResourceName);
      if (aResourceUrl != null)
        try (final InputStream aSrcIS = URLResource.getInputStream (aResourceUrl);
             final InputStream aIS = StreamHelper.getBuffered (aSrcIS))
        {
          return new XMLResourceBundle (aIS);
        }
    }
    return null;
  }
}
