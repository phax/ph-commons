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
package com.helger.tree.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.helger.commons.io.resource.ClassPathResource;
import com.helger.tree.DefaultTree;
import com.helger.tree.mock.MockHasName;
import com.helger.tree.withid.unique.DefaultTreeWithGlobalUniqueID;
import com.helger.xml.microdom.IMicroDocument;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.serialize.MicroReader;

/**
 * Test class for class {@link TreeXMLConverter}.
 *
 * @author Philip Helger
 */
public final class TreeXMLConverterTest
{
  @Test
  public void testReadWrite ()
  {
    // read initial document
    final IMicroDocument aDoc1 = MicroReader.readMicroXML (new ClassPathResource ("tree/xmlconverter-valid1.xml"));
    assertNotNull (aDoc1);

    // convert document to tree
    final DefaultTreeWithGlobalUniqueID <String, MockHasName> t1 = TreeXMLConverter.getXMLAsTreeWithUniqueStringID (aDoc1.getDocumentElement (),
                                                                                                                    new MockHasNameConverter ());
    assertNotNull (t1);

    // convert tree again to document
    final IMicroElement aDoc2 = TreeXMLConverter.getTreeWithStringIDAsXML (t1, new MockHasNameConverter ());
    assertNotNull (aDoc2);

    // and convert the document again to a tree
    DefaultTreeWithGlobalUniqueID <String, MockHasName> t2 = TreeXMLConverter.getXMLAsTreeWithUniqueStringID (aDoc2,
                                                                                                              new MockHasNameConverter ());
    assertNotNull (t2);
    assertEquals (t1, t2);

    // and convert the document again to a tree
    t2 = TreeXMLConverter.getXMLAsTreeWithUniqueID (aDoc2, aID -> aID, new MockHasNameConverter ());
    assertNotNull (t2);
    assertEquals (t1, t2);

    // and convert the document again to a tree
    assertNotNull (TreeXMLConverter.getXMLAsTreeWithID (aDoc2, aID -> aID, new MockHasNameConverter ()));
  }

  @Test
  public void testTree ()
  {
    final DefaultTree <MockHasName> aTree = new DefaultTree <> ();
    aTree.getRootItem ().createChildItem (new MockHasName ("name2"));
    aTree.getRootItem ().createChildItem (new MockHasName ("name1"));

    final IMicroElement aElement = TreeXMLConverter.getTreeAsXML (aTree,
                                                                  (o1, o2) -> o1.getData ().compareTo (o2.getData ()),
                                                                  new MockHasNameConverter ());
    assertNotNull (aElement);
  }
}
