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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

/**
 * Test class for class {@link MemoryIntIDFactory}.
 *
 * @author Philip Helger
 */
public final class MemoryIntIDFactoryTest
{
  @Test
  public void testAll ()
  {
    final MemoryIntIDFactory x = new MemoryIntIDFactory (9);
    assertEquals (9, x.getNewID ());
    assertEquals (x, new MemoryIntIDFactory (10));
    assertNotEquals (x, new MemoryIntIDFactory (11));

    try
    {
      new MemoryIntIDFactory (-1);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
  }
}
