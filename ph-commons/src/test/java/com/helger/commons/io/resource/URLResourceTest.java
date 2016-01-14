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
package com.helger.commons.io.resource;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Test;

import com.helger.commons.charset.CCharset;
import com.helger.commons.io.stream.StreamHelper;
import com.helger.commons.mock.AbstractCommonsTestCase;
import com.helger.commons.mock.CommonsTestHelper;
import com.helger.commons.url.URLHelper;

/**
 * Test class for class {@link URLResource}.
 *
 * @author Philip Helger
 */
public final class URLResourceTest extends AbstractCommonsTestCase
{
  @Test
  public void testCtor () throws MalformedURLException
  {
    assertNotNull (new URLResource ("http://www.helger.com"));
    assertNotNull (new URLResource ("http://www.helger.com/"));
    assertNotNull (new URLResource ("http://www.helger.com#anchor"));
    assertNotNull (new URLResource ("http://www.helger.com?param=value"));
    assertNotNull (new URLResource ("http://www.helger.com?param=value#anchor"));
    assertNotNull (new URLResource ("http://www.helger.com/path"));
    assertNotNull (new URLResource ("http://www.helger.com/path#anchor"));
    assertNotNull (new URLResource ("http://www.helger.com/path?param=value"));
    assertNotNull (new URLResource ("http://www.helger.com/path?param=value#anchor"));
    assertNotNull (new URLResource ("file://test.txt"));
    new URLResource ("http://www.helger.com").exists ();
    new URLResource ("http://dfgsdfdfgsdfgsdfghhh").exists ();

    try
    {
      // no protocol
      new URLResource ("test1.txt");
      fail ();
    }
    catch (final MalformedURLException ex)
    {}

    try
    {
      new URLResource ((URL) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testAccess () throws IOException
  {
    final URL aFileURL = new File ("pom.xml").toURI ().toURL ();
    final URLResource ur = new URLResource (aFileURL);
    assertTrue (ur.exists ());
    assertTrue (ur.getResourceID ().endsWith ("/pom.xml"));
    assertTrue (ur.getPath ().endsWith ("/pom.xml"));
    final byte [] aBytes = StreamHelper.getAllBytes (ur);
    assertTrue (aBytes.length > 0);
    assertNotNull (ur.getAsURL ());
    assertNotNull (ur.getAsFile ());
    ur.getReader (CCharset.CHARSET_ISO_8859_1_OBJ).close ();

    final URL aNoNExistingURL = new File ("pom2.xml").toURI ().toURL ();
    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (ur, new URLResource (aFileURL));
    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (ur, ur.getReadableCloneForPath (aFileURL));
    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (ur,
                                                                       ur.getReadableCloneForPath (aFileURL.toExternalForm ()));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (ur, new URLResource (aNoNExistingURL));

    assertNotNull (URLHelper.getAsFile (aFileURL));
    assertNotNull (URLHelper.getAsFile (aNoNExistingURL));
    try
    {
      URLHelper.getAsFile (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      URLHelper.getAsFile (new URL ("http://www.google.com"));
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    assertNotNull (URLResource.getInputStream (aFileURL));
    assertNull (URLResource.getInputStream (aNoNExistingURL));
    assertNotNull (new URLResource (aFileURL).getReader (CCharset.CHARSET_ISO_8859_1_OBJ));
    assertNull (new URLResource (aNoNExistingURL).getReader (CCharset.CHARSET_ISO_8859_1_OBJ));
    try
    {
      URLResource.getInputStream ((URL) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      ur.getReadableCloneForPath ("bla fasel");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
  }

  @Test
  public void testSerialize () throws Exception
  {
    CommonsTestHelper.testDefaultSerialization (new URLResource ("http://www.helger.com"));
  }
}
