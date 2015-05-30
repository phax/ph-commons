/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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

import javax.annotation.Nonnull;

import com.helger.commons.state.EChange;

/**
 * This is the writable extension of the {@link IGenericAttributeContainer
 * <String, Object>}. <code>null</code> values are not allowed in this attribute
 * containers.
 *
 * @author Philip Helger
 */
public interface IMutableAttributeContainer extends IGenericMutableAttributeContainer <String, Object>
{
  /**
   * Set/overwrite an in attribute value. This is a shortcut for
   * <code>setAttribute (aName, Boolean.valueOf (bValue));</code>
   *
   * @param aName
   *        The name of the attribute. May not be <code>null</code>.
   * @param bValue
   *        The value of the attribute.
   * @return {@link EChange#CHANGED} if something changed,
   *         {@link EChange#UNCHANGED} otherwise.
   * @see IGenericMutableAttributeContainer#removeAttribute(Object)
   */
  @Nonnull
  EChange setAttribute (@Nonnull String aName, boolean bValue);

  /**
   * Set/overwrite an in attribute value. This is a shortcut for
   * <code>setAttribute (aName, Integer.valueOf (nValue));</code>
   *
   * @param aName
   *        The name of the attribute. May not be <code>null</code>.
   * @param nValue
   *        The value of the attribute.
   * @return {@link EChange#CHANGED} if something changed,
   *         {@link EChange#UNCHANGED} otherwise.
   * @see IGenericMutableAttributeContainer#removeAttribute(Object)
   */
  @Nonnull
  EChange setAttribute (@Nonnull String aName, int nValue);

  /**
   * Set/overwrite an in attribute value. This is a shortcut for
   * <code>setAttribute (aName, Long.valueOf (nValue));</code>
   *
   * @param aName
   *        The name of the attribute. May not be <code>null</code>.
   * @param nValue
   *        The value of the attribute.
   * @return {@link EChange#CHANGED} if something changed,
   *         {@link EChange#UNCHANGED} otherwise.
   * @see IGenericMutableAttributeContainer#removeAttribute(Object)
   */
  @Nonnull
  EChange setAttribute (@Nonnull String aName, long nValue);

  /**
   * Set/overwrite an in attribute value. This is a shortcut for
   * <code>setAttribute (aName, Double.valueOf (nValue));</code>
   *
   * @param aName
   *        The name of the attribute. May not be <code>null</code>.
   * @param dValue
   *        The value of the attribute.
   * @return {@link EChange#CHANGED} if something changed,
   *         {@link EChange#UNCHANGED} otherwise.
   * @see IGenericMutableAttributeContainer#removeAttribute(Object)
   */
  @Nonnull
  EChange setAttribute (@Nonnull String aName, double dValue);

  /**
   * Atomic operation to set a flag to <code>true</code> if it was previously
   * set to <code>false</code> (meaning not existing). There is no possibility
   * to define a value for this flag. The value used is {@link Boolean#TRUE}.
   * {@link IGenericMutableAttributeContainer#containsAttribute(Object)} can be
   * used to check if the attribute is already present.
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
