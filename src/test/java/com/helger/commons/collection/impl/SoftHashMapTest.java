package com.helger.commons.collection.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.mutable.MutableBoolean;

public final class SoftHashMapTest
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (SoftHashMapTest.class);

  @Test
  public void testBasic ()
  {
    final MutableBoolean aChange = new MutableBoolean (false);
    final SoftHashMap <Integer, BigDecimal> map = new SoftHashMap <Integer, BigDecimal> ()
    {
      @Override
      protected void onRemoved (final Integer aKey)
      {
        s_aLogger.info ("Removed key " + aKey);
        aChange.set (true);
      }
    };

    BigDecimal aOne = new BigDecimal ("+1.000");
    final Integer aKey = Integer.valueOf (1);
    map.put (aKey, aOne);
    s_aLogger.info ("Mapped value: " + map.get (aKey));
    assertNotNull (map.get (aKey));
    aOne = null;

    final Set <Map.Entry <Integer, BigDecimal>> aEntries = map.entrySet ();
    assertEquals (1, aEntries.size ());

    try
    {
      final ArrayList <Object []> allocations = new ArrayList <Object []> ();
      int size;
      while ((size = Math.min (Math.abs ((int) Runtime.getRuntime ().freeMemory ()), Integer.MAX_VALUE)) > 0)
        allocations.add (new Object [size]);
    }
    catch (final OutOfMemoryError e)
    {
      // great!
    }
    s_aLogger.info ("Mapped value: " + map.get (aKey));
    assertNull (map.get (aKey));
  }
}
