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
package com.helger.commons.hierarchy.visit;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.hierarchy.ChildrenProviderHasChildren;
import com.helger.commons.hierarchy.IChildrenProvider;
import com.helger.commons.hierarchy.IHasChildren;

/**
 * Recursively visit all children provided by a given {@link IChildrenProvider}
 * and call a callback for each visited child.
 *
 * @author Philip Helger
 * @param <CHILDTYPE>
 *        The type of children to visit
 */
@Immutable
public class ChildrenProviderVisitorDynamic <CHILDTYPE> implements IVisitor <CHILDTYPE>
{
  private final IChildrenProvider <CHILDTYPE> m_aChildrenProvider;
  private final IHierarchyVisitorDynamicCallback <? super CHILDTYPE> m_aCallback;

  public ChildrenProviderVisitorDynamic (@Nonnull final IChildrenProvider <CHILDTYPE> aChildrenProvider,
                                         @Nonnull final IHierarchyVisitorDynamicCallback <? super CHILDTYPE> aCallback)
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
  public IHierarchyVisitorDynamicCallback <? super CHILDTYPE> getCallback ()
  {
    return m_aCallback;
  }

  @Nonnull
  private EHierarchyVisitorReturn _visitRecursive (final CHILDTYPE aObject)
  {
    // prefix insertion
    final EHierarchyVisitorReturn eRetPrefix = m_aCallback.onItemBeforeChildren (aObject);

    // call children only if mode is continue
    EHierarchyVisitorReturn eRetChildren = EHierarchyVisitorReturn.CONTINUE;
    if (eRetPrefix == EHierarchyVisitorReturn.CONTINUE && m_aChildrenProvider.hasChildren (aObject))
    {
      // iterate children
      m_aCallback.onLevelDown ();
      try
      {
        for (final CHILDTYPE aChildObject : m_aChildrenProvider.getAllChildren (aObject))
        {
          // recursive call
          eRetChildren = _visitRecursive (aChildObject);
          if (eRetChildren == EHierarchyVisitorReturn.USE_PARENTS_NEXT_SIBLING)
          {
            // If we don't want the children to be enumerated, break this
            // loop
            // and continue as normal
            eRetChildren = EHierarchyVisitorReturn.CONTINUE;
            break;
          }

          if (eRetChildren == EHierarchyVisitorReturn.STOP_ITERATION)
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
    final EHierarchyVisitorReturn eRetPostfix = m_aCallback.onItemAfterChildren (aObject);

    // most stringent first
    if (eRetPrefix == EHierarchyVisitorReturn.STOP_ITERATION ||
        eRetChildren == EHierarchyVisitorReturn.STOP_ITERATION ||
        eRetPostfix == EHierarchyVisitorReturn.STOP_ITERATION)
    {
      // stop complete iteration
      return EHierarchyVisitorReturn.STOP_ITERATION;
    }
    if (eRetPrefix == EHierarchyVisitorReturn.USE_PARENTS_NEXT_SIBLING ||
        eRetChildren == EHierarchyVisitorReturn.USE_PARENTS_NEXT_SIBLING ||
        eRetPostfix == EHierarchyVisitorReturn.USE_PARENTS_NEXT_SIBLING)
    {
      // skip children and siblings
      return EHierarchyVisitorReturn.USE_PARENTS_NEXT_SIBLING;
    }

    // continue
    return EHierarchyVisitorReturn.CONTINUE;
  }

  public void visitAll ()
  {
    m_aCallback.begin ();
    try
    {
      if (m_aChildrenProvider.hasChildren (null))
        for (final CHILDTYPE aRootChild : m_aChildrenProvider.getAllChildren (null))
          _visitRecursive (aRootChild);
    }
    finally
    {
      m_aCallback.end ();
    }
  }

  public void visitAllFrom (@Nonnull final CHILDTYPE aObject)
  {
    ValueEnforcer.notNull (aObject, "Object");

    m_aCallback.begin ();
    try
    {
      _visitRecursive (aObject);
    }
    finally
    {
      m_aCallback.end ();
    }
  }

  public static <CHILDTYPE extends IHasChildren <CHILDTYPE>> void visitAll (@Nonnull final IHierarchyVisitorDynamicCallback <? super CHILDTYPE> aCallback)
  {
    visitAll (new ChildrenProviderHasChildren <CHILDTYPE> (), aCallback);
  }

  public static <CHILDTYPE> void visitAll (@Nonnull final IChildrenProvider <CHILDTYPE> aChildrenProvider,
                                           @Nonnull final IHierarchyVisitorDynamicCallback <? super CHILDTYPE> aCallback)
  {
    new ChildrenProviderVisitorDynamic <CHILDTYPE> (aChildrenProvider, aCallback).visitAll ();
  }

  public static <CHILDTYPE extends IHasChildren <CHILDTYPE>> void visitAllFrom (@Nonnull final CHILDTYPE aObject,
                                                                                @Nonnull final IHierarchyVisitorDynamicCallback <? super CHILDTYPE> aCallback)
  {
    visitAllFrom (aObject, new ChildrenProviderHasChildren <CHILDTYPE> (), aCallback);
  }

  public static <CHILDTYPE> void visitAllFrom (@Nonnull final CHILDTYPE aObject,
                                               @Nonnull final IChildrenProvider <CHILDTYPE> aChildrenProvider,
                                               @Nonnull final IHierarchyVisitorDynamicCallback <? super CHILDTYPE> aCallback)
  {
    new ChildrenProviderVisitorDynamic <CHILDTYPE> (aChildrenProvider, aCallback).visitAllFrom (aObject);
  }
}
