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
package com.helger.commons.collections.triple;

import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.annotations.ReturnsMutableCopy;
import com.helger.commons.collections.CollectionHelper;

/**
 * Some triple utils
 *
 * @author Philip Helger
 */
@Immutable
public final class TripleUtils
{
  private TripleUtils ()
  {}

  @Nonnull
  @ReturnsMutableCopy
  public static <V1 extends Comparable <? super V1>, V2, V3> List <IReadonlyTriple <V1, V2, V3>> getSortedByTripleFirst (@Nonnull final Collection <? extends IReadonlyTriple <V1, V2, V3>> aList)
  {
    // get sorted entry list
    return CollectionHelper.getSorted (aList, new ComparatorTripleFirst <V1, V2, V3> ());
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <V1, V2 extends Comparable <? super V2>, V3> List <IReadonlyTriple <V1, V2, V3>> getSortedByTripleSecond (@Nonnull final Collection <? extends IReadonlyTriple <V1, V2, V3>> aList)
  {
    // get sorted entry list
    return CollectionHelper.getSorted (aList, new ComparatorTripleSecond <V1, V2, V3> ());
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <V1, V2, V3 extends Comparable <? super V3>> List <IReadonlyTriple <V1, V2, V3>> getSortedByTripleThird (@Nonnull final Collection <? extends IReadonlyTriple <V1, V2, V3>> aList)
  {
    // get sorted entry list
    return CollectionHelper.getSorted (aList, new ComparatorTripleThird <V1, V2, V3> ());
  }
}
