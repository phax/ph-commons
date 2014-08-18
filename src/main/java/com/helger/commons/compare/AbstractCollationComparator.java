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
package com.helger.commons.compare;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.string.ToStringGenerator;

/**
 * An abstract implementation of a {@link java.util.Comparator} that uses
 * collations for ordering. This is only necessary when comparing strings.
 * 
 * @author Philip Helger
 * @param <DATATYPE>
 *        the type of object to be compared
 */
public abstract class AbstractCollationComparator <DATATYPE> extends AbstractComparator <DATATYPE>
{
  private final Collator m_aCollator;

  /**
   * Comparator with default locale {@link Collator} and default sort order.
   */
  public AbstractCollationComparator ()
  {
    this ((Locale) null, ESortOrder.DEFAULT, (Comparator <? super DATATYPE>) null);
  }

  /**
   * Comparator with default locale {@link Collator} and default sort order and
   * a nested comparator.
   * 
   * @param aNestedComparator
   *        The nested comparator to be invoked, when the main comparison
   *        resulted in 0.
   */
  public AbstractCollationComparator (@Nullable final Comparator <? super DATATYPE> aNestedComparator)
  {
    this ((Locale) null, ESortOrder.DEFAULT, aNestedComparator);
  }

  /**
   * Comparator with default locale {@link Collator}.
   * 
   * @param eSortOrder
   *        The sort order to use. May not be <code>null</code>.
   */
  public AbstractCollationComparator (@Nonnull final ESortOrder eSortOrder)
  {
    this ((Locale) null, eSortOrder, (Comparator <? super DATATYPE>) null);
  }

  /**
   * Comparator with default locale {@link Collator} and a nested comparator.
   * 
   * @param eSortOrder
   *        The sort order to use. May not be <code>null</code>.
   * @param aNestedComparator
   *        The nested comparator to be invoked, when the main comparison
   *        resulted in 0.
   */
  public AbstractCollationComparator (@Nonnull final ESortOrder eSortOrder,
                                      @Nullable final Comparator <? super DATATYPE> aNestedComparator)
  {
    this ((Locale) null, eSortOrder, aNestedComparator);
  }

  /**
   * Comparator with default sort order and specified sort locale.
   * 
   * @param aSortLocale
   *        The locale to use. May be <code>null</code>.
   */
  public AbstractCollationComparator (@Nullable final Locale aSortLocale)
  {
    this (aSortLocale, ESortOrder.DEFAULT, (Comparator <? super DATATYPE>) null);
  }

  /**
   * Comparator with default sort order but special locale and a nested
   * comparator.
   * 
   * @param aSortLocale
   *        The locale to use. May be <code>null</code>.
   * @param aNestedComparator
   *        The nested comparator to be invoked, when the main comparison
   *        resulted in 0.
   */
  public AbstractCollationComparator (@Nullable final Locale aSortLocale,
                                      @Nullable final Comparator <? super DATATYPE> aNestedComparator)
  {
    this (aSortLocale, ESortOrder.DEFAULT, aNestedComparator);
  }

  /**
   * Constructor with locale and sort order.
   * 
   * @param aSortLocale
   *        The locale to use. May be <code>null</code>.
   * @param eSortOrder
   *        The sort order to use. May not be <code>null</code>.
   */
  public AbstractCollationComparator (@Nullable final Locale aSortLocale, @Nonnull final ESortOrder eSortOrder)
  {
    this (aSortLocale, eSortOrder, (Comparator <? super DATATYPE>) null);
  }

  /**
   * Constructor with locale and sort order and a nested comparator.
   * 
   * @param aSortLocale
   *        The locale to use. May be <code>null</code>.
   * @param eSortOrder
   *        The sort order to use. May not be <code>null</code>.
   * @param aNestedComparator
   *        The nested comparator to be invoked, when the main comparison
   *        resulted in 0.
   */
  public AbstractCollationComparator (@Nullable final Locale aSortLocale,
                                      @Nonnull final ESortOrder eSortOrder,
                                      @Nullable final Comparator <? super DATATYPE> aNestedComparator)
  {
    super (eSortOrder, aNestedComparator);
    m_aCollator = CollatorUtils.getCollatorSpaceBeforeDot (aSortLocale);
  }

  /**
   * Constructor with {@link Collator} using the default sort order
   * 
   * @param aCollator
   *        The {@link Collator} to use. May not be <code>null</code>.
   */
  public AbstractCollationComparator (@Nonnull final Collator aCollator)
  {
    this (aCollator, ESortOrder.DEFAULT, (Comparator <? super DATATYPE>) null);
  }

  /**
   * Constructor with {@link Collator} using the default sort order and a nested
   * comparator.
   * 
   * @param aCollator
   *        The {@link Collator} to use. May not be <code>null</code>.
   * @param aNestedComparator
   *        The nested comparator to be invoked, when the main comparison
   *        resulted in 0.
   */
  public AbstractCollationComparator (@Nonnull final Collator aCollator,
                                      @Nullable final Comparator <? super DATATYPE> aNestedComparator)
  {
    this (aCollator, ESortOrder.DEFAULT, aNestedComparator);
  }

  /**
   * Constructor with {@link Collator} and sort order.
   * 
   * @param aCollator
   *        The {@link Collator} to use. May not be <code>null</code>.
   * @param eSortOrder
   *        The sort order to use. May not be <code>null</code>.
   */
  public AbstractCollationComparator (@Nonnull final Collator aCollator, @Nonnull final ESortOrder eSortOrder)
  {
    this (aCollator, eSortOrder, (Comparator <? super DATATYPE>) null);
  }

  /**
   * Constructor with {@link Collator} and sort order and a nested comparator.
   * 
   * @param aCollator
   *        The {@link Collator} to use. May not be <code>null</code>.
   * @param eSortOrder
   *        The sort order to use. May not be <code>null</code>.
   * @param aNestedComparator
   *        The nested comparator to be invoked, when the main comparison
   *        resulted in 0.
   */
  public AbstractCollationComparator (@Nonnull final Collator aCollator,
                                      @Nonnull final ESortOrder eSortOrder,
                                      @Nullable final Comparator <? super DATATYPE> aNestedComparator)
  {
    super (eSortOrder, aNestedComparator);
    ValueEnforcer.notNull (aCollator, "Collator");
    m_aCollator = (Collator) aCollator.clone ();
  }

  /**
   * Abstract method that needs to be overridden to convert an object to a
   * string representation for comparison.
   * 
   * @param aObject
   *        The object to be converted. May not be <code>null</code> depending
   *        on the elements to be sorted.
   * @return The string representation of the object. May be <code>null</code>.
   */
  @Nullable
  protected abstract String asString (@Nonnull DATATYPE aObject);

  @Nullable
  private String _nullSafeGetAsString (@Nullable final DATATYPE aObject)
  {
    return aObject == null ? null : asString (aObject);
  }

  @Override
  protected final int mainCompare (@Nullable final DATATYPE aElement1, @Nullable final DATATYPE aElement2)
  {
    final String s1 = _nullSafeGetAsString (aElement1);
    final String s2 = _nullSafeGetAsString (aElement2);
    return CompareUtils.nullSafeCompare (s1, s2, m_aCollator);
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("sortOrder", getSortOrder ())
                                       .append ("collator", m_aCollator)
                                       .toString ();
  }
}
