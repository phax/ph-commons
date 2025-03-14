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

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

import com.helger.commons.mock.CommonsTestHelper;

/**
 * Test class for class {@link CountingInputStream}.
 *
 * @author Philip Helger
 */
public final class CountingInputStreamTest
{
  @Test
  public void testAll () throws IOException
  {
    final String sTestString = "test 123 - This counts!";
    final CountingInputStream aCIS = new CountingInputStream (new NonBlockingByteArrayInputStream (sTestString.getBytes (StandardCharsets.ISO_8859_1)));
    aCIS.read ();
    StreamHelper.copyInputStreamToOutputStream (aCIS, new NonBlockingByteArrayOutputStream ());
    assertEquals (sTestString.length (), aCIS.getBytesRead ());
    assertEquals (sTestString.length (), aCIS.getPosition ());
    CommonsTestHelper.testToStringImplementation (aCIS);
  }
}
