package com.helger.commons.concurrent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Function;

import javax.annotation.Nonnull;

import org.junit.Test;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.mutable.MutableInt;
import com.helger.commons.timing.StopWatch;

public class PromiseFuncTest
{
  public static class Promise
  {
    private final ExecutorService m_aES;
    private CompletableFuture <?> m_aCF;

    public Promise ()
    {
      this (ForkJoinPool.commonPool ());
    }

    public Promise (@Nonnull final ExecutorService aES)
    {
      ValueEnforcer.notNull (aES, "ES");
      m_aES = aES;
    }

    @Nonnull
    public Promise runAsync (@Nonnull final Runnable aRunnable)
    {
      if (m_aCF != null)
        m_aCF = m_aCF.thenRunAsync (aRunnable, m_aES);
      else
        m_aCF = CompletableFuture.runAsync (aRunnable, m_aES);
      return this;
    }
  }

  @Test
  public void testCFRunAsync () throws Exception
  {
    final MutableInt aValue = new MutableInt (0);
    final Runnable r = () -> {
      System.out.println ("testCFRunAsync Thread: " + Thread.currentThread ().getId ());
      ThreadHelper.sleep (300);
      aValue.inc ();
    };
    final StopWatch aSW = StopWatch.createdStarted ();
    CompletableFuture.runAsync (r).thenRunAsync (r).thenRunAsync (r).get ();
    aSW.stop ();
    assertEquals (3, aValue.intValue ());
    assertTrue (aSW.getMillis () > 900);
  }

  @Test
  public void testCFApplyAsync () throws Exception
  {
    final Function <MutableInt, MutableInt> f = x -> {
      System.out.println ("testCFApplyAsync Thread: " + Thread.currentThread ().getId ());
      ThreadHelper.sleep (300);
      return new MutableInt (x.intValue () + 1);
    };
    final StopWatch aSW = StopWatch.createdStarted ();
    final MutableInt aMI = CompletableFuture.supplyAsync ( () -> f.apply (new MutableInt (0)))
                                            .thenApplyAsync (f)
                                            .thenApplyAsync (f)
                                            .get ();
    aSW.stop ();
    assertEquals (3, aMI.intValue ());
    assertTrue (aSW.getMillis () > 900);
  }

  @Test
  public void testCFApplySync () throws Exception
  {
    final Function <MutableInt, MutableInt> f = x -> {
      System.out.println ("testCFApplySync Thread: " + Thread.currentThread ().getId ());
      ThreadHelper.sleep (300);
      return new MutableInt (x.intValue () + 1);
    };
    final StopWatch aSW = StopWatch.createdStarted ();
    final MutableInt aMI = CompletableFuture.supplyAsync ( () -> f.apply (new MutableInt (0)))
                                            .thenApply (f)
                                            .thenApply (f)
                                            .get ();
    aSW.stop ();
    assertEquals (3, aMI.intValue ());
    assertTrue (aSW.getMillis () > 900);
  }
}
