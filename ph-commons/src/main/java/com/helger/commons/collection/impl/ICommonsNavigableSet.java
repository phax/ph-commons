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
package com.helger.commons.collection.impl;

import java.util.Collections;
import java.util.NavigableSet;

import com.helger.annotation.style.CodingStyleguideUnaware;
import com.helger.annotation.style.ReturnsMutableCopy;

import jakarta.annotation.Nonnull;

/**
 * A special {@link NavigableSet} based interface with extended functionality
 * based on {@link ICommonsSortedSet}.
 *
 * @author Philip Helger
 * @param <ELEMENTTYPE>
 *        Set element type
 */
public interface ICommonsNavigableSet <ELEMENTTYPE> extends NavigableSet <ELEMENTTYPE>, ICommonsSortedSet <ELEMENTTYPE>
{
  @Override
  @Nonnull
  @CodingStyleguideUnaware
  default NavigableSet <ELEMENTTYPE> getAsUnmodifiable ()
  {
    return Collections.unmodifiableNavigableSet (this);
  }

  @Nonnull
  @ReturnsMutableCopy
  ICommonsNavigableSet <ELEMENTTYPE> getClone ();
}
