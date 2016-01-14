/**
 * Copyright (C) 2014-2016 Philip Helger (www.helger.com)
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
package com.helger.commons.mime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.helger.commons.mock.AbstractCommonsTestCase;

/**
 * Test class for class {@link MimeTypeParser}
 *
 * @author Philip Helger
 */
public final class MimeTypeParserTest extends AbstractCommonsTestCase
{
  @Test
  public void testIsToken ()
  {
    assertFalse (MimeTypeParser.isToken (null));
    assertFalse (MimeTypeParser.isToken (""));
    assertFalse (MimeTypeParser.isToken ("  "));
    assertFalse (MimeTypeParser.isToken (" any"));
    assertFalse (MimeTypeParser.isToken ("any "));
    assertFalse (MimeTypeParser.isToken ("charset="));

    assertTrue (MimeTypeParser.isToken ("a"));
    assertTrue (MimeTypeParser.isToken ("param"));
    assertTrue (MimeTypeParser.isToken ("param1"));
    assertTrue (MimeTypeParser.isToken ("charset"));
  }

  @Test
  public void testCreateFromString ()
  {
    IMimeType mt;
    assertNull (MimeTypeParser.parseMimeType (null));
    assertNull (MimeTypeParser.parseMimeType (""));

    mt = MimeTypeParser.parseMimeType ("text/x");
    assertNotNull (mt);
    assertSame (EMimeContentType.TEXT, mt.getContentType ());
    assertEquals ("x", mt.getContentSubType ());
    assertEquals ("text/x", mt.getAsString ());

    mt = MimeTypeParser.parseMimeType ("text/x;");
    assertNotNull (mt);
    assertSame (EMimeContentType.TEXT, mt.getContentType ());
    assertEquals ("x", mt.getContentSubType ());
    assertEquals ("text/x", mt.getAsString ());

    mt = MimeTypeParser.parseMimeType ("text/x;param1=x;param2=y");
    assertNotNull (mt);
    assertSame (EMimeContentType.TEXT, mt.getContentType ());
    assertEquals ("x", mt.getContentSubType ());
    assertEquals ("text/x;param1=x;param2=y", mt.getAsString ());

    mt = MimeTypeParser.parseMimeType (" text / x ; param1 = x ; param2 = y ");
    assertNotNull (mt);
    assertSame (EMimeContentType.TEXT, mt.getContentType ());
    assertEquals ("x", mt.getContentSubType ());
    assertEquals ("text/x;param1=x;param2=y", mt.getAsString ());
  }

  @Test
  public void testParseValid ()
  {
    final String [] aValid = new String [] { "text/x",
                                             "text/x;",
                                             "text/x;         ",
                                             "text/x; param1=x",
                                             "text/x;param1=x;param2=y",
                                             " text / x ; param1 = x ; param2 = y " };
    for (final EMimeQuoting eQuoting : EMimeQuoting.values ())
      for (final String sValid : aValid)
      {
        final MimeType aMT = MimeTypeParser.parseMimeType (sValid, eQuoting);
        assertNotNull (aMT);

        final String sMT = aMT.getAsString (eQuoting);
        final MimeType aMT2 = MimeTypeParser.parseMimeType (sMT, eQuoting);
        assertEquals (aMT, aMT2);
      }
  }

  @Test
  public void testParseValidURLEscape ()
  {
    final String [] aValid = new String [] { "text/x;param1=%2c%2c%2c%2c%2c%2c",
                                             "text/x;param1=%2c%2c%2c%2c%2c%2c;param2=%2c%2c%2c%2c%2c%2c",
                                             " text / x ; param1 = x ; param2 = y ; param3 = ab%2cde; param4=%20%20" };
    for (final EMimeQuoting eQuoting : EMimeQuoting.values ())
      for (final String sValid : aValid)
      {
        final MimeType aMT = MimeTypeParser.parseMimeType (sValid, EMimeQuoting.URL_ESCAPE);
        assertNotNull (aMT);

        final String sMT = aMT.getAsString (eQuoting);
        final MimeType aMT2 = MimeTypeParser.parseMimeType (sMT, eQuoting);
        assertEquals (aMT, aMT2);
      }
  }

