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
package com.helger.commons.concurrent;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.unittest.support.TestHelper;

/**
 * Test class for class {@link BasicThreadFactory}.
 *
 * @author Philip Helger
 */
public final class BasicThreadFactoryTest
{
  private static final Logger LOGGER = LoggerFactory.getLogger (BasicThreadFactoryTest.class);

  @Test
  public void testAll ()
  {
    final BasicThreadFactory x = new BasicThreadFactoryBuilder ().daemon (false)
                                                                  .priority (Thread.NORM_PRIORITY)
                                                                  .namingPattern ("pool %d")
                                                                  .build ();
    for (int i = 0; i < 2; ++i)
    {
      final Thread t = x.newThread ( () -> {
        // nada
        if (false)
          LOGGER.info ("In thread '" + Thread.currentThread ().getName () + "'");
      });
      assertNotNull (t);
      t.start ();
    }
    TestHelper.testToStringImplementation (x);
  }
}
