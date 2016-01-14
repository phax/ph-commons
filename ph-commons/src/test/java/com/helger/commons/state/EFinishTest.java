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
 * Test class for class {@link EFinish}.
 *
 * @author Philip Helger
 */
public final class EFinishTest
{
  @Test
  public void testAll ()
  {
    for (final EFinish e : EFinish.values ())
      assertSame (e, EFinish.valueOf (e.name ()));

    assertTrue (EFinish.FINISHED.isFinished ());
    assertFalse (EFinish.FINISHED.isUnfinished ());
    assertFalse (EFinish.UNFINISHED.isFinished ());
    assertTrue (EFinish.UNFINISHED.isUnfinished ());
    assertSame (EFinish.FINISHED, EFinish.valueOf (true));
    assertSame (EFinish.UNFINISHED, EFinish.valueOf (false));
    assertSame (EFinish.FINISHED, EFinish.FINISHED.or (EFinish.UNFINISHED));
    assertSame (EFinish.FINISHED, EFinish.FINISHED.or (EFinish.FINISHED));
    assertSame (EFinish.UNFINISHED, EFinish.FINISHED.and (EFinish.UNFINISHED));
    assertSame (EFinish.FINISHED, EFinish.FINISHED.and (EFinish.FINISHED));
    assertSame (EFinish.UNFINISHED, EFinish.UNFINISHED.or (EFinish.UNFINISHED));
    assertSame (EFinish.FINISHED, EFinish.UNFINISHED.or (EFinish.FINISHED));
    assertSame (EFinish.UNFINISHED, EFinish.UNFINISHED.and (EFinish.UNFINISHED));
    assertSame (EFinish.UNFINISHED, EFinish.UNFINISHED.and (EFinish.FINISHED));
  }
}
