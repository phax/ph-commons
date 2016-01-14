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
package com.helger.commons.microdom.serialize;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.helger.commons.microdom.IMicroDocument;
import com.helger.commons.microdom.MicroDocument;
import com.helger.commons.xml.serialize.write.XMLWriterSettings;

/**
 * Test class for class {@link MicroDOMInputStreamProvider}.
 *
 * @author Philip Helger
 */
public final class MicroDOMInputStreamProviderTest
{
  @Test
  public void testSimple ()
  {
    final IMicroDocument aDoc = new MicroDocument ();
    aDoc.appendElement ("test");
    assertNotNull (new MicroDOMInputStreamProvider (aDoc, XMLWriterSettings.DEFAULT_XML_CHARSET_OBJ).getInputStream ());
  }
}
