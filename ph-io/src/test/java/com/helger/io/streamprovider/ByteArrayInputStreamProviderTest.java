/*
 * Copyright (C) 2015-2025 Philip Helger (www.helger.com)
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
package com.helger.io.streamprovider;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

import com.helger.base.io.stream.StreamHelper;

/**
 * Test class for class {@link ByteArrayInputStreamProvider}.
 *
 * @author Philip Helger
 */
public final class ByteArrayInputStreamProviderTest
{
  @Test
  public void testSimple ()
  {
    final byte [] aBytes = "Hallo Weltäöü".getBytes (StandardCharsets.ISO_8859_1);
    final ByteArrayInputStreamProvider aISP = new ByteArrayInputStreamProvider (aBytes);
    final InputStream aIS = aISP.getInputStream ();
    assertArrayEquals (aBytes, StreamHelper.getAllBytes (aIS));
    StreamHelper.close (aISP.getReader (StandardCharsets.UTF_8));
    assertNotNull (aISP.toString ());
  }
}
