/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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
package com.helger.xml.microdom;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.helger.unittest.support.TestHelper;

/**
 * Test class for class {@link MicroDocument}.
 *
 * @author Philip Helger
 */
public final class MicroDocumentTest
{
  @Test
  public void testNew ()
  {
    final IMicroDocument e = new MicroDocument ();
    assertNotNull (e);
    assertNull (e.getDocType ());
    assertNull (e.getDocumentElement ());
    assertFalse (e.hasParent ());
    assertFalse (e.hasChildren ());
    assertFalse (e.isStandalone ());

    assertTrue (e.isEqualContent (e));
    assertFalse (e.isEqualContent (null));
    assertFalse (e.isEqualContent (new MicroText ("any")));

    assertTrue (e.isEqualContent (e.getClone ()));
    assertSame (EMicroNodeType.DOCUMENT, e.getType ());
    assertTrue (new MicroDocument ().isEqualContent (new MicroDocument ()));
    assertFalse (new MicroDocument ().isEqualContent (new MicroDocument (new MicroDocumentType ("any", "public", "system"))));

    // Clone with children
    e.addElement ("root");
    assertTrue (e.isEqualContent (e.getClone ()));
  }

  @Test
  public void testNewWithDocType ()
  {
    final IMicroDocument d = new MicroDocument (new MicroDocumentType ("html", "public ID", "system ID"));
    assertNotNull (d);
    assertNotNull (d.getDocType ());
    assertNull (d.getDocumentElement ());
    assertTrue (d.hasChildren ());
    assertFalse (d.isStandalone ());
    assertEquals (1, d.getAllChildren ().size ());
  }

  @Test
  public void testAppendToRoot ()
  {
    IMicroDocument d = new MicroDocument ();
    assertNotNull (d.addElement ("root"));

    try
    {
      // Can only add comments, document types or one element
      d.addEntityReference ("lt");
      fail ();
    }
    catch (final MicroException ex)
    {}

    try
    {
      // Cannot append a second root element!
      d.addElement ("root2");
      fail ();
    }
    catch (final MicroException ex)
    {}

    d = new MicroDocument ();
    assertNotNull (d.addComment ("This is a root comment"));
    assertNotNull (d.addComment ("Well I forgot something"));
    assertNotNull (d.addElement ("root"));
    assertNotNull (d.addComment ("Some more comment after the root element"));
  }

  @Test
  public void testToString ()
  {
    final IMicroDocument d = new MicroDocument ();
    TestHelper.testToStringImplementation (d);
  }

  @Test
  public void testIsStandalone ()
  {
    final IMicroDocument aDoc = new MicroDocument ();
    assertFalse (aDoc.isStandalone ());
    final IMicroElement eRoot = aDoc.addElement ("root");
    assertFalse (aDoc.isStandalone ());
    eRoot.setAttribute ("any", "Value");
    assertFalse (aDoc.isStandalone ());
    aDoc.setStandalone (true);
    assertTrue (aDoc.isStandalone ());
  }
}
