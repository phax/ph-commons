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
package com.helger.xml.microdom.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.helger.xml.microdom.IMicroDocument;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.MicroDocument;

/**
 * Test class for class {@link ChildrenProviderElementWithName}.
 *
 * @author Philip Helger
 */
public final class ChildrenProviderElementWithNameTest
{
  private static IMicroDocument _buildTestDoc ()
  {
    final IMicroDocument aDoc = new MicroDocument ();
    final IMicroElement eRoot = aDoc.addElement ("root");
    eRoot.addElement ("any");
    eRoot.addText ("Text");
    eRoot.addElement ("else");
    eRoot.addElementNS ("namespace", "any");
    return aDoc;
  }

  @Test
  public void testBasic ()
  {
    final IMicroDocument aDoc = _buildTestDoc ();
    final IMicroElement aDocElement = aDoc.getDocumentElement ();

    ChildrenProviderElementWithName x = new ChildrenProviderElementWithName ("any");

    assertTrue (x.hasChildren (aDocElement));
    assertEquals (2, x.getChildCount (aDocElement));
    assertEquals (2, x.getAllChildren (aDocElement).size ());

    x = new ChildrenProviderElementWithName ("namespace", "any");

    assertTrue (x.hasChildren (aDocElement));
    assertEquals (1, x.getChildCount (aDocElement));
    assertEquals (1, x.getAllChildren (aDocElement).size ());
  }

  @Test
  public void testCreationError ()
  {
    try
    {
      new ChildrenProviderElementWithName ("");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
  }
}
