/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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
package com.helger.commons.utils;

import static org.junit.Assert.fail;

import java.util.concurrent.Callable;

import org.junit.Test;

import com.helger.commons.callback.IThrowingRunnable;
import com.helger.commons.mock.exception.MockException;
import com.helger.commons.mock.exception.MockRuntimeException;
import com.helger.commons.util.MainRunner;

/**
 * Test class for {@link MainRunner}
 * 
 * @author Philip Helger
 */
public final class MainRunnerTest
{
  @Test
  public void testRunnable ()
  {
    MainRunner.run (new Runnable ()
    {
      public void run ()
      {}
    });

    MainRunner.run (new Runnable ()
    {
      public void run ()
      {
        throw new MockRuntimeException ();
      }
    });

    try
    {
      MainRunner.run ((Runnable) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    try
    {
      MainRunner.run ((Callable <Object>) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testThrowingRunnable ()
  {
    MainRunner.run (new IThrowingRunnable ()
    {
      public void run ()
      {}
    });

    MainRunner.run (new IThrowingRunnable ()
    {
      public void run () throws MockException
      {
        throw new MockException ();
      }
    });

    try
    {
      MainRunner.run ((IThrowingRunnable) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }
}
