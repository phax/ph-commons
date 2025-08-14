package com.helger.base.string;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.helger.base.nonblocking.NonBlockingStringWriter;

/**
 * Test class for class {@link StringReplace}.
 *
 * @author Philip Helger
 */
public final class StringReplaceTest
{

  @Test
  public void testReplaceMultipleCharArrayToChar ()
  {
    assertArrayEquals ("abc".toCharArray (), StringReplace.replaceMultiple ("abc", "def".toCharArray (), ' '));
    assertArrayEquals ("   ".toCharArray (), StringReplace.replaceMultiple ("abc", "abc".toCharArray (), ' '));
    assertArrayEquals ("   ".toCharArray (), StringReplace.replaceMultiple ("abc", "abcabc".toCharArray (), ' '));
    assertArrayEquals ("   ".toCharArray (), StringReplace.replaceMultiple ("abc", "aabbcc".toCharArray (), ' '));
    assertArrayEquals ("      ".toCharArray (), StringReplace.replaceMultiple ("abcabc", "abc".toCharArray (), ' '));
    assertArrayEquals ("      ".toCharArray (), StringReplace.replaceMultiple ("aabbcc", "abc".toCharArray (), ' '));
    assertArrayEquals ("a  ".toCharArray (), StringReplace.replaceMultiple ("abc", "bc".toCharArray (), ' '));
    assertArrayEquals (" b ".toCharArray (), StringReplace.replaceMultiple ("abc", "ac".toCharArray (), ' '));
    assertArrayEquals ("  c".toCharArray (), StringReplace.replaceMultiple ("abc", "ab".toCharArray (), ' '));
    assertArrayEquals ("abc".toCharArray (), StringReplace.replaceMultiple ("abc", "".toCharArray (), ' '));
    assertArrayEquals ("".toCharArray (), StringReplace.replaceMultiple ("", "abc".toCharArray (), ' '));
    assertArrayEquals ("".toCharArray (), StringReplace.replaceMultiple ("", "".toCharArray (), ' '));
    assertArrayEquals ("".toCharArray (), StringReplace.replaceMultiple (null, "abc".toCharArray (), ' '));
    assertArrayEquals ("".toCharArray (), StringReplace.replaceMultiple (null, "".toCharArray (), ' '));
  }

