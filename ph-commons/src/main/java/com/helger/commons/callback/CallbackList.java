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
package com.helger.commons.callback;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.concurrent.SimpleReadWriteLock;
import com.helger.commons.lang.ICloneable;
import com.helger.commons.state.EChange;
import com.helger.commons.string.ToStringGenerator;

/**
 * This class manages a list of callbacks.
 *
 * @author Philip Helger
 * @param <CALLBACKTYPE>
 *        The callback type.
 */
@ThreadSafe
public class CallbackList <CALLBACKTYPE extends ICallback>
                          implements ICallbackList <CALLBACKTYPE>, ICloneable <CallbackList <CALLBACKTYPE>>
{
  private final SimpleReadWriteLock m_aRWLock = new SimpleReadWriteLock ();

  @GuardedBy ("m_aRWLock")
  private final List <CALLBACKTYPE> m_aCallbacks = new ArrayList <CALLBACKTYPE> ();

  public CallbackList ()
  {}

  public CallbackList (@Nonnull final CallbackList <CALLBACKTYPE> aOther)
  {
    ValueEnforcer.notNull (aOther, "Other");
    m_aCallbacks.addAll (aOther.m_aCallbacks);
  }

  /**
   * Add a callback.
   *
   * @param aCallback
   *        May not be <code>null</code>.
   * @return this
   */
  @Nonnull
  public CallbackList <CALLBACKTYPE> addCallback (@Nonnull final CALLBACKTYPE aCallback)
  {
    ValueEnforcer.notNull (aCallback, "Callback");

    m_aRWLock.writeLocked ( () -> m_aCallbacks.add (aCallback));
    return this;
  }

  /**
   * Remove the specified callback
   *
   * @param aCallback
   *        May be <code>null</code>.
   * @return {@link EChange}
   */
  @Nonnull
  public EChange removeCallback (@Nullable final CALLBACKTYPE aCallback)
  {
    if (aCallback == null)
      return EChange.UNCHANGED;

    return EChange.valueOf (m_aRWLock.writeLocked ( () -> m_aCallbacks.remove (aCallback)));
  }

  /**
   * Remove all callbacks
   *
   * @return {@link EChange}
   */
  @Nonnull
  public EChange removeAllCallbacks ()
  {
    return m_aRWLock.writeLocked ( () -> {
      if (m_aCallbacks.isEmpty ())
        return EChange.UNCHANGED;
      m_aCallbacks.clear ();
      return EChange.CHANGED;
    });
  }

  @Nonnull
  @ReturnsMutableCopy
  public List <CALLBACKTYPE> getAllCallbacks ()
  {
    return m_aRWLock.readLocked ( () -> CollectionHelper.newList (m_aCallbacks));
  }

  @Nullable
  public CALLBACKTYPE getCallbackAtIndex (@Nonnegative final int nIndex)
  {
    return m_aRWLock.readLocked ( () -> CollectionHelper.getSafe (m_aCallbacks, nIndex));
  }

  @Nonnegative
  public int getCallbackCount ()
  {
    return m_aRWLock.readLocked ( () -> m_aCallbacks.size ());
  }

  public boolean hasCallbacks ()
  {
    return m_aRWLock.readLocked ( () -> !m_aCallbacks.isEmpty ());
  }

  @Nonnull
  public CallbackList <CALLBACKTYPE> getClone ()
  {
    return m_aRWLock.readLocked ( () -> new CallbackList <CALLBACKTYPE> (this));
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("callbacks", m_aCallbacks).toString ();
  }
}
