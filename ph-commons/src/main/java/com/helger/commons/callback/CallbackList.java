/**
 * Copyright (C) 2014-2021 Philip Helger (www.helger.com)
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

import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.impl.CommonsLinkedHashSet;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.collection.impl.ICommonsOrderedSet;
import com.helger.commons.concurrent.SimpleReadWriteLock;
import com.helger.commons.lang.ICloneable;
import com.helger.commons.state.EChange;
import com.helger.commons.state.EContinue;
import com.helger.commons.string.ToStringGenerator;

/**
 * This class manages a list of callbacks. In reality it is a Set of callbacks.
 *
 * @author Philip Helger
 * @param <CALLBACKTYPE>
 *        The callback type.
 */
@ThreadSafe
public class CallbackList <CALLBACKTYPE extends ICallback> implements ICallbackList <CALLBACKTYPE>, ICloneable <CallbackList <CALLBACKTYPE>>
{
  private static final Logger LOGGER = LoggerFactory.getLogger (CallbackList.class);

  private final SimpleReadWriteLock m_aRWLock = new SimpleReadWriteLock ();

  @GuardedBy ("m_aRWLock")
  private final ICommonsOrderedSet <CALLBACKTYPE> m_aCallbacks = new CommonsLinkedHashSet <> ();

  public CallbackList ()
  {}

  public CallbackList (@Nullable final CallbackList <CALLBACKTYPE> aOther)
  {
    if (aOther != null)
      m_aCallbacks.addAll (aOther.m_aCallbacks);
  }

  /**
   * Clear all elements and add all provided values. If no value is provided,the
   * collection is empty afterwards.
   *
   * @param rhs
   *        the callback list to set. May not be <code>null</code>.
   * @return {@link EChange}
   */
  @Nonnull
  public EChange set (@Nonnull final CallbackList <CALLBACKTYPE> rhs)
  {
    ValueEnforcer.notNull (rhs, "rhs");
    return m_aRWLock.writeLockedGet ( () -> m_aCallbacks.setAll (rhs.m_aCallbacks));
  }

  /**
   * Clear all elements and add only the passed value.
   *
   * @param aCallback
   *        The callback to be used. May not be <code>null</code>.
   * @return {@link EChange}
   */
  @Nonnull
  public EChange set (@Nonnull final CALLBACKTYPE aCallback)
  {
    ValueEnforcer.notNull (aCallback, "Callback");
    return m_aRWLock.writeLockedGet ( () -> m_aCallbacks.set (aCallback));
  }

  /**
   * Add a callback.
   *
   * @param aCallback
   *        May not be <code>null</code>.
   * @return {@link EChange}
   */
  @Nonnull
  public EChange add (@Nonnull final CALLBACKTYPE aCallback)
  {
    ValueEnforcer.notNull (aCallback, "Callback");

    return m_aRWLock.writeLockedGet ( () -> m_aCallbacks.addObject (aCallback));
  }

  /**
   * Add an array of elements to this collection.
   *
   * @param aCallbacks
   *        The elements to be added. May be null.
   * @return {@link EChange}
   */
  @Nonnull
  @SafeVarargs
  public final EChange addAll (@Nonnull final CALLBACKTYPE... aCallbacks)
  {
    ValueEnforcer.notNullNoNullValue (aCallbacks, "Callbacks");

    return m_aRWLock.writeLockedGet ( () -> m_aCallbacks.addAll (aCallbacks));
  }

  /**
   * Remove the specified callback
   *
   * @param aCallback
   *        May be <code>null</code>.
   * @return {@link EChange}
   */
  @Nonnull
  public EChange removeObject (@Nullable final CALLBACKTYPE aCallback)
  {
    if (aCallback == null)
      return EChange.UNCHANGED;

    return m_aRWLock.writeLockedGet ( () -> m_aCallbacks.removeObject (aCallback));
  }

  /**
   * Remove all callbacks
   *
   * @return {@link EChange}
   */
  @Nonnull
  public EChange removeAll ()
  {
    return m_aRWLock.writeLockedGet (m_aCallbacks::removeAll);
  }

  @Nonnull
  @ReturnsMutableCopy
  public ICommonsList <CALLBACKTYPE> getAllCallbacks ()
  {
    // Happy very often
    m_aRWLock.readLock ().lock ();
    try
    {
      return m_aCallbacks.getCopyAsList ();
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }

  @Nullable
  public CALLBACKTYPE getCallbackAtIndex (@Nonnegative final int nIndex)
  {
    return m_aRWLock.readLockedGet ( () -> m_aCallbacks.getAtIndex (nIndex));
  }

  @Nonnegative
  public int size ()
  {
    return m_aRWLock.readLockedInt (m_aCallbacks::size);
  }

  public boolean isEmpty ()
  {
    // Called very often - performance improvement
    m_aRWLock.readLock ().lock ();
    try
    {
      return m_aCallbacks.isEmpty ();
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }

  @Nonnull
  public CallbackList <CALLBACKTYPE> getClone ()
  {
    return m_aRWLock.readLockedGet ( () -> new CallbackList <> (this));
  }

  @Nonnull
  public Iterator <CALLBACKTYPE> iterator ()
  {
    return m_aRWLock.readLockedGet (m_aCallbacks::iterator);
  }

  @Override
  public void forEach (@Nonnull final Consumer <? super CALLBACKTYPE> aConsumer)
  {
    // Create a copy to iterate!
    for (final CALLBACKTYPE aCallback : getAllCallbacks ())
      try
      {
        aConsumer.accept (aCallback);
      }
      catch (final Exception ex)
      {
        LOGGER.error ("Failed to invoke callback " + aCallback, ex);
      }
  }

  @Nonnull
  @Override
  public EContinue forEachBreakable (@Nonnull final Function <? super CALLBACKTYPE, EContinue> aFunction)
  {
    // Create a copy to iterate!
    for (final CALLBACKTYPE aCallback : getAllCallbacks ())
      try
      {
        if (aFunction.apply (aCallback).isBreak ())
          return EContinue.BREAK;
      }
      catch (final Exception ex)
      {
        LOGGER.error ("Failed to invoke callback " + aCallback, ex);
      }
    return EContinue.CONTINUE;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("callbacks", m_aCallbacks).getToString ();
  }
}
