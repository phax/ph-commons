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
package com.helger.unittest.property;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.PresentForCodeCoverage;

/**
 * Factory methods for the basic generators. All of them shrink towards their "simplest" value: 0 or
 * the value closest to 0 for numbers, <code>false</code> for booleans, the first element for a
 * selection and the shortest allowed length for arrays, strings and lists.
 * <p>
 * Numbers are generated with a mix of uniform values, small values and edge cases (minimum, maximum,
 * 0, &plusmn;1), as boundary values find most bugs.
 *
 * @author Philip Helger
 */
@Immutable
public final class Generators
{
  /** The default maximum size of generated arrays, strings and lists */
  public static final int DEFAULT_MAX_SIZE = 1000;
  /** The upper limit for the average size of generated arrays, strings and lists */
  public static final int MAX_AVERAGE_SIZE = 32;

  /** Highest Unicode code point, excluding the 2048 surrogate code points */
  private static final int MAX_CODE_POINT_WITHOUT_SURROGATES = Character.MAX_CODE_POINT -
                                                               (Character.MAX_SURROGATE - Character.MIN_SURROGATE + 1);

  @PresentForCodeCoverage
  private static final Generators INSTANCE = new Generators ();

  private Generators ()
  {}

  /**
   * Draw a value in the range. The choice 0 maps to the value closest to 0. The hint is the value
   * that should be generated; it is only used while generating.
   */
  private static long _drawTowardsOrigin (@NonNull final ChoiceSequence aChoices,
                                          final long nMin,
                                          final long nMax,
                                          final long nHint)
  {
    if (nMin >= 0)
      return nMin + aChoices.draw (nMax - nMin, nHint - nMin);
    if (nMax <= 0)
      return nMax - aChoices.draw (nMax - nMin, nMax - nHint);
    // The range contains 0: first the sign (0 is positive), then the magnitude
    if (aChoices.draw (1, nHint < 0 ? 1 : 0) == 0)
      return aChoices.draw (nMax, nHint);
    return -aChoices.draw (-nMin, -nHint);
  }

  private static boolean _contains (final long @NonNull [] aArray, final int nCount, final long nValue)
  {
    for (int i = 0; i < nCount; ++i)
      if (aArray[i] == nValue)
        return true;
    return false;
  }

  private static long @NonNull [] _getEdgeCases (final long nMin, final long nMax)
  {
    final long nOrigin = Math.max (nMin, Math.min (nMax, 0));
    final long [] aCandidates = { nOrigin, nMin, nMax, nOrigin + 1, nOrigin - 1 };
    final long [] ret = new long [aCandidates.length];
    int nCount = 0;
    for (final long nCandidate : aCandidates)
      if (nCandidate >= nMin && nCandidate <= nMax && !_contains (ret, nCount, nCandidate))
        ret[nCount++] = nCandidate;
    return Arrays.copyOf (ret, nCount);
  }

  /**
   * Pick the value to generate: mostly uniform, sometimes small, sometimes an edge case.
   */
  private static long _pickHint (@NonNull final ChoiceSequence aChoices,
                                 final long nMin,
                                 final long nMax,
                                 final long @NonNull [] aEdgeCases)
  {
    final long nMode = aChoices.nextUnrecordedRandom (9);
    if (nMode >= 8)
      return aEdgeCases[(int) aChoices.nextUnrecordedRandom (aEdgeCases.length - 1)];
    final long nLow;
    final long nHigh;
    if (nMode == 7)
    {
      // Small values are those between -255 and 255, if the range allows it
      nLow = Math.max (nMin, Math.min (nMax, -255));
      nHigh = Math.min (nMax, Math.max (nMin, 255));
    }
    else
    {
      nLow = nMin;
      nHigh = nMax;
    }
    return nLow + aChoices.nextUnrecordedRandom (nHigh - nLow);
  }

