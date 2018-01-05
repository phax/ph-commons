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

import java.util.function.Consumer;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.collection.impl.ICommonsOrderedMap;
import com.helger.commons.collection.impl.ICommonsOrderedSet;
import com.helger.commons.state.IClearable;
import com.helger.matrix.Matrix;

/**
 * Base interface for a read-only graph.
 *
 * @author Philip Helger
 * @param <NODETYPE>
 *        Node class
 * @param <RELATIONTYPE>
 *        Relation class
 */
public interface IBaseGraph <NODETYPE extends IBaseGraphNode <NODETYPE, RELATIONTYPE>, RELATIONTYPE extends IBaseGraphRelation <NODETYPE, RELATIONTYPE>>
                            extends
                            IBaseGraphObject,
                            IClearable
{
  /**
   * @return The number of nodes currently in the graph. Always &ge; 0.
   */
  @Nonnegative
  int getNodeCount ();

  /**
   * Find the graph node with the specified ID.
   *
   * @param sID
   *        The ID to be searched. Maybe <code>null</code>.
   * @return <code>null</code> if no such graph node exists in this graph.
   */
  @Nullable
  NODETYPE getNodeOfID (@Nullable String sID);

  /**
   * @return A non-<code>null</code> collection of the nodes in this graph, in
   *         arbitrary order!
   */
  @Nonnull
  @ReturnsMutableCopy
  ICommonsOrderedMap <String, NODETYPE> getAllNodes ();

  /**
   * @return A non-<code>null</code> set of all the node IDs in this graph, in
   *         arbitrary order!
   */
  @Nonnull
  @ReturnsMutableCopy
  ICommonsOrderedSet <String> getAllNodeIDs ();

  /**
   * Iterate each node calling the provided consumer with the node object.
   *
   * @param aConsumer
   *        The consumer to be invoked. May not be <code>null</code>. May only
   *        perform reading operations!
   */
  void forEachNode (@Nonnull Consumer <? super NODETYPE> aConsumer);

  /**
   * @return A non-<code>null</code> map of the relations in this graph, in
   *         arbitrary order!
   */
  @Nonnull
  @ReturnsMutableCopy
  ICommonsOrderedMap <String, RELATIONTYPE> getAllRelations ();

  /**
   * @return A non-<code>null</code> list of the relations in this graph, in
   *         arbitrary order!
   */
  @Nonnull
  @ReturnsMutableCopy
  ICommonsList <RELATIONTYPE> getAllRelationObjs ();

  /**
   * @return A non-<code>null</code> set of all the relation IDs in this graph,
   *         in arbitrary order!
   */
  @Nonnull
  @ReturnsMutableCopy
  ICommonsOrderedSet <String> getAllRelationIDs ();

  /**
   * Iterate each relation calling the provided consumer with the relation
   * object.
   *
   * @param aConsumer
   *        The consumer to be invoked. May not be <code>null</code>. May only
   *        perform reading operations!
   */
  void forEachRelation (@Nonnull Consumer <? super RELATIONTYPE> aConsumer);

  /**
   * Check if this graph contains cycles. An example for a cycle is e.g. if
   * <code>NodeA</code> has an outgoing relation to <code>NodeB</code>,
   * <code>NodeB</code> has an outgoing relation to <code>NodeC</code> and
   * finally <code>NodeC</code> has an outgoing relation to <code>NodeA</code>.
   *
   * @return <code>true</code> if this graph contains at least one cycle,
   *         <code>false</code> if this graph is cycle-free.
   */
  boolean containsCycles ();

  /**
   * Check if this graph is completely self contained. As relations between
   * nodes do not check whether both nodes belong to the same graph it is
   * possible to link different graphs together with relations. This method
   * returns true, if all nodes referenced from all relations link to objects
   * inside this graph.
   *
   * @return <code>true</code> if this graph is self contained,
   *         <code>false</code> if not.
   */
  boolean isSelfContained ();

  /**
   * @return A new incidence matrix (Symmetric matrix where 1/-1 is set if a
   *         relation is present, 0 if no relation is present; Number of rows
   *         and columns is equal to the number of nodes).
   * @throws IllegalArgumentException
   *         If this graph contains no node
   */
  @Nonnull
  Matrix createIncidenceMatrix ();
}
