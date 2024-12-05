/*
 * Copyright (C) 2014-2024 Philip Helger (www.helger.com)
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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.helger.commons.collection.impl.CommonsHashMap;
import com.helger.commons.collection.impl.ICommonsMap;
import com.helger.commons.io.resource.FileSystemResource;
import com.helger.commons.io.stream.NonBlockingStringReader;
import com.helger.commons.lang.NonBlockingProperties;
import com.helger.commons.lang.PropertiesHelper;
import com.helger.commons.mutable.MutableInt;
import com.helger.config.source.MultiConfigurationValueProvider;
import com.helger.config.source.appl.ConfigurationSourceFunction;
import com.helger.config.source.res.ConfigurationSourceJson;
import com.helger.config.value.IConfigurationValueProviderWithPriorityCallback;

/**
 * Test class for class {@link Config}
 *
 * @author Philip Helger
 */
public final class ConfigTest
{
  private static final ConfigurationSourceJson CS1 = new ConfigurationSourceJson (17,
                                                                                  new FileSystemResource ("src/test/resources/application.json"));
  private static final ConfigurationSourceJson CS2 = new ConfigurationSourceJson (41,
                                                                                  new FileSystemResource ("src/test/resources/private-application.json"));

  @Test
  public void testForEachOneConfigSource ()
  {
    final Config aConfig = new Config (CS1);
    assertEquals ("from-application-json0", aConfig.getAsString ("element0"));
    assertNull (aConfig.getAsString ("element5"));
    assertTrue (aConfig.isReplaceVariables ());

    final MutableInt aCounter = new MutableInt (0);
    final IConfigurationValueProviderWithPriorityCallback aCallback = (aCVP, nPriority) -> {
      final int n = aCounter.getAndInc ();
      switch (n)
      {
        case 0:
          assertEquals (17, nPriority);
          break;
        default:
          throw new IllegalStateException ("Unsupported index");
      }
    };
    aConfig.forEachConfigurationValueProvider (aCallback);
  }

  @Test
  public void testForEachMultiConfigDefaultPriority ()
  {
    final IConfig aConfig = new Config (new MultiConfigurationValueProvider (CS1, CS2));
    assertEquals ("from-private-application-json0", aConfig.getAsString ("element0"));
    assertNull (aConfig.getAsString ("element5"));

    final MutableInt aCounter = new MutableInt (0);
    final IConfigurationValueProviderWithPriorityCallback aCallback = (aCVP, nPriority) -> {
      final int n = aCounter.getAndInc ();
      switch (n)
      {
        case 0:
          assertEquals (41, nPriority);
          break;
        case 1:
          assertEquals (17, nPriority);
          break;
        default:
          throw new IllegalStateException ("Unsupported index");
      }
    };
    aConfig.forEachConfigurationValueProvider (aCallback);
  }

  @Test
  public void testForEachMultiConfigCustomPriority ()
  {
    final MultiConfigurationValueProvider aMCVP = new MultiConfigurationValueProvider ();
    aMCVP.addConfigurationSource (CS1, 18);
    aMCVP.addConfigurationSource (CS2, 42);
    final IConfig aConfig = new Config (aMCVP);
    assertEquals ("from-private-application-json0", aConfig.getAsString ("element0"));
    assertNull (aConfig.getAsString ("element5"));

    final MutableInt aCounter = new MutableInt (0);
    final IConfigurationValueProviderWithPriorityCallback aCallback = (aCVP, nPriority) -> {
      final int n = aCounter.getAndInc ();
      switch (n)
      {
        case 0:
          assertEquals (42, nPriority);
          break;
        case 1:
          assertEquals (18, nPriority);
          break;
        default:
          throw new IllegalStateException ("Unsupported index");
      }
    };
    aConfig.forEachConfigurationValueProvider (aCallback);
  }

  @Test
  public void testWithDisabledVariableReplacement ()
  {
    final ICommonsMap <String, String> aMap = new CommonsHashMap <> ();
    aMap.put ("key1", "value");
    aMap.put ("key2", "${key1}");
    aMap.put ("more.complex.nested.key", "complex ${key1} ${key2}!");
    aMap.put ("key3", "not so ${more.complex.nested.key}");
    final Config aConfig = new Config (new ConfigurationSourceFunction (aMap::get)).setReplaceVariables (false);
    assertEquals ("value", aConfig.getAsString ("key1"));
    assertEquals ("${key1}", aConfig.getAsString ("key2"));
    assertEquals ("complex ${key1} ${key2}!", aConfig.getAsString ("more.complex.nested.key"));
    assertEquals ("not so ${more.complex.nested.key}", aConfig.getAsString ("key3"));
  }

