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
package com.helger.commons.compare;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.helger.commons.CGlobal;
import com.helger.commons.string.StringHelper;

/**
 * Test class for class {@link ESortOrder}.
 *
 * @author Philip Helger
 */
public final class ESortOrderTest
{
  @Test
  public void testAll ()
  {
    for (final ESortOrder e : ESortOrder.values ())
    {
      assertTrue (e.getValue () >= 0);
      assertTrue (StringHelper.hasText (e.getValueAsString ()));
      assertSame (e, ESortOrder.getFromValue (e.getValue (), null));
      assertSame (e, ESortOrder.getFromValueOrDefault (e.getValue ()));
      assertSame (e, ESortOrder.valueOf (e.name ()));
    }
    assertNull (ESortOrder.getFromValue (CGlobal.ILLEGAL_UINT, null));
    assertTrue (ESortOrder.ASCENDING.isAscending ());
    assertFalse (ESortOrder.ASCENDING.isDescending ());
    assertFalse (ESortOrder.DESCENDING.isAscending ());
    assertTrue (ESortOrder.DESCENDING.isDescending ());
  }
}
