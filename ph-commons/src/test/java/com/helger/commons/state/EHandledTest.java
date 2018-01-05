/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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
 * Test class for class {@link EHandled}.
 *
 * @author Philip Helger
 */
public final class EHandledTest
{
  @Test
  public void testAll ()
  {
    for (final EHandled e : EHandled.values ())
      assertSame (e, EHandled.valueOf (e.name ()));

    assertTrue (EHandled.HANDLED.isHandled ());
    assertFalse (EHandled.HANDLED.isUnhandled ());
    assertFalse (EHandled.UNHANDLED.isHandled ());
    assertTrue (EHandled.UNHANDLED.isUnhandled ());
    assertSame (EHandled.HANDLED, EHandled.valueOf (true));
    assertSame (EHandled.UNHANDLED, EHandled.valueOf (false));
    assertSame (EHandled.HANDLED, EHandled.HANDLED.or (EHandled.UNHANDLED));
    assertSame (EHandled.HANDLED, EHandled.HANDLED.or (EHandled.HANDLED));
    assertSame (EHandled.UNHANDLED, EHandled.HANDLED.and (EHandled.UNHANDLED));
    assertSame (EHandled.HANDLED, EHandled.HANDLED.and (EHandled.HANDLED));
    assertSame (EHandled.UNHANDLED, EHandled.UNHANDLED.or (EHandled.UNHANDLED));
    assertSame (EHandled.HANDLED, EHandled.UNHANDLED.or (EHandled.HANDLED));
    assertSame (EHandled.UNHANDLED, EHandled.UNHANDLED.and (EHandled.UNHANDLED));
    assertSame (EHandled.UNHANDLED, EHandled.UNHANDLED.and (EHandled.HANDLED));
  }
}
