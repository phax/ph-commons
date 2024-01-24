/*
 * Copyright (C) 2014-2024 Philip Helger (www.helger.com)
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
package com.helger.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Test class for class {@link RecursiveNodeIterator}.
 *
 * @author Philip Helger
 */
public final class RecursiveNodeIteratorTest
{
  @Test
  public void testBasic ()
  {
    final Document doc = XMLFactory.newDocument ();

    // No children present
    assertTrue (new RecursiveNodeIterator (doc).hasNext ());

    // 1 child
    final Element eRoot = (Element) doc.appendChild (doc.createElement ("root"));
    int nCount = 0;
    for (final Node aNode : new RecursiveNodeIterator (doc))
    {
      assertNotNull (aNode);
      switch (nCount)
      {
        case 0:
          assertEquals ("#document", aNode.getNodeName ());
          break;
        case 1:
          assertEquals ("root", aNode.getNodeName ());
          break;
      }
      ++nCount;
    }
    assertEquals (2, nCount);

    // 3 children
    eRoot.appendChild (doc.createTextNode ("Hallo"));
    eRoot.appendChild (doc.createTextNode ("Welt"));
    nCount = 0;
    final RecursiveNodeIterator it = new RecursiveNodeIterator (doc);
    for (final Node aNode : it)
    {
      assertNotNull (aNode);
      switch (nCount)
      {
        case 0:
          assertEquals ("#document", aNode.getNodeName ());
          break;
        case 1:
          assertEquals ("root", aNode.getNodeName ());
          break;
        case 2:
          assertEquals ("Hallo", aNode.getNodeValue ());
          break;
        case 3:
          assertEquals ("Welt", aNode.getNodeValue ());
          break;
      }
      ++nCount;
    }
    assertEquals (4, nCount);

    try
    {
      it.remove ();
      fail ();
    }
    catch (final UnsupportedOperationException ex)
    {}
  }

  @Test
  public void testChildNodeIterator ()
  {
    final Document doc = XMLFactory.newDocument ();

    // No children present
    assertFalse (RecursiveNodeIterator.createChildNodeIterator (doc).hasNext ());

    // 1 child
    final Element eRoot = (Element) doc.appendChild (doc.createElement ("root"));
    int nCount = 0;
    for (final Node aNode : RecursiveNodeIterator.createChildNodeIterator (doc))
    {
      assertNotNull (aNode);
      switch (nCount)
      {
        case 0:
          assertEquals ("root", aNode.getNodeName ());
          break;
      }
      ++nCount;
    }
    assertEquals (1, nCount);

    // 3 children
    eRoot.appendChild (doc.createTextNode ("Hallo"));
    eRoot.appendChild (doc.createTextNode ("Welt"));
    nCount = 0;
    final RecursiveNodeIterator it = RecursiveNodeIterator.createChildNodeIterator (doc);
    for (final Node aNode : it)
    {
      assertNotNull (aNode);
      switch (nCount)
      {
        case 0:
          assertEquals ("root", aNode.getNodeName ());
          break;
        case 1:
          assertEquals ("Hallo", aNode.getNodeValue ());
          break;
        case 2:
          assertEquals ("Welt", aNode.getNodeValue ());
          break;
      }
      ++nCount;
    }
    assertEquals (3, nCount);

    try
    {
      it.remove ();
      fail ();
    }
    catch (final UnsupportedOperationException ex)
    {}
  }

  @Test
  public void testError ()
  {
    try
    {
      new RecursiveNodeIterator (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      RecursiveNodeIterator.createChildNodeIterator (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }
}
