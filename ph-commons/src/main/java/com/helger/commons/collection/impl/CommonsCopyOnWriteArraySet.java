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
package com.helger.commons.collection.impl;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.ReturnsMutableCopy;

/**
 * A special {@link CopyOnWriteArraySet} implementation based on
 * {@link ICommonsSet}.
 *
 * @author Philip Helger
 * @param <ELEMENTTYPE>
 *        Set element type
 */
public class CommonsCopyOnWriteArraySet <ELEMENTTYPE> extends CopyOnWriteArraySet <ELEMENTTYPE> implements
                                        ICommonsSet <ELEMENTTYPE>
{
  public CommonsCopyOnWriteArraySet ()
  {}

  public CommonsCopyOnWriteArraySet (@Nullable final Collection <? extends ELEMENTTYPE> aCont)
  {
    if (aCont != null)
      addAll (aCont);
  }

  public CommonsCopyOnWriteArraySet (@Nullable final Iterable <? extends ELEMENTTYPE> aIterable)
  {
    addAll (aIterable);
  }

  public <SRCTYPE> CommonsCopyOnWriteArraySet (@Nullable final Iterable <? extends SRCTYPE> aValues,
                                               @Nonnull final Function <? super SRCTYPE, ? extends ELEMENTTYPE> aMapper)
  {
    addAllMapped (aValues, aMapper);
  }

  public CommonsCopyOnWriteArraySet (@Nullable final ELEMENTTYPE aValue)
  {
    add (aValue);
  }

  @SafeVarargs
  public CommonsCopyOnWriteArraySet (@Nullable final ELEMENTTYPE... aValues)
  {
    addAll (aValues);
  }

  public <SRCTYPE> CommonsCopyOnWriteArraySet (@Nullable final SRCTYPE [] aValues,
                                               @Nonnull final Function <? super SRCTYPE, ? extends ELEMENTTYPE> aMapper)
  {
    addAllMapped (aValues, aMapper);
  }

  @Override
  @Nonnull
  @ReturnsMutableCopy
  public <T> CommonsCopyOnWriteArraySet <T> createInstance ()
  {
    return new CommonsCopyOnWriteArraySet <> ();
  }

  @Nonnull
  @ReturnsMutableCopy
  public CommonsCopyOnWriteArraySet <ELEMENTTYPE> getClone ()
  {
    return new CommonsCopyOnWriteArraySet <> (this);
  }
}
