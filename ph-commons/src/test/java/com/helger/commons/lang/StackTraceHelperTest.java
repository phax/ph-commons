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
package com.helger.commons.lang;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.helger.commons.collection.ArrayHelper;

/**
 * Test class for class {@link StackTraceHelper}.
 *
 * @author Philip Helger
 */
public final class StackTraceHelperTest
{
  private static class Root
  {
    public int getX ()
    {
      throw new UnsupportedOperationException ();
    }
  }

  private static class Derived extends Root
  {
    public int getY ()
    {
      try
      {
        return getX ();
      }
      catch (final UnsupportedOperationException ex)
      {
        throw new IllegalArgumentException ("Mock", ex);
      }
    }
  }

  @Test
  public void testAll ()
  {
    assertNotNull (StackTraceHelper.getStackAsString (Thread.currentThread ()));
    assertNotNull (StackTraceHelper.getStackAsString (new Exception ()));
    assertNotNull (StackTraceHelper.getStackAsString (new Exception (new Exception ())));
    assertNotNull (StackTraceHelper.getStackAsString (new Exception (), false));
    assertNotNull (StackTraceHelper.getStackAsString (new Exception ().getStackTrace ()));
    assertNotNull (StackTraceHelper.getStackAsString (new Exception ().getStackTrace (), false));
    assertEquals ("", StackTraceHelper.getStackAsString ((Throwable) null, false));

    // AppServer stacktrace :)
    final StackTraceElement [] ste = ArrayHelper.newArray (new StackTraceElement ("org.eclipse.jetty.Server",
                                                                                  "start",
                                                                                  "Server.java",
                                                                                  100));
    assertNotNull (StackTraceHelper.getStackAsString (ste));

    try
    {
      new Root ().getX ();
      fail ();
    }
    catch (final UnsupportedOperationException ex)
    {
      assertNotNull (StackTraceHelper.getStackAsString (ex));
    }
    try
    {
      new Derived ().getY ();
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {
      assertNotNull (StackTraceHelper.getStackAsString (ex));
    }
    assertTrue (StackTraceHelper.containsUnitTestElement (new Exception ().getStackTrace ()));
    new Thread ( () -> {
      assertNotNull (StackTraceHelper.getCurrentThreadStackAsString ());
      assertFalse (StackTraceHelper.containsUnitTestElement (new Exception ().getStackTrace ()));
    }).start ();
    final StringBuilder aSB = new StringBuilder ();
    StackTraceHelper.appendStackToString (aSB, new Exception ().getStackTrace ());
    assertTrue (aSB.length () > 0);
    assertNotNull (StackTraceHelper.getCurrentThreadStackAsString ());
  }
}
