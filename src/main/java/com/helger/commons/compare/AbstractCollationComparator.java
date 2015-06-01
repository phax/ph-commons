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

import com.helger.commons.annotations.ReturnsMutableCopy;

/**
 * An abstract implementation of a {@link java.util.Comparator} that uses
 * collations for ordering. This is only necessary when comparing strings.
 *
 * @author Philip Helger
 * @param <DATATYPE>
 *        the type of object to be compared
 */
@NotThreadSafe
public abstract class AbstractCollationComparator <DATATYPE> extends AbstractPartComparator <DATATYPE, String>
{
  /**
   * Comparator with default sort order and specified sort locale.
   *
   * @param aSortLocale
   *        The locale to use. May be <code>null</code>.
   */
  public AbstractCollationComparator (@Nullable final Locale aSortLocale)
  {
    super (new ComparatorStringWithCollator (aSortLocale));
  }

  /**
   * Constructor with {@link Collator} using the default sort order
   *
   * @param aCollator
   *        The {@link Collator} to use. May not be <code>null</code>.
   */
  public AbstractCollationComparator (@Nonnull final Collator aCollator)
  {
    super (new ComparatorStringWithCollator (aCollator));
  }

  /**
   * @return A copy of the {@link Collator} as passed or created in the
   *         constructor. Never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  public final Collator getCollator ()
  {
    return ((ComparatorStringWithCollator) getPartComparator ()).getCollator ();
  }
}
