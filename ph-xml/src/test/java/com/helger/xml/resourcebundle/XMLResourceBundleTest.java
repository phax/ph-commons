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
package com.helger.xml.resourcebundle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.MissingResourceException;

import org.junit.Test;

import com.helger.commons.io.file.FileOperations;
import com.helger.commons.lang.NonBlockingProperties;
import com.helger.xml.microdom.serialize.MicroWriter;

/**
 * Test class for class {@link XMLResourceBundle}.
 *
 * @author Philip Helger
 */
public final class XMLResourceBundleTest
{
  @Test
  public void testAll ()
  {
    final File aFile = new File ("target/test-classes/unittest-xml-props.xml");
    try
    {
      // Create dummy properties file
      final NonBlockingProperties p = new NonBlockingProperties ();
      p.setProperty ("prop1", "Value 1");
      p.setProperty ("prop2", "äöü");
      MicroWriter.writeToFile (XMLResourceBundle.getAsPropertiesXML (p), aFile);

      // Read again
      final XMLResourceBundle aRB = XMLResourceBundle.getXMLBundle ("unittest-xml-props");
      assertNotNull (aRB);
      assertEquals ("Value 1", aRB.getString ("prop1"));
      assertEquals ("äöü", aRB.getString ("prop2"));
      try
      {
        aRB.getObject ("prop3");
        fail ();
      }
      catch (final MissingResourceException ex)
      {
        // expected
      }
    }
    finally
    {
      FileOperations.deleteFile (aFile);
    }
  }
}
