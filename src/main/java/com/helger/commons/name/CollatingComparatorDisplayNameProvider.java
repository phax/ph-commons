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
package com.helger.commons.name;

import java.text.Collator;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.compare.AbstractCollatingComparator;

/**
 * This is a collation {@link java.util.Comparator} for objects that implement
 * the {@link IDisplayNameProvider} interface.
 *
 * @author Philip Helger
 * @param <DATATYPE>
 *        The type of elements to be compared.
 */
@NotThreadSafe
public class CollatingComparatorDisplayNameProvider <DATATYPE> extends AbstractCollatingComparator <DATATYPE>
{
  private final IDisplayNameProvider <DATATYPE> m_aDisplayNameProvider;

  /**
   * Constructor.
   *
   * @param aSortLocale
   *        The locale to be used for sorting. May be <code>null</code>.
   * @param aDisplayNameProvider
   *        The display text provider to be used. May not be <code>null</code>.
   */
  public CollatingComparatorDisplayNameProvider (@Nullable final Locale aSortLocale,
                                                 @Nonnull final IDisplayNameProvider <DATATYPE> aDisplayNameProvider)
  {
    super (aSortLocale);
    m_aDisplayNameProvider = ValueEnforcer.notNull (aDisplayNameProvider, "DisplayNameProvider");
  }

  /**
   * Constructor.
   *
   * @param aCollator
   *        The {@link Collator} to use. May not be <code>null</code>.
   * @param aDisplayNameProvider
   *        The display text provider to be used. May not be <code>null</code>.
   */
  public CollatingComparatorDisplayNameProvider (@Nonnull final Collator aCollator,
                                                 @Nonnull final IDisplayNameProvider <DATATYPE> aDisplayNameProvider)
  {
    super (aCollator);
    m_aDisplayNameProvider = ValueEnforcer.notNull (aDisplayNameProvider, "DisplayNameProvider");
  }

  @Nonnull
  public final IDisplayNameProvider <DATATYPE> getDisplayNameProvider ()
  {
    return m_aDisplayNameProvider;
  }

  @Override
  protected String getPart (@Nonnull final DATATYPE aObject)
  {
    return m_aDisplayNameProvider.getDisplayName (aObject);
  }
}
