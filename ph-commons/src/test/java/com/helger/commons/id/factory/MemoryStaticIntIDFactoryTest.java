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
package com.helger.commons.id.factory;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.helger.commons.mock.CommonsTestHelper;

/**
 * Test class for class {@link MemoryStaticIntIDFactory}.
 *
 * @author Philip Helger
 */
public final class MemoryStaticIntIDFactoryTest
{
  @Test
  public void testAll ()
  {
    final MemoryStaticIntIDFactory x = new MemoryStaticIntIDFactory ();
    assertTrue (x.getNewID () >= MemoryStaticIntIDFactory.INITIAL_ID);
    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (x, new MemoryStaticIntIDFactory ());
  }
}
