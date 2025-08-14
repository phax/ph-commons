/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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
package com.helger.xml;

import java.util.NoSuchElementException;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.helger.base.equals.ValueEnforcer;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsIterableIterator;
import com.helger.collection.commons.ICommonsList;

import jakarta.annotation.Nonnull;

/**
 * Iterate all children of the start node, but NOT the start node itself.
 *
 * @author Philip Helger
 * @since 10.1.7
 */
public class RecursiveNodeIterator implements ICommonsIterableIterator <Node>
{
  private final ICommonsList <Node> m_aOpen = new CommonsArrayList <> ();

  public RecursiveNodeIterator (@Nonnull final Node aNode)
  {
    ValueEnforcer.notNull (aNode, "Node");
    m_aOpen.add (aNode);
  }

  public boolean hasNext ()
  {
    return m_aOpen.isNotEmpty ();
  }

  @Nonnull
  public Node next ()
  {
    if (m_aOpen.isEmpty ())
      throw new NoSuchElementException ();

    final Node ret = m_aOpen.remove (0);
    final NodeList aChildren = ret.getChildNodes ();
    if (aChildren != null)
    {
      final int nChildCount = aChildren.getLength ();
      int nTarget = 0;
      for (int i = 0; i < nChildCount; ++i)
      {
        m_aOpen.add (nTarget, aChildren.item (i));
        nTarget++;
      }
    }
    return ret;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("Open", m_aOpen).getToString ();
  }

  /**
   * Create a {@link RecursiveNodeIterator} that only iterates the child nodes
   * of the given node.
   *
   * @param aNode
   *        The node to iterate the children from. May not be <code>null</code>.
   * @return Never <code>null</code>.
   */
  @Nonnull
  public static RecursiveNodeIterator createChildNodeIterator (@Nonnull final Node aNode)
  {
    // Create a regular one
    final RecursiveNodeIterator ret = new RecursiveNodeIterator (aNode);
    // But skip the root
    ret.next ();
    return ret;
  }
}
