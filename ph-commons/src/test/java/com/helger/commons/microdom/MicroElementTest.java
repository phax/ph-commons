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

import java.math.BigDecimal;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import com.helger.commons.junit.DebugModeTestRule;
import com.helger.commons.mock.AbstractCommonsTestCase;
import com.helger.commons.mock.CommonsTestHelper;
import com.helger.commons.text.MultilingualText;
import com.helger.commons.typeconvert.TypeConverterException;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Test class for class {@link MicroElement}.
 *
 * @author Philip Helger
 */
public final class MicroElementTest extends AbstractCommonsTestCase
{
  @Rule
  public final TestRule m_aRule = new DebugModeTestRule ();

  @Test
  @SuppressFBWarnings (value = "NP_NONNULL_PARAM_VIOLATION")
  public void testCreation ()
  {
    try
    {
      new MicroElement ((String) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      new MicroElement ("");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    try
    {
      new MicroElement ("space unallowed");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    IMicroElement e = new MicroElement ("xyz");
    assertNotNull (e);
    assertNull (e.getLocalName ());
    assertNull (e.getNamespaceURI ());
    assertEquals ("xyz", e.getTagName ());
    assertFalse (e.hasChildren ());
    assertFalse (e.hasParent ());
    assertNull (e.getAllChildren ());
    assertNotNull (e.getAllChildElements ());
    assertTrue (e.getAllChildElements ().isEmpty ());
    assertSame (EMicroNodeType.ELEMENT, e.getType ());
    CommonsTestHelper.testToStringImplementation (e);

    e = new MicroElement ("myns", "xyz");
    assertNull (e.getAttributeValue ("attr"));
    assertNull (e.getAttributeValueWithConversion ("attr", String.class));
    assertFalse (e.removeAttribute ("attr").isChanged ());
    assertEquals ("xyz", e.getLocalName ());
    assertEquals ("myns", e.getNamespaceURI ());
    assertEquals ("xyz", e.getTagName ());
    assertFalse (e.hasAttributes ());
    assertFalse (e.hasAttribute ("attr"));
    assertSame (e, e.setAttribute ("attr", "1234"));
    assertTrue (e.hasAttribute ("attr"));
    assertFalse (e.hasAttribute ("otherattr"));
    assertEquals ("1234", e.getAttributeValue ("attr"));
    assertEquals (1234, e.getAttributeValueWithConversion ("attr", Integer.class).intValue ());
    assertNull (e.getAttributeValue ("attr2"));

    try
    {
      e.getAttributeValueWithConversion ("attr", MultilingualText.class);
      fail ();
    }
    catch (final TypeConverterException ex)
    {}

    assertTrue (e.removeAttribute ("attr").isChanged ());
    assertFalse (e.removeAttribute ("attr").isChanged ());

    e = new MicroElement ("xyz");
    assertTrue (e.isEqualContent (e.getClone ()));
    e = new MicroElement ("xyz").setAttribute ("attr", 5);
    assertTrue (e.isEqualContent (e.getClone ()));
    e = new MicroElement ("mynsuri", "xyz");
    assertTrue (e.isEqualContent (e.getClone ()));
    e = new MicroElement ("myns", "xyz").setAttribute ("name", "value");
    assertTrue (e.isEqualContent (e.getClone ()));
    e = new MicroElement ("myns", "xyz");
    e.appendText ("any");
    assertTrue (e.isEqualContent (e.getClone ()));
    e = new MicroElement ("myns", "xyz");
    e.appendElement ("x", "z");
    assertTrue (e.isEqualContent (e.getClone ()));

    assertTrue (e.isEqualContent (e));
    assertFalse (e.isEqualContent (null));
    assertFalse (e.isEqualContent (new MicroDocument ()));

    assertTrue (new MicroElement ("xyz").isEqualContent (new MicroElement ("xyz")));
    assertTrue (new MicroElement ("myns", "xyz").isEqualContent (new MicroElement ("myns", "xyz")));
    assertFalse (new MicroElement ("xyz").isEqualContent (new MicroElement ("xy")));
    assertFalse (new MicroElement ("myns", "xyz").isEqualContent (new MicroElement ("myns", "xy")));
    assertFalse (new MicroElement ("myns", "xyz").isEqualContent (new MicroElement ("myns2", "xyz")));
    assertFalse (new MicroElement ("myns", "xyz").isEqualContent (new MicroElement (null, "xyz")));
    assertFalse (new MicroElement ("myns",
                                   "xyz").isEqualContent (new MicroElement ("myns", "xyz").setAttribute ("name",
                                                                                                         "value")));

    e = new MicroElement ("ns1:element");
    assertNull (e.getLocalName ());
    assertEquals ("ns1:element", e.getTagName ());
    e = new MicroElement ("url", "ns1:element");
    assertEquals ("element", e.getLocalName ());
    assertEquals ("element", e.getTagName ());
  }

  @Test
  public void testGetChildElements ()
  {
    IMicroElement eRoot = new MicroElement ("root");
    assertNotNull (eRoot.getAllChildElements ());
    assertTrue (eRoot.getAllChildElements ().isEmpty ());
    assertFalse (eRoot.hasChildElements ());
    CommonsTestHelper.testToStringImplementation (eRoot);

    final IMicroElement e1 = eRoot.appendElement ("level1");
    e1.appendElement ("e11");
    eRoot.appendText ("My text node");
    eRoot.appendComment ("Comment");
    eRoot.appendElement ("xyz");
    CommonsTestHelper.testToStringImplementation (eRoot);

    assertNotNull (eRoot.getAllChildElements ());
    assertEquals (2, eRoot.getAllChildElements ().size ());
    assertEquals (e1, eRoot.getFirstChild ());
    assertEquals (e1, eRoot.getFirstChildElement ());
    assertNull (eRoot.getFirstChildElement ("anyothername"));
    assertTrue (eRoot.hasChildElements ());
    assertTrue (eRoot.hasChildElements ("level1"));
    assertFalse (eRoot.hasChildElements ("level2"));

    final IMicroContainer ec = eRoot.appendContainer ();
    assertEquals (2, eRoot.getAllChildElements ().size ());
    assertEquals (1, eRoot.getAllChildElements ("level1").size ());
    final IMicroElement e2a = ec.appendElement ("level2a");
    e2a.appendElement ("e2a1");
    e2a.appendComment ("any");
    e2a.appendElement ("e2a2");
    final IMicroElement e2b = ec.appendElement ("level2b");
    e2b.appendElement ("e2b1");
    e2b.appendComment ("any");
    e2b.appendElement ("e2b2");
    assertEquals (4, eRoot.getAllChildElements ().size ());
    assertEquals (1, eRoot.getAllChildElements ("level1").size ());
    assertTrue (eRoot.hasChildElements ("level2a"));
    assertEquals (1, eRoot.getAllChildElements ("level2a").size ());
    assertFalse (eRoot.hasChildElements ("level2c"));
    assertNotNull (eRoot.getFirstChildElement ("level1"));
    assertNotNull (eRoot.getFirstChildElement ("level2a"));

    // special
    eRoot = new MicroElement ("root");
    assertNull (eRoot.getFirstChildElement ());
    final IMicroContainer aCont = eRoot.appendContainer ();
    assertTrue (eRoot.hasChildren ());
    assertFalse (eRoot.hasChildElements ());
    assertNull (eRoot.getFirstChildElement ());
    aCont.appendElement ("el");
    assertTrue (eRoot.hasChildren ());
    assertTrue (eRoot.hasChildElements ());
    assertNotNull (eRoot.getFirstChildElement ());
  }

  @Test
  public void testSiblings ()
  {
    final IMicroElement eRoot = new MicroElement ("root");
    final IMicroElement e1 = eRoot.appendElement ("level1");
    e1.appendElement ("e11");
    final IMicroText eText = eRoot.appendText ("Mein text node");
    final IMicroComment eComment = eRoot.appendComment ("Comment");
    final IMicroElement eElem = eRoot.appendElement ("xyz");

    assertSame (eText, e1.getNextSibling ());
    assertSame (eComment, eText.getNextSibling ());
    assertSame (eElem, eComment.getNextSibling ());
    assertNull (eElem.getNextSibling ());

    assertNull (e1.getPreviousSibling ());
    assertSame (e1, eText.getPreviousSibling ());
    assertSame (eText, eComment.getPreviousSibling ());
    assertSame (eComment, eElem.getPreviousSibling ());

    // Alternative test
    final IMicroElement e = new MicroElement ("root");
    final IMicroElement c1 = e.appendElement ("c1");
    final IMicroElement c2 = e.appendElement ("c2");
    final IMicroElement c3 = e.appendElement ("c3");
    assertNull (e.getPreviousSibling ());
    assertNull (e.getNextSibling ());
    assertNull (c1.getPreviousSibling ());
    assertSame (c2, c1.getNextSibling ());
    assertSame (c1, c2.getPreviousSibling ());
    assertSame (c3, c2.getNextSibling ());
    assertSame (c2, c3.getPreviousSibling ());
    assertNull (c3.getNextSibling ());
  }

  @Test
  public void testGetParentElement ()
  {
    final IMicroElement eRoot = new MicroElement ("root");
    final IMicroElement e1 = eRoot.appendElement ("level1");
    final IMicroElement e11 = e1.appendElement ("e11");
    final IMicroText eText = eRoot.appendText ("Mein text node");
    final IMicroComment eComment = eRoot.appendComment ("Comment");
    final IMicroElement e2 = eRoot.appendElement ("level2");
    assertNull (e11.getParentElementWithName ("gibtsNed"));
    assertSame (e1, e11.getParentElementWithName ("level1"));
    assertSame (eRoot, e11.getParentElementWithName ("root"));
    assertNull (e2.getParentElementWithName ("gibtsNed"));
    assertNull (e2.getParentElementWithName ("level1"));
    assertSame (eRoot, e11.getParentElementWithName ("root"));
    assertSame (eRoot, eText.getParentElementWithName ("root"));
    assertSame (eRoot, eComment.getParentElementWithName ("root"));
  }

  @Test
  public void testGetChildElementOfName ()
  {
    final IMicroElement eRoot = new MicroElement ("root");
    final IMicroElement e1 = eRoot.appendElement ("level1");
    final IMicroElement e11 = e1.appendElement ("e11");
    final IMicroElement e2 = eRoot.appendElement ("level2");

    assertSame (e1, eRoot.getFirstChildElement ("level1"));
    assertSame (e11, e1.getFirstChildElement ("e11"));
    assertNull (eRoot.getFirstChildElement ("e11"));
    assertSame (e2, eRoot.getFirstChildElement ("level2"));
    assertNull (eRoot.getFirstChildElement ("root"));
  }

  @Test
  public void testRemoveChild ()
  {
    final IMicroElement eRoot = new MicroElement ("root");
    final IMicroElement e1 = eRoot.appendElement ("level1");
    final IMicroElement e11 = e1.appendElement ("e11");
    final IMicroElement e2 = eRoot.appendElement ("level2");

    assertEquals (2, eRoot.getAllChildElements ().size ());
    eRoot.removeChild (e1);
    assertEquals (1, eRoot.getAllChildElements ().size ());
    assertEquals (1, eRoot.getAllChildrenRecursive ().size ());
    assertNotNull (e1);
    assertNull (e1.getParent ());

    assertNull (eRoot.getFirstChildElement ("level1"));
    assertSame (e11, e1.getFirstChildElement ("e11"));
    assertNull (eRoot.getFirstChildElement ("e11"));
    assertSame (e2, eRoot.getFirstChildElement ("level2"));
    assertNull (eRoot.getFirstChildElement ("root"));
  }

  @Test
  public void testRemoveChildAtIndex ()
  {
    final IMicroElement eRoot = new MicroElement ("root");
    final IMicroElement e1 = eRoot.appendElement ("level1");
    e1.appendElement ("e11");
    final IMicroElement e2 = eRoot.appendElement ("level2");
    e2.appendElement ("e21");
    assertEquals (2, eRoot.getAllChildElements ().size ());

    // Remove "level2"
    assertTrue (eRoot.removeChildAtIndex (1).isChanged ());
    assertEquals (1, eRoot.getAllChildElements ().size ());
    assertEquals (2, eRoot.getAllChildrenRecursive ().size ());
    assertSame (e1, eRoot.getChildAtIndex (0));
    assertNotNull (e1.getParent ());
    assertNull (eRoot.getChildAtIndex (1));
    assertNull (e2.getParent ());
    assertEquals (1, e2.getChildCount ());
    assertSame (e2, e2.getChildAtIndex (0).getParent ());

    // No such index
    assertFalse (eRoot.removeChildAtIndex (1).isChanged ());
  }

  @Test
  public void testRemoveAllChildren ()
  {
    final IMicroElement eRoot = new MicroElement ("root");

    // No children yet
    assertFalse (eRoot.removeAllChildren ().isChanged ());

    final IMicroElement e1 = eRoot.appendElement ("level1");
    e1.appendElement ("e11");
    final IMicroElement e2 = eRoot.appendElement ("level2");
    e2.appendElement ("e21");
    assertEquals (2, eRoot.getAllChildElements ().size ());

    // Remove "all root level elements"
    assertTrue (eRoot.removeAllChildren ().isChanged ());
    assertEquals (0, eRoot.getAllChildElements ().size ());
    assertNull (e1.getParent ());
    assertEquals (1, e1.getChildCount ());
    assertSame (e1, e1.getChildAtIndex (0).getParent ());
    assertNull (e2.getParent ());
    assertEquals (1, e2.getChildCount ());
    assertSame (e2, e2.getChildAtIndex (0).getParent ());

    // No more element
    assertFalse (eRoot.removeAllChildren ().isChanged ());
  }

  @Test
  public void testGetClone ()
  {
    final IMicroElement eRoot = new MicroElement ("root");
    IMicroElement eClone = eRoot.getClone ();
    assertEquals (eRoot.getTagName (), eClone.getTagName ());
    assertNull (eRoot.getNamespaceURI ());
    assertNull (eClone.getNamespaceURI ());
    assertNull (eRoot.getAllQAttributes ());
    assertNull (eClone.getAllQAttributes ());

    eRoot.setAttribute ("attr1", "b");
    eClone = eRoot.getClone ();
    assertEquals (eRoot.getTagName (), eClone.getTagName ());
    assertNull (eRoot.getNamespaceURI ());
    assertNull (eClone.getNamespaceURI ());
    assertEquals (eRoot.getAllQAttributes (), eClone.getAllQAttributes ());

    eRoot.setAttribute ("nsuri", "attr2", "c");
    eClone = eRoot.getClone ();
    assertEquals (eRoot.getTagName (), eClone.getTagName ());
    assertNull (eRoot.getNamespaceURI ());
    assertNull (eClone.getNamespaceURI ());
    assertEquals (eRoot.getAllQAttributes (), eClone.getAllQAttributes ());

    assertTrue (eRoot.isEqualContent (eRoot.getClone ()));
    eRoot.appendText ("text");
    eRoot.appendCDATA ("CDATA content <>");
    assertTrue (eRoot.isEqualContent (eRoot.getClone ()));
  }

  @Test
  public void testGetChildElementsNS ()
  {
    final String NSURI = "http://www.helger.com/unittest";
    final String NSURI2 = "http://www.helger.com/unittest/second";
    final IMicroElement eRoot = new MicroElement ("root");
    final IMicroElement a = eRoot.appendElement (NSURI, "a");
    final IMicroElement b = eRoot.appendElement (NSURI2, "b");
    final IMicroContainer aCont = eRoot.appendContainer ();
    aCont.appendElement ("c");
    final IMicroElement d = aCont.appendElement (NSURI, "d");

    assertNotNull (eRoot.getAllChildElements ((String) null));
    assertNotNull (eRoot.getAllChildElements (null, (String) null));
    assertFalse (eRoot.hasChildElements ((String) null));
    assertFalse (eRoot.hasChildElements (null, (String) null));
    assertNull (eRoot.getFirstChildElement ((String) null));
    assertNull (eRoot.getFirstChildElement (null, (String) null));

    List <IMicroElement> x = eRoot.getAllChildElements (NSURI, "a");
    assertNotNull (x);
    assertEquals (1, x.size ());
    assertEquals ("a", x.get (0).getTagName ());
    assertEquals (a, x.get (0));

    x = eRoot.getAllChildElements (NSURI2, "b");
    assertNotNull (x);
    assertEquals (1, x.size ());
    assertEquals ("b", x.get (0).getTagName ());
    assertEquals (b, x.get (0));

    x = eRoot.getAllChildElements (NSURI, "d");
    assertNotNull (x);
    assertEquals (1, x.size ());
    assertEquals ("d", x.get (0).getTagName ());
    assertEquals (d, x.get (0));

    x = eRoot.getAllChildElements (NSURI, "e");
    assertNotNull (x);
    assertTrue (x.isEmpty ());

    assertTrue (eRoot.hasChildElements (NSURI, "a"));
    assertFalse (eRoot.hasChildElements (NSURI, "b"));
    assertFalse (eRoot.hasChildElements (NSURI, "c"));
    assertTrue (eRoot.hasChildElements (NSURI, "d"));
    assertFalse (eRoot.hasChildElements (NSURI, "e"));

    assertEquals (a, eRoot.getFirstChildElement (NSURI, "a"));
    assertNull (eRoot.getFirstChildElement (NSURI, "b"));
    assertNull (eRoot.getFirstChildElement (NSURI, "c"));
    assertEquals (d, eRoot.getFirstChildElement (NSURI, "d"));
    assertNull (eRoot.getFirstChildElement (NSURI, "e"));
  }

  @Test
  public void testGetTextContent ()
  {
    IMicroElement e = new MicroElement ("any");
    assertNull (e.getTextContent ());
    e.appendText ("ab");
    assertEquals ("ab", e.getTextContent ());
    e.appendText ("cd");
    assertEquals ("abcd", e.getTextContent ());
    e.appendCDATA ("xx");
    assertEquals ("abcdxx", e.getTextContent ());
    final IMicroContainer aCont = e.appendContainer ();
    assertEquals ("abcdxx", e.getTextContent ());
    aCont.appendText ("z1");
    assertEquals ("abcdxxz1", e.getTextContent ());
    aCont.appendCDATA ("z2");
    assertEquals ("abcdxxz1z2", e.getTextContent ());
    aCont.appendText ("z3");
    assertEquals ("abcdxxz1z2z3", e.getTextContent ());
    e.appendText ("end");
    assertEquals ("abcdxxz1z2z3end", e.getTextContent ());

    e = new MicroElement ("any");
    assertNull (e.getTextContentWithConversion (Integer.class));
    e.appendText ("1234");
    assertEquals (1234, e.getTextContentWithConversion (Integer.class).intValue ());
  }

  @Test
  public void testNamespaces ()
  {
    final IMicroElement e = new MicroElement ("any");
    assertNull (e.getNamespaceURI ());
    assertTrue (e.setNamespaceURI ("uri").isChanged ());
    assertEquals ("uri", e.getNamespaceURI ());
    assertFalse (e.setNamespaceURI ("uri").isChanged ());
    assertEquals ("uri", e.getNamespaceURI ());
    assertTrue (e.setNamespaceURI ("uri2").isChanged ());
    assertEquals ("uri2", e.getNamespaceURI ());
  }

  @Test
  @SuppressFBWarnings (value = "NP_NONNULL_PARAM_VIOLATION")
  public void testAttrs ()
  {
    final IMicroElement e = new MicroElement ("any");
    assertFalse (e.hasAttributes ());
    assertTrue (e.removeAllAttributes ().isUnchanged ());
    e.setAttribute ("attr", 5);
    assertTrue (e.hasAttributes ());
    assertEquals ("5", e.getAttributeValue ("attr"));
    assertTrue (e.removeAllAttributes ().isChanged ());
    assertTrue (e.removeAllAttributes ().isUnchanged ());

    try
    {
      e.setAttribute ((String) null, "ny");
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    // Set something that can be removed
    e.setAttribute ("myattr", "any");
    assertEquals ("any", e.getAttributeValue ("myattr"));
    assertEquals ("any", e.getAttributeValue (null, "myattr"));
    assertEquals ("any", e.getAttributeValue (new MicroQName ("myattr")));
    assertEquals ("any", e.getAttributeValue (new MicroQName (null, "myattr")));
    assertNull (e.getAttributeValue ("bla-namespace", "myattr"));
    assertTrue (e.hasAttribute ("myattr"));
    assertTrue (e.hasAttribute (null, "myattr"));
    assertTrue (e.hasAttribute (new MicroQName ("myattr")));
    assertTrue (e.hasAttribute (new MicroQName (null, "myattr")));
    assertFalse (e.hasAttribute ("bla-namespace", "myattr"));

    // set null value == remove attribute
    e.setAttribute ("myattr", (String) null);
    assertNull (e.getAttributeValue ("myattr"));
    assertFalse (e.hasAttribute ("myattr"));

    // Check with conversion
    e.setAttributeWithConversion ("myattr", new BigDecimal ("1234567890"));
    assertEquals ("1234567890", e.getAttributeValue ("myattr"));
  }

  @Test
  public void testChildren ()
  {
    final IMicroElement e = new MicroElement ("any");
    assertNull (e.appendChild (null));
    final IMicroElement eChild = e.appendElement ("child");
    assertNull (e.insertAfter (null, eChild));
    assertNull (e.insertBefore (null, eChild));

    assertNotNull (e.insertAfter (new MicroElement ("after"), eChild));
    assertNotNull (e.insertBefore (new MicroElement ("before"), eChild));
    assertEquals (3, e.getChildCount ());
    assertNotNull (e.appendChild (new MicroElement ("end")));
    assertNotNull (e.appendIgnorableWhitespaceText ("    "));
    assertNotNull (e.appendEntityReference ("name"));
    assertNotNull (e.appendProcessingInstruction ("target", "data"));
    assertEquals ("5", e.appendTextWithConversion (Integer.valueOf (5)).getData ().toString ());
    assertTrue (e.getChildAtIndex (0).isElement ());
    assertEquals (8, e.getAllChildrenRecursive ().size ());

    IMicroNode aChild1 = e.getFirstChild ();
    assertTrue (e.removeChild (aChild1).isChanged ());
    try
    {
      // child1 has no longer a parent!
      e.removeChild (aChild1);
      fail ();
    }
    catch (final MicroException ex)
    {}
    assertFalse (e.removeChild (aChild1.appendElement ("haha")).isChanged ());
    try
    {
      e.removeChild (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    assertEquals (7, e.getChildCount ());

    aChild1 = e.getFirstChild ();
    assertTrue (aChild1.isElement ());
    assertEquals ("child", aChild1.getNodeName ());
    assertFalse (e.replaceChild (aChild1, aChild1).isChanged ());
    final IMicroNode aChildNew = new MicroComment ("new");
    assertTrue (e.replaceChild (aChild1, aChildNew).isChanged ());
    assertTrue (e.getFirstChild ().isComment ());
    try
    {
      // No such child
      e.replaceChild (aChild1, aChildNew);
      fail ();
    }
    catch (final MicroException ex)
    {}
    assertTrue (e.getFirstChild ().isComment ());
    try
    {
      e.replaceChild (null, aChildNew);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      e.replaceChild (aChildNew, null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testGetParentElementWithName ()
  {
    final IMicroElement e = new MicroElement ("any");
    final IMicroElement eChild = e.appendElement ("child");
    final IMicroElement eChild2 = eChild.appendElement ("child2");
    final IMicroElement eChild3 = eChild2.appendElement ("child3");

    // 1 level
    assertSame (e, eChild.getParentElementWithName ("any"));
    assertSame (e, eChild.getParentElementWithName (null, "any"));
    assertNull (eChild.getParentElementWithName ("other"));
    assertNull (eChild.getParentElementWithName (null, "other"));

    // 2 level
    assertSame (e, eChild2.getParentElementWithName ("any"));
    assertSame (e, eChild2.getParentElementWithName (null, "any"));
    assertNull (eChild2.getParentElementWithName ("other"));
    assertNull (eChild2.getParentElementWithName (null, "other"));

    // 3 level
    assertSame (e, eChild3.getParentElementWithName ("any"));
    assertSame (e, eChild3.getParentElementWithName (null, "any"));
    assertNull (eChild3.getParentElementWithName ("other"));
    assertNull (eChild3.getParentElementWithName (null, "other"));
  }

  @Test
  public void testDetach ()
  {
    final IMicroElement e = new MicroElement ("root");
    assertFalse (e.hasParent ());
    assertSame (e, e.detachFromParent ());
    assertFalse (e.hasParent ());

    // Create child
    final IMicroElement eChild = e.appendElement ("c");
    assertTrue (eChild.hasParent ());
    assertEquals (1, e.getChildCount ());

    // Detach child
    assertSame (eChild, eChild.detachFromParent ());
    assertFalse (eChild.hasParent ());
    assertEquals (0, e.getChildCount ());
  }

  @Test
  public void testIsEqualContent ()
  {
    final IMicroElement eRoot = new MicroElement ("root");
    final IMicroElement e1 = eRoot.appendElement ("c1a");
    e1.appendElement ("c2");
    eRoot.appendElement ("c1b");
    // No children at all
    assertFalse (new MicroElement ("root").isEqualContent (new MicroElement ("root2")));
    // One with children one not
    assertFalse (eRoot.isEqualContent (new MicroElement ("root2")));
    assertFalse (new MicroElement ("root").isEqualContent (eRoot));
    // Different child count
    assertFalse (eRoot.isEqualContent (e1));
    // Same child count
    assertFalse (eRoot.isEqualContent (e1));
    // Same child count, first equal content than different content
    final IMicroElement el1 = new MicroElement ("x");
    el1.appendElement ("y1");
    el1.appendElement ("y2");
    final IMicroElement el2 = new MicroElement ("xx");
    el2.appendElement ("y1");
    el2.appendElement ("z1");
    assertFalse (el1.isEqualContent (el2));
    assertFalse (el2.isEqualContent (el1));
  }

  @Test
  public void testGetAllChildElementsRecursive ()
  {
    final IMicroElement e = new MicroElement ("x");
    assertNotNull (e.getAllChildElementsRecursive ());
    assertEquals (0, e.getAllChildElementsRecursive ().size ());

    e.appendText ("foo");
    assertNotNull (e.getAllChildElementsRecursive ());
    assertEquals (0, e.getAllChildElementsRecursive ().size ());
    assertEquals (1, e.getAllChildrenRecursive ().size ());

    final IMicroElement e1 = e.appendElement ("y1");
    assertEquals (1, e.getAllChildElementsRecursive ().size ());
    assertEquals (2, e.getAllChildrenRecursive ().size ());

    e1.appendComment ("doesn't really matter");
    assertEquals (1, e.getAllChildElementsRecursive ().size ());
    assertEquals (3, e.getAllChildrenRecursive ().size ());

    final IMicroElement e2 = e.appendElement ("y2");
    assertEquals (2, e.getAllChildElementsRecursive ().size ());
    assertEquals (4, e.getAllChildrenRecursive ().size ());

    e2.appendComment ("doesn't really matter");
    assertEquals (2, e.getAllChildElementsRecursive ().size ());
    assertEquals (5, e.getAllChildrenRecursive ().size ());

    e1.appendElement ("y11");
    assertEquals (3, e.getAllChildElementsRecursive ().size ());
    assertEquals (6, e.getAllChildrenRecursive ().size ());

    e2.appendElement ("y21");
    assertEquals (4, e.getAllChildElementsRecursive ().size ());
    assertEquals (7, e.getAllChildrenRecursive ().size ());

    assertEquals (1, e1.getAllChildElementsRecursive ().size ());
    assertEquals (1, e2.getAllChildElementsRecursive ().size ());
  }
}
