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
package com.helger.commons.collections.triple;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.compare.AbstractPartComparatorComparable;
import com.helger.commons.compare.ESortOrder;

/**
 * Comparator comparing {@link ITriple} objects by the first element
 *
 * @author Philip Helger
 * @param <DATA1TYPE>
 *        triple first type
 * @param <DATA2TYPE>
 *        triple second type
 * @param <DATA3TYPE>
 *        triple third type
 */
public class ComparatorTripleFirst <DATA1TYPE extends Comparable <? super DATA1TYPE>, DATA2TYPE, DATA3TYPE> extends AbstractPartComparatorComparable <ITriple <DATA1TYPE, DATA2TYPE, DATA3TYPE>, DATA1TYPE>
{
  public ComparatorTripleFirst ()
  {
    super ();
  }

  public ComparatorTripleFirst (@Nonnull final ESortOrder eSortOrder)
  {
    super (eSortOrder);
  }

  @Override
  @Nullable
  protected DATA1TYPE getPart (@Nonnull final ITriple <DATA1TYPE, DATA2TYPE, DATA3TYPE> aObject)
  {
    return aObject.getFirst ();
  }
}
