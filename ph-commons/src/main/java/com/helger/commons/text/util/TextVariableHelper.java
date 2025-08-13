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
package com.helger.commons.text.util;

import java.util.function.Consumer;
import java.util.function.UnaryOperator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.CheckForSigned;
import com.helger.annotation.Nonempty;
import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.VisibleForTesting;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.string.StringHelper;
import com.helger.commons.valueenforcer.ValueEnforcer;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * This class provides an easy way to replace variables in a string with other
 * values. The variables need to be present in the form of <code>${bla}</code>.
 * The variable helper supports masking with the backslash (<code>\</code>)
 * character so that the "$" can be represented as "\$".
 *
 * @author Philip Helger
 * @since 10.2.0
 */
@Immutable
public final class TextVariableHelper
{
  private static final Logger LOGGER = LoggerFactory.getLogger (TextVariableHelper.class);
  private static final char MASKING_CHAR = '\\';
  private static final char DOLLAR = '$';
  private static final char OPENING_BRACKET = '{';
  private static final char CLOSING_BRACKET = '}';

  private TextVariableHelper ()
  {}

  @CheckForSigned
  private static int _nextCharConsiderMasking (@Nonnull final char [] aChars,
                                               @Nonnegative final int nOfs,
                                               @Nonnegative final int nLen,
                                               @Nonnegative final char cSearch,
                                               @Nonnull final StringBuilder aTarget)
  {
    boolean bLastCharWasMask = false;
    for (int nIndex = nOfs; nIndex < nOfs + nLen; ++nIndex)
    {
      final char c = aChars[nIndex];
      if (c == MASKING_CHAR)
      {
        // in "\\" the first "\" switches to true and the second again to
        // false
        bLastCharWasMask = !bLastCharWasMask;
        if (!bLastCharWasMask)
          aTarget.append (c);
      }
      else
      {
        // Not a masking char
        if (c == cSearch)
        {
          if (bLastCharWasMask)
          {
            // It's masked, so we can't use it
            aTarget.append (c);
          }
          else
          {
            // Begin of variable
            return nIndex;
          }
        }
        else
        {
          // Some other char
          aTarget.append (c);
        }
        bLastCharWasMask = false;
      }
    }
    // End of string and not found
    return -1;
  }

  @CheckForSigned
  private static int _nextCharConsiderMaskingBalancedBrackets (@Nonnull final char [] aChars,
                                                               @Nonnegative final int nOfs,
                                                               @Nonnegative final int nLen,
                                                               @Nonnegative final char cClosingBracket,
                                                               @Nonnegative final char cLevelChar,
                                                               @Nonnull final StringBuilder aTarget)
  {
    boolean bLastCharWasMask = false;
    int nLevel = 0;
    for (int nIndex = nOfs; nIndex < nOfs + nLen; ++nIndex)
    {
      final char c = aChars[nIndex];
      if (c == MASKING_CHAR)
      {
        // in "\\" the first "\" switches to true and the second again to
        // false
        bLastCharWasMask = !bLastCharWasMask;
        if (!bLastCharWasMask)
          aTarget.append (c);
      }
      else
      {
        // Not a masking char
        if (c == cClosingBracket)
        {
          if (bLastCharWasMask || nLevel > 0)
          {
            // It's masked, so we can't use it
            aTarget.append (c);
          }
          else
          {
            // Begin of variable
            return nIndex;
          }
          nLevel--;
        }
        else
        {
          // Make sure that nested variables work, by counting bracket depth
          if (c == cLevelChar)
            nLevel++;

          // Some other char
          aTarget.append (c);
        }
        bLastCharWasMask = false;
      }
    }
    // End of string and not found
    return -1;
  }

  @CheckForSigned
  private static int _findStartOfVarName (@Nonnull final char [] aChars,
                                          @Nonnegative final int nOfs,
                                          @Nonnegative final int nLen,
                                          @Nonnull final StringBuilder aSB)
  {
    final int nStartOfs = nOfs;
    // Find start of variable with "$"
    int nAbsOfs = _nextCharConsiderMasking (aChars, nOfs, nLen, DOLLAR, aSB);
    while (nAbsOfs >= nOfs && nAbsOfs <= nOfs + nLen - 1)
    {
      // "$" may be the last char of the string
      final boolean bIsLastChar = nAbsOfs == nOfs + nLen - 1;
      if (!bIsLastChar && aChars[nAbsOfs + 1] == OPENING_BRACKET)
      {
        // It's a variable start if the "$" is followed by a "{"
        return nAbsOfs + 2;
      }
      // Just a plain "$" in the text - continue
      aSB.append (DOLLAR);
      if (bIsLastChar)
      {
        // End of string
        return -1;
      }
      // Find next "$" starting from where we are atm
      nAbsOfs = _nextCharConsiderMasking (aChars, nAbsOfs + 1, nLen - (nAbsOfs - nStartOfs + 1), DOLLAR, aSB);
    }
    return nAbsOfs;
  }

