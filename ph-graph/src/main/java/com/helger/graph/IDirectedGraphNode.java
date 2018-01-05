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
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.collection.impl.ICommonsSet;

/**
 * Base interface for graph node implementations.
 *
 * @author Philip Helger
 * @param <NODETYPE>
 *        Directed node class
 * @param <RELATIONTYPE>
 *        Directed relation class
 */
@MustImplementEqualsAndHashcode
public interface IDirectedGraphNode <NODETYPE extends IDirectedGraphNode <NODETYPE, RELATIONTYPE>, RELATIONTYPE extends IDirectedGraphRelation <NODETYPE, RELATIONTYPE>>
                                    extends
                                    IBaseGraphNode <NODETYPE, RELATIONTYPE>
{
  /**
   * @return <code>true</code> if this node has at least one incoming relation.
   */
  boolean hasIncomingRelations ();

  /**
   * @return The number of incoming relations. Always &ge; 0.
   */
  @Nonnegative
  int getIncomingRelationCount ();

  /**
   * Check if this node has the passed relation as an incoming relations.
   *
   * @param aRelation
   *        The relation to be checked. May be <code>null</code>.
   * @return <code>true</code> if the passed relation is an incoming relation,
   *         <code>false</code> if not
   */
  boolean isIncomingRelation (@Nullable RELATIONTYPE aRelation);

  /**
   * @return All incoming relations and never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  ICommonsList <RELATIONTYPE> getAllIncomingRelations ();

  /**
   * Iterate each incoming relation calling the provided consumer with the
   * relation object.
   *
   * @param aConsumer
   *        The consumer to be invoked. May not be <code>null</code>. May only
   *        perform reading operations!
   */
  void forEachIncomingRelation (@Nonnull Consumer <? super RELATIONTYPE> aConsumer);

  /**
   * Check if this graph node is directly connected to the passed node via an
   * incoming relation.
   *
   * @param aNode
   *        The node to be checked. May be <code>null</code>.
   * @return <code>true</code> if is connected, <code>false</code> if not
   */
  boolean isFromNode (@Nullable NODETYPE aNode);

  /**
   * @return All nodes that are connected via incoming relations.
   */
  @Nonnull
  @ReturnsMutableCopy
  ICommonsSet <NODETYPE> getAllFromNodes ();

  /**
   * Find the incoming relation from the passed node to this node.
   *
   * @param aFromNode
   *        The from node to use. May be <code>null</code>.
   * @return <code>null</code> if there exists no incoming relation from the
   *         passed node to this node.
   */
  @Nullable
  RELATIONTYPE getIncomingRelationFrom (@Nullable NODETYPE aFromNode);

  // --- outgoing ---

  /**
   * @return <code>true</code> if this node has at least one outgoing relation.
   */
  boolean hasOutgoingRelations ();

  /**
   * @return The number of outgoing relations. Always &ge; 0.
   */
  @Nonnegative
  int getOutgoingRelationCount ();

  /**
   * Check if this node has the passed relation as an outgoing relations.
   *
   * @param aRelation
   *        The relation to be checked. May be <code>null</code>.
   * @return <code>true</code> if the passed relation is an outgoing relation,
   *         <code>false</code> if not
   */
  boolean isOutgoingRelation (@Nullable RELATIONTYPE aRelation);

  /**
   * @return All outgoing relations and never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  ICommonsList <RELATIONTYPE> getAllOutgoingRelations ();

  /**
   * Iterate each outgoing relation calling the provided consumer with the
   * relation object.
   *
   * @param aConsumer
   *        The consumer to be invoked. May not be <code>null</code>. May only
   *        perform reading operations!
   */
  void forEachOutgoingRelation (@Nonnull Consumer <? super RELATIONTYPE> aConsumer);

  /**
   * Check if this graph node is directly connected to the passed node via an
   * outgoing relation.
   *
   * @param aNode
   *        The node to be checked. May be <code>null</code>.
   * @return <code>true</code> if is connected, <code>false</code> if not
   */
  boolean isToNode (@Nullable NODETYPE aNode);

  /**
   * @return All nodes that are connected via outgoing relations.
   */
  @Nonnull
  @ReturnsMutableCopy
  ICommonsSet <NODETYPE> getAllToNodes ();

  /**
   * Find the incoming relation from this node to the passed node.
   *
   * @param aToNode
   *        The to node to use. May be <code>null</code>.
   * @return <code>null</code> if there exists no incoming relation from this
   *         node to the passed node.
   */
  @Nullable
  RELATIONTYPE getOutgoingRelationTo (@Nullable NODETYPE aToNode);

  // --- incoming and/or outgoing

  /**
   * Check if this node has incoming <b>or</b> outgoing relations. This is equal
   * to calling <code>hasIncomingRelations() || hasOutgoingRelations()</code>
   *
   * @return <code>true</code> if this node has at least one incoming or
   *         outgoing relation.
   */
  default boolean hasIncomingOrOutgoingRelations ()
  {
    return hasIncomingRelations () || hasOutgoingRelations ();
  }

  /**
   * Check if this node has incoming <b>and</b> outgoing relations. This is
   * equal to calling
   * <code>hasIncomingRelations() &amp;&amp; hasOutgoingRelations()</code>
   *
   * @return <code>true</code> if this node has at least one incoming and at
   *         least one outgoing relation.
   */
  default boolean hasIncomingAndOutgoingRelations ()
  {
    return hasIncomingRelations () && hasOutgoingRelations ();
  }

  default boolean hasRelations ()
  {
    return hasIncomingOrOutgoingRelations ();
  }

  @Nonnegative
  default int getRelationCount ()
  {
    return getIncomingRelationCount () + getOutgoingRelationCount ();
  }

  default void forEachRelation (@Nonnull final Consumer <? super RELATIONTYPE> aConsumer)
  {
    forEachIncomingRelation (aConsumer);
    forEachOutgoingRelation (aConsumer);
  }
}
