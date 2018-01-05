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
package com.helger.commons.traits;

/**
 * A generic dummy interface to convert any primitive type to a certain object
 * type.
 *
 * @author Philip Helger
 * @param <DSTTYPE>
 *        The destination type to convert to
 */
public interface IPrimitiveConverterTo <DSTTYPE>
{
  /**
   * Convert from boolean to DSTTYPE
   *
   * @param value
   *        Source value
   * @return Converted destination type
   */
  DSTTYPE convert (boolean value);

  /**
   * Convert from byte to DSTTYPE
   *
   * @param value
   *        Source value
   * @return Converted destination type
   */
  DSTTYPE convert (byte value);

  /**
   * Convert from char to DSTTYPE
   *
   * @param value
   *        Source value
   * @return Converted destination type
   */
  DSTTYPE convert (char value);

  /**
   * Convert from double to DSTTYPE
   *
   * @param value
   *        Source value
   * @return Converted destination type
   */
  DSTTYPE convert (double value);

  /**
   * Convert from float to DSTTYPE
   *
   * @param value
   *        Source value
   * @return Converted destination type
   */
  DSTTYPE convert (float value);

  /**
   * Convert from int to DSTTYPE
   *
   * @param value
   *        Source value
   * @return Converted destination type
   */
  DSTTYPE convert (int value);

  /**
   * Convert from long to DSTTYPE
   *
   * @param value
   *        Source value
   * @return Converted destination type
   */
  DSTTYPE convert (long value);

  /**
   * Convert from short to DSTTYPE
   *
   * @param value
   *        Source value
   * @return Converted destination type
   */
  DSTTYPE convert (short value);

  /**
   * Convert from Object to DSTTYPE
   *
   * @param value
   *        Source value
   * @return Converted destination type
   */
  DSTTYPE convert (Object value);
}
