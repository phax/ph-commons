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

import java.util.Comparator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

/**
 * A special comparator that sorts String, but the longest strings come first.
 * For all strings with an equal length, they are sorted in regular,
 * non-collated order.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class ComparatorStringLongestFirst extends AbstractComparator <String>
{
  private final Comparator <? super String> m_aContentComparator;

  public ComparatorStringLongestFirst ()
  {
    this (null);
  }

  public ComparatorStringLongestFirst (@Nullable final Comparator <? super String> aContentComparator)
  {
    m_aContentComparator = aContentComparator;
  }

  /**
   * @return The content comparator passed in the constructor. May be
   *         <code>null</code>.
   */
  @Nullable
  public final Comparator <? super String> getContentComparator ()
  {
    return m_aContentComparator;
  }

  @Override
  protected int mainCompare (@Nonnull final String sElement1, @Nonnull final String sElement2)
  {
    // The longer, the earlier in the results
    int ret = CompareUtils.compare (sElement2.length (), sElement1.length ());
    if (ret == 0)
    {
      // Important to compare the content as well because with ret==0,
      // elements would eventually be removed (e.g. from a TreeMap)
      if (m_aContentComparator == null)
        ret = sElement1.compareTo (sElement2);
      else
        ret = m_aContentComparator.compare (sElement1, sElement2);
    }
    return ret;
  }
}
