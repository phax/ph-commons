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
package com.helger.commons.locale;

import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.compare.AbstractCollatingComparator;

/**
 * {@link java.util.Comparator} that sorts {@link Locale} objects by their
 * display name in a given locale.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class CollatingComparatorLocaleDisplayNameInLocale extends AbstractCollatingComparator <Locale>
{
  private final Locale m_aContentLocale;

  public CollatingComparatorLocaleDisplayNameInLocale (@Nullable final Locale aSortLocale,
                                                       @Nonnull final Locale aContentLocale)
  {
    super (aSortLocale);
    m_aContentLocale = ValueEnforcer.notNull (aContentLocale, "ContentLocale");
  }

  @Nonnull
  public final Locale getContentLocale ()
  {
    return m_aContentLocale;
  }

  @Override
  protected String getPart (@Nullable final Locale aLocale)
  {
    return LocaleUtils.getLocaleDisplayName (aLocale, m_aContentLocale);
  }
}
