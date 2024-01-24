/*
 * Copyright (C) 2014-2024 Philip Helger (www.helger.com)
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
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

import com.helger.commons.mock.CommonsTestHelper;

/**
 * Test class for class {@link WrappedOutputStream}.
 *
 * @author Philip Helger
 */
public final class WrappedOutputStreamTest
{
  @SuppressWarnings ("resource")
  @Test
  public void testAll () throws IOException
  {
    final NonBlockingByteArrayOutputStream baos = new NonBlockingByteArrayOutputStream ();
    try (final WrappedOutputStream ws = new WrappedOutputStream (baos))
    {
      ws.write ('a');
      ws.write ("bc".getBytes (StandardCharsets.ISO_8859_1));
      ws.write ("cde".getBytes (StandardCharsets.ISO_8859_1), 1, 1);
      ws.flush ();
      assertEquals ("abcd", baos.getAsString (StandardCharsets.ISO_8859_1));
      CommonsTestHelper.testToStringImplementation (ws);
    }

    try
    {
      new WrappedOutputStream (null).close ();
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }
}
