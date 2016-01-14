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
package com.helger.commons.vminit;

import java.util.List;

import com.helger.commons.annotation.CodingStyleguideUnaware;
import com.helger.commons.annotation.PresentForCodeCoverage;
import com.helger.commons.cleanup.CommonsCleanup;
import com.helger.commons.exception.mock.IMockException;
import com.helger.commons.lang.ServiceLoaderHelper;

/**
 * This class should be run upon VM initialization. This should be the very
 * first thing to run.
 *
 * @author Philip Helger
 */
public final class VirtualMachineInitializer
{
  private static final List <IVirtualMachineEventSPI> s_aSPIs;
  private static volatile Thread s_aShutdownThread;

  static
  {
    // Get all SPI implementations
    s_aSPIs = ServiceLoaderHelper.getAllSPIImplementations (IVirtualMachineEventSPI.class);
  }

  @PresentForCodeCoverage
  private static final VirtualMachineInitializer s_aInstance = new VirtualMachineInitializer ();

  private VirtualMachineInitializer ()
  {}

  private static void _init ()
  {
    for (final IVirtualMachineEventSPI aSPI : s_aSPIs)
      try
      {
        aSPI.onVirtualMachineStart ();
      }
      catch (final Throwable t)
      {
        // Do not use Logger because this may interfere with the general
        // startup!
        System.err.println ("!!! Error running VM initializer SPI " + aSPI);
        if (!(t instanceof IMockException))
          t.printStackTrace (System.err);
      }
  }

  private static void _done ()
  {
    for (final IVirtualMachineEventSPI aSPI : s_aSPIs)
      try
      {
        aSPI.onVirtualMachineStop ();
      }
      catch (final Throwable t)
      {
        // Do not use Logger because this may interfere with the general
        // shutdown!
        System.err.println ("!!! Error running VM shutdown SPI " + aSPI);
        if (!(t instanceof IMockException))
          t.printStackTrace (System.err);
      }

    // Cleanup everything
    CommonsCleanup.cleanup ();

    // Help the GC
    s_aShutdownThread = null;
    s_aSPIs.clear ();
  }

  @CodingStyleguideUnaware ("FindBugs claims that we do need synchronized here!")
  public static synchronized void runInitialization ()
  {
    // Only if at least one implementing class is present!
    if (s_aSPIs != null && !s_aSPIs.isEmpty ())
    {
      if (s_aShutdownThread != null)
        throw new IllegalStateException ("Already initialized!");

      // Call the SPI implementors
      _init ();

      // Define what to do upon JVM exit
      s_aShutdownThread = new Thread ("VirtualMachineInitializer.shutdown")
      {
        @Override
        public void run ()
        {
          _done ();
        }
      };
      Runtime.getRuntime ().addShutdownHook (s_aShutdownThread);
    }
  }
}
