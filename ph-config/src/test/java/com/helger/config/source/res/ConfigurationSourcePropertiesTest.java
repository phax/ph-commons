/*
 * Copyright (C) 2014-2023 Philip Helger (www.helger.com)
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
package com.helger.config.source.res;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

import com.helger.commons.io.resource.FileSystemResource;
import com.helger.commons.io.resource.IReadableResource;
import com.helger.commons.mock.CommonsTestHelper;
import com.helger.config.source.EConfigSourceType;

/**
 * Test class for class {@link ConfigurationSourceProperties}.
 *
 * @author Philip Helger
 */
public final class ConfigurationSourcePropertiesTest
{
  private static final IReadableResource RES = new FileSystemResource (new File ("src/test/resources/file/test.properties"));

  @Test
  public void testBasic ()
  {
    final ConfigurationSourceProperties c = new ConfigurationSourceProperties (RES);
    assertSame (EConfigSourceType.RESOURCE, c.getSourceType ());
    assertEquals (EConfigSourceType.RESOURCE.getDefaultPriority (), c.getPriority ());
    assertTrue (c.isInitializedAndUsable ());
    assertSame (RES, c.getResource ());
    assertEquals ("string", c.getConfigurationValue ("element1").getValue ());
    assertEquals ("2", c.getConfigurationValue ("element2").getValue ());
    assertNull (c.getConfigurationValue ("what a mess"));
    assertEquals ("true", c.getConfigurationValue ("element99").getValue ());

    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (c, new ConfigurationSourceProperties (RES));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (c,
                                                                           new ConfigurationSourceProperties (1234,
                                                                                                              RES));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (c,
                                                                           new ConfigurationSourceProperties (new FileSystemResource (new File ("bla"))));
  }

  @Test
  public void testExplicitCharset ()
  {
    final ConfigurationSourceProperties c = new ConfigurationSourceProperties (RES, StandardCharsets.ISO_8859_1);
    assertSame (EConfigSourceType.RESOURCE, c.getSourceType ());
    assertEquals (EConfigSourceType.RESOURCE.getDefaultPriority (), c.getPriority ());
    assertTrue (c.isInitializedAndUsable ());
    assertEquals ("string", c.getConfigurationValue ("element1").getValue ());
    assertEquals ("2", c.getConfigurationValue ("element2").getValue ());
  }

  @Test
  public void testDifferentPriority ()
  {
    final ConfigurationSourceProperties c = new ConfigurationSourceProperties (2323, RES);
    assertSame (EConfigSourceType.RESOURCE, c.getSourceType ());
    assertEquals (2323, c.getPriority ());
    assertTrue (c.isInitializedAndUsable ());
    assertEquals ("string", c.getConfigurationValue ("element1").getValue ());
    assertEquals ("2", c.getConfigurationValue ("element2").getValue ());
  }

  @Test
  public void testDifferentPriorityAndExplicitCharset ()
  {
    final ConfigurationSourceProperties c = new ConfigurationSourceProperties (2323, RES, StandardCharsets.ISO_8859_1);
    assertSame (EConfigSourceType.RESOURCE, c.getSourceType ());
    assertEquals (2323, c.getPriority ());
    assertTrue (c.isInitializedAndUsable ());
    assertEquals ("string", c.getConfigurationValue ("element1").getValue ());
    assertEquals ("2", c.getConfigurationValue ("element2").getValue ());
  }

  @Test
  public void testNonExisting ()
  {
    final IReadableResource f2 = new FileSystemResource (new File ("bla"));
    assertFalse (f2.exists ());
    final ConfigurationSourceProperties c = new ConfigurationSourceProperties (f2);
    assertSame (EConfigSourceType.RESOURCE, c.getSourceType ());
    assertEquals (EConfigSourceType.RESOURCE.getDefaultPriority (), c.getPriority ());
    assertFalse (c.isInitializedAndUsable ());
    assertSame (f2, c.getResource ());
    assertNull (c.getConfigurationValue ("element1"));
  }
}
