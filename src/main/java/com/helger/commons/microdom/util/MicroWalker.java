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
package com.helger.commons.microdom.util;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotations.PresentForCodeCoverage;
import com.helger.commons.hierarchy.IHierarchyWalkerCallback;
import com.helger.commons.microdom.IMicroNode;
import com.helger.commons.parent.ChildrenProviderHasChildren;
import com.helger.commons.parent.IChildrenProvider;

/**
 * Helper class that walks an {@link com.helger.commons.microdom.IMicroDocument}
 * or {@link com.helger.commons.microdom.IMicroNode} with a callback.
 *
 * @author Philip Helger
 */
@Immutable
public final class MicroWalker
{
  @PresentForCodeCoverage
  private static final MicroWalker s_aInstance = new MicroWalker ();

  private MicroWalker ()
  {}

  private static <T extends IMicroNode> void _walkNode (@Nonnull final T aNode,
                                                        @Nonnull final IChildrenProvider <T> aChildrenResolver,
                                                        @Nonnull final IHierarchyWalkerCallback <? super T> aCallback)
  {
    aCallback.onItemBeforeChildren (aNode);
    if (aChildrenResolver.hasChildren (aNode))
      for (final T aChildItem : aChildrenResolver.getAllChildren (aNode))
      {
        aCallback.onLevelDown ();
        // recursive call
        _walkNode (aChildItem, aChildrenResolver, aCallback);
        aCallback.onLevelUp ();
      }
    aCallback.onItemAfterChildren (aNode);
  }

  /**
   * Iterate the passed node and invoke the callback for all child nodes. The
   * callback is not invoked for the passed node itself!
   *
   * @param aNode
   *        The node to iterate. May not be <code>null</code>.
   * @param aCallback
   *        The callback to call. May not be <code>null</code>.
   */
  public static void walkNode (@Nonnull final IMicroNode aNode,
                               @Nonnull final IHierarchyWalkerCallback <? super IMicroNode> aCallback)
  {
    walkNode (aNode, new ChildrenProviderHasChildren <IMicroNode> (), aCallback);
  }

  /**
   * Iterate the passed node and invoke the callback for all child nodes. The
   * callback is not invoked for the passed node itself!
   *
   * @param aNode
   *        The node to iterate. May not be <code>null</code>.
   * @param aChildrenResolver
   *        The child resolver to use. May not be <code>null</code>.
   * @param aCallback
   *        The callback to call. May not be <code>null</code>.
   */
  public static <T extends IMicroNode> void walkNode (@Nonnull final T aNode,
                                                      @Nonnull final IChildrenProvider <T> aChildrenResolver,
                                                      @Nonnull final IHierarchyWalkerCallback <? super T> aCallback)
  {
    ValueEnforcer.notNull (aNode, "Node");
    ValueEnforcer.notNull (aCallback, "Callback");

    aCallback.begin ();
    try
    {
      if (aChildrenResolver.hasChildren (aNode))
        for (final T aChildItem : aChildrenResolver.getAllChildren (aNode))
          _walkNode (aChildItem, aChildrenResolver, aCallback);
    }
    finally
    {
      aCallback.end ();
    }
  }
}
