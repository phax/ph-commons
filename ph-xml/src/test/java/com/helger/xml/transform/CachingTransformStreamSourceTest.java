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
package com.helger.xml.transform;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.InputStream;

import org.junit.Test;

import com.helger.base.io.iface.IHasInputStream;
import com.helger.base.io.stream.StreamHelper;
import com.helger.io.resource.ClassPathResource;
import com.helger.io.resource.IReadableResource;
import com.helger.unittest.support.TestHelper;
import com.helger.xml.mock.MockNullInputStreamProvider;

/**
 * Test class for class {@link CachingTransformStreamSource}.
 *
 * @author Philip Helger
 */
public final class CachingTransformStreamSourceTest
{
  @Test
  public void testBasic ()
  {
    final IReadableResource aRes = new ClassPathResource ("xml/test1.xslt");
    assertTrue (aRes.exists ());
    CachingTransformStreamSource src = new CachingTransformStreamSource (aRes);
    InputStream is = src.getInputStream ();
    assertNotNull (is);
    StreamHelper.close (is);
    is = src.getInputStream ();
    assertNotNull (is);
    StreamHelper.close (is);
    assertEquals (aRes.getResourceID (), src.getSystemId ());
    assertNull (src.getPublicId ());

    src = new CachingTransformStreamSource ((IHasInputStream) aRes);
    is = src.getInputStream ();
    assertNotNull (is);
    StreamHelper.close (is);
    is = src.getInputStream ();
    assertNotNull (is);
    StreamHelper.close (is);
    assertNull (src.getSystemId ());
    assertNull (src.getPublicId ());

    src = new CachingTransformStreamSource (aRes.getInputStream ());
    is = src.getInputStream ();
    assertNotNull (is);
    StreamHelper.close (is);
    is = src.getInputStream ();
    assertNotNull (is);
    StreamHelper.close (is);
    assertNull (src.getSystemId ());
    assertNull (src.getPublicId ());

    TestHelper.testToStringImplementation (src);
  }

  @Test
  public void testCreationError ()
  {
    try
    {
      new CachingTransformStreamSource ((IReadableResource) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    new CachingTransformStreamSource ((InputStream) null);
    try
    {
      new CachingTransformStreamSource ((IHasInputStream) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    // Input stream provider
    new CachingTransformStreamSource (new MockNullInputStreamProvider (), "systid").getInputStream ();
  }
}
