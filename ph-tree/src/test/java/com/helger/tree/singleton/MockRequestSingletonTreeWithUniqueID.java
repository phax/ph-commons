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
package com.helger.tree.singleton;

import com.helger.annotation.style.UsedViaReflection;
import com.helger.base.hashcode.HashCodeGenerator;

import jakarta.annotation.Nonnull;

public final class MockRequestSingletonTreeWithUniqueID extends
                                                        AbstractRequestSingletonTreeWithUniqueID <String, String>
{
  @Deprecated (forRemoval = false)
  @UsedViaReflection
  public MockRequestSingletonTreeWithUniqueID ()
  {}

  @Nonnull
  public static MockRequestSingletonTreeWithUniqueID getInstance ()
  {
    return getRequestSingleton (MockRequestSingletonTreeWithUniqueID.class);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    return o instanceof MockRequestSingletonTreeWithUniqueID;
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).getHashCode ();
  }
}
