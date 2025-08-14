package com.helger.commons.compare;

import java.util.Locale;

import com.helger.annotation.concurrent.Immutable;
import com.helger.base.compare.CompareHelper;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

@Immutable
public final class CompareHelperExt extends CompareHelper
{
  private CompareHelperExt ()
  {}

  public static int compare (@Nullable final String sStr1,
                             @Nullable final String sStr2,
                             @Nonnull final Locale aSortLocale)
  {
    // Legacy behavior: null values come first
    return compare (sStr1, sStr2, aSortLocale, DEFAULT_NULL_VALUES_COME_FIRST);
  }

  public static int compare (@Nullable final String sStr1,
                             @Nullable final String sStr2,
                             @Nonnull final Locale aSortLocale,
                             final boolean bNullValuesComeFirst)
  {
    return compare (sStr1, sStr2, CollatorHelper.getCollatorSpaceBeforeDot (aSortLocale), bNullValuesComeFirst);
  }

}
