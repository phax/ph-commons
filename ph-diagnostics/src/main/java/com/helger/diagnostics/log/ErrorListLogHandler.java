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
package com.helger.diagnostics.log;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.log.ILogHandler;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.diagnostics.error.SingleError;
import com.helger.diagnostics.error.list.ErrorList;

/**
 * An {@link ILogHandler} that collects all messages in an {@link ErrorList}. Info, warn and error
 * messages are stored with the matching error level and the optional throwable is stored as the
 * linked exception.
 *
 * @author Philip Helger
 * @since 12.5.2
 */
@NotThreadSafe
public class ErrorListLogHandler implements ILogHandler
{
  private final ErrorList m_aErrorList;

  /**
   * Constructor
   *
   * @param aErrorList
   *        The error list to add the messages to. May not be <code>null</code>.
   */
  public ErrorListLogHandler (@NonNull final ErrorList aErrorList)
  {
    ValueEnforcer.notNull (aErrorList, "ErrorList");
    m_aErrorList = aErrorList;
  }

  /**
   * @return The error list provided in the constructor. Never <code>null</code>.
   */
  @NonNull
  public final ErrorList getErrorList ()
  {
    return m_aErrorList;
  }

  public void trace (@NonNull final String sMsg, @Nullable final Throwable aThrowable)
  {
    // swallow
  }

  public void debug (@NonNull final String sMsg, @Nullable final Throwable aThrowable)
  {
    // swallow
  }

  public void info (@NonNull final String sMsg, @Nullable final Throwable aThrowable)
  {
    m_aErrorList.add (SingleError.builderInfo ().errorText (sMsg).linkedException (aThrowable).build ());
  }

  public void warn (@NonNull final String sMsg, @Nullable final Throwable aThrowable)
  {
    m_aErrorList.add (SingleError.builderWarn ().errorText (sMsg).linkedException (aThrowable).build ());
  }

  public void error (@NonNull final String sMsg, @Nullable final Throwable aThrowable)
  {
    m_aErrorList.add (SingleError.builderError ().errorText (sMsg).linkedException (aThrowable).build ());
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("ErrorList", m_aErrorList).getToString ();
  }
}
