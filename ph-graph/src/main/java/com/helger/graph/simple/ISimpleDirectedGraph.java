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
package com.helger.graph.simple;

import javax.annotation.Nonnull;

import com.helger.commons.annotation.Nonempty;
import com.helger.graph.IMutableDirectedGraph;
import com.helger.graph.IMutableDirectedGraphRelation;

/**
 * Interface for a modifiable simple graph.
 *
 * @author Philip Helger
 */
public interface ISimpleDirectedGraph extends IMutableDirectedGraph
{
  /**
   * Create a new relation from the passed fromNode to the toNode. Internally
   * the IDs are resolved to the respective graph nodes and later on calls
   * {@link #createRelation(com.helger.graph.IMutableDirectedGraphNode, com.helger.graph.IMutableDirectedGraphNode)}
   *
   * @param sFromNodeID
   *        The from-node ID. May not be <code>null</code>.
   * @param sToNodeID
   *        The to-node ID. May not be <code>null</code>.
   * @return The created graph relation and never <code>null</code>.
   */
  @Nonnull
  IMutableDirectedGraphRelation createRelation (@Nonnull String sFromNodeID, @Nonnull String sToNodeID);

  /**
   * Create a new relation from the passed fromNode to the toNode. Internally
   * the IDs are resolved to the respective graph nodes and later on calls
   * {@link #createRelation(com.helger.graph.IMutableDirectedGraphNode, com.helger.graph.IMutableDirectedGraphNode)}
   *
   * @param sRelationID
   *        The relation ID to be used. May not be <code>null</code>.
   * @param sFromNodeID
   *        The from-node ID. May not be <code>null</code>.
   * @param sToNodeID
   *        The to-node ID. May not be <code>null</code>.
   * @return The created graph relation and never <code>null</code>.
   */
  @Nonnull
  IMutableDirectedGraphRelation createRelation (@Nonnull @Nonempty String sRelationID,
                                                @Nonnull String sFromNodeID,
                                                @Nonnull String sToNodeID);
}
