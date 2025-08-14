package com.helger.base.string;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

/**
 * Test class for class {@link StringRemove}.
 *
 * @author Philip Helger
 */
public final class StringRemoveTest
{
  @Test
  public void testRemoveAllChar ()
  {
    assertEquals ("abc", StringRemove.removeAll ("abc", 'd'));
    assertEquals ("ab", StringRemove.removeAll ("abc", 'c'));
    assertEquals ("ac", StringRemove.removeAll ("abc", 'b'));
    assertEquals ("bc", StringRemove.removeAll ("abc", 'a'));
    assertEquals ("", StringRemove.removeAll ("aaa", 'a'));
    assertEquals ("", StringRemove.removeAll ("", 'a'));
    assertEquals ("bb", StringRemove.removeAll ("ababa", 'a'));
    assertEquals ("abc", StringRemove.removeAll ("abcd", 'd'));
    assertNull (StringRemove.removeAll (null, 'a'));
  }

  @Test
  public void testRemoveAllString ()
  {
    assertEquals ("abc", StringRemove.removeAll ("abc", "d"));
    assertEquals ("abc", StringRemove.removeAll ("abc", "ac"));
    assertEquals ("ab", StringRemove.removeAll ("abc", "c"));
    assertEquals ("a", StringRemove.removeAll ("abc", "bc"));
    assertEquals ("ac", StringRemove.removeAll ("abc", "b"));
    assertEquals ("c", StringRemove.removeAll ("abc", "ab"));
    assertEquals ("bc", StringRemove.removeAll ("abc", "a"));
    assertEquals ("", StringRemove.removeAll ("aaa", "a"));
    assertEquals ("a", StringRemove.removeAll ("aaa", "aa"));
    assertEquals ("", StringRemove.removeAll ("", "a"));
    assertEquals ("bb", StringRemove.removeAll ("ababa", "a"));
    assertEquals ("a", StringRemove.removeAll ("ababa", "ab"));
    assertEquals ("abc", StringRemove.removeAll ("abcd", "d"));
    assertNull (StringRemove.removeAll (null, "a"));
  }

}
