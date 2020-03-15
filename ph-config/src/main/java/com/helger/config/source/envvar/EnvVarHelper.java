package com.helger.config.source.envvar;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;

/**
 * Helper functions for environment variables
 *
 * @author Philip Helger
 */
@Immutable
public final class EnvVarHelper
{
  public static final char DEFAULT_REPLACEMENT_CHAR = '_';
  // REGEX_ENV_VAR = "[a-zA-Z_]+[a-zA-Z0-9_]*";

  private EnvVarHelper ()
  {}

  public static boolean isValidFirstChar (final char c)
  {
    return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
  }

  public static boolean isValidFollowingChar (final char c)
  {
    return isValidFirstChar (c) || (c >= '0' && c <= '9');
  }

  @Nullable
  public static String getUnifiedSysEnvName (@Nonnull @Nonempty final String sName, final char cReplacement)
  {
    ValueEnforcer.notEmpty (sName, "Name");

    final StringBuilder ret = new StringBuilder (sName.length ());
    int nIndex = 0;
    for (final char c : sName.toCharArray ())
    {
      // No need to upper case here - handled in System.getenv
      final boolean bIsValid = nIndex == 0 ? isValidFirstChar (c) : isValidFollowingChar (c);
      ret.append (bIsValid ? c : cReplacement);
      nIndex++;
    }
    return ret.toString ();
  }
}
