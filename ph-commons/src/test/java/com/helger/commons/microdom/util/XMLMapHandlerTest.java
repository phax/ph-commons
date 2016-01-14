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
package com.helger.commons.microdom.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.helger.commons.charset.CCharset;
import com.helger.commons.io.IHasInputStream;
import com.helger.commons.io.IHasOutputStream;
import com.helger.commons.io.resource.ClassPathResource;
import com.helger.commons.io.resource.IReadableResource;
import com.helger.commons.io.stream.NonBlockingByteArrayOutputStream;
import com.helger.commons.io.streamprovider.ByteArrayOutputStreamProvider;
import com.helger.commons.io.streamprovider.StringInputStreamProvider;

/**
 * Test class for {@link XMLMapHandler}.
 *
 * @author Philip Helger
 */
public final class XMLMapHandlerTest
{
  @Test
  public void testReadBuildInfo ()
  {
    final Map <String, String> aMap = new HashMap <String, String> ();
    final IReadableResource aRes = new ClassPathResource ("xml/buildinfo.xml");
    assertTrue (XMLMapHandler.readMap (aRes, aMap).isSuccess ());
    assertNull (XMLMapHandler.readMap (new ClassPathResource ("test1.txt")));
    assertTrue (aMap.containsKey ("buildinfo.version"));
    assertEquals ("1", aMap.get ("buildinfo.version"));

    assertTrue (XMLMapHandler.readMap (aRes).containsKey ("buildinfo.version"));
    assertEquals ("1", XMLMapHandler.readMap (aRes).get ("buildinfo.version"));

    assertTrue (XMLMapHandler.writeMap (aMap, new ByteArrayOutputStreamProvider ()).isSuccess ());
    assertTrue (XMLMapHandler.writeMap (aMap, new NonBlockingByteArrayOutputStream ()).isSuccess ());
  }

  @Test
  public void testReadInvalid ()
  {
    final Map <String, String> aMap = new HashMap <String, String> ();
    final IReadableResource aRes = new ClassPathResource ("xml/buildinfo.xml");

    try
    {
      XMLMapHandler.readMap ((IHasInputStream) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      XMLMapHandler.readMap ((IHasInputStream) null, aMap);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      XMLMapHandler.readMap ((InputStream) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      XMLMapHandler.readMap ((InputStream) null, aMap);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      XMLMapHandler.readMap (aRes, null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    String sXML = "<root><map value='b'/><map key='a'/><map key='c' value='d' /><map key='c' value='e' /></root>";
    final Map <String, String> aMap2 = XMLMapHandler.readMap (new StringInputStreamProvider (sXML,
                                                                                             CCharset.CHARSET_ISO_8859_1_OBJ));
    assertNotNull (aMap2);
    assertEquals (1, aMap2.size ());
    assertEquals ("e", aMap2.get ("c"));

    sXML = "<?xml version=\"1.0\"?>";
    assertNull (XMLMapHandler.readMap (new StringInputStreamProvider (sXML, CCharset.CHARSET_ISO_8859_1_OBJ)));
  }

  @Test
  public void testWriteInvalid ()
  {
    final Map <String, String> aMap = new HashMap <String, String> ();

    try
    {
      XMLMapHandler.writeMap (aMap, (IHasOutputStream) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      XMLMapHandler.writeMap (null, new ByteArrayOutputStreamProvider ());
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      XMLMapHandler.writeMap (aMap, (OutputStream) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      XMLMapHandler.writeMap (null, new NonBlockingByteArrayOutputStream ());
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }
}
