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
package com.helger.url;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.CommonsLinkedHashMap;
import com.helger.collection.commons.ICommonsMap;
import com.helger.url.param.URLParameter;

/**
 * Test class for class {@link URLBuilder}.
 *
 * @author Philip Helger
 */
public final class URLBuilderTest
{
  private static final String URL_STR = "http://www.helger.com/index.html";

  @Test
  public void testEmptyBuilder ()
  {
    final URLBuilder aBuilder = new URLBuilder ();
    assertNotNull (aBuilder.urlData ());

    final ISimpleURL aURL = aBuilder.build ();
    assertNotNull (aURL);
    assertEquals ("", aURL.getPath ());
    assertTrue (aURL.getAllParams ().isEmpty ());
    assertNull (aURL.getAnchor ());
  }

  @Test
  public void testPathAndAnchor ()
  {
    final ISimpleURL aURL = new URLBuilder ().path (URL_STR).anchor ("top").build ();
    assertEquals (URL_STR, aURL.getPath ());
    assertEquals ("top", aURL.getAnchor ());
    assertEquals (URL_STR + "#top", aURL.getAsString ());

    // A null anchor removes it
    assertNull (new URLBuilder ().path (URL_STR).anchor ("top").anchor (null).build ().getAnchor ());
  }

  @Test
  public void testAddParamOverloads ()
  {
    final ISimpleURL aURL = new URLBuilder ().path (URL_STR)
                                             .addParam ("b", true)
                                             .addParam ("i", 42)
                                             .addParam ("l", 42L)
                                             .addParam ("s", "value")
                                             .addParam (new URLParameter ("p", "pv"))
                                             .build ();
    assertEquals (5, aURL.getAllParams ().size ());
    assertEquals ("true", aURL.getFirstParamValue ("b"));
    assertEquals ("42", aURL.getFirstParamValue ("i"));
    assertEquals ("42", aURL.getFirstParamValue ("l"));
    assertEquals ("value", aURL.getFirstParamValue ("s"));
    assertEquals ("pv", aURL.getFirstParamValue ("p"));
    assertNull (aURL.getFirstParamValue ("does-not-exist"));
  }

  @Test
  public void testParamReplacesExisting ()
  {
    final URLBuilder aBuilder = new URLBuilder ().path (URL_STR).addParam ("a", "1").addParam ("a", "2");
    assertEquals (2, aBuilder.build ().getAllParams ().size ());

    // "param" replaces all existing ones with the same name
    aBuilder.param ("a", "3");
    assertEquals (1, aBuilder.build ().getAllParams ().size ());
    assertEquals ("3", aBuilder.build ().getFirstParamValue ("a"));

    aBuilder.param (new URLParameter ("a", "4"));
    assertEquals (1, aBuilder.build ().getAllParams ().size ());
    assertEquals ("4", aBuilder.build ().getFirstParamValue ("a"));
  }

  @Test
  public void testRemoveParam ()
  {
    final URLBuilder aBuilder = new URLBuilder ().path (URL_STR).addParam ("a", "1").addParam ("b", "2");
    assertSame (aBuilder, aBuilder.removeParam ("a"));
    assertEquals (1, aBuilder.build ().getAllParams ().size ());
    assertNull (aBuilder.build ().getFirstParamValue ("a"));

    // Removing a non existing parameter is a no-op
    aBuilder.removeParam ("does-not-exist");
    assertEquals (1, aBuilder.build ().getAllParams ().size ());
  }

  @Test
  public void testParamsFromMapAndList ()
  {
    final ICommonsMap <String, String> aMap = new CommonsLinkedHashMap <> ();
    aMap.put ("a", "1");
    aMap.put ("b", "2");

    final ISimpleURL aURL = new URLBuilder ().path (URL_STR).params (aMap).build ();
    assertEquals (2, aURL.getAllParams ().size ());

    final ISimpleURL aURL2 = new URLBuilder ().path (URL_STR)
                                              .params (new CommonsArrayList <> (new URLParameter ("c", "3")))
                                              .build ();
    assertEquals (1, aURL2.getAllParams ().size ());

    // null clears the parameters
    assertTrue (new URLBuilder ().path (URL_STR)
                                 .params (aMap)
                                 .params ((ICommonsMap <String, String>) null)
                                 .build ()
                                 .getAllParams ()
                                 .isEmpty ());
  }

  @Test
  public void testCharset ()
  {
    final ISimpleURL aURL = new URLBuilder ().path (URL_STR).charset (StandardCharsets.ISO_8859_1).build ();
    assertSame (StandardCharsets.ISO_8859_1, aURL.getCharset ());

    assertNull (new URLBuilder ().path (URL_STR).charset (null).build ().getCharset ());
  }

  @Test
  public void testOfURLData ()
  {
    final ISimpleURL aSource = new URLBuilder ().path (URL_STR).addParam ("a", "1").anchor ("top").build ();

    final URLBuilder aBuilder = URLBuilder.of (aSource);
    final ISimpleURL aCopy = aBuilder.build ();
    assertEquals (URL_STR, aCopy.getPath ());
    assertEquals ("1", aCopy.getFirstParamValue ("a"));
    assertEquals ("top", aCopy.getAnchor ());

    // Modifying the copy must not modify the source
    aBuilder.addParam ("b", "2");
    assertEquals (1, aSource.getAllParams ().size ());

    // null gives an empty builder
    assertEquals ("", URLBuilder.of ((IURLDataHolder) null).build ().getPath ());
  }

  /** Only needed to disambiguate the null overloads of {@link URLBuilder#of} */
  private interface IURLDataHolder extends com.helger.url.data.IURLData
  {
    /* empty */
  }

  @Test
  public void testOfString ()
  {
    assertEquals (URL_STR, URLBuilder.of (URL_STR).build ().getPath ());
    assertEquals ("1", URLBuilder.of (URL_STR + "?a=1").build ().getFirstParamValue ("a"));
    assertEquals ("", URLBuilder.of ((String) null).build ().getPath ());
    assertEquals ("", URLBuilder.of ("").build ().getPath ());
  }

  @Test
  public void testOfURLAndURI () throws Exception
  {
    assertEquals (URL_STR, URLBuilder.of (new URI (URL_STR).toURL ()).build ().getPath ());
    assertEquals (URL_STR, URLBuilder.of (new URI (URL_STR)).build ().getPath ());
    assertEquals ("", URLBuilder.of ((URL) null).build ().getPath ());
    assertEquals ("", URLBuilder.of ((URI) null).build ().getPath ());
  }

  @Test
  public void testInvalidParams ()
  {
    final URLBuilder aBuilder = new URLBuilder ();
    try
    {
      aBuilder.path (null);
      fail ();
    }
    catch (final NullPointerException | IllegalArgumentException ex)
    {
      // expected
    }
    try
    {
      aBuilder.addParam ((URLParameter) null);
      fail ();
    }
    catch (final NullPointerException | IllegalArgumentException ex)
    {
      // expected
    }
    assertFalse (aBuilder.build ().getAllParams ().isNotEmpty ());
  }
}
