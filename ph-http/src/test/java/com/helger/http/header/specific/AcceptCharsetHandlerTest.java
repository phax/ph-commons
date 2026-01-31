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
package com.helger.http.header.specific;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.nio.charset.StandardCharsets;

import org.junit.Test;

import com.helger.base.equals.EqualsHelper;

public final class AcceptCharsetHandlerTest
{
  @Test
  public void testEmpty ()
  {
    final AcceptCharsetList c = AcceptCharsetHandler.getAcceptCharsets (null);
    assertNotNull (c);
    assertEquals ("*; q=1.0", c.getAsHttpHeaderValue ());
  }

  @Test
  public void testSimple ()
  {
    final AcceptCharsetList c = AcceptCharsetHandler.getAcceptCharsets ("UTF-8");
    assertNotNull (c);
    // Explicitly contained
    assertTrue (EqualsHelper.equals (1d, c.getQualityOfCharset ("UTF-8")));
    // Not contained
    assertTrue (EqualsHelper.equals (0d, c.getQualityOfCharset ("ISO-8859-15")));
    // Default charset
    assertTrue (EqualsHelper.equals (1d, c.getQualityOfCharset ("ISO-8859-1")));
  }

  @Test
  public void testSimpleWithQuality ()
  {
    final AcceptCharsetList c = AcceptCharsetHandler.getAcceptCharsets ("UTF-8;q=0.5");
    assertNotNull (c);
    // Explicitly contained
    assertTrue (EqualsHelper.equals (0.5d, c.getQualityOfCharset ("UTF-8")));
    // Not contained
    assertTrue (EqualsHelper.equals (0d, c.getQualityOfCharset ("ISO-8859-15")));
    // Default charset
    assertTrue (EqualsHelper.equals (1d, c.getQualityOfCharset ("ISO-8859-1")));
  }

  @Test
  public void testSimpleWithAll ()
  {
    final AcceptCharsetList c = AcceptCharsetHandler.getAcceptCharsets ("UTF-8,*");
    assertNotNull (c);
    // Explicitly contained
    assertTrue (EqualsHelper.equals (1d, c.getQualityOfCharset ("UTF-8")));
    // Not contained
    assertTrue (EqualsHelper.equals (1d, c.getQualityOfCharset ("ISO-8859-15")));
    // Default charset
    assertTrue (EqualsHelper.equals (1d, c.getQualityOfCharset ("ISO-8859-1")));
  }

  @Test
  public void testSimpleWithAllWithQuality ()
  {
    final AcceptCharsetList c = AcceptCharsetHandler.getAcceptCharsets ("UTF-8,*;q=0.9");
    assertNotNull (c);
    // Explicitly contained
    assertTrue (EqualsHelper.equals (1d, c.getQualityOfCharset ("UTF-8")));
    // Not contained
    assertTrue (EqualsHelper.equals (0.9d, c.getQualityOfCharset ("ISO-8859-15")));
    // Default charset
    assertTrue (EqualsHelper.equals (0.9d, c.getQualityOfCharset ("ISO-8859-1")));
  }

  @Test
  public void testSimpleWithQualityWithAllWithQuality ()
  {
    final AcceptCharsetList c = AcceptCharsetHandler.getAcceptCharsets ("UTF-8;q=0.2,*;q=0.9");
    assertNotNull (c);
    // Explicitly contained
    assertTrue (EqualsHelper.equals (0.2d, c.getQualityOfCharset ("UTF-8")));
    // Not contained
    assertTrue (EqualsHelper.equals (0.9d, c.getQualityOfCharset ("ISO-8859-15")));
    // Default charset
    assertTrue (EqualsHelper.equals (0.9d, c.getQualityOfCharset ("ISO-8859-1")));
  }

  @Test
  public void testGetAsHttpHeaderValue ()
  {
    final AcceptCharsetList c = new AcceptCharsetList ();
    c.addCharset (StandardCharsets.UTF_8, 1);
    c.addCharset (StandardCharsets.ISO_8859_1, 0.9);
    c.addCharset ("*", 0.1);
    final String s = c.getAsHttpHeaderValue ();
    assertEquals ("utf-8; q=1.0, iso-8859-1; q=0.9, *; q=0.1", s);
    final AcceptCharsetList c2 = AcceptCharsetHandler.getAcceptCharsets (s);
    assertNotNull (c2);
    assertEquals (c, c2);
  }
}
