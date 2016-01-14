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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.helger.commons.mock.CommonsTestHelper;

/**
 * Test class for class {@link MicroContainer}.
 *
 * @author Philip Helger
 */
public final class MicroContainerTest
{
  @Test
  public void testCreation ()
  {
    assertNotNull (new MicroContainer ());

    IMicroContainer e = new MicroContainer ();
    assertNotNull (e);
    assertFalse (e.hasParent ());
    assertFalse (e.hasChildren ());
    assertNull (e.getAllChildren ());
    assertNull (e.getFirstChild ());
    assertNull (e.getLastChild ());
    assertTrue (e.getAllChildrenRecursive ().isEmpty ());
    assertEquals (0, e.getChildCount ());
    assertNotNull (e.getNodeName ());
    assertNotNull (e.getNodeValue ());
    assertTrue (e.isEqualContent (e.getClone ()));
    assertSame (EMicroNodeType.CONTAINER, e.getType ());
    CommonsTestHelper.testToStringImplementation (e);

    e.appendElement ("any");
    assertNotNull (e);
    assertTrue (e.isEqualContent (e.getClone ()));

    assertTrue (e.isEqualContent (e));
    assertFalse (e.isEqualContent (null));
    assertFalse (e.isEqualContent (new MicroDocument ()));

    assertTrue (new MicroContainer ().isEqualContent (new MicroContainer ()));
    e = new MicroContainer ();
    e.appendText ("text");
    assertFalse (new MicroContainer ().isEqualContent (e));
    final IMicroNode [] aChildNodes = { new MicroText ("any") };

    e = new MicroContainer (aChildNodes);
    assertNotNull (e);
    assertTrue (e.hasChildren ());
    assertEquals (1, e.getChildCount ());
    assertTrue (e.getFirstChild ().isText ());
    assertTrue (e.getLastChild ().isText ());

    // Insert at index
    e.insertAtIndex (0, new MicroCDATA ("other"));
    e.insertAtIndex (500000, new MicroElement ("e"));

    assertEquals (3, e.getChildCount ());
    assertTrue (e.getFirstChild ().isCDATA ());
    assertTrue (e.getLastChild ().isElement ());
  }
}
