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
package com.helger.typeconvert.collection;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.base.callback.ICallback;
import com.helger.base.state.EContinue;

/**
 * Callback interface to be invoked before a value is set.
 *
 * @author Philip Helger
 * @param <KEYTYPE>
 *        Key type
 * @param <VALUETYPE>
 *        Value type
 */
@FunctionalInterface
public interface IBeforeSetValueCallback <KEYTYPE, VALUETYPE> extends ICallback
{
  /**
   * Invoked before a new value is set
   *
   * @param aName
   *        The attribute key. May not be <code>null</code>.
   * @param aNewValue
   *        The new value to be set. May be <code>null</code>.
   * @return {@link EContinue#CONTINUE} if the value can be set, <code>false</code> if the value
   *         cannot be set.
   */
  @NonNull
  EContinue beforeSetValue (@NonNull KEYTYPE aName, @Nullable VALUETYPE aNewValue);
}
