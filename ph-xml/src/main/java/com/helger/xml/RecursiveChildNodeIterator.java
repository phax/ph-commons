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
package com.helger.xml;

import java.util.Iterator;
import java.util.List;

import javax.annotation.Nonnull;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.collection.iterate.IIterableIterator;
import com.helger.commons.string.ToStringGenerator;

/**
 * Iterate all children of the start node, but NOT the start node itself.
 *
 * @author Philip Helger
 */
public class RecursiveChildNodeIterator implements IIterableIterator <Node>
{
  private final Iterator <Node> m_aIter;

  private static void _recursiveFillListPrefix (@Nonnull final Node aParent, @Nonnull final List <Node> aNodes)
  {
    final NodeList aNodeList = aParent.getChildNodes ();
    if (aNodeList != null)
    {
      final int nlsize = aNodeList.getLength ();
      for (int i = 0; i < nlsize; ++i)
      {
        final Node aCurrent = aNodeList.item (i);
        aNodes.add (aCurrent);

        _recursiveFillListPrefix (aCurrent, aNodes);
      }
    }
  }

  public RecursiveChildNodeIterator (@Nonnull final Node aParent)
  {
    ValueEnforcer.notNull (aParent, "Parent");

    final ICommonsList <Node> aNodes = new CommonsArrayList<> ();
    _recursiveFillListPrefix (aParent, aNodes);
    m_aIter = aNodes.iterator ();
  }

  public boolean hasNext ()
  {
    return m_aIter.hasNext ();
  }

  public Node next ()
  {
    return m_aIter.next ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("iter", m_aIter).getToString ();
  }
}
