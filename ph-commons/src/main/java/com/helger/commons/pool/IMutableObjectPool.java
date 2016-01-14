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
package com.helger.commons.pool;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.state.ESuccess;

/**
 * Simple pool interface.
 *
 * @author Philip Helger
 * @param <DATATYPE>
 *        The type of object to be pooled.
 */
public interface IMutableObjectPool <DATATYPE>
{
  /**
   * Borrow an object from the pool. This method blocks until an object is
   * available.
   *
   * @return The borrowed object. May be <code>null</code> depending on the
   *         factory, and the locking used.
   */
  @Nullable
  DATATYPE borrowObject ();

  /**
   * Return a previously borrowed object back to the pool.
   *
   * @param aItem
   *        The previously borrowed object to be returned. Never
   *        <code>null</code>.
   * @return {@link ESuccess#SUCCESS} upon success
   */
  @Nonnull
  ESuccess returnObject (@Nonnull DATATYPE aItem);
}