  @Test
  public void testReplaceAllString ()
  {
    assertEquals ("abc", StringReplace.replaceAll ("abc", "d", "e"));
    assertEquals ("abd", StringReplace.replaceAll ("abc", "c", "d"));
    assertEquals ("adc", StringReplace.replaceAll ("abc", "b", "d"));
    assertEquals ("dbc", StringReplace.replaceAll ("abc", "a", "d"));
    assertEquals ("ddd", StringReplace.replaceAll ("aaa", "a", "d"));
    assertEquals ("xyxyxy", StringReplace.replaceAll ("aaa", "a", "xy"));
    assertEquals ("", StringReplace.replaceAll ("", "anything", "nothing"));
    assertEquals ("", StringReplace.replaceAll ("aaa", "a", ""));
    assertEquals ("bb", StringReplace.replaceAll ("ababa", "a", ""));
    assertEquals ("acd", StringReplace.replaceAll ("abcd", "ab", "a"));
    assertEquals ("abd", StringReplace.replaceAll ("abcd", "bc", "b"));
    assertEquals ("abc", StringReplace.replaceAll ("abcd", "cd", "c"));
    assertEquals ("abc", StringReplace.replaceAll ("abcd", "d", ""));
    assertEquals ("bcbc", StringReplace.replaceAll ("bcbcbc", "bcbc", "bc"));
    assertEquals ("aa", StringReplace.replaceAll ("aaaa", "aa", "a"));
    assertEquals ("a  a b ", StringReplace.replaceAll ("a    a  b ", "  ", " "));
    assertNull (StringReplace.replaceAll (null, "aa", "a"));
    assertEquals ("aaaa", StringReplace.replaceAll ("aaaa", "aa", "aa"));

    try
    {
      StringReplace.replaceAll ("aaaaach", null, "a");
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      StringReplace.replaceAll ("aaaaach", "aa", null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testReplaceAllChar ()
  {
    assertEquals ("abc", StringReplace.replaceAll ("abc", 'd', 'e'));
    assertEquals ("abd", StringReplace.replaceAll ("abc", 'c', 'd'));
    assertEquals ("adc", StringReplace.replaceAll ("abc", 'b', 'd'));
    assertEquals ("dbc", StringReplace.replaceAll ("abc", 'a', 'd'));
    assertEquals ("ddd", StringReplace.replaceAll ("aaa", 'a', 'd'));
    assertEquals ("", StringReplace.replaceAll ("", 'a', 'b'));
    assertEquals ("aaa", StringReplace.replaceAll ("aaa", 'a', 'a'));
    assertEquals ("aaa", StringReplace.replaceAll ("aaa", 'b', 'b'));
    assertEquals ("bbbbb", StringReplace.replaceAll ("ababa", 'a', 'b'));
    assertEquals ("\0b\0b\0", StringReplace.replaceAll ("ababa", 'a', '\0'));
  }

  @Test
  public void testReplaceAllSafe ()
  {
    assertEquals ("abc", StringReplace.replaceAllSafe ("abc", "d", "e"));
    assertEquals ("abd", StringReplace.replaceAllSafe ("abc", "c", "d"));
    assertEquals ("adc", StringReplace.replaceAllSafe ("abc", "b", "d"));
    assertEquals ("dbc", StringReplace.replaceAllSafe ("abc", "a", "d"));
    assertEquals ("ddd", StringReplace.replaceAllSafe ("aaa", "a", "d"));
    assertEquals ("xyxyxy", StringReplace.replaceAllSafe ("aaa", "a", "xy"));
    assertEquals ("", StringReplace.replaceAllSafe ("", "anything", "nothing"));
    assertEquals ("", StringReplace.replaceAllSafe ("aaa", "a", ""));
    assertEquals ("bb", StringReplace.replaceAllSafe ("ababa", "a", ""));
    assertEquals ("acd", StringReplace.replaceAllSafe ("abcd", "ab", "a"));
    assertEquals ("abd", StringReplace.replaceAllSafe ("abcd", "bc", "b"));
    assertEquals ("abc", StringReplace.replaceAllSafe ("abcd", "cd", "c"));
    assertEquals ("abc", StringReplace.replaceAllSafe ("abcd", "d", ""));
    assertEquals ("bcbc", StringReplace.replaceAllSafe ("bcbcbc", "bcbc", "bc"));
    assertEquals ("aa", StringReplace.replaceAllSafe ("aaaa", "aa", "a"));
    assertEquals ("a  a b ", StringReplace.replaceAllSafe ("a    a  b ", "  ", " "));
    assertNull (StringReplace.replaceAllSafe (null, "aa", "a"));
    assertEquals ("aaaa", StringReplace.replaceAllSafe ("aaaa", "aa", "aa"));

    try
    {
      StringReplace.replaceAllSafe ("aaaaach", null, "a");
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    assertEquals ("ach", StringReplace.replaceAllSafe ("aaaaach", "aa", null));
  }

  @Test
  public void testReplaceAllRepeatedly ()
  {
    assertEquals ("abc", StringReplace.replaceAllRepeatedly ("abc", "d", "e"));
    assertEquals ("dbc", StringReplace.replaceAllRepeatedly ("abc", "a", "d"));
    assertEquals ("ddd", StringReplace.replaceAllRepeatedly ("aaa", "a", "d"));
    assertEquals ("a a b ", StringReplace.replaceAllRepeatedly ("a    a  b ", "  ", " "));
    assertEquals ("", StringReplace.replaceAllRepeatedly ("", " a", "b"));
    assertNull (StringReplace.replaceAllRepeatedly (null, " a", "b"));

    try
    {
      StringReplace.replaceAllRepeatedly ("aaaaach", null, "a");
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      StringReplace.replaceAllRepeatedly ("aaaaach", "aa", null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      StringReplace.replaceAllRepeatedly ("aaaaach", "a", "aa");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
  }

  @Test
  public void testReplaceMultipleMap ()
  {
    final Map <String, String> aMap = new HashMap <> ();
    aMap.put ("Hallo", "Hi");
    aMap.put ("Welt", "world");
    aMap.put ("!", "???");
    assertEquals ("Abc die Katze lief im Schnee", StringReplace.replaceMultiple ("Abc die Katze lief im Schnee", aMap));
    assertEquals ("Hi Katze", StringReplace.replaceMultiple ("Hallo Katze", aMap));
    assertEquals ("Moin world", StringReplace.replaceMultiple ("Moin Welt", aMap));
    assertEquals ("Moin welt", StringReplace.replaceMultiple ("Moin welt", aMap));
    assertEquals ("Hi", StringReplace.replaceMultiple ("Hallo", aMap));
    assertEquals ("Hi Hi", StringReplace.replaceMultiple ("Hallo Hallo", aMap));
    assertEquals ("HiHiHi", StringReplace.replaceMultiple ("HalloHalloHallo", aMap));
    assertEquals ("Hi world???", StringReplace.replaceMultiple ("Hallo Welt!", aMap));
    assertEquals ("Hi world???Hi world???", StringReplace.replaceMultiple ("Hallo Welt!Hallo Welt!", aMap));
  }

  @Test
  public void testReplaceMultipleCharArrays ()
  {
    assertArrayEquals ("bb".toCharArray (),
                       StringReplace.replaceMultiple ("a",
                                                      new char [] { 'a' },
                                                      new char [] [] { "bb".toCharArray () }));
    assertArrayEquals ("bbbb".toCharArray (),
                       StringReplace.replaceMultiple ("aa",
                                                      new char [] { 'a' },
                                                      new char [] [] { "bb".toCharArray () }));
    assertArrayEquals ("cdc".toCharArray (),
                       StringReplace.replaceMultiple ("cdc",
                                                      new char [] { 'a' },
                                                      new char [] [] { "bb".toCharArray () }));
    assertArrayEquals ("cbbc".toCharArray (),
                       StringReplace.replaceMultiple ("cac",
                                                      new char [] { 'a' },
                                                      new char [] [] { "bb".toCharArray () }));
    assertArrayEquals ("ddbbdd".toCharArray (),
                       StringReplace.replaceMultiple ("cac",
                                                      new char [] { 'a', 'c' },
                                                      new char [] [] { "bb".toCharArray (), "dd".toCharArray () }));
    assertArrayEquals ("<ddbbdd>".toCharArray (),
                       StringReplace.replaceMultiple ("<cac>",
                                                      new char [] { 'a', 'c' },
                                                      new char [] [] { "bb".toCharArray (), "dd".toCharArray () }));
    assertArrayEquals (new char [0],
                       StringReplace.replaceMultiple ("",
                                                      new char [] { 'a', 'c' },
                                                      new char [] [] { "bb".toCharArray (), "dd".toCharArray () }));
    assertArrayEquals ("any".toCharArray (), StringReplace.replaceMultiple ("any", new char [0], new char [0] []));
    try
    {
      StringReplace.replaceMultiple ("any",
                                     (char []) null,
                                     new char [] [] { "bb".toCharArray (), "dd".toCharArray () });
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      StringReplace.replaceMultiple ("any", "an".toCharArray (), null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      StringReplace.replaceMultiple ("any", new char [1], new char [2] []);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
  }

  @Test
  public void testReplaceMultipleTo () throws IOException
  {
    NonBlockingStringWriter aSW = new NonBlockingStringWriter ();
    StringReplace.replaceMultipleTo ("a", new char [] { 'a' }, new char [] [] { "bb".toCharArray () }, aSW);
    assertEquals ("bb", aSW.getAsString ());

    aSW = new NonBlockingStringWriter ();
    StringReplace.replaceMultipleTo ("aa", new char [] { 'a' }, new char [] [] { "bb".toCharArray () }, aSW);
    assertEquals ("bbbb", aSW.getAsString ());

    aSW = new NonBlockingStringWriter ();
    StringReplace.replaceMultipleTo ("cdc", new char [] { 'a' }, new char [] [] { "bb".toCharArray () }, aSW);
    assertEquals ("cdc", aSW.getAsString ());

    aSW = new NonBlockingStringWriter ();
    StringReplace.replaceMultipleTo ("cac", new char [] { 'a' }, new char [] [] { "bb".toCharArray () }, aSW);
    assertEquals ("cbbc", aSW.getAsString ());

    aSW = new NonBlockingStringWriter ();
    StringReplace.replaceMultipleTo ("cac",
                                     new char [] { 'a', 'c' },
                                     new char [] [] { "bb".toCharArray (), "dd".toCharArray () },
                                     aSW);
    assertEquals ("ddbbdd", aSW.getAsString ());

    aSW = new NonBlockingStringWriter ();
    StringReplace.replaceMultipleTo ("<cac>",
                                     new char [] { 'a', 'c' },
                                     new char [] [] { "bb".toCharArray (), "dd".toCharArray () },
                                     aSW);
    assertEquals ("<ddbbdd>", aSW.getAsString ());

    aSW = new NonBlockingStringWriter ();
    StringReplace.replaceMultipleTo ("",
                                     new char [] { 'a', 'c' },
                                     new char [] [] { "bb".toCharArray (), "dd".toCharArray () },
                                     aSW);
    assertEquals ("", aSW.getAsString ());

    aSW = new NonBlockingStringWriter ();
    StringReplace.replaceMultipleTo ("any", new char [0], new char [0] [], aSW);
    assertEquals ("any", aSW.getAsString ());

    try
    {
      StringReplace.replaceMultipleTo ("any", null, new char [0] [], aSW);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      StringReplace.replaceMultipleTo ("any", new char [0], null, aSW);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      StringReplace.replaceMultipleTo ("any", new char [0], new char [1] [], aSW);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    try
    {
      StringReplace.replaceMultipleTo ("any", new char [0], new char [0] [], null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }
}
