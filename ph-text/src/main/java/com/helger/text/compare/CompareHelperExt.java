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
package com.helger.text.compare;

import java.util.Locale;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.concurrent.Immutable;
import com.helger.base.compare.CompareHelper;

@Immutable
public final class CompareHelperExt extends CompareHelper
{
  private CompareHelperExt ()
  {}

  public static int compare (@Nullable final String sStr1,
                             @Nullable final String sStr2,
                             @NonNull final Locale aSortLocale)
  {
    // Legacy behavior: null values come first
    return compare (sStr1, sStr2, aSortLocale, DEFAULT_NULL_VALUES_COME_FIRST);
  }

  public static int compare (@Nullable final String sStr1,
                             @Nullable final String sStr2,
                             @NonNull final Locale aSortLocale,
                             final boolean bNullValuesComeFirst)
  {
    return compare (sStr1, sStr2, CollatorHelper.getCollatorSpaceBeforeDot (aSortLocale), bNullValuesComeFirst);
  }

}
