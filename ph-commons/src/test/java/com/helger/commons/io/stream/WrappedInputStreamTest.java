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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;

import com.helger.commons.mock.CommonsTestHelper;

/**
 * Test class for class {@link WrappedInputStream}.
 *
 * @author Philip Helger
 */
public final class WrappedInputStreamTest
{
  @SuppressWarnings ("resource")
  @Test
  public void testAll () throws IOException
  {
    final NonBlockingByteArrayInputStream baos = new NonBlockingByteArrayInputStream (new byte [100]);
    final WrappedInputStream ws = new WrappedInputStream (baos);
    assertTrue (ws.markSupported ());
    assertEquals (100, ws.available ());
    ws.mark (0);
    ws.read ();
    ws.read (new byte [4]);
    ws.read (new byte [5], 1, 1);
    ws.skip (4);
    assertEquals (90, ws.available ());
    ws.reset ();
    assertEquals (100, ws.available ());
    ws.close ();
    CommonsTestHelper.testToStringImplementation (ws);

    try
    {
      new WrappedInputStream (null).close ();
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }
}
