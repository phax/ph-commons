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
package com.helger.base.log;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.concurrent.Immutable;
import com.helger.base.tostring.ToStringGenerator;

/**
 * An {@link ILogHandler} that discards all messages.
 *
 * @author Philip Helger
 * @since 12.5.2
 */
@Immutable
public final class NoOpLogHandler implements ILogHandler
{
  public static final NoOpLogHandler INSTANCE = new NoOpLogHandler ();

  private NoOpLogHandler ()
  {}

  public void trace (@NonNull final String sMsg, @Nullable final Throwable aThrowable)
  {}

  public void debug (@NonNull final String sMsg, @Nullable final Throwable aThrowable)
  {}

  public void info (@NonNull final String sMsg, @Nullable final Throwable aThrowable)
  {}

  public void warn (@NonNull final String sMsg, @Nullable final Throwable aThrowable)
  {}

  public void error (@NonNull final String sMsg, @Nullable final Throwable aThrowable)
  {}

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).getToString ();
  }
}
