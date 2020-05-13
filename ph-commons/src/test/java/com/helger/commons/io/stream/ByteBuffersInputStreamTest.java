/**
 * Copyright (C) 2014-2020 Philip Helger (www.helger.com)
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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.nio.ByteBuffer;

import org.junit.Test;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Test class for class {@link ByteBuffersInputStream}.
 *
 * @author Philip Helger
 */
public final class ByteBuffersInputStreamTest
{
  @Test
  @SuppressWarnings ("resource")
  @SuppressFBWarnings (value = "OS_OPEN_STREAM")
  public void testAll ()
  {
    try
    {
      new ByteBuffersInputStream ((ByteBuffer []) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    final ByteBuffer buf = ByteBuffer.allocate (100);
    final ByteBuffersInputStream bbis = new ByteBuffersInputStream (buf);
    assertEquals (100, bbis.available ());
    bbis.read ();
    assertEquals (1, bbis.skip (1));
    assertEquals (98, bbis.available ());
    bbis.read (new byte [9]);
    bbis.read (new byte [0]);
    bbis.read (new byte [91]);
    assertEquals (-1, bbis.read ());
    assertEquals (0, bbis.skip (10));

    try
    {
      bbis.read ((byte []) null);
      bbis.close ();
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      bbis.read ((byte []) null, 0, 10);
      bbis.close ();
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    final byte [] x = new byte [100];
    try
    {
      bbis.read (x, -1, 1);
      bbis.close ();
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    try
    {
      bbis.read (x, 1, -1);
      bbis.close ();
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    try
    {
      assertEquals (0, bbis.read (x, 90, 20));
      bbis.close ();
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    assertTrue (bbis.markSupported ());
    bbis.mark (0);
    bbis.reset ();
  }
}
