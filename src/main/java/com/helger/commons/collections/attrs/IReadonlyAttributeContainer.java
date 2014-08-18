/**
 * Copyright (C) 2006-2014 phloc systems (www.phloc.com)
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
import java.util.Collection;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotations.ReturnsMutableCopy;

/**
 * Base interface for a generic read-only attribute container. It maps strings
 * to any Java object.
 * 
 * @author Philip Helger
 */
public interface IReadonlyAttributeContainer extends Serializable
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
   * @param sName
   *        name of the attribute to check
   * @return <code>true</code> if the attribute is contained, <code>false</code>
   *         otherwise
   */
  boolean containsAttribute (@Nullable String sName);

  /**
   * @return The non-<code>null</code> map with all contained attributes.
   */
  @Nonnull
  @ReturnsMutableCopy
  Map <String, Object> getAllAttributes ();

  /**
   * Get the attribute value associated to the given attribute name.
   * 
   * @param sName
   *        the attribute name
   * @return <code>null</code> if no such value exists
   */
  @Nullable
  Object getAttributeObject (@Nullable String sName);

  /**
   * Get the attribute value associated to the given attribute name. If the type
   * of the attribute in the scope does not match, a
   * {@link java.lang.ClassCastException} is thrown! If you just want to
   * retrieve a String, use {@link #getAttributeAsString(String)}!<br>
   * This call is identical to the call
   * <code>getCastedAttribute(sName, null)</code>
   * 
   * @param <DATATYPE>
   *        return type
   * @param sName
   *        the attribute name
   * @return <code>null</code> if no such value exists
   */
  @Nullable
  <DATATYPE> DATATYPE getCastedAttribute (@Nullable String sName);

  /**
   * Get the attribute value associated to the given attribute name. If the type
   * of the attribute in the scope does not match, a
   * {@link java.lang.ClassCastException} is thrown! If you just want to
   * retrieve a String, use {@link #getAttributeAsString(String, String)}!
   * 
   * @param <DATATYPE>
   *        return type
   * @param sName
   *        The attribute name.
   * @param aDefault
   *        The default value to be returned if no such attribute exists
   * @return The passed default value if no such attribute exists
   */
  @Nullable
  <DATATYPE> DATATYPE getCastedAttribute (@Nullable String sName, @Nullable DATATYPE aDefault);

  /**
   * Get the attribute value associated to the given attribute name. If the type
   * of the attribute in the scope does not match, the TypeConverter is invoked!
   * If you just want to retrieve a String, use
   * {@link #getAttributeAsString(String)}!<br>
   * This call is identical to the call
   * <code>getTypedAttribute(sName, null)</code>
   * 
   * @param <DATATYPE>
   *        return type
   * @param sName
   *        the attribute name
   * @param aDstClass
   *        The destination class to convert to. May not be <code>null</code>.
   * @return <code>null</code> if no such value exists
   */
  @Nullable
  <DATATYPE> DATATYPE getTypedAttribute (@Nullable String sName, @Nonnull Class <DATATYPE> aDstClass);

  /**
   * Get the attribute value associated to the given attribute name. If the type
   * of the attribute in the scope does not match, the TypeConverter is invoked!
   * If you just want to retrieve a String, use
   * {@link #getAttributeAsString(String, String)}!<br>
   * 
   * @param <DATATYPE>
   *        return type
   * @param sName
   *        The attribute name.
   * @param aDstClass
   *        The destination class to convert to. May not be <code>null</code>.
   * @param aDefault
   *        The default value to be returned if no such attribute exists
   * @return The passed default value if no such attribute exists
   */
  @Nullable
  <DATATYPE> DATATYPE getTypedAttribute (@Nullable String sName,
                                         @Nonnull Class <DATATYPE> aDstClass,
                                         @Nullable DATATYPE aDefault);

  /**
   * Get the attribute value associated to the given attribute name.<br>
   * This is a shortcut for the following call:
   * <code>getAttributeAsString (sName, null)</code>
   * 
   * @param sName
   *        the attribute name
   * @return The attribute value or <code>null</code> if no such attribute
   *         exists
   */
  @Nullable
  String getAttributeAsString (@Nullable String sName);

  /**
   * Get the attribute value associated to the given attribute name or the
   * passed default value if no such attribute is present.
   * 
   * @param sName
   *        the attribute name
   * @param sDefault
   *        The default value to be returned, if the passed attribute name is
   *        unknown
   * @return The attribute value or <code>sDefault</code> if no such attribute
   *         exists
   */
  @Nullable
  String getAttributeAsString (@Nullable String sName, @Nullable String sDefault);

  /**
   * Get the attribute value associated to the given attribute name.
   * 
   * @param sName
   *        the attribute name
   * @return The attribute value or
   *         {@link com.helger.commons.CGlobal#ILLEGAL_UINT} if no such attribute
   *         exists
   */
  int getAttributeAsInt (@Nullable String sName);

  /**
   * Get the attribute value associated to the given attribute name.
   * 
   * @param sName
   *        the attribute name
   * @param nDefault
   *        the default value to be returned if the value is not present
   * @return The attribute value or <code>nDefault</code> if no such attribute
   *         exists
   */
  int getAttributeAsInt (@Nullable String sName, int nDefault);

  /**
   * Get the attribute value associated to the given attribute name.
   * 
   * @param sName
   *        the attribute name
   * @return The attribute value or
   *         {@link com.helger.commons.CGlobal#ILLEGAL_ULONG} if no such
   *         attribute exists
   */
  long getAttributeAsLong (@Nullable String sName);

  /**
   * Get the attribute value associated to the given attribute name.
   * 
   * @param sName
   *        the attribute name
   * @param nDefault
   *        the default value to be returned if the value is not present
   * @return The attribute value or <code>nDefault</code> if no such attribute
   *         exists
   */
  long getAttributeAsLong (@Nullable String sName, long nDefault);

  /**
   * Get the attribute value associated to the given attribute name.
   * 
   * @param sName
   *        the attribute name
   * @return The attribute value or
   *         {@link com.helger.commons.CGlobal#ILLEGAL_UINT} if no such attribute
   *         exists
   */
  double getAttributeAsDouble (@Nullable String sName);

  /**
   * Get the attribute value associated to the given attribute name.
   * 
   * @param sName
   *        the attribute name
   * @param dDefault
   *        the default value to be returned if the value is not present
   * @return The attribute value or <code>nDefault</code> if no such attribute
   *         exists
   */
  double getAttributeAsDouble (@Nullable String sName, double dDefault);

  /**
   * Get the attribute value associated to the given attribute name.
   * 
   * @param sName
   *        the attribute name
   * @return The attribute value or <code>false</code> if no such attribute
   *         exists
   */
  boolean getAttributeAsBoolean (@Nullable String sName);

  /**
   * Get the attribute value associated to the given attribute name.
   * 
   * @param sName
   *        the attribute name
   * @param bDefault
   *        the default value to be returned if the value is not present
   * @return The attribute value or <code>bDefault</code> if no such attribute
   *         exists
   */
  boolean getAttributeAsBoolean (@Nullable String sName, boolean bDefault);

  /**
   * Get an enumerator over all contained attribute names.
   * 
   * @return an Enumerator over all attribute names
   */
  @Nonnull
  Enumeration <String> getAttributeNames ();

  /**
   * @return A non-null set of all attribute names.
   */
  @Nonnull
  Set <String> getAllAttributeNames ();

  /**
   * @return A non-null collection of all attribute values.
   */
  @Nonnull
  Collection <Object> getAllAttributeValues ();
}
