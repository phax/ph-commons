/**
 * Copyright (C) 2014-2016 Philip Helger (www.helger.com)
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
import javax.annotation.Nullable;
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
public class ChildrenProviderHierarchyVisitor <CHILDTYPE> implements IHierarchyVisitor <CHILDTYPE>
{
  private final IChildrenProvider <CHILDTYPE> m_aChildrenProvider;
  private final IHierarchyVisitorCallback <? super CHILDTYPE> m_aCallback;

  public ChildrenProviderHierarchyVisitor (@Nonnull final IChildrenProvider <CHILDTYPE> aChildrenProvider,
                                           @Nonnull final IHierarchyVisitorCallback <? super CHILDTYPE> aCallback)
  {
    m_aChildrenProvider = ValueEnforcer.notNull (aChildrenProvider, "ChildrenProvider");
    m_aCallback = ValueEnforcer.notNull (aCallback, "Callback");
  }

  /**
   * @return The children provider as passed in the constructor. Never
   *         <code>null</code>.
   */
  @Nonnull
  public IChildrenProvider <CHILDTYPE> getChildrenProvider ()
  {
    return m_aChildrenProvider;
  }

  /**
   * @return The callback to be executed as passed in the constructor. Never
   *         <code>null</code>.
   */
  @Nonnull
  public IHierarchyVisitorCallback <? super CHILDTYPE> getCallback ()
  {
    return m_aCallback;
  }

  @Nonnull
  private EHierarchyVisitorReturn _visitRecursive (@Nullable final CHILDTYPE aObject)
  {
    // prefix callback
    final EHierarchyVisitorReturn eRetPrefix = m_aCallback.onItemBeforeChildren (aObject);

    // call children only if mode is continue
    EHierarchyVisitorReturn eRetChildren = EHierarchyVisitorReturn.CONTINUE;

    // If eRetPrefix is USE_NEXT_SIBLING, simply continue!
    if (eRetPrefix.isContinue () && m_aChildrenProvider.hasChildren (aObject))
    {
      // Callback on descend
      m_aCallback.onLevelDown ();
      try
      {
        // iterate children
        for (final CHILDTYPE aChildObject : m_aChildrenProvider.getAllChildren (aObject))
        {
          // recursive call
          eRetChildren = _visitRecursive (aChildObject);
          if (eRetChildren == EHierarchyVisitorReturn.USE_PARENTS_NEXT_SIBLING)
          {
            // If we don't want the children to be enumerated, break this
            // loop and continue as normal
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

    // postfix callback even if prefix iteration failed
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

  public void visit (@Nullable final CHILDTYPE aStartObject, final boolean bInvokeOnStartObject)
  {
    m_aCallback.begin ();
    try
    {
      if (bInvokeOnStartObject)
      {
        // Include the start object
        _visitRecursive (aStartObject);
      }
      else
      {
        // Use only the children of the start object
        if (m_aChildrenProvider.hasChildren (aStartObject))
          for (final CHILDTYPE aRootChild : m_aChildrenProvider.getAllChildren (aStartObject))
          {
            final EHierarchyVisitorReturn eRet = _visitRecursive (aRootChild);
            if (!eRet.isContinue ())
              break;
          }
      }
    }
    finally
    {
      m_aCallback.end ();
    }
  }

  public static <CHILDTYPE extends IHasChildren <CHILDTYPE>> void visitAll (@Nonnull final IHierarchyVisitorCallback <? super CHILDTYPE> aCallback,
                                                                            final boolean bInvokeOnStartObject)
  {
    visitAll (new ChildrenProviderHasChildren <CHILDTYPE> (), aCallback, bInvokeOnStartObject);
  }

  public static <CHILDTYPE> void visitAll (@Nonnull final IChildrenProvider <CHILDTYPE> aChildrenProvider,
                                           @Nonnull final IHierarchyVisitorCallback <? super CHILDTYPE> aCallback,
                                           final boolean bInvokeOnStartObject)
  {
    new ChildrenProviderHierarchyVisitor <CHILDTYPE> (aChildrenProvider, aCallback).visit (null, bInvokeOnStartObject);
  }

  public static <CHILDTYPE extends IHasChildren <CHILDTYPE>> void visitFrom (@Nullable final CHILDTYPE aStartObject,
                                                                             @Nonnull final IHierarchyVisitorCallback <? super CHILDTYPE> aCallback,
                                                                             final boolean bInvokeOnStartObject)
  {
    visitFrom (aStartObject, new ChildrenProviderHasChildren <CHILDTYPE> (), aCallback, bInvokeOnStartObject);
  }

  public static <CHILDTYPE> void visitFrom (@Nullable final CHILDTYPE aStartObject,
                                            @Nonnull final IChildrenProvider <CHILDTYPE> aChildrenProvider,
                                            @Nonnull final IHierarchyVisitorCallback <? super CHILDTYPE> aCallback,
                                            final boolean bInvokeOnStartObject)
  {
    new ChildrenProviderHierarchyVisitor <CHILDTYPE> (aChildrenProvider, aCallback).visit (aStartObject,
                                                                                           bInvokeOnStartObject);
  }
}
