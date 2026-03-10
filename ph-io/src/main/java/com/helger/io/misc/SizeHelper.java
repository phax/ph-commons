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
package com.helger.io.misc;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import org.jspecify.annotations.NonNull;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.base.CGlobal;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.tostring.ToStringGenerator;

/**
 * A utility class that converts sizes into the corresponding Kilobyte, Megabyte
 * etc. notation.<br>
 * This class is not thread-safe, because the {@link DecimalFormat} class is not
 * thread-safe!
 *
 * @author Philip Helger
 */
@NotThreadSafe
public final class SizeHelper
{
  /** Suffix for Bytes */
  public static final String B_SUFFIX = "B";
  /** Suffix for Kilobytes */
  public static final String KB_SUFFIX = "KB";
  /** Suffix for Megabytes */
  public static final String MB_SUFFIX = "MB";
  /** Suffix for Gigabytes */
  public static final String GB_SUFFIX = "GB";
  /** Suffix for Terabytes */
  public static final String TB_SUFFIX = "TB";
  /** Suffix for Petabytes */
  public static final String PB_SUFFIX = "PB";

  private final DecimalFormatSymbols m_aDFS;
  private DecimalFormat m_aDF0;
  private DecimalFormat m_aDF1;
  private DecimalFormat m_aDF2;

  /**
   * Constructor using a display locale to determine the decimal format symbols.
   *
   * @param aDisplayLocale
   *        The locale to use for formatting. May not be <code>null</code>.
   */
  public SizeHelper (@NonNull final Locale aDisplayLocale)
  {
    ValueEnforcer.notNull (aDisplayLocale, "DisplayLocale");
    m_aDFS = DecimalFormatSymbols.getInstance (aDisplayLocale);
  }

  /**
   * Constructor using explicit decimal format symbols.
   *
   * @param aDFS
   *        The decimal format symbols to use. May not be <code>null</code>.
   */
  public SizeHelper (@NonNull final DecimalFormatSymbols aDFS)
  {
    m_aDFS = ValueEnforcer.notNull (aDFS, "DecimalFormatSymbols");
  }

  @NonNull
  private String _format (final long nSize)
  {
    // Lazy init
    if (m_aDF0 == null)
      m_aDF0 = new DecimalFormat ("0", m_aDFS);
    return m_aDF0.format (nSize);
  }

  @NonNull
  private String _format (final double dSize, @Nonnegative final int nDecimals)
  {
    ValueEnforcer.isGE0 (nDecimals, "Decimals");

    if (nDecimals == 0)
      return _format ((long) dSize);

    // Cache for the most common formats
    if (nDecimals == 1)
    {
      if (m_aDF1 == null)
        m_aDF1 = new DecimalFormat ("0.0", m_aDFS);
      return m_aDF1.format (dSize);
    }
    if (nDecimals == 2)
    {
      if (m_aDF2 == null)
        m_aDF2 = new DecimalFormat ("0.00", m_aDFS);
      return m_aDF2.format (dSize);
    }

    // build formatting string with at least 3 decimals
    final StringBuilder aFormat = new StringBuilder ("0.000");
    for (int i = 3; i < nDecimals; ++i)
      aFormat.append ('0');
    return new DecimalFormat (aFormat.toString (), m_aDFS).format (dSize);
  }

  /**
   * Get the passed size formatted as Kilobytes without fraction digits.
   *
   * @param nSize
   *        The size in bytes.
   * @return The formatted string with the KB suffix. Never <code>null</code>.
   */
  @NonNull
  public String getAsKB (final long nSize)
  {
    return _format (nSize / CGlobal.BYTES_PER_KILOBYTE) + KB_SUFFIX;
  }

  /**
   * Get the passed size formatted as Kilobytes with the specified number of fraction digits.
   *
   * @param nSize
   *        The size in bytes.
   * @param nDecimals
   *        The number of fraction digits to use. Must be &ge; 0.
   * @return The formatted string with the KB suffix. Never <code>null</code>.
   */
  @NonNull
  public String getAsKB (final long nSize, @Nonnegative final int nDecimals)
  {
    return _format ((double) nSize / CGlobal.BYTES_PER_KILOBYTE, nDecimals) + KB_SUFFIX;
  }

  /**
   * Get the passed size formatted as Megabytes without fraction digits.
   *
   * @param nSize
   *        The size in bytes.
   * @return The formatted string with the MB suffix. Never <code>null</code>.
   */
  @NonNull
  public String getAsMB (final long nSize)
  {
    return _format (nSize / CGlobal.BYTES_PER_MEGABYTE) + MB_SUFFIX;
  }

