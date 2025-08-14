package com.helger.base.string;

import java.io.IOException;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;

import com.helger.annotation.Nonempty;
import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.PresentForCodeCoverage;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.CGlobal;
import com.helger.base.array.ArrayHelper;
import com.helger.base.enforcer.ValueEnforcer;
import com.helger.base.math.MathHelper;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * Simple string utility methods, originally in StringHelper.
 *
 * @author Philip Helger
 * @since v12.0.0
 */
@Immutable
public class Strings
{
  /**
   * The constant to be returned if an String.indexOf call did not find a match!
   */
  public static final int STRING_NOT_FOUND = -1;

  @PresentForCodeCoverage
  private static final Strings INSTANCE = new Strings ();

  protected Strings ()
  {}

  /**
   * Check if the string is <code>null</code> or empty.
   *
   * @param aCS
   *        The character sequence to check. May be <code>null</code>.
   * @return <code>true</code> if the string is <code>null</code> or empty, <code>false</code>
   *         otherwise
   * @since 10.1.8
   */
  public static boolean isEmpty (@Nullable final CharSequence aCS)
  {
    return aCS == null || aCS.length () == 0;
  }

  /**
   * Check if the string is <code>null</code> or empty.
   *
   * @param sStr
   *        The string to check. May be <code>null</code>.
   * @return <code>true</code> if the string is <code>null</code> or empty, <code>false</code>
   *         otherwise
   * @since 10.1.8
   */
  public static boolean isEmpty (@Nullable final String sStr)
  {
    return sStr == null || sStr.isEmpty ();
  }

  /**
   * Check if the string is <code>null</code> or empty after trimming.
   *
   * @param s
   *        The string to check. May be <code>null</code>.
   * @return <code>true</code> if the string is <code>null</code> or empty or consists only of
   *         whitespaces, <code>false</code> otherwise
   * @since 10.1.8
   */
  public static boolean isEmptyAfterTrim (@Nullable final String s)
  {
    return s == null || s.trim ().isEmpty ();
  }

  /**
   * Check if the string contains any char.
   *
   * @param aCS
   *        The character sequence to check. May be <code>null</code>.
   * @return <code>true</code> if the string contains at least one, <code>false</code> otherwise
   * @since 10.1.8
   */
  public static boolean isNotEmpty (@Nullable final CharSequence aCS)
  {
    return aCS != null && aCS.length () > 0;
  }

  /**
   * Check if the string contains any char.
   *
   * @param sStr
   *        The string to check. May be <code>null</code>.
   * @return <code>true</code> if the string contains at least one char, <code>false</code>
   *         otherwise
   * @since 10.1.8
   */
  public static boolean isNotEmpty (@Nullable final String sStr)
  {
    return sStr != null && !sStr.isEmpty ();
  }

  /**
   * Check if the string neither <code>null</code> nor empty after trimming.
   *
   * @param s
   *        The string to check. May be <code>null</code>.
   * @return <code>true</code> if the string is neither <code>null</code> nor empty nor consists
   *         only of whitespaces, <code>false</code> otherwise
   * @since 10.1.8
   */
  public static boolean isNotEmptyAfterTrim (@Nullable final String s)
  {
    return s != null && !s.trim ().isEmpty ();
  }

  /**
   * Check if the string is <code>null</code> or empty.
   *
   * @param aCS
   *        The character sequence to check. May be <code>null</code>.
   * @return <code>true</code> if the string is <code>null</code> or empty, <code>false</code>
   *         otherwise
   * @deprecated Use {@link #isEmpty(CharSequence)} instead
   */
  @Deprecated (forRemoval = true, since = "12.0.0")
  public static boolean hasNoText (@Nullable final CharSequence aCS)
  {
    return isEmpty (aCS);
  }

  /**
   * Check if the string is <code>null</code> or empty.
   *
   * @param sStr
   *        The string to check. May be <code>null</code>.
   * @return <code>true</code> if the string is <code>null</code> or empty, <code>false</code>
   *         otherwise
   * @deprecated Use {@link #isEmpty(String)} instead
   */
  @Deprecated (forRemoval = true, since = "12.0.0")
  public static boolean hasNoText (@Nullable final String sStr)
  {
    return isEmpty (sStr);
  }

  /**
   * Check if the string contains any char.
   *
   * @param aCS
   *        The character sequence to check. May be <code>null</code>.
   * @return <code>true</code> if the string contains at least one, <code>false</code> otherwise
   * @deprecated Use {@link #isNotEmpty(CharSequence)} instead
   */
  @Deprecated (forRemoval = true, since = "12.0.0")
  public static boolean hasText (@Nullable final CharSequence aCS)
  {
    return isNotEmpty (aCS);
  }

  /**
   * Check if the string contains any char.
   *
   * @param sStr
   *        The string to check. May be <code>null</code>.
   * @return <code>true</code> if the string contains at least one char, <code>false</code>
   *         otherwise
   * @deprecated Use {@link #isNotEmpty(String)} instead
   */
  @Deprecated (forRemoval = true, since = "12.0.0")
  public static boolean hasText (@Nullable final String sStr)
  {
    return isNotEmpty (sStr);
  }

  /**
   * Check if the string neither <code>null</code> nor empty after trimming.
   *
   * @param s
   *        The string to check. May be <code>null</code>.
   * @return <code>true</code> if the string is neither <code>null</code> nor empty nor consists
   *         only of whitespaces, <code>false</code> otherwise
   * @deprecated Use {@link #isNotEmptyAfterTrim(String)} instead
   */
  @Deprecated (forRemoval = true, since = "12.0.0")
  public static boolean hasTextAfterTrim (@Nullable final String s)
  {
    return isNotEmptyAfterTrim (s);
  }

  /**
   * Get the length of the passed character sequence.
   *
   * @param aCS
   *        The character sequence who's length is to be determined. May be <code>null</code>.
   * @return 0 if the parameter is <code>null</code>, its length otherwise.
   * @see CharSequence#length()
   */
  @Nonnegative
  public static int getLength (@Nullable final CharSequence aCS)
  {
    return aCS == null ? 0 : aCS.length ();
  }

