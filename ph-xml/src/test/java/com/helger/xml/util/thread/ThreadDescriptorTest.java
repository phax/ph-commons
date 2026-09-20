/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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
package com.helger.xml.util.thread;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.Thread.State;

import org.junit.After;
import org.junit.Test;

import com.helger.xml.microdom.IMicroElement;

/**
 * Test class for class {@link ThreadDescriptor}.
 *
 * @author Philip Helger
 */
public final class ThreadDescriptorTest
{
  @After
  public void restoreDefaults ()
  {
    ThreadDescriptor.setEnableThreadInfo (ThreadDescriptor.DEFAULT_ENABLE_THREAD_INFO);
  }

  @Test
  public void testBasic ()
  {
    final Thread aThread = Thread.currentThread ();
    final ThreadDescriptor aDesc = new ThreadDescriptor (aThread, "the stack trace\n");

    assertEquals (aThread.getId (), aDesc.getThreadID ());
    assertSame (State.RUNNABLE, aDesc.getThreadState ());
    assertEquals ("the stack trace\n", aDesc.getStackTrace ());
    assertEquals ("the stack trace\n", aDesc.getStackTraceNotNull ());

    final String sDescriptor = aDesc.getDescriptor ();
    assertTrue (sDescriptor.startsWith ("Thread[" + aThread.getId () + "]"));
    assertTrue (sDescriptor.contains (aThread.getName ()));

    // No ThreadInfo by default
    assertEquals ("", aDesc.getLockInfo ());
    assertTrue (aDesc.getAsString ().contains ("the stack trace"));
  }

  @Test
  public void testWithoutStackTrace ()
  {
    final ThreadDescriptor aDesc = new ThreadDescriptor (Thread.currentThread (), null);
    assertNull (aDesc.getStackTrace ());
    assertEquals ("No stack trace available\n", aDesc.getStackTraceNotNull ());
  }

  @Test
  public void testGetAsMicroNode ()
  {
    final ThreadDescriptor aDesc = new ThreadDescriptor (Thread.currentThread (), "the stack trace\n");
    final IMicroElement eThread = aDesc.getAsMicroNode ();
    assertNotNull (eThread);
    assertEquals ("thread", eThread.getTagName ());
    assertEquals (Long.toString (Thread.currentThread ().getId ()), eThread.getAttributeValue ("id"));
    assertEquals (Thread.currentThread ().getName (), eThread.getAttributeValue ("name"));
    assertEquals (State.RUNNABLE.toString (), eThread.getAttributeValue ("state"));
    assertNotNull (eThread.getAttributeValue ("priority"));
    assertNotNull (eThread.getAttributeValue ("threadgroup"));
    assertNotNull (eThread.getFirstChildElement ("stacktrace"));
    // Present only if the ThreadInfo is enabled
    assertNull (eThread.getFirstChildElement ("threadinfo"));
  }

  @Test
  public void testEnableThreadInfo ()
  {
    assertEquals (Boolean.valueOf (ThreadDescriptor.DEFAULT_ENABLE_THREAD_INFO),
                  Boolean.valueOf (ThreadDescriptor.isEnableThreadInfo ()));

    ThreadDescriptor.setEnableThreadInfo (true);
    assertTrue (ThreadDescriptor.isEnableThreadInfo ());

    final ThreadDescriptor aDesc = new ThreadDescriptor (Thread.currentThread (), "the stack trace\n");
    // The lock info is now evaluated - the content depends on the runtime
    assertNotNull (aDesc.getLockInfo ());
    assertNotNull (aDesc.getAsMicroNode ().getFirstChildElement ("threadinfo"));
  }

  @Test
  public void testCreateForCurrentThread ()
  {
    final ThreadDescriptor aDesc = ThreadDescriptor.createForCurrentThread (null);
    assertNotNull (aDesc);
    assertEquals (Thread.currentThread ().getId (), aDesc.getThreadID ());
    assertNotNull (aDesc.getStackTraceNotNull ());

    final ThreadDescriptor aDesc2 = ThreadDescriptor.createForCurrentThread (new IllegalStateException ("mock"));
    assertNotNull (aDesc2.getStackTraceNotNull ());
  }

  @Test
  public void testInvalidParams ()
  {
    try
    {
      new ThreadDescriptor (null, "any");
      fail ();
    }
    catch (final NullPointerException | IllegalArgumentException ex)
    {
      // expected
    }
  }
}
