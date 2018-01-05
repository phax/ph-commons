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
package com.helger.commons.error.level;

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
  public void testBasic ()
  {
    for (final EErrorLevel e : EErrorLevel.values ())
    {
      assertNotNull (e.getID ());
      assertTrue (e.isEQ (e));
      assertFalse (e.isLT (e));
      assertTrue (e.isLE (e));
      assertFalse (e.isGT (e));
      assertTrue (e.isGE (e));
      assertSame (e, EErrorLevel.getFromIDOrNull (e.getID ()));
      assertSame (e, EErrorLevel.getFromIDOrThrow (e.getID ()));
      assertSame (e, EErrorLevel.getFromIDOrDefault (e.getID (), null));
      assertSame (e, EErrorLevel.valueOf (e.name ()));
    }
  }

  @Test
  public void testCompare ()
  {
    assertTrue (EErrorLevel.WARN.isEQ (EErrorLevel.WARN));
    assertFalse (EErrorLevel.WARN.isEQ (EErrorLevel.INFO));

    assertTrue (EErrorLevel.WARN.isLT (EErrorLevel.ERROR));
    assertFalse (EErrorLevel.WARN.isLT (EErrorLevel.WARN));
    assertFalse (EErrorLevel.WARN.isLT (EErrorLevel.INFO));

    assertTrue (EErrorLevel.WARN.isLE (EErrorLevel.ERROR));
    assertTrue (EErrorLevel.WARN.isLE (EErrorLevel.WARN));
    assertFalse (EErrorLevel.WARN.isLE (EErrorLevel.INFO));

    assertFalse (EErrorLevel.WARN.isGT (EErrorLevel.ERROR));
    assertFalse (EErrorLevel.WARN.isGT (EErrorLevel.WARN));
    assertTrue (EErrorLevel.WARN.isGT (EErrorLevel.INFO));

    assertFalse (EErrorLevel.WARN.isGE (EErrorLevel.ERROR));
    assertTrue (EErrorLevel.WARN.isGE (EErrorLevel.WARN));
    assertTrue (EErrorLevel.WARN.isGE (EErrorLevel.INFO));
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
