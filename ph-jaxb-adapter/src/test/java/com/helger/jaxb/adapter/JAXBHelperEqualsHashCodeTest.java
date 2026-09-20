/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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
package com.helger.jaxb.adapter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsList;

import jakarta.xml.bind.JAXBElement;

/**
 * Test class for the equals and hashCode helpers of {@link JAXBHelper}.
 *
 * @author Philip Helger
 */
public final class JAXBHelperEqualsHashCodeTest
{
  private static final QName QNAME1 = new QName ("urn:example.org", "test1");
  private static final QName QNAME2 = new QName ("urn:example.org", "test2");

  private static Document s_aDoc;

  @BeforeClass
  public static void createDocument () throws ParserConfigurationException
  {
    final DocumentBuilderFactory aDBF = DocumentBuilderFactory.newInstance ();
    aDBF.setNamespaceAware (true);
    s_aDoc = aDBF.newDocumentBuilder ().newDocument ();
  }

  private static Element _createElement (final String sText)
  {
    final Element eRoot = s_aDoc.createElementNS ("urn:example.org", "ns:root");
    eRoot.appendChild (s_aDoc.createTextNode (sText));
    return eRoot;
  }

  private static <T> JAXBElement <T> _je (final QName aName, final Class <T> aClass, final T aValue)
  {
    return new JAXBElement <> (aName, aClass, null, aValue);
  }

  @Test
  public void testEqualJAXBElements ()
  {
    final JAXBElement <String> aJE = _je (QNAME1, String.class, "any");

    // Identity and null handling
    assertTrue (JAXBHelper.equalJAXBElements (null, null));
    assertTrue (JAXBHelper.equalJAXBElements (aJE, aJE));
    assertFalse (JAXBHelper.equalJAXBElements (aJE, null));
    assertFalse (JAXBHelper.equalJAXBElements (null, aJE));

    assertTrue (JAXBHelper.equalJAXBElements (aJE, _je (QNAME1, String.class, "any")));

    // Different name
    assertFalse (JAXBHelper.equalJAXBElements (aJE, _je (QNAME2, String.class, "any")));
    // Different value
    assertFalse (JAXBHelper.equalJAXBElements (aJE, _je (QNAME1, String.class, "other")));
    // Different declared type
    assertFalse (JAXBHelper.equalJAXBElements (aJE, _je (QNAME1, CharSequence.class, "any")));
    // Different scope
    assertFalse (JAXBHelper.equalJAXBElements (aJE,
                                               new JAXBElement <> (QNAME1,
                                                                   String.class,
                                                                   JAXBHelperEqualsHashCodeTest.class,
                                                                   "any")));
    // Different nil state
    final JAXBElement <String> aNil = _je (QNAME1, String.class, "any");
    aNil.setNil (true);
    assertFalse (JAXBHelper.equalJAXBElements (aJE, aNil));
  }

  @Test
  public void testEqualJAXBElementsWithSpecialValues ()
  {
    // byte[] values are compared by content
    assertTrue (JAXBHelper.equalJAXBElements (_je (QNAME1, byte [].class, "abc".getBytes (StandardCharsets.ISO_8859_1)),
                                              _je (QNAME1,
                                                   byte [].class,
                                                   "abc".getBytes (StandardCharsets.ISO_8859_1))));
    assertFalse (JAXBHelper.equalJAXBElements (_je (QNAME1,
                                                    byte [].class,
                                                    "abc".getBytes (StandardCharsets.ISO_8859_1)),
                                               _je (QNAME1,
                                                    byte [].class,
                                                    "abd".getBytes (StandardCharsets.ISO_8859_1))));

    // DOM Node values are compared structurally
    assertTrue (JAXBHelper.equalJAXBElements (_je (QNAME1, Node.class, _createElement ("x")),
                                              _je (QNAME1, Node.class, _createElement ("x"))));
    assertFalse (JAXBHelper.equalJAXBElements (_je (QNAME1, Node.class, _createElement ("x")),
                                               _je (QNAME1, Node.class, _createElement ("y"))));

    // Nested JAXBElement values
    final JAXBElement <String> aInner = _je (QNAME2, String.class, "inner");
    assertTrue (JAXBHelper.equalJAXBElements (_je (QNAME1, Object.class, aInner),
                                              _je (QNAME1,
                                                   Object.class,
                                                   (Object) _je (QNAME2, String.class, "inner"))));

    // List values
    final ICommonsList <Object> aList1 = new CommonsArrayList <> ("a", "b");
    final ICommonsList <Object> aList2 = new CommonsArrayList <> ("a", "b");
    assertTrue (JAXBHelper.equalJAXBElements (_je (QNAME1, Object.class, aList1), _je (QNAME1, Object.class, aList2)));

    // Different runtime classes are never equal
    assertFalse (JAXBHelper.equalJAXBElements (_je (QNAME1, Object.class, "abc"),
                                               _je (QNAME1, Object.class, Integer.valueOf (1))));
    // A null value on one side only
    assertFalse (JAXBHelper.equalJAXBElements (_je (QNAME1, String.class, "abc"),
                                               _je (QNAME1, String.class, (String) null)));
  }

