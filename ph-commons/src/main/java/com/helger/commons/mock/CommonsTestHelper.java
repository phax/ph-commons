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
package com.helger.commons.mock;

import static org.junit.Assert.fail;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.PresentForCodeCoverage;
import com.helger.base.concurrent.ExecutorServiceHelper;
import com.helger.base.equals.ValueEnforcer;
import com.helger.base.iface.IThrowingRunnable;
import com.helger.base.string.StringImplode;
import com.helger.collection.commons.CommonsVector;
import com.helger.collection.commons.ICommonsList;
import com.helger.commons.lang.StackTraceHelper;

import jakarta.annotation.Nonnull;

/**
 * This class contains default test methods to test the correctness of implementations of standard
 * methods.
 *
 * @author Philip Helger
 */
@Immutable
public final class CommonsTestHelper
{
  @PresentForCodeCoverage
  private static final CommonsTestHelper INSTANCE = new CommonsTestHelper ();

  private CommonsTestHelper ()
  {}

  /**
   * Run something in parallel
   *
   * @param nCalls
   *        The number of invocations of the passed runnable. Must be &ge; 0.
   * @param aRunnable
   *        The runnable to execute. May not be <code>null</code>.
   */
  public static void testInParallel (@Nonnegative final int nCalls,
                                     @Nonnull final IThrowingRunnable <? extends Exception> aRunnable)
  {
    ValueEnforcer.isGE0 (nCalls, "Calls");
    ValueEnforcer.notNull (aRunnable, "Runnable");

    // More than 20s thread would be overkill!
    final ExecutorService aES = Executors.newFixedThreadPool (20);
    final ICommonsList <String> aErrors = new CommonsVector <> ();
    for (int i = 0; i < nCalls; ++i)
    {
      aES.submit ( () -> {
        try
        {
          aRunnable.run ();
        }
        catch (final Exception ex)
        {
          // Remember thread stack
          aErrors.add (ex.getMessage () + "\n" + StackTraceHelper.getStackAsString (ex));
        }
      });
    }
    ExecutorServiceHelper.shutdownAndWaitUntilAllTasksAreFinished (aES);

    // No errors should have occurred
    if (!aErrors.isEmpty ())
      fail (StringImplode.getImploded (aErrors));
  }
}
