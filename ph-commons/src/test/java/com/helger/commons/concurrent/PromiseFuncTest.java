/**
 * Copyright (C) 2014-2020 Philip Helger (www.helger.com)
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
package com.helger.commons.concurrent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Function;

import javax.annotation.Nonnull;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.mutable.MutableInt;
import com.helger.commons.timing.StopWatch;

public class PromiseFuncTest
{
  private static final Logger LOGGER = LoggerFactory.getLogger (PromiseFuncTest.class);

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
      LOGGER.info ("testCFRunAsync Thread: " + Thread.currentThread ().getId ());
      ThreadHelper.sleep (300);
      aValue.inc ();
    };
    final StopWatch aSW = StopWatch.createdStarted ();
    CompletableFuture.runAsync (r).thenRunAsync (r).thenRunAsync (r).get ();
    aSW.stop ();
    assertEquals (3, aValue.intValue ());
    final long nMillis = aSW.getMillis ();
    assertTrue ("Took " + nMillis + " ms", nMillis >= 900);
  }

  @Test
  public void testCFApplyAsync () throws Exception
  {
    final Function <MutableInt, MutableInt> f = x -> {
      LOGGER.info ("testCFApplyAsync Thread: " + Thread.currentThread ().getId ());
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
    final long nMillis = aSW.getMillis ();
    assertTrue ("Took " + nMillis + " ms", nMillis >= 900);
  }

  @Test
  public void testCFApplySync () throws Exception
  {
    final Function <MutableInt, MutableInt> f = x -> {
      LOGGER.info ("testCFApplySync Thread: " + Thread.currentThread ().getId ());
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
    final long nMillis = aSW.getMillis ();
    assertTrue ("Took " + nMillis + " ms", nMillis >= 900);
  }
}
