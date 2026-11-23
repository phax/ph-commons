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
package com.helger.unittest.property;

import java.util.function.Function;
import java.util.function.Predicate;

import org.jspecify.annotations.NonNull;

/**
 * A generator creates values from a {@link ChoiceSequence}. Implementations must be deterministic:
 * the same choices must always create the same value. To shrink well, smaller choices must create
 * simpler values. Use the factory methods in {@link Generators} as building blocks.
 *
 * @author Philip Helger
 * @param <T>
 *        The type of the generated values
 */
@FunctionalInterface
public interface IGenerator <T>
{
  /** The number of values {@link #filter(Predicate)} tries before the sample is rejected */
  int MAX_FILTER_ATTEMPTS = 100;

  /**
   * Generate a value.
   *
   * @param aChoices
   *        The source of all choices. May not be <code>null</code>.
   * @return The generated value.
   * @throws SampleRejectedException
   *         If no value could be generated
   */
  T generate (@NonNull ChoiceSequence aChoices);

  /**
   * Create a generator that converts each value. Shrinking still works, because the mapping is
   * applied to the shrunk value.
   *
   * @param <U>
   *        The target type
   * @param aMapper
   *        The conversion function. May not be <code>null</code>.
   * @return The new generator. Never <code>null</code>.
   */
  @NonNull
  default <U> IGenerator <U> map (@NonNull final Function <? super T, ? extends U> aMapper)
  {
    return aChoices -> aMapper.apply (generate (aChoices));
  }

  /**
   * Create a generator that only returns values matching the filter. Prefer generators that create
   * matching values directly, as each rejected value costs time.
   *
   * @param aFilter
   *        The filter to apply. May not be <code>null</code>.
   * @return The new generator. Never <code>null</code>.
   */
  @NonNull
  default IGenerator <T> filter (@NonNull final Predicate <? super T> aFilter)
  {
    return aChoices -> {
      for (int i = 0; i < MAX_FILTER_ATTEMPTS; ++i)
      {
        final T aValue = generate (aChoices);
        if (aFilter.test (aValue))
          return aValue;
      }
      throw new SampleRejectedException ("The filter rejected " + MAX_FILTER_ATTEMPTS + " values in a row");
    };
  }

  /**
   * Create a generator that uses each value to select the next generator, e.g. to first generate a
   * length and then an array of that length.
   *
   * @param <U>
   *        The target type
   * @param aMapper
   *        The function returning the next generator. May not be <code>null</code>.
   * @return The new generator. Never <code>null</code>.
   */
  @NonNull
  default <U> IGenerator <U> flatMap (@NonNull final Function <? super T, ? extends IGenerator <U>> aMapper)
  {
    return aChoices -> aMapper.apply (generate (aChoices)).generate (aChoices);
  }
}
