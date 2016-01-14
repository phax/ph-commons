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
 * Test class for class {@link ESuccess}.
 *
 * @author Philip Helger
 */
public final class ESuccessTest
{
  @Test
  public void testAll ()
  {
    for (final ESuccess e : ESuccess.values ())
      assertSame (e, ESuccess.valueOf (e.name ()));

    assertTrue (ESuccess.SUCCESS.isSuccess ());
    assertFalse (ESuccess.SUCCESS.isFailure ());
    assertFalse (ESuccess.FAILURE.isSuccess ());
    assertTrue (ESuccess.FAILURE.isFailure ());
    assertSame (ESuccess.SUCCESS, ESuccess.valueOf (true));
    assertSame (ESuccess.FAILURE, ESuccess.valueOf (false));
    assertSame (ESuccess.SUCCESS, ESuccess.SUCCESS.or (ESuccess.FAILURE));
    assertSame (ESuccess.SUCCESS, ESuccess.SUCCESS.or (ESuccess.SUCCESS));
    assertSame (ESuccess.FAILURE, ESuccess.SUCCESS.and (ESuccess.FAILURE));
    assertSame (ESuccess.SUCCESS, ESuccess.SUCCESS.and (ESuccess.SUCCESS));
    assertSame (ESuccess.FAILURE, ESuccess.FAILURE.or (ESuccess.FAILURE));
    assertSame (ESuccess.SUCCESS, ESuccess.FAILURE.or (ESuccess.SUCCESS));
    assertSame (ESuccess.FAILURE, ESuccess.FAILURE.and (ESuccess.FAILURE));
    assertSame (ESuccess.FAILURE, ESuccess.FAILURE.and (ESuccess.SUCCESS));
    assertSame (ESuccess.SUCCESS, ESuccess.valueOfChange (EChange.CHANGED));
    assertSame (ESuccess.FAILURE, ESuccess.valueOfChange (EChange.UNCHANGED));
  }
}
