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
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.scope.IScope;

/**
 * Mock implementation of {@link AbstractSessionSingleton}.
 *
 * @author Philip Helger
 */
public final class MockSessionSingletonWithScopeCtor extends AbstractSessionSingleton
{
  private int i = 0;
  private final IScope m_aScope;

  @Deprecated (forRemoval = false)
  @UsedViaReflection
  public MockSessionSingletonWithScopeCtor (@Nonnull final IScope aScope)
  {
    m_aScope = ValueEnforcer.notNull (aScope, "Scope");
  }

  @Nonnull
  public static MockSessionSingletonWithScopeCtor getInstance ()
  {
    return getSessionSingleton (MockSessionSingletonWithScopeCtor.class);
  }

  public void inc ()
  {
    i++;
  }

  public int get ()
  {
    return i;
  }

  @Nonnull
  public IScope getScope ()
  {
    return m_aScope;
  }

  // For serialization testing!
  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    return i == ((MockSessionSingletonWithScopeCtor) o).i;
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (i).getHashCode ();
  }
}
