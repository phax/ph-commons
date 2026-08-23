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
package com.helger.http.permissionspolicy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

/**
 * Test class for class {@link PermissionsPolicy}.
 *
 * @author Philip Helger
 */
public final class PermissionsPolicyTest
{
  @Test
  public void testBasic ()
  {
    // Empty allow list disables the feature everywhere
    assertEquals ("geolocation=()",
                  new PermissionsPolicy ().addDirective (PermissionsPolicyDirective.createGeolocation (PermissionsPolicyAllowList.createNone ()))
                                          .getAsString ());
    assertEquals ("geolocation=(self \"https://a.example.com\" \"https://b.example.com\")",
                  new PermissionsPolicy ().addDirective (PermissionsPolicyDirective.createGeolocation (new PermissionsPolicyAllowList ().addKeywordSelf ()
                                                                                                                                        .addOrigin ("https://a.example.com")
                                                                                                                                        .addOrigin ("https://b.example.com")))
                                          .getAsString ());
    assertEquals ("picture-in-picture=(), geolocation=(self \"https://example.com\"), camera=*",
                  new PermissionsPolicy ().addDirective (PermissionsPolicyDirective.createPictureInPicture (PermissionsPolicyAllowList.createNone ()))
                                          .addDirective (PermissionsPolicyDirective.createGeolocation (new PermissionsPolicyAllowList ().addKeywordSelf ()
                                                                                                                                        .addOrigin ("https://example.com")))
                                          .addDirective (PermissionsPolicyDirective.createCamera (PermissionsPolicyAllowList.createAll ()))
                                          .getAsString ());
    assertEquals ("microphone=(), geolocation=()",
                  new PermissionsPolicy ().addDirective (PermissionsPolicyDirective.createMicrophone (PermissionsPolicyAllowList.createNone ()))
                                          .addDirective (PermissionsPolicyDirective.createGeolocation (PermissionsPolicyAllowList.createNone ()))
                                          .getAsString ());
    assertEquals ("geolocation=(self \"https://*.example.com\")",
                  new PermissionsPolicy ().addDirective (PermissionsPolicyDirective.createGeolocation (PermissionsPolicyAllowList.createSelf ()
                                                                                                                                 .addOrigin ("https://*.example.com")))
                                          .getAsString ());
    // A directive without a value is skipped
    assertEquals ("",
                  new PermissionsPolicy ().addDirective (PermissionsPolicyDirective.createFullscreen (null))
                                          .getAsString ());
    assertEquals ("", new PermissionsPolicy ().getAsString ());
  }

  @Test
  public void testReportTo ()
  {
    assertEquals ("geolocation=();report-to=geo_endpoint",
                  new PermissionsPolicy ().addDirective (new PermissionsPolicyDirective ("geolocation",
                                                                                         PermissionsPolicyAllowList.createNone (),
                                                                                         "geo_endpoint"))
                                          .getAsString ());
    assertEquals ("camera=*, geolocation=();report-to=geo_endpoint",
                  new PermissionsPolicy ().addDirective (PermissionsPolicyDirective.createCamera (PermissionsPolicyAllowList.createAll ()))
                                          .addDirective (PermissionsPolicyDirective.createGeolocation (PermissionsPolicyAllowList.createNone ())
                                                                                   .getWithReportTo ("geo_endpoint"))
                                          .getAsString ());
  }

  @Test
  public void testAllowList ()
  {
    assertEquals ("()", new PermissionsPolicyAllowList ().getAsString ());
    assertEquals ("*", PermissionsPolicyAllowList.createAll ().getAsString ());
    assertEquals ("(self)", PermissionsPolicyAllowList.createSelf ().getAsString ());
    assertEquals ("(src)", new PermissionsPolicyAllowList ().addKeywordSrc ().getAsString ());
    assertEquals ("(\"https://a.example.com\")",
                  new PermissionsPolicyAllowList ().addOrigin ("https://a.example.com").getAsString ());
    // Duplicates are ignored
    assertEquals ("(self)", new PermissionsPolicyAllowList ().addKeywordSelf ().addKeywordSelf ().getAsString ());

    // The wildcard may only be used alone
    try
    {
      new PermissionsPolicyAllowList ().addKeywordSelf ().setAll ();
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {
      // expected
    }
    try
    {
      PermissionsPolicyAllowList.createAll ().addKeywordSelf ();
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {
      // expected
    }
  }

  @Test
  public void testValidity ()
  {
    assertTrue (PermissionsPolicyDirective.isValidName ("geolocation"));
    assertTrue (PermissionsPolicyDirective.isValidName ("picture-in-picture"));
    assertFalse (PermissionsPolicyDirective.isValidName (null));
    assertFalse (PermissionsPolicyDirective.isValidName (""));
    assertFalse (PermissionsPolicyDirective.isValidName ("geo location"));
    assertFalse (PermissionsPolicyDirective.isValidName ("geolocation="));

    assertTrue (PermissionsPolicyDirective.isValidValue (null));
    assertTrue (PermissionsPolicyDirective.isValidValue ("()"));
    assertTrue (PermissionsPolicyDirective.isValidValue ("(self \"https://example.com\")"));
    assertFalse (PermissionsPolicyDirective.isValidValue ("(self),camera=*"));
    assertFalse (PermissionsPolicyDirective.isValidValue ("();report-to=x"));

    assertTrue (AbstractPermissionsPolicyAllowList.isValidOrigin ("https://example.com"));
    assertTrue (AbstractPermissionsPolicyAllowList.isValidOrigin ("https://*.example.com"));
    assertFalse (AbstractPermissionsPolicyAllowList.isValidOrigin (null));
    assertFalse (AbstractPermissionsPolicyAllowList.isValidOrigin (""));
    assertFalse (AbstractPermissionsPolicyAllowList.isValidOrigin ("https://a.com https://b.com"));
    assertFalse (AbstractPermissionsPolicyAllowList.isValidOrigin ("https://a.com\""));
  }
}
