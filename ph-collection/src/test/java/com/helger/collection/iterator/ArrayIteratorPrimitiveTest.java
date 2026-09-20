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
package com.helger.collection.iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.NoSuchElementException;

import org.junit.Test;

import com.helger.base.mock.CommonsAssert;

/**
 * Test class for the primitive array iterator classes.
 *
 * @author Philip Helger
 */
public final class ArrayIteratorPrimitiveTest
{
  @Test
  public void testBoolean ()
  {
    final ArrayIteratorBoolean aIt = new ArrayIteratorBoolean (true, false);
    assertTrue (aIt.hasNext ());
    assertTrue (aIt.next ());
    assertFalse (aIt.next ());
    assertFalse (aIt.hasNext ());
    try
    {
      aIt.next ();
      fail ();
    }
    catch (final NoSuchElementException ex)
    {
      // expected
    }

    // Offset and length
    final ArrayIteratorBoolean aIt2 = new ArrayIteratorBoolean (new boolean [] { true, false, true }, 1, 2);
    assertFalse (aIt2.next ());
    assertTrue (aIt2.next ());
    assertFalse (aIt2.hasNext ());

    assertNotNull (aIt.toString ());
    assertEquals (new ArrayIteratorBoolean (true, false), new ArrayIteratorBoolean (true, false));
    assertEquals (new ArrayIteratorBoolean (true).hashCode (), new ArrayIteratorBoolean (true).hashCode ());
    assertNotEquals (new ArrayIteratorBoolean (true), null);
    assertNotEquals (new ArrayIteratorBoolean (true), "any other type");
    assertNotEquals (new ArrayIteratorBoolean (true), new ArrayIteratorBoolean (false));
    assertNotEquals (new ArrayIteratorBoolean (true, false), aIt);
    assertEquals (aIt, aIt);
  }

  @Test
  public void testByte ()
  {
    final ArrayIteratorByte aIt = new ArrayIteratorByte ((byte) 1, (byte) 2);
    assertTrue (aIt.hasNext ());
    assertEquals (1, aIt.next ());
    assertEquals (2, aIt.next ());
    assertFalse (aIt.hasNext ());
    try
    {
      aIt.next ();
      fail ();
    }
    catch (final NoSuchElementException ex)
    {
      // expected
    }

    // Without copying
    final byte [] aBytes = new byte [] { 1, 2, 3 };
    final ArrayIteratorByte aIt2 = new ArrayIteratorByte (aBytes, false);
    assertEquals (1, aIt2.next ());
    assertEquals (2, aIt2.next ());
    assertEquals (3, aIt2.next ());
    assertFalse (aIt2.hasNext ());

    // Offset and length
    final ArrayIteratorByte aIt3 = new ArrayIteratorByte (aBytes, 1, 2);
    assertEquals (2, aIt3.next ());
    assertEquals (3, aIt3.next ());
    assertFalse (aIt3.hasNext ());

    // Offset and length without copying
    final ArrayIteratorByte aIt4 = new ArrayIteratorByte (aBytes, 1, 2, false);
    assertEquals (2, aIt4.next ());
    assertEquals (3, aIt4.next ());
    assertFalse (aIt4.hasNext ());

    final ArrayIteratorByte aIt5 = new ArrayIteratorByte (aBytes, 0, 2, false);
    assertEquals (1, aIt5.next ());
    assertEquals (2, aIt5.next ());
    assertFalse (aIt5.hasNext ());

    assertNotNull (aIt.toString ());
    assertEquals (new ArrayIteratorByte ((byte) 1), new ArrayIteratorByte ((byte) 1));
    assertEquals (new ArrayIteratorByte ((byte) 1).hashCode (), new ArrayIteratorByte ((byte) 1).hashCode ());
    assertNotEquals (new ArrayIteratorByte ((byte) 1), null);
    assertNotEquals (new ArrayIteratorByte ((byte) 1), "any other type");
    assertNotEquals (new ArrayIteratorByte ((byte) 1), new ArrayIteratorByte ((byte) 2));
    assertEquals (aIt, aIt);
  }

  @Test
  public void testChar ()
  {
    final ArrayIteratorChar aIt = new ArrayIteratorChar ('a', 'b');
    assertTrue (aIt.hasNext ());
    assertEquals ('a', aIt.next ());
    assertEquals ('b', aIt.next ());
    assertFalse (aIt.hasNext ());
    try
    {
      aIt.next ();
      fail ();
    }
    catch (final NoSuchElementException ex)
    {
      // expected
    }

    final ArrayIteratorChar aIt2 = new ArrayIteratorChar (new char [] { 'a', 'b', 'c' }, 1, 2);
    assertEquals ('b', aIt2.next ());

    assertNotNull (aIt.toString ());
    assertEquals (new ArrayIteratorChar ('a'), new ArrayIteratorChar ('a'));
    assertEquals (new ArrayIteratorChar ('a').hashCode (), new ArrayIteratorChar ('a').hashCode ());
    assertNotEquals (new ArrayIteratorChar ('a'), null);
    assertNotEquals (new ArrayIteratorChar ('a'), "any other type");
    assertNotEquals (new ArrayIteratorChar ('a'), new ArrayIteratorChar ('b'));
    assertEquals (aIt, aIt);
  }

