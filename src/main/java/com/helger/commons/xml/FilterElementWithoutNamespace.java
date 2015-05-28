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

import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import org.w3c.dom.Element;

import com.helger.commons.filter.AbstractSerializableFilter;
import com.helger.commons.filter.IFilter;
import com.helger.commons.filter.ISerializableFilter;
import com.helger.commons.string.StringHelper;

/**
 * An implementation of {@link IFilter} on {@link Element} objects that will
 * only return elements without a namespace URI.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class FilterElementWithoutNamespace extends AbstractSerializableFilter <Element>
{
  public FilterElementWithoutNamespace ()
  {}

  public FilterElementWithoutNamespace (@Nullable final ISerializableFilter <? super Element> aNestedFilter)
  {
    super (aNestedFilter);
  }

  @Override
  public boolean matchesThisFilter (@Nullable final Element aElement)
  {
    return aElement != null && StringHelper.hasNoText (aElement.getNamespaceURI ());
  }
}
