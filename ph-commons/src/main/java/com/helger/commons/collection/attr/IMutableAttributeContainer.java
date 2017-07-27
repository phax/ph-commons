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
  public static interface IBeforeSetAttributeCallback <KEYTYPE, VALUETYPE> extends ICallback
  {
    @Nonnull
    EContinue beforeSetAttribute (@Nonnull KEYTYPE aName, @Nullable VALUETYPE aValue);
  }

  /**
   * @return Callbacks to be invoked before attribute values are set. May not be
   *         <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableObject
  CallbackList <IBeforeSetAttributeCallback <KEYTYPE, VALUETYPE>> beforeSetAttributeCallbacks ();

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
   * @see #removeObject(Object)
   */
  @Nonnull
  default EChange putIn (@Nonnull final KEYTYPE aName, @Nullable final VALUETYPE aValue)
  {
    ValueEnforcer.notNull (aName, "Name");

    if (aValue == null)
      return removeObject (aName);

    // Callback for checks etc.
    if (beforeSetAttributeCallbacks ().isNotEmpty ())
      if (beforeSetAttributeCallbacks ().forEachBreakable (x -> x.beforeSetAttribute (aName, aValue)).isBreak ())
        return EChange.UNCHANGED;

    // Set and compare
    final VALUETYPE aOldValue = put (aName, aValue);
    return EChange.valueOf (!EqualsHelper.equals (aValue, aOldValue));
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

  /**
   * Shortcut for clear and put-all
   * 
   * @param aAttrs
   *        New attributes. May be <code>null</code>.
   * @return {@link EChange}
   */
  @Nonnull
  default EChange replaceAll (@Nullable final Map <? extends KEYTYPE, ? extends VALUETYPE> aAttrs)
  {
    EChange eChange = removeAll ();
    eChange = eChange.or (putAllIn (aAttrs));
    return eChange;
  }
}
