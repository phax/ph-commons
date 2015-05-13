/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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
package com.helger.commons.base64;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

import com.helger.commons.charset.CCharset;
import com.helger.commons.charset.CharsetManager;

/**
 * Test class for class {@link Base64Helper}.
 *
 * @author Philip Helger
 */
public final class Base64HelperTest
{
  @Test
  public void testEncodeDecodeCharset ()
  {
    final String sSource = "dgMP$";
    final String sEncoded = Base64Helper.safeEncode (sSource, CCharset.CHARSET_ISO_8859_1_OBJ);
    assertTrue (Arrays.equals (CharsetManager.getAsBytes (sSource, CCharset.CHARSET_ISO_8859_1_OBJ),
                               Base64Helper.safeDecode (sEncoded)));
    assertTrue (Arrays.equals (CharsetManager.getAsBytes (sSource, CCharset.CHARSET_ISO_8859_1_OBJ),
                               Base64Helper.safeDecode (CharsetManager.getAsBytes (sEncoded,
                                                                                   CCharset.CHARSET_ISO_8859_1_OBJ))));
    assertEquals (sSource, Base64Helper.safeDecodeAsString (sEncoded, CCharset.CHARSET_ISO_8859_1_OBJ));
    assertEquals (sSource,
                  Base64Helper.safeDecodeAsString (CharsetManager.getAsBytes (sEncoded, CCharset.CHARSET_ISO_8859_1_OBJ),
                                                   CCharset.CHARSET_ISO_8859_1_OBJ));

    // Invalid input
    assertNull (Base64Helper.safeDecode ("xyz"));
  }
}
