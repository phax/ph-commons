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
package com.helger.http.cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.helger.base.CGlobal;
import com.helger.collection.commons.CommonsArrayList;

/**
 * Test class for class {@link CacheControlBuilder}.
 *
 * @author Philip Helger
 */
public final class CacheControlBuilderTest
{
  @Test
  public void testEmpty ()
  {
    final CacheControlBuilder aCC = new CacheControlBuilder ();
    assertFalse (aCC.hasMaxAgeSeconds ());
    assertNull (aCC.getMaxAgeSeconds ());
    assertFalse (aCC.hasSharedMaxAgeSeconds ());
    assertNull (aCC.getSharedMaxAgeSeconds ());
    assertFalse (aCC.isPublic ());
    assertFalse (aCC.isPrivate ());
    assertFalse (aCC.isNoCache ());
    assertFalse (aCC.isNoStore ());
    assertFalse (aCC.isNoTransform ());
    assertFalse (aCC.isMustRevalidate ());
    assertFalse (aCC.isProxyRevalidate ());
    assertTrue (aCC.getAllExtensions ().isEmpty ());
    assertEquals ("", aCC.getAsHTTPHeaderValue ());
    assertNotNull (aCC.toString ());
  }

  @Test
  public void testMaxAge ()
  {
    final CacheControlBuilder aCC = new CacheControlBuilder ();
    assertSame (aCC, aCC.setMaxAgeSeconds (10));
    assertTrue (aCC.hasMaxAgeSeconds ());
    assertEquals (Long.valueOf (10), aCC.getMaxAgeSeconds ());
    assertEquals ("max-age=10", aCC.getAsHTTPHeaderValue ());

    aCC.setMaxAgeMinutes (2);
    assertEquals (Long.valueOf (2 * CGlobal.SECONDS_PER_MINUTE), aCC.getMaxAgeSeconds ());
    aCC.setMaxAgeHours (2);
    assertEquals (Long.valueOf (2 * CGlobal.SECONDS_PER_HOUR), aCC.getMaxAgeSeconds ());
    aCC.setMaxAgeDays (2);
    assertEquals (Long.valueOf (2 * CGlobal.SECONDS_PER_DAY), aCC.getMaxAgeSeconds ());
    aCC.setMaxAge (TimeUnit.MINUTES, 5);
    assertEquals (Long.valueOf (5 * CGlobal.SECONDS_PER_MINUTE), aCC.getMaxAgeSeconds ());

    try
    {
      aCC.setMaxAgeSeconds (-1);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {
      // expected
    }
  }

  @Test
  public void testSharedMaxAge ()
  {
    final CacheControlBuilder aCC = new CacheControlBuilder ();
    assertSame (aCC, aCC.setSharedMaxAgeSeconds (10));
    assertTrue (aCC.hasSharedMaxAgeSeconds ());
    assertEquals (Long.valueOf (10), aCC.getSharedMaxAgeSeconds ());
    assertEquals ("s-maxage=10", aCC.getAsHTTPHeaderValue ());

    aCC.setSharedMaxAgeMinutes (2);
    assertEquals (Long.valueOf (2 * CGlobal.SECONDS_PER_MINUTE), aCC.getSharedMaxAgeSeconds ());
    aCC.setSharedMaxAgeHours (2);
    assertEquals (Long.valueOf (2 * CGlobal.SECONDS_PER_HOUR), aCC.getSharedMaxAgeSeconds ());
    aCC.setSharedMaxAgeDays (2);
    assertEquals (Long.valueOf (2 * CGlobal.SECONDS_PER_DAY), aCC.getSharedMaxAgeSeconds ());
    aCC.setSharedMaxAge (TimeUnit.MINUTES, 5);
    assertEquals (Long.valueOf (5 * CGlobal.SECONDS_PER_MINUTE), aCC.getSharedMaxAgeSeconds ());

    try
    {
      aCC.setSharedMaxAgeSeconds (-1);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {
      // expected
    }
  }

  @Test
  public void testFlags ()
  {
    final CacheControlBuilder aCC = new CacheControlBuilder ();
    assertSame (aCC, aCC.setPublic (true));
    assertTrue (aCC.isPublic ());
    assertEquals ("public", aCC.getAsHTTPHeaderValue ());

    assertSame (aCC, aCC.setPrivate (true));
    assertTrue (aCC.isPrivate ());
    assertSame (aCC, aCC.setNoCache (true));
    assertTrue (aCC.isNoCache ());
    assertSame (aCC, aCC.setNoStore (true));
    assertTrue (aCC.isNoStore ());
    assertSame (aCC, aCC.setNoTransform (true));
    assertTrue (aCC.isNoTransform ());
    assertSame (aCC, aCC.setMustRevalidate (true));
    assertTrue (aCC.isMustRevalidate ());
    assertSame (aCC, aCC.setProxyRevalidate (true));
    assertTrue (aCC.isProxyRevalidate ());

    assertEquals ("public, private, no-cache, no-store, no-transform, must-revalidate, proxy-revalidate",
                  aCC.getAsHTTPHeaderValue ());

    // And reset all of them again
    aCC.setPublic (false)
       .setPrivate (false)
       .setNoCache (false)
       .setNoStore (false)
       .setNoTransform (false)
       .setMustRevalidate (false)
       .setProxyRevalidate (false);
    assertEquals ("", aCC.getAsHTTPHeaderValue ());
  }

  @Test
  public void testExtensions ()
  {
    final CacheControlBuilder aCC = new CacheControlBuilder ();
    assertSame (aCC, aCC.addExtension ("immutable"));
    aCC.addExtension ("stale-if-error=60");
    assertEquals (new CommonsArrayList <> ("immutable", "stale-if-error=60"), aCC.getAllExtensions ());
    assertEquals ("immutable, stale-if-error=60", aCC.getAsHTTPHeaderValue ());

    try
    {
      aCC.addExtension ("");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {
      // expected
    }
  }

  @Test
  public void testClone ()
  {
    final CacheControlBuilder aCC = new CacheControlBuilder ().setMaxAgeSeconds (10)
                                                              .setSharedMaxAgeSeconds (20)
                                                              .setPublic (true)
                                                              .setPrivate (true)
                                                              .setNoCache (true)
                                                              .setNoStore (true)
                                                              .setNoTransform (true)
                                                              .setMustRevalidate (true)
                                                              .setProxyRevalidate (true)
                                                              .addExtension ("immutable");

    final CacheControlBuilder aClone = aCC.getClone ();
    assertNotSame (aCC, aClone);
    assertEquals (aCC.getAsHTTPHeaderValue (), aClone.getAsHTTPHeaderValue ());

    // Modifying the clone does not modify the original
    aClone.addExtension ("other");
    assertEquals (1, aCC.getAllExtensions ().size ());
    assertEquals (2, aClone.getAllExtensions ().size ());

    assertEquals (aCC.getAsHTTPHeaderValue (), new CacheControlBuilder (aCC).getAsHTTPHeaderValue ());
  }
}
