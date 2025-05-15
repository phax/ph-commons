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

import com.helger.annotation.Nonnull;
import com.helger.annotation.misc.UsedViaReflection;
import com.helger.scope.IScope;

/**
 * Mock implementation of {@link AbstractGlobalSingleton}
 *
 * @author Philip Helger
 */
public final class MockGlobalSingleton extends AbstractGlobalSingleton
{
  private static int s_nCtorCount = 0;
  private static int s_nDtorCount = 0;

  @Deprecated (forRemoval = false)
  @UsedViaReflection
  public MockGlobalSingleton ()
  {
    s_nCtorCount++;
  }

  @Nonnull
  public static MockGlobalSingleton getInstance ()
  {
    return getGlobalSingleton (MockGlobalSingleton.class);
  }

  @Override
  protected void onDestroy (@Nonnull final IScope aScopeInDestruction) throws Exception
  {
    s_nDtorCount++;
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
