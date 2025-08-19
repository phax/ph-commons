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
package com.helger.base.system;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test class for class {@link CryptoPolicy}.
 *
 * @author Philip Helger
 */
public final class CryptoPolicyTest
{
  private static final Logger LOGGER = LoggerFactory.getLogger (CryptoPolicyTest.class);

  @Test
  public void testIf ()
  {
    if (CryptoPolicy.isUnlimitedStrengthCryptoAvailable ())
    {
      LOGGER.info ("Unlimited strength crypto IS available.");
      assertTrue (CryptoPolicy.isUnlimitedStrengthCryptoAvailable ());
    }
    else
    {
      LOGGER.warn ("Unlimited strength crypto is NOT available.");
      assertFalse (CryptoPolicy.isUnlimitedStrengthCryptoAvailable ());
    }
  }
}
