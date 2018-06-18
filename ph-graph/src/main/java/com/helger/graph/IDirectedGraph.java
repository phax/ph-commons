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

import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.impl.ICommonsSet;

/**
 * Interface for a directed graph.
 *
 * @author Philip Helger
 * @param <NODETYPE>
 *        Directed node class
 * @param <RELATIONTYPE>
 *        Directed relation class
 */
public interface IDirectedGraph <NODETYPE extends IDirectedGraphNode <NODETYPE, RELATIONTYPE>, RELATIONTYPE extends IDirectedGraphRelation <NODETYPE, RELATIONTYPE>>
                                extends
                                IBaseGraph <NODETYPE, RELATIONTYPE>
{
  /**
   * Try to retrieve the single start node of this graph. A start node is
   * identified by having no incoming relations.
   *
   * @return The single start node and never <code>null</code>.
   * @throws IllegalStateException
   *         In case the graph has no or more than one start node.
   */
  @Nonnull
  NODETYPE getSingleStartNode ();

  /**
   * Get all start nodes of this graph. Start nodes are identified by having no
   * incoming relations.
   *
   * @return A set with all start nodes. Never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  ICommonsSet <NODETYPE> getAllStartNodes ();

  /**
   * Try to retrieve the single end node of this graph. An end node is
   * identified by having no outgoing relations.
   *
   * @return The single end node and never <code>null</code>.
   * @throws IllegalStateException
   *         In case the graph has no or more than one end node.
   */
  @Nonnull
  NODETYPE getSingleEndNode ();

  /**
   * Get all end nodes of this graph. End nodes are identified by having no
   * outgoing relations.
   *
   * @return A set with all end nodes. Never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  ICommonsSet <NODETYPE> getAllEndNodes ();
}
