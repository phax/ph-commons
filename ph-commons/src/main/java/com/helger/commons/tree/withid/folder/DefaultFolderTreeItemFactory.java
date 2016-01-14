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
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.aggregate.IAggregator;

/**
 * The default folder tree item factory implementation.
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
public class DefaultFolderTreeItemFactory <KEYTYPE, DATATYPE, COLLTYPE extends Collection <DATATYPE>> extends
                                          AbstractFolderTreeItemFactory <KEYTYPE, DATATYPE, COLLTYPE, DefaultFolderTreeItem <KEYTYPE, DATATYPE, COLLTYPE>>
{
  public DefaultFolderTreeItemFactory (@Nullable final IAggregator <KEYTYPE, KEYTYPE> aKeyCombinator)
  {
    super (aKeyCombinator);
  }

  @Override
  protected final DefaultFolderTreeItem <KEYTYPE, DATATYPE, COLLTYPE> internalCreateRoot ()
  {
    return new DefaultFolderTreeItem <KEYTYPE, DATATYPE, COLLTYPE> (this);
  }

  @Override
  @Nonnull
  protected DefaultFolderTreeItem <KEYTYPE, DATATYPE, COLLTYPE> internalCreate (@Nonnull final DefaultFolderTreeItem <KEYTYPE, DATATYPE, COLLTYPE> aParent,
                                                                                @Nonnull final KEYTYPE aDataID)
  {
    return new DefaultFolderTreeItem <KEYTYPE, DATATYPE, COLLTYPE> (aParent, aDataID);
  }
}
