/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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

import static org.junit.Assert.assertEquals;

import java.nio.charset.StandardCharsets;

import org.junit.Test;

import com.helger.commons.io.EAppend;
import com.helger.commons.io.streamprovider.ByteArrayOutputStreamProvider;
import com.helger.commons.mock.CommonsTestHelper;
import com.helger.xml.microdom.MicroDocumentType;

/**
 * Test class for {@link XMLEmitter}
 *
 * @author Philip Helger
 */
public final class XMLEmitterTest
{
  @Test
  public void testMisc ()
  {
    assertEquals ("<!DOCTYPE qname PUBLIC \"pubid\" \"sysid\">",
                  XMLEmitter.getDocTypeHTMLRepresentation (EXMLSerializeVersion.XML_10,
                                                           EXMLIncorrectCharacterHandling.DEFAULT,
                                                           new MicroDocumentType ("qname", "pubid", "sysid")));
    assertEquals ("<!DOCTYPE qname PUBLIC \"pubid\" \"sysid\">",
                  XMLEmitter.getDocTypeHTMLRepresentation (EXMLSerializeVersion.XML_11,
                                                           EXMLIncorrectCharacterHandling.DEFAULT,
                                                           new MicroDocumentType ("qname", "pubid", "sysid")));
    CommonsTestHelper.testToStringImplementation (new XMLEmitter (new ByteArrayOutputStreamProvider ().getWriter (StandardCharsets.ISO_8859_1,
                                                                                                                  EAppend.DEFAULT),
                                                                  XMLWriterSettings.DEFAULT_XML_SETTINGS));
  }
}
