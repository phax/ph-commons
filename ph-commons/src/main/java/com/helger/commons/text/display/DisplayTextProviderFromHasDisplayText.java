/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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
package com.helger.commons.text.display;

import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Default implementation of {@link IDisplayTextProvider} for
 * {@link IHasDisplayText} objects.
 *
 * @author Philip Helger
 */
public final class DisplayTextProviderFromHasDisplayText implements IDisplayTextProvider <IHasDisplayText>
{
  @Nullable
  public String getDisplayText (@Nullable final IHasDisplayText aObject, @Nonnull final Locale aContentLocale)
  {
    return aObject == null ? null : aObject.getDisplayText (aContentLocale);
  }
}
