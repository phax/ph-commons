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
package com.helger.xml.serialize.read;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

import com.helger.commons.charset.EUnicodeBOM;
import com.helger.commons.collection.ArrayHelper;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.string.StringHelper;

public final class XMLCharsetDeterminatorTest
{
  @Test
  public void testAllCharsetsDoubleQuotes ()
  {
    for (final Charset c : XMLCharsetDeterminator.getAllSupportedCharsets ())
    {
      final ICommonsList <String> aNames = new CommonsArrayList <> (c.name ());
      aNames.addAll (c.aliases ());
      for (final String sAlias : aNames)
      {
        final String sXML = "<?xml version=\"1.0\" encoding=\"" + sAlias + "\"?><!-- " + c.name () + " --><root />";
        if (false)
          System.out.println (sXML);
        final byte [] aBytes = sXML.getBytes (c);
        final Charset aDetermined = XMLCharsetDeterminator.determineXMLCharset (aBytes);
        assertEquals (c, aDetermined);
      }
    }
  }

  @Test
  public void testAllCharsetsSingleQuotes ()
  {
    for (final Charset c : XMLCharsetDeterminator.getAllSupportedCharsets ())
    {
      final ICommonsList <String> aNames = new CommonsArrayList <> (c.name ());
      aNames.addAll (c.aliases ());
      for (final String sAlias : aNames)
      {
        final String sXML = "<?xml version=\"1.0\" encoding='" + sAlias + "'?><!-- " + c.name () + " --><root />";
        if (false)
          System.out.println (sXML);
        final byte [] aBytes = sXML.getBytes (c);
        final Charset aDetermined = XMLCharsetDeterminator.determineXMLCharset (aBytes);
        assertEquals (c, aDetermined);
      }
    }
  }

  @Test
  public void testAllBOMCharsets ()
  {
    for (final EUnicodeBOM eBOM : EUnicodeBOM.values ())
      if (eBOM.hasCharset ())
      {
        final Charset c = eBOM.getCharset ();
        final ICommonsList <String> aNames = new CommonsArrayList <> (c.name ());
        aNames.addAll (c.aliases ());
        for (final String sAlias : aNames)
        {
          final String sXML = "<?xml version=\"1.0\" encoding=\"" + sAlias + "\"?><!-- " + c.name () + " --><root />";
          if (false)
            System.out.println (sXML);
          final byte [] aBytes = sXML.getBytes (c);
          assertFalse ("Charset " + sAlias + " already contains BOM " + eBOM, eBOM.isPresent (aBytes));

          // Prefix with BOM
          final Charset aDetermined = XMLCharsetDeterminator.determineXMLCharset (ArrayHelper.getConcatenated (eBOM.getAllBytes (),
                                                                                                               aBytes));
          assertEquals (c, aDetermined);
        }
      }
  }

  private static void _testUTF8Good (final String sXML)
  {
    final byte [] aBytes = sXML.getBytes (StandardCharsets.UTF_8);
    final Charset aDetermined = XMLCharsetDeterminator.determineXMLCharset (aBytes);
    assertEquals (StandardCharsets.UTF_8, aDetermined);
  }

  private static void _testUTF8Bad (final String sXML)
  {
    final byte [] aBytes = sXML.getBytes (StandardCharsets.UTF_8);
    final Charset aDetermined = XMLCharsetDeterminator.determineXMLCharset (aBytes);
    assertNull (aDetermined);
  }