  /**
   * Get the passed size formatted as Megabytes with the specified number of fraction digits.
   *
   * @param nSize
   *        The size in bytes.
   * @param nDecimals
   *        The number of fraction digits to use. Must be &ge; 0.
   * @return The formatted string with the MB suffix. Never <code>null</code>.
   */
  @NonNull
  public String getAsMB (final long nSize, @Nonnegative final int nDecimals)
  {
    return _format ((double) nSize / CGlobal.BYTES_PER_MEGABYTE, nDecimals) + MB_SUFFIX;
  }

  /**
   * Get the passed size formatted as Gigabytes without fraction digits.
   *
   * @param nSize
   *        The size in bytes.
   * @return The formatted string with the GB suffix. Never <code>null</code>.
   */
  @NonNull
  public String getAsGB (final long nSize)
  {
    return _format (nSize / CGlobal.BYTES_PER_GIGABYTE) + GB_SUFFIX;
  }

  /**
   * Get the passed size formatted as Gigabytes with the specified number of fraction digits.
   *
   * @param nSize
   *        The size in bytes.
   * @param nDecimals
   *        The number of fraction digits to use. Must be &ge; 0.
   * @return The formatted string with the GB suffix. Never <code>null</code>.
   */
  @NonNull
  public String getAsGB (final long nSize, @Nonnegative final int nDecimals)
  {
    return _format ((double) nSize / CGlobal.BYTES_PER_GIGABYTE, nDecimals) + GB_SUFFIX;
  }

  /**
   * Get the passed size formatted as Terabytes without fraction digits.
   *
   * @param nSize
   *        The size in bytes.
   * @return The formatted string with the TB suffix. Never <code>null</code>.
   */
  @NonNull
  public String getAsTB (final long nSize)
  {
    return _format (nSize / CGlobal.BYTES_PER_TERABYTE) + TB_SUFFIX;
  }

  /**
   * Get the passed size formatted as Terabytes with the specified number of fraction digits.
   *
   * @param nSize
   *        The size in bytes.
   * @param nDecimals
   *        The number of fraction digits to use. Must be &ge; 0.
   * @return The formatted string with the TB suffix. Never <code>null</code>.
   */
  @NonNull
  public String getAsTB (final long nSize, @Nonnegative final int nDecimals)
  {
    return _format ((double) nSize / CGlobal.BYTES_PER_TERABYTE, nDecimals) + TB_SUFFIX;
  }

  /**
   * Get the passed size formatted as Petabytes without fraction digits.
   *
   * @param nSize
   *        The size in bytes.
   * @return The formatted string with the PB suffix. Never <code>null</code>.
   */
  @NonNull
  public String getAsPB (final long nSize)
  {
    return _format (nSize / CGlobal.BYTES_PER_PETABYTE) + PB_SUFFIX;
  }

  /**
   * Get the passed size formatted as Petabytes with the specified number of fraction digits.
   *
   * @param nSize
   *        The size in bytes.
   * @param nDecimals
   *        The number of fraction digits to use. Must be &ge; 0.
   * @return The formatted string with the PB suffix. Never <code>null</code>.
   */
  @NonNull
  public String getAsPB (final long nSize, @Nonnegative final int nDecimals)
  {
    return _format ((double) nSize / CGlobal.BYTES_PER_PETABYTE, nDecimals) + PB_SUFFIX;
  }

  private static void _checkConvertibility (@NonNull final BigInteger aSize)
  {
    if (aSize.compareTo (CGlobal.BIGINT_MAX_LONG) > 0)
      throw new IllegalArgumentException ("The passed BigInteger is too large to be converted into a long value: " + aSize.toString ());
    if (aSize.compareTo (CGlobal.BIGINT_MIN_LONG) < 0)
      throw new IllegalArgumentException ("The passed BigInteger is too small to be converted into a long value: " + aSize.toString ());
  }

  /**
   * Get the best matching formatting of the passed value. No fraction digits
   * will be emitted.
   *
   * @param aSize
   *        The value to be converted to a size value. May not be
   *        <code>null</code>.
   * @return The string representation
   * @throws IllegalArgumentException
   *         If the passed value cannot be fit in a long
   */
  @NonNull
  public String getAsMatching (@NonNull final BigInteger aSize)
  {
    _checkConvertibility (aSize);
    return getAsMatching (aSize.longValue ());
  }

