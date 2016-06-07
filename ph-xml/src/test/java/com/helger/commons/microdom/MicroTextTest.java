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
import static org.junit.Assert.fail;

import org.junit.Test;

import com.helger.commons.mock.CommonsTestHelper;

/**
 * Test class for class {@link MicroText}.
 *
 * @author Philip Helger
 */
public final class MicroTextTest
{
  @Test
  public void testCreation ()
  {
    assertNotNull (new MicroText (null));
    assertNotNull (new MicroText (""));

    IMicroText e = new MicroText ("xyz");
    assertNotNull (e);
    assertEquals ("xyz", e.getData ().toString ());
    assertFalse (e.hasParent ());
    assertFalse (e.hasChildren ());
    assertNull (e.getAllChildren ());
    assertNull (e.getFirstChild ());
    assertNull (e.getLastChild ());
    assertNull (e.getChildAtIndex (0));
    assertNull (e.getAllChildrenRecursive ());
    assertEquals (0, e.getChildCount ());
    assertNotNull (e.getNodeName ());
    assertNotNull (e.getNodeValue ());
    assertSame (EMicroNodeType.TEXT, e.getType ());
    CommonsTestHelper.testToStringImplementation (e);

    e.setData ("allo");
    assertEquals ("allo", e.getData ().toString ());
    assertFalse (e.hasChildren ());
    assertNull (e.getAllChildren ());

    e.appendData (" Welt");
    assertEquals ("allo Welt", e.getData ().toString ());

    e.prependData ("H");
    assertEquals ("Hallo Welt", e.getData ().toString ());

    e = new MicroText ("xyz");
    assertNotNull (e);
    assertTrue (e.isEqualContent (e.getClone ()));
    e = new MicroText (null);
    assertNotNull (e);
    assertTrue (e.isEqualContent (e.getClone ()));

    assertTrue (e.isEqualContent (e));
    assertFalse (e.isEqualContent (null));
    assertFalse (e.isEqualContent (new MicroDocument ()));

    assertTrue (new MicroText ("xyz").isEqualContent (new MicroText ("xyz")));
    assertFalse (new MicroText ("xyz").isEqualContent (new MicroText ("xy")));
  }

  @Test
  public void testAddChildren ()
  {
    final IMicroText e = new MicroText ("xyz");

    try
    {
      // Cannot add any child to a comment
      e.appendChild (new MicroText ("other"));
      fail ();
    }
    catch (final MicroException ex)
    {}

    try
    {
      // Cannot add any child to a comment
      e.insertAfter (new MicroText ("other"), new MicroText ("comment"));
      fail ();
    }
    catch (final MicroException ex)
    {}

    try
    {
      // Cannot add any child to a comment
      e.insertBefore (new MicroText ("other"), new MicroText ("comment"));
      fail ();
    }
    catch (final MicroException ex)
    {}

    try
    {
      // Cannot add any child to this node
      e.insertAtIndex (0, new MicroCDATA ("other"));
      fail ();
    }
    catch (final MicroException ex)
    {}
  }
}
