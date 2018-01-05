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
import com.helger.commons.collection.impl.ICommonsOrderedMap;
import com.helger.commons.collection.impl.ICommonsOrderedSet;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.string.ToStringGenerator;
import com.helger.graph.IMutableBaseGraph;
import com.helger.graph.IMutableBaseGraphNode;
import com.helger.graph.IMutableBaseGraphRelation;

/**
 * A simple graph object that bidirectionally links graph nodes.
 *
 * @author Philip Helger
 * @param <NODETYPE>
 *        Node class
 * @param <RELATIONTYPE>
 *        Relation class
 */
@NotThreadSafe
public abstract class AbstractBaseGraph <NODETYPE extends IMutableBaseGraphNode <NODETYPE, RELATIONTYPE>, RELATIONTYPE extends IMutableBaseGraphRelation <NODETYPE, RELATIONTYPE>>
                                        extends AbstractBaseGraphObject
                                        implements IMutableBaseGraph <NODETYPE, RELATIONTYPE>
{
  /** By default this is allowed */
  public static final boolean DEFAULT_CHANGING_CONNECTED_OBJECTS_ALLOWED = true;

  protected final ICommonsOrderedMap <String, NODETYPE> m_aNodes = new CommonsLinkedHashMap<> ();
  private boolean m_bIsChangingConnectedObjectsAllowed = DEFAULT_CHANGING_CONNECTED_OBJECTS_ALLOWED;

  public AbstractBaseGraph (@Nullable final String sID)
  {
    super (sID);
  }

  public void setChangingConnectedObjectsAllowed (final boolean bIsChangingConnectedObjectsAllowed)
  {
    m_bIsChangingConnectedObjectsAllowed = bIsChangingConnectedObjectsAllowed;
  }

  public boolean isChangingConnectedObjectsAllowed ()
  {
    return m_bIsChangingConnectedObjectsAllowed;
  }

  @Nullable
  public NODETYPE getNodeOfID (@Nullable final String sID)
  {
    return m_aNodes.get (sID);
  }

  @Nonnegative
  public int getNodeCount ()
  {
    return m_aNodes.size ();
  }

  @Nonnull
  @ReturnsMutableCopy
  public ICommonsOrderedMap <String, NODETYPE> getAllNodes ()
  {
    return m_aNodes.getClone ();
  }

  @Nonnull
  @ReturnsMutableCopy
  public ICommonsOrderedSet <String> getAllNodeIDs ()
  {
    return m_aNodes.copyOfKeySet ();
  }

  public void forEachNode (@Nonnull final Consumer <? super NODETYPE> aConsumer)
  {
    ValueEnforcer.notNull (aConsumer, "Consumer");
    m_aNodes.values ().forEach (aConsumer);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    // ignore super equals!
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final AbstractBaseGraph <?, ?> rhs = (AbstractBaseGraph <?, ?>) o;
    return m_aNodes.equals (rhs.m_aNodes) &&
           m_bIsChangingConnectedObjectsAllowed == rhs.m_bIsChangingConnectedObjectsAllowed;
  }

  @Override
  public int hashCode ()
  {
    // ignore super hashCode!
    return new HashCodeGenerator (this).append (m_aNodes).append (m_bIsChangingConnectedObjectsAllowed).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ())
                            .append ("nodes", m_aNodes)
                            .append ("isChangingConnectedObjectsAllowed", m_bIsChangingConnectedObjectsAllowed)
                            .getToString ();
  }
}
