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
package com.helger.commons.tree.sort;

import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.compare.PartComparatorComparable;
import com.helger.commons.tree.IBasicTreeItem;

/**
 * Comparator for sorting {@link IBasicTreeItem} items by their value using an
 * comparable value types.<br>
 * Works for {@link com.helger.commons.tree.ITreeItem} and
 * {@link com.helger.commons.tree.withid.ITreeItemWithID}.
 *
 * @author Philip Helger
 * @param <DATATYPE>
 *        tree item value type
 * @param <ITEMTYPE>
 *        tree item implementation type
 */
@NotThreadSafe
public class ComparatorTreeItemDataComparable <DATATYPE extends Comparable <? super DATATYPE>, ITEMTYPE extends IBasicTreeItem <DATATYPE, ITEMTYPE>>
                                              extends PartComparatorComparable <ITEMTYPE, DATATYPE>
{
  public ComparatorTreeItemDataComparable ()
  {
    super (aObject -> aObject.getData ());
  }
}
