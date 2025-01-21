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

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

import com.helger.commons.charset.CharsetHelper;

/**
 * Test class for class {@link StringInputStream}.
 *
 * @author Philip Helger
 */
public final class StringInputStreamTest
{
  @Test
  public void testAll ()
  {
    final String sTestString = "test äöü 123 - This counts!";
    final NonBlockingByteArrayOutputStream aBAOS = new NonBlockingByteArrayOutputStream ();
    StreamHelper.copyInputStreamToOutputStream (new StringInputStream (sTestString, StandardCharsets.ISO_8859_1), aBAOS);
    assertEquals (sTestString, aBAOS.getAsString (StandardCharsets.ISO_8859_1));
    aBAOS.reset ();
    final Charset aCS = CharsetHelper.getCharsetFromName ("UTF-16");
    StreamHelper.copyInputStreamToOutputStream (new StringInputStream (sTestString, aCS), aBAOS);
    assertEquals (sTestString, aBAOS.getAsString (aCS));
  }
}
