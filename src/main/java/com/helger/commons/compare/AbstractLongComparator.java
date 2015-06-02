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
import javax.annotation.concurrent.NotThreadSafe;

/**
 * Abstract comparator that handles values that can be represented as long
 * values.
 *
 * @author Philip Helger
 * @param <DATATYPE>
 *        The data type to be compared. Must somehow have a value that can be
 *        compared as a long value.
 */
@NotThreadSafe
public abstract class AbstractLongComparator <DATATYPE> extends AbstractComparator <DATATYPE>
{
  public AbstractLongComparator ()
  {}

  /**
   * Protected method to convert the passed object into a long value.
   *
   * @param aObject
   *        The source object
   * @return The result long value.
   */
  protected abstract long getAsLong (@Nonnull DATATYPE aObject);

  @Override
  protected final int mainCompare (@Nonnull final DATATYPE aElement1, @Nonnull final DATATYPE aElement2)
  {
    final long n1 = getAsLong (aElement1);
    final long n2 = getAsLong (aElement2);
    return CompareHelper.compare (n1, n2);
  }
}
