package com.helger.commons.deadlock;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.lang.StackTraceHelper;

public class LoggingThreadDeadlockListener implements IThreadDeadlockListener
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (LoggingThreadDeadlockListener.class);

  public void onDeadlockDetected (@Nonnull final ThreadDeadlockInfo [] aDeadlockedThreads)
  {
    s_aLogger.error (aDeadlockedThreads.length + " deadlocked threads:");
    for (final ThreadDeadlockInfo aThreadInformation : aDeadlockedThreads)
    {
      final Thread aThread = aThreadInformation.getThread ();

      final StringBuilder aMsg = new StringBuilder ();
      aMsg.append ('\n')
          .append (aThread.toString ())
          .append (": ")
          .append (StackTraceHelper.getStackAsString (aThread.getStackTrace ()));

      s_aLogger.error (aMsg.toString ());
    }
  }
}
