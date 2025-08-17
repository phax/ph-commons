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
package com.helger.base.rt;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.helger.base.rt.BooleanHelper;

/**
 * Test class for class {@link BooleanHelper}.
 *
 * @author Philip Helger
 */
public final class BooleanHelperTest
{
  @Test
  public void testGetBooleanValue ()
  {
    assertTrue (BooleanHelper.getBooleanValue (Boolean.TRUE, true));
    assertTrue (BooleanHelper.getBooleanValue (Boolean.TRUE, false));
    assertFalse (BooleanHelper.getBooleanValue (Boolean.FALSE, true));
    assertFalse (BooleanHelper.getBooleanValue (Boolean.FALSE, false));
    assertTrue (BooleanHelper.getBooleanValue (null, true));
    assertFalse (BooleanHelper.getBooleanValue (null, false));
  }
}
