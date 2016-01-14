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
package com.helger.commons.xml.transform;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.InputStream;

import org.junit.Test;

import com.helger.commons.io.resource.ClassPathResource;
import com.helger.commons.io.resource.IReadableResource;
import com.helger.commons.io.stream.StreamHelper;
import com.helger.commons.io.streamprovider.MockNullInputStreamProvider;
import com.helger.commons.mock.CommonsTestHelper;

/**
 * Test class for class {@link ResourceStreamSource}.
 *
 * @author Philip Helger
 */
public final class ResourceStreamSourceTest
{
  @Test
  public void testAll ()
  {
    final IReadableResource aRes = new ClassPathResource ("xml/test1.xslt");
    assertTrue (aRes.exists ());
    final ResourceStreamSource src = new ResourceStreamSource (aRes);
    final InputStream is = src.getInputStream ();
    assertNotNull (is);
    StreamHelper.close (is);
    assertEquals (aRes.getResourceID (), src.getSystemId ());
    assertNull (src.getPublicId ());

    CommonsTestHelper.testToStringImplementation (src);

    try
    {
      new ResourceStreamSource (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      new ResourceStreamSource (null, "systid");
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      // Input stream provider
      new ResourceStreamSource (new MockNullInputStreamProvider (), "systid").getInputStream ();
      fail ();
    }
    catch (final IllegalStateException ex)
    {}
  }
}
