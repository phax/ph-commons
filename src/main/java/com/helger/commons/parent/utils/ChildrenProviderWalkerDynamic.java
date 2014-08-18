/**
 * Copyright (C) 2006-2014 phloc systems (www.phloc.com)
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
package com.helger.commons.parent.utils;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.hierarchy.EHierarchyCallbackReturn;
import com.helger.commons.hierarchy.IHierarchyWalkerDynamicCallback;
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
public final class ChildrenProviderWalkerDynamic <CHILDTYPE>
{
  private final IChildrenProvider <CHILDTYPE> m_aChildrenProvider;
  private final IHierarchyWalkerDynamicCallback <CHILDTYPE> m_aCallback;

  public ChildrenProviderWalkerDynamic (@Nonnull final IChildrenProvider <CHILDTYPE> aChildrenProvider,
                                        @Nonnull final IHierarchyWalkerDynamicCallback <CHILDTYPE> aCallback)
  {
    m_aChildrenProvider = ValueEnforcer.notNull (aChildrenProvider, "ChildrenProvider");
    m_aCallback = ValueEnforcer.notNull (aCallback, "Callback");
  }

  @Nonnull
  private EHierarchyCallbackReturn _walkRecursive (final CHILDTYPE aObject)
  {
    // prefix insertion
    final EHierarchyCallbackReturn eRetPrefix = m_aCallback.onItemBeforeChildren (aObject);

    // call children only if mode is continue
    EHierarchyCallbackReturn eRetChildren = EHierarchyCallbackReturn.CONTINUE;
    if (eRetPrefix == EHierarchyCallbackReturn.CONTINUE && m_aChildrenProvider.hasChildren (aObject))
    {
      // iterate children
      m_aCallback.onLevelDown ();
      try
      {
        for (final CHILDTYPE aChildObject : m_aChildrenProvider.getChildren (aObject))
        {
          // recursive call
          eRetChildren = _walkRecursive (aChildObject);
          if (eRetChildren == EHierarchyCallbackReturn.USE_PARENTS_NEXT_SIBLING)
          {
            // If we don't want the children to be enumerated, break this
            // loop
            // and continue as normal
            eRetChildren = EHierarchyCallbackReturn.CONTINUE;
            break;
          }

          if (eRetChildren == EHierarchyCallbackReturn.STOP_ITERATION)
          {
            // stop iterating and propagate the return code to the root
            break;
          }
        }
      }
      finally
      {
        // callback
        m_aCallback.onLevelUp ();
      }
    }

    // postfix insertion even if prefix iteration failed
    final EHierarchyCallbackReturn eRetPostfix = m_aCallback.onItemAfterChildren (aObject);

    // most stringent first
    if (eRetPrefix == EHierarchyCallbackReturn.STOP_ITERATION ||
        eRetChildren == EHierarchyCallbackReturn.STOP_ITERATION ||
        eRetPostfix == EHierarchyCallbackReturn.STOP_ITERATION)
    {
      // stop complete iteration
      return EHierarchyCallbackReturn.STOP_ITERATION;
    }
    if (eRetPrefix == EHierarchyCallbackReturn.USE_PARENTS_NEXT_SIBLING ||
        eRetChildren == EHierarchyCallbackReturn.USE_PARENTS_NEXT_SIBLING ||
        eRetPostfix == EHierarchyCallbackReturn.USE_PARENTS_NEXT_SIBLING)
    {
      // skip children and siblings
      return EHierarchyCallbackReturn.USE_PARENTS_NEXT_SIBLING;
    }

    // continue
    return EHierarchyCallbackReturn.CONTINUE;
  }

  public void walk ()
  {
    m_aCallback.begin ();
    try
    {
      if (m_aChildrenProvider.hasChildren (null))
        for (final CHILDTYPE aRootChild : m_aChildrenProvider.getChildren (null))
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

  public static <CHILDTYPE> void walkProvider (@Nonnull final IChildrenProvider <CHILDTYPE> aChildrenProvider,
                                               @Nonnull final IHierarchyWalkerDynamicCallback <CHILDTYPE> aCallback)
  {
    new ChildrenProviderWalkerDynamic <CHILDTYPE> (aChildrenProvider, aCallback).walk ();
  }

  public static <CHILDTYPE> void walkSubProvider (@Nonnull final CHILDTYPE aObject,
                                                  @Nonnull final IChildrenProvider <CHILDTYPE> aChildrenProvider,
                                                  @Nonnull final IHierarchyWalkerDynamicCallback <CHILDTYPE> aCallback)
  {
    new ChildrenProviderWalkerDynamic <CHILDTYPE> (aChildrenProvider, aCallback).walkSub (aObject);
  }
}
