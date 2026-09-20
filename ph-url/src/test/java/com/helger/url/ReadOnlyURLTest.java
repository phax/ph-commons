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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsList;
import com.helger.unittest.support.TestHelper;
import com.helger.url.data.URLData;
import com.helger.url.param.URLParameter;

/**
 * Test class for class {@link ReadOnlyURL}.
 *
 * @author Philip Helger
 */
public final class ReadOnlyURLTest
{
  private static final String URL_STR = "http://www.helger.com/index.html";

  private static ReadOnlyURL _create ()
  {
    return ReadOnlyURL.of (URL_STR + "?a=1#top");
  }

  @Test
  public void testAccessors ()
  {
    final ReadOnlyURL aURL = _create ();
    assertEquals (URL_STR, aURL.getPath ());
    assertEquals (1, aURL.getAllParams ().size ());
    assertEquals ("1", aURL.getFirstParamValue ("a"));
    assertNull (aURL.getFirstParamValue ("does-not-exist"));
    assertEquals ("top", aURL.getAnchor ());
    assertNotNull (aURL.toString ());

    // Must be a copy
    final ICommonsList <URLParameter> aParams = aURL.getAllParams ();
    assertTrue (aURL.getAllParams () != aParams);
  }

  @Test
  public void testGetWithPath ()
  {
    final ReadOnlyURL aURL = _create ();
    // The same path returns the same instance
    assertSame (aURL, aURL.getWithPath (URL_STR));

    final ReadOnlyURL aOther = aURL.getWithPath ("http://example.org/");
    assertNotSame (aURL, aOther);
    assertEquals ("http://example.org/", aOther.getPath ());
    // The original is untouched
    assertEquals (URL_STR, aURL.getPath ());
  }

  @Test
  public void testGetWithParams ()
  {
    final ReadOnlyURL aURL = _create ();
    assertSame (aURL, aURL.getWithParams (aURL.getAllParams ()));

    final ReadOnlyURL aOther = aURL.getWithParams (new CommonsArrayList <> (new URLParameter ("b", "2")));
    assertNotSame (aURL, aOther);
    assertEquals ("2", aOther.getFirstParamValue ("b"));
    assertNull (aOther.getFirstParamValue ("a"));
    assertEquals ("1", aURL.getFirstParamValue ("a"));
  }

  @Test
  public void testGetWithAnchor ()
  {
    final ReadOnlyURL aURL = _create ();
    assertSame (aURL, aURL.getWithAnchor ("top"));

    final ReadOnlyURL aOther = aURL.getWithAnchor ("bottom");
    assertNotSame (aURL, aOther);
    assertEquals ("bottom", aOther.getAnchor ());
    assertEquals ("top", aURL.getAnchor ());

    assertNull (aURL.getWithAnchor (null).getAnchor ());
  }

  @Test
  public void testGetWithCharset ()
  {
    final ReadOnlyURL aURL = ReadOnlyURL.of (URL_STR, StandardCharsets.UTF_8);
    assertSame (StandardCharsets.UTF_8, aURL.getCharset ());
    assertSame (aURL, aURL.getWithCharset (StandardCharsets.UTF_8));

    final ReadOnlyURL aOther = aURL.getWithCharset (StandardCharsets.ISO_8859_1);
    assertNotSame (aURL, aOther);
    assertSame (StandardCharsets.ISO_8859_1, aOther.getCharset ());
  }

  @Test
  public void testFactories () throws Exception
  {
    assertEquals (URL_STR, ReadOnlyURL.of (URL_STR).getPath ());
    assertEquals (URL_STR, ReadOnlyURL.of (URL_STR, StandardCharsets.UTF_8).getPath ());

    final URL aJdkURL = new URI (URL_STR).toURL ();
    assertEquals (URL_STR, ReadOnlyURL.of (aJdkURL).getPath ());
    assertEquals (URL_STR, ReadOnlyURL.of (aJdkURL, StandardCharsets.UTF_8).getPath ());

    final URI aURI = new URI (URL_STR);
    assertEquals (URL_STR, ReadOnlyURL.of (aURI).getPath ());
    assertEquals (URL_STR, ReadOnlyURL.of (aURI, StandardCharsets.UTF_8).getPath ());

    assertEquals (URL_STR, ReadOnlyURL.of (new URLData (_create ())).getPath ());
  }

  @Test
  public void testEqualsHashcode ()
  {
    TestHelper.testDefaultImplementationWithEqualContentObject (_create (), _create ());
    TestHelper.testDefaultImplementationWithDifferentContentObject (_create (),
                                                                    ReadOnlyURL.of ("http://example.org/other"));
  }

  @SuppressWarnings ("removal")
  @Test
  public void testAsStringAndURL ()
  {
    final ReadOnlyURL aURL = _create ();
    assertNotNull (aURL.getAsString ());
    assertNotNull (aURL.getAsStringWithEncodedParameters ());
    assertNotNull (aURL.getAsStringWithEncodedParameters (StandardCharsets.UTF_8));
    assertNotNull (aURL.getAsStringWithoutEncodedParameters ());
    assertNotNull (aURL.getAsURL ());
    assertNotNull (aURL.getAsURI ());
  }

  @Test
  public void testInvalidParams ()
  {
    try
    {
      ReadOnlyURL.of ((String) null);
      fail ();
    }
    catch (final NullPointerException | IllegalArgumentException ex)
    {
      // expected
    }
  }
}