  @Test
  public void testWithVariableReplacement ()
  {
    final ICommonsMap <String, String> aMap = new CommonsHashMap <> ();
    aMap.put ("key1", "value");
    aMap.put ("key2", "${key1}");
    aMap.put ("more.complex.nested.key", "complex ${key1} ${key2}!");
    aMap.put ("key3", "not so ${more.complex.nested.key}");
    aMap.put ("key4", "abc ${dummy.foo} xyz");
    final Config aConfig = new Config (new ConfigurationSourceFunction (aMap::get)).setReplaceVariables (true);
    assertEquals ("value", aConfig.getAsString ("key1"));
    assertEquals ("value", aConfig.getAsString ("key2"));
    assertEquals ("complex value value!", aConfig.getAsString ("more.complex.nested.key"));
    assertEquals ("not so complex value value!", aConfig.getAsString ("key3"));
    assertEquals ("abc unresolved-var(dummy.foo) xyz", aConfig.getAsString ("key4"));

    aConfig.setUnresolvedVariableProvider (x -> "bla<" + x + ">");
    assertEquals ("abc bla<dummy.foo> xyz", aConfig.getAsString ("key4"));
  }

  @Test
  public void testWithVariableReplacementCyclicDep ()
  {
    final ICommonsMap <String, String> aMap = new CommonsHashMap <> ();
    aMap.put ("key1", "1 and ${key2}");
    aMap.put ("key2", "2 and ${key1}");
    final Config aConfig = new Config (new ConfigurationSourceFunction (aMap::get)).setReplaceVariables (true);
    assertEquals ("1 and ${key2}", aConfig.getAsString ("key1"));
    assertEquals ("2 and ${key1}", aConfig.getAsString ("key2"));
  }

  @Test
  public void testWithVariableReplacementCyclicDepV2 ()
  {
    final ICommonsMap <String, String> aMap = new CommonsHashMap <> ();
    aMap.put ("key1", "1 and ${key3}");
    aMap.put ("key2", "2 and ${key1}");
    aMap.put ("key3", "3 and ${key2}");
    final Config aConfig = new Config (new ConfigurationSourceFunction (aMap::get)).setReplaceVariables (true);
    assertEquals ("1 and ${key3}", aConfig.getAsString ("key1"));
    assertEquals ("2 and ${key1}", aConfig.getAsString ("key2"));
    assertEquals ("3 and ${key2}", aConfig.getAsString ("key3"));
  }

  @Test
  public void testWithVariableReplacementSelfRef ()
  {
    final ICommonsMap <String, String> aMap = new CommonsHashMap <> ();
    aMap.put ("key1", "Prefix ${key1}");
    final Config aConfig = new Config (new ConfigurationSourceFunction (aMap::get)).setReplaceVariables (true);
    assertEquals ("Prefix ${key1}", aConfig.getAsString ("key1"));
  }

  @Test
  public void testWithVariableDefaults ()
  {
    final ICommonsMap <String, String> aMap = new CommonsHashMap <> ();
    aMap.put ("key1", "Prefix ${key2:default}");
    final Config aConfig = new Config (new ConfigurationSourceFunction (aMap::get)).setReplaceVariables (true);
    assertEquals ("Prefix default", aConfig.getAsString ("key1"));

    // Now define it
    aMap.put ("key2", "xyz");
    assertEquals ("Prefix xyz", aConfig.getAsString ("key1"));
  }

  @Test
  public void testWithVariableEmptyDefault ()
  {
    final ICommonsMap <String, String> aMap = new CommonsHashMap <> ();
    aMap.put ("key1", "Prefix ${key2:}");
    final Config aConfig = new Config (new ConfigurationSourceFunction (aMap::get)).setReplaceVariables (true);
    assertEquals ("Prefix ", aConfig.getAsString ("key1"));

    // Now define it
    aMap.put ("key2", "xyz");
    assertEquals ("Prefix xyz", aConfig.getAsString ("key1"));
  }

