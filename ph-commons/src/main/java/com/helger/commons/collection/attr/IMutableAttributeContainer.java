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
package com.helger.commons.collection.attr;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.state.EChange;
import com.helger.commons.state.IClearable;

/**
 * This is the writable extension of the {@link IAttributeContainer}.
 * <code>null</code> values are not allowed in this attribute containers.
 *
 * @author Philip Helger
 * @param <KEYTYPE>
 *        Key type
 * @param <VALUETYPE>
 *        Value type
 */
public interface IMutableAttributeContainer <KEYTYPE, VALUETYPE>
                                            extends IAttributeContainer <KEYTYPE, VALUETYPE>, IClearable
{
  /**
   * Set/overwrite an attribute value.
   *
   * @param aName
   *        The name of the attribute. May not be <code>null</code>.
   * @param aValue
   *        The value of the attribute. If it is <code>null</code>, the value
   *        will be removed.
   * @return {@link EChange#CHANGED} if something changed,
   *         {@link EChange#UNCHANGED} otherwise.
   * @see #removeAttribute(Object)
   */
  @Nonnull
  EChange setAttribute (@Nonnull KEYTYPE aName, @Nullable VALUETYPE aValue);

  /**
   * Set/overwrite an arbitrary number of attribute values.
   *
   * @param aValues
   *        The map of attributes to be set. May be <code>null</code>.
   * @return {@link EChange#CHANGED} if something changed,
   *         {@link EChange#UNCHANGED} otherwise.
   * @see #setAttribute(Object,Object)
   */
  @Nonnull
  EChange setAttributes (@Nullable Map <? extends KEYTYPE, ? extends VALUETYPE> aValues);

  /**
   * Set/overwrite an arbitrary number of attribute values.
   *
   * @param aValues
   *        The attributes to be set. May be <code>null</code>.
   * @return {@link EChange#CHANGED} if something changed,
   *         {@link EChange#UNCHANGED} otherwise.
   * @see #setAttribute(Object,Object)
   */
  @Nonnull
  EChange setAttributes (@Nullable IAttributeContainer <? extends KEYTYPE, ? extends VALUETYPE> aValues);

  /**
   * Remove the specified attribute from the container.
   *
   * @param aName
   *        The attribute name to be removed. If it is <code>null</code> nothing
   *        happens.
   * @return {@link EChange#CHANGED} if something changed,
   *         {@link EChange#UNCHANGED} otherwise.
   */
  @Nonnull
  EChange removeAttribute (@Nullable KEYTYPE aName);
}
