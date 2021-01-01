/**
 * Copyright (C) 2014-2021 Philip Helger (www.helger.com)
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

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

import com.helger.commons.annotation.Nonempty;

/**
 * The default implementation of {@link ICache} and {@link IMutableCache}. Since
 * v9.3.8 this class is based on {@link MappedCache}.
 *
 * @author Philip Helger
 * @param <KEYTYPE>
 *        The cache key type
 * @param <VALUETYPE>
 *        The cache value type
 */
@ThreadSafe
public class Cache <KEYTYPE, VALUETYPE> extends MappedCache <KEYTYPE, KEYTYPE, VALUETYPE>
{
  public static final boolean DEFAULT_ALLOW_NULL_VALUES = false;

  public Cache (@Nonnull final Function <KEYTYPE, VALUETYPE> aCacheValueProvider, @Nonnull @Nonempty final String sCacheName)
  {
    this (aCacheValueProvider, NO_MAX_SIZE, sCacheName);
  }

  public Cache (@Nonnull final Function <KEYTYPE, VALUETYPE> aCacheValueProvider,
                final int nMaxSize,
                @Nonnull @Nonempty final String sCacheName)
  {
    this (aCacheValueProvider, nMaxSize, sCacheName, DEFAULT_ALLOW_NULL_VALUES);
  }

  public Cache (@Nonnull final Function <KEYTYPE, VALUETYPE> aCacheValueProvider,
                final int nMaxSize,
                @Nonnull @Nonempty final String sCacheName,
                final boolean bAllowNullValues)
  {
    super (x -> x, aCacheValueProvider, nMaxSize, sCacheName, bAllowNullValues);
  }
}
