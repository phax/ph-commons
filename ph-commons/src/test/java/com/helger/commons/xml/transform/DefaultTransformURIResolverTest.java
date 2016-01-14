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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import javax.xml.transform.Templates;
import javax.xml.transform.TransformerFactory;

import org.junit.Test;

import com.helger.commons.io.resource.ClassPathResource;
import com.helger.commons.mock.CommonsTestHelper;

/**
 * Test class for class {@link DefaultTransformURIResolver}.
 *
 * @author Philip Helger
 */
public final class DefaultTransformURIResolverTest
{
  @Test
  public void testAll ()
  {
    for (int i = 0; i < 2; ++i)
    {
      final DefaultTransformURIResolver res = new DefaultTransformURIResolver (i == 0 ? null
                                                                                      : new LoggingTransformURIResolver ());
      final TransformerFactory fac = XMLTransformerFactory.createTransformerFactory (null, res);
      assertNotNull (fac);

      // Read valid XSLT
      Templates t1 = XMLTransformerFactory.newTemplates (fac, new ClassPathResource ("xml/test1.xslt"));
      assertNotNull (t1);

      // Read valid XSLT with valid include
      t1 = XMLTransformerFactory.newTemplates (fac, new ClassPathResource ("xml/test2.xslt"));
      assertNotNull (t1);

      // Read valid XSLT with invalid include
      t1 = XMLTransformerFactory.newTemplates (fac, new ClassPathResource ("xml/test3.xslt"));
      assertNull (t1);

      CommonsTestHelper.testToStringImplementation (res);
    }
  }
}
