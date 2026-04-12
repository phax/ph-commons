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
package com.helger.config.fallback;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.atomic.AtomicInteger;

import org.jspecify.annotations.NonNull;
import org.junit.Test;

import com.helger.base.numeric.BigHelper;
import com.helger.collection.commons.CommonsHashMap;
import com.helger.collection.commons.ICommonsMap;
import com.helger.config.IConfig;
import com.helger.config.source.appl.ConfigurationSourceFunction;

/**
 * Test class for class {@link ConfigSubsetWithFallback}
 *
 * @author Philip Helger
 */
public final class ConfigSubsetWithFallbackTest
{
  @NonNull
  private static IConfigWithFallback _createTestConfig ()
  {
    final ICommonsMap <String, String> aMap = new CommonsHashMap <> ();
    aMap.put ("db.host", "localhost");
    aMap.put ("db.port", "5432");
    aMap.put ("db.url", "jdbc:postgresql://localhost:5432/test");
    aMap.put ("db.connection-string", "jdbc:postgresql://localhost:5432/legacy");
    aMap.put ("db.enabled", "true");
    aMap.put ("db.readonly", "false");
    aMap.put ("db.pool.max-size", "10");
    aMap.put ("db.pool.min-idle", "2");
    aMap.put ("db.amount", "123.45");
    aMap.put ("cache.ttl", "3600");
    return new ConfigWithFallback (new ConfigurationSourceFunction (aMap::get));
  }

  @Test
  public void testGetSubConfigReturnType ()
  {
    final IConfigWithFallback aConfig = _createTestConfig ();
    final IConfig aSubConfig = aConfig.getSubConfig ("db");
    assertTrue (aSubConfig instanceof IConfigWithFallback);
    assertTrue (aSubConfig instanceof ConfigSubsetWithFallback);
  }

  @Test
  public void testBasicLookup ()
  {
    final IConfigWithFallback aConfig = _createTestConfig ();
    final IConfigWithFallback aDBConfig = aConfig.getSubConfig ("db");

    assertEquals ("localhost", aDBConfig.getAsString ("host"));
    assertEquals ("5432", aDBConfig.getAsString ("port"));
    assertNull (aDBConfig.getAsString ("ttl"));
  }

  @Test
  public void testGetConfiguredValueOrFallback ()
  {
    final IConfigWithFallback aConfig = _createTestConfig ();
    final IConfigWithFallback aDBConfig = aConfig.getSubConfig ("db");

    // Primary exists
    assertNotNull (aDBConfig.getConfiguredValueOrFallback ("url", "connection-string"));
    assertEquals ("jdbc:postgresql://localhost:5432/test",
                  aDBConfig.getConfiguredValueOrFallback ("url", "connection-string").getValue ());

    // Primary missing, fallback exists
    assertEquals ("jdbc:postgresql://localhost:5432/legacy",
                  aDBConfig.getConfiguredValueOrFallback ("url-new", "connection-string").getValue ());

    // Neither exists
    assertNull (aDBConfig.getConfiguredValueOrFallback ("foo", "bar"));
  }

  @Test
  public void testGetAsStringOrFallback ()
  {
    final IConfigWithFallback aConfig = _createTestConfig ();
    final IConfigWithFallback aDBConfig = aConfig.getSubConfig ("db");

    assertEquals ("localhost", aDBConfig.getAsStringOrFallback ("host", "hostname"));
    assertEquals ("localhost", aDBConfig.getAsStringOrFallback ("hostname", "host"));
    assertNull (aDBConfig.getAsStringOrFallback ("foo", "bar"));
  }

  @Test
  public void testGetAsBooleanOrFallback ()
  {
    final IConfigWithFallback aConfig = _createTestConfig ();
    final IConfigWithFallback aDBConfig = aConfig.getSubConfig ("db");

    assertTrue (aDBConfig.getAsBooleanOrFallback ("enabled", false, "active"));
    assertTrue (aDBConfig.getAsBooleanOrFallback ("active", false, "enabled"));
    assertFalse (aDBConfig.getAsBooleanOrFallback ("readonly", true, "read-only"));
    assertFalse (aDBConfig.getAsBooleanOrFallback ("read-only", true, "readonly"));
    assertTrue (aDBConfig.getAsBooleanOrFallback ("foo", true, "bar"));
    assertFalse (aDBConfig.getAsBooleanOrFallback ("foo", false, "bar"));
  }

