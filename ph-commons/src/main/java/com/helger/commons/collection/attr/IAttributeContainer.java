/**
 * Copyright (C) 2014-2017 Philip Helger (www.helger.com)
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
package com.helger.commons.collection.attr;

import javax.annotation.Nullable;

import com.helger.commons.CGlobal;
import com.helger.commons.collection.impl.ICommonsMap;
import com.helger.commons.traits.IGetterByKeyTrait;

/**
 * Base interface for a generic read-only attribute container. It maps keys to
 * values.<br>
 * Note: don't implement Iterable<Map.Entry<...>> because this would make the
 * object ambiguous to e.g. HashCodeGenerator
 *
 * @author Philip Helger
 * @param <KEYTYPE>
 *        Key type
 * @param <VALUETYPE>
 *        Value type
 */
public interface IAttributeContainer <KEYTYPE, VALUETYPE> extends
                                     ICommonsMap <KEYTYPE, VALUETYPE>,
                                     IGetterByKeyTrait <KEYTYPE>
{
  /**
   * Get the attribute value associated to the given attribute name.
   *
   * @param aName
   *        the attribute name
   * @return <code>null</code> if no such value exists
   */
  @Nullable
  default VALUETYPE getValue (@Nullable final KEYTYPE aName)
  {
    return get (aName);
  }

  /**
   * Get the attribute value associated to the given attribute name.<br>
   * Avoid throwing TypeConverterException by providing a default value.
   *
   * @param aName
   *        the attribute name
   * @return The attribute value or
   *         {@link com.helger.commons.CGlobal#ILLEGAL_UINT} if no such
   *         attribute exists
   */
  default int getAsInt (@Nullable final KEYTYPE aName)
  {
    return getAsInt (aName, CGlobal.ILLEGAL_UINT);
  }

  /**
   * Get the attribute value associated to the given attribute name.<br>
   * Avoid throwing TypeConverterException by providing a default value.
   *
   * @param aName
   *        the attribute name
   * @return The attribute value or
   *         {@link com.helger.commons.CGlobal#ILLEGAL_ULONG} if no such
   *         attribute exists
   */
  default long getAsLong (@Nullable final KEYTYPE aName)
  {
    return getAsLong (aName, CGlobal.ILLEGAL_ULONG);
  }

  /**
   * Get the attribute value associated to the given attribute name.<br>
   * Avoid throwing TypeConverterException by providing a default value.
   *
   * @param aName
   *        the attribute name
   * @return The attribute value or
   *         {@link com.helger.commons.CGlobal#ILLEGAL_UINT} if no such
   *         attribute exists
   */
  default float getAsFloat (@Nullable final KEYTYPE aName)
  {
    return getAsFloat (aName, CGlobal.ILLEGAL_UINT);
  }

  /**
   * Get the attribute value associated to the given attribute name.
   *
   * @param aName
   *        the attribute name
   * @return The attribute value or
   *         {@link com.helger.commons.CGlobal#ILLEGAL_UINT} if no such
   *         attribute exists
   */
  default double getAsDouble (@Nullable final KEYTYPE aName)
  {
    return getAsDouble (aName, CGlobal.ILLEGAL_UINT);
  }

  /**
   * Get the attribute value associated to the given attribute name.<br>
   * Avoid throwing TypeConverterException by providing a default value.
   *
   * @param aName
   *        the attribute name
   * @return The attribute value or <code>false</code> if no such attribute
   *         exists
   */
  default boolean getAsBoolean (@Nullable final KEYTYPE aName)
  {
    return getAsBoolean (aName, false);
  }
}
