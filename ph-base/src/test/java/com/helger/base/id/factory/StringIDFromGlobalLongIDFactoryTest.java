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
package com.helger.base.id.factory;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.helger.base.BaseTestHelper;

/**
 * Test class for class {@link StringIDFromGlobalLongIDFactory}.
 *
 * @author Philip Helger
 */
public final class StringIDFromGlobalLongIDFactoryTest
{
  @Test
  public void testAll ()
  {
    final StringIDFromGlobalLongIDFactory x = new StringIDFromGlobalLongIDFactory ("idd");
    BaseTestHelper.testDefaultImplementationWithEqualContentObject (x, new StringIDFromGlobalLongIDFactory ("idd"));
    BaseTestHelper.testDefaultImplementationWithDifferentContentObject (x,
                                                                        new StringIDFromGlobalLongIDFactory ("prefix"));
    assertTrue (x.getNewID ().startsWith ("idd"));

    try
    {
      new StringIDFromGlobalLongIDFactory (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }
}
