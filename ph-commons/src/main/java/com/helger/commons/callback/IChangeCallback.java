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
package com.helger.commons.callback;

import javax.annotation.Nonnull;

import com.helger.commons.state.EContinue;

/**
 * Simple notification interface.
 *
 * @author Philip Helger
 * @param <DATATYPE>
 *        The type of object that is changed
 */
public interface IChangeCallback <DATATYPE> extends ICallback
{
  /**
   * Called before the object will be changed.
   *
   * @param aObjectToChange
   *        The object to be changed. Never <code>null</code>.
   * @return {@link EContinue#CONTINUE} if the action may be performed. If the
   *         return value is {@link EContinue#BREAK} the action will not be
   *         performed.
   */
  @Nonnull
  EContinue beforeChange (@Nonnull DATATYPE aObjectToChange);

  /**
   * Called after the object changed.
   *
   * @param aChangedObject
   *        The changed object. Never <code>null</code>.
   */
  void afterChange (@Nonnull DATATYPE aChangedObject);
}
