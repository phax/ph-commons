/**
 * Copyright (C) 2014-2016 Philip Helger (www.helger.com)
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
package com.helger.commons.scope.spi;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.helger.commons.scope.ISessionScope;
import com.helger.commons.scope.ScopeHelper;
import com.helger.commons.scope.mgr.ScopeManager;

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
    assertEquals (2, ScopeSPIManager.getInstance ().getAllApplicationScopeSPIs ().size ());
    assertEquals (2, ScopeSPIManager.getInstance ().getAllSessionScopeSPIs ().size ());
    assertEquals (2, ScopeSPIManager.getInstance ().getAllSessionApplicationScopeSPIs ().size ());
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
    ScopeManager.onRequestBegin ("appid", "scopeid", "sessionid");
    assertEquals (nPrev + 1, AbstractMockScopeSPI.getBegin ());
    assertEquals (nPrevT + 1, AbstractMockThrowingScopeSPI.getBegin ());

    // End request scope
    nPrev = AbstractMockScopeSPI.getEnd ();
    nPrevT = AbstractMockThrowingScopeSPI.getEnd ();
    ScopeManager.onRequestEnd ();
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
  public void testApplicationScope ()
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
    ScopeManager.onRequestBegin ("appid", "scopeid", "sessionid");
    assertEquals (nPrev + 1, AbstractMockScopeSPI.getBegin ());
    assertEquals (nPrevT + 1, AbstractMockThrowingScopeSPI.getBegin ());

    // Create application scope
    nPrev = AbstractMockScopeSPI.getBegin ();
    nPrevT = AbstractMockThrowingScopeSPI.getBegin ();
    ScopeManager.getApplicationScope ();
    assertEquals (nPrev + 1, AbstractMockScopeSPI.getBegin ());
    assertEquals (nPrevT + 1, AbstractMockThrowingScopeSPI.getBegin ());

    // End request scope
    nPrev = AbstractMockScopeSPI.getEnd ();
    nPrevT = AbstractMockThrowingScopeSPI.getEnd ();
    ScopeManager.onRequestEnd ();
    assertEquals (nPrev + 1, AbstractMockScopeSPI.getEnd ());
    assertEquals (nPrevT + 1, AbstractMockThrowingScopeSPI.getEnd ());
    ScopeManager.onRequestEnd ();
    assertEquals (nPrev + 1, AbstractMockScopeSPI.getEnd ());
    assertEquals (nPrevT + 1, AbstractMockThrowingScopeSPI.getEnd ());

    // End global scope and application scope
    nPrev = AbstractMockScopeSPI.getEnd ();
    nPrevT = AbstractMockThrowingScopeSPI.getEnd ();
    ScopeManager.onGlobalEnd ();
    assertEquals (nPrev + 2, AbstractMockScopeSPI.getEnd ());
    assertEquals (nPrev + 2, AbstractMockThrowingScopeSPI.getEnd ());
    ScopeManager.onGlobalEnd ();
    assertEquals (nPrev + 2, AbstractMockScopeSPI.getEnd ());
    assertEquals (nPrev + 2, AbstractMockThrowingScopeSPI.getEnd ());
  }

  @Test
  public void testApplicationScopes ()
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
    ScopeManager.onRequestBegin ("appid", "scopeid", "sessionid");
    assertEquals (nPrev + 1, AbstractMockScopeSPI.getBegin ());
    assertEquals (nPrevT + 1, AbstractMockThrowingScopeSPI.getBegin ());

    // Create application scope
    nPrev = AbstractMockScopeSPI.getBegin ();
    nPrevT = AbstractMockThrowingScopeSPI.getBegin ();
    ScopeManager.getApplicationScope ();
    assertEquals (nPrev + 1, AbstractMockScopeSPI.getBegin ());
    assertEquals (nPrevT + 1, AbstractMockThrowingScopeSPI.getBegin ());

    // Create second application scope
    nPrev = AbstractMockScopeSPI.getBegin ();
    nPrevT = AbstractMockThrowingScopeSPI.getBegin ();
    ScopeManager.getApplicationScope ("any other blabla");
    assertEquals (nPrev + 1, AbstractMockScopeSPI.getBegin ());
    assertEquals (nPrevT + 1, AbstractMockThrowingScopeSPI.getBegin ());

    // End request scope
    nPrev = AbstractMockScopeSPI.getEnd ();
    nPrevT = AbstractMockThrowingScopeSPI.getEnd ();
    ScopeManager.onRequestEnd ();
    assertEquals (nPrev + 1, AbstractMockScopeSPI.getEnd ());
    assertEquals (nPrevT + 1, AbstractMockThrowingScopeSPI.getEnd ());
    ScopeManager.onRequestEnd ();
    assertEquals (nPrev + 1, AbstractMockScopeSPI.getEnd ());
    assertEquals (nPrevT + 1, AbstractMockThrowingScopeSPI.getEnd ());

    // End global scope and application scopes
    nPrev = AbstractMockScopeSPI.getEnd ();
    nPrevT = AbstractMockThrowingScopeSPI.getEnd ();
    ScopeManager.onGlobalEnd ();
    assertEquals (nPrev + 3, AbstractMockScopeSPI.getEnd ());
    assertEquals (nPrevT + 3, AbstractMockThrowingScopeSPI.getEnd ());
    ScopeManager.onGlobalEnd ();
    assertEquals (nPrev + 3, AbstractMockScopeSPI.getEnd ());
    assertEquals (nPrevT + 3, AbstractMockThrowingScopeSPI.getEnd ());
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
    ScopeManager.onRequestBegin ("appid", "scopeid", "sessionid");
    assertEquals (nPrev + 1, AbstractMockScopeSPI.getBegin ());
    assertEquals (nPrevT + 1, AbstractMockThrowingScopeSPI.getBegin ());

    // Create application scope
    nPrev = AbstractMockScopeSPI.getBegin ();
    nPrevT = AbstractMockThrowingScopeSPI.getBegin ();
    ScopeManager.getApplicationScope ();
    assertEquals (nPrev + 1, AbstractMockScopeSPI.getBegin ());
    assertEquals (nPrevT + 1, AbstractMockThrowingScopeSPI.getBegin ());

    // Create second application scope
    nPrev = AbstractMockScopeSPI.getBegin ();
    nPrevT = AbstractMockThrowingScopeSPI.getBegin ();
    ScopeManager.getApplicationScope ("any other blabla");
    assertEquals (nPrev + 1, AbstractMockScopeSPI.getBegin ());
    assertEquals (nPrevT + 1, AbstractMockThrowingScopeSPI.getBegin ());

    // Begin session scope
    nPrev = AbstractMockScopeSPI.getBegin ();
    nPrevT = AbstractMockThrowingScopeSPI.getBegin ();
    final ISessionScope aSessionScope = ScopeManager.getSessionScope ();
    assertEquals (nPrev + 1, AbstractMockScopeSPI.getBegin ());
    assertEquals (nPrevT + 1, AbstractMockThrowingScopeSPI.getBegin ());

    // End request scope
    nPrev = AbstractMockScopeSPI.getEnd ();
    nPrevT = AbstractMockThrowingScopeSPI.getEnd ();
    ScopeManager.onRequestEnd ();
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
    assertEquals (nPrev + 3, AbstractMockScopeSPI.getEnd ());
    assertEquals (nPrevT + 3, AbstractMockThrowingScopeSPI.getEnd ());
    ScopeManager.onGlobalEnd ();
    assertEquals (nPrev + 3, AbstractMockScopeSPI.getEnd ());
    assertEquals (nPrevT + 3, AbstractMockThrowingScopeSPI.getEnd ());
  }

  @Test
  public void testSessionApplicationScopes ()
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
    ScopeManager.onRequestBegin ("appid", "scopeid", "sessionid");
    assertEquals (nPrev + 1, AbstractMockScopeSPI.getBegin ());
    assertEquals (nPrevT + 1, AbstractMockThrowingScopeSPI.getBegin ());

    // Create application scope
    nPrev = AbstractMockScopeSPI.getBegin ();
    nPrevT = AbstractMockThrowingScopeSPI.getBegin ();
    ScopeManager.getApplicationScope ();
    assertEquals (nPrev + 1, AbstractMockScopeSPI.getBegin ());
    assertEquals (nPrevT + 1, AbstractMockThrowingScopeSPI.getBegin ());

    // Create second application scope
    nPrev = AbstractMockScopeSPI.getBegin ();
    nPrevT = AbstractMockThrowingScopeSPI.getBegin ();
    ScopeManager.getApplicationScope ("any other blabla");
    assertEquals (nPrev + 1, AbstractMockScopeSPI.getBegin ());
    assertEquals (nPrevT + 1, AbstractMockThrowingScopeSPI.getBegin ());

    // Begin session scope
    nPrev = AbstractMockScopeSPI.getBegin ();
    nPrevT = AbstractMockThrowingScopeSPI.getBegin ();
    final ISessionScope aSessionScope = ScopeManager.getSessionScope ();
    assertEquals (nPrev + 1, AbstractMockScopeSPI.getBegin ());
    assertEquals (nPrevT + 1, AbstractMockThrowingScopeSPI.getBegin ());

    // Get session application scope
    nPrev = AbstractMockScopeSPI.getBegin ();
    nPrevT = AbstractMockThrowingScopeSPI.getBegin ();
    ScopeManager.getSessionApplicationScope ();
    assertEquals (nPrev + 1, AbstractMockScopeSPI.getBegin ());
    assertEquals (nPrevT + 1, AbstractMockThrowingScopeSPI.getBegin ());

    // Get second session application scope
    nPrev = AbstractMockScopeSPI.getBegin ();
    nPrevT = AbstractMockThrowingScopeSPI.getBegin ();
    ScopeManager.getSessionApplicationScope ("session web scope for testing");
    assertEquals (nPrev + 1, AbstractMockScopeSPI.getBegin ());
    assertEquals (nPrevT + 1, AbstractMockThrowingScopeSPI.getBegin ());

    // End request scope
    nPrev = AbstractMockScopeSPI.getEnd ();
    nPrevT = AbstractMockThrowingScopeSPI.getEnd ();
    ScopeManager.onRequestEnd ();
    assertEquals (nPrev + 1, AbstractMockScopeSPI.getEnd ());
    assertEquals (nPrevT + 1, AbstractMockThrowingScopeSPI.getEnd ());
    ScopeManager.onRequestEnd ();
    assertEquals (nPrev + 1, AbstractMockScopeSPI.getEnd ());
    assertEquals (nPrevT + 1, AbstractMockThrowingScopeSPI.getEnd ());

    // End session scope and session application scopes
    nPrev = AbstractMockScopeSPI.getEnd ();
    nPrevT = AbstractMockThrowingScopeSPI.getEnd ();
    ScopeManager.destroySessionScope (aSessionScope);
    assertEquals (nPrev + 3, AbstractMockScopeSPI.getEnd ());
    assertEquals (nPrevT + 3, AbstractMockThrowingScopeSPI.getEnd ());
    ScopeManager.destroySessionScope (aSessionScope);
    assertEquals (nPrev + 3, AbstractMockScopeSPI.getEnd ());
    assertEquals (nPrevT + 3, AbstractMockThrowingScopeSPI.getEnd ());

    // End global scope and application scopes
    nPrev = AbstractMockScopeSPI.getEnd ();
    nPrevT = AbstractMockThrowingScopeSPI.getEnd ();
    ScopeManager.onGlobalEnd ();
    assertEquals (nPrev + 3, AbstractMockScopeSPI.getEnd ());
    assertEquals (nPrevT + 3, AbstractMockThrowingScopeSPI.getEnd ());
    ScopeManager.onGlobalEnd ();
    assertEquals (nPrev + 3, AbstractMockScopeSPI.getEnd ());
    assertEquals (nPrevT + 3, AbstractMockThrowingScopeSPI.getEnd ());
  }
}
