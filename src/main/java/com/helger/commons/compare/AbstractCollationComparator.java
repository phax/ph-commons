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
package com.helger.commons.compare;

import java.text.Collator;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotations.ReturnsMutableCopy;
import com.helger.commons.string.ToStringGenerator;

/**
 * An abstract implementation of a {@link java.util.Comparator} that uses
 * collations for ordering. This is only necessary when comparing strings.
 *
 * @author Philip Helger
 * @param <DATATYPE>
 *        the type of object to be compared
 */
@NotThreadSafe
public abstract class AbstractCollationComparator <DATATYPE> extends AbstractComparator <DATATYPE>
{
  public static final boolean DEFAULT_CASE_INSENSITIVE = false;

  private final Collator m_aCollator;

  /**
   * Comparator with default sort order and specified sort locale.
   *
   * @param aSortLocale
   *        The locale to use. May be <code>null</code>.
   */
  public AbstractCollationComparator (@Nullable final Locale aSortLocale)
  {
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
    ValueEnforcer.notNull (aCollator, "Collator");
    m_aCollator = (Collator) aCollator.clone ();
  }

  @Nonnull
  @ReturnsMutableCopy
  public final Collator getCollator ()
  {
    return (Collator) m_aCollator.clone ();
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
  protected abstract String getAsString (@Nonnull DATATYPE aObject);

  @Nullable
  private String _nullSafeGetAsString (@Nullable final DATATYPE aObject)
  {
    return aObject == null ? null : getAsString (aObject);
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
    return ToStringGenerator.getDerived (super.toString ()).append ("collator", m_aCollator).toString ();
  }
}
