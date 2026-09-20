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
package com.helger.http;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.helger.base.string.StringHelper;

/**
 * Test class for class {@link EHttpVersion}.
 *
 * @author Philip Helger
 */
public final class EHttpVersionTest
{
  @Test
  public void testAll ()
  {
    for (final EHttpVersion e : EHttpVersion.values ())
    {
      assertTrue (StringHelper.isNotEmpty (e.getName ()));
      assertSame (e, EHttpVersion.getFromNameOrNull (e.getName ()));
      assertSame (e, EHttpVersion.valueOf (e.name ()));
      assertNotNull (e.toString ());
    }
    assertNull (EHttpVersion.getFromNameOrNull (null));
    assertNull (EHttpVersion.getFromNameOrNull ("HTTP/0.9"));
  }

  @Test
  public void testVersionChecks ()
  {
    assertTrue (EHttpVersion.HTTP_10.is10 ());
    assertFalse (EHttpVersion.HTTP_11.is10 ());
    assertFalse (EHttpVersion.HTTP_20.is10 ());

    assertFalse (EHttpVersion.HTTP_10.isAtLeast11 ());
    assertTrue (EHttpVersion.HTTP_11.isAtLeast11 ());
    assertTrue (EHttpVersion.HTTP_20.isAtLeast11 ());
    assertTrue (EHttpVersion.HTTP_30.isAtLeast11 ());
  }
}