  /**
   * Generate int values in the passed range. The distribution only influences generation; the
   * recorded choices are just sign and magnitude, so that edge cases shrink like any other value.
   *
   * @param nMin
   *        Minimum value, inclusive.
   * @param nMax
   *        Maximum value, inclusive. Must be &ge; <code>nMin</code>.
   * @return The generator. Never <code>null</code>.
   */
  @NonNull
  public static IGenerator <Integer> ints (final int nMin, final int nMax)
  {
    if (nMin > nMax)
      throw new IllegalArgumentException ("Min " + nMin + " must be <= max " + nMax);

    final long [] aEdgeCases = _getEdgeCases (nMin, nMax);
    return aChoices -> {
      final long nHint = aChoices.isGenerating () ? _pickHint (aChoices, nMin, nMax, aEdgeCases) : 0;
      return Integer.valueOf ((int) _drawTowardsOrigin (aChoices, nMin, nMax, nHint));
    };
  }

  /**
   * @return A generator for all int values. Never <code>null</code>.
   */
  @NonNull
  public static IGenerator <Integer> ints ()
  {
    return ints (Integer.MIN_VALUE, Integer.MAX_VALUE);
  }

  /**
   * Generate long values in the passed range. The distribution and the shrinking behaviour are the
   * same as for {@link #ints(int, int)}.
   *
   * @param nMin
   *        Minimum value, inclusive.
   * @param nMax
   *        Maximum value, inclusive. Must be &ge; <code>nMin</code>, and the difference must be
   *        smaller than {@link Long#MAX_VALUE} - use {@link #longs()} for the full range.
   * @return The generator. Never <code>null</code>.
   */
  @NonNull
  public static IGenerator <Long> longs (final long nMin, final long nMax)
  {
    if (nMin > nMax)
      throw new IllegalArgumentException ("Min " + nMin + " must be <= max " + nMax);
    if (nMax - nMin < 0 || nMax - nMin == Long.MAX_VALUE)
      throw new IllegalArgumentException ("The range from " + nMin + " to " + nMax + " is too large");

    final long [] aEdgeCases = _getEdgeCases (nMin, nMax);
    return aChoices -> {
      final long nHint = aChoices.isGenerating () ? _pickHint (aChoices, nMin, nMax, aEdgeCases) : 0;
      return Long.valueOf (_drawTowardsOrigin (aChoices, nMin, nMax, nHint));
    };
  }

  /**
   * @return A generator for all long values, combined from two int values for the high and the low
   *         32 bits. It shrinks towards 0. Never <code>null</code>.
   */
  @NonNull
  public static IGenerator <Long> longs ()
  {
    final IGenerator <Integer> aInts = ints ();
    return aChoices -> {
      final long nHigh = aInts.generate (aChoices).intValue ();
      final long nLow = aInts.generate (aChoices).intValue () & 0xffff_ffffL;
      return Long.valueOf ((nHigh << 32) | nLow);
    };
  }

  /**
   * Generate byte values in the passed range.
   *
   * @param nMin
   *        Minimum value, inclusive.
   * @param nMax
   *        Maximum value, inclusive. Must be &ge; <code>nMin</code>.
   * @return The generator. Never <code>null</code>.
   */
  @NonNull
  public static IGenerator <Byte> bytes (final byte nMin, final byte nMax)
  {
    return ints (nMin, nMax).map (aValue -> Byte.valueOf (aValue.byteValue ()));
  }

  /**
   * @return A generator for all byte values. Never <code>null</code>.
   */
  @NonNull
  public static IGenerator <Byte> bytes ()
  {
    return bytes (Byte.MIN_VALUE, Byte.MAX_VALUE);
  }

  /**
   * @return A generator for <code>false</code> and <code>true</code>, shrinking to
   *         <code>false</code>. Never <code>null</code>.
   */
  @NonNull
  public static IGenerator <Boolean> booleans ()
  {
    return aChoices -> Boolean.valueOf (aChoices.draw (1) == 1);
  }

  /**
   * @param <T>
   *        The element type
   * @param aValue
   *        The value to return. May be <code>null</code>.
   * @return A generator that always returns the passed value. Never <code>null</code>.
   */
  @NonNull
  public static <T> IGenerator <T> constant (@Nullable final T aValue)
  {
    return aChoices -> aValue;
  }

