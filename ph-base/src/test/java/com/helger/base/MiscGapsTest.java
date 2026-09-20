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
package com.helger.base;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Predicate;

import org.junit.Test;

import com.helger.base.io.nonblocking.NonBlockingByteArrayInputStream;
import com.helger.base.io.nonblocking.NonBlockingByteArrayOutputStream;
import com.helger.base.io.nonblocking.NonBlockingCharArrayWriter;
import com.helger.base.lang.EnumHelper;
import com.helger.base.lang.clazz.ClassHelper;
import com.helger.base.mock.CommonsAssert;
import com.helger.base.numeric.MathHelper;
import com.helger.base.pool.ObjectPool;
import com.helger.base.serialize.SerializationHelper;
import com.helger.base.state.EChange;

/**
 * Test class for the remaining gaps of several base classes.
 *
 * @author Philip Helger
 */
public final class MiscGapsTest
{
  @Test
  public void testMathHelperHypot ()
  {
    CommonsAssert.assertEquals (5, MathHelper.hypot (3, 4));
    CommonsAssert.assertEquals (5, MathHelper.hypot (4, 3));
    CommonsAssert.assertEquals (5, MathHelper.hypot (-3, -4));
    // One of the values is 0
    CommonsAssert.assertEquals (4, MathHelper.hypot (0, 4));
    CommonsAssert.assertEquals (3, MathHelper.hypot (3, 0));
    CommonsAssert.assertEquals (0, MathHelper.hypot (0, 0));
  }

  @Test
  public void testEnumHelperGetAll ()
  {
    final List <EChange> aAll = EnumHelper.getAll (EChange.class, null);
    assertEquals (EChange.values ().length, aAll.size ());

    final List <EChange> aFiltered = EnumHelper.getAll (EChange.class, EChange::isChanged);
    assertEquals (1, aFiltered.size ());
    assertEquals (EChange.CHANGED, aFiltered.get (0));

    assertTrue (EnumHelper.getAll (EChange.class, (Predicate <EChange>) x -> false).isEmpty ());
  }

  @Test
  public void testClassHelperAreConvertibleClasses ()
  {
    assertTrue (ClassHelper.areConvertibleClasses (String.class, String.class));
    assertTrue (ClassHelper.areConvertibleClasses (String.class, CharSequence.class));
    assertFalse (ClassHelper.areConvertibleClasses (CharSequence.class, String.class));
    // Primitive and wrapper
    assertTrue (ClassHelper.areConvertibleClasses (int.class, Integer.class));
    assertTrue (ClassHelper.areConvertibleClasses (Integer.class, int.class));
    assertFalse (ClassHelper.areConvertibleClasses (String.class, Integer.class));
  }

  @Test
  public void testSerializationHelper ()
  {
    final String sSrc = "Hello world";
    final byte [] aBytes = SerializationHelper.getSerializedByteArray (sSrc);
    assertNotNull (aBytes);
    assertTrue (aBytes.length > 0);

    final String sDst = SerializationHelper.getDeserializedObject (aBytes);
    assertEquals (sSrc, sDst);
    assertNotSame (sSrc, sDst);

    // With an explicit filter
    assertEquals (sSrc, SerializationHelper.getDeserializedObject (aBytes, null));

    // Not serializable
    try
    {
      SerializationHelper.getSerializedByteArray (new java.io.Serializable ()
      {
        @SuppressWarnings ("unused")
        private final Object m_aObj = new Object ();
      });
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {
      // expected
    }
  }

  @Test
  public void testNonBlockingByteArrayOutputStream () throws java.io.IOException
  {
    try (final NonBlockingByteArrayOutputStream aBAOS = new NonBlockingByteArrayOutputStream ())
    {
      // Read from an InputStream
      try (final NonBlockingByteArrayInputStream aBAIS = new NonBlockingByteArrayInputStream ("abc".getBytes (StandardCharsets.ISO_8859_1)))
      {
        aBAOS.readFrom (aBAIS);
      }
      assertEquals ("abc", aBAOS.getAsString (StandardCharsets.ISO_8859_1));
      assertEquals ("bc", aBAOS.getAsString (1, 2, StandardCharsets.ISO_8859_1));
      assertEquals (3, aBAOS.size ());
      assertFalse (aBAOS.isEmpty ());
      assertTrue (aBAOS.isNotEmpty ());
      assertNotNull (aBAOS.toString ());
    }
  }

  @Test
  public void testNonBlockingCharArrayWriter ()
  {
    try (final NonBlockingCharArrayWriter aWriter = new NonBlockingCharArrayWriter ())
    {
      aWriter.write ("abcdef");
      assertEquals ("abcdef", aWriter.getAsString ());
      assertEquals ("bcd", aWriter.getAsString (1, 3));
      assertEquals (6, aWriter.getSize ());

      assertTrue (aWriter.startsWith ("abc".toCharArray ()));
      assertFalse (aWriter.startsWith ("bcd".toCharArray ()));
      assertFalse (aWriter.startsWith ("abcdefg".toCharArray ()));
    }
  }

  @Test
  public void testObjectPool ()
  {
    final ObjectPool <StringBuilder> aPool = new ObjectPool <> (2, StringBuilder::new);
    assertEquals (2, aPool.getPoolSize ());
    assertEquals (0, aPool.getBorrowedObjectCount ());

    final StringBuilder aSB1 = aPool.borrowObject ();
    assertNotNull (aSB1);
    assertEquals (1, aPool.getBorrowedObjectCount ());

    final StringBuilder aSB2 = aPool.borrowObject ();
    assertNotNull (aSB2);
    assertEquals (2, aPool.getBorrowedObjectCount ());
    assertNotNull (aPool.toString ());

    assertTrue (aPool.returnObject (aSB1).isSuccess ());
    assertTrue (aPool.returnObject (aSB2).isSuccess ());
    assertEquals (0, aPool.getBorrowedObjectCount ());

    // Returning an object that was not borrowed fails
    assertTrue (aPool.returnObject (new StringBuilder ()).isFailure ());

    // All unused items are removed
    aPool.clearUnusedItems ();
    assertEquals (2, aPool.getPoolSize ());
    assertNotNull (aPool.borrowObject ());
  }
}
