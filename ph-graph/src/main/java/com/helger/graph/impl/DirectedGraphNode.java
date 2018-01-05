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
package com.helger.graph.impl;

import java.util.function.Consumer;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.CommonsHashSet;
import com.helger.commons.collection.impl.CommonsLinkedHashMap;
import com.helger.commons.collection.impl.CommonsLinkedHashSet;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.collection.impl.ICommonsOrderedMap;
import com.helger.commons.collection.impl.ICommonsOrderedSet;
import com.helger.commons.collection.impl.ICommonsSet;
import com.helger.commons.state.EChange;
import com.helger.commons.string.ToStringGenerator;
import com.helger.graph.IMutableDirectedGraphNode;
import com.helger.graph.IMutableDirectedGraphRelation;

/**
 * Default implementation if the {@link IMutableDirectedGraphNode} interface
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class DirectedGraphNode extends AbstractBaseGraphObject implements IMutableDirectedGraphNode
{
  private ICommonsOrderedMap <String, IMutableDirectedGraphRelation> m_aIncoming;
  private ICommonsOrderedMap <String, IMutableDirectedGraphRelation> m_aOutgoing;

  public DirectedGraphNode ()
  {
    this (null);
  }

  public DirectedGraphNode (@Nullable final String sID)
  {
    super (sID);
  }

  public final boolean isDirected ()
  {
    return true;
  }

  public void addIncomingRelation (@Nonnull final IMutableDirectedGraphRelation aNewRelation)
  {
    ValueEnforcer.notNull (aNewRelation, "NewRelation");
    ValueEnforcer.isTrue (aNewRelation.getTo () == this, "Passed incoming relation is not based on this node");
    if (m_aIncoming != null)
    {
      if (m_aIncoming.containsKey (aNewRelation.getID ()))
        throw new IllegalArgumentException ("The passed relation (" +
                                            aNewRelation +
                                            ") is already contained as an incoming relation");

      // check if the relation from-node is already contained
      for (final IMutableDirectedGraphRelation aRelation : m_aIncoming.values ())
        if (aRelation.getFrom () == aNewRelation.getFrom ())
          throw new IllegalArgumentException ("The from-node of the passed relation (" +
                                              aNewRelation +
                                              ") is already contained");
    }
    else
    {
      m_aIncoming = new CommonsLinkedHashMap <> ();
    }

    // Add!
    m_aIncoming.put (aNewRelation.getID (), aNewRelation);
  }

  public boolean hasIncomingRelations ()
  {
    return m_aIncoming != null && m_aIncoming.isNotEmpty ();
  }

  @Nonnegative
  public int getIncomingRelationCount ()
  {
    return m_aIncoming == null ? 0 : m_aIncoming.size ();
  }

  public boolean isIncomingRelation (@Nullable final IMutableDirectedGraphRelation aRelation)
  {
    return m_aIncoming != null && aRelation != null && aRelation.equals (m_aIncoming.get (aRelation.getID ()));
  }

  @Nonnull
  @ReturnsMutableCopy
  public ICommonsList <IMutableDirectedGraphRelation> getAllIncomingRelations ()
  {
    return m_aIncoming == null ? new CommonsArrayList <> () : new CommonsArrayList <> (m_aIncoming.values ());
  }

  public void forEachIncomingRelation (@Nonnull final Consumer <? super IMutableDirectedGraphRelation> aConsumer)
  {
    ValueEnforcer.notNull (aConsumer, "Consumer");
    if (m_aIncoming != null)
      m_aIncoming.forEachValue (aConsumer);
  }

  @Nonnull
  public EChange removeIncomingRelation (@Nullable final IMutableDirectedGraphRelation aRelation)
  {
    return aRelation == null || m_aIncoming == null ? EChange.UNCHANGED : m_aIncoming.removeObject (aRelation.getID ());
  }

  @Nonnull
  public EChange removeAllIncomingRelations ()
  {
    if (!hasIncomingRelations ())
      return EChange.UNCHANGED;
    m_aIncoming = null;
    return EChange.CHANGED;
  }

  public boolean isFromNode (@Nullable final IMutableDirectedGraphNode aNode)
  {
    return getIncomingRelationFrom (aNode) != null;
  }

  @Nonnull
  @ReturnsMutableCopy
  public ICommonsSet <IMutableDirectedGraphNode> getAllFromNodes ()
  {
    final ICommonsSet <IMutableDirectedGraphNode> ret = new CommonsHashSet <> ();
    if (m_aIncoming != null)
      CollectionHelper.findAllMapped (m_aIncoming.values (), IMutableDirectedGraphRelation::getFrom, ret::add);
    return ret;
  }

  @Nullable
  public IMutableDirectedGraphRelation getIncomingRelationFrom (@Nullable final IMutableDirectedGraphNode aFromNode)
  {
    if (m_aIncoming != null && aFromNode != null)
      for (final IMutableDirectedGraphRelation aRelation : m_aIncoming.values ())
        if (aRelation.getFrom ().equals (aFromNode))
          return aRelation;
    return null;
  }

  public void addOutgoingRelation (@Nonnull final IMutableDirectedGraphRelation aNewRelation)
  {
    ValueEnforcer.notNull (aNewRelation, "NewRelation");
    ValueEnforcer.isTrue (aNewRelation.getFrom () == this, "Passed outgoing relation is not based on this node");
    if (m_aOutgoing != null)
    {
      if (m_aOutgoing.containsKey (aNewRelation.getID ()))
        throw new IllegalArgumentException ("The passed relation " +
                                            aNewRelation +
                                            " is already contained as an outgoing relation");
      // check if the relation to-node is already contained
      for (final IMutableDirectedGraphRelation aRelation : m_aOutgoing.values ())
        if (aRelation.getTo () == aNewRelation.getTo ())
          throw new IllegalArgumentException ("The to-node of the passed relation " +
                                              aNewRelation +
                                              " is already contained");
    }
    else
    {
      m_aOutgoing = new CommonsLinkedHashMap <> ();
    }

    // Add!
    m_aOutgoing.put (aNewRelation.getID (), aNewRelation);
  }

  public boolean hasOutgoingRelations ()
  {
    return m_aOutgoing != null && m_aOutgoing.isNotEmpty ();
  }

  @Nonnegative
  public int getOutgoingRelationCount ()
  {
    return m_aOutgoing == null ? 0 : m_aOutgoing.size ();
  }

  public boolean isOutgoingRelation (@Nullable final IMutableDirectedGraphRelation aRelation)
  {
    return m_aOutgoing != null && aRelation != null && aRelation.equals (m_aOutgoing.get (aRelation.getID ()));
  }

  @Nonnull
  @ReturnsMutableCopy
  public ICommonsList <IMutableDirectedGraphRelation> getAllOutgoingRelations ()
  {
    return m_aOutgoing == null ? new CommonsArrayList <> () : new CommonsArrayList <> (m_aOutgoing.values ());
  }

  public void forEachOutgoingRelation (@Nonnull final Consumer <? super IMutableDirectedGraphRelation> aConsumer)
  {
    ValueEnforcer.notNull (aConsumer, "Consumer");
    if (m_aOutgoing != null)
      m_aOutgoing.values ().forEach (aConsumer);
  }

  @Nonnull
  @ReturnsMutableCopy
  public ICommonsSet <IMutableDirectedGraphNode> getAllToNodes ()
  {
    final ICommonsSet <IMutableDirectedGraphNode> ret = new CommonsHashSet <> ();
    if (m_aOutgoing != null)
      CollectionHelper.findAllMapped (m_aOutgoing.values (), IMutableDirectedGraphRelation::getTo, ret::add);
    return ret;
  }

  @Nonnull
  public EChange removeOutgoingRelation (@Nullable final IMutableDirectedGraphRelation aRelation)
  {
    return aRelation == null || m_aOutgoing == null ? EChange.UNCHANGED : m_aOutgoing.removeObject (aRelation.getID ());
  }

  @Nonnull
  public EChange removeAllOutgoingRelations ()
  {
    if (!hasOutgoingRelations ())
      return EChange.UNCHANGED;
    m_aOutgoing = null;
    return EChange.CHANGED;
  }

  public boolean isToNode (@Nullable final IMutableDirectedGraphNode aNode)
  {
    return getOutgoingRelationTo (aNode) != null;
  }

  @Nullable
  public IMutableDirectedGraphRelation getOutgoingRelationTo (@Nullable final IMutableDirectedGraphNode aToNode)
  {
    if (m_aOutgoing != null && aToNode != null)
      for (final IMutableDirectedGraphRelation aRelation : m_aOutgoing.values ())
        if (aRelation.getTo ().equals (aToNode))
          return aRelation;
    return null;
  }

  public boolean isConnectedWith (@Nullable final IMutableDirectedGraphNode aNode)
  {
    if (aNode == null)
      return false;
    return getIncomingRelationFrom (aNode) != null || getOutgoingRelationTo (aNode) != null;
  }

  @Nullable
  public IMutableDirectedGraphRelation getRelation (@Nullable final IMutableDirectedGraphNode aNode)
  {
    if (aNode == null)
      return null;
    final IMutableDirectedGraphRelation aIncoming = getIncomingRelationFrom (aNode);
    final IMutableDirectedGraphRelation aOutgoing = getOutgoingRelationTo (aNode);
    if (aIncoming != null && aOutgoing != null)
      throw new IllegalStateException ("Both incoming and outgoing relations between node '" +
                                       getID () +
                                       "' and '" +
                                       aNode.getID () +
                                       "' exist!");
    return aIncoming != null ? aIncoming : aOutgoing;
  }

  @Nonnull
  @ReturnsMutableCopy
  public ICommonsOrderedSet <IMutableDirectedGraphRelation> getAllRelations ()
  {
    final ICommonsOrderedSet <IMutableDirectedGraphRelation> ret = new CommonsLinkedHashSet <> ();
    if (m_aIncoming != null)
      ret.addAll (m_aIncoming.values ());
    if (m_aOutgoing != null)
      ret.addAll (m_aOutgoing.values ());
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public ICommonsOrderedSet <String> getAllRelationIDs ()
  {
    final ICommonsOrderedSet <String> ret = new CommonsLinkedHashSet <> ();
    if (m_aIncoming != null)
      ret.addAll (m_aIncoming.keySet ());
    if (m_aOutgoing != null)
      ret.addAll (m_aOutgoing.keySet ());
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public ICommonsOrderedSet <IMutableDirectedGraphNode> getAllRelatedNodes ()
  {
    final ICommonsOrderedSet <IMutableDirectedGraphNode> ret = new CommonsLinkedHashSet <> ();
    if (m_aIncoming != null)
      for (final IMutableDirectedGraphRelation aRelation : m_aIncoming.values ())
        ret.add (aRelation.getFrom ());
    if (m_aOutgoing != null)
      for (final IMutableDirectedGraphRelation aRelation : m_aOutgoing.values ())
        ret.add (aRelation.getTo ());
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public ICommonsOrderedSet <String> getAllRelatedNodeIDs ()
  {
    final ICommonsOrderedSet <String> ret = new CommonsLinkedHashSet <> ();
    if (m_aIncoming != null)
      for (final IMutableDirectedGraphRelation aRelation : m_aIncoming.values ())
        ret.add (aRelation.getFromID ());
    if (m_aOutgoing != null)
      for (final IMutableDirectedGraphRelation aRelation : m_aOutgoing.values ())
        ret.add (aRelation.getToID ());
    return ret;
  }

  @Override
  public boolean equals (final Object o)
  {
    return super.equals (o);
  }

  @Override
  public int hashCode ()
  {
    return super.hashCode ();
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ())
                            .append ("incomingIDs", m_aIncoming == null ? null : m_aIncoming.keySet ())
                            .append ("outgoingIDs", m_aOutgoing == null ? null : m_aOutgoing.keySet ())
                            .getToString ();
  }
}
