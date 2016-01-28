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
package com.helger.commons.microdom.convert;

import java.util.Comparator;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.annotation.IsSPIImplementation;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.locale.LocaleCache;
import com.helger.commons.microdom.IMicroElement;
import com.helger.commons.microdom.MicroElement;
import com.helger.commons.text.IMultilingualText;
import com.helger.commons.text.MultilingualText;
import com.helger.commons.text.ReadOnlyMultilingualText;

/**
 * {@link IMicroTypeConverterRegistrarSPI} implementation for
 * {@link ReadOnlyMultilingualText} and {@link MultilingualText}.
 *
 * @author Philip Helger
 */
@Immutable
@IsSPIImplementation
public final class MultilingualTextMicroTypeConverterRegistrar implements IMicroTypeConverterRegistrarSPI
{
  private abstract static class AbstractMLTConverter implements IMicroTypeConverter
  {
    private static final String ELEMENT_TEXT = "text";
    private static final String ATTR_LOCALE = "locale";

    @Nonnull
    public final IMicroElement convertToMicroElement (@Nonnull final Object aSource,
                                                      @Nullable final String sNamespaceURI,
                                                      @Nonnull @Nonempty final String sTagName)
    {
      final IMultilingualText aMLT = (IMultilingualText) aSource;
      final IMicroElement eMText = new MicroElement (sNamespaceURI, sTagName);
      for (final Locale aLocale : CollectionHelper.getSorted (aMLT.getAllLocales (),
                                                              Comparator.comparing (Locale::toString)))
      {
        final IMicroElement eText = eMText.appendElement (sNamespaceURI, ELEMENT_TEXT);
        eText.setAttribute (ATTR_LOCALE, aLocale.toString ());
        eText.appendText (aMLT.getText (aLocale));
      }
      return eMText;
    }

    @Nonnull
    protected static MultilingualText convertToMLT (@Nonnull final IMicroElement aElement)
    {
      final MultilingualText aMLT = new MultilingualText ();
      final LocaleCache aLC = LocaleCache.getInstance ();
      for (final IMicroElement eText : aElement.getAllChildElements (ELEMENT_TEXT))
      {
        final Locale aLocale = aLC.getLocale (eText.getAttributeValue (ATTR_LOCALE));
        aMLT.setText (aLocale, eText.getTextContent ());
      }
      return aMLT;
    }
  }

  public static final class ReadOnlyMultilingualTextConverter extends AbstractMLTConverter
  {
    @Nonnull
    public ReadOnlyMultilingualText convertToNative (@Nonnull final IMicroElement aElement)
    {
      return new ReadOnlyMultilingualText (convertToMLT (aElement));
    }
  }

  public static final class MultilingualTextConverter extends AbstractMLTConverter
  {
    @Nonnull
    public MultilingualText convertToNative (@Nonnull final IMicroElement aElement)
    {
      return convertToMLT (aElement);
    }
  }

  public void registerMicroTypeConverter (@Nonnull final IMicroTypeConverterRegistry aRegistry)
  {
    // Register the read-only version first!
    aRegistry.registerMicroElementTypeConverter (ReadOnlyMultilingualText.class,
                                                 new ReadOnlyMultilingualTextConverter ());

    // Register the writable version afterwards!
    aRegistry.registerMicroElementTypeConverter (MultilingualText.class, new MultilingualTextConverter ());
  }
}
