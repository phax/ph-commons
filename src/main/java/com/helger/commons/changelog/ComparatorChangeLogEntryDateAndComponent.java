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
package com.helger.commons.changelog;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.compare.AbstractComparator;
import com.helger.commons.compare.CompareHelper;

/**
 * Special comparator to sort change log entries by their date and in case of
 * equality by the parent change logs component name.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class ComparatorChangeLogEntryDateAndComponent extends AbstractComparator <ChangeLogEntry>
{
  /**
   * Comparator with default sort order and no nested comparator.
   */
  public ComparatorChangeLogEntryDateAndComponent ()
  {}

  @Override
  protected int mainCompare (@Nonnull final ChangeLogEntry aElement1, @Nonnull final ChangeLogEntry aElement2)
  {
    int ret = CompareHelper.compare (aElement1.getDate ().getTime (), aElement2.getDate ().getTime ());
    if (ret == 0)
    {
      ret = aElement1.getChangeLog ().getComponent ().compareTo (aElement2.getChangeLog ().getComponent ());
    }
    return ret;
  }
}
