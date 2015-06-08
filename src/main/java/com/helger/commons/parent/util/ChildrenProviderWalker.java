/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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
package com.helger.commons.parent.util;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.hierarchy.IHierarchyWalkerCallback;
import com.helger.commons.parent.IChildrenProvider;

/**
 * Recursively visit all children provided by a given {@link IChildrenProvider}
 * and call a callback for each visited child.
 *
 * @author Philip Helger
 * @param <CHILDTYPE>
 *        The type of children to walk
 */
@Immutable
public final class ChildrenProviderWalker <CHILDTYPE>
{
  private final IChildrenProvider <CHILDTYPE> m_aChildrenProvider;
  private final IHierarchyWalkerCallback <CHILDTYPE> m_aCallback;

  public ChildrenProviderWalker (@Nonnull final IChildrenProvider <CHILDTYPE> aChildrenProvider,
                                 @Nonnull final IHierarchyWalkerCallback <CHILDTYPE> aCallback)
  {
    m_aChildrenProvider = ValueEnforcer.notNull (aChildrenProvider, "ChildrenProvider");
    m_aCallback = ValueEnforcer.notNull (aCallback, "Callback");
  }

  @Nonnull
  public IChildrenProvider <CHILDTYPE> getChildrenProvider ()
  {
    return m_aChildrenProvider;
  }

  @Nonnull
  public IHierarchyWalkerCallback <CHILDTYPE> getCallback ()
  {
    return m_aCallback;
  }

  private void _walkRecursive (@Nonnull final CHILDTYPE aObject)
  {
    // Only the root level is null -> otherwise we end up in a potentially
    // endless recursion
    ValueEnforcer.notNull (aObject, "Object");

    // prefix insertion
    m_aCallback.onItemBeforeChildren (aObject);

    // call children only if mode is continue
    if (m_aChildrenProvider.hasChildren (aObject))
    {
      // iterate children
      m_aCallback.onLevelDown ();
      try
      {
        for (final CHILDTYPE aChildObject : m_aChildrenProvider.getAllChildren (aObject))
        {
          // recursive call
          _walkRecursive (aChildObject);
        }
      }
      finally
      {
        // callback
        m_aCallback.onLevelUp ();
      }
    }

    // postfix insertion even if prefix iteration failed
    m_aCallback.onItemAfterChildren (aObject);
  }

  public void walk ()
  {
    m_aCallback.begin ();
    try
    {
      // null == root level
      if (m_aChildrenProvider.hasChildren (null))
        for (final CHILDTYPE aRootChild : m_aChildrenProvider.getAllChildren (null))
          _walkRecursive (aRootChild);
    }
    finally
    {
      m_aCallback.end ();
    }
  }

  public void walkSub (@Nonnull final CHILDTYPE aObject)
  {
    ValueEnforcer.notNull (aObject, "Object");

    m_aCallback.begin ();
    try
    {
      _walkRecursive (aObject);
    }
    finally
    {
      m_aCallback.end ();
    }
  }

  public static <CHILDTYPE> void walkProvider (@Nonnull final IChildrenProvider <CHILDTYPE> aChildProvider,
                                               @Nonnull final IHierarchyWalkerCallback <CHILDTYPE> aCallback)
  {
    new ChildrenProviderWalker <CHILDTYPE> (aChildProvider, aCallback).walk ();
  }

  public static <CHILDTYPE> void walkSubProvider (@Nonnull final CHILDTYPE aObject,
                                                  @Nonnull final IChildrenProvider <CHILDTYPE> aChildProvider,
                                                  @Nonnull final IHierarchyWalkerCallback <CHILDTYPE> aCallback)
  {
    new ChildrenProviderWalker <CHILDTYPE> (aChildProvider, aCallback).walkSub (aObject);
  }
}
