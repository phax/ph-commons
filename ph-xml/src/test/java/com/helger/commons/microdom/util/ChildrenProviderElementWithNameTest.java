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
package com.helger.commons.microdom.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.helger.commons.microdom.IMicroDocument;
import com.helger.commons.microdom.IMicroElement;
import com.helger.commons.microdom.MicroDocument;
import com.helger.commons.mock.AbstractCommonsTestCase;

/**
 * Test class for class {@link ChildrenProviderElementWithName}.
 *
 * @author Philip Helger
 */
public final class ChildrenProviderElementWithNameTest extends AbstractCommonsTestCase
{
  private static IMicroDocument _buildTestDoc ()
  {
    final IMicroDocument aDoc = new MicroDocument ();
    final IMicroElement eRoot = aDoc.appendElement ("root");
    eRoot.appendElement ("any");
    eRoot.appendText ("Text");
    eRoot.appendElement ("else");
    eRoot.appendElement ("namespace", "any");
    return aDoc;
  }

  @Test
  public void testAll ()
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

    try
    {
      new ChildrenProviderElementWithName ("");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
  }
}
