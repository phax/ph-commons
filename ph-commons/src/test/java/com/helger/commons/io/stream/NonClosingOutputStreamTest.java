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

import java.io.OutputStream;

import org.junit.Test;

import com.helger.commons.charset.CCharset;
import com.helger.commons.charset.CharsetManager;

/**
 * Test class for class {@link CountingOutputStream}.
 *
 * @author Philip Helger
 */
public final class NonClosingOutputStreamTest
{
  private static final class MockCloseCountingOutputStream extends WrappedOutputStream
  {
    private int m_nCount = 0;

    public MockCloseCountingOutputStream (final OutputStream aSourceOS)
    {
      super (aSourceOS);
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
    final MockCloseCountingOutputStream aX = new MockCloseCountingOutputStream (new NonBlockingByteArrayOutputStream ());
    StreamHelper.copyInputStreamToOutputStreamAndCloseOS (new NonBlockingByteArrayInputStream (CharsetManager.getAsBytes ("abc",
                                                                                                                          CCharset.CHARSET_ISO_8859_1_OBJ)),
                                                          aX);
    assertEquals (1, aX.getCloseCount ());
  }

  @Test
  public void testNonClosing ()
  {
    final MockCloseCountingOutputStream aX = new MockCloseCountingOutputStream (new NonBlockingByteArrayOutputStream ());
    StreamHelper.copyInputStreamToOutputStreamAndCloseOS (new NonBlockingByteArrayInputStream (CharsetManager.getAsBytes ("abc",
                                                                                                                          CCharset.CHARSET_ISO_8859_1_OBJ)),
                                                          new NonClosingOutputStream (aX));
    assertEquals (0, aX.getCloseCount ());
  }
}
