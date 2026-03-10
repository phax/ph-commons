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
package com.helger.text.compare;

import java.util.Locale;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.concurrent.Immutable;
import com.helger.base.compare.CompareHelper;

/**
 * Extended comparison helper that adds locale-aware string comparison using
 * {@link java.text.Collator}.
 *
 * @author Philip Helger
 */
@Immutable
public final class CompareHelperExt extends CompareHelper
{
  private CompareHelperExt ()
  {}

  /**
   * Compare two strings using a locale-aware {@link java.text.Collator}. <code>null</code> values
   * are sorted first by default.
   *
   * @param sStr1
   *        The first string. May be <code>null</code>.
   * @param sStr2
   *        The second string. May be <code>null</code>.
   * @param aSortLocale
   *        The locale to use for collation. May not be <code>null</code>.
   * @return a negative integer, zero, or a positive integer as the first string is less than, equal
   *         to, or greater than the second.
   */
  public static int compare (@Nullable final String sStr1,
                             @Nullable final String sStr2,
                             @NonNull final Locale aSortLocale)
  {
    // Legacy behavior: null values come first
    return compare (sStr1, sStr2, aSortLocale, DEFAULT_NULL_VALUES_COME_FIRST);
  }

  /**
   * Compare two strings using a locale-aware {@link java.text.Collator}, with control over
   * <code>null</code> value ordering.
   *
   * @param sStr1
   *        The first string. May be <code>null</code>.
   * @param sStr2
   *        The second string. May be <code>null</code>.
   * @param aSortLocale
   *        The locale to use for collation. May not be <code>null</code>.
   * @param bNullValuesComeFirst
   *        <code>true</code> if <code>null</code> values should be sorted before non-null values,
   *        <code>false</code> if they should be sorted after.
   * @return a negative integer, zero, or a positive integer as the first string is less than, equal
   *         to, or greater than the second.
   */
  public static int compare (@Nullable final String sStr1,
                             @Nullable final String sStr2,
                             @NonNull final Locale aSortLocale,
                             final boolean bNullValuesComeFirst)
  {
    return compare (sStr1, sStr2, CollatorHelper.getCollatorSpaceBeforeDot (aSortLocale), bNullValuesComeFirst);
  }

}