  /**
   * Get the passed string but never return <code>null</code>. If the passed parameter is
   * <code>null</code> an empty string is returned.
   *
   * @param s
   *        The parameter to be not <code>null</code>.
   * @return An empty string if the passed parameter is <code>null</code>, the passed string
   *         otherwise.
   */
  @Nonnull
  public static String getNotNull (@Nullable final String s)
  {
    return getNotNull (s, "");
  }

  /**
   * Get the passed string but never return <code>null</code>. If the passed parameter is
   * <code>null</code> the second parameter is returned.
   *
   * @param s
   *        The parameter to be not <code>null</code>.
   * @param sDefaultIfNull
   *        The value to be used if the first parameter is <code>null</code>. May be
   *        <code>null</code> but in this case the call to this method is obsolete.
   * @return The passed default value if the string is <code>null</code>, otherwise the input
   *         string.
   */
  @Nullable
  public static String getNotNull (@Nullable final String s, @Nullable final String sDefaultIfNull)
  {
    return s == null ? sDefaultIfNull : s;
  }

  /**
   * Get the passed string but never return <code>null</code>. If the passed parameter is
   * <code>null</code> the second parameter is returned.
   *
   * @param s
   *        The parameter to be not <code>null</code>.
   * @param aDefaultIfNull
   *        The value supplier to be used if the first parameter is <code>null</code>. May not be
   *        <code>null</code>.
   * @return The passed default value if the string is <code>null</code>, otherwise the input
   *         string.
   * @since 10.2.0
   */
  @Nullable
  public static String getNotNull (@Nullable final String s, @Nonnull final Supplier <String> aDefaultIfNull)
  {
    return s == null ? aDefaultIfNull.get () : s;
  }

  /**
   * Get the passed {@link CharSequence} but never return <code>null</code>. If the passed parameter
   * is <code>null</code> an empty string is returned.
   *
   * @param s
   *        The parameter to be not <code>null</code>.
   * @return An empty string if the passed parameter is <code>null</code>, the passed
   *         {@link CharSequence} otherwise.
   */
  @Nonnull
  public static CharSequence getNotNull (@Nullable final CharSequence s)
  {
    return getNotNull (s, "");
  }

  /**
   * Get the passed {@link CharSequence} but never return <code>null</code>. If the passed parameter
   * is <code>null</code> the second parameter is returned.
   *
   * @param s
   *        The parameter to be not <code>null</code>.
   * @param sDefaultIfNull
   *        The value to be used if the first parameter is <code>null</code>. May be
   *        <code>null</code> but in this case the call to this method is obsolete.
   * @return The passed default value if the string is <code>null</code>, otherwise the input
   *         {@link CharSequence}.
   */
  @Nullable
  public static CharSequence getNotNull (@Nullable final CharSequence s, @Nullable final CharSequence sDefaultIfNull)
  {
    return s == null ? sDefaultIfNull : s;
  }

  /**
   * Get the passed {@link CharSequence} but never return <code>null</code>. If the passed parameter
   * is <code>null</code> the second parameter is returned.
   *
   * @param s
   *        The parameter to be not <code>null</code>.
   * @param aDefaultIfNull
   *        The value supplier to be used if the first parameter is <code>null</code>. May not be
   *        <code>null</code>.
   * @return The passed default value if the string is <code>null</code>, otherwise the input
   *         {@link CharSequence}.
   * @since 10.2.0
   */
  @Nullable
  public static CharSequence getNotNull (@Nullable final CharSequence s,
                                         @Nonnull final Supplier <? extends CharSequence> aDefaultIfNull)
  {
    return s == null ? aDefaultIfNull.get () : s;
  }

  /**
   * Get the passed string but never return an empty string. If the passed parameter is
   * <code>null</code> or empty the second parameter is returned.
   *
   * @param s
   *        The parameter to be not <code>null</code> nor empty.
   * @param sDefaultIfEmpty
   *        The value to be used if the first parameter is <code>null</code> or empty. May be
   *        <code>null</code> but in this case the call to this method is obsolete.
   * @return The passed default value if the string is <code>null</code> or empty, otherwise the
   *         input string.
   */
  @Nullable
  public static String getNotEmpty (@Nullable final String s, @Nullable final String sDefaultIfEmpty)
  {
    return hasNoText (s) ? sDefaultIfEmpty : s;
  }

  /**
   * Get the passed string but never return an empty string. If the passed parameter is
   * <code>null</code> or empty the second parameter is returned.
   *
   * @param s
   *        The parameter to be not <code>null</code> nor empty.
   * @param aDefaultIfEmpty
   *        The value supplier to be used if the first parameter is <code>null</code> or empty. May
   *        not be <code>null</code>.
   * @return The passed default value if the string is <code>null</code> or empty, otherwise the
   *         input string.
   * @since 10.2.0
   */
  @Nullable
  public static String getNotEmpty (@Nullable final String s, @Nonnull final Supplier <String> aDefaultIfEmpty)
  {
    return hasNoText (s) ? aDefaultIfEmpty.get () : s;
  }

  /**
   * Get the passed char sequence but never return an empty char sequence. If the passed parameter
   * is <code>null</code> or empty the second parameter is returned.
   *
   * @param s
   *        The parameter to be not <code>null</code> nor empty.
   * @param sDefaultIfEmpty
   *        The value to be used if the first parameter is <code>null</code> or empty. May be
   *        <code>null</code> but in this case the call to this method is obsolete.
   * @return The passed default value if the char sequence is <code>null</code> or empty, otherwise
   *         the input char sequence.
   */
  @Nullable
  public static CharSequence getNotEmpty (@Nullable final CharSequence s, @Nullable final CharSequence sDefaultIfEmpty)
  {
    return isEmpty (s) ? sDefaultIfEmpty : s;
  }

  /**
   * Get the passed char sequence but never return an empty char sequence. If the passed parameter
   * is <code>null</code> or empty the second parameter is returned.
   *
   * @param s
   *        The parameter to be not <code>null</code> nor empty.
   * @param aDefaultIfEmpty
   *        The value supplier to be used if the first parameter is <code>null</code> or empty. May
   *        not be <code>null</code>.
   * @return The passed default value if the char sequence is <code>null</code> or empty, otherwise
   *         the input char sequence.
   * @since 10.2.0
   */
  @Nullable
  public static CharSequence getNotEmpty (@Nullable final CharSequence s,
                                          @Nullable final Supplier <? extends CharSequence> aDefaultIfEmpty)
  {
    return isEmpty (s) ? aDefaultIfEmpty.get () : s;
  }

