/**
 * Copyright (C) 2014 Philip Helger (www.helger.com)
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
package com.helger.commons.collections.attrs;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotations.ReturnsMutableCopy;

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
public interface IGenericReadonlyAttributeContainer <KEYTYPE, VALUETYPE> extends Serializable
{
  /**
   * @return The number of contained attributes. Always &ge; 0.
   */
  @Nonnegative
  int getAttributeCount ();

  /**
   * @return <code>true</code> if this attribute container does not contain any
   *         attribute at all, <code>false</code> if at least one attribute is
   *         contained.
   */
  boolean containsNoAttribute ();

  /**
   * Check if an attribute of the given name is contained.
   *
   * @param aName
   *        name of the attribute to check
   * @return <code>true</code> if the attribute is contained, <code>false</code>
   *         otherwise
   */
  boolean containsAttribute (@Nullable KEYTYPE aName);

  /**
   * @return The non-<code>null</code> map with all contained attributes.
   */
  @Nonnull
  @ReturnsMutableCopy
  Map <KEYTYPE, VALUETYPE> getAllAttributes ();

  /**
   * Get the attribute value associated to the given attribute name.
   *
   * @param aName
   *        the attribute name
   * @return <code>null</code> if no such value exists
   */
  @Nullable
  VALUETYPE getAttributeObject (@Nullable KEYTYPE aName);

  /**
   * Get the attribute value associated to the given attribute name. If the type
   * of the attribute in the scope does not match, a
   * {@link java.lang.ClassCastException} is thrown! If you just want to
   * retrieve a String, use {@link #getAttributeAsString(Object)}!<br>
   * This call is identical to the call
   * <code>getCastedAttribute(aName, null)</code>
   *
   * @param <DATATYPE>
   *        return type
   * @param aName
   *        the attribute name
   * @return <code>null</code> if no such value exists
   */
  @Nullable
  <DATATYPE> DATATYPE getCastedAttribute (@Nullable KEYTYPE aName);

  /**
   * Get the attribute value associated to the given attribute name. If the type
   * of the attribute in the scope does not match, a
   * {@link java.lang.ClassCastException} is thrown! If you just want to
   * retrieve a String, use {@link #getAttributeAsString(Object, String)}!
   *
   * @param <DATATYPE>
   *        return type
   * @param aName
   *        The attribute name.
   * @param aDefault
   *        The default value to be returned if no such attribute exists
   * @return The passed default value if no such attribute exists
   */
  @Nullable
  <DATATYPE> DATATYPE getCastedAttribute (@Nullable KEYTYPE aName, @Nullable DATATYPE aDefault);

  /**
   * Get the attribute value associated to the given attribute name. If the type
   * of the attribute in the scope does not match, the TypeConverter is invoked!
   * If you just want to retrieve a String, use
   * {@link #getAttributeAsString(Object)}!<br>
   * This call is identical to the call
   * <code>getTypedAttribute(aName, null)</code>
   *
   * @param <DATATYPE>
   *        return type
   * @param aName
   *        the attribute name
   * @param aDstClass
   *        The destination class to convert to. May not be <code>null</code>.
   * @return <code>null</code> if no such value exists
   */
  @Nullable
  <DATATYPE> DATATYPE getTypedAttribute (@Nullable KEYTYPE aName, @Nonnull Class <DATATYPE> aDstClass);

  /**
   * Get the attribute value associated to the given attribute name. If the type
   * of the attribute in the scope does not match, the TypeConverter is invoked!
   * If you just want to retrieve a String, use
   * {@link #getAttributeAsString(Object, String)}!<br>
   *
   * @param <DATATYPE>
   *        return type
   * @param aName
   *        The attribute name.
   * @param aDstClass
   *        The destination class to convert to. May not be <code>null</code>.
   * @param aDefault
   *        The default value to be returned if no such attribute exists
   * @return The passed default value if no such attribute exists
   */
  @Nullable
  <DATATYPE> DATATYPE getTypedAttribute (@Nullable KEYTYPE aName,
                                         @Nonnull Class <DATATYPE> aDstClass,
                                         @Nullable DATATYPE aDefault);

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
  String getAttributeAsString (@Nullable KEYTYPE aName);

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
  String getAttributeAsString (@Nullable KEYTYPE aName, @Nullable String sDefault);

  /**
   * Get the attribute value associated to the given attribute name.
   *
   * @param aName
   *        the attribute name
   * @return The attribute value or
   *         {@link com.helger.commons.CGlobal#ILLEGAL_UINT} if no such
   *         attribute exists
   */
  int getAttributeAsInt (@Nullable KEYTYPE aName);

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
  int getAttributeAsInt (@Nullable KEYTYPE aName, int nDefault);

  /**
   * Get the attribute value associated to the given attribute name.
   *
   * @param aName
   *        the attribute name
   * @return The attribute value or
   *         {@link com.helger.commons.CGlobal#ILLEGAL_ULONG} if no such
   *         attribute exists
   */
  long getAttributeAsLong (@Nullable KEYTYPE aName);

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
  long getAttributeAsLong (@Nullable KEYTYPE aName, long nDefault);

  /**
   * Get the attribute value associated to the given attribute name.
   *
   * @param aName
   *        the attribute name
   * @return The attribute value or
   *         {@link com.helger.commons.CGlobal#ILLEGAL_UINT} if no such
   *         attribute exists
   */
  double getAttributeAsDouble (@Nullable KEYTYPE aName);

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
  double getAttributeAsDouble (@Nullable KEYTYPE aName, double dDefault);

  /**
   * Get the attribute value associated to the given attribute name.
   *
   * @param aName
   *        the attribute name
   * @return The attribute value or <code>false</code> if no such attribute
   *         exists
   */
  boolean getAttributeAsBoolean (@Nullable KEYTYPE aName);

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
  boolean getAttributeAsBoolean (@Nullable KEYTYPE aName, boolean bDefault);

  /**
   * Get the attribute value associated to the given attribute name.
   *
   * @param aName
   *        the attribute name
   * @return The attribute value or <code>null</code> if no such attribute
   *         exists
   */
  @Nullable
  BigInteger getAttributeAsBigInteger (@Nullable KEYTYPE aName);

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
  BigInteger getAttributeAsBigInteger (@Nullable KEYTYPE aName, @Nullable BigInteger aDefault);

  /**
   * Get the attribute value associated to the given attribute name.
   *
   * @param aName
   *        the attribute name
   * @return The attribute value or <code>null</code> if no such attribute
   *         exists
   */
  @Nullable
  BigDecimal getAttributeAsBigDecimal (@Nullable KEYTYPE aName);

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
  BigDecimal getAttributeAsBigDecimal (@Nullable KEYTYPE aName, @Nullable BigDecimal aDefault);

  /**
   * Get an enumerator over all contained attribute names.
   *
   * @return an Enumerator over all attribute names
   */
  @Nonnull
  Enumeration <KEYTYPE> getAttributeNames ();

  /**
   * @return A non-null set of all attribute names.
   */
  @Nonnull
  Set <KEYTYPE> getAllAttributeNames ();

  /**
   * @return A non-null collection of all attribute values.
   */
  @Nonnull
  Collection <VALUETYPE> getAllAttributeValues ();
}
