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

import com.helger.commons.state.EChange;

/**
 * Interface for a modifiable undirected graph.
 *
 * @author Philip Helger
 */
public interface IMutableGraph extends
                               IMutableBaseGraph <IMutableGraphNode, IMutableGraphRelation>,
                               IMutableGraphObjectFactory
{
  /**
   * Allow or disallow that {@link #addNode(IMutableGraphNode)} and
   * {@link #removeNode(IMutableGraphNode)} can handle graph nodes that are
   * already connected.
   *
   * @param bAllow
   *        if <code>true</code> it is allowed to add and remove nodes that
   *        already have incoming or outgoing relations.
   */
  void setChangingConnectedObjectsAllowed (boolean bAllow);

  /**
   * @return The current state, whether changing connected objects is allowed or
   *         not. The default value should be <code>true</code> for backward
   *         compatibility.
   * @see #setChangingConnectedObjectsAllowed(boolean)
   */
  boolean isChangingConnectedObjectsAllowed ();

  /**
   * Add an existing node to this graph.
   *
   * @param aNode
   *        The node to be added. May not be <code>null</code>.
   * @return {@link EChange}
   * @throws IllegalArgumentException
   *         If the node to be added already has incoming or outgoing relations,
   *         and {@link #isChangingConnectedObjectsAllowed()} returned
   *         <code>false</code>
   * @see #setChangingConnectedObjectsAllowed(boolean)
   */
  @Nonnull
  EChange addNode (@Nonnull IMutableGraphNode aNode);

  /**
   * Remove an existing node from the graph. <br>
   * Important note: existing relations are not altered when this method is
   * called, so it may be possible that existing relations pointing to that
   * object therefore reference a node that is no longer in the graph!
   *
   * @param aNode
   *        The node to be removed. May not be <code>null</code>.
   * @return {@link EChange}
   * @throws IllegalArgumentException
   *         If the node to be removed already has incoming or outgoing
   *         relations, and {@link #isChangingConnectedObjectsAllowed()}
   *         returned <code>false</code>
   * @see #setChangingConnectedObjectsAllowed(boolean)
   */
  @Nonnull
  EChange removeNode (@Nonnull IMutableGraphNode aNode);

  /**
   * Remove the passed relation from the graph.
   *
   * @param aRelation
   *        The relation to be removed. May be <code>null</code>.
   * @return {@link EChange#CHANGED} if the relation was at least removed from
   *         the from- or the to-node
   */
  @Nonnull
  EChange removeRelation (@Nullable IMutableGraphRelation aRelation);
}
