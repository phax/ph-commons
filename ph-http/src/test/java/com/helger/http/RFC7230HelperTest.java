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
package com.helger.http;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.helger.base.CGlobal;

/**
 * Test class for class {@link RFC7230Helper}.
 *
 * @author Philip Helger
 */
public final class RFC7230HelperTest
{
  @Test
  public void testIsValidToken ()
  {
    assertFalse (RFC7230Helper.isValidToken ((String) null));
    assertFalse (RFC7230Helper.isValidToken ((char []) null));
    assertFalse (RFC7230Helper.isValidToken (""));
    assertFalse (RFC7230Helper.isValidToken (CGlobal.EMPTY_CHAR_ARRAY));
    assertFalse (RFC7230Helper.isValidToken (" "));
    assertFalse (RFC7230Helper.isValidToken ("bla bla"));
    assertFalse (RFC7230Helper.isValidToken ("bla;"));
    assertTrue (RFC7230Helper.isValidToken ("bla"));
    assertTrue (RFC7230Helper.isValidToken ("!#$%&'*+-.^_`|~09azAZ"));

    for (final String s : new String [] { " ", "a", "bla", "bla bla", "bla;", "a\"b", "!#$%&'*+-.^_`|~", "blä" })
      assertEquals (s,
                    Boolean.valueOf (RFC7230Helper.isValidToken (s.toCharArray ())),
                    Boolean.valueOf (RFC7230Helper.isValidToken (s)));
  }
}
