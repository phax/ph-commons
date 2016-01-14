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

import com.helger.commons.string.StringHelper;

/**
 * A simple collation aware comparator that compares objects by their "toString"
 * representation.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class CollatingComparatorToString extends CollatingPartComparator <Object>
{
  /**
   * Comparator with the specified sort locale.
   *
   * @param aSortLocale
   *        The locale to use. May be <code>null</code>.
   */
  public CollatingComparatorToString (@Nullable final Locale aSortLocale)
  {
    this (new CollatingComparator (aSortLocale));
  }

  /**
   * Constructor with {@link Collator}
   *
   * @param aCollator
   *        The {@link Collator} to use. May not be <code>null</code>.
   */
  public CollatingComparatorToString (@Nonnull final Collator aCollator)
  {
    this (new CollatingComparator (aCollator));
  }

  /**
   * Constructor with {@link CollatingComparator}
   *
   * @param aComparator
   *        The {@link CollatingComparator} to use. May not be <code>null</code>
   *        .
   */
  public CollatingComparatorToString (@Nonnull final CollatingComparator aComparator)
  {
    super (aComparator, aObject -> StringHelper.getToString (aObject));
  }
}
