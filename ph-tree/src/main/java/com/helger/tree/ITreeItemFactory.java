/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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
package com.helger.tree;

import javax.annotation.Nonnull;

/**
 * Interface for a simple tree item factory
 *
 * @author Philip Helger
 * @param <DATATYPE>
 *        item value type
 * @param <ITEMTYPE>
 *        item implementation type
 */
public interface ITreeItemFactory <DATATYPE, ITEMTYPE extends ITreeItem <DATATYPE, ITEMTYPE>>
{
  /**
   * Create a root entry.
   *
   * @return New root entry. May not be <code>null</code>.
   */
  @Nonnull
  ITEMTYPE createRoot ();

  /**
   * Create a child entry.
   *
   * @param aParent
   *        The parent entry to use. May not be <code>null</code>.
   * @return The created non-root entry. May not be <code>null</code>.
   */
  @Nonnull
  ITEMTYPE create (@Nonnull ITEMTYPE aParent);
}