  /**
   * Get the best matching formatting of the passed value.
   *
   * @param aSize
   *        The value to be converted to a size value. May not be
   *        <code>null</code>.
   * @param nDecimals
   *        The number of fraction digits.
   * @return The string representation
   * @throws IllegalArgumentException
   *         If the passed value cannot be fit in a long
   */
  @NonNull
  public String getAsMatching (@NonNull final BigInteger aSize, @Nonnegative final int nDecimals)
  {
    _checkConvertibility (aSize);
    return getAsMatching (aSize.longValue (), nDecimals);
  }

  private static void _checkConvertibility (@NonNull final BigDecimal aSize)
  {
    if (aSize.compareTo (CGlobal.BIGDEC_MAX_LONG) > 0)
      throw new IllegalArgumentException ("The passed BigDecimal is too large to be converted into a long value: " + aSize.toString ());
    if (aSize.compareTo (CGlobal.BIGDEC_MIN_LONG) < 0)
      throw new IllegalArgumentException ("The passed BigDecimal is too small to be converted into a long value: " + aSize.toString ());
  }

  /**
   * Get the best matching formatting of the passed value. No fraction digits
   * will be emitted.
   *
   * @param aSize
   *        The value to be converted to a size value. May not be
   *        <code>null</code>.
   * @return The string representation
   * @throws IllegalArgumentException
   *         If the passed value cannot be fit in a long
   */
  @NonNull
  public String getAsMatching (@NonNull final BigDecimal aSize)
  {
    _checkConvertibility (aSize);
    return getAsMatching (aSize.longValue ());
  }

  /**
   * Get the best matching formatting of the passed value.
   *
   * @param aSize
   *        The value to be converted to a size value. May not be
   *        <code>null</code>.
   * @param nDecimals
   *        The number of fraction digits.
   * @return The string representation
   * @throws IllegalArgumentException
   *         If the passed value cannot be fit in a long
   */
  @NonNull
  public String getAsMatching (@NonNull final BigDecimal aSize, @Nonnegative final int nDecimals)
  {
    _checkConvertibility (aSize);
    return getAsMatching (aSize.longValue (), nDecimals);
  }

  /**
   * Get the best matching formatting of the passed value. No fraction digits will be emitted.
   *
   * @param nSize
   *        The size in bytes.
   * @return The string representation using the best matching unit. Never <code>null</code>.
   */
  @NonNull
  public String getAsMatching (final long nSize)
  {
    if (nSize >= CGlobal.BYTES_PER_PETABYTE)
      return getAsPB (nSize);
    if (nSize >= CGlobal.BYTES_PER_TERABYTE)
      return getAsTB (nSize);
    if (nSize >= CGlobal.BYTES_PER_GIGABYTE)
      return getAsGB (nSize);
    if (nSize >= CGlobal.BYTES_PER_MEGABYTE)
      return getAsMB (nSize);
    if (nSize >= CGlobal.BYTES_PER_KILOBYTE)
      return getAsKB (nSize);
    return _format (nSize) + B_SUFFIX;
  }

  /**
   * Get the best matching formatting of the passed value.
   *
   * @param nSize
   *        The size in bytes.
   * @param nDecimals
   *        The number of fraction digits to use. Must be &ge; 0.
   * @return The string representation using the best matching unit. Never <code>null</code>.
   */
  @NonNull
  public String getAsMatching (final long nSize, @Nonnegative final int nDecimals)
  {
    if (nSize >= CGlobal.BYTES_PER_PETABYTE)
      return getAsPB (nSize, nDecimals);
    if (nSize >= CGlobal.BYTES_PER_TERABYTE)
      return getAsTB (nSize, nDecimals);
    if (nSize >= CGlobal.BYTES_PER_GIGABYTE)
      return getAsGB (nSize, nDecimals);
    if (nSize >= CGlobal.BYTES_PER_MEGABYTE)
      return getAsMB (nSize, nDecimals);
    if (nSize >= CGlobal.BYTES_PER_KILOBYTE)
      return getAsKB (nSize, nDecimals);
    return _format (nSize, nDecimals) + B_SUFFIX;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("dfs", m_aDFS).getToString ();
  }

  /**
   * Get the size helper for the passed locale. The locale determines the
   * formatting of the numeric value.
   *
   * @param aDisplayLocale
   *        The locale to be used.
   * @return The non-<code>null</code> {@link SizeHelper} object for the passed
   *         locale.
   */
  @NonNull
  public static SizeHelper getSizeHelperOfLocale (@NonNull final Locale aDisplayLocale)
  {
    return new SizeHelper (aDisplayLocale);
  }
}
