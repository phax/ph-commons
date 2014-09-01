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

import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.state.EChange;
import com.helger.commons.state.IClearable;

/**
 * This is the writable extension of the {@link IReadonlyAttributeContainer}.
 * <code>null</code> values are not allowed in this attribute containers.
 * 
 * @author Philip Helger
 */
public interface IAttributeContainer extends IReadonlyAttributeContainer, IClearable
{
  /**
   * Set/overwrite an attribute value.
   * 
   * @param sName
   *        The name of the attribute. May not be <code>null</code>.
   * @param aValue
   *        The value of the attribute. If it is <code>null</code>, the value
   *        will be removed.
   * @return {@link EChange#CHANGED} if something changed,
   *         {@link EChange#UNCHANGED} otherwise.
   * @see #removeAttribute(String)
   */
  @Nonnull
  EChange setAttribute (@Nonnull String sName, @Nullable Object aValue);

  /**
   * Set/overwrite an in attribute value. This is a shortcut for
   * <code>setAttribute (sName, Boolean.valueOf (bValue));</code>
   * 
   * @param sName
   *        The name of the attribute. May not be <code>null</code>.
   * @param bValue
   *        The value of the attribute.
   * @return {@link EChange#CHANGED} if something changed,
   *         {@link EChange#UNCHANGED} otherwise.
   * @see #removeAttribute(String)
   */
  @Nonnull
  EChange setAttribute (@Nonnull String sName, boolean bValue);

  /**
   * Set/overwrite an in attribute value. This is a shortcut for
   * <code>setAttribute (sName, Integer.valueOf (nValue));</code>
   * 
   * @param sName
   *        The name of the attribute. May not be <code>null</code>.
   * @param nValue
   *        The value of the attribute.
   * @return {@link EChange#CHANGED} if something changed,
   *         {@link EChange#UNCHANGED} otherwise.
   * @see #removeAttribute(String)
   */
  @Nonnull
  EChange setAttribute (@Nonnull String sName, int nValue);

  /**
   * Set/overwrite an in attribute value. This is a shortcut for
   * <code>setAttribute (sName, Long.valueOf (nValue));</code>
   * 
   * @param sName
   *        The name of the attribute. May not be <code>null</code>.
   * @param nValue
   *        The value of the attribute.
   * @return {@link EChange#CHANGED} if something changed,
   *         {@link EChange#UNCHANGED} otherwise.
   * @see #removeAttribute(String)
   */
  @Nonnull
  EChange setAttribute (@Nonnull String sName, long nValue);

  /**
   * Set/overwrite an in attribute value. This is a shortcut for
   * <code>setAttribute (sName, Double.valueOf (nValue));</code>
   * 
   * @param sName
   *        The name of the attribute. May not be <code>null</code>.
   * @param dValue
   *        The value of the attribute.
   * @return {@link EChange#CHANGED} if something changed,
   *         {@link EChange#UNCHANGED} otherwise.
   * @see #removeAttribute(String)
   */
  @Nonnull
  EChange setAttribute (@Nonnull String sName, double dValue);

  /**
   * Set/overwrite an arbitrary number of attribute values.
   * 
   * @param aValues
   *        The map of attributes to be set. May be <code>null</code>.
   * @return {@link EChange#CHANGED} if something changed,
   *         {@link EChange#UNCHANGED} otherwise.
   * @see #setAttribute(String,Object)
   */
  @Nonnull
  EChange setAttributes (@Nullable Map <String, ?> aValues);

  /**
   * Set/overwrite an arbitrary number of attribute values.
   * 
   * @param aValues
   *        The attributes to be set. May be <code>null</code>.
   * @return {@link EChange#CHANGED} if something changed,
   *         {@link EChange#UNCHANGED} otherwise.
   * @see #setAttribute(String,Object)
   */
  @Nonnull
  EChange setAttributes (@Nullable IReadonlyAttributeContainer aValues);

  /**
   * Remove the specified attribute from the container.
   * 
   * @param sName
   *        The attribute name to be removed. If it is <code>null</code> nothing
   *        happens.
   * @return {@link EChange#CHANGED} if something changed,
   *         {@link EChange#UNCHANGED} otherwise.
   */
  @Nonnull
  EChange removeAttribute (@Nullable String sName);

  /**
   * Atomic operation to set a flag to <code>true</code> if it was previously
   * set to <code>false</code> (meaning not existing). There is no possibility
   * to define a value for this flag. The value used is {@link Boolean#TRUE}.
   * {@link #containsAttribute(String)} can be used to check if the attribute is
   * already present.
   * 
   * @param sName
   *        The name of the flag to set.
   * @return The old value of the flag. If the flag was not present previously,
   *         than <code>false</code> is returned, whereas if the flag was
   *         already present, <code>true</code> is returned. Any other than the
   *         first call for the same flag is always returning <code>true</code>.
   */
  boolean getAndSetAttributeFlag (@Nonnull String sName);
}
