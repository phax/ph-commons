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
package com.helger.base.hashcode;

import java.util.Arrays;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.PresentForCodeCoverage;
import com.helger.annotation.style.UsedInGeneratedCode;
import com.helger.base.CGlobal;
import com.helger.base.enforce.ValueEnforcer;

/**
 * This class provides the hash code generation for different data types.
 *
 * @author Philip Helger
 */
@Immutable
public final class HashCodeCalculator
{
  /** Use a prime number as the start. */
  public static final int INITIAL_HASHCODE = 17;

  /**
   * Each value is multiplied with this value. 31 because it can easily be optimized to
   * <code>(1 &lt;&lt; 5) - 1</code>.
   */
  public static final int MULTIPLIER = 31;

  /**
   * The hash code value to be used for <code>null</code> values. Do not use 0 as e.g.
   * <code>BigDecimal ("0")</code> also results in a 0 hash code.
   */
  public static final int HASHCODE_NULL = 129;

  @PresentForCodeCoverage
  private static final HashCodeCalculator INSTANCE = new HashCodeCalculator ();

  private HashCodeCalculator ()
  {}

  public static int hashCode (final boolean x)
  {
    return x ? 1231 : 1237;
  }

  public static int hashCode (final byte x)
  {
    return x;
  }

  public static int hashCode (final char x)
  {
    return x;
  }

  public static long hashCode (final double x)
  {
    return x == 0.0 ? 0L : Double.doubleToLongBits (x);
  }

  public static int hashCode (final float x)
  {
    return x == 0.0F ? 0 : Float.floatToIntBits (x);
  }

  public static int hashCode (final short x)
  {
    return x;
  }

  @UsedInGeneratedCode
  public static int hashCode (@Nullable final Object x)
  {
    return x == null ? HASHCODE_NULL : x.hashCode ();
  }

  /**
   * Atomic type hash code generation.
   *
   * @param nPrevHashCode
   *        The previous hash code used as the basis for calculation
   * @param x
   *        Array to add
   * @return The updated hash code
   */
  public static int append (final int nPrevHashCode, final boolean x)
  {
    return append (nPrevHashCode, hashCode (x));
  }

  /**
   * Atomic type hash code generation.
   *
   * @param nPrevHashCode
   *        The previous hash code used as the basis for calculation
   * @param x
   *        Array to add
   * @return The updated hash code
   */
  public static int append (final int nPrevHashCode, final byte x)
  {
    return append (nPrevHashCode, hashCode (x));
  }

  /**
   * Atomic type hash code generation.
   *
   * @param nPrevHashCode
   *        The previous hash code used as the basis for calculation
   * @param x
   *        Array to add
   * @return The updated hash code
   */
  public static int append (final int nPrevHashCode, final char x)
  {
    return append (nPrevHashCode, hashCode (x));
  }

  /**
   * Atomic type hash code generation.
   *
   * @param nPrevHashCode
   *        The previous hash code used as the basis for calculation
   * @param x
   *        Array to add
   * @return The updated hash code
   */
  public static int append (final int nPrevHashCode, final double x)
  {
    return append (nPrevHashCode, hashCode (x));
  }

  /**
   * Atomic type hash code generation.
   *
   * @param nPrevHashCode
   *        The previous hash code used as the basis for calculation
   * @param x
   *        Array to add
   * @return The updated hash code
   */
  public static int append (final int nPrevHashCode, final float x)
  {
    return append (nPrevHashCode, hashCode (x));
  }

  /**
   * Atomic type hash code generation.
   *
   * @param nPrevHashCode
   *        The previous hash code used as the basis for calculation
   * @param x
   *        Array to add
   * @return The updated hash code
   */
  public static int append (final int nPrevHashCode, final int x)
  {
    return nPrevHashCode * MULTIPLIER + x;
  }

  /**
   * Atomic type hash code generation.
   *
   * @param nPrevHashCode
   *        The previous hash code used as the basis for calculation
   * @param x
   *        Array to add
   * @return The updated hash code
   */
  public static int append (final int nPrevHashCode, final long x)
  {
    final int nTemp = append (nPrevHashCode, (int) (x >>> CGlobal.BITS_PER_INT));
    return append (nTemp, (int) (x & 0xffffffffL));
  }

  /**
   * Atomic type hash code generation.
   *
   * @param nPrevHashCode
   *        The previous hash code used as the basis for calculation
   * @param x
   *        Array to add
   * @return The updated hash code
   */
  public static int append (final int nPrevHashCode, final short x)
  {
    return append (nPrevHashCode, hashCode (x));
  }

