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
package com.helger.commons.xml.sax;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.InputStream;

import org.junit.Test;

import com.helger.commons.io.IHasInputStream;
import com.helger.commons.io.resource.ClassPathResource;
import com.helger.commons.io.resource.IReadableResource;
import com.helger.commons.io.stream.StreamHelper;
import com.helger.commons.io.streamprovider.MockNullInputStreamProvider;
import com.helger.commons.mock.CommonsTestHelper;

/**
 * Test class for class {@link CachingSAXInputSource}.
 *
 * @author Philip Helger
 */
public final class CachingSAXInputSourceTest
{
  @Test
  public void testAll ()
  {
    final IReadableResource aRes = new ClassPathResource ("xml/list.xml");

    CachingSAXInputSource is = new CachingSAXInputSource (aRes);
    assertEquals (aRes.getResourceID (), is.getSystemId ());
    assertNotNull (StreamHelper.getAllBytes (is.getByteStream ()));

    is = new CachingSAXInputSource ((IHasInputStream) aRes);
    assertNull (is.getSystemId ());
    assertNotNull (StreamHelper.getAllBytes (is.getByteStream ()));

    is = new CachingSAXInputSource (aRes, "sysid");
    assertEquals ("sysid", is.getSystemId ());
    assertNotNull (StreamHelper.getAllBytes (is.getByteStream ()));

    is = new CachingSAXInputSource (aRes.getInputStream ());
    assertNull (is.getSystemId ());
    assertNotNull (StreamHelper.getAllBytes (is.getByteStream ()));

    is = new CachingSAXInputSource (aRes.getInputStream (), "sysid");
    assertEquals ("sysid", is.getSystemId ());
    assertNotNull (StreamHelper.getAllBytes (is.getByteStream ()));

    CommonsTestHelper.testToStringImplementation (is);

    try
    {
      new CachingSAXInputSource ((IReadableResource) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      new CachingSAXInputSource ((IHasInputStream) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      new CachingSAXInputSource ((IHasInputStream) null, "sysid");
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      new CachingSAXInputSource ((InputStream) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      new CachingSAXInputSource ((InputStream) null, "sysid");
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      new CachingSAXInputSource (new MockNullInputStreamProvider (), "sysid").getByteStream ();
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }
}
