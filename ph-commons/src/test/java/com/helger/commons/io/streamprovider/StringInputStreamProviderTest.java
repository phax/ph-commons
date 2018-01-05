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
package com.helger.commons.io.streamprovider;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

import com.helger.commons.io.stream.StreamHelper;
import com.helger.commons.mock.CommonsTestHelper;

/**
 * Test class for class {@link StringInputStreamProvider}.
 *
 * @author Philip Helger
 */
public final class StringInputStreamProviderTest
{
  @Test
  public void testSimpleCharset ()
  {
    final String s = "Hallo Weltäöü";

    // String constructor
    InputStream aIS = new StringInputStreamProvider (s, StandardCharsets.UTF_8).getInputStream ();
    assertEquals (s, StreamHelper.getAllBytesAsString (aIS, StandardCharsets.UTF_8));

    // char[] constructor
    aIS = new StringInputStreamProvider (s.toCharArray (), StandardCharsets.UTF_8).getInputStream ();
    assertEquals (s, StreamHelper.getAllBytesAsString (aIS, StandardCharsets.UTF_8));

    // CharSequence constructor
    aIS = new StringInputStreamProvider (new StringBuilder (s), StandardCharsets.UTF_8).getInputStream ();
    assertEquals (s, StreamHelper.getAllBytesAsString (aIS, StandardCharsets.UTF_8));
  }

  @Test
  public void testBOM ()
  {
    // BOM is emitted
    byte [] aBytes = StreamHelper.getAllBytes (new StringInputStreamProvider ("abc", StandardCharsets.UTF_16));
    assertArrayEquals (new byte [] { (byte) 0xfe, (byte) 0xff, 0, 'a', 0, 'b', 0, 'c' }, aBytes);

    // No BOM is emitted!
    aBytes = StreamHelper.getAllBytes (new StringInputStreamProvider ("abc", StandardCharsets.UTF_16BE));
    assertArrayEquals (new byte [] { 0, 'a', 0, 'b', 0, 'c' }, aBytes);

    // No BOM is emitted!
    aBytes = StreamHelper.getAllBytes (new StringInputStreamProvider ("abc", StandardCharsets.UTF_16LE));
    assertArrayEquals (new byte [] { 'a', 0, 'b', 0, 'c', 0 }, aBytes);

    // No BOM is emitted!
    aBytes = StreamHelper.getAllBytes (new StringInputStreamProvider ("abc", StandardCharsets.UTF_8));
    assertArrayEquals (new byte [] { 'a', 'b', 'c' }, aBytes);
  }

  @Test
  public void testSerialization ()
  {
    CommonsTestHelper.testDefaultSerialization (new StringInputStreamProvider ("Hallo Weltäöü",
                                                                               StandardCharsets.UTF_8));
  }
}
