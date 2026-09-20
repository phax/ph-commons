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

import org.junit.Test;

import com.helger.collection.commons.ICommonsList;
import com.helger.xml.microdom.IMicroElement;

/**
 * Test class for class {@link ThreadDescriptorList}.
 *
 * @author Philip Helger
 */
public final class ThreadDescriptorListTest
{
  @Test
  public void testEmpty ()
  {
    final ThreadDescriptorList aList = new ThreadDescriptorList ();
    assertTrue (aList.getAllDescriptors ().isEmpty ());
    assertNull (aList.getError ());

    final String sStr = aList.getAsString ();
    assertTrue (sStr.contains ("Total thread count: 0"));
    // Every state is emitted, also the empty ones
    for (final State eState : State.values ())
      assertTrue (sStr.contains ("Thread state " + eState + " [0]"));

    final IMicroElement eList = aList.getAsMicroNode ();
    assertEquals ("threadlist", eList.getTagName ());
    assertEquals ("0", eList.getAttributeValue ("threadcount"));
    assertNull (eList.getFirstChildElement ("error"));
    assertEquals (State.values ().length, eList.getAllChildElements ("threadstate").size ());
  }

  @Test
  public void testWithDescriptors ()
  {
    final ThreadDescriptor aDesc = ThreadDescriptor.createForCurrentThread (null);

    final ThreadDescriptorList aList = new ThreadDescriptorList ();
    assertSame (aList, aList.addDescriptor (aDesc));

    final ICommonsList <ThreadDescriptor> aAll = aList.getAllDescriptors ();
    assertEquals (1, aAll.size ());
    assertSame (aDesc, aAll.getFirstOrNull ());
    // Must be a copy
    assertTrue (aList.getAllDescriptors () != aAll);

    final String sStr = aList.getAsString ();
    assertTrue (sStr.contains ("Total thread count: 1"));
    assertTrue (sStr.contains ("Thread state RUNNABLE [1]"));
    assertTrue (sStr.contains ("Thread state BLOCKED [0]"));

    final IMicroElement eList = aList.getAsMicroNode ();
    assertEquals ("1", eList.getAttributeValue ("threadcount"));
    assertEquals (1, eList.getAllChildElements ("thread").size ());
  }

  @Test
  public void testError ()
  {
    final ThreadDescriptorList aList = new ThreadDescriptorList ();
    assertSame (aList, aList.setError ("any error"));
    assertEquals ("any error", aList.getError ());

    assertTrue (aList.getAsString ().startsWith ("ERROR retrieving all thread stack traces: any error"));

    final IMicroElement eError = aList.getAsMicroNode ().getFirstChildElement ("error");
    assertNotNull (eError);
    assertEquals ("any error", eError.getTextContent ());

    assertSame (aList, aList.setError (null));
    assertNull (aList.getError ());
    assertNull (aList.getAsMicroNode ().getFirstChildElement ("error"));
  }

  @Test
  public void testCreateWithAllThreads ()
  {
    final ThreadDescriptorList aList = ThreadDescriptorList.createWithAllThreads ();
    assertNotNull (aList);
    // At least the current thread must be contained
    assertTrue (aList.getAllDescriptors ().isNotEmpty ());
    assertNotNull (aList.getAsString ());
    assertNotNull (aList.getAsMicroNode ());
  }

  @Test
  public void testInvalidParams ()
  {
    try
    {
      new ThreadDescriptorList ().addDescriptor (null);
      fail ();
    }
    catch (final NullPointerException | IllegalArgumentException ex)
    {
      // expected
    }
  }
}
