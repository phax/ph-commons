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
 * Root class for a simple tree. The elements of the tree are not sorted by any
 * means.
 *
 * @param <KEYTYPE>
 *        The type of the key elements for the tree.
 * @param <DATATYPE>
 *        The type of the elements contained in the tree
 * @author Philip Helger
 */
@NotThreadSafe
public class DefaultTreeWithID <KEYTYPE, DATATYPE>
                               extends BasicTreeWithID <KEYTYPE, DATATYPE, DefaultTreeItemWithID <KEYTYPE, DATATYPE>>
{
  public DefaultTreeWithID ()
  {
    this (new DefaultTreeItemWithIDFactory <KEYTYPE, DATATYPE> ());
  }

  public DefaultTreeWithID (@Nonnull final ITreeItemWithIDFactory <KEYTYPE, DATATYPE, DefaultTreeItemWithID <KEYTYPE, DATATYPE>> aFactory)
  {
    super (aFactory);
  }
}