  /**
   * This method is responsible for splitting a source string into the constant
   * text fragments and the variable names.<br>
   * By convention, the first result element is always a constant text fragment
   * (maybe empty) and the second element is a variable name, the third element
   * is again a constant text fragment etc. So constant text fragments and
   * variable names are always intermixed. However it is not guaranteed, with
   * what kind of element the result list ends.<br>
   * Example: a variable <code>${bla}</code> ends up as <code>bla</code> in the
   * result list. <br>
   * Example the text <code>Hello ${x}!</code> results in a list with 3
   * elements: <code>"Hello "</code>, <code>"x"</code> and <code>"!"</code>.
   *
   * @param sText
   *        The string to split into elements of constant text and variable
   *        names. May neither be <code>null</code> nor empty.
   * @return Never <code>null</code> list of elements, intermixed between
   *         constant strings and variable names.
   */
  @VisibleForTesting
  @Nonnull
  static ICommonsList <String> splitByVariables (@Nonnull @Nonempty final String sText)
  {
    ValueEnforcer.notEmpty (sText, "Text");

    final char [] aTextChars = sText.toCharArray ();
    final int nTextLen = aTextChars.length;

    final ICommonsList <String> ret = new CommonsArrayList <> ();
    int nStart = 0;

    // Contains the unmasked content for every element
    final StringBuilder aTarget = new StringBuilder (nTextLen);

    // Find the beginning of the next variable starting outside of a variable
    int nNextVar = _findStartOfVarName (aTextChars, nStart, nTextLen - nStart, aTarget);
    while (nNextVar >= 0)
    {
      // Add the stuff before the variable
      // Also add empty strings here, because for evaluation it must always be
      // intermixed!
      ret.add (aTarget.toString ());
      aTarget.setLength (0);

      // Search for unmasked end of variable
      final int nEndOfVar = _nextCharConsiderMaskingBalancedBrackets (aTextChars,
                                                                      nNextVar,
                                                                      nTextLen - nNextVar,
                                                                      CLOSING_BRACKET,
                                                                      OPENING_BRACKET,
                                                                      aTarget);
      if (nEndOfVar < 0)
      {
        LOGGER.warn ("End of variable was not found in '" + sText + "' starting from ofs " + nNextVar);

        // Add the remaining part "as-is"
        // Go back 2 chars to include "${"
        final String sRestToAdd = sText.substring (nNextVar - 2);
        if ((ret.size () % 2) == 1)
        {
          // Append to last text block
          ret.setLast (ret.getLastOrNull () + sRestToAdd);
        }
        else
        {
          // Append as new text block
          ret.add (sRestToAdd);
        }
        aTarget.setLength (0);
        break;
      }
      // Add variable
      ret.add (aTarget.toString ());
      aTarget.setLength (0);

      // Next round - search for next variable
      nStart = nEndOfVar + 1;
      nNextVar = _findStartOfVarName (aTextChars, nStart, nTextLen - nStart, aTarget);
    }
    // Take whatever is left
    if (aTarget.length () > 0)
      ret.add (aTarget.toString ());

    return ret;
  }

  /**
   * Quickly check if a string contains a variable.
   *
   * @param sSourceString
   *        the string to check for variables.
   * @return <code>true</code> if at least one variable is contained,
   *         <code>false</code> if not.
   */
  public static boolean containsVariables (@Nullable final String sSourceString)
  {
    return StringHelper.hasText (sSourceString) && splitByVariables (sSourceString).size () > 1;
  }

  /**
   * Parse the provided source string looking for variables in the form
   * <code>${...}</code> and invoke callbacks for either text fragments or
   * variable names.
   *
   * @param sSourceString
   *        The source string to parse and analyze. May be <code>null</code>.
   * @param aTextFragmentHandler
   *        The callback to be invoked for each text fragment. May not be
   *        <code>null</code>.
   * @param aVariableNameHandler
   *        The callback to be invoked for each variable name. May not be
   *        <code>null</code>.
   */
  public static void forEachTextAndVariable (@Nullable final String sSourceString,
                                             @Nonnull final Consumer <String> aTextFragmentHandler,
                                             @Nonnull final Consumer <String> aVariableNameHandler)
  {
    ValueEnforcer.notNull (aTextFragmentHandler, "TextFragmentHandler");
    ValueEnforcer.notNull (aVariableNameHandler, "VariableNameHandler");
    if (StringHelper.hasNoText (sSourceString))
    {
      // Surely no variables
      aTextFragmentHandler.accept (sSourceString);
    }
    else
    {
      final ICommonsList <String> aPieces = splitByVariables (sSourceString);
      if (aPieces.size () <= 1)
      {
        // Syntax error or no variables
        aTextFragmentHandler.accept (sSourceString);
      }
      else
      {
        // Text and variable intermixed
        boolean bText = true;
        for (final String sPiece : aPieces)
        {
          if (StringHelper.hasText (sPiece))
          {
            if (bText)
              aTextFragmentHandler.accept (sPiece);
            else
              aVariableNameHandler.accept (sPiece);
          }
          bText = !bText;
        }
      }
    }
  }

  @Nullable
  public static String getWithReplacedVariables (@Nullable final String sSourceString,
                                                 @Nonnull final UnaryOperator <String> aVariableProvider)
  {
    ValueEnforcer.notNull (aVariableProvider, "VariableProvider");

    if (StringHelper.hasNoText (sSourceString))
      return sSourceString;

    // Allocate some space
    final StringBuilder aSB = new StringBuilder (sSourceString.length () * 2);
    // The text is copied "as-is"
    // Variable names are resolved through the provider
    forEachTextAndVariable (sSourceString, aSB::append, sVarName -> {
      final String sResolved = aVariableProvider.apply (sVarName);
      aSB.append (sResolved);
      if (LOGGER.isDebugEnabled ())
        LOGGER.debug ("Resolved configuration variable '" + sVarName + "' to '" + sResolved + "'");
    });
    return aSB.toString ();
  }
}
