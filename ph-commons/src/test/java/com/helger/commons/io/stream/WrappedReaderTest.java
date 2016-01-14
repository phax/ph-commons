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
import java.nio.CharBuffer;

import org.junit.Test;

import com.helger.commons.mock.CommonsTestHelper;
import com.helger.commons.string.StringHelper;

/**
 * Test class for class {@link WrappedReader}.
 *
 * @author Philip Helger
 */
public final class WrappedReaderTest
{
  @Test
  public void testAll () throws IOException
  {
    final NonBlockingStringReader baos = new NonBlockingStringReader (StringHelper.getRepeated ('a', 100));
    try (final WrappedReader ws = new WrappedReader (baos))
    {
      assertTrue (ws.markSupported ());
      assertTrue (ws.ready ());
      ws.mark (0);
      ws.read ();
      assertEquals (4, ws.read (new char [4]));
      assertEquals (1, ws.read (new char [5], 1, 1));
      ws.read (CharBuffer.allocate (1));
      assertEquals (4, ws.skip (4));
      assertEquals (89, ws.skip (100));
      ws.reset ();
      assertEquals (100, ws.skip (100));
      CommonsTestHelper.testToStringImplementation (ws);
    }

    try (WrappedReader aReader = new WrappedReader (null))
    {
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }
}
