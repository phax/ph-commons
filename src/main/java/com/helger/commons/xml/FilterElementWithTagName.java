/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import org.w3c.dom.Element;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotations.Nonempty;
import com.helger.commons.filter.AbstractSerializableFilter;
import com.helger.commons.filter.ISerializableFilter;
import com.helger.commons.hash.HashCodeGenerator;
import com.helger.commons.string.ToStringGenerator;

/**
 * An implementation of {@link ISerializableFilter} on {@link Element} objects
 * that will only return elements with a certain tag name and without a
 * namespace URI.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public final class FilterElementWithTagName extends AbstractSerializableFilter <Element>
{
  private final String m_sTagName;

  public FilterElementWithTagName (@Nonnull @Nonempty final String sTagName)
  {
    this (sTagName, null);
  }

  public FilterElementWithTagName (@Nonnull @Nonempty final String sTagName,
                                   @Nullable final ISerializableFilter <? super Element> aNestedFilter)
  {
    super (aNestedFilter);
    m_sTagName = ValueEnforcer.notNull (sTagName, "TagName");
  }

  @Nonnull
  @Nonempty
  public String getTagName ()
  {
    return m_sTagName;
  }

  @Override
  public boolean matchesThisFilter (@Nullable final Element aElement)
  {
    return aElement != null && aElement.getNamespaceURI () == null && aElement.getTagName ().equals (m_sTagName);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (!super.equals (o))
      return false;
    final FilterElementWithTagName rhs = (FilterElementWithTagName) o;
    return m_sTagName.equals (rhs.m_sTagName);
  }

  @Override
  public int hashCode ()
  {
    return HashCodeGenerator.getDerived (super.hashCode ()).append (m_sTagName).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ()).append ("tagName", m_sTagName).toString ();
  }
}