  /**
   * Get the passed string element repeated for a certain number of times. Each string element is
   * simply appended at the end of the string.
   *
   * @param cElement
   *        The character to get repeated.
   * @param nRepeats
   *        The number of repetitions to retrieve. May not be &lt; 0.
   * @return A non-<code>null</code> string containing the string element for the given number of
   *         times.
   */
  @Nonnull
  public static String getRepeated (final char cElement, @Nonnegative final int nRepeats)
  {
    ValueEnforcer.isGE0 (nRepeats, "Repeats");

    if (nRepeats == 0)
      return "";
    if (nRepeats == 1)
      return Character.toString (cElement);

    final char [] aElement = new char [nRepeats];
    Arrays.fill (aElement, cElement);
    return new String (aElement);
  }

  /**
   * Get the passed string element repeated for a certain number of times. Each string element is
   * simply appended at the end of the string.
   *
   * @param sElement
   *        The string to get repeated. May not be <code>null</code>.
   * @param nRepeats
   *        The number of repetitions to retrieve. May not be &lt; 0.
   * @return A non-<code>null</code> string containing the string element for the given number of
   *         times.
   */
  @Nonnull
  public static String getRepeated (@Nonnull final String sElement, @Nonnegative final int nRepeats)
  {
    ValueEnforcer.notNull (sElement, "Element");
    ValueEnforcer.isGE0 (nRepeats, "Repeats");

    final int nElementLength = sElement.length ();

    // Check if result length would exceed int range
    if ((long) nElementLength * nRepeats > Integer.MAX_VALUE)
      throw new IllegalArgumentException ("Resulting string exceeds the maximum integer length");

    if (nElementLength == 0 || nRepeats == 0)
      return "";
    if (nRepeats == 1)
      return sElement;

    // use character version
    if (nElementLength == 1)
      return getRepeated (sElement.charAt (0), nRepeats);

    // combine via StringBuilder
    final StringBuilder ret = new StringBuilder (nElementLength * nRepeats);
    for (int i = 0; i < nRepeats; ++i)
      ret.append (sElement);
    return ret.toString ();
  }

  /**
   * Get the result string with at least the desired length, and fill the lead or trail with the
   * provided char
   *
   * @param sSrc
   *        Source string. May be <code>null</code> or empty.
   * @param nMinLen
   *        The destination minimum length. Should be &gt; 0 to have an impact.
   * @param cGap
   *        The character to use to fill the gap
   * @param bLeading
   *        <code>true</code> to fill at the front (like "00" in "007") or at the end (like "00" in
   *        "700")
   * @return Never <code>null</code>.
   */
  @Nonnull
  private static String _getWithLeadingOrTrailing (@Nullable final String sSrc,
                                                   @Nonnegative final int nMinLen,
                                                   final char cGap,
                                                   final boolean bLeading)
  {
    if (nMinLen <= 0)
    {
      // Requested length is too short - return as is
      return getNotNull (sSrc, "");
    }

    final int nSrcLen = getLength (sSrc);
    if (nSrcLen == 0)
    {
      // Input string is empty
      return getRepeated (cGap, nMinLen);
    }

    final int nCharsToAdd = nMinLen - nSrcLen;
    if (nCharsToAdd <= 0)
    {
      // Input string is already longer than requested minimum length
      return sSrc;
    }

    final StringBuilder aSB = new StringBuilder (nMinLen);
    if (!bLeading)
      aSB.append (sSrc);
    for (int i = 0; i < nCharsToAdd; ++i)
      aSB.append (cGap);
    if (bLeading)
      aSB.append (sSrc);
    return aSB.toString ();
  }

  /**
   * Get a string that is filled at the beginning with the passed character until the minimum length
   * is reached. If the input string is empty, the result is a string with the provided len only
   * consisting of the passed characters. If the input String is longer than the provided length, it
   * is returned unchanged.
   *
   * @param sSrc
   *        Source string. May be <code>null</code>.
   * @param nMinLen
   *        Minimum length. Should be &gt; 0.
   * @param cFront
   *        The character to be used at the beginning
   * @return A non-<code>null</code> string that has at least nLen chars
   */
  @Nonnull
  public static String getWithLeading (@Nullable final String sSrc, @Nonnegative final int nMinLen, final char cFront)
  {
    return _getWithLeadingOrTrailing (sSrc, nMinLen, cFront, true);
  }

  /**
   * Get a string that is filled at the beginning with the passed character until the minimum length
   * is reached. If the input String is longer than the provided length, it is returned unchanged.
   *
   * @param nValue
   *        Source string. May be <code>null</code>.
   * @param nMinLen
   *        Minimum length. Should be &gt; 0.
   * @param cFront
   *        The character to be used at the beginning
   * @return A non-<code>null</code> string that has at least nLen chars
   * @see #getWithLeading(String, int, char)
   */
  @Nonnull
  public static String getWithLeading (final int nValue, @Nonnegative final int nMinLen, final char cFront)
  {
    return _getWithLeadingOrTrailing (Integer.toString (nValue), nMinLen, cFront, true);
  }

  /**
   * Get a string that is filled at the beginning with the passed character until the minimum length
   * is reached. If the input String is longer than the provided length, it is returned unchanged.
   *
   * @param nValue
   *        Source string. May be <code>null</code>.
   * @param nMinLen
   *        Minimum length. Should be &gt; 0.
   * @param cFront
   *        The character to be used at the beginning
   * @return A non-<code>null</code> string that has at least nLen chars
   * @see #getWithLeading(String, int, char)
   */
  @Nonnull
  public static String getWithLeading (final long nValue, @Nonnegative final int nMinLen, final char cFront)
  {
    return _getWithLeadingOrTrailing (Long.toString (nValue), nMinLen, cFront, true);
  }

  /**
   * Get a string that is filled at the end with the passed character until the minimum length is
   * reached. If the input string is empty, the result is a string with the provided len only
   * consisting of the passed characters. If the input String is longer than the provided length, it
   * is returned unchanged.
   *
   * @param sSrc
   *        Source string. May be <code>null</code>.
   * @param nMinLen
   *        Minimum length. Should be &gt; 0.
   * @param cEnd
   *        The character to be used at the end
   * @return A non-<code>null</code> string that has at least nLen chars
   */
  @Nonnull
  public static String getWithTrailing (@Nullable final String sSrc, @Nonnegative final int nMinLen, final char cEnd)
  {
    return _getWithLeadingOrTrailing (sSrc, nMinLen, cEnd, false);
  }