  /**
   * @param <T>
   *        The element type
   * @param aValues
   *        The values to choose from. May neither be <code>null</code> nor empty.
   * @return A generator selecting one of the passed values, shrinking towards the first one. Never
   *         <code>null</code>.
   */
  @NonNull
  @SafeVarargs
  public static <T> IGenerator <T> of (@NonNull final T... aValues)
  {
    if (aValues.length == 0)
      throw new IllegalArgumentException ("At least one value is needed");
    final T [] aCopy = aValues.clone ();
    return aChoices -> aCopy[(int) aChoices.draw (aCopy.length - 1)];
  }

  /**
   * @param <T>
   *        The element type
   * @param aGenerators
   *        The generators to choose from. May neither be <code>null</code> nor empty.
   * @return A generator that uses one of the passed generators, shrinking towards the first one.
   *         Never <code>null</code>.
   */
  @NonNull
  @SafeVarargs
  public static <T> IGenerator <T> oneOf (@NonNull final IGenerator <? extends T>... aGenerators)
  {
    if (aGenerators.length == 0)
      throw new IllegalArgumentException ("At least one generator is needed");
    final IGenerator <? extends T> [] aCopy = aGenerators.clone ();
    return aChoices -> aCopy[(int) aChoices.draw (aCopy.length - 1)].generate (aChoices);
  }

  /**
   * Draw the elements of a collection. Each optional element is preceded by a "continue" choice where
   * 0 means "stop". Shrinking that choice to 0 cuts the collection, and deleting the choices of an
   * element removes just this element.
   */
  private static void _drawElements (@NonNull final ChoiceSequence aChoices,
                                     @Nonnegative final int nMinSize,
                                     @Nonnegative final int nMaxSize,
                                     @NonNull final Runnable aAddElement)
  {
    final int nAverageSize = Math.max (1, Math.min (MAX_AVERAGE_SIZE, nMaxSize - nMinSize));
    for (int nCount = 0; nCount < nMaxSize; ++nCount)
    {
      if (nCount >= nMinSize && aChoices.draw (nAverageSize) == 0)
        break;
      aAddElement.run ();
    }
  }

  private static void _checkSizes (final int nMinSize, final int nMaxSize)
  {
    if (nMinSize < 0)
      throw new IllegalArgumentException ("MinSize must be >= 0: " + nMinSize);
    if (nMinSize > nMaxSize)
      throw new IllegalArgumentException ("MinSize " + nMinSize + " must be <= MaxSize " + nMaxSize);
  }

  /**
   * @param <T>
   *        The element type
   * @param aElementGenerator
   *        The generator for the elements. May not be <code>null</code>.
   * @param nMinSize
   *        Minimum size, inclusive.
   * @param nMaxSize
   *        Maximum size, inclusive.
   * @return A generator for lists. Never <code>null</code>.
   */
  @NonNull
  public static <T> IGenerator <List <T>> lists (@NonNull final IGenerator <T> aElementGenerator,
                                                @Nonnegative final int nMinSize,
                                                @Nonnegative final int nMaxSize)
  {
    _checkSizes (nMinSize, nMaxSize);
    return aChoices -> {
      final List <T> ret = new ArrayList <> ();
      _drawElements (aChoices, nMinSize, nMaxSize, () -> ret.add (aElementGenerator.generate (aChoices)));
      return ret;
    };
  }

  /**
   * @param nMinSize
   *        Minimum size, inclusive.
   * @param nMaxSize
   *        Maximum size, inclusive.
   * @return A generator for byte arrays with arbitrary content. Never <code>null</code>.
   */
  @NonNull
  public static IGenerator <byte []> byteArrays (@Nonnegative final int nMinSize, @Nonnegative final int nMaxSize)
  {
    return byteArrays (bytes (), nMinSize, nMaxSize);
  }

  /**
   * @param aByteGenerator
   *        The generator for each byte, e.g. to restrict the possible values. May not be
   *        <code>null</code>.
   * @param nMinSize
   *        Minimum size, inclusive.
   * @param nMaxSize
   *        Maximum size, inclusive.
   * @return A generator for byte arrays. Never <code>null</code>.
   */
  @NonNull
  public static IGenerator <byte []> byteArrays (@NonNull final IGenerator <Byte> aByteGenerator,
                                                @Nonnegative final int nMinSize,
                                                @Nonnegative final int nMaxSize)
  {
    _checkSizes (nMinSize, nMaxSize);
    return aChoices -> {
      final List <Byte> aList = new ArrayList <> ();
      _drawElements (aChoices, nMinSize, nMaxSize, () -> aList.add (aByteGenerator.generate (aChoices)));
      final byte [] ret = new byte [aList.size ()];
      for (int i = 0; i < ret.length; ++i)
        ret[i] = aList.get (i).byteValue ();
      return ret;
    };
  }

