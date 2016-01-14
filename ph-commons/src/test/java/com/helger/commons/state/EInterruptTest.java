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
 * Test class for class {@link EInterrupt}.
 *
 * @author Philip Helger
 */
public final class EInterruptTest
{
  @Test
  public void testAll ()
  {
    for (final EInterrupt e : EInterrupt.values ())
      assertSame (e, EInterrupt.valueOf (e.name ()));

    assertTrue (EInterrupt.INTERRUPTED.isInterrupted ());
    assertFalse (EInterrupt.INTERRUPTED.isNotInterrupted ());
    assertFalse (EInterrupt.NOT_INTERRUPTED.isInterrupted ());
    assertTrue (EInterrupt.NOT_INTERRUPTED.isNotInterrupted ());
    assertSame (EInterrupt.INTERRUPTED, EInterrupt.valueOf (true));
    assertSame (EInterrupt.NOT_INTERRUPTED, EInterrupt.valueOf (false));
    assertSame (EInterrupt.INTERRUPTED, EInterrupt.INTERRUPTED.or (EInterrupt.NOT_INTERRUPTED));
    assertSame (EInterrupt.INTERRUPTED, EInterrupt.INTERRUPTED.or (EInterrupt.INTERRUPTED));
    assertSame (EInterrupt.NOT_INTERRUPTED, EInterrupt.INTERRUPTED.and (EInterrupt.NOT_INTERRUPTED));
    assertSame (EInterrupt.INTERRUPTED, EInterrupt.INTERRUPTED.and (EInterrupt.INTERRUPTED));
    assertSame (EInterrupt.NOT_INTERRUPTED, EInterrupt.NOT_INTERRUPTED.or (EInterrupt.NOT_INTERRUPTED));
    assertSame (EInterrupt.INTERRUPTED, EInterrupt.NOT_INTERRUPTED.or (EInterrupt.INTERRUPTED));
    assertSame (EInterrupt.NOT_INTERRUPTED, EInterrupt.NOT_INTERRUPTED.and (EInterrupt.NOT_INTERRUPTED));
    assertSame (EInterrupt.NOT_INTERRUPTED, EInterrupt.NOT_INTERRUPTED.and (EInterrupt.INTERRUPTED));
  }
}
