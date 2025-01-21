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
package com.helger.commons.charset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.helger.commons.string.StringHelper;

/**
 * Test class for class {@link EUnicodeBOM}.
 *
 * @author Philip Helger
 */
public final class EUnicodeBOMTest
{
  @Test
  public void testGetSizeInBytes ()
  {
    assertEquals (3, EUnicodeBOM.BOM_UTF_8.getByteCount ());
    assertEquals (2, EUnicodeBOM.BOM_UTF_16_BIG_ENDIAN.getByteCount ());
    assertEquals (2, EUnicodeBOM.BOM_UTF_16_LITTLE_ENDIAN.getByteCount ());
    assertEquals (4, EUnicodeBOM.BOM_UTF_32_BIG_ENDIAN.getByteCount ());
    assertEquals (4, EUnicodeBOM.BOM_UTF_32_LITTLE_ENDIAN.getByteCount ());
    assertEquals (4, EUnicodeBOM.BOM_UTF_7.getByteCount ());
    assertEquals (4, EUnicodeBOM.BOM_UTF_7_ALT2.getByteCount ());
    assertEquals (4, EUnicodeBOM.BOM_UTF_7_ALT3.getByteCount ());
    assertEquals (4, EUnicodeBOM.BOM_UTF_7_ALT4.getByteCount ());
    assertEquals (3, EUnicodeBOM.BOM_UTF_1.getByteCount ());
    assertEquals (4, EUnicodeBOM.BOM_UTF_EBCDIC.getByteCount ());
    assertEquals (3, EUnicodeBOM.BOM_SCSU.getByteCount ());
    assertEquals (3, EUnicodeBOM.BOM_SCSU_TO_UCS.getByteCount ());
    assertEquals (3, EUnicodeBOM.BOM_SCSU_W0_TO_FE80.getByteCount ());
    assertEquals (3, EUnicodeBOM.BOM_SCSU_W1_TO_FE80.getByteCount ());
    assertEquals (3, EUnicodeBOM.BOM_SCSU_W2_TO_FE80.getByteCount ());
    assertEquals (3, EUnicodeBOM.BOM_SCSU_W3_TO_FE80.getByteCount ());
    assertEquals (3, EUnicodeBOM.BOM_SCSU_W4_TO_FE80.getByteCount ());
    assertEquals (3, EUnicodeBOM.BOM_SCSU_W5_TO_FE80.getByteCount ());
    assertEquals (3, EUnicodeBOM.BOM_SCSU_W6_TO_FE80.getByteCount ());
    assertEquals (3, EUnicodeBOM.BOM_SCSU_W7_TO_FE80.getByteCount ());
    assertEquals (3, EUnicodeBOM.BOM_BOCU_1.getByteCount ());
    assertEquals (4, EUnicodeBOM.BOM_BOCU_1_ALT2.getByteCount ());
    assertEquals (4, EUnicodeBOM.BOM_GB_18030.getByteCount ());

    for (final EUnicodeBOM eBOM : EUnicodeBOM.values ())
    {
      // Check whether declared length matches byte length
      assertEquals (eBOM.getByteCount (), eBOM.getAllBytes ().length);
      assertTrue (eBOM.getByteCount () <= EUnicodeBOM.getMaximumByteCount ());
      assertFalse (eBOM.isPresent (null));
      assertFalse (eBOM.isPresent (new byte [0]));
      assertFalse (eBOM.isPresent (new byte [] { 1 }));
      assertFalse (eBOM.isPresent (new byte [eBOM.getByteCount () + 1]));
      assertTrue (eBOM.isPresent (eBOM.getAllBytes ()));
      assertSame (eBOM, EUnicodeBOM.valueOf (eBOM.name ()));
      assertSame (eBOM, EUnicodeBOM.getFromBytesOrNull (eBOM.getAllBytes ()));
      if (eBOM.getCharset () != null)
        assertTrue (StringHelper.hasText (eBOM.getCharsetName ()));
    }

    assertEquals (4, EUnicodeBOM.getMaximumByteCount ());
  }
}