  /**
   * Object hash code generation.
   *
   * @param nPrevHashCode
   *        The previous hash code used as the basis for calculation
   * @param x
   *        Object to add. May be <code>null</code>.
   * @return The updated hash code
   */
  public static int append (final int nPrevHashCode, @Nullable final Object x)
  {
    return append (nPrevHashCode, hashCode (x));
  }

  public static int hashCode (final boolean @NonNull [] x, @Nonnegative final int nOfs, @Nonnegative final int nLen)
  {
    ValueEnforcer.isArrayOfsLen (x, nOfs, nLen);
    int ret = INITIAL_HASHCODE;
    final int nMax = nOfs + nLen;
    for (int i = nOfs; i < nMax; ++i)
      ret = append (ret, x[i]);
    return ret;
  }

  public static int hashCode (final byte @NonNull [] x, @Nonnegative final int nOfs, @Nonnegative final int nLen)
  {
    ValueEnforcer.isArrayOfsLen (x, nOfs, nLen);
    int ret = INITIAL_HASHCODE;
    final int nMax = nOfs + nLen;
    for (int i = nOfs; i < nMax; ++i)
      ret = append (ret, x[i]);
    return ret;
  }

  public static int hashCode (final char @NonNull [] x, @Nonnegative final int nOfs, @Nonnegative final int nLen)
  {
    ValueEnforcer.isArrayOfsLen (x, nOfs, nLen);
    int ret = INITIAL_HASHCODE;
    final int nMax = nOfs + nLen;
    for (int i = nOfs; i < nMax; ++i)
      ret = append (ret, x[i]);
    return ret;
  }

  public static int hashCode (final double @NonNull [] x, @Nonnegative final int nOfs, @Nonnegative final int nLen)
  {
    ValueEnforcer.isArrayOfsLen (x, nOfs, nLen);
    int ret = INITIAL_HASHCODE;
    final int nMax = nOfs + nLen;
    for (int i = nOfs; i < nMax; ++i)
      ret = append (ret, x[i]);
    return ret;
  }

  public static int hashCode (final float @NonNull [] x, @Nonnegative final int nOfs, @Nonnegative final int nLen)
  {
    ValueEnforcer.isArrayOfsLen (x, nOfs, nLen);
    int ret = INITIAL_HASHCODE;
    final int nMax = nOfs + nLen;
    for (int i = nOfs; i < nMax; ++i)
      ret = append (ret, x[i]);
    return ret;
  }

  public static int hashCode (final int @NonNull [] x, @Nonnegative final int nOfs, @Nonnegative final int nLen)
  {
    ValueEnforcer.isArrayOfsLen (x, nOfs, nLen);
    int ret = INITIAL_HASHCODE;
    final int nMax = nOfs + nLen;
    for (int i = nOfs; i < nMax; ++i)
      ret = append (ret, x[i]);
    return ret;
  }

  public static int hashCode (final long @NonNull [] x, @Nonnegative final int nOfs, @Nonnegative final int nLen)
  {
    ValueEnforcer.isArrayOfsLen (x, nOfs, nLen);
    int ret = INITIAL_HASHCODE;
    final int nMax = nOfs + nLen;
    for (int i = nOfs; i < nMax; ++i)
      ret = append (ret, x[i]);
    return ret;
  }

  public static int hashCode (final short @NonNull [] x, @Nonnegative final int nOfs, @Nonnegative final int nLen)
  {
    ValueEnforcer.isArrayOfsLen (x, nOfs, nLen);
    int ret = INITIAL_HASHCODE;
    final int nMax = nOfs + nLen;
    for (int i = nOfs; i < nMax; ++i)
      ret = append (ret, x[i]);
    return ret;
  }

  public static int hashCode (@NonNull final Object [] x, @Nonnegative final int nOfs, @Nonnegative final int nLen)
  {
    ValueEnforcer.isArrayOfsLen (x, nOfs, nLen);
    int ret = INITIAL_HASHCODE;
    final int nMax = nOfs + nLen;
    for (int i = nOfs; i < nMax; ++i)
      ret = append (ret, x[i]);
    return ret;
  }

  /**
   * Atomic type hash code generation.
   *
   * @param nPrevHashCode
   *        The previous hash code used as the basis for calculation
   * @param x
   *        Array to add
   * @return The updated hash code
   */
  public static int append (final int nPrevHashCode, final boolean @Nullable [] x)
  {
    return append (nPrevHashCode, x == null ? HASHCODE_NULL : Arrays.hashCode (x));
  }

