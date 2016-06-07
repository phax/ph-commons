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
package com.helger.commons.xml.sax;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.xml.sax.SAXException;

import com.helger.commons.io.resource.ClassPathResource;
import com.helger.commons.xml.serialize.read.DOMReader;
import com.helger.commons.xml.serialize.read.DOMReaderSettings;

/**
 * Test class for class {@link CollectingSAXErrorHandler}.
 *
 * @author Philip Helger
 */
public final class CollectingSAXErrorHandlerTest
{
  @Test
  public void testAll () throws SAXException
  {
    CollectingSAXErrorHandler aCEH = new CollectingSAXErrorHandler ();
    assertNotNull (DOMReader.readXMLDOM (new ClassPathResource ("xml/buildinfo.xml"),
                                         new DOMReaderSettings ().setErrorHandler (aCEH)));
    assertTrue (aCEH.getResourceErrors ().isEmpty ());
    assertNotNull (aCEH.toString ());

    aCEH = new CollectingSAXErrorHandler ();
    try
    {
      DOMReader.readXMLDOM (new ClassPathResource ("test1.txt"), new DOMReaderSettings ().setErrorHandler (aCEH));
      fail ();
    }
    catch (final SAXException ex)
    {}
    assertFalse (aCEH.getResourceErrors ().isEmpty ());
  }
}
