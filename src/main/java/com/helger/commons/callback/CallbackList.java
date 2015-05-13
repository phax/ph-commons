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
package com.helger.commons.callback;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import com.helger.commons.ICloneable;
import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotations.ReturnsMutableCopy;
import com.helger.commons.collections.CollectionHelper;
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
public class CallbackList <CALLBACKTYPE extends ICallback> implements ICloneable <CallbackList <CALLBACKTYPE>>
{
  private final ReadWriteLock m_aRWLock = new ReentrantReadWriteLock ();

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

    m_aRWLock.writeLock ().lock ();
    try
    {
      m_aCallbacks.add (aCallback);
    }
    finally
    {
      m_aRWLock.writeLock ().unlock ();
    }
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

    m_aRWLock.writeLock ().lock ();
    try
    {
      return EChange.valueOf (m_aCallbacks.remove (aCallback));
    }
    finally
    {
      m_aRWLock.writeLock ().unlock ();
    }
  }

  /**
   * Remove all callbacks
   *
   * @return {@link EChange}
   */
  @Nonnull
  public EChange removeAllCallbacks ()
  {
    m_aRWLock.writeLock ().lock ();
    try
    {
      if (m_aCallbacks.isEmpty ())
        return EChange.UNCHANGED;
      m_aCallbacks.clear ();
      return EChange.CHANGED;
    }
    finally
    {
      m_aRWLock.writeLock ().unlock ();
    }
  }

  /**
   * @return A list of all callbacks. Never <code>null</code> and only
   *         containing non-<code>null</code> elements.
   */
  @Nonnull
  @ReturnsMutableCopy
  public List <CALLBACKTYPE> getAllCallbacks ()
  {
    m_aRWLock.readLock ().lock ();
    try
    {
      return CollectionHelper.newList (m_aCallbacks);
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }

  @Nullable
  public CALLBACKTYPE getCallbackAtIndex (@Nonnegative final int nIndex)
  {
    m_aRWLock.readLock ().lock ();
    try
    {
      return CollectionHelper.getSafe (m_aCallbacks, nIndex);
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }

  @Nonnegative
  public int getCallbackCount ()
  {
    m_aRWLock.readLock ().lock ();
    try
    {
      return m_aCallbacks.size ();
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }

  public boolean hasCallbacks ()
  {
    m_aRWLock.readLock ().lock ();
    try
    {
      return !m_aCallbacks.isEmpty ();
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }

  @Nonnull
  public CallbackList <CALLBACKTYPE> getClone ()
  {
    m_aRWLock.readLock ().lock ();
    try
    {
      return new CallbackList <CALLBACKTYPE> (this);
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("callbacks", m_aCallbacks).toString ();
  }
}
