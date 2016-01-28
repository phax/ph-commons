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
package com.helger.commons.compare;

import java.text.Collator;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collation.CollatorHelper;
import com.helger.commons.string.ToStringGenerator;

/**
 * An implementation of a {@link java.util.Comparator} that compares
 * {@link String} objects with a {@link Collator}.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class CollatingComparator extends AbstractComparator <String>
{
  private final Collator m_aCollator;

  /**
   * Comparator with default sort order and specified sort locale.
   *
   * @param aSortLocale
   *        The locale to use. May be <code>null</code>.
   */
  public CollatingComparator (@Nullable final Locale aSortLocale)
  {
    m_aCollator = CollatorHelper.getCollatorSpaceBeforeDot (aSortLocale);
  }

  /**
   * Constructor with {@link Collator} using the default sort order
   *
   * @param aCollator
   *        The {@link Collator} to use. May not be <code>null</code>.
   */
  public CollatingComparator (@Nonnull final Collator aCollator)
  {
    ValueEnforcer.notNull (aCollator, "Collator");
    m_aCollator = (Collator) aCollator.clone ();
  }

  /**
   * @return A copy of the {@link Collator} as passed or created in the
   *         constructor. Never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  public final Collator getCollator ()
  {
    return (Collator) m_aCollator.clone ();
  }

  @Override
  protected final int mainCompare (@Nonnull final String sElement1, @Nonnull final String sElement2)
  {
    return m_aCollator.compare (sElement1, sElement2);
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ()).append ("collator", m_aCollator).toString ();
  }
}
