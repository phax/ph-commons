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
package com.helger.commons.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.NoSuchElementException;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.helger.commons.mock.AbstractCommonsTestCase;

/**
 * Test class for class {@link ChildNodeIterator}.
 *
 * @author Philip Helger
 */
public final class ChildNodeIteratorTest extends AbstractCommonsTestCase
{
  @Test
  public void testGetRecursiveChildIter ()
  {
    final Document doc = XMLFactory.newDocument ();

    // No children present
    assertFalse (new ChildNodeIterator (doc).hasNext ());

    // 1 child
    final Element eRoot = (Element) doc.appendChild (doc.createElement ("root"));
    int nCount = 0;
    for (final Node aNode : new ChildNodeIterator (doc))
    {
      assertNotNull (aNode);
      ++nCount;
    }
    assertEquals (1, nCount);

    // 2 children
    eRoot.appendChild (doc.createTextNode ("Hallo Welt"));
    nCount = 0;
    final ChildNodeIterator it = new ChildNodeIterator (doc);
    for (final Node aNode : it)
    {
      assertNotNull (aNode);
      ++nCount;
    }
    assertEquals (1, nCount);

    try
    {
      it.next ();
      fail ();
    }
    catch (final NoSuchElementException ex)
    {}
    try
    {
      it.remove ();
      fail ();
    }
    catch (final UnsupportedOperationException ex)
    {}
    try
    {
      new ChildNodeIterator (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }
}
