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
package com.helger.commons.tree.util.sort;

import java.util.Comparator;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.tree.withid.DefaultTreeItemWithID;

/**
 * Comparator for sorting {@link DefaultTreeItemWithID} items by their value
 * using an explicit {@link Comparator}.
 *
 * @author Philip Helger
 * @param <IDTYPE>
 *        tree item ID type
 * @param <DATATYPE>
 *        tree item value type
 */
@NotThreadSafe
public class ComparatorDefaultTreeItemWithIDData <IDTYPE, DATATYPE> extends ComparatorTreeItemData <DATATYPE, DefaultTreeItemWithID <IDTYPE, DATATYPE>>
{
  /**
   * Constructor with default sort order.
   *
   * @param aDataComparator
   *        Comparator for the data elements. May not be <code>null</code>.
   */
  public ComparatorDefaultTreeItemWithIDData (@Nonnull final Comparator <? super DATATYPE> aDataComparator)
  {
    super (aDataComparator);
  }
}
