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

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

/**
 * Test class for class {@link CountingInputStream}.
 *
 * @author Philip Helger
 */
public final class NonClosingInputStreamTest
{
  private static final class MockCloseCountingInputStream extends WrappedInputStream
  {
    private int m_nCount = 0;

    public MockCloseCountingInputStream (final InputStream aSourceIS)
    {
      super (aSourceIS);
    }

    @Override
    public void close ()
    {
      m_nCount++;
    }

    public int getCloseCount ()
    {
      return m_nCount;
    }
  }

  @Test
  public void testClosing ()
  {
    final MockCloseCountingInputStream aX = new MockCloseCountingInputStream (new NonBlockingByteArrayInputStream ("abc".getBytes (StandardCharsets.ISO_8859_1)));
    StreamHelper.copyInputStreamToOutputStream (aX, new NonBlockingByteArrayOutputStream ());
    assertEquals (1, aX.getCloseCount ());
  }

  @Test
  public void testNonClosing ()
  {
    final MockCloseCountingInputStream aX = new MockCloseCountingInputStream (new NonBlockingByteArrayInputStream ("abc".getBytes (StandardCharsets.ISO_8859_1)));
    StreamHelper.copyInputStreamToOutputStream (new NonClosingInputStream (aX), new NonBlockingByteArrayOutputStream ());
    assertEquals (0, aX.getCloseCount ());
  }
}
