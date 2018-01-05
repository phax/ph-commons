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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.impl.CommonsLinkedHashSet;
import com.helger.commons.collection.impl.ICommonsOrderedSet;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.string.ToStringGenerator;
import com.helger.graph.IMutableDirectedGraphNode;
import com.helger.graph.IMutableDirectedGraphRelation;

/**
 * Default implementation of the {@link IMutableDirectedGraphRelation} interface
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class DirectedGraphRelation extends AbstractBaseGraphObject implements IMutableDirectedGraphRelation
{
  private final IMutableDirectedGraphNode m_aFrom;
  private final IMutableDirectedGraphNode m_aTo;

  public DirectedGraphRelation (@Nonnull final IMutableDirectedGraphNode aFrom,
                                @Nonnull final IMutableDirectedGraphNode aTo)
  {
    this (null, aFrom, aTo);
  }

  public DirectedGraphRelation (@Nullable final String sID,
                                @Nonnull final IMutableDirectedGraphNode aFrom,
                                @Nonnull final IMutableDirectedGraphNode aTo)
  {
    super (sID);
    ValueEnforcer.notNull (aFrom, "From");
    ValueEnforcer.notNull (aTo, "To");
    m_aFrom = aFrom;
    m_aTo = aTo;
  }

  public final boolean isDirected ()
  {
    return true;
  }

  public boolean isRelatedTo (@Nullable final IMutableDirectedGraphNode aNode)
  {
    return m_aFrom.equals (aNode) || m_aTo.equals (aNode);
  }

  @Nonnull
  @ReturnsMutableCopy
  public ICommonsOrderedSet <IMutableDirectedGraphNode> getAllConnectedNodes ()
  {
    return new CommonsLinkedHashSet<> (m_aFrom, m_aTo);
  }

  @Nonnull
  @ReturnsMutableCopy
  public ICommonsOrderedSet <String> getAllConnectedNodeIDs ()
  {
    return new CommonsLinkedHashSet<> (m_aFrom.getID (), m_aTo.getID ());
  }

  @Nonnull
  public IMutableDirectedGraphNode getFrom ()
  {
    return m_aFrom;
  }

  @Nonnull
  public String getFromID ()
  {
    return m_aFrom.getID ();
  }

  @Nonnull
  public IMutableDirectedGraphNode getTo ()
  {
    return m_aTo;
  }

  @Nonnull
  public String getToID ()
  {
    return m_aTo.getID ();
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (!super.equals (o))
      return false;
    final DirectedGraphRelation rhs = (DirectedGraphRelation) o;
    return m_aFrom.equals (rhs.m_aFrom) && m_aTo.equals (rhs.m_aTo);
  }

  @Override
  public int hashCode ()
  {
    return HashCodeGenerator.getDerived (super.hashCode ()).append (m_aFrom).append (m_aTo).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ())
                            .append ("from", m_aFrom)
                            .append ("to", m_aTo)
                            .getToString ();
  }
}
