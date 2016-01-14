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

import com.helger.commons.string.StringHelper;

/**
 * Test class for class {@link ETriState}.
 *
 * @author Philip Helger
 */
public final class ETriStateTest
{
  @Test
  public void testAll ()
  {
    for (final ETriState e : ETriState.values ())
      assertSame (e, ETriState.valueOf (e.name ()));

    assertTrue (ETriState.TRUE.isTrue ());
    assertFalse (ETriState.TRUE.isFalse ());
    assertFalse (ETriState.TRUE.isUndefined ());
    assertFalse (ETriState.FALSE.isTrue ());
    assertTrue (ETriState.FALSE.isFalse ());
    assertFalse (ETriState.FALSE.isUndefined ());
    assertFalse (ETriState.UNDEFINED.isTrue ());
    assertFalse (ETriState.UNDEFINED.isFalse ());
    assertTrue (ETriState.UNDEFINED.isUndefined ());

    assertTrue (ETriState.TRUE.getAsBooleanValue (true));
    assertTrue (ETriState.TRUE.getAsBooleanValue (false));
    assertFalse (ETriState.FALSE.getAsBooleanValue (true));
    assertFalse (ETriState.FALSE.getAsBooleanValue (false));
    assertTrue (ETriState.UNDEFINED.getAsBooleanValue (true));
    assertFalse (ETriState.UNDEFINED.getAsBooleanValue (false));

    assertSame (Boolean.TRUE, ETriState.TRUE.getAsBooleanObj (Boolean.TRUE));
    assertSame (Boolean.TRUE, ETriState.TRUE.getAsBooleanObj (Boolean.FALSE));
    assertSame (Boolean.FALSE, ETriState.FALSE.getAsBooleanObj (Boolean.TRUE));
    assertSame (Boolean.FALSE, ETriState.FALSE.getAsBooleanObj (Boolean.FALSE));
    assertSame (Boolean.TRUE, ETriState.UNDEFINED.getAsBooleanObj (Boolean.TRUE));
    assertSame (Boolean.FALSE, ETriState.UNDEFINED.getAsBooleanObj (Boolean.FALSE));

    assertSame (ETriState.TRUE, ETriState.valueOf (true));
    assertSame (ETriState.FALSE, ETriState.valueOf (false));
    assertSame (ETriState.TRUE, ETriState.valueOf (Boolean.TRUE));
    assertSame (ETriState.FALSE, ETriState.valueOf (Boolean.FALSE));
    assertSame (ETriState.UNDEFINED, ETriState.valueOf ((Boolean) null));

    for (final ETriState eTriState : ETriState.values ())
    {
      assertTrue (StringHelper.hasText (eTriState.getID ()));
      assertSame (eTriState, ETriState.getFromIDOrNull (eTriState.getID ()));
      assertSame (eTriState, ETriState.getFromIDOrDefault (eTriState.getID (), null));
      assertSame (eTriState, ETriState.getFromIDOrUndefined (eTriState.getID ()));
    }
  }
}
