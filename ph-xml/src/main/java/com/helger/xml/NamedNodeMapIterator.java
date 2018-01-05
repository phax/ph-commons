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
import java.util.NoSuchElementException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.iterate.IIterableIterator;
import com.helger.commons.string.ToStringGenerator;

/**
 * Special {@link Iterator} for {@link Node} objects based on
 * {@link NamedNodeMap} objects.
 *
 * @author Philip Helger
 */
public class NamedNodeMapIterator implements IIterableIterator <Node>
{
  private final NamedNodeMap m_aNL;
  private int m_nIndex = 0;
  private final int m_nMax;

  public NamedNodeMapIterator (@Nullable final NamedNodeMap aNL)
  {
    m_aNL = aNL;
    m_nMax = aNL == null ? 0 : aNL.getLength ();
  }

  public boolean hasNext ()
  {
    return m_nIndex < m_nMax;
  }

  @Nullable
  public Node next ()
  {
    if (m_nIndex >= m_nMax)
      throw new NoSuchElementException ();

    final Node ret = m_aNL.item (m_nIndex);
    m_nIndex++;
    return ret;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("NamedNodeMap", m_aNL)
                                       .append ("Max", m_nMax)
                                       .append ("Index", m_nIndex)
                                       .getToString ();
  }

  @Nonnull
  @ReturnsMutableCopy
  public static NamedNodeMapIterator createAttributeIterator (@Nullable final Element aElement)
  {
    return new NamedNodeMapIterator (aElement == null ? null : aElement.getAttributes ());
  }
}
