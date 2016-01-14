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
import com.helger.commons.callback.IThrowingCallable;
import com.helger.commons.callback.IThrowingCallableWithParameter;
import com.helger.commons.callback.IThrowingRunnable;
import com.helger.commons.callback.IThrowingRunnableWithParameter;
import com.helger.commons.string.ToStringGenerator;

/**
 * A helper class that converts a {@link IThrowingRunnable} into an
 * {@link IThrowingCallable}.
 *
 * @author Philip Helger
 * @param <DATATYPE>
 *        The callable result type.
 * @param <PARAMTYPE>
 *        The parameter type
 * @param <EXTYPE>
 *        Exception type to be thrown
 */
@Immutable
public class AdapterThrowingRunnableToCallableWithParameter <DATATYPE, PARAMTYPE, EXTYPE extends Exception> implements
                                                            IThrowingCallableWithParameter <DATATYPE, PARAMTYPE, EXTYPE>
{
  private final IThrowingRunnableWithParameter <PARAMTYPE, EXTYPE> m_aRunnable;
  private final DATATYPE m_aResult;

  public AdapterThrowingRunnableToCallableWithParameter (@Nonnull final IThrowingRunnableWithParameter <PARAMTYPE, EXTYPE> aRunnable)
  {
    this (aRunnable, null);
  }

  public AdapterThrowingRunnableToCallableWithParameter (@Nonnull final IThrowingRunnableWithParameter <PARAMTYPE, EXTYPE> aRunnable,
                                                         @Nullable final DATATYPE aResult)
  {
    m_aRunnable = ValueEnforcer.notNull (aRunnable, "Runnable");
    m_aResult = aResult;
  }

  @Nonnull
  public IThrowingRunnableWithParameter <PARAMTYPE, EXTYPE> getRunnable ()
  {
    return m_aRunnable;
  }

  @Nullable
  public DATATYPE getResult ()
  {
    return m_aResult;
  }

  @Nullable
  public DATATYPE call (final PARAMTYPE aParam) throws EXTYPE
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
   * @return The created {@link AdapterThrowingRunnableToCallableWithParameter}
   *         object.
   * @param <PARAMTYPE>
   *        The parameter type.
   * @param <EXTYPE>
   *        Exception type to be thrown
   */
  @Nonnull
  public static <PARAMTYPE, EXTYPE extends Exception> AdapterThrowingRunnableToCallableWithParameter <Object, PARAMTYPE, EXTYPE> createAdapter (@Nonnull final IThrowingRunnableWithParameter <PARAMTYPE, EXTYPE> aRunnable)
  {
    return new AdapterThrowingRunnableToCallableWithParameter <Object, PARAMTYPE, EXTYPE> (aRunnable);
  }

  /**
   * Create a callable that always returns the passed value.
   *
   * @param aRunnable
   *        The runnable to be executed.
   * @param aResult
   *        The expected result from calling {@link IThrowingCallable#call()} .
   *        May be <code>null</code>.
   * @return The created {@link AdapterThrowingRunnableToCallableWithParameter}
   *         object.
   * @param <DATATYPE>
   *        The callable result type.
   * @param <PARAMTYPE>
   *        The parameter type.
   * @param <EXTYPE>
   *        Exception type to be thrown
   */
  @Nonnull
  public static <DATATYPE, PARAMTYPE, EXTYPE extends Exception> AdapterThrowingRunnableToCallableWithParameter <DATATYPE, PARAMTYPE, EXTYPE> createAdapter (@Nonnull final IThrowingRunnableWithParameter <PARAMTYPE, EXTYPE> aRunnable,
                                                                                                                                                            @Nullable final DATATYPE aResult)
  {
    return new AdapterThrowingRunnableToCallableWithParameter <DATATYPE, PARAMTYPE, EXTYPE> (aRunnable, aResult);
  }
}
