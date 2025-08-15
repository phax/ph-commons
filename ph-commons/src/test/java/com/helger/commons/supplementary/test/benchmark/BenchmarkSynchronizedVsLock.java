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
package com.helger.commons.supplementary.test.benchmark;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.helger.base.concurrent.ExecutorServiceHelper;
import com.helger.base.system.SystemHelper;

/**
 * Check if there is any difference between the different locking methods.
 */
public final class BenchmarkSynchronizedVsLock extends AbstractBenchmarkTask
{
  private BenchmarkSynchronizedVsLock ()
  {}

  public static void main (final String [] aArgs)
  {
    logSystemInfo ();
    _run ();
  }

  private static Runnable _getRunnable (final int nThreads, final AbstractBase aObj)
  {
    if (nThreads == 1)
      return aObj;

    return () -> {
      final ExecutorService aExecSvc = Executors.newFixedThreadPool (nThreads);
      for (int i = 0; i < nThreads; ++i)
        aExecSvc.submit (aObj);
      ExecutorServiceHelper.shutdownAndWaitUntilAllTasksAreFinished (aExecSvc);
    };
  }

  private static void _run ()
  {
    for (int i = 1; i <= SystemHelper.getNumberOfProcessors () * 2; ++i)
    {
      double dTime = benchmarkTask (_getRunnable (i, new UseSynchronizedMethod ()));
      LOGGER.info ("Time using synchronized method[" + i + "]:               " + dTime + " ns");

      dTime = benchmarkTask (_getRunnable (i, new UseSynchronizedBlock ()));
      LOGGER.info ("Time using synchronized block[" + i + "]:                " + dTime + " ns");

      dTime = benchmarkTask (_getRunnable (i, new UseUnfairLock ()));
      LOGGER.info ("Time using unfair Lock[" + i + "]:                       " + dTime + " ns");

      dTime = benchmarkTask (_getRunnable (i, new UseFairLock ()));
      LOGGER.info ("Time using fair Lock[" + i + "]:                         " + dTime + " ns");

      dTime = benchmarkTask (_getRunnable (i, new UseUnfairReadLock ()));
      LOGGER.info ("Time using unfair ReadWriteLock.readLock ()[" + i + "]:  " + dTime + " ns");

      dTime = benchmarkTask (_getRunnable (i, new UseFairReadLock ()));
      LOGGER.info ("Time using fair ReadWriteLock.readLock ()[" + i + "]:    " + dTime + " ns");

      dTime = benchmarkTask (_getRunnable (i, new UseUnfairWriteLock ()));
      LOGGER.info ("Time using unfair ReadWriteLock.writeLock ()[" + i + "]: " + dTime + " ns");

      dTime = benchmarkTask (_getRunnable (i, new UseFairWriteLock ()));
      LOGGER.info ("Time using fair ReadWriteLock.writeLock ()[" + i + "]:   " + dTime + " ns");
    }
  }

  protected abstract static class AbstractBase implements Runnable
  {
    private int m_nRuns = 10000;

    protected AbstractBase ()
    {}

    protected final void performAction ()
    {
      m_nRuns--;
    }

    public abstract void performThreadSafeAction ();

    public final void run ()
    {
      while (m_nRuns > 0)
        performThreadSafeAction ();
    }
  }

  private static final class UseSynchronizedMethod extends AbstractBase
  {
    @Override
    public synchronized void performThreadSafeAction ()
    {
      performAction ();
    }
  }

  private static final class UseSynchronizedBlock extends AbstractBase
  {
    @Override
    public void performThreadSafeAction ()
    {
      synchronized (this)
      {
        performAction ();
      }
    }
  }

  private static final class UseUnfairLock extends AbstractBase
  {
    private final Lock m_aLock = new ReentrantLock (false);

    @Override
    public void performThreadSafeAction ()
    {
      m_aLock.lock ();
      try
      {
        performAction ();
      }
      finally
      {
        m_aLock.unlock ();
      }
    }
  }

  private static final class UseFairLock extends AbstractBase
  {
    private final Lock m_aLock = new ReentrantLock (true);

    @Override
    public void performThreadSafeAction ()
    {
      m_aLock.lock ();
      try
      {
        performAction ();
      }
      finally
      {
        m_aLock.unlock ();
      }
    }
  }

  private static final class UseUnfairReadLock extends AbstractBase
  {
    private final ReadWriteLock m_aRWLock = new ReentrantReadWriteLock (false);

    @Override
    public void performThreadSafeAction ()
    {
      m_aRWLock.readLock ().lock ();
      try
      {
        performAction ();
      }
      finally
      {
        m_aRWLock.readLock ().unlock ();
      }
    }
  }

  private static final class UseFairReadLock extends AbstractBase
  {
    private final ReadWriteLock m_aRWLock = new ReentrantReadWriteLock (true);

    @Override
    public void performThreadSafeAction ()
    {
      m_aRWLock.readLock ().lock ();
      try
      {
        performAction ();
      }
      finally
      {
        m_aRWLock.readLock ().unlock ();
      }
    }
  }

  private static final class UseUnfairWriteLock extends AbstractBase
  {
    private final ReadWriteLock m_aRWLock = new ReentrantReadWriteLock (false);

    @Override
    public void performThreadSafeAction ()
    {
      m_aRWLock.writeLock ().lock ();
      try
      {
        performAction ();
      }
      finally
      {
        m_aRWLock.writeLock ().unlock ();
      }
    }
  }

  private static final class UseFairWriteLock extends AbstractBase
  {
    private final ReadWriteLock m_aRWLock = new ReentrantReadWriteLock (true);

    @Override
    public void performThreadSafeAction ()
    {
      m_aRWLock.writeLock ().lock ();
      try
      {
        performAction ();
      }
      finally
      {
        m_aRWLock.writeLock ().unlock ();
      }
    }
  }
}
