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
package com.helger.scope.singleton;

import static org.junit.Assert.assertEquals;

import javax.annotation.Nonnull;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import com.helger.scope.IScope;
import com.helger.scope.mock.ScopeTestRule;

/**
 * Test class for class {@link AbstractGlobalSingleton}.
 *
 * @author Philip Helger
 */
public final class GlobalSingletonRecursionFuncTest
{
  @Rule
  public final TestRule m_aScopeRule = new ScopeTestRule ();

  public static final class MockRecursiveGlobalSingleton extends AbstractGlobalSingleton
  {
    private int m_nCountCtor = 0;
    private int m_nCountVirtual = 0;

    public MockRecursiveGlobalSingleton ()
    {
      ++m_nCountCtor;
    }

    static MockRecursiveGlobalSingleton getInstance ()
    {
      return getGlobalSingleton (MockRecursiveGlobalSingleton.class);
    }

    @Override
    protected void onAfterInstantiation (@Nonnull final IScope aScope)
    {
      ++m_nCountVirtual;
      final int nCountCtor = getInstance ().m_nCountCtor;
      assertEquals (1, nCountCtor);
    }
  }

  @Test
  public void testRecursiveSingleton ()
  {
    final int nCountCtor = MockRecursiveGlobalSingleton.getInstance ().m_nCountCtor;
    assertEquals (1, nCountCtor);
    final int nCountVirtual = MockRecursiveGlobalSingleton.getInstance ().m_nCountVirtual;
    assertEquals (1, nCountVirtual);
  }
}
