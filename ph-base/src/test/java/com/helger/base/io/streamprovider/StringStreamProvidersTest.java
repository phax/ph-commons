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
package com.helger.base.io.streamprovider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.nio.charset.StandardCharsets;

import org.junit.Test;

import com.helger.base.BaseTestHelper;
import com.helger.base.io.stream.StreamHelper;

/**
 * Test class for class {@link StringInputStreamProvider}, {@link StringReaderProvider} and
 * {@link StringWriterProvider}.
 *
 * @author Philip Helger
 */
public final class StringStreamProvidersTest
{
  private static final String TEXT = "Hello World";

  @Test
  public void testInputStreamProviderCtors ()
  {
    final StringInputStreamProvider a = new StringInputStreamProvider (TEXT, StandardCharsets.ISO_8859_1);
    assertEquals (TEXT, a.getData ());
    assertSame (StandardCharsets.ISO_8859_1, a.getCharset ());
    assertTrue (a.isReadMultiple ());
    assertNotNull (a.toString ());

    assertEquals (TEXT, new StringInputStreamProvider (TEXT.toCharArray (), StandardCharsets.ISO_8859_1).getData ());
    assertEquals ("Hello",
                  new StringInputStreamProvider (TEXT.toCharArray (), 0, 5, StandardCharsets.ISO_8859_1).getData ());
    assertEquals (TEXT,
                  new StringInputStreamProvider ((CharSequence) new StringBuilder (TEXT), StandardCharsets.ISO_8859_1)
                                                                                                                      .getData ());
  }

  @Test
  public void testInputStreamProviderStreams ()
  {
    final StringInputStreamProvider a = new StringInputStreamProvider (TEXT, StandardCharsets.ISO_8859_1);

    assertEquals (TEXT, StreamHelper.getAllBytesAsString (a.getInputStream (), StandardCharsets.ISO_8859_1));
    assertEquals (TEXT, StreamHelper.getAllCharactersAsString (a.getReader ()));
    assertEquals (TEXT, StreamHelper.getAllCharactersAsString (a.getReader (StandardCharsets.ISO_8859_1)));
  }

  @Test
  public void testInputStreamProviderEqualsHashcode ()
  {
    BaseTestHelper.testDefaultImplementationWithEqualContentObject (new StringInputStreamProvider (TEXT,
                                                                                                   StandardCharsets.ISO_8859_1),
                                                                    new StringInputStreamProvider (TEXT,
                                                                                                   StandardCharsets.ISO_8859_1));
    BaseTestHelper.testDefaultImplementationWithDifferentContentObject (new StringInputStreamProvider (TEXT,
                                                                                                       StandardCharsets.ISO_8859_1),
                                                                        new StringInputStreamProvider ("other",
                                                                                                       StandardCharsets.ISO_8859_1));
    BaseTestHelper.testDefaultImplementationWithDifferentContentObject (new StringInputStreamProvider (TEXT,
                                                                                                       StandardCharsets.ISO_8859_1),
                                                                        new StringInputStreamProvider (TEXT,
                                                                                                       StandardCharsets.UTF_16));
  }

  @Test
  public void testReaderProvider ()
  {
    final StringReaderProvider a = new StringReaderProvider (TEXT);
    assertEquals (TEXT, a.getData ());
    assertEquals (TEXT, StreamHelper.getAllCharactersAsString (a.getReader ()));
    assertNotNull (a.toString ());

    assertEquals (TEXT, new StringReaderProvider (TEXT.toCharArray ()).getData ());
    assertEquals ("Hello", new StringReaderProvider (TEXT.toCharArray (), 0, 5).getData ());
    assertEquals (TEXT, new StringReaderProvider ((CharSequence) new StringBuilder (TEXT)).getData ());

    BaseTestHelper.testDefaultImplementationWithEqualContentObject (a, new StringReaderProvider (TEXT));
    BaseTestHelper.testDefaultImplementationWithDifferentContentObject (a, new StringReaderProvider ("other"));
  }

  @Test
  public void testWriterProvider ()
  {
    final StringWriterProvider a = new StringWriterProvider ();
    assertNotNull (a.getWriter ());
    assertNotNull (a.toString ());

    // Each call gives a new writer
    assertNotNull (a.getWriter ());

    BaseTestHelper.testDefaultImplementationWithEqualContentObject (a, new StringWriterProvider ());
  }

  @Test
  public void testInvalidParams ()
  {
    try
    {
      new StringInputStreamProvider ((String) null, StandardCharsets.ISO_8859_1);
      fail ();
    }
    catch (final NullPointerException | IllegalArgumentException ex)
    {
      // expected
    }
    try
    {
      new StringReaderProvider ((String) null);
      fail ();
    }
    catch (final NullPointerException | IllegalArgumentException ex)
    {
      // expected
    }
  }
}
