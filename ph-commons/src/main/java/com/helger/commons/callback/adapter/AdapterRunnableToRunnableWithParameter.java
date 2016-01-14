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
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.callback.INonThrowingRunnableWithParameter;
import com.helger.commons.callback.IThrowingRunnableWithParameter;
import com.helger.commons.string.ToStringGenerator;

/**
 * A helper class that converts a {@link Runnable} into an
 * {@link IThrowingRunnableWithParameter}.
 *
 * @author Philip Helger
 * @param <PARAMTYPE>
 *        The parameter type.
 */
@Immutable
public class AdapterRunnableToRunnableWithParameter <PARAMTYPE> implements INonThrowingRunnableWithParameter <PARAMTYPE>
{
  private final Runnable m_aRunnable;

  public AdapterRunnableToRunnableWithParameter (@Nonnull final Runnable aRunnable)
  {
    m_aRunnable = ValueEnforcer.notNull (aRunnable, "Runnable");
  }

  @Nonnull
  public Runnable getRunnable ()
  {
    return m_aRunnable;
  }

  public void run (final PARAMTYPE aParam)
  {
    m_aRunnable.run ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("runnable", m_aRunnable).toString ();
  }
}
