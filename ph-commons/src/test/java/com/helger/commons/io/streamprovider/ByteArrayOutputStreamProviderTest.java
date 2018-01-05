/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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
package com.helger.commons.io.streamprovider;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

import com.helger.commons.io.EAppend;
import com.helger.commons.io.stream.NonBlockingStringReader;
import com.helger.commons.io.stream.StreamHelper;
import com.helger.commons.io.stream.StringInputStream;

/**
 * Test class for class {@link ByteArrayOutputStreamProvider}.
 *
 * @author Philip Helger
 */
public final class ByteArrayOutputStreamProviderTest
{
  @Test
  public void testSimple ()
  {
    final ByteArrayOutputStreamProvider aOSP = new ByteArrayOutputStreamProvider ();
    final OutputStream aOS = aOSP.getOutputStream (EAppend.DEFAULT);
    assertNotNull (aOS);
    StreamHelper.copyInputStreamToOutputStreamAndCloseOS (new StringInputStream ("Hiho", StandardCharsets.ISO_8859_1),
                                                          aOS);
    assertEquals ("Hiho", aOSP.getAsString (StandardCharsets.ISO_8859_1));
    assertArrayEquals ("Hiho".getBytes (StandardCharsets.ISO_8859_1), aOSP.getBytes ());
    // Close the underlying OS
    StreamHelper.close (aOSP.getWriter (StandardCharsets.UTF_8, EAppend.DEFAULT));

    // Reader/Writer
    StreamHelper.copyReaderToWriterAndCloseWriter (new NonBlockingStringReader ("Hiho"),
                                                   aOSP.getWriter (StandardCharsets.UTF_8, EAppend.DEFAULT));
    assertEquals ("Hiho", aOSP.getAsString (StandardCharsets.UTF_8));
  }
}
