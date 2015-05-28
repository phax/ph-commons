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
  private static final Logger s_aLogger = LoggerFactory.getLogger (LoggingThreadDeadlockCallback.class);

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
          .append (StackTraceHelper.getStackAsString (aThread.getStackTrace ()));
    }

    s_aLogger.error (aMsg.toString ());
  }
}
