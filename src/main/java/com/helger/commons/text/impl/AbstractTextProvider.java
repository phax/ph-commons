/**
 * Copyright (C) 2006-2014 phloc systems (www.phloc.com)
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
package com.helger.commons.text.impl;

import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.text.ITextProvider;

/**
 * An abstract implementation of the {@link ITextProvider} that has default
 * implementations for the formatting things.
 * 
 * @author Philip Helger
 */
public abstract class AbstractTextProvider extends AbstractSimpleTextProvider implements ITextProvider
{
  protected AbstractTextProvider ()
  {}

  @Nullable
  public final String getTextWithArgs (@Nonnull final Locale aContentLocale, @Nullable final Object... aArgs)
  {
    final String sText = getText (aContentLocale);
    return TextFormatter.getFormattedText (sText, aArgs);
  }

  @Nullable
  public final String getTextWithLocaleFallbackAndArgs (@Nonnull final Locale aContentLocale,
                                                        @Nullable final Object... aArgs)
  {
    final String sText = getTextWithLocaleFallback (aContentLocale);
    return TextFormatter.getFormattedText (sText, aArgs);
  }
}
