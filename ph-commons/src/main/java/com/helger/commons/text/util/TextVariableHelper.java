package com.helger.commons.text.util;

import java.util.function.Consumer;
import java.util.function.UnaryOperator;

import javax.annotation.CheckForSigned;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.VisibleForTesting;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.string.StringHelper;

/**
 * This class provides an easy way to replace variables in a string with other
 * values. The variables need to be present in the form of <code>${bla}</code>.
 * The variable helper supports masking with the backslash (<code>\</code>)
 * character so that the "$" can be represented as "\$".
 *
 * @author Philip Helger
 * @since 10.1.9
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
  private static int _findStartOfVarName (@Nonnull final char [] aChars,
                                          @Nonnegative final int nOfs,
                                          @Nonnegative final int nLen,
                                          @Nonnull final StringBuilder aSB)
  {
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
      nAbsOfs = _nextCharConsiderMasking (aChars, nAbsOfs + 1, nLen, DOLLAR, aSB);
    }
    return nAbsOfs;
  }

  /**
   * This method is responsible for splitting a source string into the constant
   * text parts and the variable names.<br>
   * By convention, the first result element is always a constant String (maybe
   * empty) and the second element is a variable name, the third element is
   * again a constant string etc. So constant strings and variable names are
   * always intermixed. However it is not guaranteed, with what kind of element
   * the result list ends.<br>
   * Note: a variable <code>${bla}</code> ends up as <code>bla</code> in the
   * result list.
   *
   * @param sText
   *        The string to split into elements of constant text and variable
   *        names. May neither be <code>null</code> nor empty.
   * @return
   */
  @VisibleForTesting
  @Nullable
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
      final int nEndOfVar = _nextCharConsiderMasking (aTextChars,
                                                      nNextVar,
                                                      nTextLen - nNextVar,
                                                      CLOSING_BRACKET,
                                                      aTarget);
      if (nEndOfVar < 0)
      {
        if (LOGGER.isWarnEnabled ())
          LOGGER.warn ("End of variable was not found in '" + sText + "' starting from ofs " + nNextVar);

        // Add the remaining part "as-is"
        // Go back 2 chars to include "${"
        final String sRestToAdd = sText.substring (nNextVar - 2);
        if ((ret.size () % 2) == 1)
        {
          // Append to last text block
          ret.setLast (ret.getLast () + sRestToAdd);
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

  public static void replaceVariables (@Nullable final String sSourceString,
                                       @Nonnull final Consumer <String> aTextHandler,
                                       @Nonnull final Consumer <String> aReplacedVariableHandler)
  {
    ValueEnforcer.notNull (aTextHandler, "TextHandler");
    ValueEnforcer.notNull (aReplacedVariableHandler, "ReplacedVariableHandler");

    if (StringHelper.hasNoText (sSourceString))
    {
      // Surely no variables
      aTextHandler.accept (sSourceString);
    }
    else
    {
      final ICommonsList <String> aPieces = splitByVariables (sSourceString);
      if (aPieces == null || aPieces.size () <= 1)
      {
        // Syntax error or no variables
        aTextHandler.accept (sSourceString);
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
              aTextHandler.accept (sPiece);
            else
              aReplacedVariableHandler.accept (sPiece);
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

    // Allocate some spacw
    final StringBuilder aSB = new StringBuilder (sSourceString.length () * 2);
    // The text is copied "as-is"
    // Variable names are resolved through the provider
    replaceVariables (sSourceString, aSB::append, x -> aSB.append (aVariableProvider.apply (x)));
    return aSB.toString ();
  }
}
