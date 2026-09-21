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
package com.helger.base.serialize;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.ObjectInputFilter;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

/**
 * Test class for class {@link SerializationHelper}.
 *
 * @author Philip Helger
 */
public final class SerializationHelperTest
{
  /** A class that is Serializable but contains a non-serializable member */
  private static final class MockNotSerializable implements Serializable
  {
    @SuppressWarnings ("unused")
    private final Object m_aObj = new Object ();
  }

  /** A class that fails with a "regular" IOException upon serialization */
  private static final class MockIOException implements Serializable
  {
    private void writeObject (@SuppressWarnings ("unused") final ObjectOutputStream aOOS) throws IOException
    {
      throw new IOException ("Mock IO error");
    }
  }

  /** A simple Serializable class */
  private static final class MockData implements Serializable
  {
    private final String m_sText;
    private final int m_nValue;

    MockData (final String sText, final int nValue)
    {
      m_sText = sText;
      m_nValue = nValue;
    }

    @Override
    public boolean equals (final Object o)
    {
      if (o == this)
        return true;
      if (o == null || !getClass ().equals (o.getClass ()))
        return false;
      final MockData rhs = (MockData) o;
      return m_sText.equals (rhs.m_sText) && m_nValue == rhs.m_nValue;
    }

    @Override
    public int hashCode ()
    {
      return m_sText.hashCode () * 31 + m_nValue;
    }
  }

  @Test
  public void testSerializeAndDeserialize ()
  {
    final String sSrc = "Hello world";
    final byte [] aBytes = SerializationHelper.getSerializedByteArray (sSrc);
    assertNotNull (aBytes);
    assertTrue (aBytes.length > 0);

    @SuppressWarnings ("deprecation")
    final String sDst = SerializationHelper.getDeserializedObject (aBytes);
    assertEquals (sSrc, sDst);
    assertNotSame (sSrc, sDst);

    // With an explicit filter
    assertEquals (sSrc, SerializationHelper.getDeserializedObject (aBytes, null));

    // Serializing the same object twice delivers the same bytes
    assertArrayEquals (aBytes, SerializationHelper.getSerializedByteArray (sSrc));
  }

  @Test
  public void testSerializeDifferentTypes ()
  {
    for (final Serializable aSrc : new Serializable [] { "any String", Integer.valueOf (17), Boolean.TRUE,
                                                         new BigDecimal ("1.5"), LocalDate.of (2020, 1, 2), "any bytes"
                                                                                                                       .getBytes (StandardCharsets.ISO_8859_1),
                                                         new MockData ("text", 17) })
    {
      final byte [] aBytes = SerializationHelper.getSerializedByteArray (aSrc);
      final Object aDst = SerializationHelper.getDeserializedObject (aBytes, null);
      if (aSrc instanceof final byte [] aSrcBytes)
        assertArrayEquals ((byte []) aDst, aSrcBytes);
      else
        assertEquals (aSrc, aDst);
    }

    // A mutable object is a real copy
    final List <String> aSrcList = new ArrayList <> ();
    aSrcList.add ("a");
    final List <String> aDstList = SerializationHelper.getDeserializedObject (SerializationHelper.getSerializedByteArray ((Serializable) aSrcList),
                                                                              null);
    assertEquals (aSrcList, aDstList);
    assertNotSame (aSrcList, aDstList);
    aDstList.add ("b");
    assertEquals (1, aSrcList.size ());
  }

  @Test
  public void testSerializeInvalid ()
  {
    // A non-serializable member
    try
    {
      SerializationHelper.getSerializedByteArray (new MockNotSerializable ());
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {
      // expected
      assertTrue (ex.getMessage (), ex.getMessage ().startsWith ("Not serializable: "));
    }

    // A "regular" IOException while writing
    try
    {
      SerializationHelper.getSerializedByteArray (new MockIOException ());
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {
      // expected
      assertTrue (ex.getMessage (), ex.getMessage ().startsWith ("Failed to write serializable object "));
      assertEquals ("Mock IO error", ex.getCause ().getMessage ());
    }

    // null is not allowed
    try
    {
      SerializationHelper.getSerializedByteArray (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {
      // expected
    }
  }

  @Test
  public void testDeserializeWithFilter ()
  {
    final MockData aData = new MockData ("text", 17);
    final byte [] aDataBytes = SerializationHelper.getSerializedByteArray (aData);

    // A filter that allows everything
    assertEquals (aData,
                  SerializationHelper.getDeserializedObject (aDataBytes, aInfo -> ObjectInputFilter.Status.ALLOWED));

    // A filter that decides nothing falls back to the default behaviour
    assertEquals (aData,
                  SerializationHelper.getDeserializedObject (aDataBytes, aInfo -> ObjectInputFilter.Status.UNDECIDED));

    // A filter that rejects everything
    try
    {
      SerializationHelper.getDeserializedObject (aDataBytes, aInfo -> ObjectInputFilter.Status.REJECTED);
      fail ();
    }
    catch (final IllegalStateException ex)
    {
      // expected
      assertEquals ("Failed to read serializable object", ex.getMessage ());
    }

    // A pattern based filter that only allows String
    try
    {
      SerializationHelper.getDeserializedObject (aDataBytes,
                                                 ObjectInputFilter.Config.createFilter ("java.lang.String;!*"));
      fail ();
    }
    catch (final IllegalStateException ex)
    {
      // expected
    }

    // Note: a String is stored as a plain data record and not as a class based
    // record, so no filter is invoked for it at all
    final byte [] aStringBytes = SerializationHelper.getSerializedByteArray ("Hello world");
    assertEquals ("Hello world",
                  SerializationHelper.getDeserializedObject (aStringBytes, aInfo -> ObjectInputFilter.Status.REJECTED));
  }

  @Test
  public void testDeserializeInvalid ()
  {
    // Not serialized data at all
    try
    {
      SerializationHelper.getDeserializedObject ("This is not a serialized object".getBytes (StandardCharsets.ISO_8859_1),
                                                 null);
      fail ();
    }
    catch (final IllegalStateException ex)
    {
      // expected
    }

    // An empty byte array
    try
    {
      SerializationHelper.getDeserializedObject (new byte [0], null);
      fail ();
    }
    catch (final IllegalStateException ex)
    {
      // expected
    }

    // Truncated data
    final byte [] aBytes = SerializationHelper.getSerializedByteArray ("Hello world");
    final byte [] aTruncated = new byte [aBytes.length / 2];
    System.arraycopy (aBytes, 0, aTruncated, 0, aTruncated.length);
    try
    {
      SerializationHelper.getDeserializedObject (aTruncated, null);
      fail ();
    }
    catch (final IllegalStateException ex)
    {
      // expected
    }

    // null is not allowed
    try
    {
      SerializationHelper.getDeserializedObject (null, null);
      fail ();
    }
    catch (final NullPointerException ex)
    {
      // expected
    }
  }
}
