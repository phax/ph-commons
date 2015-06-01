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

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

/**
 * A special comparator that sorts String, but the shortest strings come first.
 * For all strings with an equal length, they are sorted in regular,
 * non-collated order.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class ComparatorStringShortestFirst extends AbstractComparator <String>
{
  public ComparatorStringShortestFirst ()
  {}

  @Override
  protected int mainCompare (@Nonnull final String sElement1, @Nonnull final String sElement2)
  {
    // The shorter, the earlier in the results
    int ret = CompareUtils.compare (sElement1.length (), sElement2.length ());
    if (ret == 0)
    {
      // Important to compare the content as well because with ret==0,
      // elements would eventually be removed (e.g. from a TreeMap)
      ret = sElement1.compareTo (sElement2);
    }
    return ret;
  }
}
