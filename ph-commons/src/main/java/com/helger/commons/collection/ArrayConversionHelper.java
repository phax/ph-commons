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
package com.helger.commons.collection;

import java.util.Collection;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.PresentForCodeCoverage;
import com.helger.commons.annotation.ReturnsMutableCopy;

/**
 * This utility class provides conversions from array objects.
 *
 * @author Philip Helger
 */
@Immutable
public final class ArrayConversionHelper
{
  @PresentForCodeCoverage
  private static final ArrayConversionHelper s_aInstance = new ArrayConversionHelper ();

  private ArrayConversionHelper ()
  {}

  @Nonnull
  @ReturnsMutableCopy
  public static <SRCTYPE, DSTTYPE> DSTTYPE [] newArray (@Nonnull final Collection <? extends SRCTYPE> aList,
                                                        @Nonnull final Function <SRCTYPE, DSTTYPE> aConv,
                                                        @Nonnull final Class <DSTTYPE> aDstClass)
  {
    ValueEnforcer.notNull (aList, "List");
    ValueEnforcer.notNull (aConv, "Converter");
    ValueEnforcer.notNull (aDstClass, "DestClass");

    final DSTTYPE [] ret = ArrayHelper.newArray (aDstClass, aList.size ());
    int i = 0;
    for (final SRCTYPE aObj : aList)
      ret[i++] = aConv.apply (aObj);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <SRCTYPE, DSTTYPE> DSTTYPE [] newArray (@Nullable final SRCTYPE [] aArray,
                                                        @Nonnull final Function <SRCTYPE, DSTTYPE> aConv,
                                                        @Nonnull final Class <DSTTYPE> aDstClass)
  {
    ValueEnforcer.notNull (aConv, "Converter");
    ValueEnforcer.notNull (aDstClass, "DestClass");

    final DSTTYPE [] ret = ArrayHelper.newArray (aDstClass, ArrayHelper.getSize (aArray));
    if (aArray != null)
    {
      int i = 0;
      for (final SRCTYPE aObj : aArray)
        ret[i++] = aConv.apply (aObj);
    }
    return ret;
  }
}
