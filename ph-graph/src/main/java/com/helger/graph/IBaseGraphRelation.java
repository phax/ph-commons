/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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
package com.helger.graph;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.MustImplementEqualsAndHashcode;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.impl.ICommonsOrderedSet;

/**
 * Base interface for a single undirected graph relation.
 *
 * @author Philip Helger
 * @param <NODETYPE>
 *        Node class
 * @param <RELATIONTYPE>
 *        Relation class
 */
@MustImplementEqualsAndHashcode
public interface IBaseGraphRelation <NODETYPE extends IBaseGraphNode <NODETYPE, RELATIONTYPE>, RELATIONTYPE extends IBaseGraphRelation <NODETYPE, RELATIONTYPE>>
                                    extends IBaseGraphObject
{
  /**
   * Check if this relation is connected to the passed node.
   *
   * @param aNode
   *        The node to be checked. May be <code>null</code>.
   * @return <code>true</code> if the passed node is related via this relation,
   *         <code>false</code> if not.
   */
  boolean isRelatedTo (@Nullable NODETYPE aNode);

  /**
   * @return A list with all connected nodes. Usually 2 elements.
   */
  @Nonnull
  @ReturnsMutableCopy
  ICommonsOrderedSet <NODETYPE> getAllConnectedNodes ();

  /**
   * @return A list with the ID of all connected nodes. Usually 2 elements.
   */
  @Nonnull
  @ReturnsMutableCopy
  ICommonsOrderedSet <String> getAllConnectedNodeIDs ();
}
