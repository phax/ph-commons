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
package com.helger.commons.callback;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.Nonnull;
import com.helger.annotation.Nullable;
import com.helger.annotation.misc.ReturnsMutableCopy;
import com.helger.commons.collection.impl.ICommonsIterable;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.lang.IHasSize;

/**
 * Read-only interface for a list of {@link ICallback} objects.<br>
 * Note: Does not implement Iterable because the returned iterator would either
 * be an Iterator over the list in which case you can use
 * {@link #getAllCallbacks()} directly or the returned Iterator would not be
 * thread-safe and that is not an option for this type.
 *
 * @author Philip Helger
 * @param <CALLBACKTYPE>
 *        The callback type.
 */
public interface ICallbackList <CALLBACKTYPE extends ICallback> extends ICommonsIterable <CALLBACKTYPE>, IHasSize
{
  /**
   * @return A list of all callbacks. Never <code>null</code> and only
   *         containing non-<code>null</code> elements.
   */
  @Nonnull
  @ReturnsMutableCopy
  ICommonsList <CALLBACKTYPE> getAllCallbacks ();

  /**
   * Get the callback at the specified index.
   *
   * @param nIndex
   *        The index to be retrieved. Should be &ge; 0.
   * @return <code>null</code> if the provided index is invalid.
   */
  @Nullable
  CALLBACKTYPE getCallbackAtIndex (@Nonnegative int nIndex);
}
