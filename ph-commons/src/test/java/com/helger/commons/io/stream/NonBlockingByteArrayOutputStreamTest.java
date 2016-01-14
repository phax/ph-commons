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

import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;

import com.helger.commons.random.VerySecureRandom;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Test class for class {@link NonBlockingByteArrayOutputStream}.
 *
 * @author Philip Helger
 */
public final class NonBlockingByteArrayOutputStreamTest
{
  @SuppressWarnings ("resource")
  @Test
  @SuppressFBWarnings ("TQ_NEVER_VALUE_USED_WHERE_ALWAYS_REQUIRED")
  public void testAll () throws IOException
  {
    try
    {
      new NonBlockingByteArrayOutputStream (-1).close ();
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    final NonBlockingByteArrayOutputStream b = new NonBlockingByteArrayOutputStream ();
    final byte [] buf = new byte [100];
    VerySecureRandom.getInstance ().nextBytes (buf);
    try
    {
      b.write (null, 0, 10);
      b.close ();
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      b.write (buf, -1, 100);
      b.close ();
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    try
    {
      b.write (buf, 101, 100);
      b.close ();
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    try
    {
      b.write (buf, 0, -1);
      b.close ();
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    try
    {
      b.write (buf, 90, 11);
      b.close ();
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    b.write (buf);

    final NonBlockingByteArrayOutputStream baos = new NonBlockingByteArrayOutputStream ();
    b.writeTo (baos);

    try
    {
      b.writeTo (null);
      b.close ();
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }
}