  @Test
  public void testParseInvalid ()
  {
    final String [] aInvalid = new String [] { "text",
                                               "text/;",
                                               "text/;  ",
                                               "foo/bar",
                                               "/x",
                                               "te xt/x",
                                               "text/x;param1",
                                               "text/x; param1",
                                               "text/x;param1 ",
                                               "text/x;param1=",
                                               "text/x;param1= ",
                                               "text/x;param1 =",
                                               "text/x;param1 = ",
                                               "text/x;param1 = ;",
                                               "text/x;param1 =x;",
                                               "text/x;param1 =x;param2",
                                               "text/x;param1 =x;param2=",
                                               "text/x;param1 =x;param2= ",
                                               "text/x;param1 =x;param2 =",
                                               "text/x;param1 =x;param2 = " };
    for (final EMimeQuoting eQuoting : EMimeQuoting.values ())
      for (final String sInvalid : aInvalid)
      {
        try
        {
          final MimeType aMT = MimeTypeParser.parseMimeType (sInvalid, eQuoting);
          fail ("'" + sInvalid + "' should not be parsable with quoting " + eQuoting + "! Got " + aMT);
        }
        catch (final MimeTypeParserException ex)
        {}
      }
  }

  @Test
  public void testParseInvalidQuotedString ()
  {
    final String [] aInvalid = new String [] { // Incorrectly quoted
                                               "text/plain;param=\"",
                                               "text/plain;param= \"",
                                               "text/plain;param=\"abc",
                                               "text/plain;param=\"abc\\",
                                               "text/plain;param=\"abc\\\"",
                                               // With leading token-based
                                               // parameter
                                               "text/plain;param0=xml;param=\"",
                                               "text/plain;param0=xml;param= \"",
                                               "text/plain;param0=xml;param=\"abc",
                                               "text/plain;param0=xml;param=\"abc\\",
                                               "text/plain;param0=xml;param=\"abc\\\"",
                                               // With leading quoted string
                                               // parameter
                                               "text/plain;param0=\"foo bar\";param=\"",
                                               "text/plain;param0=\"foo bar\";param= \"",
                                               "text/plain;param0=\"foo bar\";param=\"abc",
                                               "text/plain;param0=\"foo bar\";param=\"abc\\",
                                               "text/plain;param0=\"foo bar\";param=\"abc\\\"" };
    for (final String sInvalid : aInvalid)
    {
      try
      {
        final MimeType aMT = MimeTypeParser.parseMimeType (sInvalid, EMimeQuoting.QUOTED_STRING);
        fail ("'" + sInvalid + "' should not be parsable with quoting " + EMimeQuoting.QUOTED_STRING + "! Got " + aMT);
      }
      catch (final MimeTypeParserException ex)
      {}
    }
  }

  @Test
  public void testParseInvalidURLEscape ()
  {
    final String [] aInvalid = new String [] { // Invalid URL escaped
                                               "text/plain;param=abc%",
                                               "text/plain;param=abc%1",
                                               "text/plain;param=abc%bb%",
                                               "text/plain;param=abc%bb%a",
                                               "text/plain;param=abc%g0",
                                               "text/plain;param=abc%0g" };
    for (final String sInvalid : aInvalid)
    {
      try
      {
        final MimeType aMT = MimeTypeParser.parseMimeType (sInvalid, EMimeQuoting.URL_ESCAPE);
        fail ("'" + sInvalid + "' should not be parsable with quoting " + EMimeQuoting.URL_ESCAPE + "! Got " + aMT);
      }
      catch (final MimeTypeParserException ex)
      {}
    }
  }

  @Test
  public void testParseInvalidQuotedPrintable ()
  {
    final String [] aInvalid = new String [] { // Invalid Quoted Printable
                                               "text/plain;param=abc=",
                                               "text/plain;param=abc=1",
                                               "text/plain;param=abc=bb=",
                                               "text/plain;param=abc=bb=a",
                                               "text/plain;param=abc=g0",
                                               "text/plain;param=abc=0g" };
    for (final String sInvalid : aInvalid)
    {
      try
      {
        final MimeType aMT = MimeTypeParser.parseMimeType (sInvalid, EMimeQuoting.QUOTED_PRINTABLE);
        fail ("'" +
              sInvalid +
              "' should not be parsable with quoting " +
              EMimeQuoting.QUOTED_PRINTABLE +
              "! Got " +
              aMT);
      }
      catch (final MimeTypeParserException ex)
      {}
    }
  }
}
