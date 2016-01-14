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
package com.helger.commons.text;

import java.util.Locale;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;

/**
 * This class represents a multilingual text that cannot be altered after the
 * constructor.
 *
 * @author Philip Helger
 */
@Immutable
public class ReadOnlyMultilingualText extends AbstractReadOnlyMapBasedMultilingualText
{
  /**
   * Create an empty read-only multilingual text. Handle with care, as this type
   * does not allow for public modification!
   */
  public ReadOnlyMultilingualText ()
  {}

  public ReadOnlyMultilingualText (@Nonnull final Locale aContentLocale, @Nonnull final String sValue)
  {
    internalAddText (aContentLocale, sValue);
  }

  public ReadOnlyMultilingualText (@Nonnull final Map <Locale, String> aContent)
  {
    ValueEnforcer.notNull (aContent, "Content");

    for (final Map.Entry <Locale, String> aEntry : aContent.entrySet ())
      internalAddText (aEntry);
  }

  public ReadOnlyMultilingualText (@Nonnull final IMultilingualText aMLT)
  {
    ValueEnforcer.notNull (aMLT, "MLT");

    for (final Map.Entry <Locale, String> aEntry : aMLT.getAllTexts ().entrySet ())
      internalAddText (aEntry);
  }
}
