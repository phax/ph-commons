/**
 * Copyright (C) 2014-2016 Philip Helger (www.helger.com)
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
package com.helger.commons.xml;

import java.util.Iterator;
import java.util.NoSuchElementException;

import javax.annotation.Nonnull;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.UnsupportedOperation;
import com.helger.commons.collection.iterate.IIterableIterator;
import com.helger.commons.string.ToStringGenerator;

/**
 * Iterate child elements of a single node. Does not iterate recursively. Does
 * not return the source node.
 *
 * @author Philip Helger
 */
public class ChildNodeIterator implements IIterableIterator <Node>
{
  /** The nodes to iterate. */
  private final NodeList m_aNL;

  /** the maximum number of nodes. */
  private final int m_nMax;

  /** the current index. */
  private int m_nIndex = 0;

  public ChildNodeIterator (@Nonnull final Node aStartNode)
  {
    ValueEnforcer.notNull (aStartNode, "StartNode");
    m_aNL = aStartNode.getChildNodes ();
    m_nMax = m_aNL.getLength ();
  }

  public boolean hasNext ()
  {
    return m_nIndex < m_nMax;
  }

  @Nonnull
  public Node next ()
  {
    if (!hasNext ())
      throw new NoSuchElementException ();

    // just advance to next index
    final Node ret = m_aNL.item (m_nIndex);
    ++m_nIndex;
    return ret;
  }

  @UnsupportedOperation
  public void remove ()
  {
    throw new UnsupportedOperationException ();
  }

  @Nonnull
  public Iterator <Node> iterator ()
  {
    return this;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("nodeList", m_aNL)
                                       .append ("max", m_nMax)
                                       .append ("index", m_nIndex)
                                       .toString ();
  }
}
