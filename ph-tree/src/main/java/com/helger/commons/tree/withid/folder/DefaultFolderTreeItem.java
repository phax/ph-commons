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

/**
 * Default implementation of the {@link IFolderTreeItem} interface.
 *
 * @author Philip Helger
 * @param <KEYTYPE>
 *        Key type
 * @param <DATATYPE>
 *        Value type
 * @param <COLLTYPE>
 *        Collection type consisting of value elements
 */
@NotThreadSafe
public class DefaultFolderTreeItem <KEYTYPE, DATATYPE, COLLTYPE extends Collection <DATATYPE>> extends
                                   BasicFolderTreeItem <KEYTYPE, DATATYPE, COLLTYPE, DefaultFolderTreeItem <KEYTYPE, DATATYPE, COLLTYPE>>
{
  /**
   * Constructor for root object
   *
   * @param aFactory
   *        The item factory to use.
   */
  public DefaultFolderTreeItem (@Nonnull final IFolderTreeItemFactory <KEYTYPE, DATATYPE, COLLTYPE, DefaultFolderTreeItem <KEYTYPE, DATATYPE, COLLTYPE>> aFactory)
  {
    super (aFactory);
  }

  /**
   * Constructor for normal elements
   *
   * @param aParent
   *        Parent item. May never be <code>null</code> since only the root has
   *        no parent.
   * @param aDataID
   *        The ID of the new item. May not be <code>null</code>.
   */
  public DefaultFolderTreeItem (@Nonnull final DefaultFolderTreeItem <KEYTYPE, DATATYPE, COLLTYPE> aParent,
                                @Nonnull final KEYTYPE aDataID)
  {
    super (aParent, aDataID);
  }
}
