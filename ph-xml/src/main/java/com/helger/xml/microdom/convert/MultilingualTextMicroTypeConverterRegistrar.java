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
package com.helger.xml.microdom.convert;

import java.util.Comparator;
import java.util.Locale;
import java.util.Map;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonempty;
import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.IsSPIImplementation;
import com.helger.text.IMultilingualText;
import com.helger.text.MultilingualText;
import com.helger.text.ReadOnlyMultilingualText;
import com.helger.text.locale.LocaleCache;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.IMicroQName;
import com.helger.xml.microdom.MicroElement;
import com.helger.xml.microdom.MicroQName;

/**
 * {@link IMicroTypeConverterRegistrarSPI} implementation for {@link ReadOnlyMultilingualText} and
 * {@link MultilingualText}.
 *
 * @author Philip Helger
 */
@Immutable
@IsSPIImplementation
public final class MultilingualTextMicroTypeConverterRegistrar implements IMicroTypeConverterRegistrarSPI
{
  private abstract static class AbstractMLTConverter <T extends IMultilingualText> implements IMicroTypeConverter <T>
  {
    private static final String ELEMENT_TEXT = "text";
    private static final IMicroQName ATTR_LOCALE = new MicroQName ("locale");

    protected AbstractMLTConverter ()
    {}

    @NonNull
    public final IMicroElement convertToMicroElement (@NonNull final T aSource,
                                                      @Nullable final String sNamespaceURI,
                                                      @NonNull @Nonempty final String sTagName)
    {
      final IMicroElement eMText = new MicroElement (sNamespaceURI, sTagName);
      for (final Map.Entry <Locale, String> aEntry : aSource.texts ()
                                                            .getSortedByKey (Comparator.comparing (Locale::toString))
                                                            .entrySet ())
      {
        final IMicroElement eText = eMText.addElementNS (sNamespaceURI, ELEMENT_TEXT);
        eText.setAttribute (ATTR_LOCALE, aEntry.getKey ().toString ());
        eText.addText (aEntry.getValue ());
      }
      return eMText;
    }

    @NonNull
    protected static MultilingualText convertToMLT (@NonNull final IMicroElement aElement)
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

  public static final class ReadOnlyMultilingualTextConverter extends AbstractMLTConverter <ReadOnlyMultilingualText>
  {
    @NonNull
    public ReadOnlyMultilingualText convertToNative (@NonNull final IMicroElement aElement)
    {
      return new ReadOnlyMultilingualText (convertToMLT (aElement));
    }
  }

  public static final class MultilingualTextConverter extends AbstractMLTConverter <MultilingualText>
  {
    @NonNull
    public MultilingualText convertToNative (@NonNull final IMicroElement aElement)
    {
      return convertToMLT (aElement);
    }
  }

  public void registerMicroTypeConverter (@NonNull final IMicroTypeConverterRegistry aRegistry)
  {
    // Register the read-only version first!
    aRegistry.registerMicroElementTypeConverter (ReadOnlyMultilingualText.class,
                                                 new ReadOnlyMultilingualTextConverter ());

    // Register the writable version afterwards!
    aRegistry.registerMicroElementTypeConverter (MultilingualText.class, new MultilingualTextConverter ());
  }
}
