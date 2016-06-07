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
package com.helger.commons.microdom;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.helger.commons.mock.CommonsTestHelper;

/**
 * Test class for class {@link MicroDocumentType}.
 *
 * @author Philip Helger
 */
public final class MicroDocumentTypeTest
{
  @Test
  public void testAll ()
  {
    final IMicroDocumentType e = new MicroDocumentType ("qname", "pid", "sid");
    assertNotNull (e);
    assertEquals ("qname", e.getQualifiedName ());
    assertEquals ("pid", e.getPublicID ());
    assertEquals ("sid", e.getSystemID ());
    assertFalse (e.hasParent ());
    assertFalse (e.hasChildren ());
    assertNull (e.getFirstChild ());
    assertNull (e.getLastChild ());
    assertNull (e.getChildAtIndex (0));
    assertEquals (0, e.getChildCount ());
    assertNotNull (e.getNodeName ());
    assertNotNull (e.getNodeValue ());
    assertSame (EMicroNodeType.DOCUMENT_TYPE, e.getType ());
    CommonsTestHelper.testToStringImplementation (e);

    assertFalse (e.isEqualContent (null));
    assertTrue (e.isEqualContent (e));
    assertTrue (e.isEqualContent (e.getClone ()));
    assertTrue (new MicroDocumentType ("qname", "pid", "sid").isEqualContent (new MicroDocumentType ("qname",
                                                                                                     "pid",
                                                                                                     "sid")));
    assertTrue (new MicroDocumentType ("qname", null, null).isEqualContent (new MicroDocumentType ("qname",
                                                                                                   null,
                                                                                                   null)));
    assertFalse (new MicroDocumentType ("qname", "pid", "sid").isEqualContent (new MicroDocumentType ("qname",
                                                                                                      "pid",
                                                                                                      "sid2")));
    assertFalse (new MicroDocumentType ("qname", "pid", "sid").isEqualContent (new MicroDocumentType ("qname",
                                                                                                      "pid",
                                                                                                      null)));
    assertFalse (new MicroDocumentType ("qname", "pid", "sid").isEqualContent (new MicroDocumentType ("qname",
                                                                                                      "pid2",
                                                                                                      "sid")));
    assertFalse (new MicroDocumentType ("qname", "pid", "sid").isEqualContent (new MicroDocumentType ("qname",
                                                                                                      null,
                                                                                                      "sid")));
    assertFalse (new MicroDocumentType ("qname", "pid", "sid").isEqualContent (new MicroDocumentType ("qname",
                                                                                                      null,
                                                                                                      null)));
    assertFalse (new MicroDocumentType ("qname", "pid", "sid").isEqualContent (new MicroDocumentType ("qname2",
                                                                                                      "pid",
                                                                                                      "sid")));

    try
    {
      new MicroDocumentType ("", "pid", "sid");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    try
    {
      // Cannot add any child to this node
      e.insertAtIndex (0, new MicroCDATA ("other"));
      fail ();
    }
    catch (final MicroException ex)
    {}
  }
}
