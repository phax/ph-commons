/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.hashcode.HashCodeGenerator;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.collection.commons.CommonsLinkedHashSet;
import com.helger.collection.commons.ICommonsOrderedSet;
import com.helger.graph.IMutableGraphNode;
import com.helger.graph.IMutableGraphRelation;

/**
 * Default implementation of the {@link IMutableGraphRelation} interface
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class GraphRelation extends AbstractBaseGraphObject implements IMutableGraphRelation
{
  private final IMutableGraphNode m_aNode1;
  private final IMutableGraphNode m_aNode2;

  /**
   * Constructor with an auto-generated ID.
   *
   * @param aNode1
   *        The first node. May not be <code>null</code>.
   * @param aNode2
   *        The second node. May not be <code>null</code>.
   */
  public GraphRelation (@NonNull final IMutableGraphNode aNode1, @NonNull final IMutableGraphNode aNode2)
  {
    this (null, aNode1, aNode2);
  }

  /**
   * Constructor with an existing ID.
   *
   * @param sID
   *        The ID of this relation. If <code>null</code> or empty a new ID is
   *        generated.
   * @param aNode1
   *        The first node. May not be <code>null</code>.
   * @param aNode2
   *        The second node. May not be <code>null</code>.
   */
  public GraphRelation (@Nullable final String sID, @NonNull final IMutableGraphNode aNode1, @NonNull final IMutableGraphNode aNode2)
  {
    super (sID);
    ValueEnforcer.notNull (aNode1, "Node1");
    ValueEnforcer.notNull (aNode2, "Node2");
    m_aNode1 = aNode1;
    m_aNode2 = aNode2;
  }

  /** {@inheritDoc} */
  public final boolean isDirected ()
  {
    return false;
  }

  /** {@inheritDoc} */
  public boolean isRelatedTo (@Nullable final IMutableGraphNode aNode)
  {
    return aNode != null && (m_aNode1.equals (aNode) || m_aNode2.equals (aNode));
  }

  /** {@inheritDoc} */
  @NonNull
  @ReturnsMutableCopy
  public ICommonsOrderedSet <IMutableGraphNode> getAllConnectedNodes ()
  {
    return new CommonsLinkedHashSet <> (m_aNode1, m_aNode2);
  }

  /** {@inheritDoc} */
  @NonNull
  @ReturnsMutableCopy
  public ICommonsOrderedSet <String> getAllConnectedNodeIDs ()
  {
    return new CommonsLinkedHashSet <> (m_aNode1.getID (), m_aNode2.getID ());
  }

  /** {@inheritDoc} */
  @NonNull
  public IMutableGraphNode getNode1 ()
  {
    return m_aNode1;
  }

  /** {@inheritDoc} */
  @NonNull
  public IMutableGraphNode getNode2 ()
  {
    return m_aNode2;
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (!super.equals (o))
      return false;
    final GraphRelation rhs = (GraphRelation) o;
    return m_aNode1.equals (rhs.m_aNode1) && m_aNode2.equals (rhs.m_aNode2);
  }

  @Override
  public int hashCode ()
  {
    return HashCodeGenerator.getDerived (super.hashCode ()).append (m_aNode1).append (m_aNode2).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ()).append ("node1", m_aNode1).append ("node2", m_aNode2).getToString ();
  }
}
