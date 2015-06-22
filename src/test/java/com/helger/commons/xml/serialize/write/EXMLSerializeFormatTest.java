/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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
package com.helger.commons.xml.serialize.write;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.helger.commons.xml.serialize.write.EXMLSerializeFormat;

/**
 * Test class for class {@link EXMLSerializeFormat}.
 * 
 * @author Philip Helger
 */
public final class EXMLSerializeFormatTest
{
  @Test
  public void testAll ()
  {
    for (final EXMLSerializeFormat e : EXMLSerializeFormat.values ())
    {
      assertNotNull (e.getID ());
      assertSame (e, EXMLSerializeFormat.valueOf (e.name ()));
      assertSame (e, EXMLSerializeFormat.getFromIDOrNull (e.getID ()));
    }

    assertTrue (EXMLSerializeFormat.HTML.isHTML ());
    assertFalse (EXMLSerializeFormat.XHTML.isHTML ());
    assertFalse (EXMLSerializeFormat.XML.isHTML ());

    assertFalse (EXMLSerializeFormat.HTML.isXHTML ());
    assertTrue (EXMLSerializeFormat.XHTML.isXHTML ());
    assertFalse (EXMLSerializeFormat.XML.isXHTML ());

    assertFalse (EXMLSerializeFormat.HTML.isXML ());
    assertFalse (EXMLSerializeFormat.XHTML.isXML ());
    assertTrue (EXMLSerializeFormat.XML.isXML ());
  }
}
