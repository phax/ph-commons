/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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
package com.helger.collection.commons;

import java.util.Iterator;
import java.util.function.Predicate;

import com.helger.collection.base.FilterIterator;
import com.helger.collection.base.IIterableIterator;

import jakarta.annotation.Nonnull;

/**
 * A specific {@link FilterIterator} that implements {@link ICommonsIterableIterator}
 *
 * @author Philip Helger
 * @param <ELEMENTTYPE>
 *        The type to iterate
 */
public class CommonsFilterIterator <ELEMENTTYPE> extends FilterIterator <ELEMENTTYPE> implements
                                   ICommonsIterableIterator <ELEMENTTYPE>
{
  public CommonsFilterIterator (final IIterableIterator <? extends ELEMENTTYPE> aBaseIter,
                                final Predicate <? super ELEMENTTYPE> aFilter)
  {
    super (aBaseIter, aFilter);
  }

  public CommonsFilterIterator (@Nonnull final Iterable <? extends ELEMENTTYPE> aBaseCont,
                                @Nonnull final Predicate <? super ELEMENTTYPE> aFilter)
  {
    super (aBaseCont, aFilter);
  }

  public CommonsFilterIterator (@Nonnull final Iterator <? extends ELEMENTTYPE> aBaseIter,
                                @Nonnull final Predicate <? super ELEMENTTYPE> aFilter)
  {
    super (aBaseIter, aFilter);
  }
}
