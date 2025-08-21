package com.helger.typeconvert.collection;

import com.helger.base.callback.ICallback;
import com.helger.base.state.EContinue;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * Callback interface to be invoked before a value is set.
 *
 * @author Philip Helger
 * @param <KEYTYPE>
 *        Key type
 * @param <VALUETYPE>
 *        Value type
 */
@FunctionalInterface
public interface IBeforeSetValueCallback <KEYTYPE, VALUETYPE> extends ICallback
{
  /**
   * Invoked before a new value is set
   *
   * @param aName
   *        The attribute key. May not be <code>null</code>.
   * @param aNewValue
   *        The new value to be set. May be <code>null</code>.
   * @return {@link EContinue#CONTINUE} if the value can be set, <code>false</code> if the value
   *         cannot be set.
   */
  @Nonnull
  EContinue beforeSetValue (@Nonnull KEYTYPE aName, @Nullable VALUETYPE aNewValue);
}
