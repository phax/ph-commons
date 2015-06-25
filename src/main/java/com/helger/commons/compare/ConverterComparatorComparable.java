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
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.convert.IConverter;

/**
 * This is a {@link java.util.Comparator} for objects which are extracted from a
 * converter.
 *
 * @author Philip Helger
 * @param <DATATYPE>
 *        The data type to be compared
 * @param <PARTTYPE>
 *        The part type that is extracted from the data element and compared
 */
@NotThreadSafe
public class ConverterComparatorComparable <DATATYPE, PARTTYPE extends Comparable <? super PARTTYPE>> extends AbstractPartComparatorComparable <DATATYPE, PARTTYPE>
{
  private final IConverter <DATATYPE, PARTTYPE> m_aConverter;

  /**
   * Constructor.
   *
   * @param aConverter
   *        The converter to be used to extract the part from the source object.
   *        May not be <code>null</code>.
   */
  public ConverterComparatorComparable (@Nonnull final IConverter <DATATYPE, PARTTYPE> aConverter)
  {
    m_aConverter = ValueEnforcer.notNull (aConverter, "Converter");
  }

  /**
   * @return The converter passed in the constructor. Never <code>null</code>.
   */
  @Nonnull
  public final IConverter <DATATYPE, PARTTYPE> getConverter ()
  {
    return m_aConverter;
  }

  @Override
  @Nullable
  protected final PARTTYPE getPart (@Nonnull final DATATYPE aObject)
  {
    return m_aConverter.convert (aObject);
  }
}
