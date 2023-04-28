package com.helger.commons.text.codepoint;

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
