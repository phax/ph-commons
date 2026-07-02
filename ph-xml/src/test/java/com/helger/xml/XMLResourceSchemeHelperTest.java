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
package com.helger.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.MalformedURLException;

import org.junit.Test;

import com.helger.collection.commons.CommonsHashSet;
import com.helger.collection.commons.ICommonsSet;
import com.helger.io.resource.ClassPathResource;
import com.helger.io.resource.FileSystemResource;
import com.helger.io.resource.URLResource;

/**
 * Test class for class {@link XMLResourceSchemeHelper}.
 *
 * @author Philip Helger
 */
public final class XMLResourceSchemeHelperTest
{
  @Test
  public void testGetAllRemoteNetworkSchemes ()
  {
    final ICommonsSet <String> aSchemes = XMLResourceSchemeHelper.getAllRemoteNetworkSchemes ();
    assertEquals (4, aSchemes.size ());
    assertTrue (aSchemes.contains ("http"));
    assertTrue (aSchemes.contains ("https"));
    assertTrue (aSchemes.contains ("ftp"));
    assertTrue (aSchemes.contains ("ftps"));

    // Must be a copy - modifying it does not affect the source
    aSchemes.clear ();
    assertTrue (XMLResourceSchemeHelper.getAllRemoteNetworkSchemes ().contains ("http"));
  }

  @Test
  public void testNullResourceIsAllowed ()
  {
    // A null resource cannot point anywhere -> allowed
    assertTrue (XMLResourceSchemeHelper.isResourceAccessAllowed (null, new CommonsHashSet <> ()));
  }

  @Test
  public void testNonUrlResourcesAreAllowed ()
  {
    final ICommonsSet <String> aNoRemote = new CommonsHashSet <> ();
    // Class path and file system resources never trigger a network request
    assertTrue (XMLResourceSchemeHelper.isResourceAccessAllowed (new ClassPathResource ("xml/schema1.xsd"), aNoRemote));
    assertTrue (XMLResourceSchemeHelper.isResourceAccessAllowed (new FileSystemResource ("pom.xml"), aNoRemote));
  }

  @Test
  public void testLocalUrlSchemesAreAllowed () throws MalformedURLException
  {
    final ICommonsSet <String> aNoRemote = new CommonsHashSet <> ();
    // file and jar are local schemes -> always allowed
    assertTrue (XMLResourceSchemeHelper.isResourceAccessAllowed (new URLResource ("file:/tmp/schema.xsd"), aNoRemote));
    assertTrue (XMLResourceSchemeHelper.isResourceAccessAllowed (new URLResource ("jar:file:/tmp/lib.jar!/schema.xsd"),
                                                                 aNoRemote));
  }

  @Test
  public void testRemoteSchemesBlockedByDefault () throws MalformedURLException
  {
    final ICommonsSet <String> aNoRemote = new CommonsHashSet <> ();
    assertFalse (XMLResourceSchemeHelper.isResourceAccessAllowed (new URLResource ("http://example.org/schema.xsd"),
                                                                  aNoRemote));
    assertFalse (XMLResourceSchemeHelper.isResourceAccessAllowed (new URLResource ("https://example.org/schema.xsd"),
                                                                  aNoRemote));
    assertFalse (XMLResourceSchemeHelper.isResourceAccessAllowed (new URLResource ("ftp://example.org/schema.xsd"),
                                                                  aNoRemote));
  }

  @Test
  public void testRemoteSchemesAllowedWhenListed () throws MalformedURLException
  {
    final ICommonsSet <String> aAllowHttp = new CommonsHashSet <> ("http");
    // http is explicitly allowed
    assertTrue (XMLResourceSchemeHelper.isResourceAccessAllowed (new URLResource ("http://example.org/schema.xsd"),
                                                                 aAllowHttp));
    // https is not in the allowed set -> still blocked
    assertFalse (XMLResourceSchemeHelper.isResourceAccessAllowed (new URLResource ("https://example.org/schema.xsd"),
                                                                  aAllowHttp));
  }

  @Test
  public void testNullAllowedSchemesThrows ()
  {
    try
    {
      XMLResourceSchemeHelper.isResourceAccessAllowed (null, null);
      fail ();
    }
    catch (final NullPointerException ex)
    {
      // Expected - the allowed schemes set may not be null
    }
  }
}
