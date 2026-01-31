/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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
package com.helger.typeconvert.collection;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.base.callback.ICallback;

/**
 * Callback interface to be invoked after a value was set.
 *
 * @author Philip Helger
 * @param <KEYTYPE>
 *        Key type
 * @param <VALUETYPE>
 *        Value type
 */
@FunctionalInterface
public interface IAfterSetValueCallback <KEYTYPE, VALUETYPE> extends ICallback
{
  /**
   * Method that is invoked after a value changed.
   *
   * @param aName
   *        The changed key. Neither <code>null</code> nor empty.
   * @param aOldValue
   *        The old value. May be <code>null</code>.
   * @param aNewValue
   *        The new value. May be <code>null</code> in which case the value was removed.
   */
  void afterSetValue (@NonNull KEYTYPE aName, @Nullable VALUETYPE aOldValue, @Nullable VALUETYPE aNewValue);
}
