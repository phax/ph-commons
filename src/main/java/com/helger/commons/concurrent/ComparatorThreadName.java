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
package com.helger.commons.concurrent;

import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.compare.AbstractCollationComparator;
import com.helger.commons.compare.ESortOrder;

/**
 * {@link java.util.Comparator} for ordering {@link Thread} objects by their
 * name.
 * 
 * @author Philip Helger
 */
@NotThreadSafe
public final class ComparatorThreadName extends AbstractCollationComparator <Thread>
{
  public ComparatorThreadName (@Nullable final Locale aSortLocale)
  {
    super (aSortLocale);
  }

  public ComparatorThreadName (@Nullable final Locale aSortLocale, @Nonnull final ESortOrder eSortOrder)
  {
    super (aSortLocale, eSortOrder);
  }

  @Override
  protected String asString (@Nonnull final Thread aThread)
  {
    return aThread.getName ();
  }
}
