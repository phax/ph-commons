/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.junit.Ignore;
import org.junit.Test;

import com.helger.commons.io.stream.NonBlockingBufferedReader;
import com.helger.commons.io.stream.NonBlockingByteArrayInputStream;
import com.helger.commons.io.stream.StreamHelper;
import com.helger.commons.random.RandomHelper;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Test class for class {@link CharsetHelper}.
 *
 * @author Philip Helger
 */
public final class CharsetHelperTest
{
  @Test
  @SuppressFBWarnings (value = "NP_NONNULL_PARAM_VIOLATION")
  public void testCharsetFromName ()
  {
    assertNotNull (CharsetHelper.getCharsetFromName ("UTF-8"));
    try
    {
      CharsetHelper.getCharsetFromName (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      // Illegal name
      CharsetHelper.getCharsetFromName ("does not exist");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    try
    {
      // Unsupported
      CharsetHelper.getCharsetFromName ("bla");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
  }

  @Test
  public void testGetAsBytesCharset ()
  {
    final String s = "äbc";
    assertEquals (3, s.getBytes (StandardCharsets.ISO_8859_1).length);
    assertEquals (4, s.getBytes (StandardCharsets.UTF_8).length);

    try
    {
      s.getBytes ((Charset) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testGetAsStringInOtherCharsetCharset ()
  {
    final String s = "äbc";
    assertEquals (3,
                  CharsetHelper.getAsStringInOtherCharset (s, StandardCharsets.ISO_8859_1, StandardCharsets.UTF_8)
                               .length ());
    assertEquals (4,
                  CharsetHelper.getAsStringInOtherCharset (s, StandardCharsets.UTF_8, StandardCharsets.ISO_8859_1)
                               .length ());
    assertNull (CharsetHelper.getAsStringInOtherCharset (null, StandardCharsets.ISO_8859_1, StandardCharsets.UTF_8));
    assertEquals (s,
                  CharsetHelper.getAsStringInOtherCharset (s,
                                                           StandardCharsets.ISO_8859_1,
                                                           StandardCharsets.ISO_8859_1));
    assertEquals (s, CharsetHelper.getAsStringInOtherCharset (s, StandardCharsets.UTF_8, StandardCharsets.UTF_8));

    try
    {
      CharsetHelper.getAsStringInOtherCharset (s, null, StandardCharsets.UTF_8);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      CharsetHelper.getAsStringInOtherCharset (s, StandardCharsets.ISO_8859_1, null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testGreek () throws Exception
  {
    final String sAlpha = "?\u03B1";
    byte [] b = sAlpha.getBytes (StandardCharsets.UTF_8);
    assertEquals (sAlpha, new String (b, StandardCharsets.UTF_8));

    b = sAlpha.getBytes (StandardCharsets.UTF_8);
    assertEquals (sAlpha, new String (b, StandardCharsets.UTF_8));

    NonBlockingBufferedReader aReader = new NonBlockingBufferedReader (new InputStreamReader (new NonBlockingByteArrayInputStream (b),
                                                                                              StandardCharsets.UTF_8));
    assertEquals (sAlpha, aReader.readLine ());
    StreamHelper.close (aReader);

    aReader = new NonBlockingBufferedReader (new InputStreamReader (new NonBlockingByteArrayInputStream (b),
                                                                    StandardCharsets.UTF_8));
    assertEquals (sAlpha, aReader.readLine ());
    StreamHelper.close (aReader);
  }

  @Test
  public void testJavaCompiledAsUTF8 ()
  {
    final String s = "ä";
    if (s.length () != 1)
      throw new IllegalStateException ("Seems like the Java Source files were not compiled with UTF-8 encoding!");
  }

  @Test
  public void testGetUTF8ByteCount ()
  {
    assertEquals (0, CharsetHelper.getUTF8ByteCount ((String) null));
    assertEquals (0, CharsetHelper.getUTF8ByteCount ((char []) null));

    assertEquals (2, CharsetHelper.getUTF8ByteCount ("\0"));
    assertEquals (2, CharsetHelper.getUTF8ByteCount ("ä"));
    assertEquals (2, CharsetHelper.getUTF8ByteCount ('ä'));
    assertEquals (0, CharsetHelper.getUTF8ByteCount (""));
    assertEquals (3, CharsetHelper.getUTF8ByteCount ("abc"));
    assertEquals (9, CharsetHelper.getUTF8ByteCount ("abcäöü"));
    assertEquals (3, CharsetHelper.getUTF8ByteCount ("\ud7ff"));

    assertEquals (0, CharsetHelper.getUTF8ByteCount ("\udfff"));
    assertEquals (0, CharsetHelper.getUTF8ByteCount ("\ue000"));
    assertEquals (0, CharsetHelper.getUTF8ByteCount ("\uffff"));
    try
    {
      CharsetHelper.getUTF8ByteCount (0x110000);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    try
    {
      CharsetHelper.getUTF8ByteCount (0x10000);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
  }

  @Test
  @Ignore ("doesn't work reliably")
  public void testGetUTF8ByteCountRandom ()
  {
    for (int i = 0; i < 1000; i++)
    {
      // Build random String with 20 chars
      final int nStringLen = 20;
      final StringBuilder aSB = new StringBuilder ();
      for (int x = 0; x < nStringLen; ++x)
      {
        final int c = RandomHelper.getRandom ().nextInt (Character.MIN_HIGH_SURROGATE);
        aSB.append ((char) c);
      }

      // Count
      final int nCounted = CharsetHelper.getUTF8ByteCount (aSB.toString ());
      assertTrue (nCounted >= nStringLen);

      // Convert and count
      final byte [] b = aSB.toString ().getBytes (StandardCharsets.UTF_8);
      assertTrue (b.length >= nStringLen);

      // Must be equals
      assertEquals (Arrays.toString (b), nCounted, b.length);
    }
  }

  @Test
  public void testBasic ()
  {
    assertNotNull (CharsetHelper.getAllCharsets ());
    assertTrue (CharsetHelper.getAllCharsets ().size () > 0);
  }
}
