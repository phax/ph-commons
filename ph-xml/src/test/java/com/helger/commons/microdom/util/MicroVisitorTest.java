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
package com.helger.commons.microdom.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import javax.annotation.Nonnull;

import org.junit.Test;

import com.helger.commons.hierarchy.visit.DefaultHierarchyVisitorCallback;
import com.helger.commons.hierarchy.visit.EHierarchyVisitorReturn;
import com.helger.commons.hierarchy.visit.IHierarchyVisitorCallback;
import com.helger.commons.io.resource.ClassPathResource;
import com.helger.commons.microdom.IMicroDocument;
import com.helger.commons.microdom.IMicroNode;
import com.helger.commons.microdom.serialize.MicroReader;
import com.helger.commons.mutable.MutableInt;

/**
 * Test class for class {@link MicroVisitor}.
 *
 * @author Philip Helger
 */
public final class MicroVisitorTest
{
  @Test
  public void testVisitStatic ()
  {
    // Read file with processing instruction
    final IMicroDocument doc = MicroReader.readMicroXML (new ClassPathResource ("xml/xml-processing-instruction.xml"));
    assertNotNull (doc);

    // Count processing instruction
    final MutableInt aInt = new MutableInt (0);
    MicroVisitor.visit (doc, new DefaultHierarchyVisitorCallback <IMicroNode> ()
    {
      @Override
      @Nonnull
      public EHierarchyVisitorReturn onItemBeforeChildren (final IMicroNode aItem)
      {
        if (aItem.isProcessingInstruction ())
          aInt.inc ();
        return EHierarchyVisitorReturn.CONTINUE;
      }
    });
    assertEquals (3, aInt.intValue ());

    // Start from the root document -> only 1 left
    aInt.set (0);
    MicroVisitor.visit (doc.getDocumentElement (), new DefaultHierarchyVisitorCallback <IMicroNode> ()
    {
      @Override
      @Nonnull
      public EHierarchyVisitorReturn onItemBeforeChildren (@Nonnull final IMicroNode aItem)
      {
        if (aItem.isProcessingInstruction ())
          aInt.inc ();
        return EHierarchyVisitorReturn.CONTINUE;
      }
    });
    assertEquals (1, aInt.intValue ());

    try
    {
      MicroVisitor.visit (null, new DefaultHierarchyVisitorCallback <IMicroNode> ());
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      MicroVisitor.visit (doc, (IHierarchyVisitorCallback <IMicroNode>) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }
}
