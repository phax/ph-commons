/**
 * Copyright (C) 2014-2016 Philip Helger (www.helger.com)
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

import java.util.Comparator;

import javax.annotation.Nonnull;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.string.ToStringGenerator;

/**
 * A special comparator that reverses another comparator
 *
 * @author Philip Helger
 * @param <T>
 *        Data type to compare
 */
public class ReverseComparator <T> implements ISerializableComparator <T>
{
  private final Comparator <? super T> m_aComparator;

  public ReverseComparator (@Nonnull final Comparator <? super T> aComparator)
  {
    m_aComparator = ValueEnforcer.notNull (aComparator, "Comparator");
  }

  /**
   * @return The original comparator as passed in the constructor.
   */
  @Nonnull
  public Comparator <? super T> getOriginalComparator ()
  {
    return m_aComparator;
  }

  public int compare (final T aObj1, final T aObj2)
  {
    // Reverse result, by reversing the object order :)
    return m_aComparator.compare (aObj2, aObj1);
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("comparator", m_aComparator).toString ();
  }

  @Nonnull
  public static <U> ReverseComparator <U> create (@Nonnull final Comparator <U> aComparator)
  {
    return new ReverseComparator <U> (aComparator);
  }
}
