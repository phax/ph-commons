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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.helger.config.source.MultiConfigurationValueProvider;

/**
 * Test class for
 * {@link ConfigFactory#addProfilePropertiesSources(MultiConfigurationValueProvider, String...)}.
 *
 * @author Philip Helger
 */
public final class ConfigFactoryProfileTest
{
  static
  {
    // Ensure the default is created first
    ConfigFactory.getDefaultConfig ();
  }

  @Test
  public void testSingleProfileOverridesApplication ()
  {
    // Build a fresh default value provider (sysprop, envvar,
    // private-application, application, reference)
    final MultiConfigurationValueProvider aMCSVP = ConfigFactory.createDefaultValueProvider ();
    final int nAdded = ConfigFactory.addProfilePropertiesSources (aMCSVP, "dev");
    assertEquals (1, nAdded);

    final IConfig aConfig = Config.create (aMCSVP);

    // element0 is defined in private-application (prio 190), profile-dev
    // (prio 185), application (prio 180), and reference (prio 1).
    // private-application wins because 190 > 185.
    assertEquals ("from-private-application-properties0", aConfig.getAsString ("element0"));

    // element1 is in private-application (190) and profile-dev (185).
    // private-application still wins.
    assertEquals ("from-private-application-properties1", aConfig.getAsString ("element1"));

    // element2 is in profile-dev (185) and application (180) and reference (1).
    // Profile-dev wins because 185 > 180.
    assertEquals ("from-profile-dev2", aConfig.getAsString ("element2"));

    // element3 is only in application (180) and reference (1).
    // Profile-dev does NOT define it, so application.properties wins.
    assertEquals ("from-application-properties3", aConfig.getAsString ("element3"));

    // element4 is only in reference
    assertEquals ("from-reference-properties4", aConfig.getAsString ("element4"));

    // Profile-only key
    assertEquals ("dev-only-value", aConfig.getAsString ("profile.dev.only"));

    // Prod-only key is not present
    assertNull (aConfig.getAsString ("profile.prod.only"));
  }

  @Test
  public void testSingleProfileProd ()
  {
    final MultiConfigurationValueProvider aMCSVP = ConfigFactory.createDefaultValueProvider ();
    final int nAdded = ConfigFactory.addProfilePropertiesSources (aMCSVP, "prod");
    assertEquals (1, nAdded);

    final IConfig aConfig = Config.create (aMCSVP);

    // private-application still wins for element0 and element1
    assertEquals ("from-private-application-properties0", aConfig.getAsString ("element0"));
    assertEquals ("from-private-application-properties1", aConfig.getAsString ("element1"));

    // Profile-prod overrides application for element2 and element3
    assertEquals ("from-profile-prod2", aConfig.getAsString ("element2"));
    assertEquals ("from-profile-prod3", aConfig.getAsString ("element3"));

    // reference-only
    assertEquals ("from-reference-properties4", aConfig.getAsString ("element4"));

    // Profile-only keys
    assertEquals ("prod-only-value", aConfig.getAsString ("profile.prod.only"));
    assertNull (aConfig.getAsString ("profile.dev.only"));
  }

  @Test
  public void testMultipleProfilesBothLoaded ()
  {
    final MultiConfigurationValueProvider aMCSVP = ConfigFactory.createDefaultValueProvider ();
    // Both profiles have the same priority (185), so both are loaded.
    // When two sources share the same priority, the one added last wins
    // because it is inserted later at the same priority level.
    final int nAdded = ConfigFactory.addProfilePropertiesSources (aMCSVP, "dev", "prod");
    assertEquals (2, nAdded);

    final IConfig aConfig = Config.create (aMCSVP);

    // private-application still wins for element0 and element1
    assertEquals ("from-private-application-properties0", aConfig.getAsString ("element0"));
    assertEquals ("from-private-application-properties1", aConfig.getAsString ("element1"));

    // Both dev and prod are at prio 185 — both override application (180).
    // element2 is defined in both profiles. Because MultiConfigurationValueProvider
    // uses a stable sort, the first-added profile (dev) comes first and wins.
    assertEquals ("from-profile-dev2", aConfig.getAsString ("element2"));

    // element3 is only in prod profile and application.properties.
    // Prod (185) overrides application (180).
    assertEquals ("from-profile-prod3", aConfig.getAsString ("element3"));

    // Both profile-only keys are available
    assertEquals ("dev-only-value", aConfig.getAsString ("profile.dev.only"));
    assertEquals ("prod-only-value", aConfig.getAsString ("profile.prod.only"));
  }

