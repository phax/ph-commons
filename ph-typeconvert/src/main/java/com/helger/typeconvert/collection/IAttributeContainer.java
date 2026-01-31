/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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
package com.helger.typeconvert.collection;

import java.util.Map;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.annotation.style.ReturnsMutableObject;
import com.helger.base.CGlobal;
import com.helger.base.callback.CallbackList;
import com.helger.base.equals.EqualsHelper;
import com.helger.base.state.EChange;
import com.helger.collection.commons.ICommonsMap;
import com.helger.typeconvert.trait.IGetterByKeyTrait;

/**
 * Base interface for a generic read-only attribute container. It maps keys to values.<br>
 * Note: don't implement <code>Iterable&lt;Map.Entry&lt;...&gt;&gt;</code> because this would make
 * the object ambiguous to e.g. <code>HashCodeGenerator</code>
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
   * @return The attribute value or {@link com.helger.base.CGlobal#ILLEGAL_UINT} if no such
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
   * @return The attribute value or {@link com.helger.base.CGlobal#ILLEGAL_ULONG} if no such
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
   * @return The attribute value or {@link com.helger.base.CGlobal#ILLEGAL_UINT} if no such
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
   * @return The attribute value or {@link com.helger.base.CGlobal#ILLEGAL_UINT} if no such
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
   * @return The attribute value or <code>false</code> if no such attribute exists
   */
  @Override
  default boolean getAsBoolean (@Nullable final KEYTYPE aName)
  {
    return getAsBoolean (aName, false);
  }

  /**
   * @return Callbacks to be invoked before values are set. May not be <code>null</code>.
   */
  @NonNull
  @ReturnsMutableObject
  CallbackList <IBeforeSetValueCallback <KEYTYPE, VALUETYPE>> beforeSetValueCallbacks ();

  /**
   * @return Callbacks to be invoked after values are set. May not be <code>null</code>.
   */
  @NonNull
  @ReturnsMutableObject
  CallbackList <IAfterSetValueCallback <KEYTYPE, VALUETYPE>> afterSetValueCallbacks ();

  /**
   * Set/overwrite an attribute value including before and after callbacks.
   *
   * @param aName
   *        The name of the attribute. May not be <code>null</code>.
   * @param aNewValue
   *        The new value of the attribute. May be <code>null</code>.
   * @return {@link EChange#CHANGED} if something changed, {@link EChange#UNCHANGED} otherwise.
   * @see #beforeSetValueCallbacks()
   * @see #afterSetValueCallbacks()
   * @see #removeObject(Object)
   */
  @NonNull
  default EChange putIn (@NonNull final KEYTYPE aName, @Nullable final VALUETYPE aNewValue)
  {
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

  @NonNull
  default EChange putAllIn (@Nullable final Map <? extends KEYTYPE, ? extends VALUETYPE> aAttrs)
  {
    EChange eChange = EChange.UNCHANGED;
    if (aAttrs != null)
      for (final Map.Entry <? extends KEYTYPE, ? extends VALUETYPE> aEntry : aAttrs.entrySet ())
        eChange = eChange.or (putIn (aEntry.getKey (), aEntry.getValue ()));
    return eChange;
  }

  @NonNull
  @ReturnsMutableCopy
  IAttributeContainer <KEYTYPE, VALUETYPE> getClone ();
}
