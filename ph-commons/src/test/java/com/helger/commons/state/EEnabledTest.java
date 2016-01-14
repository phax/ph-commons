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
package com.helger.commons.state;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Test class for class {@link EEnabled}.
 *
 * @author Philip Helger
 */
public final class EEnabledTest
{
  @Test
  public void testAll ()
  {
    for (final EEnabled e : EEnabled.values ())
      assertSame (e, EEnabled.valueOf (e.name ()));

    assertTrue (EEnabled.ENABLED.isEnabled ());
    assertFalse (EEnabled.ENABLED.isDisabled ());
    assertFalse (EEnabled.DISABLED.isEnabled ());
    assertTrue (EEnabled.DISABLED.isDisabled ());
    assertSame (EEnabled.ENABLED, EEnabled.valueOf (true));
    assertSame (EEnabled.DISABLED, EEnabled.valueOf (false));
    assertSame (EEnabled.ENABLED, EEnabled.ENABLED.or (EEnabled.DISABLED));
    assertSame (EEnabled.ENABLED, EEnabled.ENABLED.or (EEnabled.ENABLED));
    assertSame (EEnabled.DISABLED, EEnabled.ENABLED.and (EEnabled.DISABLED));
    assertSame (EEnabled.ENABLED, EEnabled.ENABLED.and (EEnabled.ENABLED));
    assertSame (EEnabled.DISABLED, EEnabled.DISABLED.or (EEnabled.DISABLED));
    assertSame (EEnabled.ENABLED, EEnabled.DISABLED.or (EEnabled.ENABLED));
    assertSame (EEnabled.DISABLED, EEnabled.DISABLED.and (EEnabled.DISABLED));
    assertSame (EEnabled.DISABLED, EEnabled.DISABLED.and (EEnabled.ENABLED));
  }
}
