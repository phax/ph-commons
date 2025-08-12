/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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
package com.helger.commons.callback.exception;

import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.commons.string.ToStringGenerator;

import jakarta.annotation.Nullable;

/**
 * A specific implementation of the {@link IExceptionCallback} interface, that
 * stores the last exception.
 *
 * @author Philip Helger
 * @param <EXTYPE>
 *        The exception type to be handled
 */
@NotThreadSafe
public class CollectingExceptionCallback <EXTYPE extends Throwable> implements IExceptionCallback <EXTYPE>
{
  private EXTYPE m_aException;

  public void onException (@Nullable final EXTYPE aEx)
  {
    m_aException = aEx;
  }

  public boolean hasException ()
  {
    return m_aException != null;
  }

  @Nullable
  public EXTYPE getException ()
  {
    return m_aException;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("exception", m_aException).getToString ();
  }
}
