/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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
package com.helger.io.stream;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

import com.helger.base.io.iface.IHasInputStream;
import com.helger.base.io.nonblocking.NonBlockingByteArrayInputStream;
import com.helger.base.io.stream.StreamHelper;
import com.helger.base.io.streamprovider.ByteArrayInputStreamProvider;
import com.helger.collection.commons.ICommonsList;
import com.helger.io.resource.ClassPathResource;
import com.helger.io.resource.IReadableResource;

/**
 * Test class for class {@link StreamHelperExt}.
 *
 * @author Philip Helger
 */
public final class StreamHelperExtTest
{
  @Test
  public void testReadLines ()
  {
    assertNull (StreamHelperExt.readStreamLines ((IReadableResource) null, StandardCharsets.ISO_8859_1));
    assertNull (StreamHelperExt.readStreamLines (new ClassPathResource ("gibts-ned"), StandardCharsets.ISO_8859_1));
    assertNull (StreamHelperExt.readStreamLines (ClassPathResource.getInputStream ("gibts-ned"),
                                                 StandardCharsets.ISO_8859_1));

    final IReadableResource aRes = new ClassPathResource ("streamutils-lines.txt");

    // Read all lines
    ICommonsList <String> aLines = StreamHelperExt.readStreamLines (aRes, StandardCharsets.UTF_8);
    assertNotNull (aLines);
    assertEquals (10, aLines.size ());
    for (int i = 0; i < 10; ++i)
      assertEquals (Integer.toString (i + 1), aLines.get (i));

    // Read only partial amount of lines
    aLines = StreamHelperExt.readStreamLines (aRes, StandardCharsets.UTF_8, 2, 4);
    assertNotNull (aLines);
    assertEquals (4, aLines.size ());
    for (int i = 0; i < 4; ++i)
      assertEquals (Integer.toString (i + 3), aLines.get (i));

    // Skip more than available
    aLines = StreamHelperExt.readStreamLines (aRes, StandardCharsets.UTF_8, Integer.MAX_VALUE, Integer.MAX_VALUE);
    assertNotNull (aLines);
    assertEquals (0, aLines.size ());

    // Try to read more than available
    aLines = StreamHelperExt.readStreamLines (aRes, StandardCharsets.UTF_8, 9, Integer.MAX_VALUE);
    assertNotNull (aLines);
    assertEquals (1, aLines.size ());

    // Read 0 lines
    aLines = StreamHelperExt.readStreamLines (aRes, StandardCharsets.UTF_8, 0, 0);
    assertNotNull (aLines);
    assertEquals (0, aLines.size ());

    try
    {
      // Lines to skip may not be negative
      StreamHelperExt.readStreamLines (aRes, StandardCharsets.UTF_8, -1, 4);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {
      // expected
    }

    try
    {
      // Lines to read may not be negative
      StreamHelperExt.readStreamLines (aRes, StandardCharsets.UTF_8, 0, -2);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {
      // expected
    }
  }

  @Test
  public void testGetAllBytesAsString ()
  {
    final IReadableResource aRes = new ClassPathResource ("streamutils-bytes.txt");
    assertEquals ("abc", StreamHelper.getAllBytesAsString (aRes, StandardCharsets.UTF_8));
    // Non existing
    assertNull (StreamHelper.getAllBytesAsString (new ClassPathResource ("bla fasel"), StandardCharsets.UTF_8));
  }

  @Test
  public void testGetAllBytesCharset ()
  {
    final String sInput = "Hallo";
    final byte [] aInput = sInput.getBytes (StandardCharsets.ISO_8859_1);
    assertArrayEquals (aInput, StreamHelper.getAllBytes (new ByteArrayInputStreamProvider (aInput)));
    assertArrayEquals (aInput, StreamHelper.getAllBytes (new NonBlockingByteArrayInputStream (aInput)));
    assertNull (StreamHelper.getAllBytes ((IHasInputStream) null));
    assertNull (StreamHelper.getAllBytes ((InputStream) null));

    assertEquals (sInput,
                  StreamHelper.getAllBytesAsString (new ByteArrayInputStreamProvider (aInput),
                                                    StandardCharsets.ISO_8859_1));
    assertEquals (sInput,
                  StreamHelper.getAllBytesAsString (new NonBlockingByteArrayInputStream (aInput),
                                                    StandardCharsets.ISO_8859_1));
    assertNull (StreamHelper.getAllBytesAsString ((IHasInputStream) null, StandardCharsets.ISO_8859_1));
    assertNull (StreamHelper.getAllBytesAsString ((InputStream) null, StandardCharsets.ISO_8859_1));
    try
    {
      StreamHelper.getAllBytesAsString (new NonBlockingByteArrayInputStream (aInput), (Charset) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {
      // Expected
    }
  }
}
