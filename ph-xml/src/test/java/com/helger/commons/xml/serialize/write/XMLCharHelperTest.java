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
package com.helger.commons.xml.serialize.write;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Test class for class {@link XMLCharHelper}.
 *
 * @author Philip Helger
 */
public final class XMLCharHelperTest
{
  @Test
  public void testAttributeValue ()
  {
    assertFalse (XMLCharHelper.isInvalidXMLAttributeValueChar (EXMLSerializeVersion.XML_10, '<'));
    assertFalse (XMLCharHelper.isInvalidXMLAttributeValueChar (EXMLSerializeVersion.XML_11, '<'));
    assertFalse (XMLCharHelper.isInvalidXMLAttributeValueChar (EXMLSerializeVersion.HTML, '<'));

    assertTrue (XMLCharHelper.isInvalidXMLAttributeValueChar (EXMLSerializeVersion.XML_10, '\u0001'));
    assertTrue (XMLCharHelper.isInvalidXMLAttributeValueChar (EXMLSerializeVersion.XML_11, '\u007f'));
    assertTrue (XMLCharHelper.isInvalidXMLAttributeValueChar (EXMLSerializeVersion.HTML, '\u007f'));
  }

  @Test
  public void testText ()
  {
    assertFalse (XMLCharHelper.isInvalidXMLTextChar (EXMLSerializeVersion.XML_10, '<'));
    assertFalse (XMLCharHelper.isInvalidXMLTextChar (EXMLSerializeVersion.XML_11, '<'));
    assertFalse (XMLCharHelper.isInvalidXMLTextChar (EXMLSerializeVersion.HTML, '<'));
  }
}
