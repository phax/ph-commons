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
package com.helger.tree.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.util.Comparator;

import org.junit.Test;

import com.helger.tree.DefaultTree;
import com.helger.tree.DefaultTreeItem;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.MicroElement;

/**
 * Test class for class {@link MicroTypeConverterTreeXML}.
 *
 * @author Philip Helger
 */
public final class MicroTypeConverterTreeXMLTest
{
  @Test
  public void testCreateWithoutNamespace ()
  {
    final MicroTypeConverterTreeXML <String> aConv = MicroTypeConverterTreeXML.create ("data", String.class);
    assertNull (aConv.getNamespaceURI ());
    assertEquals ("data", aConv.getElementName ());
    assertSame (String.class, aConv.getNativeClass ());
  }

  @Test
  public void testCreateWithNamespace ()
  {
    final MicroTypeConverterTreeXML <String> aConv = MicroTypeConverterTreeXML.create ("urn:example",
                                                                                       "data",
                                                                                       String.class);
    assertEquals ("urn:example", aConv.getNamespaceURI ());
    assertEquals ("data", aConv.getElementName ());
    assertSame (String.class, aConv.getNativeClass ());
  }

  @Test
  public void testAppendAndGetDataValue ()
  {
    final MicroTypeConverterTreeXML <String> aConv = MicroTypeConverterTreeXML.create ("data", String.class);

    final IMicroElement eDataElement = new MicroElement ("item");
    aConv.appendDataValue (eDataElement, "any value");
    assertNotNull (eDataElement.getFirstChildElement ());

    assertEquals ("any value", aConv.getAsDataValue (eDataElement));
  }

  @Test
  public void testGetDataValueOfEmptyElement ()
  {
    final MicroTypeConverterTreeXML <String> aConv = MicroTypeConverterTreeXML.create ("data", String.class);
    // No child element at all
    assertNull (aConv.getAsDataValue (new MicroElement ("item")));
  }

  @Test
  public void testTagNameMismatch ()
  {
    final MicroTypeConverterTreeXML <String> aConv = MicroTypeConverterTreeXML.create ("data", String.class);
    final IMicroElement eDataElement = new MicroElement ("item");
    eDataElement.addElement ("other");
    try
    {
      aConv.getAsDataValue (eDataElement);
      fail ();
    }
    catch (final IllegalStateException ex)
    {
      // expected
    }
  }

  @Test
  public void testNamespaceMismatch ()
  {
    final MicroTypeConverterTreeXML <String> aConv = MicroTypeConverterTreeXML.create ("urn:example",
                                                                                       "data",
                                                                                       String.class);
    final IMicroElement eDataElement = new MicroElement ("item");
    // No namespace at all
    eDataElement.addElement ("data");
    try
    {
      aConv.getAsDataValue (eDataElement);
      fail ();
    }
    catch (final IllegalStateException ex)
    {
      // expected
    }
  }

  @Test
  public void testRoundTripInTree ()
  {
    final DefaultTree <String> aTree = new DefaultTree <> ();
    aTree.getRootItem ().createChildItem ("value1");
    aTree.getRootItem ().createChildItem ("value2");

    final IMicroElement aElement = TreeXMLConverter.getTreeAsXML (aTree,
                                                                  Comparator.comparing (DefaultTreeItem <String>::getData),
                                                                  MicroTypeConverterTreeXML.create ("data",
                                                                                                    String.class));
    assertNotNull (aElement);
  }

  @Test
  public void testInvalidParams ()
  {
    try
    {
      MicroTypeConverterTreeXML.create (null, String.class);
      fail ();
    }
    catch (final NullPointerException | IllegalArgumentException ex)
    {
      // expected
    }
    try
    {
      MicroTypeConverterTreeXML.create ("", String.class);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {
      // expected
    }
    try
    {
      MicroTypeConverterTreeXML.create ("data", null);
      fail ();
    }
    catch (final NullPointerException | IllegalArgumentException ex)
    {
      // expected
    }
  }
}
