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

import java.text.Collator;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.compare.AbstractCollatingComparator;

/**
 * This is a collation {@link java.util.Comparator} for objects that implement
 * the {@link IDisplayTextProvider} interface.
 *
 * @author Philip Helger
 * @param <DATATYPE>
 *        The type of elements to be compared.
 */
@NotThreadSafe
public class CollatingComparatorDisplayTextProvider <DATATYPE> extends AbstractCollatingComparator <DATATYPE>
{
  private final IDisplayTextProvider <DATATYPE> m_aDisplayTextProvider;
  private final Locale m_aContentLocale;

  /**
   * Constructor.
   *
   * @param aSortLocale
   *        The locale to be used for sorting. May be <code>null</code>.
   * @param aDisplayTextProvider
   *        The display text provider to be used. May not be <code>null</code>.
   * @param aContentLocale
   *        The locale to be used to retrieve the display text of an object. May
   *        not be <code>null</code>.
   */
  public CollatingComparatorDisplayTextProvider (@Nullable final Locale aSortLocale,
                                                 @Nonnull final IDisplayTextProvider <DATATYPE> aDisplayTextProvider,
                                                 @Nonnull final Locale aContentLocale)
  {
    super (aSortLocale);
    m_aDisplayTextProvider = ValueEnforcer.notNull (aDisplayTextProvider, "DisplayTextProvider");
    m_aContentLocale = ValueEnforcer.notNull (aContentLocale, "ContentLocale");
  }

  /**
   * Constructor.
   *
   * @param aCollator
   *        The {@link Collator} to use. May not be <code>null</code>.
   * @param aDisplayTextProvider
   *        The display text provider to be used. May not be <code>null</code>.
   * @param aContentLocale
   *        The locale to be used to retrieve the display text of an object. May
   *        not be <code>null</code>.
   */
  public CollatingComparatorDisplayTextProvider (@Nullable final Collator aCollator,
                                                 @Nonnull final IDisplayTextProvider <DATATYPE> aDisplayTextProvider,
                                                 @Nonnull final Locale aContentLocale)
  {
    super (aCollator);
    m_aDisplayTextProvider = ValueEnforcer.notNull (aDisplayTextProvider, "DisplayTextProvider");
    m_aContentLocale = ValueEnforcer.notNull (aContentLocale, "ContentLocale");
  }

  @Nonnull
  public IDisplayTextProvider <DATATYPE> getDisplayTextProvider ()
  {
    return m_aDisplayTextProvider;
  }

  @Nonnull
  public Locale getContentLocale ()
  {
    return m_aContentLocale;
  }

  @Override
  protected String getPart (@Nonnull final DATATYPE aObject)
  {
    return m_aDisplayTextProvider.getDisplayText (aObject, m_aContentLocale);
  }
}
