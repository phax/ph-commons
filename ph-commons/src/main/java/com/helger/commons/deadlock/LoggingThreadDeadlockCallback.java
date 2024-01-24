/*
 * Copyright (C) 2014-2024 Philip Helger (www.helger.com)
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
package com.helger.commons.deadlock;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.lang.StackTraceHelper;

/**
 * A logging implementation of {@link IThreadDeadlockCallback}.
 *
 * @author Philip Helger
 */
public class LoggingThreadDeadlockCallback implements IThreadDeadlockCallback
{
  private static final Logger LOGGER = LoggerFactory.getLogger (LoggingThreadDeadlockCallback.class);

  public void onDeadlockDetected (@Nonnull final ThreadDeadlockInfo [] aDeadlockedThreads)
  {
    final StringBuilder aMsg = new StringBuilder ();
    aMsg.append (aDeadlockedThreads.length).append (" deadlocked threads:\n");
    for (final ThreadDeadlockInfo aThreadInformation : aDeadlockedThreads)
    {
      final Thread aThread = aThreadInformation.getThread ();
      aMsg.append ('\n')
          .append (aThread.toString ())
          .append (":\n")
          .append (StackTraceHelper.getStackAsString (aThread));
    }
    LOGGER.error (aMsg.toString ());
  }
}
