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
package com.helger.commons.url;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

/**
 * Test class for class {@link URLProtocolRegistry}.
 *
 * @author Philip Helger
 */
public final class URLProtocolRegistryTest
{
  @Test
  public void testDefaultsPresent ()
  {
    for (final EURLProtocol e : EURLProtocol.values ())
    {
      final String sURL = e.getProtocol () + "xyz";
      final URLData aURL = new URLData (sURL);
      assertTrue (URLProtocolRegistry.getInstance ().hasKnownProtocol (sURL));
      assertTrue (URLProtocolRegistry.getInstance ().hasKnownProtocol (aURL));
      assertSame (e, URLProtocolRegistry.getInstance ().getProtocol (sURL));
      assertSame (e, URLProtocolRegistry.getInstance ().getProtocol (aURL));
    }
  }

  @Test
  public void testReinitialize ()
  {
    final String sPrefix = "xyz://";
    final URLProtocol aCustom = new URLProtocol (sPrefix, false);
    final String sTestURL = sPrefix + "abZZ";
    assertFalse (URLProtocolRegistry.getInstance ().hasKnownProtocol (sTestURL));
    URLProtocolRegistry.getInstance ().registerProtocol (aCustom);
    assertTrue (URLProtocolRegistry.getInstance ().hasKnownProtocol (sTestURL));

    try
    {
      // Already registered
      URLProtocolRegistry.getInstance ().registerProtocol (aCustom);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    URLProtocolRegistry.getInstance ().reinitialize ();
    assertFalse (URLProtocolRegistry.getInstance ().hasKnownProtocol (sTestURL));
    URLProtocolRegistry.getInstance ().registerProtocol (aCustom);
    assertTrue (URLProtocolRegistry.getInstance ().hasKnownProtocol (sTestURL));
  }
}
