/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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
package com.helger.security.authentication.result;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.helger.commons.collection.impl.CommonsHashSet;
import com.helger.commons.collection.impl.ICommonsSet;
import com.helger.commons.random.RandomHelper;

/**
 * Test class for class AuthTokenIDGenerator.
 *
 * @author Philip Helger
 */
public final class AuthTokenIDGeneratorTest
{
  @Test
  public void testAll ()
  {
    // Ensure they are unique....
    final boolean bOld = RandomHelper.isUseSecureRandom ();
    RandomHelper.setUseSecureRandom (false);
    try
    {
      final ICommonsSet <String> aAll = new CommonsHashSet <> ();
      for (int i = 0; i < 1000; i++)
        assertTrue (aAll.add (AuthTokenIDGenerator.generateNewTokenID ()));
    }
    finally
    {
      RandomHelper.setUseSecureRandom (bOld);
    }
  }
}