  @Nullable
  public static String getLeadingZero (@Nullable final Byte aValue, final int nChars)
  {
    return aValue == null ? null : getLeadingZero (aValue.byteValue (), nChars);
  }

  @Nullable
  public static String getLeadingZero (@Nullable final Integer aValue, final int nChars)
  {
    return aValue == null ? null : getLeadingZero (aValue.longValue (), nChars);
  }

  @Nullable
  public static String getLeadingZero (@Nullable final Long aValue, final int nChars)
  {
    return aValue == null ? null : getLeadingZero (aValue.longValue (), nChars);
  }

  @Nullable
  public static String getLeadingZero (@Nullable final Short aValue, final int nChars)
  {
    return aValue == null ? null : getLeadingZero (aValue.shortValue (), nChars);
  }

  @Nonnull
  public static String getLeadingZero (final int nValue, final int nChars)
  {
    final boolean bNeg = nValue < 0;
    final String sValue = Integer.toString (MathHelper.abs (nValue));
    if (sValue.length () >= nChars)
      return bNeg ? '-' + sValue : sValue;

    // prepend '0's
    final StringBuilder aSB = new StringBuilder ((bNeg ? 1 : 0) + nChars);
    if (bNeg)
      aSB.append ('-');
    for (int i = 0; i < nChars - sValue.length (); ++i)
      aSB.append ('0');
    return aSB.append (sValue).toString ();
  }

  @Nonnull
  public static String getLeadingZero (final long nValue, final int nChars)
  {
    final boolean bNeg = nValue < 0;
    final String sValue = Long.toString (MathHelper.abs (nValue));
    if (sValue.length () >= nChars)
      return bNeg ? '-' + sValue : sValue;

    // prepend '0's
    final StringBuilder aSB = new StringBuilder ((bNeg ? 1 : 0) + nChars);
    if (bNeg)
      aSB.append ('-');
    for (int i = 0; i < nChars - sValue.length (); ++i)
      aSB.append ('0');
    return aSB.append (sValue).toString ();
  }

  @Nonnull
  public static String getLeadingZero (@Nonnull final String sValue, final int nChars)
  {
    return getWithLeading (sValue, nChars, '0');
  }

  /**
   * Encode a char array to a byte array using the provided charset. This does the same as
   * <code>new String (aCharArray).getBytes (aCharset)</code> just without the intermediate objects.
   *
   * @param aCharset
   *        Charset to be used. May not be <code>null</code>.
   * @param aCharArray
   *        The char array to be encoded. May not be <code>null</code>.
   * @return The created byte array. Never <code>null</code>.
   * @since 8.6.4
   */
  @Nonnull
  @ReturnsMutableCopy
  public static byte [] encodeCharToBytes (@Nonnull final char [] aCharArray, @Nonnull final Charset aCharset)
  {
    return encodeCharToBytes (aCharArray, 0, aCharArray.length, aCharset);
  }

  /**
   * Encode a char array to a byte array using the provided charset. This does the same as
   * <code>new String (aCharArray).getBytes (aCharset)</code> just without the intermediate objects.
   *
   * @param aCharset
   *        Charset to be used. May not be <code>null</code>.
   * @param aCharArray
   *        The char array to be encoded. May not be <code>null</code>.
   * @param nOfs
   *        Offset into char array. Must be &ge; 0.
   * @param nLen
   *        Chars to encode. Must be &ge; 0.
   * @return The created byte array. Never <code>null</code>.
   * @since 8.6.4
   */
  @Nonnull
  @ReturnsMutableCopy
  public static byte [] encodeCharToBytes (@Nonnull final char [] aCharArray,
                                           @Nonnegative final int nOfs,
                                           @Nonnegative final int nLen,
                                           @Nonnull final Charset aCharset)
  {
    ValueEnforcer.isArrayOfsLen (aCharArray, nOfs, nLen);

    final CharsetEncoder aEncoder = aCharset.newEncoder ();
    // We need to perform double, not float, arithmetic; otherwise
    // we lose low order bits when nLen is larger than 2^24.
    final int nEncodedLen = (int) (nLen * (double) aEncoder.maxBytesPerChar ());
    final byte [] aByteArray = new byte [nEncodedLen];
    if (nLen == 0)
      return aByteArray;
    aEncoder.onMalformedInput (CodingErrorAction.REPLACE).onUnmappableCharacter (CodingErrorAction.REPLACE).reset ();

    final CharBuffer aSrcBuf = CharBuffer.wrap (aCharArray, nOfs, nLen);
    final ByteBuffer aDstBuf = ByteBuffer.wrap (aByteArray);
    try
    {
      CoderResult aRes = aEncoder.encode (aSrcBuf, aDstBuf, true);
      if (!aRes.isUnderflow ())
        aRes.throwException ();
      aRes = aEncoder.flush (aDstBuf);
      if (!aRes.isUnderflow ())
        aRes.throwException ();
    }
    catch (final CharacterCodingException x)
    {
      throw new IllegalStateException (x);
    }

    final int nDstLen = aDstBuf.position ();
    if (nDstLen == aByteArray.length)
      return aByteArray;
    return Arrays.copyOf (aByteArray, nDstLen);
  }

  /**
   * Decode a byte array to a char array using the provided charset. This does the same as
   * <code>new String (aByteArray, aCharset)</code> just without the intermediate objects.
   *
   * @param aByteArray
   *        The byte array to be decoded. May not be <code>null</code>.
   * @param aCharset
   *        Charset to be used. May not be <code>null</code>.
   * @return The created char array. Never <code>null</code>.
   * @since 8.6.4
   */
  @Nonnull
  public static char [] decodeBytesToChars (@Nonnull final byte [] aByteArray, @Nonnull final Charset aCharset)
  {
    return decodeBytesToChars (aByteArray, 0, aByteArray.length, aCharset);
  }

