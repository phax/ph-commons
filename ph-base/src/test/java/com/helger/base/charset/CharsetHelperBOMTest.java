/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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
package com.helger.base.charset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

import com.helger.base.array.ArrayHelper;
import com.helger.base.charset.CharsetHelper.InputStreamAndCharset;
import com.helger.base.io.nonblocking.NonBlockingByteArrayInputStream;
import com.helger.base.io.stream.StreamHelper;

/**
 * Test class for the BOM handling of {@link CharsetHelper}.
 *
 * @author Philip Helger
 */
public final class CharsetHelperBOMTest
{
  private static final String TEXT = "Hällö";

  @Test
  public void testWithoutBOM () throws IOException
  {
    final byte [] aBytes = TEXT.getBytes (StandardCharsets.ISO_8859_1);
    try (final NonBlockingByteArrayInputStream aBAIS = new NonBlockingByteArrayInputStream (aBytes))
    {
      final InputStreamAndCharset aISAC = CharsetHelper.getInputStreamAndCharsetFromBOM (aBAIS);
      assertNotNull (aISAC);
      assertFalse (aISAC.hasBOM ());
      assertNull (aISAC.getBOM ());
      assertNull (aISAC.getCharset ());
      assertFalse (aISAC.isReadMultiple ());
      // Nothing was consumed
      assertEquals (TEXT, new String (StreamHelper.getAllBytes (aISAC.getInputStream ()), StandardCharsets.ISO_8859_1));
    }
  }

  @Test
  public void testWithUTF8BOM () throws IOException
  {
    final byte [] aBytes = ArrayHelper.getConcatenated (EUnicodeBOM.BOM_UTF_8.getAllBytes (),
                                                        TEXT.getBytes (StandardCharsets.UTF_8));
    try (final NonBlockingByteArrayInputStream aBAIS = new NonBlockingByteArrayInputStream (aBytes))
    {
      final InputStreamAndCharset aISAC = CharsetHelper.getInputStreamAndCharsetFromBOM (aBAIS);
      assertTrue (aISAC.hasBOM ());
      assertSame (EUnicodeBOM.BOM_UTF_8, aISAC.getBOM ());
      assertEquals (StandardCharsets.UTF_8, aISAC.getCharset ());
      // The BOM was consumed
      assertEquals (TEXT, new String (StreamHelper.getAllBytes (aISAC.getInputStream ()), StandardCharsets.UTF_8));
    }
  }

  @Test
  public void testWithUTF16BOM () throws IOException
  {
    final byte [] aBytes = ArrayHelper.getConcatenated (EUnicodeBOM.BOM_UTF_16_BIG_ENDIAN.getAllBytes (),
                                                        TEXT.getBytes (StandardCharsets.UTF_16BE));
    try (final NonBlockingByteArrayInputStream aBAIS = new NonBlockingByteArrayInputStream (aBytes))
    {
      final InputStreamAndCharset aISAC = CharsetHelper.getInputStreamAndCharsetFromBOM (aBAIS);
      assertTrue (aISAC.hasBOM ());
      assertSame (EUnicodeBOM.BOM_UTF_16_BIG_ENDIAN, aISAC.getBOM ());
      assertEquals (StandardCharsets.UTF_16BE, aISAC.getCharset ());
    }
  }

  @Test
  public void testEmptyStream () throws IOException
  {
    try (final NonBlockingByteArrayInputStream aBAIS = new NonBlockingByteArrayInputStream (new byte [0]))
    {
      final InputStreamAndCharset aISAC = CharsetHelper.getInputStreamAndCharsetFromBOM (aBAIS);
      assertFalse (aISAC.hasBOM ());
      assertEquals (0, StreamHelper.getAllBytes (aISAC.getInputStream ()).length);
    }
  }

  @Test
  public void testGetReaderByBOM () throws IOException
  {
    // With a BOM the BOM charset wins
    final byte [] aWithBOM = ArrayHelper.getConcatenated (EUnicodeBOM.BOM_UTF_8.getAllBytes (),
                                                          TEXT.getBytes (StandardCharsets.UTF_8));
    try (final NonBlockingByteArrayInputStream aBAIS = new NonBlockingByteArrayInputStream (aWithBOM);
         final InputStreamReader aReader = CharsetHelper.getReaderByBOM (aBAIS, StandardCharsets.ISO_8859_1))
    {
      assertEquals (TEXT, StreamHelper.getAllCharactersAsString (aReader));
    }

    // Without a BOM the fallback charset is used
    try (final NonBlockingByteArrayInputStream aBAIS = new NonBlockingByteArrayInputStream (TEXT.getBytes (StandardCharsets.ISO_8859_1));
         final InputStreamReader aReader = CharsetHelper.getReaderByBOM (aBAIS, StandardCharsets.ISO_8859_1))
    {
      assertEquals (TEXT, StreamHelper.getAllCharactersAsString (aReader));
    }
  }
}
