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
package com.helger.commons.mime;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.compare.AbstractComparator;
import com.helger.commons.compare.ESortOrder;

@NotThreadSafe
public class ComparatorMimeTypeInfoPrimaryMimeType extends AbstractComparator <MimeTypeInfo>
{
  /**
   * Comparator with default sort order and no nested comparator.
   */
  public ComparatorMimeTypeInfoPrimaryMimeType ()
  {
    super ();
  }

  /**
   * Constructor with sort order.
   *
   * @param eSortOrder
   *        The sort order to use. May not be <code>null</code>.
   */
  public ComparatorMimeTypeInfoPrimaryMimeType (@Nonnull final ESortOrder eSortOrder)
  {
    super (eSortOrder);
  }

  @Override
  protected int mainCompare (@Nonnull final MimeTypeInfo aElement1, @Nonnull final MimeTypeInfo aElement2)
  {
    final String s1 = aElement1.getPrimaryMimeTypeWithSource ().getMimeTypeAsString ();
    final String s2 = aElement2.getPrimaryMimeTypeWithSource ().getMimeTypeAsString ();
    return s1.compareToIgnoreCase (s2);
  }
}
