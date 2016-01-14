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

import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import org.w3c.dom.Element;

import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.filter.IFilter;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.string.ToStringGenerator;

/**
 * An implementation of {@link IFilter} on {@link Element} objects that will
 * only return elements with a certain namespace URI.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class FilterElementWithNamespace implements IFilter <Element>
{
  private final String m_sNamespaceURI;

  public FilterElementWithNamespace (@Nullable final String sNamespaceURI)
  {
    m_sNamespaceURI = sNamespaceURI;
  }

  @Nullable
  public String getNamespaceURI ()
  {
    return m_sNamespaceURI;
  }

  @Override
  public boolean test (@Nullable final Element aElement)
  {
    return aElement != null && XMLHelper.hasNamespaceURI (aElement, m_sNamespaceURI);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (!super.equals (o))
      return false;
    final FilterElementWithNamespace rhs = (FilterElementWithNamespace) o;
    return EqualsHelper.equals (m_sNamespaceURI, rhs.m_sNamespaceURI);
  }

  @Override
  public int hashCode ()
  {
    return HashCodeGenerator.getDerived (super.hashCode ()).append (m_sNamespaceURI).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ()).append ("namespaceURI", m_sNamespaceURI).toString ();
  }
}