  /**
   * Decode a byte array to a char array using the provided charset. This does the same as
   * <code>new String (aByteArray, aCharset)</code> just without the intermediate objects.
   *
   * @param aByteArray
   *        The byte array to be decoded. May not be <code>null</code>.
   * @param nOfs
   *        Offset into byte array. Must be &ge; 0.
   * @param nLen
   *        Bytes to encode. Must be &ge; 0.
   * @param aCharset
   *        Charset to be used. May not be <code>null</code>.
   * @return The created char array. Never <code>null</code>.
   * @since 8.6.4
   */
  @Nonnull
  public static char [] decodeBytesToChars (@Nonnull final byte [] aByteArray,
                                            @Nonnegative final int nOfs,
                                            @Nonnegative final int nLen,
                                            @Nonnull final Charset aCharset)
  {
    final CharsetDecoder aDecoder = aCharset.newDecoder ();
    final int nDecodedLen = (int) (nLen * (double) aDecoder.maxCharsPerByte ());
    final char [] aCharArray = new char [nDecodedLen];
    if (nLen == 0)
      return aCharArray;
    aDecoder.onMalformedInput (CodingErrorAction.REPLACE).onUnmappableCharacter (CodingErrorAction.REPLACE).reset ();

    final ByteBuffer aSrcBuf = ByteBuffer.wrap (aByteArray, nOfs, nLen);
    final CharBuffer aDstBuf = CharBuffer.wrap (aCharArray);
    try
    {
      CoderResult aRes = aDecoder.decode (aSrcBuf, aDstBuf, true);
      if (!aRes.isUnderflow ())
        aRes.throwException ();
      aRes = aDecoder.flush (aDstBuf);
      if (!aRes.isUnderflow ())
        aRes.throwException ();
    }
    catch (final CharacterCodingException x)
    {
      // Substitution is always enabled,
      // so this shouldn't happen
      throw new IllegalStateException (x);
    }

    final int nDstLen = aDstBuf.position ();
    if (nDstLen == aCharArray.length)
      return aCharArray;
    return Arrays.copyOf (aCharArray, nDstLen);
  }

  /**
   * Same as {@link #replaceAll(String, String, CharSequence)} but allowing for a <code>null</code>
   * new-value, which is than interpreted as an empty string instead.
   *
   * @param sInputString
   *        The input string where the text should be replace. If this parameter is
   *        <code>null</code> or empty, no replacement is done.
   * @param sSearchText
   *        The string to be replaced. May neither be <code>null</code> nor empty.
   * @param aReplacementText
   *        The string with the replacement. May be <code>null</code> or empty.
   * @return The input string as is, if the input string is empty or if the string to be replaced is
   *         not contained.
   */
  public static String replaceAllSafe (@Nullable final String sInputString,
                                       @Nonnull final String sSearchText,
                                       @Nullable final CharSequence aReplacementText)
  {
    return replaceAll (sInputString, sSearchText, getNotNull (aReplacementText, ""));
  }

  /**
   * This is a fast replacement for {@link String#replace(CharSequence, CharSequence)}. The problem
   * with the mentioned {@link String} method is, that is uses internally regular expressions which
   * use a synchronized block to compile the patterns. This method is inherently thread safe since
   * {@link String} is immutable and we're operating on different temporary {@link StringBuilder}
   * objects.
   *
   * @param sInputString
   *        The input string where the text should be replace. If this parameter is
   *        <code>null</code> or empty, no replacement is done.
   * @param sSearchText
   *        The string to be replaced. May neither be <code>null</code> nor empty.
   * @param aReplacementText
   *        The string with the replacement. May not be <code>null</code> but may be empty.
   * @return The input string as is, if the input string is empty or if the search pattern and the
   *         replacement are equal or if the string to be replaced is not contained.
   */
  @Nullable
  public static String replaceAll (@Nullable final String sInputString,
                                   @Nonnull final String sSearchText,
                                   @Nonnull final CharSequence aReplacementText)
  {
    ValueEnforcer.notEmpty (sSearchText, "SearchText");
    ValueEnforcer.notNull (aReplacementText, "ReplacementText");

    // Is input string empty?
    if (hasNoText (sInputString))
      return sInputString;

    // Replace old with the same new?
    final int nOldLength = sSearchText.length ();
    final int nNewLength = aReplacementText.length ();
    if (nOldLength == nNewLength)
    {
      // Any change?
      if (sSearchText.equals (aReplacementText))
        return sInputString;

      if (nOldLength == 1)
      {
        // Use char version which is more efficient
        return replaceAll (sInputString, sSearchText.charAt (0), aReplacementText.charAt (0));
      }
    }

    // Does the old text occur anywhere?
    int nIndex = sInputString.indexOf (sSearchText, 0);
    if (nIndex == STRING_NOT_FOUND)
      return sInputString;

    // build output buffer
    final StringBuilder ret = new StringBuilder (nOldLength >= nNewLength ? sInputString.length () : sInputString
                                                                                                                 .length () *
                                                                                                     2);
    int nOldIndex = 0;
    do
    {
      ret.append (sInputString, nOldIndex, nIndex).append (aReplacementText);
      nIndex += nOldLength;
      nOldIndex = nIndex;
      nIndex = sInputString.indexOf (sSearchText, nIndex);
    } while (nIndex != STRING_NOT_FOUND);
    ret.append (sInputString, nOldIndex, sInputString.length ());
    return ret.toString ();
  }

  /**
   * This is a fast replacement for {@link String#replace(char, char)} for characters. The problem
   * with the mentioned String method is, that is uses internally regular expressions which use a
   * synchronized block to compile the patterns. This method is inherently thread safe since
   * {@link String} is immutable and we're operating on different temporary {@link StringBuilder}
   * objects.
   *
   * @param sInputString
   *        The input string where the text should be replace. If this parameter is
   *        <code>null</code> or empty, no replacement is done.
   * @param cSearchChar
   *        The character to be replaced.
   * @param cReplacementChar
   *        The character with the replacement.
   * @return The input string as is, if the input string is empty or if the search pattern and the
   *         replacement are equal or if the string to be replaced is not contained.
   */
  @Nullable
  public static String replaceAll (@Nullable final String sInputString,
                                   final char cSearchChar,
                                   final char cReplacementChar)
  {
    // Is input string empty?
    if (hasNoText (sInputString))
      return sInputString;

    // Replace old with the same new?
    if (cSearchChar == cReplacementChar)
      return sInputString;

    // Does the old text occur anywhere?
    int nIndex = sInputString.indexOf (cSearchChar, 0);
    if (nIndex == STRING_NOT_FOUND)
      return sInputString;

    // build output buffer
    final StringBuilder ret = new StringBuilder (sInputString.length ());
    int nOldIndex = 0;
    do
    {
      ret.append (sInputString, nOldIndex, nIndex).append (cReplacementChar);
      nIndex++;
      nOldIndex = nIndex;
      nIndex = sInputString.indexOf (cSearchChar, nIndex);
    } while (nIndex != STRING_NOT_FOUND);
    ret.append (sInputString, nOldIndex, sInputString.length ());
    return ret.toString ();
  }

