/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.helger.scope.IRequestScope;
import com.helger.scope.mock.ScopeAwareTestSetup;

/**
 * Test class for class {@link Scoped}.
 *
 * @author Philip Helger
 */
public final class ScopedTest
{
  // Scoped creates the request scope on its own, so only the global scope may
  // be present upfront
  @Before
  public void beginGlobalScope ()
  {
    ScopeManager.onGlobalBegin (ScopeAwareTestSetup.MOCK_GLOBAL_SCOPE_ID);
  }

  @After
  public void endGlobalScope ()
  {
    ScopeManager.onGlobalEnd ();
  }

  @Test
  public void testDefaultCtor ()
  {
    try (final Scoped aScoped = new Scoped ())
    {
      final IRequestScope aRequestScope = aScoped.getRequestScope ();
      assertNotNull (aRequestScope);
      assertSame (aRequestScope, ScopeManager.getRequestScope ());
    }
  }

  @Test
  public void testCustomIDs ()
  {
    try (final Scoped aScoped = new Scoped ("my-scope", "my-session"))
    {
      assertNotNull (aScoped.getRequestScope ());
    }
  }

  @Test
  public void testGetRequestScopeAfterClose ()
  {
    @SuppressWarnings ("resource")
    final Scoped aScoped = new Scoped ();
    aScoped.close ();
    try
    {
      aScoped.getRequestScope ();
      fail ();
    }
    catch (final IllegalStateException ex)
    {
      // expected
    }
  }
}
