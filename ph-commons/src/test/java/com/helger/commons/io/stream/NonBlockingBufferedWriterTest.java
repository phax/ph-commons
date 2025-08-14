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
package com.helger.commons.io.stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;

import com.helger.base.io.nonblocking.NonBlockingBufferedWriter;
import com.helger.base.io.nonblocking.NonBlockingStringWriter;

/**
 * Test class for class {@link NonBlockingBufferedWriter}.
 *
 * @author Philip Helger
 */
public class NonBlockingBufferedWriterTest
{
  @Test
  public void testAll () throws IOException
  {
    final NonBlockingStringWriter aSW = new NonBlockingStringWriter ();
    try (final NonBlockingBufferedWriter aBBW = new NonBlockingBufferedWriter (aSW))
    {
      assertTrue (aBBW.isEmpty ());
      assertEquals (0, aBBW.getSize ());
      aBBW.write ('a');
      assertFalse (aBBW.isEmpty ());
      assertEquals (1, aBBW.getSize ());
      aBBW.write ("bc".toCharArray ());
      aBBW.write ("de".toCharArray (), 0, 1);
      aBBW.write ("ef");
      aBBW.write ("fgh", 1, 1);
      assertEquals ("abcdefg", aBBW.getAsString ());
      assertEquals (7, aBBW.getSize ());
      aBBW.append ('0').append ("12").append ("234", 1, 2);
      assertEquals ("abcdefg0123", aBBW.getAsString ());
      aBBW.append (null).append (null, 1, 2);
      assertEquals ("abcdefg0123nullu", aBBW.getAsString ());
      aBBW.flush ();
    }
  }

  @Test
  @SuppressWarnings ("resource")
  public void testError () throws IOException
  {
    try
    {
      new NonBlockingStringWriter (-1);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    try
    {
      new NonBlockingBufferedWriter (new NonBlockingStringWriter (-1));
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    final char [] ca = "abc".toCharArray ();
    try
    {
      new NonBlockingBufferedWriter (new NonBlockingStringWriter (-1)).write (ca, -1, 1);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    try
    {
      new NonBlockingBufferedWriter (new NonBlockingStringWriter (-1)).write (ca, 0, -1);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    try
    {
      new NonBlockingBufferedWriter (new NonBlockingStringWriter (-1)).write (ca, 2, 5);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
  }
}
