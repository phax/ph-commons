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
package com.helger.commons.tree.withid.unique;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.tree.withid.DefaultTreeItemWithID;

/**
 * A special tree item factory that maintains a unique ID over all items!
 *
 * @author Philip Helger
 * @param <KEYTYPE>
 *        The key type.
 * @param <DATATYPE>
 *        The value type to be contained in tree items.
 */
@NotThreadSafe
public class DefaultTreeItemWithUniqueIDFactory <KEYTYPE, DATATYPE> extends
                                                AbstractTreeItemWithUniqueIDFactory <KEYTYPE, DATATYPE, DefaultTreeItemWithID <KEYTYPE, DATATYPE>>
{
  @Override
  @Nonnull
  protected DefaultTreeItemWithID <KEYTYPE, DATATYPE> internalCreate (@Nonnull final DefaultTreeItemWithID <KEYTYPE, DATATYPE> aParent,
                                                                      @Nonnull final KEYTYPE aDataID)
  {
    return new DefaultTreeItemWithID <KEYTYPE, DATATYPE> (aParent, aDataID);
  }

  @Nonnull
  public DefaultTreeItemWithID <KEYTYPE, DATATYPE> createRoot ()
  {
    return new DefaultTreeItemWithID <KEYTYPE, DATATYPE> (this);
  }
}
