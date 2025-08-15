package com.helger.commons.url;

import com.helger.annotation.RegEx;
import com.helger.base.string.Strings;
import com.helger.commons.regex.RegExHelper;

import jakarta.annotation.Nullable;

/**
 * Helper class for dealing with URNs
 *
 * @author Philip Helger
 */
public final class URNHelper
{
  private URNHelper ()
  {}

  @RegEx
  public static final String REGEX_URN = "^\\Qurn:\\E" +
                                         "[a-zA-Z0-9][a-zA-Z0-9-]{0,31}" +
                                         "\\Q:\\E" +
                                         "[a-zA-Z0-9()+,\\-.:=@;$_!*'%/?#]+" +
                                         "$";

  /**
   * Check if the provided string is valid according to RFC 2141. Leading and trailing spaces of the
   * value to check will result in a negative result.
   *
   * @param sURN
   *        the URN to be validated. May be <code>null</code>.
   * @return <code>true</code> if the provided URN is not empty and matches the regular expression
   *         {@link #REGEX_URN}.
   * @since 10.0.0
   */
  public static boolean isValidURN (@Nullable final String sURN)
  {
    if (Strings.isEmpty (sURN))
      return false;
    return RegExHelper.stringMatchesPattern (REGEX_URN, sURN);
  }
}
