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
 * Abstract comparator that handles values that can be represented as float
 * values.
 *
 * @author Philip Helger
 * @param <DATATYPE>
 *        The data type to be compared. Must somehow have a value that can be
 *        compared as a float value.
 */
public abstract class AbstractFloatComparator <DATATYPE> extends AbstractComparator <DATATYPE>
{
  public AbstractFloatComparator ()
  {}

  /**
   * Protected method to convert the passed object into a float value.
   *
   * @param aObject
   *        The source object
   * @return The result float value.
   */
  protected abstract float getAsFloat (@Nonnull DATATYPE aObject);

  @Override
  protected final int mainCompare (@Nonnull final DATATYPE aElement1, @Nonnull final DATATYPE aElement2)
  {
    final float f1 = getAsFloat (aElement1);
    final float f2 = getAsFloat (aElement2);
    return CompareHelper.compare (f1, f2);
  }
}
