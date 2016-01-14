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
package com.helger.commons.tree.withid;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

/**
 * Special implementation of {@link BasicTreeItemWithID} using the item type
 * {@link DefaultTreeItemWithID}.
 *
 * @author Philip Helger
 * @param <KEYTYPE>
 *        tree item key type
 * @param <DATATYPE>
 *        tree item value type
 */
@NotThreadSafe
public class DefaultTreeItemWithID <KEYTYPE, DATATYPE> extends
                                   BasicTreeItemWithID <KEYTYPE, DATATYPE, DefaultTreeItemWithID <KEYTYPE, DATATYPE>>
{
  /**
   * Constructor for root object
   *
   * @param aFactory
   *        The tree item factory to use. May not be <code>null</code>.
   */
  public DefaultTreeItemWithID (@Nonnull final ITreeItemWithIDFactory <KEYTYPE, DATATYPE, DefaultTreeItemWithID <KEYTYPE, DATATYPE>> aFactory)
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
  public DefaultTreeItemWithID (@Nonnull final DefaultTreeItemWithID <KEYTYPE, DATATYPE> aParent,
                                @Nonnull final KEYTYPE aDataID)
  {
    super (aParent, aDataID);
  }
}
