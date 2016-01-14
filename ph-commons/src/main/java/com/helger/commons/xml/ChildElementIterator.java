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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.helger.commons.annotation.UnsupportedOperation;
import com.helger.commons.collection.iterate.FilterIterator;
import com.helger.commons.collection.iterate.IIterableIterator;
import com.helger.commons.filter.IFilter;
import com.helger.commons.string.ToStringGenerator;

/**
 * Iterate child elements of a single node. Does not iterate recursively. Does
 * not return the start node.
 *
 * @author Philip Helger
 */
public class ChildElementIterator implements IIterableIterator <Element>
{
  /** The nodes to iterate. */
  private final FilterIterator <Node> m_aIter;

  public ChildElementIterator (@Nonnull final Node aStartNode)
  {
    this (aStartNode, null);
  }

  public ChildElementIterator (@Nonnull final Node aStartNode, @Nullable final IFilter <? super Element> aCustomFilter)
  {
    m_aIter = new FilterIterator <Node> (new ChildNodeIterator (aStartNode), new FilterNodeIsElement (aCustomFilter));
  }

  @Nonnull
  public FilterIterator <Node> getFilterIterator ()
  {
    return m_aIter;
  }

  public final boolean hasNext ()
  {
    return m_aIter.hasNext ();
  }

  @Nonnull
  public final Element next ()
  {
    return (Element) m_aIter.next ();
  }

  @UnsupportedOperation
  public final void remove ()
  {
    throw new UnsupportedOperationException ();
  }

  @Nonnull
  public Iterator <Element> iterator ()
  {
    return this;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("iter", m_aIter).toString ();
  }
}
