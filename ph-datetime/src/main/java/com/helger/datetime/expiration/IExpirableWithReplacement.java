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
package com.helger.datetime.expiration;

import javax.annotation.Nullable;

/**
 * Read-only interface for objects that can expire but offer a replacement once
 * the object expires.
 *
 * @author Philip Helger
 * @param <DATATYPE>
 *        The type of the object use for defining a replacement.
 */
public interface IExpirableWithReplacement <DATATYPE> extends IExpirable
{
  /**
   * In case the object has an expiration date defined and is expired, the
   * object returned by this method directs to replacement object to be used
   * instead.
   *
   * @return <code>null</code> if no replacement is defined, a non-
   *         <code>null</code> object otherwise.
   */
  @Nullable
  DATATYPE getReplacement ();
}
