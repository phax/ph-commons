/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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
package com.helger.xml.ls;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

import com.helger.base.io.nonblocking.NonBlockingByteArrayInputStream;
import com.helger.base.io.stream.StreamHelper;
import com.helger.commons.io.resource.ClassPathResource;
import com.helger.commons.mock.CommonsTestHelper;

/**
 * Test class for class {@link ResourceLSInput}.
 *
 * @author Philip Helger
 */
public final class ResourceLSInputTest
{
  @Test
  public void testBasic ()
  {
    final ResourceLSInput lsi = new ResourceLSInput (new ClassPathResource ("xml/buildinfo.xml"));
    assertNull (lsi.getBaseURI ());
    final InputStream aIS = lsi.getByteStream ();
    assertNotNull (aIS);
    StreamHelper.close (aIS);
    assertFalse (lsi.getCertifiedText ());
    assertNull (lsi.getCharacterStream ());
    assertNull (lsi.getEncoding ());
    assertNull (lsi.getPublicId ());
    assertNull (lsi.getStringData ());
    assertNotNull (lsi.getSystemId ());
    assertTrue (lsi.getSystemId ().endsWith ("buildinfo.xml"));

    lsi.setBaseURI ("any");
    assertEquals ("any", lsi.getBaseURI ());
    try
    {
      lsi.setByteStream (new NonBlockingByteArrayInputStream (new byte [1]));
      fail ();
    }
    catch (final UnsupportedOperationException ex)
    {}
    lsi.setCertifiedText (true);
    assertTrue (lsi.getCertifiedText ());
    try
    {
      lsi.setCharacterStream (null);
      fail ();
    }
    catch (final UnsupportedOperationException ex)
    {}
    lsi.setEncoding (StandardCharsets.ISO_8859_1.name ());
    assertEquals (StandardCharsets.ISO_8859_1.name (), lsi.getEncoding ());
    lsi.setPublicId ("pubid");
    assertEquals ("pubid", lsi.getPublicId ());
    lsi.setStringData ("str");
    assertEquals ("str", lsi.getStringData ());

    CommonsTestHelper.testToStringImplementation (lsi);
  }

  @Test
  public void testError ()
  {
    try
    {
      new ResourceLSInput (null, "sysid");
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }
}
