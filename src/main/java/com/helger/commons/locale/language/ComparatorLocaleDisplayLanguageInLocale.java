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
package com.helger.commons.locale.language;

import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.compare.AbstractCollationComparator;

/**
 * {@link java.util.Comparator} that sorts {@link Locale} objects by their
 * language display name in the passed locale.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class ComparatorLocaleDisplayLanguageInLocale extends AbstractCollationComparator <Locale>
{
  private final Locale m_aContentLocale;

  public ComparatorLocaleDisplayLanguageInLocale (@Nullable final Locale aSortLocale,
                                                  @Nonnull final Locale aContentLocale)
  {
    super (aSortLocale);
    ValueEnforcer.notNull (aContentLocale, "ContentLocale");
    m_aContentLocale = aContentLocale;
  }

  @Nonnull
  public Locale getContentLocale ()
  {
    return m_aContentLocale;
  }

  @Override
  protected String asString (@Nonnull final Locale aLocale)
  {
    return aLocale.getDisplayLanguage (m_aContentLocale);
  }
}
