/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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
import java.util.function.Function;

import org.jspecify.annotations.NonNull;

import com.helger.collection.base.IIterableIterator;
import com.helger.collection.base.MapperIterator;

/**
 * A specific {@link MapperIterator} that implements {@link ICommonsIterableIterator}
 *
 * @author Philip Helger
 * @param <SRCTYPE>
 *        The type of the source iterator
 * @param <ELEMENTTYPE>
 *        The type of this iterator
 */
public class CommonsMapperIterator <SRCTYPE, ELEMENTTYPE> extends MapperIterator <SRCTYPE, ELEMENTTYPE> implements
                                   ICommonsIterableIterator <ELEMENTTYPE>
{
  public CommonsMapperIterator (@NonNull final IIterableIterator <? extends SRCTYPE> aBaseIter,
                                @NonNull final Function <? super SRCTYPE, ? extends ELEMENTTYPE> aConverter)
  {
    super (aBaseIter, aConverter);
  }

  public CommonsMapperIterator (@NonNull final Iterable <? extends SRCTYPE> aBaseCont,
                                @NonNull final Function <? super SRCTYPE, ? extends ELEMENTTYPE> aConverter)
  {
    super (aBaseCont, aConverter);
  }

  public CommonsMapperIterator (@NonNull final Iterator <? extends SRCTYPE> aBaseIter,
                                @NonNull final Function <? super SRCTYPE, ? extends ELEMENTTYPE> aConverter)
  {
    super (aBaseIter, aConverter);
  }
}
