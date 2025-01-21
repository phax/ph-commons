/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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
package com.helger.commons.callback;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.helger.commons.callback.exception.LoggingExceptionCallback;

/**
 * Test class for class {@link CallbackList}.
 *
 * @author Philip Helger
 */
public final class CallbackListTest
{
  @Test
  public void testBasic ()
  {
    final CallbackList <ICallback> aCL = new CallbackList <> ();
    assertTrue (aCL.isEmpty ());
    assertEquals (0, aCL.size ());
    aCL.add (new LoggingExceptionCallback ());
    assertFalse (aCL.isEmpty ());
    assertEquals (1, aCL.size ());
    aCL.add (new LoggingExceptionCallback ());
    assertFalse (aCL.isEmpty ());
    assertEquals (2, aCL.size ());
  }
}