  @Test
  public void testDouble ()
  {
    final ArrayIteratorDouble aIt = new ArrayIteratorDouble (1, 2);
    assertTrue (aIt.hasNext ());
    CommonsAssert.assertEquals (1, aIt.next ());
    CommonsAssert.assertEquals (2, aIt.next ());
    assertFalse (aIt.hasNext ());
    try
    {
      aIt.next ();
      fail ();
    }
    catch (final NoSuchElementException ex)
    {
      // expected
    }

    final ArrayIteratorDouble aIt2 = new ArrayIteratorDouble (new double [] { 1, 2, 3 }, 1, 2);
    CommonsAssert.assertEquals (2, aIt2.next ());

    assertNotNull (aIt.toString ());
    assertEquals (new ArrayIteratorDouble (1), new ArrayIteratorDouble (1));
    assertEquals (new ArrayIteratorDouble (1).hashCode (), new ArrayIteratorDouble (1).hashCode ());
    assertNotEquals (new ArrayIteratorDouble (1), null);
    assertNotEquals (new ArrayIteratorDouble (1), "any other type");
    assertNotEquals (new ArrayIteratorDouble (1), new ArrayIteratorDouble (2));
    assertEquals (aIt, aIt);
  }

  @Test
  public void testFloat ()
  {
    final ArrayIteratorFloat aIt = new ArrayIteratorFloat (1, 2);
    assertTrue (aIt.hasNext ());
    CommonsAssert.assertEquals (1f, aIt.next ());
    CommonsAssert.assertEquals (2f, aIt.next ());
    assertFalse (aIt.hasNext ());
    try
    {
      aIt.next ();
      fail ();
    }
    catch (final NoSuchElementException ex)
    {
      // expected
    }

    final float [] aArray = new float [] { 1, 2, 3 };
    CommonsAssert.assertEquals (2f, ArrayIteratorFloat.createOfsLen (aArray, 1, 2).next ());
    CommonsAssert.assertEquals (2f, ArrayIteratorFloat.createBeginEnd (aArray, 1, 3).next ());
    try
    {
      ArrayIteratorFloat.createBeginEnd (aArray, 2, 1);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {
      // expected
    }

    assertNotNull (aIt.toString ());
    assertEquals (new ArrayIteratorFloat (1), new ArrayIteratorFloat (1));
    assertEquals (new ArrayIteratorFloat (1).hashCode (), new ArrayIteratorFloat (1).hashCode ());
    assertNotEquals (new ArrayIteratorFloat (1), null);
    assertNotEquals (new ArrayIteratorFloat (1), "any other type");
    assertNotEquals (new ArrayIteratorFloat (1), new ArrayIteratorFloat (2));
    assertEquals (aIt, aIt);
  }

  @Test
  public void testInt ()
  {
    final ArrayIteratorInt aIt = new ArrayIteratorInt (1, 2);
    assertTrue (aIt.hasNext ());
    assertEquals (1, aIt.next ());
    assertEquals (2, aIt.next ());
    assertFalse (aIt.hasNext ());
    try
    {
      aIt.next ();
      fail ();
    }
    catch (final NoSuchElementException ex)
    {
      // expected
    }

    final ArrayIteratorInt aIt2 = new ArrayIteratorInt (new int [] { 1, 2, 3 }, 1, 2);
    assertEquals (2, aIt2.next ());

    assertNotNull (aIt.toString ());
    assertEquals (new ArrayIteratorInt (1), new ArrayIteratorInt (1));
    assertEquals (new ArrayIteratorInt (1).hashCode (), new ArrayIteratorInt (1).hashCode ());
    assertNotEquals (new ArrayIteratorInt (1), null);
    assertNotEquals (new ArrayIteratorInt (1), "any other type");
    assertNotEquals (new ArrayIteratorInt (1), new ArrayIteratorInt (2));
    assertEquals (aIt, aIt);
  }

  @Test
  public void testLong ()
  {
    final ArrayIteratorLong aIt = new ArrayIteratorLong (1, 2);
    assertTrue (aIt.hasNext ());
    assertEquals (1, aIt.next ());
    assertEquals (2, aIt.next ());
    assertFalse (aIt.hasNext ());
    try
    {
      aIt.next ();
      fail ();
    }
    catch (final NoSuchElementException ex)
    {
      // expected
    }

    final ArrayIteratorLong aIt2 = new ArrayIteratorLong (new long [] { 1, 2, 3 }, 1, 2);
    assertEquals (2, aIt2.next ());

    assertNotNull (aIt.toString ());
    assertEquals (new ArrayIteratorLong (1), new ArrayIteratorLong (1));
    assertEquals (new ArrayIteratorLong (1).hashCode (), new ArrayIteratorLong (1).hashCode ());
    assertNotEquals (new ArrayIteratorLong (1), null);
    assertNotEquals (new ArrayIteratorLong (1), "any other type");
    assertNotEquals (new ArrayIteratorLong (1), new ArrayIteratorLong (2));
    assertEquals (aIt, aIt);
  }

  @Test
  public void testShort ()
  {
    final ArrayIteratorShort aIt = new ArrayIteratorShort ((short) 1, (short) 2);
    assertTrue (aIt.hasNext ());
    assertEquals (1, aIt.next ());
    assertEquals (2, aIt.next ());
    assertFalse (aIt.hasNext ());
    try
    {
      aIt.next ();
      fail ();
    }
    catch (final NoSuchElementException ex)
    {
      // expected
    }

    final ArrayIteratorShort aIt2 = new ArrayIteratorShort (new short [] { 1, 2, 3 }, 1, 2);
    assertEquals (2, aIt2.next ());

    assertNotNull (aIt.toString ());
    assertEquals (new ArrayIteratorShort ((short) 1), new ArrayIteratorShort ((short) 1));
    assertEquals (new ArrayIteratorShort ((short) 1).hashCode (), new ArrayIteratorShort ((short) 1).hashCode ());
    assertNotEquals (new ArrayIteratorShort ((short) 1), null);
    assertNotEquals (new ArrayIteratorShort ((short) 1), "any other type");
    assertNotEquals (new ArrayIteratorShort ((short) 1), new ArrayIteratorShort ((short) 2));
    assertEquals (aIt, aIt);
  }
}