  @Test
  public void testWithVariableNestedVariableDefault ()
  {
    final ICommonsMap <String, String> aMap = new CommonsHashMap <> ();
    aMap.put ("key1", "Prefix ${key2:${key3}}");
    aMap.put ("key3", "fallback");
    final Config aConfig = new Config (new ConfigurationSourceFunction (aMap::get)).setReplaceVariables (true);
    assertEquals ("Prefix fallback", aConfig.getAsString ("key1"));

    // Now define it
    aMap.put ("key2", "xyz");
    assertEquals ("Prefix xyz", aConfig.getAsString ("key1"));
  }

  @Test
  public void testWithVariableMultipleNestedVariableDefault ()
  {
    final ICommonsMap <String, String> aMap = new CommonsHashMap <> ();
    aMap.put ("key1", "Prefix ${key2:${key3:${key4:${key5}}}}");
    aMap.put ("key5", "fallback");
    final Config aConfig = new Config (new ConfigurationSourceFunction (aMap::get)).setReplaceVariables (true);
    assertEquals ("Prefix fallback", aConfig.getAsString ("key1"));

    // Now define it
    aMap.put ("key4", "abc");
    assertEquals ("Prefix abc", aConfig.getAsString ("key1"));

    // Now define it
    aMap.put ("key3", "qwe");
    assertEquals ("Prefix qwe", aConfig.getAsString ("key1"));

    // Now define it
    aMap.put ("key2", "xyz");
    assertEquals ("Prefix xyz", aConfig.getAsString ("key1"));
  }

  @Test
  public void testWithVariableBrokenDefault ()
  {
    final ICommonsMap <String, String> aMap = new CommonsHashMap <> ();
    aMap.put ("key1", "Prefix ${key2:$$$}");
    final Config aConfig = new Config (new ConfigurationSourceFunction (aMap::get)).setReplaceVariables (true);
    assertEquals ("Prefix $$$", aConfig.getAsString ("key1"));

    // Broken definition
    aMap.put ("key1", "Prefix ${key2:${}");
    assertEquals ("Prefix ${key2:${}", aConfig.getAsString ("key1"));

    // double masked brackets are kept
    aMap.put ("key1", "Prefix ${key2:\\\\{\\\\}}");
    assertEquals ("Prefix \\{\\}", aConfig.getAsString ("key1"));

    // Unmasked brackets work as well
    aMap.put ("key1", "Prefix ${key2:{}}");
    assertEquals ("Prefix {}", aConfig.getAsString ("key1"));

    // Masked nested variable
    aMap.put ("key1", "Prefix ${key2:\\\\${literal}}");
    assertEquals ("Prefix \\${literal}", aConfig.getAsString ("key1"));

    // Masked nested variable
    aMap.put ("key1", "Prefix ${key2:\\\\${literal:\\\\whocares}}");
    assertEquals ("Prefix \\${literal:\\whocares}", aConfig.getAsString ("key1"));
  }

