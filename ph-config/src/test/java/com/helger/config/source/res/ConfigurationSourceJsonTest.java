/**
 * Copyright (C) 2014-2020 Philip Helger (www.helger.com)
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
 * Test class for class {@link ConfigurationSourceJson}.
 *
 * @author Philip Helger
 */
public final class ConfigurationSourceJsonTest
{
  private static final IReadableResource f = new FileSystemResource (new File ("src/test/resources/file/test.json"));

  @Test
  public void testBasic ()
  {
    final ConfigurationSourceJson c = new ConfigurationSourceJson (f);
    assertSame (EConfigSourceType.RESOURCE, c.getSourceType ());
    assertEquals (EConfigSourceType.RESOURCE.getDefaultPriority (), c.getPriority ());
    assertTrue (c.isInitializedAndUsable ());
    assertSame (f, c.getResource ());
    assertEquals ("string", c.getConfigurationValue ("element1"));
    assertEquals ("2", c.getConfigurationValue ("element2"));
    assertEquals ("1234", c.getConfigurationValue ("element5.network.port"));
    assertNull (c.getConfigurationValue ("what a mess"));

    // Check array
    assertEquals ("4", c.getConfigurationValue ("element6.$count"));
    assertEquals ("17", c.getConfigurationValue ("element6.0"));
    assertEquals ("12", c.getConfigurationValue ("element6.3"));

    assertEquals ("3", c.getConfigurationValue ("element7.$count"));
    assertEquals ("10", c.getConfigurationValue ("element7.0.key"));
    assertNull (c.getConfigurationValue ("element7.0.value"));
    assertEquals ("3", c.getConfigurationValue ("element7.0.value.$count"));
    assertEquals ("bar", c.getConfigurationValue ("element7.0.value.2"));
    assertEquals ("plain value", c.getConfigurationValue ("element7.1"));
    assertEquals ("blub", c.getConfigurationValue ("element7.2.value.0"));
    // Check additional
    assertEquals ("value", c.getConfigurationValue ("element7.sub.key"));

    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (c, new ConfigurationSourceJson (f));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (c, new ConfigurationSourceJson (1234, f));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (c,
                                                                           new ConfigurationSourceJson (new FileSystemResource (new File ("bla"))));
  }

  @Test
  public void testExplicitCharset ()
  {
    final ConfigurationSourceJson c = new ConfigurationSourceJson (f, StandardCharsets.ISO_8859_1);
    assertSame (EConfigSourceType.RESOURCE, c.getSourceType ());
    assertEquals (EConfigSourceType.RESOURCE.getDefaultPriority (), c.getPriority ());
    assertTrue (c.isInitializedAndUsable ());
    assertEquals ("string", c.getConfigurationValue ("element1"));
    assertEquals ("2", c.getConfigurationValue ("element2"));
    assertEquals ("1234", c.getConfigurationValue ("element5.network.port"));
  }

  @Test
  public void testDifferentPriority ()
  {
    final ConfigurationSourceJson c = new ConfigurationSourceJson (2323, f);
    assertSame (EConfigSourceType.RESOURCE, c.getSourceType ());
    assertEquals (2323, c.getPriority ());
    assertTrue (c.isInitializedAndUsable ());
    assertEquals ("string", c.getConfigurationValue ("element1"));
    assertEquals ("2", c.getConfigurationValue ("element2"));
    assertEquals ("1234", c.getConfigurationValue ("element5.network.port"));
  }

  @Test
  public void testDifferentPriorityAndExplicitCharset ()
  {
    final ConfigurationSourceJson c = new ConfigurationSourceJson (2323, f, StandardCharsets.ISO_8859_1);
    assertSame (EConfigSourceType.RESOURCE, c.getSourceType ());
    assertEquals (2323, c.getPriority ());
    assertTrue (c.isInitializedAndUsable ());
    assertEquals ("string", c.getConfigurationValue ("element1"));
    assertEquals ("2", c.getConfigurationValue ("element2"));
    assertEquals ("1234", c.getConfigurationValue ("element5.network.port"));
  }

  @Test
  public void testNonExisting ()
  {
    final IReadableResource f2 = new FileSystemResource (new File ("bla"));
    assertFalse (f2.exists ());
    final ConfigurationSourceJson c = new ConfigurationSourceJson (f2);
    assertSame (EConfigSourceType.RESOURCE, c.getSourceType ());
    assertEquals (EConfigSourceType.RESOURCE.getDefaultPriority (), c.getPriority ());
    assertFalse (c.isInitializedAndUsable ());
    assertSame (f2, c.getResource ());
    assertNull (c.getConfigurationValue ("element1"));
    assertNull (c.getConfigurationValue ("element2"));
    assertNull (c.getConfigurationValue ("element5.network.port"));
  }
}
