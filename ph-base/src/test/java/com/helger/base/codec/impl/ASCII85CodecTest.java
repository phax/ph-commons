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
package com.helger.base.codec.impl;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.nio.charset.StandardCharsets;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.junit.Test;

import com.helger.base.codec.DecodeException;

/**
 * Test class for class {@link ASCII85Codec}
 *
 * @author Philip Helger
 */
public final class ASCII85CodecTest
{
  @Nullable
  private static String _decode (@NonNull final String sEncoded)
  {
    return new ASCII85Codec ().getDecodedAsString (sEncoded, StandardCharsets.US_ASCII);
  }

  @Test
  public void testDecode ()
  {
    final String sEncoded = "<~9jqo^BlbD-BleB1DJ+*+F(f,q/0JhKF<GL>Cj@.4Gp$d7F!,L7@<6@)/0JDEF<G%<+EV:2F!,\n" +
                            "O<DJ+*.@<*K0@<6L(Df-\\0Ec5e;DffZ(EZee.Bl.9pF\"AGXBPCsi+DGm>@3BB/F*&OCAfu2/AKY\n" +
                            "i(DIb:@FD,*)+C]U=@3BN#EcYf8ATD3s@q?d$AftVqCh[NqF<G:8+EV:.+Cf>-FD5W8ARlolDIa\n" +
                            "l(DId<j@<?3r@:F%a+D58'ATD4$Bl@l3De:,-DJs`8ARoFb/0JMK@qB4^F!,R<AKZ&-DfTqBG%G\n" +
                            ">uD.RTpAKYo'+CT/5+Cei#DII?(E,9)oF*2M7/c~>";
    final String sDecoded = new ASCII85Codec ().getDecodedAsString (sEncoded, StandardCharsets.US_ASCII);
    assertEquals ("Man is distinguished, not only by his reason, but by this singular passion from other animals, which is a lust of the mind, that by a perseverance of delight in the continued and indefatigable generation of knowledge, exceeds the short vehemence of any carnal pleasure.",
                  sDecoded);
  }

  @Test
  public void testDecodePartialGroups ()
  {
    // The number of encoded characters determines the number of decoded bytes
    assertEquals ("M", _decode ("<~9`~>"));
    assertEquals ("Ma", _decode ("<~9jn~>"));
    assertEquals ("Man", _decode ("<~9jqo~>"));
    assertEquals ("Mans", _decode ("<~9jqp\\~>"));
    assertEquals ("Mansi", _decode ("<~9jqp\\B`~>"));

    // Without the start and end sequence
    assertEquals ("Man ", _decode ("9jqo^"));
  }

  @Test
  public void testDecodeZeroGroup ()
  {
    // "z" is the shortcut for 4 zero bytes
    assertArrayEquals (new byte [4], new ASCII85Codec ().getDecoded ("<~z~>".getBytes (StandardCharsets.US_ASCII)));
    assertEquals ("\0\0\0\0abcd", _decode ("<~z@:E_W~>"));
  }

  @Test
  public void testDecodeSpecialCases ()
  {
    // null stays null
    assertNull (new ASCII85Codec ().getDecoded ((byte []) null));

    // Whitespace is ignored
    assertEquals ("Man", _decode ("<~9j\n qo~>"));

    // Everything after "~" is ignored
    assertEquals ("Man", _decode ("<~9jqo~>and some trailing garbage"));
  }

  @Test
  public void testDecodeInvalid ()
  {
    // A buffer with less than 4 bytes is not allowed
    try
    {
      new ASCII85Codec ().getDecoded ("<~~".getBytes (StandardCharsets.US_ASCII));
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {
      // expected
    }

    // "v" is not a valid ASCII85 character
    try
    {
      _decode ("<~9jqv~>");
      fail ();
    }
    catch (final DecodeException ex)
    {
      // expected
    }

    // A single trailing character cannot be decoded
    try
    {
      _decode ("<~9jqo^9~>");
      fail ();
    }
    catch (final IllegalStateException ex)
    {
      // expected
    }
  }
}
