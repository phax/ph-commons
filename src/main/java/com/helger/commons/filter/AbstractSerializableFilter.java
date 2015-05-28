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
package com.helger.commons.filter;

import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

/**
 * An abstract implementation of {@link ISerializableFilter} that has an
 * optional nested filter.
 *
 * @author Philip Helger
 * @param <DATATYPE>
 *        The type of object to filter.
 */
@NotThreadSafe
public abstract class AbstractSerializableFilter <DATATYPE> extends AbstractFilter <DATATYPE> implements ISerializableFilter <DATATYPE>
{
  /**
   * Constructor.
   *
   * @param aNestedFilter
   *        The nested filter to use. May be <code>null</code> to not have a
   *        nested filter-
   */
  public AbstractSerializableFilter (@Nullable final ISerializableFilter <? super DATATYPE> aNestedFilter)
  {
    super (aNestedFilter);
  }
}
