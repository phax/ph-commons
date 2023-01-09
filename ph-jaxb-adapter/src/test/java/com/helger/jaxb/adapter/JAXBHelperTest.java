/*
 * Copyright (C) 2014-2023 Philip Helger (www.helger.com)
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

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import javax.xml.namespace.QName;

import org.junit.Test;

import com.helger.commons.equals.EqualsHelper;

import jakarta.xml.bind.JAXBElement;

/**
 * Test class for class {@link JAXBHelper}.
 *
 * @author Philip Helger
 */
public final class JAXBHelperTest
{
  @Test
  public void testClone ()
  {
    final JAXBElement <String> aJE = new JAXBElement <> (new QName ("urn:example.org", "test"), String.class, null, "any");
    final JAXBElement <String> aClone = JAXBHelper.getClonedJAXBElement (aJE);
    assertNotNull (aClone);
    assertNotSame (aJE, aClone);
    assertNotEquals (aJE, aClone);
    assertTrue (EqualsHelper.equals (aJE, aClone));
  }

  @Test
  public void testCloneWithScope ()
  {
    final JAXBElement <String> aJE = new JAXBElement <> (new QName ("urn:example.org", "test2"), String.class, JAXBHelperTest.class, "any");
    final JAXBElement <String> aClone = JAXBHelper.getClonedJAXBElement (aJE);
    assertNotNull (aClone);
    assertNotSame (aJE, aClone);
    assertNotEquals (aJE, aClone);
    assertTrue (EqualsHelper.equals (aJE, aClone));
  }
}
