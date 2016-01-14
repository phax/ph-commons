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
package com.helger.commons.system;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.helger.commons.string.StringHelper;

/**
 * Test class for class {@link EOperatingSystem}.
 *
 * @author Philip Helger
 */
public final class EOperatingSystemTest
{
  @Test
  public void testAll ()
  {
    for (final EOperatingSystem e : EOperatingSystem.values ())
    {
      assertTrue (StringHelper.hasText (e.getDisplayName ()));
      assertSame (e, EOperatingSystem.valueOf (e.name ()));
      assertSame (e, EOperatingSystem.forName (e.getDisplayName ()));
    }

    assertNotNull (EOperatingSystem.getCurrentOS ());
    final EOperatingSystem eOS = EOperatingSystem.getCurrentOS ();
    assertNotNull (eOS.toString ());
    assertTrue (eOS.isCurrentOS ());

    // We just expect to run in only on Windows as the only non-Unix OS
    if (eOS == EOperatingSystem.WINDOWS)
      assertFalse (eOS.isUnixBased ());
    else
      assertTrue (eOS.isUnixBased ());

    assertFalse (EOperatingSystem.UNKNOWN.isCurrentOS ());
    assertSame (EOperatingSystem.UNKNOWN, EOperatingSystem.forName (null));
  }
}
