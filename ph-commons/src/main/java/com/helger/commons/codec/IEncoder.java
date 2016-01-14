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
package com.helger.commons.codec;

import javax.annotation.Nullable;

/**
 * The most basic encoding interface
 *
 * @param <DATATYPE>
 *        data type
 * @author Philip Helger
 */
@FunctionalInterface
public interface IEncoder <DATATYPE>
{
  /**
   * Encode the passed source object
   *
   * @param aInput
   *        The source object to be encoded
   * @return The encoded value.
   * @throws EncodeException
   *         In case something goes wrong
   */
  @Nullable
  DATATYPE getEncoded (@Nullable DATATYPE aInput);
}