  /**
   * Just calls <code>replaceAll</code> as long as there are still replacements found
   *
   * @param sInputString
   *        The input string where the text should be replace. If this parameter is
   *        <code>null</code> or empty, no replacement is done.
   * @param sSearchText
   *        The string to be replaced. May neither be <code>null</code> nor empty.
   * @param sReplacementText
   *        The string with the replacement. May not be <code>null</code> but may be empty.
   * @return The input string as is, if the input string is empty or if the string to be replaced is
   *         not contained.
   */
  @Nullable
  public static String replaceAllRepeatedly (@Nullable final String sInputString,
                                             @Nonnull final String sSearchText,
                                             @Nonnull final String sReplacementText)
  {
    ValueEnforcer.notEmpty (sSearchText, "SearchText");
    ValueEnforcer.notNull (sReplacementText, "ReplacementText");
    ValueEnforcer.isFalse (sReplacementText.contains (sSearchText),
                           "Loop detection: replacementText must not contain searchText");

    // Is input string empty?
    if (hasNoText (sInputString))
      return sInputString;

    String sRet = sInputString;
    String sLastLiteral;
    do
    {
      sLastLiteral = sRet;
      sRet = replaceAll (sRet, sSearchText, sReplacementText);
    } while (!sLastLiteral.equals (sRet));
    return sRet;
  }

  /**
   * Get the result length (in characters) when replacing all patterns with the replacements on the
   * passed input array.
   *
   * @param aInputString
   *        Input char array. May not be <code>null</code>.
   * @param aSearchChars
   *        The one-character search patterns. May not be <code>null</code>.
   * @param aReplacementStrings
   *        The replacements to be performed. May not be <code>null</code>. The first dimension of
   *        this array must have exactly the same amount of elements as the patterns parameter
   *        array.
   * @return {@link CGlobal#ILLEGAL_UINT} if no replacement was needed, and therefore the length of
   *         the input array could be used.
   */
  public static int getReplaceMultipleResultLength (@Nonnull final char [] aInputString,
                                                    @Nonnull @Nonempty final char [] aSearchChars,
                                                    @Nonnull @Nonempty final char [] [] aReplacementStrings)
  {
    int nResultLen = 0;
    boolean bAnyReplacement = false;
    for (final char cInput : aInputString)
    {
      // In case no replacement is found use a single char
      int nReplacementLength = 1;
      for (int nIndex = 0; nIndex < aSearchChars.length; nIndex++)
        if (cInput == aSearchChars[nIndex])
        {
          nReplacementLength = aReplacementStrings[nIndex].length;
          bAnyReplacement = true;
          break;
        }
      nResultLen += nReplacementLength;
    }
    return bAnyReplacement ? nResultLen : CGlobal.ILLEGAL_UINT;
  }

  /**
   * Optimized replace method that replaces a set of characters with a set of strings. This method
   * was created for efficient XML special character replacements!
   *
   * @param sInputString
   *        The input string.
   * @param aSearchChars
   *        The characters to replace.
   * @param aReplacementStrings
   *        The new strings to be inserted instead. Must have the same array length as aPatterns.
   * @return The replaced version of the string or an empty char array if the input string was
   *         <code>null</code>.
   */
  @Nonnull
  public static char [] replaceMultiple (@Nullable final String sInputString,
                                         @Nonnull final char [] aSearchChars,
                                         @Nonnull final char [] [] aReplacementStrings)
  {
    // Any input text?
    if (hasNoText (sInputString))
      return CGlobal.EMPTY_CHAR_ARRAY;

    return replaceMultiple (sInputString.toCharArray (), aSearchChars, aReplacementStrings);
  }

  /**
   * Optimized replace method that replaces a set of characters with a set of strings. This method
   * was created for efficient XML special character replacements!
   *
   * @param aInput
   *        The input string.
   * @param aSearchChars
   *        The characters to replace.
   * @param aReplacementStrings
   *        The new strings to be inserted instead. Must have the same array length as aPatterns.
   * @return The replaced version of the string or an empty char array if the input string was
   *         <code>null</code>.
   */
  @Nonnull
  public static char [] replaceMultiple (@Nullable final char [] aInput,
                                         @Nonnull final char [] aSearchChars,
                                         @Nonnull final char [] [] aReplacementStrings)
  {
    ValueEnforcer.notNull (aSearchChars, "SearchChars");
    ValueEnforcer.notNull (aReplacementStrings, "ReplacementStrings");
    ValueEnforcer.isEqual (aSearchChars.length, aReplacementStrings.length, "array length mismatch");

    // Any input text?
    if (aInput == null || aInput.length == 0)
      return CGlobal.EMPTY_CHAR_ARRAY;

    // Any replacement patterns?
    if (aSearchChars.length == 0)
      return aInput;

    // get result length
    final int nResultLen = getReplaceMultipleResultLength (aInput, aSearchChars, aReplacementStrings);

    // nothing to replace in here?
    if (nResultLen == CGlobal.ILLEGAL_UINT)
      return aInput;

    // build result
    final char [] aOutput = new char [nResultLen];
    int nOutputIndex = 0;

    // For all input chars
    for (final char cInput : aInput)
    {
      boolean bFoundReplacement = false;
      for (int nPatternIndex = 0; nPatternIndex < aSearchChars.length; nPatternIndex++)
      {
        if (cInput == aSearchChars[nPatternIndex])
        {
          final char [] aReplacement = aReplacementStrings[nPatternIndex];
          final int nReplacementLength = aReplacement.length;
          System.arraycopy (aReplacement, 0, aOutput, nOutputIndex, nReplacementLength);
          nOutputIndex += nReplacementLength;
          bFoundReplacement = true;
          break;
        }
      }
      if (!bFoundReplacement)
      {
        // copy char as is
        aOutput[nOutputIndex++] = cInput;
      }
    }

    return aOutput;
  }

