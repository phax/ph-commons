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
package com.helger.commons.compare;

import javax.annotation.Nonnull;

/**
 * Abstract comparator that handles values that can be represented as double
 * values.
 *
 * @author Philip Helger
 * @param <DATATYPE>
 *        The data type to be compared. Must somehow have a value that can be
 *        compared as a double value.
 */
public abstract class AbstractDoubleComparator <DATATYPE> extends AbstractComparator <DATATYPE>
{
  public AbstractDoubleComparator ()
  {
    super ();
  }

  /**
   * Compare with a special order.
   *
   * @param eSortOrder
   *        The sort order to use. May not be <code>null</code>.
   */
  public AbstractDoubleComparator (@Nonnull final ESortOrder eSortOrder)
  {
    super (eSortOrder);
  }

  /**
   * Protected method to convert the passed object into a double value.
   *
   * @param aObject
   *        The source object
   * @return The result double value.
   */
  protected abstract double getAsDouble (DATATYPE aObject);

  @Override
  protected final int mainCompare (final DATATYPE aElement1, final DATATYPE aElement2)
  {
    final double d1 = getAsDouble (aElement1);
    final double d2 = getAsDouble (aElement2);
    return CompareUtils.compare (d1, d2);
  }
}
