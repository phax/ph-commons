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
package com.helger.base.io.nonblocking;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

/**
 * Test class for class {@link NonBlockingStringReader}.
 *
 * @author Philip Helger
 */
public final class NonBlockingStringReaderTest
{
  @Test
  public void testOffsetAndLength () throws IOException
  {
    final char [] aChars = "0123456789".toCharArray ();
    try (final NonBlockingStringReader aReader = new NonBlockingStringReader (aChars, 2, 3))
    {
      assertEquals ('2', aReader.read ());
      assertEquals ('3', aReader.read ());
      assertEquals ('4', aReader.read ());
      assertEquals (-1, aReader.read ());
      assertEquals (0, aReader.skip (5));
    }

    try (final NonBlockingStringReader aReader = new NonBlockingStringReader ("0123456789", 5, 5))
    {
      final char [] aBuf = new char [10];
      assertEquals (5, aReader.read (aBuf, 0, aBuf.length));
      assertEquals ("56789", new String (aBuf, 0, 5));
      assertEquals (-1, aReader.read (aBuf, 0, aBuf.length));
    }

    try (final NonBlockingStringReader aReader = new NonBlockingStringReader ("0123456789", 4, 4))
    {
      assertEquals (2, aReader.skip (2));
      // Skipping is bounded by the end of the data
      assertEquals (2, aReader.skip (10));
      assertEquals (-1, aReader.read ());
    }
  }
}
