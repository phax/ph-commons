/**
 * Copyright (C) 2014 Philip Helger (www.helger.com)
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

import org.junit.Test;

import com.helger.commons.microdom.IMicroDocument;
import com.helger.commons.microdom.IMicroElement;
import com.helger.commons.microdom.impl.MicroDocument;
import com.helger.commons.microdom.impl.MicroDocumentType;
import com.helger.commons.microdom.serialize.MicroSerializer;
import com.helger.commons.mock.AbstractPHTestCase;

/**
 * Test class for class {@link DefaultXMLIterationHandler}.
 * 
 * @author Philip Helger
 */
public final class DefaultXMLIterationHandlerTest extends AbstractPHTestCase
{
  @Test
  public void testAll ()
  {
    final IMicroDocument aDoc = new MicroDocument (new MicroDocumentType ("qname", "pid", "sid"));
    final IMicroElement eRoot = aDoc.appendElement ("root");
    eRoot.appendElement ("nsuri", "child");
    eRoot.appendCDATA ("CDATA");
    eRoot.appendText ("Text");
    eRoot.appendComment ("Comment");
    eRoot.appendEntityReference ("name");
    eRoot.appendProcessingInstruction ("target", "wse");
    eRoot.appendIgnorableWhitespaceText (" ");

    final DefaultXMLIterationHandler hdl = new DefaultXMLIterationHandler ();
    final MicroSerializer aMS = new MicroSerializer ();
    aMS.write (aDoc, hdl);
  }
}
