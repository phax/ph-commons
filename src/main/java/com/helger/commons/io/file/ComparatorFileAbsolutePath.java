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
package com.helger.commons.io.file;

import java.io.File;
import java.util.Comparator;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.compare.AbstractCollationComparator;
import com.helger.commons.compare.ESortOrder;

/**
 * Sort files by their absolute path name.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public final class ComparatorFileAbsolutePath extends AbstractCollationComparator <File>
{
  public ComparatorFileAbsolutePath ()
  {}

  public ComparatorFileAbsolutePath (@Nullable final Locale aSortLocale)
  {
    super (aSortLocale);
  }

  public ComparatorFileAbsolutePath (@Nonnull final ESortOrder eSortOrder)
  {
    super (eSortOrder);
  }

  public ComparatorFileAbsolutePath (@Nullable final Comparator <? super File> aNestedComparator)
  {
    super (aNestedComparator);
  }

  public ComparatorFileAbsolutePath (@Nonnull final ESortOrder eSortOrder,
                                     @Nullable final Comparator <? super File> aNestedComparator)
  {
    super (eSortOrder, aNestedComparator);
  }

  public ComparatorFileAbsolutePath (@Nullable final Locale aSortLocale,
                                     @Nullable final Comparator <? super File> aNestedComparator)
  {
    super (aSortLocale, aNestedComparator);
  }

  public ComparatorFileAbsolutePath (@Nullable final Locale aSortLocale,
                                     @Nonnull final ESortOrder eSortOrder,
                                     @Nullable final Comparator <? super File> aNestedComparator)
  {
    super (aSortLocale, eSortOrder, aNestedComparator);
  }

  @Override
  protected String asString (@Nonnull final File aObject)
  {
    return aObject.getAbsolutePath ();
  }
}