  /**
   * Atomic type hash code generation.
   *
   * @param nPrevHashCode
   *        The previous hash code used as the basis for calculation
   * @param x
   *        Array to add
   * @param nOfs
   *        Offset to start from. Must be &ge; 0.
   * @param nLen
   *        Number of array items to use. Must be &ge; 0.
   * @return The updated hash code
   */
  public static int append (final int nPrevHashCode,
                            final boolean @Nullable [] x,
                            @Nonnegative final int nOfs,
                            @Nonnegative final int nLen)
  {
    return append (nPrevHashCode, x == null ? HASHCODE_NULL : hashCode (x, nOfs, nLen));
  }

  /**
   * Atomic type hash code generation.
   *
   * @param nPrevHashCode
   *        The previous hash code used as the basis for calculation
   * @param x
   *        Array to add
   * @return The updated hash code
   */
  public static int append (final int nPrevHashCode, final byte @Nullable [] x)
  {
    return append (nPrevHashCode, x == null ? HASHCODE_NULL : Arrays.hashCode (x));
  }

  /**
   * Atomic type hash code generation.
   *
   * @param nPrevHashCode
   *        The previous hash code used as the basis for calculation
   * @param x
   *        Array to add
   * @param nOfs
   *        Offset to start from. Must be &ge; 0.
   * @param nLen
   *        Number of array items to use. Must be &ge; 0.
   * @return The updated hash code
   */
  public static int append (final int nPrevHashCode,
                            final byte @Nullable [] x,
                            @Nonnegative final int nOfs,
                            @Nonnegative final int nLen)
  {
    return append (nPrevHashCode, x == null ? HASHCODE_NULL : hashCode (x, nOfs, nLen));
  }

  /**
   * Atomic type hash code generation.
   *
   * @param nPrevHashCode
   *        The previous hash code used as the basis for calculation
   * @param x
   *        Array to add
   * @return The updated hash code
   */
  public static int append (final int nPrevHashCode, final char @Nullable [] x)
  {
    return append (nPrevHashCode, x == null ? HASHCODE_NULL : Arrays.hashCode (x));
  }

  /**
   * Atomic type hash code generation.
   *
   * @param nPrevHashCode
   *        The previous hash code used as the basis for calculation
   * @param x
   *        Array to add
   * @param nOfs
   *        Offset to start from. Must be &ge; 0.
   * @param nLen
   *        Number of array items to use. Must be &ge; 0.
   * @return The updated hash code
   */
  public static int append (final int nPrevHashCode,
                            final char @Nullable [] x,
                            @Nonnegative final int nOfs,
                            @Nonnegative final int nLen)
  {
    return append (nPrevHashCode, x == null ? HASHCODE_NULL : hashCode (x, nOfs, nLen));
  }

  /**
   * Atomic type hash code generation.
   *
   * @param nPrevHashCode
   *        The previous hash code used as the basis for calculation
   * @param x
   *        Array to add
   * @return The updated hash code
   */
  public static int append (final int nPrevHashCode, final double @Nullable [] x)
  {
    return append (nPrevHashCode, x == null ? HASHCODE_NULL : Arrays.hashCode (x));
  }

  /**
   * Atomic type hash code generation.
   *
   * @param nPrevHashCode
   *        The previous hash code used as the basis for calculation
   * @param x
   *        Array to add
   * @param nOfs
   *        Offset to start from. Must be &ge; 0.
   * @param nLen
   *        Number of array items to use. Must be &ge; 0.
   * @return The updated hash code
   */
  public static int append (final int nPrevHashCode,
                            final double @Nullable [] x,
                            @Nonnegative final int nOfs,
                            @Nonnegative final int nLen)
  {
    return append (nPrevHashCode, x == null ? HASHCODE_NULL : hashCode (x, nOfs, nLen));
  }

  /**
   * Atomic type hash code generation.
   *
   * @param nPrevHashCode
   *        The previous hash code used as the basis for calculation
   * @param x
   *        Array to add
   * @return The updated hash code
   */
  public static int append (final int nPrevHashCode, final float @Nullable [] x)
  {
    return append (nPrevHashCode, x == null ? HASHCODE_NULL : Arrays.hashCode (x));
  }

  /**
   * Atomic type hash code generation.
   *
   * @param nPrevHashCode
   *        The previous hash code used as the basis for calculation
   * @param x
   *        Array to add
   * @param nOfs
   *        Offset to start from. Must be &ge; 0.
   * @param nLen
   *        Number of array items to use. Must be &ge; 0.
   * @return The updated hash code
   */
  public static int append (final int nPrevHashCode,
                            final float @Nullable [] x,
                            @Nonnegative final int nOfs,
                            @Nonnegative final int nLen)
  {
    return append (nPrevHashCode, x == null ? HASHCODE_NULL : hashCode (x, nOfs, nLen));
  }