  @Test
  public void testEqualDOMNodes ()
  {
    assertTrue (JAXBHelper.equalDOMNodes (null, null));
    final Element e1 = _createElement ("x");
    assertTrue (JAXBHelper.equalDOMNodes (e1, e1));
    assertFalse (JAXBHelper.equalDOMNodes (e1, null));
    assertFalse (JAXBHelper.equalDOMNodes (null, e1));

    assertTrue (JAXBHelper.equalDOMNodes (e1, _createElement ("x")));
    assertFalse (JAXBHelper.equalDOMNodes (e1, _createElement ("y")));

    // Different node type
    assertFalse (JAXBHelper.equalDOMNodes (e1, s_aDoc.createTextNode ("x")));
    // Different node name
    assertFalse (JAXBHelper.equalDOMNodes (e1, s_aDoc.createElementNS ("urn:example.org", "ns:other")));
    // Different namespace URI
    assertFalse (JAXBHelper.equalDOMNodes (e1, s_aDoc.createElementNS ("urn:other", "ns:root")));
    // Different prefix
    assertFalse (JAXBHelper.equalDOMNodes (e1, s_aDoc.createElementNS ("urn:example.org", "other:root")));

    // Different number of children
    final Element e2 = _createElement ("x");
    e2.appendChild (s_aDoc.createTextNode ("more"));
    assertFalse (JAXBHelper.equalDOMNodes (e1, e2));
  }

  @Test
  public void testEqualListJAXBElements ()
  {
    final List <JAXBElement <?>> aList1 = new CommonsArrayList <> (_je (QNAME1, String.class, "a"));
    final List <JAXBElement <?>> aList2 = new CommonsArrayList <> (_je (QNAME1, String.class, "a"));
    final List <JAXBElement <?>> aList3 = new CommonsArrayList <> (_je (QNAME1, String.class, "b"));

    assertTrue (JAXBHelper.equalListJAXBElements (null, null));
    assertTrue (JAXBHelper.equalListJAXBElements (aList1, aList1));
    assertTrue (JAXBHelper.equalListJAXBElements (aList1, aList2));
    assertFalse (JAXBHelper.equalListJAXBElements (aList1, aList3));
    assertFalse (JAXBHelper.equalListJAXBElements (aList1, null));
    assertFalse (JAXBHelper.equalListJAXBElements (null, aList1));
    // Different size
    assertFalse (JAXBHelper.equalListJAXBElements (aList1, new CommonsArrayList <> ()));
  }

  @Test
  public void testEqualListAnys ()
  {
    final List <Object> aList1 = new CommonsArrayList <> ("a", Integer.valueOf (1));
    final List <Object> aList2 = new CommonsArrayList <> ("a", Integer.valueOf (1));
    final List <Object> aList3 = new CommonsArrayList <> ("a", Integer.valueOf (2));

    assertTrue (JAXBHelper.equalListAnys (null, null));
    assertTrue (JAXBHelper.equalListAnys (aList1, aList2));
    assertFalse (JAXBHelper.equalListAnys (aList1, aList3));
    assertFalse (JAXBHelper.equalListAnys (aList1, null));
    assertFalse (JAXBHelper.equalListAnys (aList1, new CommonsArrayList <> ()));
  }

