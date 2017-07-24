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

import java.math.BigDecimal;
import java.math.BigInteger;

import javax.annotation.Nullable;

import com.helger.commons.CGlobal;
import com.helger.commons.collection.impl.ICommonsMap;
import com.helger.commons.traits.IGetterByKeyTrait;

/**
 * Base interface for a generic read-only attribute container. It maps keys to
 * values.
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
   * This is a shortcut for the following call:
   * <code>getAttributeAsString (aName, null)</code>
   *
   * @param aName
   *        the attribute name
   * @return The attribute value or <code>null</code> if no such attribute
   *         exists
   */
  @Nullable
  default String getAsString (@Nullable final KEYTYPE aName)
  {
    return getAsString (aName, null);
  }

  /**
   * Get the attribute value associated to the given attribute name or the
   * passed default value if no such attribute is present.
   *
   * @param aName
   *        the attribute name
   * @param sDefault
   *        The default value to be returned, if the passed attribute name is
   *        unknown
   * @return The attribute value or <code>sDefault</code> if no such attribute
   *         exists
   */
  @Nullable
  default String getAsString (@Nullable final KEYTYPE aName, @Nullable final String sDefault)
  {
    final Object aValue = getValue (aName);
    return AttributeValueConverter.getAsString (aName, aValue, sDefault);
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
  default int getAsInt (@Nullable final KEYTYPE aName)
  {
    return getAsInt (aName, CGlobal.ILLEGAL_UINT);
  }

  /**
   * Get the attribute value associated to the given attribute name.
   *
   * @param aName
   *        the attribute name
   * @param nDefault
   *        the default value to be returned if the value is not present or not
   *        convertible
   * @return The attribute value or <code>nDefault</code> if no such attribute
   *         exists
   */
  default int getAsInt (@Nullable final KEYTYPE aName, final int nDefault)
  {
    final Object aValue = getValue (aName);
    return AttributeValueConverter.getAsInt (aName, aValue, nDefault);
  }

  /**
   * Get the attribute value associated to the given attribute name.
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
   * Get the attribute value associated to the given attribute name.
   *
   * @param aName
   *        the attribute name
   * @param nDefault
   *        the default value to be returned if the value is not present or not
   *        convertible
   * @return The attribute value or <code>nDefault</code> if no such attribute
   *         exists
   */
  default long getAsLong (@Nullable final KEYTYPE aName, final long nDefault)
  {
    final Object aValue = getValue (aName);
    return AttributeValueConverter.getAsLong (aName, aValue, nDefault);
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
   * Get the attribute value associated to the given attribute name.
   *
   * @param aName
   *        the attribute name
   * @param dDefault
   *        the default value to be returned if the value is not present or not
   *        convertible
   * @return The attribute value or <code>nDefault</code> if no such attribute
   *         exists
   */
  default double getAsDouble (@Nullable final KEYTYPE aName, final double dDefault)
  {
    final Object aValue = getValue (aName);
    return AttributeValueConverter.getAsDouble (aName, aValue, dDefault);
  }

  /**
   * Get the attribute value associated to the given attribute name.
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

  /**
   * Get the attribute value associated to the given attribute name.
   *
   * @param aName
   *        the attribute name
   * @param bDefault
   *        the default value to be returned if the value is not present or not
   *        convertible
   * @return The attribute value or <code>bDefault</code> if no such attribute
   *         exists
   */
  default boolean getAsBoolean (@Nullable final KEYTYPE aName, final boolean bDefault)
  {
    final Object aValue = getValue (aName);
    return AttributeValueConverter.getAsBoolean (aName, aValue, bDefault);
  }

  /**
   * Get the attribute value associated to the given attribute name.
   *
   * @param aName
   *        the attribute name
   * @return The attribute value or <code>null</code> if no such attribute
   *         exists
   */
  @Nullable
  default BigInteger getAsBigInteger (@Nullable final KEYTYPE aName)
  {
    return getAsBigInteger (aName, null);
  }

  /**
   * Get the attribute value associated to the given attribute name.
   *
   * @param aName
   *        the attribute name
   * @param aDefault
   *        the default value to be returned if the value is not present or not
   *        convertible
   * @return The attribute value or <code>aDefault</code> if no such attribute
   *         exists
   */
  @Nullable
  default BigInteger getAsBigInteger (@Nullable final KEYTYPE aName, @Nullable final BigInteger aDefault)
  {
    final Object aValue = getValue (aName);
    return AttributeValueConverter.getAsBigInteger (aName, aValue, aDefault);
  }

  /**
   * Get the attribute value associated to the given attribute name.
   *
   * @param aName
   *        the attribute name
   * @return The attribute value or <code>null</code> if no such attribute
   *         exists
   */
  @Nullable
  default BigDecimal getAsBigDecimal (@Nullable final KEYTYPE aName)
  {
    return getAsBigDecimal (aName, null);
  }

  /**
   * Get the attribute value associated to the given attribute name.
   *
   * @param aName
   *        the attribute name
   * @param aDefault
   *        the default value to be returned if the value is not present or not
   *        convertible
   * @return The attribute value or <code>aDefault</code> if no such attribute
   *         exists
   */
  @Nullable
  default BigDecimal getAsBigDecimal (@Nullable final KEYTYPE aName, @Nullable final BigDecimal aDefault)
  {
    final Object aValue = getValue (aName);
    return AttributeValueConverter.getAsBigDecimal (aName, aValue, aDefault);
  }
}
