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

import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.ReturnsMutableObject;
import com.helger.commons.callback.CallbackList;
import com.helger.commons.callback.ICallback;
import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.state.EChange;
import com.helger.commons.state.EContinue;

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
public interface IMutableAttributeContainer <KEYTYPE, VALUETYPE> extends IAttributeContainer <KEYTYPE, VALUETYPE>
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
   * Restore a value from serialization. Does not trigger an event!
   *
   * @param aName
   *        The name of the field.
   * @param aNewValue
   *        The value to be set. May be <code>null</code> .
   */
  default void restoreValue (@Nonnull final KEYTYPE aName, @Nullable final VALUETYPE aNewValue)
  {
    ValueEnforcer.notNull (aName, "Name");
    if (aNewValue == null)
      removeObject (aName);
    else
      put (aName, aNewValue);
  }

  /**
   * Set/overwrite an attribute value.
   *
   * @param aName
   *        The name of the attribute. May not be <code>null</code>.
   * @param aNewValue
   *        The new value of the attribute. If it is <code>null</code>, the
   *        value will be removed.
   * @return {@link EChange#CHANGED} if something changed,
   *         {@link EChange#UNCHANGED} otherwise.
   * @see #removeObject(Object)
   */
  @Nonnull
  default EChange putIn (@Nonnull final KEYTYPE aName, @Nullable final VALUETYPE aNewValue)
  {
    ValueEnforcer.notNull (aName, "Name");

    if (aNewValue == null)
      return removeObject (aName);

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
}
