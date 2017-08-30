package com.helger.cli2;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.CommonsHashMap;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.collection.impl.ICommonsMap;
import com.helger.commons.string.StringHelper;

public class CmdLineParser
{
  private final ICommonsList <Option> m_aOptions;

  public CmdLineParser (@Nullable final Iterable <? extends Option> aOptions)
  {
    m_aOptions = new CommonsArrayList <> (aOptions);
  }

  private static Option _findOption (@Nonnull final ICommonsMap <String, Option> aStrToOptionMap,
                                     @Nullable final String sToken,
                                     @Nonnull final ParsedCmdLine ret)
  {
    if (StringHelper.hasNoText (sToken))
      return null;

    // Skip prefix
    String sText = sToken;
    if (sText.startsWith ("--"))
      sText = sText.substring (2);
    else
      if (sText.startsWith ("-"))
        sText = sText.substring (1);

    if (sText.length () == 0)
    {
      // Just "--" or "-" without a name
      return null;
    }

    Option aOption = aStrToOptionMap.get (sText);
    if (aOption == null)
    {
      // No direct match - try something like -Dversion=1.0 or -Xmx512m
      while (aOption == null)
      {
        // Remove last char and try
        sText = sText.substring (0, sText.length () - 1);
        if (sText.length () == 0)
          break;

        aOption = aStrToOptionMap.get (sText);
      }
    }
    return aOption;
  }

  @Nonnull
  public static ParsedCmdLine parseStatic (@Nonnull final ICommonsList <Option> aOptions,
                                           @Nullable final String [] aArgs)
  {
    ValueEnforcer.notNull (aOptions, "Options");

    // Create map from option name to option definition
    final ICommonsMap <String, Option> aStrToOptionMap = new CommonsHashMap <> ();
    for (final Option aOption : aOptions)
    {
      if (aOption.hasOpt ())
        aStrToOptionMap.put (aOption.getOpt (), aOption);
      if (aOption.hasLongOpt ())
        aStrToOptionMap.put (aOption.getLongOpt (), aOption);
    }

    final ParsedCmdLine ret = new ParsedCmdLine ();
    if (aArgs != null)
      for (final String sArg : aArgs)
        _findOption (aStrToOptionMap, sArg, ret);
    return ret;
  }

  @Nonnull
  public ParsedCmdLine parse (@Nullable final String [] aArgs)
  {
    return parseStatic (m_aOptions, aArgs);
  }
}
