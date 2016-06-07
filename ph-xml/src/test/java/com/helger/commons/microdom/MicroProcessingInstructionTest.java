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

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Test class for class {@link MicroProcessingInstruction}.
 *
 * @author Philip Helger
 */
public final class MicroProcessingInstructionTest
{
  @Test
  @SuppressFBWarnings (value = "NP_NONNULL_PARAM_VIOLATION")
  public void testCreation ()
  {
    IMicroProcessingInstruction e = new MicroProcessingInstruction ("xyz");
    assertNotNull (e);
    assertEquals ("xyz", e.getTarget ());
    assertNull (e.getData ());
    assertFalse (e.hasParent ());
    assertFalse (e.hasChildren ());
    assertNull (e.getAllChildren ());
    assertNull (e.getFirstChild ());
    assertNull (e.getLastChild ());
    assertNull (e.getChildAtIndex (0));
    assertNull (e.getAllChildrenRecursive ());
    assertEquals (0, e.getChildCount ());
    assertNotNull (e.getNodeName ());
    assertNotNull (e.getNodeValue ());
    assertSame (EMicroNodeType.PROCESSING_INSTRUCTION, e.getType ());
    CommonsTestHelper.testToStringImplementation (e);

    e = new MicroProcessingInstruction ("xyz", "data");
    assertNotNull (e);
    assertEquals ("xyz", e.getTarget ());
    assertEquals ("data", e.getData ());
    assertFalse (e.hasChildren ());
    assertNull (e.getAllChildren ());

    e = new MicroProcessingInstruction ("targ");
    assertNotNull (e);
    assertTrue (e.isEqualContent (e.getClone ()));
    e = new MicroProcessingInstruction ("targ", "data");
    assertNotNull (e);
    assertTrue (e.isEqualContent (e.getClone ()));

    assertTrue (e.isEqualContent (e));
    assertFalse (e.isEqualContent (null));
    assertFalse (e.isEqualContent (new MicroDocument ()));

    assertTrue (new MicroProcessingInstruction ("xyz").isEqualContent (new MicroProcessingInstruction ("xyz")));
    assertTrue (new MicroProcessingInstruction ("xyz",
                                                "data").isEqualContent (new MicroProcessingInstruction ("xyz",
                                                                                                        "data")));
    assertFalse (new MicroProcessingInstruction ("xyz").isEqualContent (new MicroProcessingInstruction ("xy")));
    assertFalse (new MicroProcessingInstruction ("xyz", "data").isEqualContent (new MicroProcessingInstruction ("xyz",
                                                                                                                null)));
    assertFalse (new MicroProcessingInstruction ("xyz",
                                                 "data").isEqualContent (new MicroProcessingInstruction ("xyz",
                                                                                                         "dat")));

    try
    {
      new MicroProcessingInstruction (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      new MicroProcessingInstruction ("");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
  }

  @Test
  public void testAddChildren ()
  {
    final IMicroProcessingInstruction e = new MicroProcessingInstruction ("xyz");

    try
    {
      // Cannot add any child to a comment
      e.appendChild (new MicroProcessingInstruction ("other"));
      fail ();
    }
    catch (final MicroException ex)
    {}

    try
    {
      // Cannot add any child to a comment
      e.insertAfter (new MicroProcessingInstruction ("other"), new MicroProcessingInstruction ("comment"));
      fail ();
    }
    catch (final MicroException ex)
    {}

    try
    {
      // Cannot add any child to a comment
      e.insertBefore (new MicroProcessingInstruction ("other"), new MicroProcessingInstruction ("comment"));
      fail ();
    }
    catch (final MicroException ex)
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
