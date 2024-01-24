/*
 * Copyright (C) 2014-2024 Philip Helger (www.helger.com)
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
 * Test class for class {@link EContinue}.
 *
 * @author Philip Helger
 */
public final class EContinueTest
{
  @Test
  public void testAll ()
  {
    for (final EContinue e : EContinue.values ())
      assertSame (e, EContinue.valueOf (e.name ()));

    assertTrue (EContinue.CONTINUE.isContinue ());
    assertFalse (EContinue.CONTINUE.isBreak ());
    assertFalse (EContinue.BREAK.isContinue ());
    assertTrue (EContinue.BREAK.isBreak ());
    assertSame (EContinue.CONTINUE, EContinue.valueOf (true));
    assertSame (EContinue.BREAK, EContinue.valueOf (false));
    assertSame (EContinue.CONTINUE, EContinue.CONTINUE.or (EContinue.BREAK));
    assertSame (EContinue.CONTINUE, EContinue.CONTINUE.or (EContinue.CONTINUE));
    assertSame (EContinue.BREAK, EContinue.CONTINUE.and (EContinue.BREAK));
    assertSame (EContinue.CONTINUE, EContinue.CONTINUE.and (EContinue.CONTINUE));
    assertSame (EContinue.BREAK, EContinue.BREAK.or (EContinue.BREAK));
    assertSame (EContinue.CONTINUE, EContinue.BREAK.or (EContinue.CONTINUE));
    assertSame (EContinue.BREAK, EContinue.BREAK.and (EContinue.BREAK));
    assertSame (EContinue.BREAK, EContinue.BREAK.and (EContinue.CONTINUE));
  }
}
