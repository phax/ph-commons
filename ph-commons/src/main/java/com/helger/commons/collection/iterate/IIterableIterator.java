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
package com.helger.commons.collection.iterate;

import java.util.Iterator;

import javax.annotation.Nonnull;

import com.helger.commons.collection.impl.ICommonsIterable;
import com.helger.commons.functional.IFunction;
import com.helger.commons.functional.IPredicate;

/**
 * This is a merged interface of {@link Iterator} and {@link Iterable} for
 * simpler usage of iterators in the new Java 1.5 "for" constructs.
 *
 * @author Philip Helger
 * @param <ELEMENTTYPE>
 *        The type of object to iterate
 */
public interface IIterableIterator <ELEMENTTYPE> extends ICommonsIterable <ELEMENTTYPE>, Iterator <ELEMENTTYPE>
{
  @Nonnull
  default Iterator <ELEMENTTYPE> iterator ()
  {
    return this;
  }

  @Nonnull
  default IIterableIterator <ELEMENTTYPE> withFilter (@Nonnull final IPredicate <? super ELEMENTTYPE> aFilter)
  {
    return new FilterIterator <> (this, aFilter);
  }

  @Nonnull
  default <DSTTYPE> IIterableIterator <DSTTYPE> withMapper (@Nonnull final IFunction <? super ELEMENTTYPE, ? extends DSTTYPE> aMapper)
  {
    return new MapperIterator <> (this, aMapper);
  }
}
