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
package com.helger.commons.concurrent;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;

import com.helger.base.concurrent.ExecutorServiceHelper;

/**
 * Test class for class {@link ExecutorServiceHelper}
 *
 * @author Philip Helger
 */
public final class ExecutorServiceHelperTest
{
  @Test
  public void testAll ()
  {
    final ExecutorService aExecSvc = Executors.newFixedThreadPool (3);
    aExecSvc.submit ( () -> {
      // empty
    });
    assertTrue (ExecutorServiceHelper.shutdownAndWaitUntilAllTasksAreFinished (aExecSvc).isNotInterrupted ());

    try
    {
      // null not allowed
      ExecutorServiceHelper.shutdownAndWaitUntilAllTasksAreFinished (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }
}
