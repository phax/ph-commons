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

import com.helger.scope.singleton.AbstractSessionSingleton;
import com.helger.tree.withid.unique.DefaultTreeWithGlobalUniqueID;

import jakarta.annotation.Nonnull;

/**
 * Represents a global singleton tree with a unique ID. It basically is a
 * {@link AbstractSessionSingleton} wrapping a {@link DefaultTreeWithGlobalUniqueID} with the same
 * API.
 *
 * @author Philip Helger
 * @param <KEYTYPE>
 *        Tree key type
 * @param <VALUETYPE>
 *        Tree value type
 */
public abstract class AbstractSessionSingletonTreeWithUniqueID <KEYTYPE, VALUETYPE> extends AbstractSessionSingleton
                                                               implements
                                                               ITreeWithUniqueIDProxy <KEYTYPE, VALUETYPE>
{
  protected final DefaultTreeWithGlobalUniqueID <KEYTYPE, VALUETYPE> m_aTree = new DefaultTreeWithGlobalUniqueID <> ();

  protected AbstractSessionSingletonTreeWithUniqueID ()
  {}

  @Nonnull
  public final DefaultTreeWithGlobalUniqueID <KEYTYPE, VALUETYPE> getProxyTree ()
  {
    return m_aTree;
  }
}
