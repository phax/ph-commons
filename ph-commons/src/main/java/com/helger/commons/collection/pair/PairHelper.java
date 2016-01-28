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
package com.helger.commons.collection.pair;

import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.CollectionHelper;

/**
 * Some pair utils
 *
 * @author Philip Helger
 */
@Immutable
public final class PairHelper
{
  private PairHelper ()
  {}

  @Nonnull
  @ReturnsMutableCopy
  public static <V1 extends Comparable <? super V1>, V2> List <IPair <V1, V2>> getSortedByPairFirst (@Nonnull final Collection <? extends IPair <V1, V2>> aList)
  {
    // get sorted entry list
    return CollectionHelper.getSorted (aList, IPair.getComparatorFirst ());
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <V1, V2 extends Comparable <? super V2>> List <IPair <V1, V2>> getSortedByPairSecond (@Nonnull final Collection <? extends IPair <V1, V2>> aList)
  {
    // get sorted entry list
    return CollectionHelper.getSorted (aList, IPair.getComparatorSecond ());
  }
}
