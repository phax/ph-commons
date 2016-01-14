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
package com.helger.commons.supplementary.test;

import com.helger.commons.thread.ThreadHelper;

/**
 * Determine the maximum number of threads to be created. Warning: may
 * potentially crash your system :)
 *
 * @author Philip Helger
 */
public final class MainMaxThreadCount
{
  private MainMaxThreadCount ()
  {}

  private static class TestThread extends Thread
  {
    public TestThread ()
    {}

    @Override
    public void run ()
    {
      while (true)
        ThreadHelper.sleep (60000);
    }
  }

  public static void main (final String [] aArgs)
  {
    long nCount = 0;
    while (true)
    {
      new TestThread ().start ();
      System.out.println ("Started #" + ++nCount);
    }
  }
}
