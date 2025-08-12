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
package com.helger.commons.cache;

import java.util.function.Function;

import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.commons.builder.IBuilder;
import com.helger.commons.string.StringHelper;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * Builder class for {@link Cache} objects.
 *
 * @author Philip Helger
 * @param <KEYTYPE>
 *        The cache key type
 * @param <VALUETYPE>
 *        The cache value type
 * @since v12.0.0
 */
@NotThreadSafe
public class CacheBuilder <KEYTYPE, VALUETYPE> implements IBuilder <Cache <KEYTYPE, VALUETYPE>>
{
  private Function <KEYTYPE, VALUETYPE> m_aValueProvider;
  private int m_nMaxSize = MappedCache.NO_MAX_SIZE;
  private String m_sName;
  private boolean m_bAllowNullValues = Cache.DEFAULT_ALLOW_NULL_VALUES;

  public CacheBuilder ()
  {}

  @Nonnull
  public CacheBuilder <KEYTYPE, VALUETYPE> valueProvider (@Nullable final Function <KEYTYPE, VALUETYPE> a)
  {
    m_aValueProvider = a;
    return this;
  }

  @Nonnull
  public CacheBuilder <KEYTYPE, VALUETYPE> maxSize (final int n)
  {
    m_nMaxSize = n;
    return this;
  }

  @Nonnull
  public CacheBuilder <KEYTYPE, VALUETYPE> name (@Nullable final String s)
  {
    m_sName = s;
    return this;
  }

  @Nonnull
  public CacheBuilder <KEYTYPE, VALUETYPE> allowNullValues (final boolean b)
  {
    m_bAllowNullValues = b;
    return this;
  }

  @Nonnull
  public Cache <KEYTYPE, VALUETYPE> build ()
  {
    if (m_aValueProvider == null)
      throw new IllegalStateException ("The mandatory Cache Value Provider is missing");
    if (StringHelper.hasNoText (m_sName))
      throw new IllegalStateException ("The mandatory Cache Name is missing");

    return new Cache <> (m_aValueProvider, m_nMaxSize, m_sName, m_bAllowNullValues);
  }
}
