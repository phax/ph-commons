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
package com.helger.commons.tree;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

/**
 * Default implementation of the {@link ITreeItem} interface
 *
 * @author Philip Helger
 * @param <DATATYPE>
 *        tree item value type
 */
@NotThreadSafe
public class DefaultTreeItem <DATATYPE> extends BasicTreeItem <DATATYPE, DefaultTreeItem <DATATYPE>>
{
  /**
   * Constructor for root object.
   *
   * @param aFactory
   *        The factory to use for creating tree items. May not be
   *        <code>null</code>.
   */
  public DefaultTreeItem (@Nonnull final ITreeItemFactory <DATATYPE, DefaultTreeItem <DATATYPE>> aFactory)
  {
    super (aFactory);
  }

  /**
   * Constructor for normal elements.
   *
   * @param aParent
   *        Parent item to use. May never be <code>null</code> since only the
   *        root has no parent and for the root item a special no-argument
   *        constructor is present.
   */
  public DefaultTreeItem (@Nonnull final DefaultTreeItem <DATATYPE> aParent)
  {
    super (aParent);
  }
}
