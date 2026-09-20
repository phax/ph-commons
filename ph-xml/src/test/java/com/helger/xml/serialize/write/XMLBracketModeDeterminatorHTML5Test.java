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
package com.helger.xml.serialize.write;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import org.junit.Test;

import com.helger.unittest.support.TestHelper;

/**
 * Test class for class {@link XMLBracketModeDeterminatorHTML5}.
 *
 * @author Philip Helger
 */
public final class XMLBracketModeDeterminatorHTML5Test
{
  @Test
  public void testVoidElements ()
  {
    final XMLBracketModeDeterminatorHTML5 aDet = new XMLBracketModeDeterminatorHTML5 ();

    // Void elements without children are self closed - case insensitive
    assertSame (EXMLSerializeBracketMode.SELF_CLOSED, aDet.getBracketMode (null, "br", null, false));
    assertSame (EXMLSerializeBracketMode.SELF_CLOSED, aDet.getBracketMode (null, "BR", null, false));
    assertSame (EXMLSerializeBracketMode.SELF_CLOSED, aDet.getBracketMode (null, "img", null, false));
    assertSame (EXMLSerializeBracketMode.SELF_CLOSED, aDet.getBracketMode (null, "input", null, false));
  }

  @Test
  public void testNonVoidElements ()
  {
    final XMLBracketModeDeterminatorHTML5 aDet = new XMLBracketModeDeterminatorHTML5 ();

    assertSame (EXMLSerializeBracketMode.OPEN_CLOSE, aDet.getBracketMode (null, "div", null, false));
    assertSame (EXMLSerializeBracketMode.OPEN_CLOSE, aDet.getBracketMode (null, "span", null, true));
    // A void element with children keeps the end tag
    assertSame (EXMLSerializeBracketMode.OPEN_CLOSE, aDet.getBracketMode (null, "br", null, true));
  }

  @Test
  public void testEqualsHashcode ()
  {
    final XMLBracketModeDeterminatorHTML5 aDet = new XMLBracketModeDeterminatorHTML5 ();
    assertNotNull (aDet.toString ());
    assertFalse (aDet.equals (null));
    assertFalse (aDet.equals ("any other type"));
    TestHelper.testDefaultImplementationWithEqualContentObject (aDet, new XMLBracketModeDeterminatorHTML5 ());
  }
}