  /**
   * @return A generator for byte arrays with 0 to {@link #DEFAULT_MAX_SIZE} bytes. Never
   *         <code>null</code>.
   */
  @NonNull
  public static IGenerator <byte []> byteArrays ()
  {
    return byteArrays (0, DEFAULT_MAX_SIZE);
  }

  /**
   * @param sAlphabet
   *        The characters to use. May neither be <code>null</code> nor empty. Shrinking goes towards
   *        the first character.
   * @param nMinLength
   *        Minimum length, inclusive.
   * @param nMaxLength
   *        Maximum length, inclusive.
   * @return A generator for strings consisting of the passed characters only. Never
   *         <code>null</code>.
   */
  @NonNull
  public static IGenerator <String> strings (@NonNull final String sAlphabet,
                                             @Nonnegative final int nMinLength,
                                             @Nonnegative final int nMaxLength)
  {
    if (sAlphabet.isEmpty ())
      throw new IllegalArgumentException ("The alphabet may not be empty");
    _checkSizes (nMinLength, nMaxLength);
    return aChoices -> {
      final StringBuilder aSB = new StringBuilder ();
      _drawElements (aChoices,
                     nMinLength,
                     nMaxLength,
                     () -> aSB.append (sAlphabet.charAt ((int) aChoices.draw (sAlphabet.length () - 1))));
      return aSB.toString ();
    };
  }

  /**
   * @param cMin
   *        The smallest character, inclusive. Shrinking goes towards it.
   * @param cMax
   *        The largest character, inclusive. Must be &ge; <code>cMin</code>.
   * @param nMinLength
   *        Minimum length, inclusive.
   * @param nMaxLength
   *        Maximum length, inclusive.
   * @return A generator for strings consisting of characters from the passed range, e.g.
   *         <code>'\u0000'</code> to <code>'\u00ff'</code> for ISO-8859-1. Never <code>null</code>.
   */
  @NonNull
  public static IGenerator <String> strings (final char cMin,
                                             final char cMax,
                                             @Nonnegative final int nMinLength,
                                             @Nonnegative final int nMaxLength)
  {
    if (cMin > cMax)
      throw new IllegalArgumentException ("Min char " + (int) cMin + " must be <= max char " + (int) cMax);
    _checkSizes (nMinLength, nMaxLength);
    return aChoices -> {
      final StringBuilder aSB = new StringBuilder ();
      _drawElements (aChoices, nMinLength, nMaxLength, () -> aSB.append ((char) (cMin + aChoices.draw (cMax - cMin))));
      return aSB.toString ();
    };
  }

  /**
   * Generate strings of arbitrary Unicode code points, excluding lone surrogates. The length is
   * measured in code points, so the <code>String.length ()</code> may be up to twice as long.
   *
   * @param nMinLength
   *        Minimum length in code points, inclusive.
   * @param nMaxLength
   *        Maximum length in code points, inclusive.
   * @return A generator for Unicode strings. Never <code>null</code>.
   */
  @NonNull
  public static IGenerator <String> unicodeStrings (@Nonnegative final int nMinLength,
                                                    @Nonnegative final int nMaxLength)
  {
    _checkSizes (nMinLength, nMaxLength);
    final IGenerator <Integer> aCodePoints = ints (0, MAX_CODE_POINT_WITHOUT_SURROGATES);
    return aChoices -> {
      final StringBuilder aSB = new StringBuilder ();
      _drawElements (aChoices, nMinLength, nMaxLength, () -> {
        final int nValue = aCodePoints.generate (aChoices).intValue ();
        // Skip the surrogate range
        aSB.appendCodePoint (nValue < Character.MIN_SURROGATE ? nValue : nValue +
                                                                          (Character.MAX_SURROGATE -
                                                                           Character.MIN_SURROGATE +
                                                                           1));
      });
      return aSB.toString ();
    };
  }

