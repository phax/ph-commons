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
package com.helger.jaxb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.base.classloader.ClassLoaderHelper;
import com.helger.base.log.ConditionalLogger;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.CommonsHashMap;
import com.helger.collection.commons.ICommonsMap;
import com.helger.jaxb.mock.external.MockJAXBArchive;
import com.helger.jaxb.mock.external.MockJAXBIssue;

/**
 * Test class for class {@link JAXBContextCacheKey}.
 *
 * @author Philip Helger
 */
public final class JAXBContextCacheKeyTest
{
  private static final Logger LOGGER = LoggerFactory.getLogger (JAXBContextCacheKeyTest.class);
  private static final ConditionalLogger CONDLOG = new ConditionalLogger (LOGGER, true);

  @Test
  public void testCreateForPackage ()
  {
    final Package aPackage = MockJAXBArchive.class.getPackage ();
    final JAXBContextCacheKey aKey = JAXBContextCacheKey.createForPackage (aPackage);
    assertNotNull (aKey.toString ());

    assertEquals (aKey, JAXBContextCacheKey.createForPackage (aPackage));
    assertEquals (aKey, JAXBContextCacheKey.createForPackage (aPackage, ClassLoaderHelper.getDefaultClassLoader ()));
    assertEquals (aKey, JAXBContextCacheKey.createForPackages (aPackage));
    assertEquals (aKey, JAXBContextCacheKey.createForPackages (new CommonsArrayList <> (aPackage)));
    assertEquals (aKey,
                  JAXBContextCacheKey.createForPackages (new CommonsArrayList <> (aPackage),
                                                         ClassLoaderHelper.getDefaultClassLoader ()));
    assertEquals (aKey.hashCode (), JAXBContextCacheKey.createForPackage (aPackage).hashCode ());

    assertNotEquals (aKey, null);
    assertNotEquals (aKey, "any other type");
    assertNotEquals (aKey, JAXBContextCacheKey.createForPackage (JAXBContextCacheKeyTest.class.getPackage ()));
    assertNotEquals (aKey, JAXBContextCacheKey.createForClasses (MockJAXBArchive.class));

    // A class in a package with an @XmlSchema annotation is redirected to the
    // package based version
    assertEquals (aKey, JAXBContextCacheKey.createForClass (MockJAXBArchive.class));
    assertEquals (aKey, JAXBContextCacheKey.createForClass (MockJAXBArchive.class, null));

    assertNotNull (aKey.createJAXBContext (CONDLOG));
  }

  @Test
  public void testCreateForClass ()
  {
    final JAXBContextCacheKey aKey = JAXBContextCacheKey.createForClasses (MockJAXBArchive.class);
    assertNotNull (aKey.toString ());

    assertEquals (aKey, JAXBContextCacheKey.createForClasses (MockJAXBArchive.class));
    assertEquals (aKey, JAXBContextCacheKey.createForClasses (new CommonsArrayList <> (MockJAXBArchive.class)));
    assertEquals (aKey, JAXBContextCacheKey.createForClasses (new CommonsArrayList <> (MockJAXBArchive.class), null));
    assertEquals (aKey.hashCode (), JAXBContextCacheKey.createForClasses (MockJAXBArchive.class).hashCode ());

    assertNotEquals (aKey, JAXBContextCacheKey.createForClasses (MockJAXBIssue.class));
    assertNotEquals (aKey,
                     JAXBContextCacheKey.createForClasses (new CommonsArrayList <> (MockJAXBArchive.class,
                                                                                    MockJAXBIssue.class)));

    // Different properties
    final ICommonsMap <String, Object> aProps = new CommonsHashMap <> ();
    aProps.put ("any.property", "any value");
    assertNotEquals (aKey,
                     JAXBContextCacheKey.createForClasses (new CommonsArrayList <> (MockJAXBArchive.class), aProps));

    assertNotNull (aKey.createJAXBContext (CONDLOG));
  }

  @Test
  public void testCreateInvalid ()
  {
    // Not a JAXB generated package
    final JAXBContextCacheKey aKey = JAXBContextCacheKey.createForPackage (String.class.getPackage ());
    try
    {
      aKey.createJAXBContext (CONDLOG);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {
      // expected
    }

    try
    {
      JAXBContextCacheKey.createForPackages (new CommonsArrayList <> ());
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {
      // expected
    }

    try
    {
      JAXBContextCacheKey.createForClasses (new CommonsArrayList <> ());
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {
      // expected
    }
  }
}
