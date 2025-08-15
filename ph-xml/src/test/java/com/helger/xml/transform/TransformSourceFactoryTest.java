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

import java.io.IOException;
import java.io.InputStream;

import javax.xml.transform.stream.StreamSource;

import org.junit.Test;

import com.helger.commons.io.resource.ClassPathResource;
import com.helger.commons.io.resource.IReadableResource;
import com.helger.unittest.support.TestHelper;

/**
 * Test class for class {@link TransformSourceFactory}.
 *
 * @author Philip Helger
 */
public final class TransformSourceFactoryTest
{
  @Test
  public void testReadableResource () throws IOException
  {
    final IReadableResource aRes = new ClassPathResource ("xml/test1.xslt");
    assertTrue (aRes.exists ());
    final StreamSource src = TransformSourceFactory.create (aRes);
    try (final InputStream is = src.getInputStream ())
    {
      assertNotNull (is);
    }
    assertEquals (aRes.getResourceID (), src.getSystemId ());
    assertNull (src.getPublicId ());

    TestHelper.testToStringImplementation (src);
  }
}
