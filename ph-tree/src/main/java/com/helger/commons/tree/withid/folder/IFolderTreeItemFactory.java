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
package com.helger.commons.tree.withid.folder;

import java.util.Collection;

import javax.annotation.Nullable;

import com.helger.commons.aggregate.IAggregator;
import com.helger.commons.tree.withid.unique.ITreeItemWithUniqueIDFactory;

/**
 * A factory interface that creates tree items.
 *
 * @author Philip Helger
 * @param <KEYTYPE>
 *        Key type
 * @param <DATATYPE>
 *        Value type
 * @param <COLLTYPE>
 *        Collection type consisting of value elements
 * @param <ITEMTYPE>
 *        the implementation item type
 */
public interface IFolderTreeItemFactory <KEYTYPE, DATATYPE, COLLTYPE extends Collection <DATATYPE>, ITEMTYPE extends IFolderTreeItem <KEYTYPE, DATATYPE, COLLTYPE, ITEMTYPE>>
                                        extends ITreeItemWithUniqueIDFactory <KEYTYPE, COLLTYPE, ITEMTYPE>
{
  /**
   * @return The key combinator to be used to create global unique IDs.
   */
  @Nullable
  IAggregator <KEYTYPE, KEYTYPE> getKeyCombinator ();
}
