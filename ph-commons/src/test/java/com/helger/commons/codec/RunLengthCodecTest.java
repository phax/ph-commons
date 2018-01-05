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
package com.helger.commons.codec;

import static org.junit.Assert.assertEquals;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

/**
 * Test class for class {@link RunLengthCodec}
 *
 * @author Philip Helger
 */
public final class RunLengthCodecTest
{
  private static final Charset CHARSET = StandardCharsets.UTF_8;

  @Test
  public void testDecode ()
  {
    final RunLengthCodec c = new RunLengthCodec ();
    assertEquals ("WWWWWWWWWWWWTFF",
                  c.getDecodedAsString (new byte [] { (byte) (257 - 12), 'W', 0x03, 'T', 'F', 'F' }, CHARSET));
    assertEquals ("WWWWWWWWWWTFF",
                  c.getDecodedAsString (new byte [] { (byte) (257 - 10), 'W', 0x03, 'T', 'F', 'F', (byte) 0x80 },
                                        CHARSET));
  }
}
