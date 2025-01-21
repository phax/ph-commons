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
package com.helger.scope.mgr;

import static org.junit.Assert.assertNotNull;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import com.helger.scope.mock.ScopeTestRule;

/**
 * Test class for class {@link EScope}.
 *
 * @author Philip Helger
 */
public final class EScopeTest
{
  @Rule
  public final TestRule m_aScopeRule = new ScopeTestRule ();

  @Test
  public void testGetScope ()
  {
    for (final EScope eScope : EScope.values ())
    {
      eScope.getScope (false);
      assertNotNull (eScope.getScope (true));
      assertNotNull (eScope.getScope ());
      // Now all scopes are present
      assertNotNull (EScope.getScope (eScope, false));
      assertNotNull (EScope.getScope (eScope, true));
    }
  }
}
