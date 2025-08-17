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
package com.helger.base.callback.exception;

import java.util.ArrayList;
import java.util.List;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.tostring.ToStringGenerator;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * A specific implementation of the {@link IExceptionCallback} interface, that stores all
 * exceptions.
 *
 * @author Philip Helger
 * @param <EXTYPE>
 *        The exception type to be handled
 */
@NotThreadSafe
public class CollectingExceptionListCallback <EXTYPE extends Throwable> implements IExceptionCallback <EXTYPE>
{
  private final List <EXTYPE> m_aExceptions = new ArrayList <> ();

  public void onException (@Nullable final EXTYPE aEx)
  {
    if (aEx != null)
      m_aExceptions.add (aEx);
  }

  public boolean hasException ()
  {
    return !m_aExceptions.isEmpty ();
  }

  @Nonnegative
  public int getExceptionCount ()
  {
    return m_aExceptions.size ();
  }

  @Nonnull
  @ReturnsMutableCopy
  public List <EXTYPE> getAllExceptions ()
  {
    return new ArrayList <> (m_aExceptions);
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("Exceptions", m_aExceptions).getToString ();
  }
}
