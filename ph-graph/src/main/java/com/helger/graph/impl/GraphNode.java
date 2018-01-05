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
import com.helger.commons.collection.impl.CommonsLinkedHashMap;
import com.helger.commons.collection.impl.CommonsLinkedHashSet;
import com.helger.commons.collection.impl.ICommonsOrderedMap;
import com.helger.commons.collection.impl.ICommonsOrderedSet;
import com.helger.commons.state.EChange;
import com.helger.commons.string.ToStringGenerator;
import com.helger.graph.IMutableGraphNode;
import com.helger.graph.IMutableGraphRelation;

/**
 * Default implementation if the {@link IMutableGraphNode} interface
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class GraphNode extends AbstractBaseGraphObject implements IMutableGraphNode
{
  private ICommonsOrderedMap <String, IMutableGraphRelation> m_aRelations;

  public GraphNode ()
  {
    this (null);
  }

  public GraphNode (@Nullable final String sID)
  {
    super (sID);
  }

  public final boolean isDirected ()
  {
    return false;
  }

  @Nonnull
  public EChange addRelation (@Nullable final IMutableGraphRelation aRelation)
  {
    if (aRelation == null)
      return EChange.UNCHANGED;
    if (!aRelation.isRelatedTo (this))
      throw new IllegalArgumentException ("Relation is not suitable for this node!");

    final String sRelationID = aRelation.getID ();
    if (m_aRelations == null)
      m_aRelations = new CommonsLinkedHashMap<> ();
    else
      if (m_aRelations.containsKey (sRelationID))
        return EChange.UNCHANGED;

    m_aRelations.put (sRelationID, aRelation);
    return EChange.CHANGED;
  }

  @Nonnull
  public EChange removeRelation (@Nullable final IMutableGraphRelation aRelation)
  {
    if (aRelation == null || m_aRelations == null)
      return EChange.UNCHANGED;
    return EChange.valueOf (m_aRelations.remove (aRelation.getID ()) != null);
  }

  @Nonnull
  public EChange removeAllRelations ()
  {
    if (!hasRelations ())
      return EChange.UNCHANGED;
    m_aRelations = null;
    return EChange.CHANGED;
  }

  public boolean isConnectedWith (@Nullable final IMutableGraphNode aNode)
  {
    return getRelation (aNode) != null;
  }

  @Nullable
  public IMutableGraphRelation getRelation (@Nullable final IMutableGraphNode aNode)
  {
    if (m_aRelations != null && aNode != null && aNode != this)
    {
      for (final IMutableGraphRelation aRelation : m_aRelations.values ())
        if (aRelation.isRelatedTo (aNode))
          return aRelation;
    }
    return null;
  }

  public boolean hasRelations ()
  {
    return m_aRelations != null && m_aRelations.isNotEmpty ();
  }

  @Nonnegative
  public int getRelationCount ()
  {
    return m_aRelations == null ? 0 : m_aRelations.size ();
  }

  @Nonnull
  @ReturnsMutableCopy
  public ICommonsOrderedSet <IMutableGraphRelation> getAllRelations ()
  {
    final ICommonsOrderedSet <IMutableGraphRelation> ret = new CommonsLinkedHashSet<> ();
    if (m_aRelations != null)
      ret.addAll (m_aRelations.values ());
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public ICommonsOrderedSet <String> getAllRelationIDs ()
  {
    final ICommonsOrderedSet <String> ret = new CommonsLinkedHashSet<> ();
    if (m_aRelations != null)
      ret.addAll (m_aRelations.keySet ());
    return ret;
  }

  public void forEachRelation (@Nonnull final Consumer <? super IMutableGraphRelation> aConsumer)
  {
    ValueEnforcer.notNull (aConsumer, "Consumer");
    if (m_aRelations != null)
      m_aRelations.forEachValue (aConsumer);
  }

  @Nonnull
  @ReturnsMutableCopy
  public ICommonsOrderedSet <IMutableGraphNode> getAllRelatedNodes ()
  {
    final ICommonsOrderedSet <IMutableGraphNode> ret = new CommonsLinkedHashSet<> ();
    if (m_aRelations != null)
      for (final IMutableGraphRelation aRelation : m_aRelations.values ())
      {
        ret.add (aRelation.getNode1 ());
        ret.add (aRelation.getNode2 ());
      }
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public ICommonsOrderedSet <String> getAllRelatedNodeIDs ()
  {
    final ICommonsOrderedSet <String> ret = new CommonsLinkedHashSet<> ();
    if (m_aRelations != null)
      for (final IMutableGraphRelation aRelation : m_aRelations.values ())
      {
        ret.add (aRelation.getNode1ID ());
        ret.add (aRelation.getNode2ID ());
      }
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
                            .append ("relationIDs", m_aRelations == null ? null : m_aRelations.keySet ())
                            .getToString ();
  }
}
