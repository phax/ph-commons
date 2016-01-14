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
package com.helger.commons.microdom;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.helger.commons.mock.CommonsTestHelper;

/**
 * Test class for class {@link MicroEvent}.
 *
 * @author Philip Helger
 */
public final class MicroEventTest
{
  @Test
  public void testBasic ()
  {
    final IMicroElement e1 = new MicroElement ("a1");
    final IMicroElement e2 = new MicroElement ("a1");
    MicroEvent e = new MicroEvent (EMicroEvent.NODE_INSERTED, e1, e2);
    assertEquals (EMicroEvent.NODE_INSERTED, e.getEventType ());
    assertSame (e1, e.getSourceNode ());
    assertSame (e2, e.getTargetNode ());
    CommonsTestHelper.testToStringImplementation (e);

    e = new MicroEvent (EMicroEvent.NODE_INSERTED, null, null);
    assertEquals (EMicroEvent.NODE_INSERTED, e.getEventType ());
    assertNull (e.getSourceNode ());
    assertNull (e.getTargetNode ());
    CommonsTestHelper.testToStringImplementation (e);

    try
    {
      new MicroEvent (null, e1, e2);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testInsertedEvent ()
  {
    final MockMicroEventListener aIEL = new MockMicroEventListener (EMicroEvent.NODE_INSERTED);
    final IMicroDocument aDoc = new MicroDocument ();
    assertFalse (aDoc.unregisterEventTarget (EMicroEvent.NODE_INSERTED, aIEL).isChanged ());
    assertTrue (aDoc.registerEventTarget (EMicroEvent.NODE_INSERTED, aIEL).isChanged ());
    assertFalse (aDoc.registerEventTarget (EMicroEvent.NODE_INSERTED, aIEL).isChanged ());
    IMicroElement eRoot = null;
    try
    {
      assertEquals (0, aIEL.getInvocationCount ());
      // Direct invoke!
      eRoot = aDoc.appendElement ("root_element");
      assertEquals (1, aIEL.getInvocationCount ());
      // Recursive invoke!
      eRoot.appendElement ("root_element");
      assertEquals (2, aIEL.getInvocationCount ());
      // Simple create a text node
      eRoot.appendText ("My Text node");
      assertEquals (3, aIEL.getInvocationCount ());
    }
    finally
    {
      // unregister
      aDoc.unregisterEventTarget (EMicroEvent.NODE_INSERTED, aIEL);
    }

    // append another element to the root -> no change!
    eRoot.appendElement ("dummy");
    assertEquals (3, aIEL.getInvocationCount ());

    try
    {
      aDoc.registerEventTarget (null, aIEL);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      aDoc.registerEventTarget (EMicroEvent.NODE_INSERTED, null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      aDoc.unregisterEventTarget (null, aIEL);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      aDoc.unregisterEventTarget (EMicroEvent.NODE_INSERTED, null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testRemovedEvent ()
  {
    final MockMicroEventListener aIEL = new MockMicroEventListener (EMicroEvent.NODE_REMOVED);
    final IMicroDocument aDoc = new MicroDocument ();
    assertFalse (aDoc.unregisterEventTarget (EMicroEvent.NODE_REMOVED, aIEL).isChanged ());
    assertTrue (aDoc.registerEventTarget (EMicroEvent.NODE_REMOVED, aIEL).isChanged ());
    assertFalse (aDoc.registerEventTarget (EMicroEvent.NODE_REMOVED, aIEL).isChanged ());
    IMicroElement eRoot = null;
    try
    {
      assertEquals (0, aIEL.getInvocationCount ());
      eRoot = aDoc.appendElement ("root_element");
      final IMicroNode aNode1 = eRoot.appendElement ("root_element");
      final IMicroNode aNode2 = eRoot.appendText ("My Text node");

      // Only insertions so far
      assertEquals (0, aIEL.getInvocationCount ());
      assertEquals (2, eRoot.getChildCount ());

      // Remove element
      eRoot.removeChild (aNode1);
      assertEquals (1, aIEL.getInvocationCount ());
      assertEquals (1, eRoot.getChildCount ());
      assertSame (aNode2, eRoot.getFirstChild ());

      // Remove by index
      eRoot.removeChildAtIndex (0);
      assertEquals (2, aIEL.getInvocationCount ());
      assertFalse (eRoot.hasChildren ());

      // Rebuild and than remove all at once
      eRoot.appendElement ("root_element");
      eRoot.appendText ("My Text node");
      eRoot.appendElement ("a").appendElement ("b").appendText ("c");
      assertEquals (3, eRoot.getChildCount ());
      eRoot.removeAllChildren ();
      assertEquals (2 + 3, aIEL.getInvocationCount ());
    }
    finally
    {
      // unregister
      aDoc.unregisterEventTarget (EMicroEvent.NODE_REMOVED, aIEL);
    }

    // Rebuild and than remove all at once again - no events triggered because
    // event listener was removed
    eRoot.appendElement ("root_element");
    eRoot.appendText ("My Text node");
    eRoot.appendElement ("a").appendElement ("b").appendText ("c");
    assertEquals (3, eRoot.getChildCount ());
    eRoot.removeAllChildren ();
    assertEquals (2 + 3, aIEL.getInvocationCount ());
  }
}
