/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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
package com.helger.xml.serialize;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import com.helger.annotation.Nonnegative;
import com.helger.base.string.Strings;
import com.helger.commons.system.SystemProperties;
import com.helger.xml.microdom.IMicroDocument;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.MicroDocument;
import com.helger.xml.microdom.serialize.MicroReader;
import com.helger.xml.microdom.serialize.MicroWriter;
import com.helger.xml.serialize.read.SAXReaderFactory;
import com.helger.xml.serialize.write.EXMLSerializeVersion;
import com.helger.xml.serialize.write.XMLWriterSettings;

/**
 * Test whether reading of XML 1.1 documents is valid.<br>
 * Links:
 * <ul>
 * <li><a href= "http://stackoverflow.com/questions/4988114/java-standard-lib-produce-wrong-xml-1-1"
 * >Link 1</a></li>
 * </ul>
 *
 * @author Philip Helger
 */
public final class ReadWriteXML11FuncTest
{
  private static final XMLWriterSettings XWS_11 = new XMLWriterSettings ().setSerializeVersion (EXMLSerializeVersion.XML_11);

  private static void _generateXmlFile (final String sFilename, @Nonnegative final int nElementCount)
  {
    final IMicroDocument aDoc = new MicroDocument ();
    final IMicroElement eMain = aDoc.addElement ("main_tag");
    for (int i = 0; i < nElementCount; ++i)
      eMain.addElement ("test").addText (Strings.getLeadingZero (i, 4));

    assertTrue (MicroWriter.writeToFile (aDoc, new File (sFilename), XWS_11).isSuccess ());
  }

  @Test
  public void testReadingXML11 ()
  {
    final String sFilename1 = "target/xml11test.xml";
    _generateXmlFile (sFilename1, 2500);

    // Read again
    final IMicroDocument aDoc = MicroReader.readMicroXML (new File (sFilename1));
    assertNotNull (aDoc);

    // Write again
    final String sFilename2 = "target/xml11test2.xml";
    assertTrue (MicroWriter.writeToFile (aDoc, new File (sFilename2), XWS_11).isSuccess ());

    // Read again
    final IMicroDocument aDoc2 = MicroReader.readMicroXML (new File (sFilename2));
    assertNotNull (aDoc2);

    // When using JAXP with Java 1.6.0_22, 1.6.0_29 or 1.6.0_45 (tested only
    // with this
    // version) the following test fails. That's why xerces must be included!
    // The bogus XMLReader is
    // com.sun.org.apache.xerces.internal.parsers.SAXParser
    assertTrue ("Documents are different when written to XML 1.1!\nUsed SAX XML reader: " +
                SAXReaderFactory.createXMLReader ().getClass ().getName () +
                "\nJava version: " +
                SystemProperties.getJavaVersion () +
                "\n" +
                MicroWriter.getNodeAsString (aDoc) +
                "\n\n" +
                MicroWriter.getNodeAsString (aDoc2),
                aDoc.isEqualContent (aDoc2));
  }
}
