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
package com.helger.commons.callback.adapter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.callback.INonThrowingCallable;
import com.helger.commons.string.ToStringGenerator;

/**
 * A helper class that converts a {@link Runnable} into an
 * {@link INonThrowingCallable}.
 *
 * @author Philip Helger
 * @param <DATATYPE>
 *        The callable result type.
 */
@Immutable
public class AdapterRunnableToCallable <DATATYPE> implements INonThrowingCallable <DATATYPE>
{
  private final Runnable m_aRunnable;
  private final DATATYPE m_aResult;

  public AdapterRunnableToCallable (@Nonnull final Runnable aRunnable)
  {
    this (aRunnable, null);
  }

  public AdapterRunnableToCallable (@Nonnull final Runnable aRunnable, @Nullable final DATATYPE aResult)
  {
    m_aRunnable = ValueEnforcer.notNull (aRunnable, "Runnable");
    m_aResult = aResult;
  }

  @Nonnull
  public Runnable getRunnable ()
  {
    return m_aRunnable;
  }

  @Nullable
  public DATATYPE getResult ()
  {
    return m_aResult;
  }

  @Nullable
  public DATATYPE call ()
  {
    m_aRunnable.run ();
    return m_aResult;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("runnable", m_aRunnable).append ("result", m_aResult).toString ();
  }

  /**
   * Create a callable that always returns <code>null</code>.
   *
   * @param aRunnable
   *        The runnable to be executed.
   * @return The created {@link AdapterRunnableToCallable} object.
   */
  @Nonnull
  public static AdapterRunnableToCallable <Object> createAdapter (@Nonnull final Runnable aRunnable)
  {
    return new AdapterRunnableToCallable <Object> (aRunnable);
  }

  /**
   * Create a callable that always returns the passed value.
   *
   * @param aRunnable
   *        The runnable to be executed.
   * @param aResult
   *        The expected result from calling {@link INonThrowingCallable#call()}
   *        . May be <code>null</code>.
   * @return The created {@link AdapterRunnableToCallable} object.
   * @param <DATATYPE>
   *        The callable result type.
   */
  @Nonnull
  public static <DATATYPE> AdapterRunnableToCallable <DATATYPE> createAdapter (@Nonnull final Runnable aRunnable,
                                                                               @Nullable final DATATYPE aResult)
  {
    return new AdapterRunnableToCallable <DATATYPE> (aRunnable, aResult);
  }
}
