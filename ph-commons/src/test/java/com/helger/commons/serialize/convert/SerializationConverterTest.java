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
package com.helger.commons.serialize.convert;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.jspecify.annotations.NonNull;
import org.junit.Test;

import com.helger.base.io.nonblocking.NonBlockingByteArrayInputStream;
import com.helger.base.io.nonblocking.NonBlockingByteArrayOutputStream;
import com.helger.base.numeric.mutable.MutableInt;
import com.helger.base.state.EContinue;
import com.helger.typeconvert.TypeConverterException;
import com.helger.typeconvert.TypeConverterException.EReason;

/**
 * Test class for class {@link SerializationConverter} and {@link SerializationConverterRegistry}.
 *
 * @author Philip Helger
 */
public final class SerializationConverterTest
{
  /** A class that is not serializable and has no converter */
  private static final class MockNotSerializable
  {
    // empty
  }

  @Test
  public void testRegistry ()
  {
    final SerializationConverterRegistry aRegistry = SerializationConverterRegistry.getInstance ();
    assertTrue (SerializationConverterRegistry.isInstantiated ());
    assertSame (aRegistry, SerializationConverterRegistry.getInstance ());
    assertTrue (aRegistry.getRegisteredSerializationConverterCount () > 0);

    // Charset is registered by the default registrar
    assertNotNull (aRegistry.getConverter (Charset.class));
    // The concrete Charset implementation class is found via its class hierarchy
    assertNotNull (aRegistry.getConverter (StandardCharsets.UTF_8.getClass ()));
    // Only the registered classes and their sub classes have a converter, super classes and
    // interfaces of the registered classes have none
    assertNull (aRegistry.getConverter (MockNotSerializable.class));
    assertNull (aRegistry.getConverter (Object.class));
    assertNull (aRegistry.getConverter (Comparable.class));
    assertNull (aRegistry.getConverter (Image.class));
    assertNotNull (aRegistry.getConverter (BufferedImage.class));
    // Exactly the 2 classes of the default registrar are registered
    assertEquals (2, aRegistry.getRegisteredSerializationConverterCount ());

    final MutableInt aCount = new MutableInt (0);
    aRegistry.iterateAllRegisteredSerializationConverters ((aClass, aConverter) -> {
      aCount.inc ();
      return EContinue.CONTINUE;
    });
    assertEquals (aRegistry.getRegisteredSerializationConverterCount (), aCount.intValue ());

    // Break after the first one
    final MutableInt aCount2 = new MutableInt (0);
    aRegistry.iterateAllRegisteredSerializationConverters ((aClass, aConverter) -> {
      aCount2.inc ();
      return EContinue.BREAK;
    });
    assertEquals (1, aCount2.intValue ());

    // Registering something serializable is not allowed
    try
    {
      aRegistry.registerSerializationConverter (String.class, new ISerializationConverter <String> ()
      {
        public void writeConvertedObject (@NonNull final String aSourceObject, @NonNull final ObjectOutputStream aOOS)
        {}

        public String readConvertedObject (@NonNull final ObjectInputStream aOIS)
        {
          return null;
        }
      });
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {
      // expected
    }

    // Registering the same class twice is not allowed
    try
    {
      aRegistry.registerSerializationConverter (Charset.class, new ISerializationConverter <Charset> ()
      {
        public void writeConvertedObject (@NonNull final Charset aSourceObject, @NonNull final ObjectOutputStream aOOS)
        {}

        public Charset readConvertedObject (@NonNull final ObjectInputStream aOIS)
        {
          return null;
        }
      });
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {
      // expected
    }
  }

  @Test
  public void testWriteAndRead () throws IOException
  {
    // A Charset is not serializable, but a converter is present
    final Charset aCharset = StandardCharsets.ISO_8859_1;
    try (final NonBlockingByteArrayOutputStream aBAOS = new NonBlockingByteArrayOutputStream ())
    {
      try (final ObjectOutputStream aOOS = new ObjectOutputStream (aBAOS))
      {
        SerializationConverter.writeConvertedObject (aCharset, aOOS);
        SerializationConverter.writeConvertedObject (null, aOOS);
      }

      try (final ObjectInputStream aOIS = new ObjectInputStream (new NonBlockingByteArrayInputStream (aBAOS.toByteArray ())))
      {
        assertEquals (aCharset, SerializationConverter.readConvertedObject (aOIS, Charset.class));
        assertNull (SerializationConverter.readConvertedObject (aOIS, Charset.class));
      }
    }
  }

  @Test
  public void testWriteWithoutConverter () throws IOException
  {
    // No converter is present, so the dedicated exception must be thrown
    try (final NonBlockingByteArrayOutputStream aBAOS = new NonBlockingByteArrayOutputStream ();
         final ObjectOutputStream aOOS = new ObjectOutputStream (aBAOS))
    {
      SerializationConverter.writeConvertedObject (new MockNotSerializable (), aOOS);
      fail ();
    }
    catch (final TypeConverterException ex)
    {
      assertEquals (EReason.NO_CONVERTER_FOUND_SINGLE, ex.getReason ());
    }
  }

}
