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

import java.util.concurrent.Callable;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.callback.IThrowingCallable;
import com.helger.commons.callback.IThrowingCallableWithParameter;
import com.helger.commons.string.ToStringGenerator;

/**
 * A helper class that converts a {@link Callable} into an
 * {@link IThrowingCallableWithParameter}.
 *
 * @author Philip Helger
 * @param <DATATYPE>
 *        The callable result type.
 * @param <PARAMTYPE>
 *        The parameter type.
 * @param <EXTYPE>
 *        Exception type to be thrown
 */
@Immutable
public class AdapterCallableToCallableWithParam <DATATYPE, PARAMTYPE, EXTYPE extends Exception>
                                                implements IThrowingCallableWithParameter <DATATYPE, PARAMTYPE, EXTYPE>
{
  private final IThrowingCallable <DATATYPE, EXTYPE> m_aCallable;

  public AdapterCallableToCallableWithParam (@Nonnull final IThrowingCallable <DATATYPE, EXTYPE> aCallable)
  {
    m_aCallable = ValueEnforcer.notNull (aCallable, "Callable");
  }

  @Nonnull
  public IThrowingCallable <DATATYPE, EXTYPE> getCallable ()
  {
    return m_aCallable;
  }

  @Nonnull
  public DATATYPE call (@Nonnull final PARAMTYPE aParam) throws EXTYPE
  {
    return m_aCallable.call ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("callabale", m_aCallable).toString ();
  }
}
