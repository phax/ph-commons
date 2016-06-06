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
 * Default implementation of the {@link ITree} interface using
 * {@link DefaultTreeItem} tree items
 *
 * @author Philip Helger
 * @param <DATATYPE>
 *        tree item value type
 */
@NotThreadSafe
public class DefaultTree <DATATYPE> extends BasicTree <DATATYPE, DefaultTreeItem <DATATYPE>>
{
  public DefaultTree ()
  {
    this (new DefaultTreeItemFactory <DATATYPE> ());
  }

  public DefaultTree (@Nonnull final ITreeItemFactory <DATATYPE, DefaultTreeItem <DATATYPE>> aFactory)
  {
    super (aFactory);
  }
}