  /**
   * Specialized version of {@link #replaceMultiple(String, char[], char[][])} where the object
   * where the output should be appended is passed in as a parameter. This has the advantage, that
   * not length calculation needs to take place!
   *
   * @param sInputString
   *        The input string.
   * @param aSearchChars
   *        The characters to replace.
   * @param aReplacementStrings
   *        The new strings to be inserted instead. Must have the same array length as aPatterns.
   * @param aTarget
   *        Where the replaced objects should be written to. May not be <code>null</code>.
   * @return The number of replacements performed. Always &ge; 0.
   * @throws IOException
   *         In case writing to the Writer fails
   */
  @Nonnegative
  public static int replaceMultipleTo (@Nullable final String sInputString,
                                       @Nonnull final char [] aSearchChars,
                                       @Nonnull final char [] [] aReplacementStrings,
                                       @Nonnull final Writer aTarget) throws IOException
  {
    if (hasNoText (sInputString))
      return 0;

    return replaceMultipleTo (sInputString.toCharArray (), aSearchChars, aReplacementStrings, aTarget);
  }

  /**
   * Specialized version of {@link #replaceMultiple(String, char[], char[][])} where the object
   * where the output should be appended is passed in as a parameter. This has the advantage, that
   * not length calculation needs to take place!
   *
   * @param aInput
   *        The input char array. May not be <code>null</code>.
   * @param aSearchChars
   *        The characters to replace.
   * @param aReplacementStrings
   *        The new strings to be inserted instead. Must have the same array length as aPatterns.
   * @param aTarget
   *        Where the replaced objects should be written to. May not be <code>null</code>.
   * @return The number of replacements performed. Always &ge; 0.
   * @throws IOException
   *         In case writing to the Writer fails
   */
  @Nonnegative
  public static int replaceMultipleTo (@Nullable final char [] aInput,
                                       @Nonnull final char [] aSearchChars,
                                       @Nonnull final char [] [] aReplacementStrings,
                                       @Nonnull final Writer aTarget) throws IOException
  {
    return aInput == null ? 0 : replaceMultipleTo (aInput,
                                                   0,
                                                   aInput.length,
                                                   aSearchChars,
                                                   aReplacementStrings,
                                                   aTarget);
  }

  /**
   * Specialized version of {@link #replaceMultiple(String, char[], char[][])} where the object
   * where the output should be appended is passed in as a parameter. This has the advantage, that
   * not length calculation needs to take place!
   *
   * @param aInput
   *        The input char array. May be <code>null</code>.
   * @param nOfs
   *        Offset into input array. Must be &ge; 0.
   * @param nLen
   *        Number of characters from input array. Must be &ge; 0.
   * @param aSearchChars
   *        The characters to replace.
   * @param aReplacementStrings
   *        The new strings to be inserted instead. Must have the same array length as aPatterns.
   * @param aTarget
   *        Where the replaced objects should be written to. May not be <code>null</code>.
   * @return The number of replacements performed. Always &ge; 0.
   * @throws IOException
   *         In case writing to the Writer fails
   */
  @Nonnegative
  public static int replaceMultipleTo (@Nullable final char [] aInput,
                                       @Nonnegative final int nOfs,
                                       @Nonnegative final int nLen,
                                       @Nonnull final char [] aSearchChars,
                                       @Nonnull final char [] [] aReplacementStrings,
                                       @Nonnull final Writer aTarget) throws IOException
  {
    if (aInput != null)
      ValueEnforcer.isArrayOfsLen (aInput, nOfs, nLen);
    ValueEnforcer.notNull (aSearchChars, "SearchChars");
    ValueEnforcer.notNull (aReplacementStrings, "ReplacementStrings");
    ValueEnforcer.isEqual (aSearchChars.length, aReplacementStrings.length, "array length mismatch");
    ValueEnforcer.notNull (aTarget, "Target");

    if (aInput == null || aInput.length == 0 || nLen == 0)
      return 0;

    if (aSearchChars.length == 0)
    {
      // No modifications required
      aTarget.write (aInput, nOfs, nLen);
      return 0;
    }

    // for all input string characters
    int nFirstNonReplace = nOfs;
    int nInputIndex = nOfs;
    int nTotalReplacements = 0;
    final int nMaxSearchChars = aSearchChars.length;
    for (int i = 0; i < nLen; ++i)
    {
      final char cInput = aInput[nOfs + i];
      for (int nPatternIndex = 0; nPatternIndex < nMaxSearchChars; nPatternIndex++)
      {
        if (cInput == aSearchChars[nPatternIndex])
        {
          if (nFirstNonReplace < nInputIndex)
            aTarget.write (aInput, nFirstNonReplace, nInputIndex - nFirstNonReplace);
          nFirstNonReplace = nInputIndex + 1;
          aTarget.write (aReplacementStrings[nPatternIndex]);
          ++nTotalReplacements;
          break;
        }
      }
      nInputIndex++;
    }
    if (nFirstNonReplace < nInputIndex)
      aTarget.write (aInput, nFirstNonReplace, nInputIndex - nFirstNonReplace);
    return nTotalReplacements;
  }

  /**
   * Optimized replace method that replaces a set of characters with another character. This method
   * was created for efficient unsafe character replacements!
   *
   * @param sInputString
   *        The input string.
   * @param aSearchChars
   *        The characters to replace.
   * @param cReplacementChar
   *        The new char to be used instead of the search chars.
   * @return The replaced version of the string or an empty char array if the input string was
   *         <code>null</code>.
   */
  @Nonnull
  public static char [] replaceMultiple (@Nullable final String sInputString,
                                         @Nonnull final char [] aSearchChars,
                                         final char cReplacementChar)
  {
    ValueEnforcer.notNull (aSearchChars, "SearchChars");

    // Any input text?
    if (hasNoText (sInputString))
      return CGlobal.EMPTY_CHAR_ARRAY;

    // Get char array
    final char [] aInput = sInputString.toCharArray ();

    // Any replacement patterns?
    if (aSearchChars.length == 0)
      return aInput;

    // build result
    final char [] aOutput = new char [aInput.length];
    int nOutputIndex = 0;
    for (final char c : aInput)
    {
      if (ArrayHelper.contains (aSearchChars, c))
        aOutput[nOutputIndex] = cReplacementChar;
      else
        aOutput[nOutputIndex] = c;
      nOutputIndex++;
    }
    return aOutput;
  }

