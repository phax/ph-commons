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
package com.helger.commons.id.factory;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.helger.commons.mock.CommonsTestHelper;

/**
 * Test class for class {@link StringIDFromGlobalPersistentIntIDFactory}.
 *
 * @author Philip Helger
 */
public final class StringIDFromGlobalPersistentIntIDFactoryTest
{
  @Test
  public void testAll ()
  {
    GlobalIDFactory.setPersistentIntIDFactory (new MemoryStaticIntIDFactory ());
    final StringIDFromGlobalPersistentIntIDFactory x = new StringIDFromGlobalPersistentIntIDFactory ("idd");
    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (x, new StringIDFromGlobalPersistentIntIDFactory ("idd"));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (x, new StringIDFromGlobalPersistentIntIDFactory ("prefix"));
    assertTrue (x.getNewID ().startsWith ("idd"));

    try
    {
      new StringIDFromGlobalPersistentIntIDFactory (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }
}
