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
package com.helger.scope.singleton;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Test;

/**
 * Test class for class {@link SingletonHelper}.
 *
 * @author Philip Helger
 */
public final class SingletonHelperTest
{
  @After
  public void restoreDefaults ()
  {
    SingletonHelper.setDebugConsistency (SingletonHelper.DEFAULT_DEBUG_CONSISTENCY);
    SingletonHelper.setDebugWithStackTrace (SingletonHelper.DEFAULT_DEBUG_WITH_STACK_TRACE);
  }

  @Test
  public void testDebugConsistency ()
  {
    SingletonHelper.setDebugConsistency (true);
    assertTrue (SingletonHelper.isDebugConsistency ());

    SingletonHelper.setDebugConsistency (false);
    assertFalse (SingletonHelper.isDebugConsistency ());
  }

  @Test
  public void testDebugWithStackTrace ()
  {
    assertFalse (SingletonHelper.DEFAULT_DEBUG_WITH_STACK_TRACE);

    SingletonHelper.setDebugWithStackTrace (true);
    assertTrue (SingletonHelper.isDebugWithStackTrace ());

    SingletonHelper.setDebugWithStackTrace (false);
    assertFalse (SingletonHelper.isDebugWithStackTrace ());
  }

  @Test
  public void testGetDebugStackTrace ()
  {
    SingletonHelper.setDebugWithStackTrace (false);
    assertNull (SingletonHelper.getDebugStackTrace ());

    SingletonHelper.setDebugWithStackTrace (true);
    assertNotNull (SingletonHelper.getDebugStackTrace ());
  }
}
