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

import com.helger.commons.annotation.MustImplementEqualsAndHashcode;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.impl.ICommonsOrderedSet;

/**
 * Base interface for a single graph node.
 *
 * @author Philip Helger
 * @param <NODETYPE>
 *        Node class
 * @param <RELATIONTYPE>
 *        Relation class
 */
@MustImplementEqualsAndHashcode
public interface IBaseGraphNode <NODETYPE extends IBaseGraphNode <NODETYPE, RELATIONTYPE>, RELATIONTYPE extends IBaseGraphRelation <NODETYPE, RELATIONTYPE>>
                                extends IBaseGraphObject
{
  /**
   * Check if this graph node is directly connected to the passed node, either
   * via an incoming or via an outgoing relation.<br>
   * This is the same as calling
   * <code>isFromNode(aNode) || isToNode(aNode)</code>
   *
   * @param aNode
   *        The node to be checked. May be <code>null</code>.
   * @return <code>true</code> if is connected, <code>false</code> if not
   */
  boolean isConnectedWith (@Nullable NODETYPE aNode);

  /**
   * Find the relation from this node to the passed node.
   *
   * @param aNode
   *        The to node to use. May be <code>null</code>.
   * @return <code>null</code> if there exists no relation between this node and
   *         the passed node.
   */
  @Nullable
  RELATIONTYPE getRelation (@Nullable NODETYPE aNode);

  /**
   * Check if this node has any relations.
   *
   * @return <code>true</code> if this node has at least one incoming or
   *         outgoing relation.
   */
  boolean hasRelations ();

  /**
   * @return A non-negative amount of all incoming and outgoing relations.
   *         Always &ge; 0.
   */
  @Nonnegative
  int getRelationCount ();

  /**
   * @return A container with all incoming and outgoing relations. Never
   *         <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  ICommonsOrderedSet <RELATIONTYPE> getAllRelations ();

  /**
   * @return A container with the IDs of all incoming and outgoing relations.
   *         Never <code>null</code>.
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
   * @return A container with all nodes directly connected to this node's
   *         relations. Never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  ICommonsOrderedSet <NODETYPE> getAllRelatedNodes ();

  /**
   * @return A container with the IDs of all nodes directly connected to this
   *         node's relations. Never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  ICommonsOrderedSet <String> getAllRelatedNodeIDs ();
}
