package com.helger.base.string;

import com.helger.base.array.ArrayHelper;
import com.helger.base.enforcer.ValueEnforcer;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

public final class StringRemove
{
  private StringRemove ()
  {}

  /**
   * Remove all occurrences of the passed character from the specified input string
   *
   * @param sInputString
   *        The input string where the character should be removed. If this parameter is
   *        <code>null</code> or empty, no removing is done.
   * @param cRemoveChar
   *        The character to be removed.
   * @return The input string as is, if the input string is empty or if the remove char is not
   *         contained.
   */
  @Nullable
  public static String removeAll (@Nullable final String sInputString, final char cRemoveChar)
  {
    // Is input string empty?
    if (Strings.isEmpty (sInputString))
      return sInputString;

    // Does the char occur anywhere?
    final int nFirstIndex = sInputString.indexOf (cRemoveChar, 0);
    if (nFirstIndex == Strings.STRING_NOT_FOUND)
      return sInputString;

    // build output buffer
    final char [] aChars = sInputString.toCharArray ();
    final int nMax = aChars.length;
    final StringBuilder aSB = new StringBuilder (nMax);
    // Copy the first chars where we know it is not contained
    aSB.append (aChars, 0, nFirstIndex);
    // Start searching after the first occurrence because we know that this is a
    // char to be removed
    for (int i = nFirstIndex; i < nMax; ++i)
    {
      final char c = aChars[i];
      if (c != cRemoveChar)
        aSB.append (c);
    }
    return aSB.toString ();
  }

  /**
   * Remove all occurrences of the passed character from the specified input string
   *
   * @param sInputString
   *        The input string where the character should be removed. If this parameter is
   *        <code>null</code> or empty, no removing is done.
   * @param sRemoveString
   *        The String to be removed. May be <code>null</code> or empty in which case nothing
   *        happens.
   * @return The input string as is, if the input string is empty or if the remove string is empty
   *         or not contained.
   */
  @Nullable
  public static String removeAll (@Nullable final String sInputString, @Nullable final String sRemoveString)
  {
    // Is input string empty?
    if (Strings.isEmpty (sInputString))
      return sInputString;

    final int nRemoveLength = Strings.getLength (sRemoveString);
    if (nRemoveLength == 0)
    {
      // Nothing to be removed
      return sInputString;
    }

    if (nRemoveLength == 1)
    {
      // Shortcut to char version
      return removeAll (sInputString, sRemoveString.charAt (0));
    }

    // Does the string occur anywhere?
    int nIndex = sInputString.indexOf (sRemoveString, 0);
    if (nIndex == Strings.STRING_NOT_FOUND)
      return sInputString;

    // build output buffer
    final StringBuilder ret = new StringBuilder (sInputString.length ());
    int nOldIndex = 0;
    do
    {
      ret.append (sInputString, nOldIndex, nIndex);
      nOldIndex = nIndex + nRemoveLength;
      nIndex = sInputString.indexOf (sRemoveString, nOldIndex);
    } while (nIndex != Strings.STRING_NOT_FOUND);
    ret.append (sInputString, nOldIndex, sInputString.length ());
    return ret.toString ();
  }

  /**
   * Optimized remove method that removes a set of characters from an input string!
   *
   * @param sInputString
   *        The input string.
   * @param aRemoveChars
   *        The characters to remove. May not be <code>null</code>.
   * @return The version of the string without the passed characters or an empty String if the input
   *         string was <code>null</code>.
   */
  @Nonnull
  public static String removeMultiple (@Nullable final String sInputString, @Nonnull final char [] aRemoveChars)
  {
    ValueEnforcer.notNull (aRemoveChars, "RemoveChars");

    // Any input text?
    if (Strings.isEmpty (sInputString))
      return "";

    // Anything to remove?
    if (aRemoveChars.length == 0)
      return sInputString;

    final StringBuilder aSB = new StringBuilder (sInputString.length ());
    Strings.iterateChars (sInputString, cInput -> {
      if (!ArrayHelper.contains (aRemoveChars, cInput))
        aSB.append (cInput);
    });
    return aSB.toString ();
  }
}
