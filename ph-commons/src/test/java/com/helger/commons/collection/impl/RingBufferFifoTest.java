package com.helger.commons.collection.impl;

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
