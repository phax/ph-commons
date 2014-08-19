/**
 * Copyright (C) 2006-2014 phloc systems (www.phloc.com)
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
package com.helger.commons.xml.serialize;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.helger.commons.charset.CCharset;
import com.helger.commons.io.EAppend;
import com.helger.commons.io.streamprovider.ByteArrayOutputStreamProvider;
import com.helger.commons.microdom.impl.MicroDocumentType;
import com.helger.commons.mock.AbstractPHTestCase;
import com.helger.commons.mock.PhlocTestUtils;
import com.helger.commons.xml.EXMLIncorrectCharacterHandling;

/**
 * Test class for {@link XMLEmitterPhloc}
 * 
 * @author Philip Helger
 */
public final class XMLEmitterPhlocTest extends AbstractPHTestCase
{
  @Test
  public void testMisc ()
  {
    assertEquals ("<!DOCTYPE qname PUBLIC \"pubid\" \"sysid\">",
                  XMLEmitterPhloc.getDocTypeHTMLRepresentation (EXMLSerializeVersion.XML_10,
                                                                EXMLIncorrectCharacterHandling.DEFAULT,
                                                                new MicroDocumentType ("qname", "pubid", "sysid")));
    assertEquals ("<!DOCTYPE qname PUBLIC \"pubid\" \"sysid\">",
                  XMLEmitterPhloc.getDocTypeHTMLRepresentation (EXMLSerializeVersion.XML_11,
                                                                EXMLIncorrectCharacterHandling.DEFAULT,
                                                                new MicroDocumentType ("qname", "pubid", "sysid")));
    PhlocTestUtils.testToStringImplementation (new XMLEmitterPhloc (new ByteArrayOutputStreamProvider ().getWriter (CCharset.CHARSET_ISO_8859_1_OBJ,
                                                                                                                    EAppend.DEFAULT),
                                                                    XMLWriterSettings.DEFAULT_XML_SETTINGS));
  }
}
