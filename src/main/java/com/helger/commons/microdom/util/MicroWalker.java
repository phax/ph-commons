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

import com.helger.commons.annotation.PresentForCodeCoverage;
import com.helger.commons.hierarchy.IChildrenProvider;
import com.helger.commons.hierarchy.visit.ChildrenProviderVisitor;
import com.helger.commons.hierarchy.visit.IHierarchyVisitorCallback;
import com.helger.commons.microdom.IMicroNode;

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
                               @Nonnull final IHierarchyVisitorCallback <? super IMicroNode> aCallback)
  {
    ChildrenProviderVisitor.visitAllFrom (aNode, aCallback);
  }

  /**
   * Iterate the passed node and invoke the callback for all child nodes. The
   * callback is not invoked for the passed node itself!
   *
   * @param aNode
   *        The node to iterate. May not be <code>null</code>.
   * @param aChildrenProvider
   *        The child resolver to use. May not be <code>null</code>.
   * @param aCallback
   *        The callback to call. May not be <code>null</code>.
   */
  public static <T extends IMicroNode> void walkNode (@Nonnull final T aNode,
                                                      @Nonnull final IChildrenProvider <T> aChildrenProvider,
                                                      @Nonnull final IHierarchyVisitorCallback <? super T> aCallback)
  {
    ChildrenProviderVisitor.visitAllFrom (aNode, aChildrenProvider, aCallback);
  }
}
