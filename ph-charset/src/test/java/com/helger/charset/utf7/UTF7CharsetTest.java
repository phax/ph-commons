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
package com.helger.charset.utf7;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;

import org.junit.Before;
import org.junit.Test;

public final class UTF7CharsetTest extends AbstractCharsetTestCase
{
  @Before
  public void setUp () throws Exception
  {
    tested = new UTF7Charset ("UTF-7", new String [] {}, false);
  }

  @Test
  public void testContains ()
  {
    assertTrue (tested.contains (Charset.forName ("US-ASCII")));
    assertTrue (tested.contains (Charset.forName ("ISO-8859-1")));
    assertTrue (tested.contains (Charset.forName ("UTF-8")));
    assertTrue (tested.contains (Charset.forName ("UTF-16")));
    assertTrue (tested.contains (Charset.forName ("UTF-16LE")));
    assertTrue (tested.contains (Charset.forName ("UTF-16BE")));
  }

  @Test
  public void testEmpty () throws Exception
  {
    final String string = "";
    assertEquals (string, decode (string));
    assertEquals (string, encode (string));
  }

  @Test
  public void testDecodeSimple () throws Exception
  {
    assertEquals ("abcdefghijklmnopqrstuvwxyz", decode ("abcdefghijklmnopqrstuvwxyz"));
  }

  @Test
  public void testDecodeShiftSequence () throws Exception
  {
    assertEquals ("+", decode ("+-"));
    assertEquals ("+-", decode ("+--"));
    assertEquals ("++", decode ("+-+-"));
  }

  @Test
  public void testDecodeBase64 () throws Exception
  {
    assertEquals ("Hi Mom \u263A!", decode ("Hi Mom +Jjo-!"));
    assertEquals ("Hi Mom -\u263A-!", decode ("Hi Mom -+Jjo--!"));
    assertEquals ("\u65E5\u672C\u8A9E", decode ("+ZeVnLIqe-"));
    assertEquals ("Item 3 is £1.", decode ("Item 3 is +AKM-1."));
  }

  @Test
  public void testDecodeLong () throws Exception
  {
    assertEquals ("xxxÿxÿxÿÿÿÿÿÿÿ", decode ("xxx+AP8-x+AP8-x+AP8A/wD/AP8A/wD/AP8-"));
    assertEquals ("\u2262\u0391123\u2262\u0391", decode ("+ImIDkQ-123+ImIDkQ"));
  }

  @Test
  public void testDecodeUnclosed () throws Exception
  {
    assertEquals ("ÿÿÿ#", decode ("+AP8A/wD/#"));
    assertEquals ("ÿÿÿ#", decode ("+AP8A/wD/#"));
    assertEquals ("ÿÿ#", decode ("+AP8A/w#"));
    assertEquals ("#áá#ááá#", decode ("#+AOEA4Q#+AOEA4QDh#"));
  }

  @Test
  public void testDecodeNoUnshiftAtEnd () throws Exception
  {
    assertEquals ("€áé", decode ("+IKwA4QDp"));
    assertEquals ("#ááá", decode ("#+AOEA4QDh"));
  }

  @Test
  public void testDecodeMalformed () throws Exception
  {
    _verifyMalformed ("+IKx#");
    _verifyMalformed ("+IKwA#");
    _verifyMalformed ("+IKwA4#");
    assertEquals ("€á", decode ("+IKwA4Q"));
  }

  @Test
  public void testDecodeOptionalCharsUTF7 () throws Exception
  {
    assertEquals ("~!@", decode ("+AH4AIQBA-"));
  }

  @Test
  public void testDecodeOptionalCharsPlain () throws Exception
  {
    assertEquals ("!\"#$%*;<=>@[]^_'{|}", decode ("!\"#$%*;<=>@[]^_'{|}"));
  }

  @Test
  public void testDecodeLimitedOutput () throws Exception
  {
    final CharsetDecoder decoder = tested.newDecoder ();
    ByteBuffer in = CharsetTestHelper.wrap ("+IKwA4QDp-");
    CharBuffer out = CharBuffer.allocate (3);
    assertEquals (CoderResult.UNDERFLOW, decoder.decode (in, out, true));
    assertEquals (CoderResult.UNDERFLOW, decoder.flush (out));
    out.flip ();
    assertEquals ("€áé", out.toString ());
    decoder.reset ();
    in = CharsetTestHelper.wrap ("A+ImIDkQ.");
    out = CharBuffer.allocate (4);
    assertEquals (CoderResult.UNDERFLOW, decoder.decode (in, out, true));
    out.flip ();
    assertEquals ("A\u2262\u0391.", out.toString ());
  }

  private void _verifyMalformed (final String string) throws UnsupportedEncodingException
  {
    final ByteBuffer in = CharsetTestHelper.wrap (string);
    final CharBuffer out = CharBuffer.allocate (1024);
    final CoderResult result = tested.newDecoder ().decode (in, out, false); // "€#"
    assertTrue (result.isMalformed ());
  }

  @Test
  public void testEncodeSimple () throws Exception
  {
    assertEquals ("abcdefghijklmnopqrstuvwxyz",
                  CharsetTestHelper.asString (tested.encode ("abcdefghijklmnopqrstuvwxyz")));
    assertEquals (" \r\t\n", CharsetTestHelper.asString (tested.encode (" \r\t\n")));
  }

  @Test
  public void testEncodeBase64 () throws Exception
  {
    assertEquals ("A+ImIDkQ.", encode ("A\u2262\u0391."));
  }

  @Test
  public void testEncodeBase64NoImplicitUnshift () throws Exception
  {
    assertEquals ("A+ImIDkQ-A", encode ("A\u2262\u0391A"));
  }

  @Test
  public void testEncodeLong () throws Exception
  {
    assertEquals ("+IKwA4QDpAPoA7QDzAP0A5ADrAO8A9gD8AP8-", encode ("€áéúíóýäëïöüÿ"));
  }

  @Test
  public void testEncodeShiftUnshift () throws Exception
  {
    assertEquals ("+--", encode ("+-"));
  }

  @Test
  public void testEncodeAddUnshiftOnUnshift () throws Exception
  {
    assertEquals ("+AO0AKw--", encode ("í+-"));
  }

  @Test
  public void testEncodeNormalAndDifferent () throws Exception
  {
    assertEquals ("xxx+AP8-x+AP8-x+AP8A/wD/AP8A/wD/AP8-", encode ("xxxÿxÿxÿÿÿÿÿÿÿ"));
    assertEquals ("+AP8A/wD/AP8-x+AP8A/wD/AP8-", encode ("ÿÿÿÿxÿÿÿÿ"));
    assertEquals ("abc+AOEA6QDt-def+APMA+gDk-gh", encode ("abcáéídefóúägh"));
  }

  @Test
  public void testEncodeOptionalCharsUTF7 () throws Exception
  {
    assertEquals ("+ACEAIgAjACQAJQAqADsAPAA9AD4AQABbAF0AXgBfAGAAewB8AH0-", encode ("!\"#$%*;<=>@[]^_`{|}"));
  }

  @Test
  public void testEncodeAlphabet () throws Exception
  {
    assertEquals ("+AL4AvgC+-", encode ("¾¾¾"));
    assertEquals ("+AL8AvwC/-", encode ("¿¿¿"));
  }
}