  @Test
  public void testParser ()
  {
    // Double quotes
    _testUTF8Good ("<?xml version=\"1.0\" encoding=\"utf-8\"?><root />");
    // Single quotes
    _testUTF8Good ("<?xml version=\"1.0\" encoding='utf-8'?><root />");
    // Blanks around
    _testUTF8Good ("<?xml version=\"1.0\" encoding  =   \"utf-8\"?><root />");
    _testUTF8Good ("<?xml version=\"1.0\" encoding  =   'utf-8'?><root />");
    // Blanks inside
    // Blanks inside
    _testUTF8Good ("<?xml version=\"1.0\" encoding=\"   utf-8  \"?><root />");
    _testUTF8Good ("<?xml version=\"1.0\" encoding='   utf-8  '?><root />");
    // Blanks inside and around
    _testUTF8Good ("<?xml version=\"1.0\"   encoding  =   \"  utf-8 \"  ?><root />");
    _testUTF8Good ("<?xml version=\"1.0\"   encoding  =   '   utf-8   '  ?><root />");
    // Upper case
    _testUTF8Good ("<?xml version=\"1.0\" encoding=\"UTF-8\"?><root />");
    _testUTF8Good ("<?xml version=\"1.0\" encoding='UTF-8'?><root />");
    // Mixed case
    _testUTF8Good ("<?xml version=\"1.0\" encoding=\"Utf-8\"?><root />");
    _testUTF8Good ("<?xml version=\"1.0\" encoding='Utf-8'?><root />");

    // -- no version

    // Double quotes
    _testUTF8Good ("<?xml encoding=\"utf-8\"?><root />");
    // Single quotes
    _testUTF8Good ("<?xml encoding='utf-8'?><root />");
    // Blanks around
    _testUTF8Good ("<?xml encoding  =   \"utf-8\"?><root />");
    _testUTF8Good ("<?xml encoding  =   'utf-8'?><root />");
    // Blanks inside
    // Blanks inside
    _testUTF8Good ("<?xml encoding=\"   utf-8  \"?><root />");
    _testUTF8Good ("<?xml encoding='   utf-8  '?><root />");
    // Blanks inside and around
    _testUTF8Good ("<?xml   encoding  =   \"  utf-8 \"  ?><root />");
    _testUTF8Good ("<?xml   encoding  =   '   utf-8   '  ?><root />");
    // Upper case
    _testUTF8Good ("<?xml encoding=\"UTF-8\"?><root />");
    _testUTF8Good ("<?xml encoding='UTF-8'?><root />");
    // Mixed case
    _testUTF8Good ("<?xml encoding=\"Utf-8\"?><root />");
    _testUTF8Good ("<?xml encoding='Utf-8'?><root />");

    // -- first encoding than version

    // Double quotes
    _testUTF8Good ("<?xml encoding=\"utf-8\" version=\"1.0\"?><root />");
    // Single quotes
    _testUTF8Good ("<?xml encoding='utf-8' version=\"1.0\"?><root />");
    // Blanks around
    _testUTF8Good ("<?xml encoding  =   \"utf-8\" version=\"1.0\"?><root />");
    _testUTF8Good ("<?xml encoding  =   'utf-8' version=\"1.0\"?><root />");
    // Blanks inside
    // Blanks inside
    _testUTF8Good ("<?xml encoding=\"   utf-8  \" version=\"1.0\"?><root />");
    _testUTF8Good ("<?xml encoding='   utf-8  ' version=\"1.0\"?><root />");
    // Blanks inside and around
    _testUTF8Good ("<?xml   encoding  =   \"  utf-8 \"    version=\"1.0\"?><root />");
    _testUTF8Good ("<?xml   encoding  =   '   utf-8   '   version=\"1.0\"?><root />");
    // Upper case
    _testUTF8Good ("<?xml encoding=\"UTF-8\" version=\"1.0\"?><root />");
    _testUTF8Good ("<?xml encoding='UTF-8' version=\"1.0\"?><root />");
    // Mixed case
    _testUTF8Good ("<?xml encoding=\"Utf-8\" version=\"1.0\"?><root />");
    _testUTF8Good ("<?xml encoding='Utf-8' version=\"1.0\"?><root />");

    // Bad cases
    _testUTF8Bad ("");
    _testUTF8Bad (StringHelper.getRepeated (' ', 10_000));
    _testUTF8Bad ("abc");
    _testUTF8Bad ("<");
    _testUTF8Bad ("<?");
    _testUTF8Bad ("<?x");
    _testUTF8Bad ("<?xm");
    _testUTF8Bad ("<?xml");
    _testUTF8Bad ("<?xml version=\"1.0\"");
    _testUTF8Bad ("<?xml version=\"1.0\" encoding");
    _testUTF8Bad ("<?xml version=\"1.0\" encoding=");
    _testUTF8Bad ("<?xml version=\"1.0\" encoding=\"");
    _testUTF8Bad ("<?xml version=\"1.0\" encoding=\"utf-8");
    _testUTF8Bad ("<?xml version=\"1.0\" encoding=\"utf-8'");
    _testUTF8Bad ("<?xml version=\"1.0\" encoding=\"utf-8?>");
    _testUTF8Bad ("<?xml version=\"1.0\" encoding='");
    _testUTF8Bad ("<?xml version=\"1.0\" encoding='utf-8");
    _testUTF8Bad ("<?xml version=\"1.0\" encoding='utf-8\"");
    _testUTF8Bad ("<?xml version=\"1.0\" encoding='utf-8?>");
  }
}