  /**
   * @return A generator for Unicode strings with 0 to 100 code points. Never <code>null</code>.
   */
  @NonNull
  public static IGenerator <String> unicodeStrings ()
  {
    return unicodeStrings (0, 100);
  }
  /**
   * Apply one random edit. The choice 0 for each decision means: replace the first byte with 0.
   */
  private static byte @NonNull [] _mutate (@NonNull final ChoiceSequence aChoices,
                                           @NonNull final IGenerator <Byte> aByteGenerator,
                                           final byte @NonNull [] aSrc)
  {
    final int nLen = aSrc.length;
    // 0: replace, 1: insert, 2: delete, 3: truncate. Empty input can only grow.
    final int nKind = nLen == 0 ? 1 : (int) aChoices.draw (3);
    switch (nKind)
    {
      case 0:
      {
        final byte [] ret = aSrc.clone ();
        ret[(int) aChoices.draw (nLen - 1)] = aByteGenerator.generate (aChoices).byteValue ();
        return ret;
      }
      case 1:
      {
        final int nPos = (int) aChoices.draw (nLen);
        final byte [] ret = new byte [nLen + 1];
        System.arraycopy (aSrc, 0, ret, 0, nPos);
        ret[nPos] = aByteGenerator.generate (aChoices).byteValue ();
        System.arraycopy (aSrc, nPos, ret, nPos + 1, nLen - nPos);
        return ret;
      }
      case 2:
      {
        final int nPos = (int) aChoices.draw (nLen - 1);
        final byte [] ret = new byte [nLen - 1];
        System.arraycopy (aSrc, 0, ret, 0, nPos);
        System.arraycopy (aSrc, nPos + 1, ret, nPos, nLen - nPos - 1);
        return ret;
      }
      default:
        // Keep at least one byte less than before
        return Arrays.copyOf (aSrc, (int) aChoices.draw (nLen - 1));
    }
  }

  /**
   * Take valid input and apply 1 to <code>nMaxMutations</code> random edits: replace a byte, insert
   * a byte, delete a byte or truncate. This is the most effective way to test decoders: purely random
   * input is usually rejected at the first byte, whereas a valid encoding with a few defects reaches
   * the deeper decoding logic. The edits shrink like everything else, towards "replace the first
   * byte with 0".
   *
   * @param aValidGenerator
   *        The generator of the valid input, e.g. the encoded form of random bytes. May not be
   *        <code>null</code>.
   * @param aByteGenerator
   *        The generator for replaced and inserted bytes. May not be <code>null</code>.
   * @param nMaxMutations
   *        The maximum number of edits. Must be &gt; 0.
   * @return A generator for mutated byte arrays. Never <code>null</code>.
   */
  @NonNull
  public static IGenerator <byte []> mutatedByteArrays (@NonNull final IGenerator <byte []> aValidGenerator,
                                                       @NonNull final IGenerator <Byte> aByteGenerator,
                                                       final int nMaxMutations)
  {
    if (nMaxMutations <= 0)
      throw new IllegalArgumentException ("MaxMutations must be > 0: " + nMaxMutations);
    return aChoices -> {
      byte [] ret = aValidGenerator.generate (aChoices);
      final int nMutations = 1 + (int) aChoices.draw (nMaxMutations - 1);
      for (int i = 0; i < nMutations; ++i)
        ret = _mutate (aChoices, aByteGenerator, ret);
      return ret;
    };
  }

  /**
   * Same as {@link #mutatedByteArrays(IGenerator, IGenerator, int)} with arbitrary replacement bytes
   * and up to 3 edits.
   *
   * @param aValidGenerator
   *        The generator of the valid input. May not be <code>null</code>.
   * @return A generator for mutated byte arrays. Never <code>null</code>.
   */
  @NonNull
  public static IGenerator <byte []> mutatedByteArrays (@NonNull final IGenerator <byte []> aValidGenerator)
  {
    return mutatedByteArrays (aValidGenerator, bytes (), 3);
  }
}
