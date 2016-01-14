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

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public class MainBuildThreadGroupTree
{
  @SuppressFBWarnings ("IL_INFINITE_LOOP")
  private static String _getTGName (final ThreadGroup aParamTG)
  {
    ThreadGroup aTG = aParamTG;
    final StringBuilder ret = new StringBuilder ();
    while (aTG != null)
    {
      if (ret.length () > 0)
        ret.insert (0, "/");
      ret.insert (0, aTG.getName ());
      aTG = aTG.getParent ();
    }
    return ret.toString ();
  }

  public static void main (final String [] args)
  {
    /*
     * Enumerate all threads and thread groups in the system. See the sample in
     * The Java Class Libraries, volume 1 in the Thread description.
     */
    ThreadGroup rootGroup = Thread.currentThread ().getThreadGroup ();
    while (rootGroup.getParent () != null)
      rootGroup = rootGroup.getParent ();

    /*
     * Create a vector that contains all threads in all groups.
     */
    final Thread [] threads = new Thread [rootGroup.activeCount ()];
    final int threadCount = rootGroup.enumerate (threads, true);
    /*
     * Copy the thread information into a string vector.
     */
    final String [] threadInfo = new String [threadCount];
    for (int i = 0; i < threadCount; i++)
    {
      final Thread t = threads[i];
      final StringBuilder state = new StringBuilder (t.isAlive () ? "Alive" : "Dormant");
      if (t.isInterrupted ())
        state.append (", Interrupted");
      if (t.isDaemon ())
        state.append (", Daemon");
      threadInfo[i] = _getTGName (t.getThreadGroup ()) +
                      "[" +
                      t.getName () +
                      "], " +
                      "Priority: " +
                      t.getPriority () +
                      ", " +
                      state.toString ();
    }
    /* Dump the vector */
    for (final String s : threadInfo)
      System.out.println (s);
  }
}
