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
package com.helger.json.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

import org.junit.Test;

/**
 * Test class for class {@link JsonParsePosition}.
 *
 * @author Philip Helger
 */
public final class JsonParsePositionTest
{
  private static final int TAB_SIZE = 8;

  @Test
  public void testInitialPosition ()
  {
    final JsonParsePosition aPos = new JsonParsePosition ();
    assertEquals (1, aPos.getLineNumber ());
    assertEquals (1, aPos.getColumnNumber ());
    assertEquals ("[1:1]", aPos.getAsString ());
    assertNotNull (aPos.toString ());
  }

  @Test
  public void testRegularChars ()
  {
    final JsonParsePosition aPos = new JsonParsePosition ();
    aPos.updatePosition ('a', TAB_SIZE);
    assertEquals (1, aPos.getLineNumber ());
    assertEquals (2, aPos.getColumnNumber ());

    aPos.updatePosition ('b', TAB_SIZE);
    assertEquals (1, aPos.getLineNumber ());
    assertEquals (3, aPos.getColumnNumber ());
    assertEquals ("[1:3]", aPos.getAsString ());
  }

  @Test
  public void testLF ()
  {
    final JsonParsePosition aPos = new JsonParsePosition ();
    aPos.updatePosition ('a', TAB_SIZE);
    // The line break only takes effect with the next character
    aPos.updatePosition ('\n', TAB_SIZE);
    assertEquals (1, aPos.getLineNumber ());

    aPos.updatePosition ('b', TAB_SIZE);
    assertEquals (2, aPos.getLineNumber ());
    assertEquals (1, aPos.getColumnNumber ());
  }

  @Test
  public void testCR ()
  {
    final JsonParsePosition aPos = new JsonParsePosition ();
    aPos.updatePosition ('a', TAB_SIZE);
    aPos.updatePosition ('\r', TAB_SIZE);
    assertEquals (1, aPos.getLineNumber ());

    // A CR not followed by an LF is a line break on its own
    aPos.updatePosition ('b', TAB_SIZE);
    assertEquals (2, aPos.getLineNumber ());
    assertEquals (1, aPos.getColumnNumber ());
  }

  @Test
  public void testCRLF ()
  {
    final JsonParsePosition aPos = new JsonParsePosition ();
    aPos.updatePosition ('a', TAB_SIZE);
    aPos.updatePosition ('\r', TAB_SIZE);
    // CR LF is a single line break
    aPos.updatePosition ('\n', TAB_SIZE);
    assertEquals (1, aPos.getLineNumber ());

    aPos.updatePosition ('b', TAB_SIZE);
    assertEquals (2, aPos.getLineNumber ());
    assertEquals (1, aPos.getColumnNumber ());
  }

  @Test
  public void testTab ()
  {
    final JsonParsePosition aPos = new JsonParsePosition ();
    // A tab advances to the next multiple of the tab size
    aPos.updatePosition ('\t', TAB_SIZE);
    assertEquals (1, aPos.getLineNumber ());
    assertEquals (TAB_SIZE, aPos.getColumnNumber ());

    aPos.updatePosition ('\t', TAB_SIZE);
    assertEquals (2 * TAB_SIZE, aPos.getColumnNumber ());
  }

  @Test
  public void testGetClone ()
  {
    final JsonParsePosition aPos = new JsonParsePosition ();
    aPos.updatePosition ('a', TAB_SIZE);
    aPos.updatePosition ('\n', TAB_SIZE);
    aPos.updatePosition ('b', TAB_SIZE);

    final JsonParsePosition aClone = aPos.getClone ();
    assertNotNull (aClone);
    assertNotSame (aPos, aClone);
    assertEquals (aPos.getLineNumber (), aClone.getLineNumber ());
    assertEquals (aPos.getColumnNumber (), aClone.getColumnNumber ());

    // The clone is independent
    aClone.updatePosition ('c', TAB_SIZE);
    assertEquals (aPos.getColumnNumber () + 1, aClone.getColumnNumber ());
  }
}
