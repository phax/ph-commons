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
package com.helger.scope.spi;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.helger.scope.ISessionScope;
import com.helger.scope.ScopeHelper;
import com.helger.scope.mgr.ScopeManager;
import com.helger.scope.mgr.Scoped;

/**
 * Test class for class {@link ScopeSPIManager}.
 *
 * @author Philip Helger
 */
public final class ScopeSPIManagerTest
{
  static
  {
    ScopeHelper.setLifeCycleDebuggingEnabled (true);
  }

  @Test
  public void testCount ()
  {
    assertEquals (2, ScopeSPIManager.getInstance ().getAllGlobalScopeSPIs ().size ());
    assertEquals (2, ScopeSPIManager.getInstance ().getAllSessionScopeSPIs ().size ());
    assertEquals (2, ScopeSPIManager.getInstance ().getAllRequestScopeSPIs ().size ());
  }

  @Test
  public void testGlobalScope ()
  {
    // Create global scope only
    int nPrev = AbstractMockScopeSPI.getBegin ();
    int nPrevT = AbstractMockThrowingScopeSPI.getBegin ();
    ScopeManager.onGlobalBegin ("global");
    assertEquals (nPrev + 1, AbstractMockScopeSPI.getBegin ());
    assertEquals (nPrevT + 1, AbstractMockThrowingScopeSPI.getBegin ());

    // End global scope
    nPrev = AbstractMockScopeSPI.getEnd ();
    nPrevT = AbstractMockThrowingScopeSPI.getEnd ();
    ScopeManager.onGlobalEnd ();
    assertEquals (nPrev + 1, AbstractMockScopeSPI.getEnd ());
    assertEquals (nPrevT + 1, AbstractMockThrowingScopeSPI.getEnd ());
    ScopeManager.onGlobalEnd ();
    assertEquals (nPrev + 1, AbstractMockScopeSPI.getEnd ());
    assertEquals (nPrevT + 1, AbstractMockThrowingScopeSPI.getEnd ());
  }

  @Test
  public void testRequestScope ()
  {
    // Create global scope
    int nPrev = AbstractMockScopeSPI.getBegin ();
    int nPrevT = AbstractMockThrowingScopeSPI.getBegin ();
    ScopeManager.onGlobalBegin ("global");
    assertEquals (nPrev + 1, AbstractMockScopeSPI.getBegin ());
    assertEquals (nPrevT + 1, AbstractMockThrowingScopeSPI.getBegin ());

    // Create request scope
    nPrev = AbstractMockScopeSPI.getBegin ();
    nPrevT = AbstractMockThrowingScopeSPI.getBegin ();
    try (final Scoped aScoped = new Scoped ())
    {
      assertEquals (nPrev + 1, AbstractMockScopeSPI.getBegin ());
      assertEquals (nPrevT + 1, AbstractMockThrowingScopeSPI.getBegin ());

      // End request scope
      nPrev = AbstractMockScopeSPI.getEnd ();
      nPrevT = AbstractMockThrowingScopeSPI.getEnd ();
    }
    assertEquals (nPrev + 1, AbstractMockScopeSPI.getEnd ());
    assertEquals (nPrevT + 1, AbstractMockThrowingScopeSPI.getEnd ());
    ScopeManager.onRequestEnd ();
    assertEquals (nPrev + 1, AbstractMockScopeSPI.getEnd ());
    assertEquals (nPrevT + 1, AbstractMockThrowingScopeSPI.getEnd ());

    // End global scope
    nPrev = AbstractMockScopeSPI.getEnd ();
    nPrevT = AbstractMockThrowingScopeSPI.getEnd ();
    ScopeManager.onGlobalEnd ();
    assertEquals (nPrev + 1, AbstractMockScopeSPI.getEnd ());
    assertEquals (nPrevT + 1, AbstractMockThrowingScopeSPI.getEnd ());
    ScopeManager.onGlobalEnd ();
    assertEquals (nPrev + 1, AbstractMockScopeSPI.getEnd ());
    assertEquals (nPrevT + 1, AbstractMockThrowingScopeSPI.getEnd ());
  }

  @Test
  public void testSessionScopes ()
  {
    // Create global scope
    int nPrev = AbstractMockScopeSPI.getBegin ();
    int nPrevT = AbstractMockThrowingScopeSPI.getBegin ();
    ScopeManager.onGlobalBegin ("global");
    assertEquals (nPrev + 1, AbstractMockScopeSPI.getBegin ());
    assertEquals (nPrevT + 1, AbstractMockThrowingScopeSPI.getBegin ());

    // Create request scope
    nPrev = AbstractMockScopeSPI.getBegin ();
    nPrevT = AbstractMockThrowingScopeSPI.getBegin ();
    ISessionScope aSessionScope;
    try (final Scoped aScoped = new Scoped ())
    {
      assertEquals (nPrev + 1, AbstractMockScopeSPI.getBegin ());
      assertEquals (nPrevT + 1, AbstractMockThrowingScopeSPI.getBegin ());

      // Begin session scope
      nPrev = AbstractMockScopeSPI.getBegin ();
      nPrevT = AbstractMockThrowingScopeSPI.getBegin ();
      aSessionScope = ScopeManager.getSessionScope ();
      assertEquals (nPrev + 1, AbstractMockScopeSPI.getBegin ());
      assertEquals (nPrevT + 1, AbstractMockThrowingScopeSPI.getBegin ());

      // End request scope
      nPrev = AbstractMockScopeSPI.getEnd ();
      nPrevT = AbstractMockThrowingScopeSPI.getEnd ();
    }
    assertEquals (nPrev + 1, AbstractMockScopeSPI.getEnd ());
    assertEquals (nPrevT + 1, AbstractMockThrowingScopeSPI.getEnd ());
    ScopeManager.onRequestEnd ();
    assertEquals (nPrev + 1, AbstractMockScopeSPI.getEnd ());
    assertEquals (nPrevT + 1, AbstractMockThrowingScopeSPI.getEnd ());

    // End session scope
    nPrev = AbstractMockScopeSPI.getEnd ();
    nPrevT = AbstractMockThrowingScopeSPI.getEnd ();
    ScopeManager.destroySessionScope (aSessionScope);
    assertEquals (nPrev + 1, AbstractMockScopeSPI.getEnd ());
    assertEquals (nPrevT + 1, AbstractMockThrowingScopeSPI.getEnd ());
    ScopeManager.destroySessionScope (aSessionScope);
    assertEquals (nPrev + 1, AbstractMockScopeSPI.getEnd ());
    assertEquals (nPrevT + 1, AbstractMockThrowingScopeSPI.getEnd ());

    // End global scope and application scopes
    nPrev = AbstractMockScopeSPI.getEnd ();
    nPrevT = AbstractMockThrowingScopeSPI.getEnd ();
    ScopeManager.onGlobalEnd ();
    assertEquals (nPrev + 1, AbstractMockScopeSPI.getEnd ());
    assertEquals (nPrevT + 1, AbstractMockThrowingScopeSPI.getEnd ());
    ScopeManager.onGlobalEnd ();
    assertEquals (nPrev + 1, AbstractMockScopeSPI.getEnd ());
    assertEquals (nPrevT + 1, AbstractMockThrowingScopeSPI.getEnd ());
  }
}
