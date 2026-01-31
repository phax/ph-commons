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
package com.helger.base.callback;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.GuardedBy;
import com.helger.annotation.concurrent.ThreadSafe;
import com.helger.annotation.style.CodingStyleguideUnaware;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.clone.ICloneable;
import com.helger.base.concurrent.SimpleReadWriteLock;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.state.EChange;
import com.helger.base.state.EContinue;
import com.helger.base.tostring.ToStringGenerator;

/**
 * This class manages a list of callbacks. In reality it is a Set of callbacks.
 *
 * @author Philip Helger
 * @param <CALLBACKTYPE>
 *        The callback type.
 */
@ThreadSafe
public class CallbackList <CALLBACKTYPE extends ICallback> implements
                          ICallbackList <CALLBACKTYPE>,
                          ICloneable <CallbackList <CALLBACKTYPE>>
{
  private static final Logger LOGGER = LoggerFactory.getLogger (CallbackList.class);

  private final SimpleReadWriteLock m_aRWLock = new SimpleReadWriteLock ();

  @GuardedBy ("m_aRWLock")
  @CodingStyleguideUnaware
  private final Set <CALLBACKTYPE> m_aCallbacks = new LinkedHashSet <> ();

  public CallbackList ()
  {}

  public CallbackList (@Nullable final CallbackList <CALLBACKTYPE> aOther)
  {
    if (aOther != null)
      m_aCallbacks.addAll (aOther.m_aCallbacks);
  }

  /**
   * Clear all elements and add all provided values. If no value is provided,the collection is empty
   * afterwards.
   *
   * @param rhs
   *        the callback list to set. May not be <code>null</code>.
   * @return {@link EChange}
   */
  @NonNull
  public EChange set (@NonNull final CallbackList <CALLBACKTYPE> rhs)
  {
    ValueEnforcer.notNull (rhs, "rhs");

    return m_aRWLock.writeLockedGet ( () -> {
      // If something was present or something is to be set -> change
      final EChange ret = EChange.valueOf (!m_aCallbacks.isEmpty () || rhs.isNotEmpty ());
      m_aCallbacks.clear ();
      m_aCallbacks.addAll (rhs.m_aCallbacks);
      return ret;
    });
  }

  /**
   * Clear all elements and add only the passed value.
   *
   * @param aCallback
   *        The callback to be used. May not be <code>null</code>.
   * @return {@link EChange}
   */
  @NonNull
  public EChange set (@NonNull final CALLBACKTYPE aCallback)
  {
    ValueEnforcer.notNull (aCallback, "Callback");

    return m_aRWLock.writeLockedGet ( () -> {
      final EChange ret = EChange.valueOf (!m_aCallbacks.isEmpty ());
      m_aCallbacks.clear ();
      m_aCallbacks.add (aCallback);
      return ret;
    });
  }

  /**
   * Add a callback.
   *
   * @param aCallback
   *        May not be <code>null</code>.
   * @return {@link EChange}
   */
  @NonNull
  public EChange add (@NonNull final CALLBACKTYPE aCallback)
  {
    ValueEnforcer.notNull (aCallback, "Callback");

    return m_aRWLock.writeLockedGet ( () -> EChange.valueOf (m_aCallbacks.add (aCallback)));
  }

  /**
   * Add an array of elements to this collection.
   *
   * @param aCallbacks
   *        The elements to be added. May be null.
   * @return {@link EChange}
   */
  @NonNull
  @SafeVarargs
  public final EChange addAll (@NonNull final CALLBACKTYPE... aCallbacks)
  {
    ValueEnforcer.notNullNoNullValue (aCallbacks, "Callbacks");

    return m_aRWLock.writeLockedGet ( () -> {
      EChange ret = EChange.UNCHANGED;
      for (final CALLBACKTYPE aItem : aCallbacks)
        if (m_aCallbacks.add (aItem))
          ret = EChange.CHANGED;
      return ret;
    });
  }

  /**
   * Remove the specified callback
   *
   * @param aCallback
   *        May be <code>null</code>.
   * @return {@link EChange}
   */
  @NonNull
  public EChange removeObject (@Nullable final CALLBACKTYPE aCallback)
  {
    if (aCallback == null)
      return EChange.UNCHANGED;

    return m_aRWLock.writeLockedGet ( () -> EChange.valueOf (m_aCallbacks.remove (aCallback)));
  }

  /**
   * Remove all callbacks
   *
   * @return {@link EChange}
   */
  @NonNull
  public EChange removeAll ()
  {
    return m_aRWLock.writeLockedGet ( () -> {
      final EChange ret = EChange.valueOf (!m_aCallbacks.isEmpty ());
      m_aCallbacks.clear ();
      return ret;
    });
  }

  @NonNull
  @ReturnsMutableCopy
  public List <CALLBACKTYPE> getAllCallbacks ()
  {
    // Happy very often
    m_aRWLock.readLock ().lock ();
    try
    {
      return new ArrayList <> (m_aCallbacks);
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }

  @Nullable
  public CALLBACKTYPE getCallbackAtIndex (@Nonnegative final int nIndex)
  {
    ValueEnforcer.isGE0 (nIndex, "Index");

    return m_aRWLock.readLockedGet ( () -> {
      int nCurIdx = 0;
      for (final CALLBACKTYPE aItem : m_aCallbacks)
      {
        if (nCurIdx == nIndex)
          return aItem;
        nCurIdx++;
      }
      return null;
    });
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

  @NonNull
  public CallbackList <CALLBACKTYPE> getClone ()
  {
    return m_aRWLock.readLockedGet ( () -> new CallbackList <> (this));
  }

  @NonNull
  public Iterator <CALLBACKTYPE> iterator ()
  {
    return m_aRWLock.readLockedGet (m_aCallbacks::iterator);
  }

  @Override
  public void forEach (@NonNull final Consumer <? super CALLBACKTYPE> aConsumer)
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

  @NonNull
  public EContinue forEachBreakable (@NonNull final Function <? super CALLBACKTYPE, EContinue> aFunction)
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
