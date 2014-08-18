/**
 * Copyright (C) 2006-2014 phloc systems (www.phloc.com)
 * Copyright (C) 2014 Philip Helger (www.helger.com)
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
package com.helger.commons.collections.pair;

import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.annotations.ReturnsMutableCopy;
import com.helger.commons.collections.ContainerHelper;

/**
 * Some pair utils
 * 
 * @author Philip Helger
 */
@Immutable
public final class PairUtils
{
  private PairUtils ()
  {}

  @Nonnull
  @ReturnsMutableCopy
  public static <V1 extends Comparable <? super V1>, V2> List <IReadonlyPair <V1, V2>> getSortedByPairFirst (@Nonnull final Collection <? extends IReadonlyPair <V1, V2>> aList)
  {
    // get sorted entry list
    return ContainerHelper.getSorted (aList, new ComparatorPairFirst <V1, V2> ());
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <V1, V2 extends Comparable <? super V2>> List <IReadonlyPair <V1, V2>> getSortedByPairSecond (@Nonnull final Collection <? extends IReadonlyPair <V1, V2>> aList)
  {
    // get sorted entry list
    return ContainerHelper.getSorted (aList, new ComparatorPairSecond <V1, V2> ());
  }
}
