/**
 * Copyright (C) 2014 Philip Helger (www.helger.com)
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
package com.helger.commons.cache;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.state.EChange;

/**
 * Implementation of the {@link SimpleCacheMBean} interface. Is a wrapper around
 * an {@link AbstractCache}.
 * 
 * @author Philip Helger
 */
final class SimpleCache implements SimpleCacheMBean
{
  private final AbstractCache <?, ?> m_aCache;

  public SimpleCache (@Nonnull final AbstractCache <?, ?> aCache)
  {
    m_aCache = ValueEnforcer.notNull (aCache, "Cache");
  }

  @Nonnegative
  public int size ()
  {
    return m_aCache.size ();
  }

  public boolean isEmpty ()
  {
    return m_aCache.isEmpty ();
  }

  @Nonnull
  public EChange clearCache ()
  {
    return m_aCache.clearCache ();
  }
}
