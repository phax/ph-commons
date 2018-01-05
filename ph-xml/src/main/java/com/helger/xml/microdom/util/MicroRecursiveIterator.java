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
package com.helger.xml.microdom.util;

import java.util.NoSuchElementException;

import javax.annotation.Nonnull;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.collection.iterate.IIterableIterator;
import com.helger.xml.microdom.IMicroNode;

/**
 * Class for recursively visiting all elements of a micro node
 *
 * @author Philip Helger
 */
public class MicroRecursiveIterator implements IIterableIterator <IMicroNode>
{
  private final ICommonsList <IMicroNode> m_aOpen = new CommonsArrayList<> ();

  public MicroRecursiveIterator (@Nonnull final IMicroNode aNode)
  {
    ValueEnforcer.notNull (aNode, "Node");
    m_aOpen.add (aNode);
  }

  public boolean hasNext ()
  {
    return !m_aOpen.isEmpty ();
  }

  public IMicroNode next ()
  {
    if (m_aOpen.isEmpty ())
      throw new NoSuchElementException ();

    final IMicroNode ret = m_aOpen.remove (0);
    if (ret.hasChildren ())
      m_aOpen.addAll (0, ret.getAllChildren ());
    return ret;
  }
}
