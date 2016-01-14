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
package com.helger.commons.thirdparty;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.helger.commons.mock.CommonsTestHelper;
import com.helger.commons.version.Version;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Test class for class {@link ThirdPartyModule}.
 *
 * @author Philip Helger
 */
public final class ThirdPartyModuleTest
{
  @Test
  @SuppressFBWarnings (value = "NP_NONNULL_PARAM_VIOLATION")
  public void testAll ()
  {
    ThirdPartyModule mod = new ThirdPartyModule ("displayname", "owner", ELicense.APACHE1);
    assertEquals ("displayname", mod.getDisplayName ());
    assertEquals ("owner", mod.getCopyrightOwner ());
    assertEquals (ELicense.APACHE1, mod.getLicense ());
    assertNull (mod.getVersion ());
    assertNull (mod.getWebSiteURL ());
    assertFalse (mod.isOptional ());

    mod = new ThirdPartyModule ("displayname", "owner", ELicense.APACHE1, true);
    assertEquals ("displayname", mod.getDisplayName ());
    assertEquals ("owner", mod.getCopyrightOwner ());
    assertEquals (ELicense.APACHE1, mod.getLicense ());
    assertNull (mod.getVersion ());
    assertNull (mod.getWebSiteURL ());
    assertTrue (mod.isOptional ());

    mod = new ThirdPartyModule ("displayname", "owner", ELicense.APACHE1, new Version ("1.1"), null);
    assertEquals ("displayname", mod.getDisplayName ());
    assertEquals ("owner", mod.getCopyrightOwner ());
    assertEquals (ELicense.APACHE1, mod.getLicense ());
    assertEquals (new Version (1, 1), mod.getVersion ());
    assertNull (mod.getWebSiteURL ());
    assertFalse (mod.isOptional ());

    mod = new ThirdPartyModule ("displayname", "owner", ELicense.APACHE1, new Version ("1.1"), "url", true);
    assertEquals ("displayname", mod.getDisplayName ());
    assertEquals ("owner", mod.getCopyrightOwner ());
    assertEquals (ELicense.APACHE1, mod.getLicense ());
    assertEquals (new Version (1, 1), mod.getVersion ());
    assertEquals ("url", mod.getWebSiteURL ());
    assertTrue (mod.isOptional ());

    try
    {
      new ThirdPartyModule (null, "owner", ELicense.APACHE1);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      new ThirdPartyModule ("displayname", null, ELicense.APACHE1);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      new ThirdPartyModule ("displayname", "owner", null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testStd ()
  {
    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (new ThirdPartyModule ("displayname",
                                                                                             "owner",
                                                                                             ELicense.APACHE1),
                                                                       new ThirdPartyModule ("displayname",
                                                                                             "owner",
                                                                                             ELicense.APACHE1));
    final ThirdPartyModule mod = new ThirdPartyModule ("displayname",
                                                       "owner",
                                                       ELicense.APACHE1,
                                                       new Version ("1.1"),
                                                       "url",
                                                       true);
    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (mod,
                                                                       new ThirdPartyModule ("displayname",
                                                                                             "owner",
                                                                                             ELicense.APACHE1,
                                                                                             new Version ("1.1"),
                                                                                             "url",
                                                                                             true));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (mod,
                                                                           new ThirdPartyModule ("displayname2",
                                                                                                 "owner",
                                                                                                 ELicense.APACHE1,
                                                                                                 new Version ("1.1"),
                                                                                                 "url",
                                                                                                 true));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (mod,
                                                                           new ThirdPartyModule ("displayname",
                                                                                                 "owner2",
                                                                                                 ELicense.APACHE1,
                                                                                                 new Version ("1.1"),
                                                                                                 "url",
                                                                                                 true));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (mod,
                                                                           new ThirdPartyModule ("displayname",
                                                                                                 "owner",
                                                                                                 ELicense.APACHE2,
                                                                                                 new Version ("1.1"),
                                                                                                 "url",
                                                                                                 true));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (mod,
                                                                           new ThirdPartyModule ("displayname",
                                                                                                 "owner",
                                                                                                 ELicense.APACHE1,
                                                                                                 new Version ("1.1.2"),
                                                                                                 "url",
                                                                                                 true));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (mod,
                                                                           new ThirdPartyModule ("displayname",
                                                                                                 "owner",
                                                                                                 ELicense.APACHE1,
                                                                                                 new Version ("1.1"),
                                                                                                 "url2",
                                                                                                 true));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (mod,
                                                                           new ThirdPartyModule ("displayname",
                                                                                                 "owner",
                                                                                                 ELicense.APACHE1,
                                                                                                 new Version ("1.1"),
                                                                                                 "url",
                                                                                                 false));
  }
}
