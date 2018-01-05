/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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

import java.io.Serializable;

import javax.annotation.Nullable;

/**
 * Base interface for wrapping an object within another object.
 *
 * @author Philip Helger
 * @param <DATATYPE>
 *        The type of the wrapped object.
 */
@FunctionalInterface
public interface IWrapper <DATATYPE> extends Serializable
{
  /**
   * @return The currently wrapped object. May be <code>null</code>.
   */
  @Nullable
  DATATYPE get ();

  /**
   * @return <code>true</code> if the contained value is not <code>null</code>,
   *         <code>false</code> if it is <code>null</code>.
   */
  default boolean isSet ()
  {
    return get () != null;
  }

  /**
   * @return <code>true</code> if the contained value is <code>null</code>,
   *         <code>false</code> if it is not <code>null</code>.
   */
  default boolean isNotSet ()
  {
    return get () == null;
  }
}
