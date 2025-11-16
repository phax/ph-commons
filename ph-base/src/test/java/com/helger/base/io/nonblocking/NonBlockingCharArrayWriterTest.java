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
package com.helger.base.io.nonblocking;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;

/**
 * Test class for class {@link NonBlockingCharArrayWriter}.
 *
 * @author Philip Helger
 */
public final class NonBlockingCharArrayWriterTest
{
  @Test
  public void testAll () throws IOException
  {
    try (final NonBlockingCharArrayWriter aCAW = new NonBlockingCharArrayWriter ())
    {
      assertTrue (aCAW.isEmpty ());
      assertEquals (0, aCAW.getSize ());
      aCAW.write ('a');
      assertFalse (aCAW.isEmpty ());
      assertEquals (1, aCAW.getSize ());
      aCAW.write ("bc".toCharArray ());
      aCAW.write ("de".toCharArray (), 0, 1);
      aCAW.write ("ef");
      aCAW.write ("fgh", 1, 1);
      assertEquals ("abcdefg", aCAW.getAsString ());
      assertEquals (7, aCAW.getSize ());
      aCAW.append ('0').append ("12").append ("234", 1, 2);
      assertEquals ("abcdefg0123", aCAW.getAsString ());
      aCAW.append (null).append (null, 1, 2);
      assertEquals ("abcdefg0123nullu", aCAW.getAsString ());
      aCAW.flush ();

      try (final NonBlockingStringWriter aSW = new NonBlockingStringWriter ())
      {
        aCAW.writeTo (aSW);
        assertEquals ("abcdefg0123nullu", aSW.getAsString ());
      }
    }
  }

  @Test
  @SuppressWarnings ("resource")
  public void testError ()
  {
    try
    {
      new NonBlockingCharArrayWriter (-1);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    final char [] ca = "abc".toCharArray ();
    try
    {
      new NonBlockingCharArrayWriter ().write (ca, -1, 1);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    try
    {
      new NonBlockingCharArrayWriter ().write (ca, 0, -1);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    try
    {
      new NonBlockingCharArrayWriter ().write (ca, 2, 5);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
  }
}
