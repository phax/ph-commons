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
package com.helger.commons.collection.attr;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.CGlobal;
import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.annotation.ReturnsMutableObject;
import com.helger.commons.callback.CallbackList;
import com.helger.commons.callback.ICallback;
import com.helger.commons.collection.impl.ICommonsMap;
import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.state.EChange;
import com.helger.commons.state.EContinue;
import com.helger.commons.traits.IGetterByKeyTrait;

/**
 * Base interface for a generic read-only attribute container. It maps keys to
 * values.<br>
 * Note: don't implement <code>Iterable&lt;Map.Entry&lt;...&gt;&gt;</code>
 * because this would make the object ambiguous to e.g.
 * <code>HashCodeGenerator</code>
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
  @FunctionalInterface
  public static interface IBeforeSetValueCallback <KEYTYPE, VALUETYPE> extends ICallback
  {
    /**
     * Invoked before a new value is set
     *
     * @param aName
     *        The attribute key. May not be <code>null</code>.
     * @param aNewValue
     *        The new value to be set. May be <code>null</code>.
     * @return {@link EContinue#CONTINUE} if the value can be set,
     *         <code>false</code> if the value cannot be set.
     */
    @Nonnull
    EContinue beforeSetValue (@Nonnull KEYTYPE aName, @Nullable VALUETYPE aNewValue);
  }

  @FunctionalInterface
  public static interface IAfterSetValueCallback <KEYTYPE, VALUETYPE> extends ICallback
  {
    /**
     * Method that is invoked after a value changed.
     *
     * @param aName
     *        The changed key. Neither <code>null</code> nor empty.
     * @param aOldValue
     *        The old value. May be <code>null</code>.
     * @param aNewValue
     *        The new value. May be <code>null</code> in which case the value
     *        was removed.
     */
    void afterSetValue (@Nonnull KEYTYPE aName, @Nullable VALUETYPE aOldValue, @Nullable VALUETYPE aNewValue);
  }

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
  @Override
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
  @Override
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
  @Override
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
  @Override
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
  @Override
  default boolean getAsBoolean (@Nullable final KEYTYPE aName)
  {
    return getAsBoolean (aName, false);
  }

  /**
   * @return Callbacks to be invoked before values are set. May not be
   *         <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableObject
  CallbackList <IBeforeSetValueCallback <KEYTYPE, VALUETYPE>> beforeSetValueCallbacks ();

  /**
   * @return Callbacks to be invoked after values are set. May not be
   *         <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableObject
  CallbackList <IAfterSetValueCallback <KEYTYPE, VALUETYPE>> afterSetValueCallbacks ();

  /**
   * Set/overwrite an attribute value including before and after callbacks.
   *
   * @param aName
   *        The name of the attribute. May not be <code>null</code>.
   * @param aNewValue
   *        The new value of the attribute. May be <code>null</code>.
   * @return {@link EChange#CHANGED} if something changed,
   *         {@link EChange#UNCHANGED} otherwise.
   * @see #beforeSetValueCallbacks()
   * @see #afterSetValueCallbacks()
   * @see #removeObject(Object)
   */
  @Nonnull
  default EChange putIn (@Nonnull final KEYTYPE aName, @Nullable final VALUETYPE aNewValue)
  {
    ValueEnforcer.notNull (aName, "Name");

    // Before change checking callback
    if (beforeSetValueCallbacks ().forEachBreakable (x -> x.beforeSetValue (aName, aNewValue)).isBreak ())
      return EChange.UNCHANGED;

    // Set and compare
    final VALUETYPE aOldValue = put (aName, aNewValue);
    if (EqualsHelper.equals (aNewValue, aOldValue))
      return EChange.UNCHANGED;

    // After change callback
    afterSetValueCallbacks ().forEach (x -> x.afterSetValue (aName, aOldValue, aNewValue));
    return EChange.CHANGED;
  }

  @Nonnull
  default EChange putAllIn (@Nullable final Map <? extends KEYTYPE, ? extends VALUETYPE> aAttrs)
  {
    EChange eChange = EChange.UNCHANGED;
    if (aAttrs != null)
      for (final Map.Entry <? extends KEYTYPE, ? extends VALUETYPE> aEntry : aAttrs.entrySet ())
        eChange = eChange.or (putIn (aEntry.getKey (), aEntry.getValue ()));
    return eChange;
  }

  @Nonnull
  @ReturnsMutableCopy
  IAttributeContainer <KEYTYPE, VALUETYPE> getClone ();
}
