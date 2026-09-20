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
package com.helger.base.url;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.net.URI;
import java.net.URL;

import org.junit.After;
import org.junit.Test;

import com.helger.base.debug.GlobalDebug;

/**
 * Additional test class for class {@link URLHelper}, covering the conversions and the class path
 * helpers.
 *
 * @author Philip Helger
 */
public final class URLHelperExtTest
{
  private static final String URL_STR = "http://www.helger.com/index.html";

  @After
  public void restoreDebugMode ()
  {
    GlobalDebug.setDebugModeDirect (GlobalDebug.DEFAULT_DEBUG_MODE);
  }

  @Test
  public void testEqualURLs () throws Exception
  {
    final URL aURL = new URI (URL_STR).toURL ();
    assertTrue (URLHelper.equalURLs (null, null));
    assertTrue (URLHelper.equalURLs (aURL, aURL));
    assertTrue (URLHelper.equalURLs (aURL, new URI (URL_STR).toURL ()));
    assertFalse (URLHelper.equalURLs (aURL, null));
    assertFalse (URLHelper.equalURLs (null, aURL));
    assertFalse (URLHelper.equalURLs (aURL, new URI ("http://example.org/").toURL ()));
  }

  @Test
  public void testGetURLString ()
  {
    assertEquals ("path", URLHelper.getURLString ("path", null, null));
    assertEquals ("path?a=1", URLHelper.getURLString ("path", "a=1", null));
    assertEquals ("path#top", URLHelper.getURLString ("path", null, "top"));
    assertEquals ("path?a=1#top", URLHelper.getURLString ("path", "a=1", "top"));
    assertEquals ("?a=1", URLHelper.getURLString (null, "a=1", null));
    assertEquals ("#top", URLHelper.getURLString (null, null, "top"));
    assertNull (URLHelper.getURLString (null, null, null));
  }

  @Test
  public void testGetURLStringConsistencyChecksInDebugMode ()
  {
    // The consistency checks only run in debug mode - they only log
    GlobalDebug.setDebugModeDirect (true);
    assertNotNull (URLHelper.getURLString ("path?x", "a=1", "top"));
    assertNotNull (URLHelper.getURLString ("path&x", "a=1", "top"));
    assertNotNull (URLHelper.getURLString ("path#x", "a=1", "top"));
    assertNotNull (URLHelper.getURLString ("path", "a=1?x", "top"));
    assertNotNull (URLHelper.getURLString ("path", "a=1", "top#x"));
  }

  @Test
  public void testGetAsURL () throws Exception
  {
    assertNotNull (URLHelper.getAsURL (URL_STR));
    assertNotNull (URLHelper.getAsURL (URL_STR, true));
    assertNull (URLHelper.getAsURL ((String) null));
    assertNull (URLHelper.getAsURL ("this is not a URL", false));
    assertNull (URLHelper.getAsURL ("this is not a URL", true));

    assertNotNull (URLHelper.getAsURL (new URI (URL_STR)));
    assertNull (URLHelper.getAsURL ((URI) null));
    // A relative URI is not absolute and can therefore not be converted
    try
    {
      URLHelper.getAsURL (new URI ("relative/path"));
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {
      // expected
    }
  }

  @Test
  public void testGetAsURI () throws Exception
  {
    assertNotNull (URLHelper.getAsURI (URL_STR));
    assertNull (URLHelper.getAsURI ((String) null));
    assertNull (URLHelper.getAsURI ("this is not a URI"));

    assertNotNull (URLHelper.getAsURI (new URI (URL_STR).toURL ()));
    assertNull (URLHelper.getAsURI ((URL) null));
  }

  @Test
  public void testGetAsFile () throws Exception
  {
    final File aFile = new File ("pom.xml").getAbsoluteFile ();
    final URL aFileURL = aFile.toURI ().toURL ();

    assertEquals (aFile, URLHelper.getAsFile (aFileURL));
    assertEquals (aFile, URLHelper.getAsFileOrNull (aFileURL));
    assertNull (URLHelper.getAsFileOrNull (null));

    // A non-file URL is not convertible
    final URL aHttpURL = new URI (URL_STR).toURL ();
    try
    {
      URLHelper.getAsFile (aHttpURL);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {
      // expected
    }
    assertNull (URLHelper.getAsFileOrNull (aHttpURL));
  }

  @Test
  public void testClassPathURL ()
  {
    assertNotNull (URLHelper.getClassPathURL ("com/helger/base/url/URLHelper.class"));
    assertNull (URLHelper.getClassPathURL ("does/not/exist.txt"));

    assertTrue (URLHelper.isClassPathURLExisting ("com/helger/base/url/URLHelper.class"));
    assertFalse (URLHelper.isClassPathURLExisting ("does/not/exist.txt"));

    final ClassLoader aCL = URLHelperExtTest.class.getClassLoader ();
    assertTrue (URLHelper.isClassPathURLExisting ("com/helger/base/url/URLHelper.class", aCL));
    assertFalse (URLHelper.isClassPathURLExisting ("does/not/exist.txt", aCL));
  }
}
