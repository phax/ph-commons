/*
 * Copyright (C) 2014-2024 Philip Helger (www.helger.com)
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

import javax.annotation.Nonnull;

import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.state.EChange;

/**
 * This is the writable extension of the {@link IAttributeContainer &lt;String,
 * Object&gt;}. <code>null</code> values are not allowed in this attribute
 * containers.
 *
 * @author Philip Helger
 * @param <KEYTYPE>
 *        Key type
 */
public interface IAttributeContainerAny <KEYTYPE> extends IAttributeContainer <KEYTYPE, Object>
{
  /**
   * Set/overwrite an attribute value. This is a shortcut for
   * <code>putIn (aName, Boolean.valueOf (bValue));</code>
   *
   * @param aName
   *        The name of the attribute. May not be <code>null</code>.
   * @param bValue
   *        The value of the attribute.
   * @return {@link EChange#CHANGED} if something changed,
   *         {@link EChange#UNCHANGED} otherwise.
   */
  @Nonnull
  default EChange putIn (@Nonnull final KEYTYPE aName, final boolean bValue)
  {
    return putIn (aName, Boolean.valueOf (bValue));
  }

  /**
   * Set/overwrite an attribute value. This is a shortcut for
   * <code>putIn (aName, Integer.valueOf (nValue));</code>
   *
   * @param aName
   *        The name of the attribute. May not be <code>null</code>.
   * @param nValue
   *        The value of the attribute.
   * @return {@link EChange#CHANGED} if something changed,
   *         {@link EChange#UNCHANGED} otherwise.
   */
  @Nonnull
  default EChange putIn (@Nonnull final KEYTYPE aName, final int nValue)
  {
    return putIn (aName, Integer.valueOf (nValue));
  }

  /**
   * Set/overwrite an attribute value. This is a shortcut for
   * <code>putIn (aName, Long.valueOf (nValue));</code>
   *
   * @param aName
   *        The name of the attribute. May not be <code>null</code>.
   * @param nValue
   *        The value of the attribute.
   * @return {@link EChange#CHANGED} if something changed,
   *         {@link EChange#UNCHANGED} otherwise.
   */
  @Nonnull
  default EChange putIn (@Nonnull final KEYTYPE aName, final long nValue)
  {
    return putIn (aName, Long.valueOf (nValue));
  }

  /**
   * Set/overwrite an attribute value. This is a shortcut for
   * <code>putIn (aName, Short.valueOf (nValue));</code>
   *
   * @param aName
   *        The name of the attribute. May not be <code>null</code>.
   * @param nValue
   *        The value of the attribute.
   * @return {@link EChange#CHANGED} if something changed,
   *         {@link EChange#UNCHANGED} otherwise.
   */
  @Nonnull
  default EChange putIn (@Nonnull final KEYTYPE aName, final short nValue)
  {
    return putIn (aName, Short.valueOf (nValue));
  }

  /**
   * Set/overwrite an attribute value. This is a shortcut for
   * <code>putIn (aName, Float.valueOf (fValue));</code>
   *
   * @param aName
   *        The name of the attribute. May not be <code>null</code>.
   * @param fValue
   *        The value of the attribute.
   * @return {@link EChange#CHANGED} if something changed,
   *         {@link EChange#UNCHANGED} otherwise.
   */
  @Nonnull
  default EChange putIn (@Nonnull final KEYTYPE aName, final float fValue)
  {
    return putIn (aName, Float.valueOf (fValue));
  }

  /**
   * Set/overwrite an attribute value. This is a shortcut for
   * <code>putIn (aName, Double.valueOf (dValue));</code>
   *
   * @param aName
   *        The name of the attribute. May not be <code>null</code>.
   * @param dValue
   *        The value of the attribute.
   * @return {@link EChange#CHANGED} if something changed,
   *         {@link EChange#UNCHANGED} otherwise.
   */
  @Nonnull
  default EChange putIn (@Nonnull final KEYTYPE aName, final double dValue)
  {
    return putIn (aName, Double.valueOf (dValue));
  }

  /**
   * Atomic operation to set a flag to <code>true</code> if it was previously
   * set to <code>false</code> (meaning not existing). There is no possibility
   * to define a value for this flag. The value used is {@link Boolean#TRUE}.
   * {@link IAttributeContainer#containsKey(Object)} can be used to check if the
   * attribute is already present.
   *
   * @param aName
   *        The name of the flag to set.
   * @return The old value of the flag. If the flag was not present previously,
   *         than <code>false</code> is returned, whereas if the flag was
   *         already present, <code>true</code> is returned. Any other than the
   *         first call for the same flag is always returning <code>true</code>.
   */
  default boolean getAndSetFlag (@Nonnull final KEYTYPE aName)
  {
    final Object aOldValue = get (aName);
    if (aOldValue != null)
    {
      // flag is already present
      return true;
    }
    // flag is not yet present -> set it
    // Invoke callback!
    putIn (aName, Boolean.TRUE);
    return false;
  }

  @Nonnull
  @ReturnsMutableCopy
  IAttributeContainerAny <KEYTYPE> getClone ();
}
