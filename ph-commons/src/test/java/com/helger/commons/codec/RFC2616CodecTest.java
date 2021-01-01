/**
 * Copyright (C) 2014-2021 Philip Helger (www.helger.com)
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.helger.commons.collection.ArrayHelper;

/**
 * Test class for class {@link RFC2616Codec}
 *
 * @author Philip Helger
 */
public final class RFC2616CodecTest
{
  @Test
  public void testEncode ()
  {
    final RFC2616Codec aCodec = new RFC2616Codec ();
    assertNull (aCodec.getEncodedAsString ((char []) null));
    assertNull (aCodec.getEncodedAsString ((String) null));
    assertEquals ("\"\"", aCodec.getEncodedAsString (ArrayHelper.EMPTY_CHAR_ARRAY));
    assertEquals ("\"\"", aCodec.getEncodedAsString (""));
    assertEquals ("\"abc\"", aCodec.getEncodedAsString ("abc"));
    assertEquals ("\"\\\"abc\\\"\"", aCodec.getEncodedAsString ("\"abc\""));
    assertEquals ("\"a b c\"", aCodec.getEncodedAsString ("a b c"));
    assertEquals ("\" \\\\ \\\" \"", aCodec.getEncodedAsString (" \\ \" "));
  }

  @Test
  public void testDecode ()
  {
    final RFC2616Codec aCodec = new RFC2616Codec ();
    assertNull (aCodec.getDecodedAsString ((char []) null));
    assertNull (aCodec.getDecodedAsString ((String) null));
    assertEquals ("", aCodec.getDecodedAsString ("\"\""));
    assertEquals ("abc", aCodec.getDecodedAsString ("\"abc\""));
    assertEquals ("a b c", aCodec.getDecodedAsString ("\"a b c\""));
    assertEquals (" \\ \" ", aCodec.getDecodedAsString ("\" \\\\ \\\" \""));

    try
    {
      aCodec.getDecoded ("\"");
      fail ();
    }
    catch (final DecodeException ex)
    {}
    try
    {
      aCodec.getDecoded ("\"abc");
      fail ();
    }
    catch (final DecodeException ex)
    {}
    try
    {
      aCodec.getDecoded ("abc");
      fail ();
    }
    catch (final DecodeException ex)
    {}
  }

  @Test
  public void testIsToken ()
  {
    assertFalse (RFC2616Codec.isToken (""));
    assertFalse (RFC2616Codec.isToken ((String) null));
    assertFalse (RFC2616Codec.isToken ((char []) null));
    assertFalse (RFC2616Codec.isToken ("abc "));
    assertFalse (RFC2616Codec.isToken (" abc"));
    assertFalse (RFC2616Codec.isToken ("ab c"));
    assertFalse (RFC2616Codec.isToken ("ab[c"));
    assertFalse (RFC2616Codec.isToken ("("));
    assertFalse (RFC2616Codec.isToken ("\\"));
    assertFalse (RFC2616Codec.isToken ("\""));

    assertTrue (RFC2616Codec.isToken ("abc"));
    assertTrue (RFC2616Codec.isToken ("abc123"));
    assertTrue (RFC2616Codec.isToken ("123"));
    assertTrue (RFC2616Codec.isToken ("123abc"));
  }
}
