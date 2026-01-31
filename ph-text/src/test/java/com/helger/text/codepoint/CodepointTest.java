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
package com.helger.text.codepoint;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Test class for class {@link Codepoint}
 *
 * @author Philip Helger
 */
public final class CodepointTest
{
  @Test
  public void testBasic ()
  {
    for (int i = Character.MIN_CODE_POINT; i <= Character.MAX_CODE_POINT; ++i)
    {
      final Codepoint cp = new Codepoint (i);

      assertEquals (i, cp.getValue ());

      if (i >= Character.MIN_SUPPLEMENTARY_CODE_POINT && i <= Character.MAX_CODE_POINT)
      {
        assertTrue (i + " should be a supplementary", cp.isSupplementary ());
        assertEquals (2, cp.getCharCount ());
        assertEquals (2, cp.getAsChars ().length);
      }
      else
      {
        assertFalse (i + " should not be a supplementary", cp.isSupplementary ());
        assertEquals (1, cp.getCharCount ());
        assertEquals (1, cp.getAsChars ().length);
      }

      if (i >= Character.MIN_LOW_SURROGATE && i <= Character.MAX_LOW_SURROGATE)
        assertTrue (i + " should be a low surrogate", cp.isLowSurrogate ());
      else
        assertFalse (i + " should not be a low surrogate", cp.isLowSurrogate ());

      if (i >= Character.MIN_HIGH_SURROGATE && i <= Character.MAX_HIGH_SURROGATE)
        assertTrue (i + " should be a high surrogate", cp.isHighSurrogate ());
      else
        assertFalse (i + " should not be a high surrogate", cp.isHighSurrogate ());
    }
  }
}
