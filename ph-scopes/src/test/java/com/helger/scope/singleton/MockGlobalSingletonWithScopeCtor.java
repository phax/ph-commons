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
package com.helger.scope.singleton;

import javax.annotation.Nonnull;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.UsedViaReflection;
import com.helger.scope.IScope;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Mock implementation of {@link AbstractGlobalSingleton}
 *
 * @author Philip Helger
 */
public final class MockGlobalSingletonWithScopeCtor extends AbstractGlobalSingleton
{
  private static int s_nCtorCount = 0;
  private static int s_nDtorCount = 0;
  private final IScope m_aScope;

  @Deprecated (forRemoval = false)
  @UsedViaReflection
  @SuppressFBWarnings ("ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD")
  public MockGlobalSingletonWithScopeCtor (@Nonnull final IScope aScope)
  {
    m_aScope = ValueEnforcer.notNull (aScope, "Scope");
    s_nCtorCount++;
  }

  @Nonnull
  public static MockGlobalSingletonWithScopeCtor getInstance ()
  {
    return getGlobalSingleton (MockGlobalSingletonWithScopeCtor.class);
  }

  @Override
  protected void onDestroy (@Nonnull final IScope aScopeInDestruction) throws Exception
  {
    if (!m_aScope.equals (aScopeInDestruction))
      throw new IllegalStateException ("Saved scope and scope in destruction don't match!");
    s_nDtorCount++;
  }

  @Nonnull
  public IScope getScope ()
  {
    return m_aScope;
  }

  protected static int getCtorCount ()
  {
    return s_nCtorCount;
  }

  protected static int getDtorCount ()
  {
    return s_nDtorCount;
  }
}
