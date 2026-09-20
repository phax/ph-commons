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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.helger.base.string.StringHelper;

/**
 * Test class for class {@link EHttpReferrerPolicy}.
 *
 * @author Philip Helger
 */
public final class EHttpReferrerPolicyTest
{
  @Test
  public void testAll ()
  {
    for (final EHttpReferrerPolicy e : EHttpReferrerPolicy.values ())
    {
      assertNotNull (e.getValue ());
      assertSame (e, EHttpReferrerPolicy.valueOf (e.name ()));
      if (e != EHttpReferrerPolicy.NONE)
        assertTrue (StringHelper.isNotEmpty (e.getValue ()));
    }
    assertEquals ("", EHttpReferrerPolicy.NONE.getValue ());
    assertEquals ("no-referrer", EHttpReferrerPolicy.NO_REFERRER.getValue ());
    assertEquals ("strict-origin-when-cross-origin", EHttpReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN.getValue ());
  }
}
