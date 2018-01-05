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
package com.helger.collection.ring;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

/**
 * Unit test for class {@link RingBufferFifo}.
 *
 * @author Philip Helger
 */
public final class RingBufferFifoTest
{
  @SuppressWarnings ("unused")
  @Test
  public void testInvalid ()
  {
    try
    {
      new RingBufferFifo <String> (-1, true);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    try
    {
      new RingBufferFifo <String> (0, true);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    try
    {
      new RingBufferFifo <String> (-1, false);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
  }

  @Test
  public void testValidNoOverwrite ()
  {
    final RingBufferFifo <String> aRB = new RingBufferFifo <> (3, false);
    assertEquals (3, aRB.getCapacity ());
    assertEquals (0, aRB.getAvailable ());
    assertEquals (3, aRB.getRemainingCapacity ());
    assertTrue (aRB.put ("a").isChanged ());
    assertEquals (3, aRB.getCapacity ());
    assertEquals (1, aRB.getAvailable ());
    assertEquals (2, aRB.getRemainingCapacity ());
    assertTrue (aRB.put ("b").isChanged ());
    assertEquals (3, aRB.getCapacity ());
    assertEquals (2, aRB.getAvailable ());
    assertEquals (1, aRB.getRemainingCapacity ());
    assertTrue (aRB.put ("c").isChanged ());
    assertEquals (3, aRB.getCapacity ());
    assertEquals (3, aRB.getAvailable ());
    assertEquals (0, aRB.getRemainingCapacity ());
    assertFalse (aRB.put ("d").isChanged ());

    assertEquals (3, aRB.getCapacity ());
    assertEquals (3, aRB.getAvailable ());
    assertEquals (0, aRB.getRemainingCapacity ());
    assertEquals ("a", aRB.take ());
    assertEquals (3, aRB.getCapacity ());
    assertEquals (2, aRB.getAvailable ());
    assertEquals (1, aRB.getRemainingCapacity ());
    assertEquals ("b", aRB.take ());
    assertEquals (3, aRB.getCapacity ());
    assertEquals (1, aRB.getAvailable ());
    assertEquals (2, aRB.getRemainingCapacity ());
    assertEquals ("c", aRB.take ());
    assertEquals (3, aRB.getCapacity ());
    assertEquals (0, aRB.getAvailable ());
    assertEquals (3, aRB.getRemainingCapacity ());
    assertNull (aRB.take ());
    assertEquals (3, aRB.getCapacity ());
    assertEquals (0, aRB.getAvailable ());
    assertEquals (3, aRB.getRemainingCapacity ());
  }

  @Test
  public void testValidWithOverwrite ()
  {
    final RingBufferFifo <String> aRB = new RingBufferFifo <> (3, true);
    assertEquals (3, aRB.getCapacity ());
    assertEquals (0, aRB.getAvailable ());
    assertEquals (3, aRB.getRemainingCapacity ());
    assertTrue (aRB.put ("a").isChanged ());
    assertEquals (3, aRB.getCapacity ());
    assertEquals (1, aRB.getAvailable ());
    assertEquals (2, aRB.getRemainingCapacity ());
    assertTrue (aRB.put ("b").isChanged ());
    assertEquals (3, aRB.getCapacity ());
    assertEquals (2, aRB.getAvailable ());
    assertEquals (1, aRB.getRemainingCapacity ());
    assertTrue (aRB.put ("c").isChanged ());
    assertEquals (3, aRB.getCapacity ());
    assertEquals (3, aRB.getAvailable ());
    assertEquals (0, aRB.getRemainingCapacity ());
    assertTrue (aRB.put ("d").isChanged ());
    assertEquals (3, aRB.getCapacity ());
    assertEquals (3, aRB.getAvailable ());
    assertEquals (0, aRB.getRemainingCapacity ());

    assertEquals ("b", aRB.take ());
    assertEquals (3, aRB.getCapacity ());
    assertEquals (2, aRB.getAvailable ());
    assertEquals (1, aRB.getRemainingCapacity ());
    assertEquals ("c", aRB.take ());
    assertEquals (3, aRB.getCapacity ());
    assertEquals (1, aRB.getAvailable ());
    assertEquals (2, aRB.getRemainingCapacity ());
    assertEquals ("d", aRB.take ());
    assertEquals (3, aRB.getCapacity ());
    assertEquals (0, aRB.getAvailable ());
    assertEquals (3, aRB.getRemainingCapacity ());
    assertNull (aRB.take ());
    assertEquals (3, aRB.getCapacity ());
    assertEquals (0, aRB.getAvailable ());
    assertEquals (3, aRB.getRemainingCapacity ());
  }
}
