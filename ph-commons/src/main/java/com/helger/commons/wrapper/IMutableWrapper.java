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
package com.helger.commons.wrapper;

import javax.annotation.Nullable;

import com.helger.commons.state.EChange;

/**
 * Base interface for mutable wrapping an object within another object.
 *
 * @author Philip Helger
 * @param <DATATYPE>
 *        The type of the wrapped object.
 */
public interface IMutableWrapper <DATATYPE> extends IWrapper <DATATYPE>
{
  /**
   * Change the wrapped object.
   *
   * @param aObj
   *        The new object to be wrapped. May be <code>null</code>.
   * @return {@link EChange}
   */
  @Nullable
  EChange set (@Nullable DATATYPE aObj);
}
