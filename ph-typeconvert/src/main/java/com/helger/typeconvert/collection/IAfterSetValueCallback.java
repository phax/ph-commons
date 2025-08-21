package com.helger.typeconvert.collection;

import com.helger.base.callback.ICallback;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * Callback interface to be invoked after a value was set.
 *
 * @author Philip Helger
 * @param <KEYTYPE>
 *        Key type
 * @param <VALUETYPE>
 *        Value type
 */
@FunctionalInterface
public interface IAfterSetValueCallback <KEYTYPE, VALUETYPE> extends ICallback
{
  /**
   * Method that is invoked after a value changed.
   *
   * @param aName
   *        The changed key. Neither <code>null</code> nor empty.
   * @param aOldValue
   *        The old value. May be <code>null</code>.
   * @param aNewValue
   *        The new value. May be <code>null</code> in which case the value was removed.
   */
  void afterSetValue (@Nonnull KEYTYPE aName, @Nullable VALUETYPE aOldValue, @Nullable VALUETYPE aNewValue);
}
