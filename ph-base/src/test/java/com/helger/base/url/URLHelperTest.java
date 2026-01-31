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

import java.io.File;
import java.io.InputStream;
import java.net.URL;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.base.io.stream.StreamHelper;

/**
 * Test class for class {@link URLHelper}.
 *
 * @author Philip Helger
 */
public final class URLHelperTest
{
  private static final Logger LOGGER = LoggerFactory.getLogger (URLHelperTest.class);

  @Test
  @Ignore ("Works only when being online")
  public void testGetInputStream ()
  {
    try
    {
      final InputStream aIS = URLHelper.getInputStream (new URL ("http://www.orf.at"), 3000, -1, null, null);
      final byte [] aContent = StreamHelper.getAllBytes (aIS);
      LOGGER.info ("Read " + aContent.length + " bytes");
    }
    catch (final Throwable t)
    {
      // ignore
      LOGGER.info ("Failed to GET: " + t.getMessage ());
    }
  }

  @Test
  public void testIsClassPathURLExisting ()
  {
    assertTrue (URLHelper.isClassPathURLExisting ("/test1.txt"));
    assertFalse (URLHelper.isClassPathURLExisting ("/test1 not existing.txt"));
  }

  @Test
  public void testGetAsURL ()
  {
    assertNull (URLHelper.getAsURL ("../common/file.xsd"));
    assertNotNull (URLHelper.getAsURL ("http://www.helger.com"));
  }

  @Test
  public void testGetAsURI ()
  {
    assertNotNull (URLHelper.getAsURI ("../common/file.xsd"));
    assertNotNull (URLHelper.getAsURI ("http://www.helger.com"));
    assertNotNull (URLHelper.getAsURI ("iso6523-actorid-upis"));
    assertNull (URLHelper.getAsURI ("test/##"));
    assertNull (URLHelper.getAsURI ("test##"));
  }

  @Test
  public void testGetAsFile ()
  {
    final URL u = URLHelper.getAsURL ("file:/../dir/include.xml");
    final File f = URLHelper.getAsFile (u);
    assertNotNull (f);
    assertEquals (new File ("/../dir/include.xml").getAbsolutePath (), f.getAbsolutePath ());
  }

  @Test
  public void testGetUrlString ()
  {
    assertNull (URLHelper.getURLString (null, null, null));
    assertEquals ("", URLHelper.getURLString ("", null, null));
    assertEquals ("a", URLHelper.getURLString ("a", null, null));
    assertEquals ("http://something", URLHelper.getURLString ("http://something", null, null));
    assertEquals ("/a?x=y&z=0#anchor", URLHelper.getURLString ("/a", "x=y&z=0", "anchor"));
    assertEquals ("/a?x=y&z=0", URLHelper.getURLString ("/a", "x=y&z=0", null));
    assertEquals ("/a#anchor", URLHelper.getURLString ("/a", null, "anchor"));
    assertEquals ("?x=y&z=0#anchor", URLHelper.getURLString (null, "x=y&z=0", "anchor"));
    assertEquals ("?x=y&z=0", URLHelper.getURLString (null, "x=y&z=0", null));
    assertEquals ("#anchor", URLHelper.getURLString (null, null, "anchor"));
  }
}
