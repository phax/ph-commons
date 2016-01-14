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
import com.helger.commons.callback.INonThrowingCallableWithParameter;
import com.helger.commons.callback.INonThrowingRunnableWithParameter;
import com.helger.commons.string.ToStringGenerator;

/**
 * A helper class that converts a {@link INonThrowingRunnableWithParameter} into
 * an {@link INonThrowingCallableWithParameter}.
 *
 * @author Philip Helger
 * @param <DATATYPE>
 *        The callable result type.
 * @param <PARAMTYPE>
 *        The parameter type.
 */
@Immutable
public class AdapterRunnableToCallableWithParameter <DATATYPE, PARAMTYPE>
                                                    implements INonThrowingCallableWithParameter <DATATYPE, PARAMTYPE>
{
  private final INonThrowingRunnableWithParameter <PARAMTYPE> m_aRunnable;
  private final DATATYPE m_aResult;

  public AdapterRunnableToCallableWithParameter (@Nonnull final INonThrowingRunnableWithParameter <PARAMTYPE> aRunnable)
  {
    this (aRunnable, null);
  }

  public AdapterRunnableToCallableWithParameter (@Nonnull final INonThrowingRunnableWithParameter <PARAMTYPE> aRunnable,
                                                 @Nullable final DATATYPE aResult)
  {
    m_aRunnable = ValueEnforcer.notNull (aRunnable, "Runnable");
    m_aResult = aResult;
  }

  @Nonnull
  public INonThrowingRunnableWithParameter <PARAMTYPE> getRunnable ()
  {
    return m_aRunnable;
  }

  @Nullable
  public DATATYPE getResult ()
  {
    return m_aResult;
  }

  @Nullable
  public DATATYPE call (final PARAMTYPE aParam)
  {
    m_aRunnable.run (aParam);
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
   * @return The created {@link AdapterRunnableToCallableWithParameter} object.
   * @param <PARAMTYPE>
   *        The parameter type.
   */
  @Nonnull
  public static <PARAMTYPE> AdapterRunnableToCallableWithParameter <Object, PARAMTYPE> createAdapter (@Nonnull final INonThrowingRunnableWithParameter <PARAMTYPE> aRunnable)
  {
    return new AdapterRunnableToCallableWithParameter <Object, PARAMTYPE> (aRunnable);
  }

  /**
   * Create a callable that always returns the passed value.
   *
   * @param aRunnable
   *        The runnable to be executed.
   * @param aResult
   *        The expected result from calling {@link INonThrowingCallable#call()}
   *        . May be <code>null</code>.
   * @return The created {@link AdapterRunnableToCallableWithParameter} object.
   * @param <DATATYPE>
   *        The callable result type.
   * @param <PARAMTYPE>
   *        The parameter type.
   */
  @Nonnull
  public static <DATATYPE, PARAMTYPE> AdapterRunnableToCallableWithParameter <DATATYPE, PARAMTYPE> createAdapter (@Nonnull final INonThrowingRunnableWithParameter <PARAMTYPE> aRunnable,
                                                                                                                  @Nullable final DATATYPE aResult)
  {
    return new AdapterRunnableToCallableWithParameter <DATATYPE, PARAMTYPE> (aRunnable, aResult);
  }
}
