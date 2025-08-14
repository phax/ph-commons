package com.helger.base.text;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

/**
 * Test class for class {@link TextFormatter}.
 *
 * @author Philip Helger
 */
public final class TextFormatterTest
{
  @Test
  public void testGetFormattedText ()
  {
    assertEquals ("a", TextFormatter.getFormattedText ("a", (Object []) null));
    assertEquals ("a", TextFormatter.getFormattedText ("a", new Object [0]));
    assertEquals ("a{0}", TextFormatter.getFormattedText ("a{0}", new Object [0]));
    assertEquals ("anull", TextFormatter.getFormattedText ("a{0}", (Object) null));
    assertEquals ("ab", TextFormatter.getFormattedText ("a{0}", "b"));
    assertEquals ("ab{1}", TextFormatter.getFormattedText ("a{0}{1}", "b"));
    assertEquals ("anull{1}", TextFormatter.getFormattedText ("a{0}{1}", (Object) null));
    assertNull (TextFormatter.getFormattedText ((String) null, "b"));
  }
}