  @Test
  public void testGetHashCodeJAXBElement ()
  {
    assertEquals (JAXBHelper.getHashCode ((JAXBElement <?>) null), JAXBHelper.getHashCode ((JAXBElement <?>) null));

    final JAXBElement <String> aJE = _je (QNAME1, String.class, "any");
    assertEquals (JAXBHelper.getHashCode (aJE), JAXBHelper.getHashCode (_je (QNAME1, String.class, "any")));
    assertNotEquals (JAXBHelper.getHashCode (aJE), JAXBHelper.getHashCode (_je (QNAME2, String.class, "any")));

    // The special value types
    assertEquals (JAXBHelper.getHashCode (_je (QNAME1, byte [].class, "abc".getBytes (StandardCharsets.ISO_8859_1))),
                  JAXBHelper.getHashCode (_je (QNAME1, byte [].class, "abc".getBytes (StandardCharsets.ISO_8859_1))));
    assertEquals (JAXBHelper.getHashCode (_je (QNAME1, Node.class, _createElement ("x"))),
                  JAXBHelper.getHashCode (_je (QNAME1, Node.class, _createElement ("x"))));
    assertEquals (JAXBHelper.getHashCode (_je (QNAME1, Object.class, (Object) _je (QNAME2, String.class, "i"))),
                  JAXBHelper.getHashCode (_je (QNAME1, Object.class, (Object) _je (QNAME2, String.class, "i"))));
    assertEquals (JAXBHelper.getHashCode (_je (QNAME1, Object.class, (Object) new CommonsArrayList <> ("a", "b"))),
                  JAXBHelper.getHashCode (_je (QNAME1, Object.class, (Object) new CommonsArrayList <> ("a", "b"))));
    // null value
    assertEquals (JAXBHelper.getHashCode (_je (QNAME1, String.class, (String) null)),
                  JAXBHelper.getHashCode (_je (QNAME1, String.class, (String) null)));
  }

  @Test
  public void testGetHashCodeNode ()
  {
    assertEquals (JAXBHelper.getHashCode ((Node) null), JAXBHelper.getHashCode ((Node) null));
    assertEquals (JAXBHelper.getHashCode (_createElement ("x")), JAXBHelper.getHashCode (_createElement ("x")));
    assertNotEquals (JAXBHelper.getHashCode (_createElement ("x")), JAXBHelper.getHashCode (_createElement ("y")));
  }

  @Test
  public void testGetListHashCodes ()
  {
    assertEquals (JAXBHelper.getListJAXBElementHashCode (null), JAXBHelper.getListJAXBElementHashCode (null));

    final List <JAXBElement <?>> aList1 = new CommonsArrayList <> (_je (QNAME1, String.class, "a"));
    final List <JAXBElement <?>> aList2 = new CommonsArrayList <> (_je (QNAME1, String.class, "a"));
    assertEquals (JAXBHelper.getListJAXBElementHashCode (aList1), JAXBHelper.getListJAXBElementHashCode (aList2));

    assertEquals (JAXBHelper.getListAnyHashCode (null), JAXBHelper.getListAnyHashCode (null));
    assertEquals (JAXBHelper.getListAnyHashCode (new CommonsArrayList <> ("a", "b")),
                  JAXBHelper.getListAnyHashCode (new CommonsArrayList <> ("a", "b")));
  }

  @Test
  public void testGetClonedJAXBElementNull ()
  {
    assertNull (JAXBHelper.getClonedJAXBElement (null));

    // A nil element keeps its nil state
    final JAXBElement <String> aNil = _je (QNAME1, String.class, "any");
    aNil.setNil (true);
    assertTrue (JAXBHelper.getClonedJAXBElement (aNil).isNil ());
  }
}