  @Test
  public void testIssueSmp234 ()
  {
    final String s = "# Global flags for initializer\r\n" +
                     "# For production debug should be false and production should be true\r\n" +
                     "global.debug = true\r\n" +
                     "global.production = false\r\n" +
                     "global.debugjaxws = false\r\n" +
                     "\r\n" +
                     "## Application Configuration\r\n" +
                     "# Type (JKS or PKCS12)\r\n" +
                     "pdclient.keystore.type = ${smp.keystore.type}\r\n" +
                     "# The path should be absolute for docker configuration\r\n" +
                     "# Put the .p12 file in the same directory as this file (depends on the docker config)\r\n" +
                     "pdclient.keystore.path = ${smp.keystore.path}\r\n" +
                     "pdclient.keystore.key.alias = ${smp.keystore.key.alias}\r\n" +
                     "\r\n" +
                     "#DO NOT COMMIT THE REAL PASSWORD!\r\n" +
                     "pdclient.keystore.password = ${smp.keystore.password}\r\n" +
                     "pdclient.keystore.key.password = ${smp.keystore.key.password}\r\n" +
                     "\r\n" +
                     "## SMP Configuration\r\n" +
                     "# The backend to be used. Can either be \"sql\" or \"xml\". Any other value will result in a startup error\r\n" +
                     "smp.backend = xml\r\n" +
                     "\r\n" +
                     "## Keystore data\r\n" +
                     "\r\n" +
                     "# Type (JKS or PKCS12)\r\n" +
                     "smp.keystore.type = pkcs12\r\n" +
                     "# The path should be absolute for docker configuration\r\n" +
                     "# Put the .p12 file in the same directory as this file (depends on the docker config)\r\n" +
                     "smp.keystore.path = /config/smp-test-complete.p12\r\n" +
                     "smp.keystore.key.alias = smp-test\r\n" +
                     "#DO NOT COMMIT THE REAL PASSWORD!\r\n" +
                     "smp.keystore.password = password\r\n" +
                     "smp.keystore.key.password = password\r\n" +
                     "\r\n" +
                     "# This default truststore handles 2010 and 2018 PKIs\r\n" +
                     "#smp.truststore.type     = jks\r\n" +
                     "#smp.truststore.path     = truststore/complete-truststore.jks\r\n" +
                     "#smp.truststore.password = peppol\r\n" +
                     "\r\n" +
                     "# Force all paths (links) to be \"/\" instead of the context path\r\n" +
                     "# This is helpful if the web application runs in a context like \"/smp\" but is proxied to a root path\r\n" +
                     "smp.forceroot = true\r\n" +
                     "\r\n" +
                     "# If this property is specified, it will overwrite the automatically generated URL\r\n" +
                     "# for all cases where absolute URLs are necessary\r\n" +
                     "# This might be helpful when running on a proxied Tomcat behind a web server\r\n" +
                     "smp.publicurl = http://smp-test.payreq.com/\r\n" +
                     "\r\n" +
                     "## Write to SML? true or false\r\n" +
                     "sml.enabled=false\r\n" +
                     "# Is an SML needed in the current scenario - show warnings if true\r\n" +
                     "sml.required=true\r\n" +
                     "# The SMP ID also used in the SML!\r\n" +
                     "sml.smpid=PAU000363\r\n" +
                     "\r\n" +
                     "# SML connection timeout milliseconds\r\n" +
                     "#sml.connection.timeout.ms = 5000\r\n" +
                     "\r\n" +
                     "# SML request timeout milliseconds\r\n" +
                     "#sml.request.timeout.ms = 20000\r\n" +
                     "\r\n" +
                     "# Enable PEPPOL Directory integration?\r\n" +
                     "#todo: change to true in prod\r\n" +
                     "smp.directory.integration.enabled=true\r\n" +
                     "smp.directory.hostname=https://test-directory.peppol.eu\r\n" +
                     "\r\n" +
                     "# Use PEPPOL identifiers (with all constraints) or simple, unchecked identifiers?\r\n" +
                     "# Possible values are \"peppol\", \"simple\" and \"bdxr\"\r\n" +
                     "smp.identifiertype=peppol\r\n" +
                     "\r\n" +
                     "smp.rest.type=peppol\r\n" +
                     "smp.rest.log.exceptions=true\r\n" +
                     "\r\n" +
                     "# Central directory where the data should be stored.\r\n" +
                     "# This should be absolute in production.\r\n" +
                     "webapp.datapath = /home/git/conf\r\n" +
                     "\r\n" +
                     "# Should all files of the application checked for readability?\r\n" +
                     "# This should only be set to true when datapath is a relative directory inside a production version\r\n" +
                     "webapp.checkfileaccess = false\r\n" +
                     "\r\n" +
                     "# Is it a test version? E.g. a separate header is shown\r\n" +
                     "webapp.testversion = true\r\n" +
                     "\r\n" +
                     "# Use slow, but fancy dynamic table on the start page?\r\n" +
                     "webapp.startpage.dynamictable = false\r\n" +
                     "\r\n" +
                     "# Participant list is enabled by default\r\n" +
                     "webapp.startpage.participants.none = false\r\n" +
                     "\r\n" +
                     "# Don't show content of extensions by default on start page\r\n" +
                     "webapp.startpage.extensions.show = false\r\n" +
                     "\r\n" +
                     "# The name of the Directory implementation\r\n" +
                     "webapp.directory.name = PEPPOL Directory\r\n" +
                     "\r\n" +
                     "# Don't show content of extensions by default in service groups\r\n" +
                     "webapp.servicegroups.extensions.show = false\r\n";

    try (final NonBlockingStringReader aReader = new NonBlockingStringReader (s))
    {
      final NonBlockingProperties aProps = PropertiesHelper.loadProperties (aReader);
      final Config aConfig = new Config (new ConfigurationSourceFunction (aProps::get)).setReplaceVariables (true);
      assertEquals ("false", aConfig.getAsString ("webapp.startpage.participants.none"));
    }
  }
}
