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
package com.helger.io.resource.inmemory;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

import com.helger.base.io.nonblocking.NonBlockingByteArrayInputStream;
import com.helger.base.io.stream.StreamHelper;
import com.helger.base.string.StringHelper;

/**
 * Test class for class {@link ReadableResourceByteArray}.
 *
 * @author Philip Helger
 */
public final class ReadableResourceByteArrayTest
{
  private static final byte [] BYTES = "abcdef".getBytes (StandardCharsets.ISO_8859_1);

  @Test
  public void testBasic ()
  {
    final ReadableResourceByteArray aRes = new ReadableResourceByteArray (BYTES);
    assertTrue (StringHelper.isNotEmpty (aRes.getResourceID ()));
    // The path is empty by default
    assertEquals ("", aRes.getPath ());
    aRes.setPath ("any/path");
    assertEquals ("any/path", aRes.getPath ());
    assertNull (aRes.getAsURL ());
    assertNull (aRes.getAsFile ());
    assertTrue (aRes.exists ());
    assertNotNull (aRes.toString ());

    assertTrue (aRes.isCopy ());
    assertEquals (0, aRes.getOffset ());
    assertEquals (BYTES.length, aRes.size ());
    assertFalse (aRes.isEmpty ());
    assertTrue (aRes.isReadMultiple ());
    assertArrayEquals (BYTES, aRes.bytes ());

    try (final InputStream aIS = aRes.getInputStream ())
    {
      assertArrayEquals (BYTES, StreamHelper.getAllBytes (aIS));
    }
    catch (final Exception ex)
    {
      fail (ex.getMessage ());
    }

    // The resource can be read multiple times
    assertArrayEquals (BYTES, StreamHelper.getAllBytes (aRes.getInputStream ()));
  }

  @Test
  public void testCtors ()
  {
    // With an explicit resource ID
    assertEquals ("my-id", new ReadableResourceByteArray ("my-id", BYTES).getResourceID ());
    assertEquals ("my-id", new ReadableResourceByteArray ("my-id", BYTES, false).getResourceID ());
    assertEquals ("my-id", new ReadableResourceByteArray ("my-id", BYTES, 1, 2).getResourceID ());
    assertEquals ("my-id", new ReadableResourceByteArray ("my-id", BYTES, 1, 2, false).getResourceID ());

    // Offset and length
    final ReadableResourceByteArray aRes = new ReadableResourceByteArray (BYTES, 1, 2);
    assertEquals (2, aRes.size ());
    assertArrayEquals ("bc".getBytes (StandardCharsets.ISO_8859_1), StreamHelper.getAllBytes (aRes.getInputStream ()));

    // Without copying, the offset is maintained separately
    final ReadableResourceByteArray aNoCopy = new ReadableResourceByteArray (BYTES, 1, 2, false);
    assertFalse (aNoCopy.isCopy ());
    assertEquals (1, aNoCopy.getOffset ());
    assertEquals (2, aNoCopy.size ());
    assertSame (BYTES, aNoCopy.bytes ());

    assertFalse (new ReadableResourceByteArray (BYTES, false).isCopy ());
    assertTrue (new ReadableResourceByteArray (BYTES, true).isCopy ());

    // Empty byte array
    final ReadableResourceByteArray aEmpty = new ReadableResourceByteArray (new byte [0]);
    assertTrue (aEmpty.isEmpty ());
    assertEquals (0, aEmpty.size ());

    try
    {
      new ReadableResourceByteArray (BYTES, 1, BYTES.length);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {
      // expected
    }
  }

  @Test
  public void testInputStreamResource ()
  {
    try (final NonBlockingByteArrayInputStream aIS = new NonBlockingByteArrayInputStream (BYTES))
    {
      final ReadableResourceInputStream aRes = new ReadableResourceInputStream ("my-id", aIS);
      assertEquals ("my-id", aRes.getResourceID ());
      assertFalse (aRes.isReadMultiple ());
      assertNotNull (aRes.toString ());
      assertArrayEquals (BYTES, StreamHelper.getAllBytes (aRes.getInputStream ()));
    }
  }
}
