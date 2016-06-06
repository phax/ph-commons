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

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.tree.withid.unique.BasicTreeWithGlobalUniqueID;

/**
 * Abstract implementation class for a folder tree. The elements of the tree are
 * not sorted by any means.
 *
 * @param <KEYTYPE>
 *        The type of the element keys.
 * @param <DATATYPE>
 *        The type of the elements contained in the tree
 * @param <COLLTYPE>
 *        the collection type consisting of value elements
 * @param <ITEMTYPE>
 *        the implementation item type
 * @author Philip Helger
 */
@NotThreadSafe
public class BasicFolderTree <KEYTYPE, DATATYPE, COLLTYPE extends Collection <DATATYPE>, ITEMTYPE extends BasicFolderTreeItem <KEYTYPE, DATATYPE, COLLTYPE, ITEMTYPE>>
                             extends BasicTreeWithGlobalUniqueID <KEYTYPE, COLLTYPE, ITEMTYPE>
                             implements IFolderTree <KEYTYPE, DATATYPE, COLLTYPE, ITEMTYPE>
{
  /**
   * Constructor
   *
   * @param aFactory
   *        The item factory to use. May not be <code>null</code>.
   */
  public BasicFolderTree (@Nonnull final IFolderTreeItemFactory <KEYTYPE, DATATYPE, COLLTYPE, ITEMTYPE> aFactory)
  {
    super (aFactory);
  }
}
