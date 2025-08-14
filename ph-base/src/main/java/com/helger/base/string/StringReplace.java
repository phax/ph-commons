package com.helger.base.string;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.Map.Entry;

import com.helger.annotation.Nonempty;
import com.helger.annotation.Nonnegative;
import com.helger.base.CGlobal;
import com.helger.base.array.ArrayHelper;
import com.helger.base.enforcer.ValueEnforcer;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

public final class StringReplace
{
  private StringReplace ()
  {}

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
    return replaceAll (sInputString, sSearchText, Strings.getNotNull (aReplacementText, ""));
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
    if (Strings.isEmpty (sInputString))
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
    if (nIndex == Strings.STRING_NOT_FOUND)
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
    } while (nIndex != Strings.STRING_NOT_FOUND);
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
    if (Strings.isEmpty (sInputString))
      return sInputString;

    // Replace old with the same new?
    if (cSearchChar == cReplacementChar)
      return sInputString;

    // Does the old text occur anywhere?
    int nIndex = sInputString.indexOf (cSearchChar, 0);
    if (nIndex == Strings.STRING_NOT_FOUND)
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
    } while (nIndex != Strings.STRING_NOT_FOUND);
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
    if (Strings.isEmpty (sInputString))
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
    if (Strings.isEmpty (sInputString))
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
    if (Strings.isEmpty (sInputString))
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
    if (Strings.isEmpty (sInputString))
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
    if (Strings.isNotEmpty (sInputString))
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
    if (Strings.isNotEmpty (sInputString))
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

    if (Strings.isEmpty (sInputString))
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
    if (Strings.isEmpty (sInputString) || aTransTable == null || aTransTable.isEmpty ())
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
    if (Strings.isEmpty (sInputString))
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
