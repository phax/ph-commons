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
package com.helger.commons.error;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Test class for class {@link EErrorLevel}
 *
 * @author Philip Helger
 */
public final class EErrorLevelTest
{
  @Test
  public void testBasic ()
  {
    for (final EErrorLevel e : EErrorLevel.values ())
    {
      assertNotNull (e.getID ());
      assertTrue (e.isEqualSevereThan (e));
      assertFalse (e.isLessSevereThan (e));
      assertTrue (e.isLessOrEqualSevereThan (e));
      assertFalse (e.isMoreSevereThan (e));
      assertTrue (e.isMoreOrEqualSevereThan (e));
      assertSame (e, EErrorLevel.getFromIDOrNull (e.getID ()));
      assertSame (e, EErrorLevel.getFromIDOrThrow (e.getID ()));
      assertSame (e, EErrorLevel.getFromIDOrDefault (e.getID (), null));
      assertSame (e, EErrorLevel.valueOf (e.name ()));
    }
  }

  @Test
  public void testCompare ()
  {
    assertTrue (EErrorLevel.WARN.isEqualSevereThan (EErrorLevel.WARN));
    assertFalse (EErrorLevel.WARN.isEqualSevereThan (EErrorLevel.INFO));

    assertTrue (EErrorLevel.WARN.isLessSevereThan (EErrorLevel.ERROR));
    assertFalse (EErrorLevel.WARN.isLessSevereThan (EErrorLevel.WARN));
    assertFalse (EErrorLevel.WARN.isLessSevereThan (EErrorLevel.INFO));

    assertTrue (EErrorLevel.WARN.isLessOrEqualSevereThan (EErrorLevel.ERROR));
    assertTrue (EErrorLevel.WARN.isLessOrEqualSevereThan (EErrorLevel.WARN));
    assertFalse (EErrorLevel.WARN.isLessOrEqualSevereThan (EErrorLevel.INFO));

    assertFalse (EErrorLevel.WARN.isMoreSevereThan (EErrorLevel.ERROR));
    assertFalse (EErrorLevel.WARN.isMoreSevereThan (EErrorLevel.WARN));
    assertTrue (EErrorLevel.WARN.isMoreSevereThan (EErrorLevel.INFO));

    assertFalse (EErrorLevel.WARN.isMoreOrEqualSevereThan (EErrorLevel.ERROR));
    assertTrue (EErrorLevel.WARN.isMoreOrEqualSevereThan (EErrorLevel.WARN));
    assertTrue (EErrorLevel.WARN.isMoreOrEqualSevereThan (EErrorLevel.INFO));
  }

  @Test
  public void testIsError ()
  {
    assertFalse (EErrorLevel.SUCCESS.isError ());
    assertTrue (EErrorLevel.SUCCESS.isNoError ());
    assertFalse (EErrorLevel.INFO.isError ());
    assertTrue (EErrorLevel.INFO.isNoError ());
    assertFalse (EErrorLevel.WARN.isError ());
    assertTrue (EErrorLevel.WARN.isNoError ());

    assertTrue (EErrorLevel.ERROR.isError ());
    assertFalse (EErrorLevel.ERROR.isNoError ());
    assertTrue (EErrorLevel.FATAL_ERROR.isError ());
    assertFalse (EErrorLevel.FATAL_ERROR.isNoError ());
  }

  @Test
  public void testGetMostSevere ()
  {
    assertNull (IErrorLevel.getMostSevere (null, null));
    assertEquals (EErrorLevel.ERROR, IErrorLevel.getMostSevere (EErrorLevel.ERROR, null));
    assertEquals (EErrorLevel.ERROR, IErrorLevel.getMostSevere (null, EErrorLevel.ERROR));
    assertEquals (EErrorLevel.ERROR, IErrorLevel.getMostSevere (EErrorLevel.ERROR, EErrorLevel.ERROR));
    assertEquals (EErrorLevel.ERROR, IErrorLevel.getMostSevere (EErrorLevel.WARN, EErrorLevel.ERROR));
    assertEquals (EErrorLevel.ERROR, IErrorLevel.getMostSevere (EErrorLevel.ERROR, EErrorLevel.WARN));
  }
}
