/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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
package com.helger.diagnostics.error.level;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

/**
 * Test class for class {@link ErrorLevel}.
 *
 * @author Philip Helger
 */
public final class ErrorLevelTest
{
  @Test
  public void testBasic ()
  {
    final ErrorLevel aEL = new ErrorLevel ("id1", 100);
    assertEquals ("id1", aEL.getID ());
    assertEquals (100, aEL.getNumericLevel ());
    assertNotNull (aEL.toString ());

    assertEquals (aEL, aEL);
    assertEquals (aEL, new ErrorLevel ("id1", 100));
    assertEquals (aEL.hashCode (), new ErrorLevel ("id1", 100).hashCode ());
    assertNotEquals (aEL, null);
    assertNotEquals (aEL, "any other type");
    assertNotEquals (aEL, new ErrorLevel ("id2", 100));
    assertNotEquals (aEL, new ErrorLevel ("id1", 101));

    try
    {
      new ErrorLevel ("", 100);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {
      // expected
    }
  }

  @Test
  public void testComparable ()
  {
    final ErrorLevel aLow = new ErrorLevel ("low", 100);
    final ErrorLevel aHigh = new ErrorLevel ("high", 200);

    assertTrue (aLow.isLT (aHigh));
    assertTrue (aLow.isLE (aHigh));
    assertFalse (aLow.isGT (aHigh));
    assertFalse (aLow.isGE (aHigh));
    assertFalse (aLow.isEQ (aHigh));
    assertTrue (aLow.isNE (aHigh));
    assertTrue (aLow.compareTo (aHigh) < 0);

    assertTrue (aLow.isEQ (new ErrorLevel ("other", 100)));
    assertTrue (aLow.isLE (new ErrorLevel ("other", 100)));
    assertTrue (aLow.isGE (new ErrorLevel ("other", 100)));
    assertEquals (0, aLow.compareTo (new ErrorLevel ("other", 100)));

    assertFalse (aLow.isHighest ());
    assertTrue (EErrorLevel.HIGHEST.isHighest ());
  }

  @SuppressWarnings ("removal")
  @Test
  public void testSuccessAndError ()
  {
    // Only the lowest level is a "success"
    final ErrorLevel aSuccess = new ErrorLevel ("s", EErrorLevel.SUCCESS.getNumericLevel ());
    assertTrue (aSuccess.isSuccess ());
    assertFalse (aSuccess.isFailure ());
    assertFalse (aSuccess.isError ());
    assertTrue (aSuccess.isNoError ());

    // Everything above "success" is a failure, but not yet an error
    final ErrorLevel aInfo = new ErrorLevel ("i", EErrorLevel.INFO.getNumericLevel ());
    assertFalse (aInfo.isSuccess ());
    assertTrue (aInfo.isFailure ());
    assertFalse (aInfo.isError ());
    assertTrue (aInfo.isNoError ());

    final ErrorLevel aError = new ErrorLevel ("e", EErrorLevel.ERROR.getNumericLevel ());
    assertFalse (aError.isSuccess ());
    assertTrue (aError.isFailure ());
    assertTrue (aError.isError ());
    assertFalse (aError.isNoError ());
  }
}
