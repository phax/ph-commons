/**
 * Copyright (C) 2014-2016 Philip Helger (www.helger.com)
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

import org.junit.Test;

import com.helger.commons.charset.CCharset;
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
    InputStream aIS = new StringInputStreamProvider (s, CCharset.CHARSET_UTF_8_OBJ).getInputStream ();
    assertEquals (s, StreamHelper.getAllBytesAsString (aIS, CCharset.CHARSET_UTF_8_OBJ));

    // char[] constructor
    aIS = new StringInputStreamProvider (s.toCharArray (), CCharset.CHARSET_UTF_8_OBJ).getInputStream ();
    assertEquals (s, StreamHelper.getAllBytesAsString (aIS, CCharset.CHARSET_UTF_8_OBJ));

    // CharSequence constructor
    aIS = new StringInputStreamProvider (new StringBuilder (s), CCharset.CHARSET_UTF_8_OBJ).getInputStream ();
    assertEquals (s, StreamHelper.getAllBytesAsString (aIS, CCharset.CHARSET_UTF_8_OBJ));
  }

  @Test
  public void testBOM ()
  {
    // BOM is emitted
    byte [] aBytes = StreamHelper.getAllBytes (new StringInputStreamProvider ("abc", CCharset.CHARSET_UTF_16_OBJ));
    assertArrayEquals (new byte [] { (byte) 0xfe, (byte) 0xff, 0, 'a', 0, 'b', 0, 'c' }, aBytes);

    // No BOM is emitted!
    aBytes = StreamHelper.getAllBytes (new StringInputStreamProvider ("abc", CCharset.CHARSET_UTF_16BE_OBJ));
    assertArrayEquals (new byte [] { 0, 'a', 0, 'b', 0, 'c' }, aBytes);

    // No BOM is emitted!
    aBytes = StreamHelper.getAllBytes (new StringInputStreamProvider ("abc", CCharset.CHARSET_UTF_16LE_OBJ));
    assertArrayEquals (new byte [] { 'a', 0, 'b', 0, 'c', 0 }, aBytes);

    // No BOM is emitted!
    aBytes = StreamHelper.getAllBytes (new StringInputStreamProvider ("abc", CCharset.CHARSET_UTF_8_OBJ));
    assertArrayEquals (new byte [] { 'a', 'b', 'c' }, aBytes);
  }

  @Test
  public void testSerialization ()
  {
    CommonsTestHelper.testDefaultSerialization (new StringInputStreamProvider ("Hallo Weltäöü",
                                                                               CCharset.CHARSET_UTF_8_OBJ));
  }
}