  /**
   * Atomic type hash code generation.
   *
   * @param nPrevHashCode
   *        The previous hash code used as the basis for calculation
   * @param x
   *        Array to add
   * @return The updated hash code
   */
  public static int append (final int nPrevHashCode, final int @Nullable [] x)
  {
    return append (nPrevHashCode, x == null ? HASHCODE_NULL : Arrays.hashCode (x));
  }

  /**
   * Atomic type hash code generation.
   *
   * @param nPrevHashCode
   *        The previous hash code used as the basis for calculation
   * @param x
   *        Array to add
   * @param nOfs
   *        Offset to start from. Must be &ge; 0.
   * @param nLen
   *        Number of array items to use. Must be &ge; 0.
   * @return The updated hash code
   */
  public static int append (final int nPrevHashCode,
                            final int @Nullable [] x,
                            @Nonnegative final int nOfs,
                            @Nonnegative final int nLen)
  {
    return append (nPrevHashCode, x == null ? HASHCODE_NULL : hashCode (x, nOfs, nLen));
  }

  /**
   * Atomic type hash code generation.
   *
   * @param nPrevHashCode
   *        The previous hash code used as the basis for calculation
   * @param x
   *        Array to add
   * @return The updated hash code
   */
  public static int append (final int nPrevHashCode, final long @Nullable [] x)
  {
    return append (nPrevHashCode, x == null ? HASHCODE_NULL : Arrays.hashCode (x));
  }

  /**
   * Atomic type hash code generation.
   *
   * @param nPrevHashCode
   *        The previous hash code used as the basis for calculation
   * @param x
   *        Array to add
   * @param nOfs
   *        Offset to start from. Must be &ge; 0.
   * @param nLen
   *        Number of array items to use. Must be &ge; 0.
   * @return The updated hash code
   */
  public static int append (final int nPrevHashCode,
                            final long @Nullable [] x,
                            @Nonnegative final int nOfs,
                            @Nonnegative final int nLen)
  {
    return append (nPrevHashCode, x == null ? HASHCODE_NULL : hashCode (x, nOfs, nLen));
  }

  /**
   * Atomic type hash code generation.
   *
   * @param nPrevHashCode
   *        The previous hash code used as the basis for calculation
   * @param x
   *        Array to add
   * @return The updated hash code
   */
  public static int append (final int nPrevHashCode, final short @Nullable [] x)
  {
    return append (nPrevHashCode, x == null ? HASHCODE_NULL : Arrays.hashCode (x));
  }

  /**
   * Atomic type hash code generation.
   *
   * @param nPrevHashCode
   *        The previous hash code used as the basis for calculation
   * @param x
   *        Array to add
   * @param nOfs
   *        Offset to start from. Must be &ge; 0.
   * @param nLen
   *        Number of array items to use. Must be &ge; 0.
   * @return The updated hash code
   */
  public static int append (final int nPrevHashCode,
                            final short @Nullable [] x,
                            @Nonnegative final int nOfs,
                            @Nonnegative final int nLen)
  {
    return append (nPrevHashCode, x == null ? HASHCODE_NULL : hashCode (x, nOfs, nLen));
  }

  /**
   * Array hash code generation.
   *
   * @param nPrevHashCode
   *        The previous hash code used as the basis for calculation
   * @param x
   *        Object array to add. May be <code>null</code>.
   * @return The updated hash code
   */
  public static int append (final int nPrevHashCode, @Nullable final Object [] x)
  {
    return append (nPrevHashCode, x == null ? HASHCODE_NULL : Arrays.hashCode (x));
  }

  /**
   * Atomic type hash code generation.
   *
   * @param nPrevHashCode
   *        The previous hash code used as the basis for calculation
   * @param x
   *        Array to add
   * @param nOfs
   *        Offset to start from. Must be &ge; 0.
   * @param nLen
   *        Number of array items to use. Must be &ge; 0.
   * @return The updated hash code
   */
  public static int append (final int nPrevHashCode,
                            @Nullable final Object [] x,
                            @Nonnegative final int nOfs,
                            @Nonnegative final int nLen)
  {
    return append (nPrevHashCode, x == null ? HASHCODE_NULL : hashCode (x, nOfs, nLen));
  }
}