  @Test
  public void testGetAsIntOrFallback ()
  {
    final IConfigWithFallback aConfig = _createTestConfig ();
    final IConfigWithFallback aDBConfig = aConfig.getSubConfig ("db");

    assertEquals (5432, aDBConfig.getAsIntOrFallback ("port", -1, "port-old"));
    assertEquals (5432, aDBConfig.getAsIntOrFallback ("port-new", -1, "port"));
    assertEquals (99, aDBConfig.getAsIntOrFallback ("foo", 99, "bar"));
  }

  @Test
  public void testGetAsLongOrFallback ()
  {
    final IConfigWithFallback aConfig = _createTestConfig ();
    final IConfigWithFallback aDBConfig = aConfig.getSubConfig ("db");

    assertEquals (5432L, aDBConfig.getAsLongOrFallback ("port", -1L, "port-old"));
    assertEquals (5432L, aDBConfig.getAsLongOrFallback ("port-new", -1L, "port"));
    assertEquals (99L, aDBConfig.getAsLongOrFallback ("foo", 99L, "bar"));
  }

  @Test
  public void testGetAsBigDecimalOrFallback ()
  {
    final IConfigWithFallback aConfig = _createTestConfig ();
    final IConfigWithFallback aDBConfig = aConfig.getSubConfig ("db");

    assertEquals (BigHelper.toBigDecimal (123.45), aDBConfig.getAsBigDecimalOrFallback ("amount", "amount-old"));
    assertEquals (BigHelper.toBigDecimal (123.45), aDBConfig.getAsBigDecimalOrFallback ("amount-new", "amount"));
    assertNull (aDBConfig.getAsBigDecimalOrFallback ("foo", "bar"));
  }

  @Test
  public void testGetAsCharArrayOrFallback ()
  {
    final IConfigWithFallback aConfig = _createTestConfig ();
    final IConfigWithFallback aDBConfig = aConfig.getSubConfig ("db");

    assertEquals ("localhost", new String (aDBConfig.getAsCharArrayOrFallback ("host", "hostname")));
    assertEquals ("localhost", new String (aDBConfig.getAsCharArrayOrFallback ("hostname", "host")));
    assertNull (aDBConfig.getAsCharArrayOrFallback ("foo", "bar"));
  }

  @Test
  public void testOutdatedNotifierFires ()
  {
    final IConfigWithFallback aConfig = _createTestConfig ();
    final AtomicInteger aNotifyCount = new AtomicInteger (0);
    ((ConfigWithFallback) aConfig).setOutdatedNotifier ( (aOldConfigSrc, sOld, sNew) -> {
      // The notifier should receive the full prefixed keys
      assertEquals ("db.host", sOld);
      assertEquals ("db.hostname", sNew);
      aNotifyCount.incrementAndGet ();
    });
    final IConfigWithFallback aDBConfig = aConfig.getSubConfig ("db");
    // Primary "hostname" doesn't exist, fallback "host" does → notifier should fire
    assertEquals ("localhost", aDBConfig.getAsStringOrFallback ("hostname", "host"));
    assertEquals (1, aNotifyCount.get ());
  }

  @Test
  public void testNested ()
  {
    final IConfigWithFallback aConfig = _createTestConfig ();
    final IConfigWithFallback aPoolConfig = aConfig.getSubConfig ("db").getSubConfig ("pool");

    assertEquals ("10", aPoolConfig.getAsString ("max-size"));
    assertEquals ("10", aPoolConfig.getAsStringOrFallback ("max-size", "maximum-size"));
    assertEquals ("10", aPoolConfig.getAsStringOrFallback ("maximum-size", "max-size"));
    assertEquals (10, aPoolConfig.getAsIntOrFallback ("max-size", -1, "maximum-size"));
  }

  @Test
  public void testContains ()
  {
    final IConfigWithFallback aConfig = _createTestConfig ();
    final IConfigWithFallback aDBConfig = aConfig.getSubConfig ("db");

    assertTrue (aDBConfig.containsConfiguredValue ("host"));
    assertFalse (aDBConfig.containsConfiguredValue ("ttl"));
  }
}
