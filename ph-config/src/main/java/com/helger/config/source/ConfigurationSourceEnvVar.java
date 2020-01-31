package com.helger.config.source;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;

public class ConfigurationSourceEnvVar extends AbstractConfigurationSource
{
  public static final char DEFAULT_REPLACEMENT_CHAR = '_';
  // REGEX_ENV_VAR = "[a-zA-Z_]+[a-zA-Z0-9_]*";

  public ConfigurationSourceEnvVar ()
  {
    this (EConfigSourceType.ENVIRONMENT_VARIABLE.getDefaultPriority ());
  }

  public ConfigurationSourceEnvVar (final int nPriority)
  {
    super (EConfigSourceType.ENVIRONMENT_VARIABLE, nPriority);
  }

  public static boolean isValidFirstChar (final char c)
  {
    return (c >= 'A' && c <= 'Z') || c == '_';
  }

  public static boolean isValidFollowingChar (final char c)
  {
    return (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9') || c == '_';
  }

  @Nullable
  public static String getUnifiedSysEnvName (@Nonnull @Nonempty final String sName, final char cReplacement)
  {
    ValueEnforcer.notEmpty (sName, "Name");

    final StringBuilder ret = new StringBuilder (sName.length ());
    int nIndex = 0;
    for (final char c : sName.toCharArray ())
    {
      final boolean bIsValid = nIndex == 0 ? isValidFirstChar (c) : isValidFollowingChar (c);
      ret.append (bIsValid ? c : cReplacement);
      nIndex++;
    }
    return ret.toString ();
  }

  @Nullable
  public String getConfigurationValue (@Nonnull @Nonempty final String sKey)
  {
    final String sRealName = getUnifiedSysEnvName (sKey, DEFAULT_REPLACEMENT_CHAR);
    // TODO SecurityException handler
    return System.getenv (sRealName);
  }
}