  /**
   * Optimized replace method that replaces a set of characters with another character. This method
   * was created for efficient unsafe character replacements!
   *
   * @param sInputString
   *        The input string.
   * @param aSearchChars
   *        The characters to replace.
   * @param cReplacementChar
   *        The new char to be used instead of the search chars.
   * @param aTarget
   *        The target StringBuilder to write the result to. May not be <code>null</code>.
   */
  public static void replaceMultipleTo (@Nullable final String sInputString,
                                        @Nonnull final char [] aSearchChars,
                                        final char cReplacementChar,
                                        @Nonnull final StringBuilder aTarget)
  {
    ValueEnforcer.notNull (aSearchChars, "SearchChars");
    ValueEnforcer.notNull (aTarget, "Target");

    // Any input text?
    if (isNotEmpty (sInputString))
    {
      // Any search chars?
      if (aSearchChars.length == 0)
      {
        aTarget.append (sInputString);
      }
      else
      {
        // Perform the replacement
        for (final char c : sInputString.toCharArray ())
        {
          if (ArrayHelper.contains (aSearchChars, c))
            aTarget.append (cReplacementChar);
          else
            aTarget.append (c);
        }
      }
    }
  }

  /**
   * Optimized replace method that replaces a set of characters with another character. This method
   * was created for efficient unsafe character replacements!
   *
   * @param sInputString
   *        The input string.
   * @param aSearchChars
   *        The characters to replace.
   * @param cReplacementChar
   *        The new char to be used instead of the search chars.
   * @param aTarget
   *        The target writer to write the result to. May not be <code>null</code>.
   * @throws IOException
   *         in case writing to the Writer fails
   * @since 8.6.3
   */
  public static void replaceMultipleTo (@Nullable final String sInputString,
                                        @Nonnull final char [] aSearchChars,
                                        final char cReplacementChar,
                                        @Nonnull final Writer aTarget) throws IOException
  {
    ValueEnforcer.notNull (aSearchChars, "SearchChars");
    ValueEnforcer.notNull (aTarget, "Target");

    // Any input text?
    if (isNotEmpty (sInputString))
    {
      // Any search chars?
      if (aSearchChars.length == 0)
      {
        aTarget.write (sInputString);
      }
      else
      {
        // Perform the replacement
        for (final char c : sInputString.toCharArray ())
        {
          if (ArrayHelper.contains (aSearchChars, c))
            aTarget.write (cReplacementChar);
          else
            aTarget.write (c);
        }
      }
    }
  }

  /**
   * Optimized replace method that replaces a set of characters with another character. This method
   * was created for efficient unsafe character replacements!
   *
   * @param sInputString
   *        The input string.
   * @param aSearchChars
   *        The characters to replace.
   * @param cReplacementChar
   *        The new char to be used instead of the search chars.
   * @return The replaced version of the string or an empty char array if the input string was
   *         <code>null</code>.
   * @since 8.6.3
   */
  @Nonnull
  public static String replaceMultipleAsString (@Nullable final String sInputString,
                                                @Nonnull final char [] aSearchChars,
                                                final char cReplacementChar)
  {
    ValueEnforcer.notNull (aSearchChars, "SearchChars");

    if (hasNoText (sInputString))
      return "";

    final StringBuilder aSB = new StringBuilder (sInputString.length ());
    replaceMultipleTo (sInputString, aSearchChars, cReplacementChar, aSB);
    return aSB.toString ();
  }

  /**
   * Perform all string replacements on the input string as defined by the passed map. All
   * replacements are done using {@link #replaceAll(String,String,CharSequence)} which is ok.
   *
   * @param sInputString
   *        The input string where the text should be replaced. May be <code>null</code>.
   * @param aTransTable
   *        The map with the replacements to execute. If <code>null</code> is passed, the input
   *        string is not altered.
   * @return <code>null</code> if the input string was <code>null</code>.
   */
  @Nullable
  public static String replaceMultiple (@Nullable final String sInputString,
                                        @Nullable final Map <String, String> aTransTable)
  {
    if (hasNoText (sInputString) || aTransTable == null || aTransTable.isEmpty ())
      return sInputString;

    String sOutput = sInputString;
    for (final Entry <String, String> aEntry : aTransTable.entrySet ())
      sOutput = replaceAll (sOutput, aEntry.getKey (), aEntry.getValue ());
    return sOutput;
  }

  /**
   * Perform all string replacements on the input string as defined by the passed map. All
   * replacements are done using {@link #replaceAll(String,String,CharSequence)} which is ok.
   *
   * @param sInputString
   *        The input string where the text should be replaced. May be <code>null</code>.
   * @param aSearchTexts
   *        The texts to be searched. If <code>null</code> is passed, the input string is not
   *        altered.
   * @param aReplacementTexts
   *        The texts to be used as the replacements. This array must have exactly the same number
   *        of elements than the searched texts! If <code>null</code> is passed, the input string is
   *        not altered.
   * @return <code>null</code> if the input string was <code>null</code>. The unmodified input
   *         string if no search/replace patterns where provided.
   */
  @Nullable
  public static String replaceMultiple (@Nullable final String sInputString,
                                        @Nullable final String [] aSearchTexts,
                                        @Nullable final String [] aReplacementTexts)
  {
    if (hasNoText (sInputString))
      return sInputString;

    final int nSearchTextLength = aSearchTexts == null ? 0 : aSearchTexts.length;
    final int nReplacementTextLength = aReplacementTexts == null ? 0 : aReplacementTexts.length;
    if (nSearchTextLength != nReplacementTextLength)
      throw new IllegalArgumentException ("Array length mismatch!");

    // Nothing to replace?
    if (nSearchTextLength == 0)
      return sInputString;

    String sOutput = sInputString;
    for (int nIndex = 0; nIndex < nSearchTextLength; ++nIndex)
      sOutput = replaceAll (sOutput, aSearchTexts[nIndex], aReplacementTexts[nIndex]);
    return sOutput;
  }
}
