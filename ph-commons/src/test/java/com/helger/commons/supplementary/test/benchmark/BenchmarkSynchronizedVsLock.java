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
package com.helger.commons.supplementary.test.benchmark;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.helger.commons.concurrent.ManagedExecutorService;
import com.helger.commons.system.SystemHelper;

/**
 * Check if there is any difference between the different locking methods.
 */
public final class BenchmarkSynchronizedVsLock extends AbstractBenchmarkTask
{
  private BenchmarkSynchronizedVsLock ()
  {}

  public static void main (final String [] aArgs) throws Exception
  {
    logSystemInfo ();
    findWhetherSynchronizedOrLockAreFaster ();
  }

  private static Runnable _getRunnable (final int nThreads, final BaseClass aObj)
  {
    if (nThreads == 1)
      return aObj;

    return () -> {
      final ExecutorService aExecSvc = Executors.newFixedThreadPool (nThreads);
      for (int i = 0; i < nThreads; ++i)
        aExecSvc.submit (aObj);
      new ManagedExecutorService (aExecSvc).shutdownAndWaitUntilAllTasksAreFinished ();
    };
  }

  private static void findWhetherSynchronizedOrLockAreFaster ()
  {
    for (int i = 1; i <= SystemHelper.getNumberOfProcessors () * 2; ++i)
    {
      double dTime = benchmarkTask (_getRunnable (i, new UseSynchronizedMethod ()));
      System.out.println ("Time using synchronized method[" + i + "]:               " + dTime + " ns");

      dTime = benchmarkTask (_getRunnable (i, new UseSynchronizedBlock ()));
      System.out.println ("Time using synchronized block[" + i + "]:                " + dTime + " ns");

      dTime = benchmarkTask (_getRunnable (i, new UseUnfairLock ()));
      System.out.println ("Time using unfair Lock[" + i + "]:                       " + dTime + " ns");

      dTime = benchmarkTask (_getRunnable (i, new UseFairLock ()));
      System.out.println ("Time using fair Lock[" + i + "]:                         " + dTime + " ns");

      dTime = benchmarkTask (_getRunnable (i, new UseUnfairReadLock ()));
      System.out.println ("Time using unfair ReadWriteLock.readLock ()[" + i + "]:  " + dTime + " ns");

      dTime = benchmarkTask (_getRunnable (i, new UseFairReadLock ()));
      System.out.println ("Time using fair ReadWriteLock.readLock ()[" + i + "]:    " + dTime + " ns");

      dTime = benchmarkTask (_getRunnable (i, new UseUnfairWriteLock ()));
      System.out.println ("Time using unfair ReadWriteLock.writeLock ()[" + i + "]: " + dTime + " ns");

      dTime = benchmarkTask (_getRunnable (i, new UseFairWriteLock ()));
      System.out.println ("Time using fair ReadWriteLock.writeLock ()[" + i + "]:   " + dTime + " ns");
    }
  }

  protected abstract static class BaseClass implements Runnable
  {
    private int m_nRuns = 10000;

    public BaseClass ()
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

  private static final class UseSynchronizedMethod extends BaseClass
  {
    @Override
    public synchronized void performThreadSafeAction ()
    {
      performAction ();
    }
  }

  private static final class UseSynchronizedBlock extends BaseClass
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

  private static final class UseUnfairLock extends BaseClass
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

  private static final class UseFairLock extends BaseClass
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

  private static final class UseUnfairReadLock extends BaseClass
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

  private static final class UseFairReadLock extends BaseClass
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

  private static final class UseUnfairWriteLock extends BaseClass
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

  private static final class UseFairWriteLock extends BaseClass
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
