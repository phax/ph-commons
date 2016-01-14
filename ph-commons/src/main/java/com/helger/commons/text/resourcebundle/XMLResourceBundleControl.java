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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.annotation.Nonnull;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.io.resource.URLResource;
import com.helger.commons.io.stream.StreamHelper;
import com.helger.commons.lang.ClassLoaderHelper;

/**
 * Special {@link java.util.ResourceBundle.Control} to handle XML files
 *
 * @author Philip Helger
 */
public final class XMLResourceBundleControl extends ResourceBundle.Control
{
  private static final String FORMAT_XML = "xml";
  private static final List <String> FORMATS = CollectionHelper.newList (FORMAT_XML);

  @Override
  @ReturnsMutableCopy
  public List <String> getFormats (@Nonnull final String sBaseName)
  {
    ValueEnforcer.notNull (sBaseName, "BaseName");
    return CollectionHelper.newList (FORMATS);
  }

  @Override
  public ResourceBundle newBundle (@Nonnull final String sBaseName,
                                   @Nonnull final Locale aLocale,
                                   @Nonnull final String sFormat,
                                   @Nonnull final ClassLoader aClassLoader,
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
      {
        final InputStream aIS = StreamHelper.getBuffered (URLResource.getInputStream (aResourceUrl));
        try
        {
          return new XMLResourceBundle (aIS);
        }
        finally
        {
          StreamHelper.close (aIS);
        }
      }
    }
    return null;
  }
}
