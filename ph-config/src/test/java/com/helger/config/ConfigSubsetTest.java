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
package com.helger.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.jspecify.annotations.NonNull;
import org.junit.Test;

import com.helger.collection.commons.CommonsHashMap;
import com.helger.collection.commons.ICommonsMap;
import com.helger.config.source.appl.ConfigurationSourceFunction;

/**
 * Test class for class {@link ConfigSubset}
 *
 * @author Philip Helger
 */
public final class ConfigSubsetTest
{
  @NonNull
  private static IConfig _createTestConfig ()
  {
    final ICommonsMap <String, String> aMap = new CommonsHashMap <> ();
    aMap.put ("db.host", "localhost");
    aMap.put ("db.port", "5432");
    aMap.put ("db.pool.max-size", "10");
    aMap.put ("db.pool.min-idle", "2");
    aMap.put ("cache.ttl", "3600");
    aMap.put ("app.name", "test");
    return new Config (new ConfigurationSourceFunction (aMap::get));
  }

  @Test
  public void testBasicLookup ()
  {
    final IConfig aConfig = _createTestConfig ();
    final IConfig aDBConfig = aConfig.getSubConfig ("db");

    assertEquals ("localhost", aDBConfig.getAsString ("host"));
    assertEquals ("5432", aDBConfig.getAsString ("port"));
    assertNull (aDBConfig.getAsString ("ttl"));
    assertNull (aDBConfig.getAsString ("name"));
  }

  @Test
  public void testContains ()
  {
    final IConfig aConfig = _createTestConfig ();
    final IConfig aDBConfig = aConfig.getSubConfig ("db");

    assertTrue (aDBConfig.containsConfiguredValue ("host"));
    assertTrue (aDBConfig.containsConfiguredValue ("port"));
    assertFalse (aDBConfig.containsConfiguredValue ("ttl"));
    assertFalse (aDBConfig.containsConfiguredValue (null));
  }

  @Test
  public void testGetConfiguredValue ()
  {
    final IConfig aConfig = _createTestConfig ();
    final IConfig aDBConfig = aConfig.getSubConfig ("db");

    assertNotNull (aDBConfig.getConfiguredValue ("host"));
    assertEquals ("localhost", aDBConfig.getConfiguredValue ("host").getValue ());
    assertNull (aDBConfig.getConfiguredValue ("nonexistent"));
    assertNull (aDBConfig.getConfiguredValue (null));
  }

  @Test
  public void testTypedAccess ()
  {
    final IConfig aConfig = _createTestConfig ();
    final IConfig aDBConfig = aConfig.getSubConfig ("db");

    assertEquals (5432, aDBConfig.getAsInt ("port", -1));
    assertEquals (-1, aDBConfig.getAsInt ("nonexistent", -1));
  }

  @Test
  public void testNested ()
  {
    final IConfig aConfig = _createTestConfig ();
    final IConfig aPoolConfig = aConfig.getSubConfig ("db").getSubConfig ("pool");

    assertEquals ("10", aPoolConfig.getAsString ("max-size"));
    assertEquals ("2", aPoolConfig.getAsString ("min-idle"));
    assertNull (aPoolConfig.getAsString ("host"));
  }

  @Test
  public void testPrefixWithTrailingDot ()
  {
    final IConfig aConfig = _createTestConfig ();
    final IConfig aDBConfig = aConfig.getSubConfig ("db.");

    assertEquals ("localhost", aDBConfig.getAsString ("host"));
    assertEquals ("5432", aDBConfig.getAsString ("port"));
  }

  @Test
  public void testPrefixWithoutTrailingDot ()
  {
    final IConfig aConfig = _createTestConfig ();
    final IConfig aDBConfig = aConfig.getSubConfig ("db");

    assertEquals ("localhost", aDBConfig.getAsString ("host"));
    assertEquals ("5432", aDBConfig.getAsString ("port"));
  }

  @Test
  public void testGetPrefix ()
  {
    final IConfig aConfig = _createTestConfig ();
    final ConfigSubset aSubset = new ConfigSubset (aConfig, "db");
    assertEquals ("db.", aSubset.getPrefix ());

    final ConfigSubset aSubsetWithDot = new ConfigSubset (aConfig, "db.");
    assertEquals ("db.", aSubsetWithDot.getPrefix ());
  }

  @Test
  public void testGetParent ()
  {
    final IConfig aConfig = _createTestConfig ();
    final ConfigSubset aSubset = new ConfigSubset (aConfig, "db");
    assertSame (aSubset.getParent (), aConfig);
  }

  @Test
  public void testVariableSubstitution ()
  {
    final ICommonsMap <String, String> aMap = new CommonsHashMap <> ();
    aMap.put ("base.dir", "/opt/app");
    aMap.put ("base.data", "${base.dir}/data");
    final IConfig aConfig = new Config (new ConfigurationSourceFunction (aMap::get)).setReplaceVariables (true);
    final IConfig aBaseConfig = aConfig.getSubConfig ("base");

    // Variable substitution resolves via the parent, so ${base.dir} resolves globally
    assertEquals ("/opt/app/data", aBaseConfig.getAsString ("data"));
  }

  @Test
  public void testToString ()
  {
    final IConfig aConfig = _createTestConfig ();
    final ConfigSubset aSubset = new ConfigSubset (aConfig, "db");
    assertNotNull (aSubset.toString ());
  }
}
