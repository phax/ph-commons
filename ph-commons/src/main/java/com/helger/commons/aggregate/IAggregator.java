/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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
package com.helger.commons.aggregate;

import java.util.Collection;

import javax.annotation.Nullable;

import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.functional.IFunction;

/**
 * Aggregate a list of input objects to a single output object (change n to 1).
 *
 * @author Philip Helger
 * @param <SRCTYPE>
 *        The input type.
 * @param <DSTTYPE>
 *        The output type.
 */
@FunctionalInterface
public interface IAggregator <SRCTYPE, DSTTYPE> extends IFunction <Collection <SRCTYPE>, DSTTYPE>
{
  /**
   * Aggregate a array of input objects to a single output object.
   *
   * @param aObjects
   *        Source object array. May not be <code>null</code>.
   * @return The aggregated object. May be <code>null</code>.
   */
  @Nullable
  @SuppressWarnings ("unchecked")
  // @SafeVarArgs does not work for default methods
  default DSTTYPE apply (@Nullable final SRCTYPE... aObjects)
  {
    return apply (new CommonsArrayList <> (aObjects));
  }
}
