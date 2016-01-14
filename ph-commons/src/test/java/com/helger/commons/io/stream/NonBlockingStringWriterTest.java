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
package com.helger.commons.io.stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Test class for class {@link NonBlockingStringWriter}.
 *
 * @author Philip Helger
 */
public final class NonBlockingStringWriterTest
{
  @Test
  @SuppressFBWarnings ("TQ_NEVER_VALUE_USED_WHERE_ALWAYS_REQUIRED")
  public void testAll () throws IOException
  {
    try (final NonBlockingStringWriter aSW = new NonBlockingStringWriter ())
    {
      assertTrue (aSW.isEmpty ());
      assertEquals (0, aSW.getSize ());
      aSW.write ('a');
      assertFalse (aSW.isEmpty ());
      assertEquals (1, aSW.getSize ());
      aSW.write ("bc".toCharArray ());
      aSW.write ("de".toCharArray (), 0, 1);
      aSW.write ("ef");
      aSW.write ("fgh", 1, 1);
      assertEquals ("abcdefg", aSW.getAsString ());
      assertEquals (7, aSW.getSize ());
      aSW.append ('0').append ("12").append ("234", 1, 2);
      assertEquals ("abcdefg0123", aSW.getAsString ());
      assertEquals ("abcdefg0123", aSW.directGetStringBuilder ().toString ());
      aSW.append (null).append (null, 1, 2);
      assertEquals ("abcdefg0123nullu", aSW.getAsString ());
      aSW.flush ();
    }

    try
    {
      new NonBlockingStringWriter (-1);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    final char [] ca = "abc".toCharArray ();
    try
    {
      new NonBlockingStringWriter ().write (ca, -1, 1);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    try
    {
      new NonBlockingStringWriter ().write (ca, 0, -1);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    try
    {
      new NonBlockingStringWriter ().write (ca, 2, 5);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
  }
}