  @Test
  public void testNonExistentProfileReturnsZero ()
  {
    final MultiConfigurationValueProvider aMCSVP = ConfigFactory.createDefaultValueProvider ();
    final int nAdded = ConfigFactory.addProfilePropertiesSources (aMCSVP, "nonexistent");
    assertEquals (0, nAdded);

    // Config behaves exactly like the default — no profile overrides
    final IConfig aConfig = Config.create (aMCSVP);
    assertEquals ("from-private-application-properties0", aConfig.getAsString ("element0"));
    assertEquals ("from-application-properties2", aConfig.getAsString ("element2"));
  }

  @Test
  public void testMixOfExistingAndNonExistentProfiles ()
  {
    final MultiConfigurationValueProvider aMCSVP = ConfigFactory.createDefaultValueProvider ();
    final int nAdded = ConfigFactory.addProfilePropertiesSources (aMCSVP, "nonexistent", "dev", "alsoMissing");
    // Only "dev" was found
    assertEquals (1, nAdded);

    final IConfig aConfig = Config.create (aMCSVP);
    assertEquals ("from-profile-dev2", aConfig.getAsString ("element2"));
    assertEquals ("dev-only-value", aConfig.getAsString ("profile.dev.only"));
  }

  @Test
  public void testEmptyProfileArrayAddsNothing ()
  {
    final MultiConfigurationValueProvider aMCSVP = ConfigFactory.createDefaultValueProvider ();
    final int nAdded = ConfigFactory.addProfilePropertiesSources (aMCSVP);
    assertEquals (0, nAdded);
  }

  @Test
  public void testBlankProfileNamesAreSkipped ()
  {
    final MultiConfigurationValueProvider aMCSVP = ConfigFactory.createDefaultValueProvider ();
    final int nAdded = ConfigFactory.addProfilePropertiesSources (aMCSVP, "", null, "dev");
    assertEquals (1, nAdded);

    final IConfig aConfig = Config.create (aMCSVP);
    assertEquals ("from-profile-dev2", aConfig.getAsString ("element2"));
  }

  @Test
  public void testProfilePriorityConstant ()
  {
    // Verify the priority sits between private-application and application
    final int nProfilePrio = ConfigFactory.APPLICATION_PROFILE_PROPERTIES_PRIORITY;
    final int nPrivatePrio = ConfigFactory.PRIVATE_APPLICATION_PROPERTIES_PRIORITY;
    final int nAppPrio = ConfigFactory.APPLICATION_PROPERTIES_PRIORITY;

    // Profile must be lower priority than private-application
    assertTrue ("Profile priority (" +
                nProfilePrio +
                ") must be less than private-application priority (" +
                nPrivatePrio +
                ")",
                nProfilePrio < nPrivatePrio);

    // Profile must be higher priority than application
    assertTrue ("Profile priority (" + nProfilePrio + ") must be greater than application priority (" + nAppPrio + ")",
                nProfilePrio > nAppPrio);
  }

  @Test
  public void testProfileDoesNotAffectExistingValues ()
  {
    // Verify that keys NOT defined in any profile are still resolved from
    // their original source
    final MultiConfigurationValueProvider aMCSVP = ConfigFactory.createDefaultValueProvider ();
    ConfigFactory.addProfilePropertiesSources (aMCSVP, "dev");

    final IConfig aConfig = Config.create (aMCSVP);

    // These come from application.properties — untouched by dev profile
    assertNotNull (aConfig.getAsString ("x_int"));
    assertEquals (123456, aConfig.getAsInt ("x_int"));
    assertNotNull (aConfig.getAsString ("element.boolean.t"));
    assertTrue (aConfig.getAsBoolean ("element.boolean.t", false));
  }
}
