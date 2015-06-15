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
package com.helger.commons.xml.namespace;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import javax.xml.namespace.QName;

import com.helger.commons.compare.AbstractComparator;
import com.helger.commons.compare.CompareHelper;

/**
 * Comparator for {@link QName} objects
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class ComparatorQName extends AbstractComparator <QName>
{
  public ComparatorQName ()
  {}

  @Override
  protected int mainCompare (@Nonnull final QName aElement1, @Nonnull final QName aElement2)
  {
    int ret = CompareHelper.compare (aElement1.getNamespaceURI (),
                                     aElement2.getNamespaceURI (),
                                     isNullValuesComeFirst ());
    if (ret == 0)
      ret = aElement1.getLocalPart ().compareTo (aElement2.getLocalPart ());
    return ret;
  }
}
