package com.helger.cli2;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.CommonsHashMap;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.collection.impl.ICommonsMap;
import com.helger.commons.string.StringHelper;

public class CmdLineParser
{
  public static final String PREFIX_SHORT_OPT = "-";
  public static final String PREFIX_LONG_OPT = "--";

  private static final Logger s_aLogger = LoggerFactory.getLogger (CmdLineParser.class);

  private static final class MatchedOption
  {
    private final Option m_aOption;
    private final String m_sMatchedText;

    MatchedOption (@Nonnull final Option aOption, @Nonnull @Nonempty final String sMatchedText)
    {
      m_aOption = aOption;
      m_sMatchedText = sMatchedText;
    }
  }

  private final ICommonsList <Option> m_aOptions;

  public CmdLineParser (@Nullable final Iterable <? extends Option> aOptions)
  {
    m_aOptions = new CommonsArrayList <> (aOptions);
  }

  @Nullable
  private static MatchedOption _findMatchingOption (@Nonnull final ICommonsMap <String, Option> aStrToOptionMap,
                                                    @Nonnull @Nonempty final String sToken)
  {
    // Skip prefix (long before first)
    String sText = sToken;
    if (sText.startsWith (PREFIX_LONG_OPT))
      sText = sText.substring (PREFIX_LONG_OPT.length ());
    else
      if (sText.startsWith (PREFIX_SHORT_OPT))
        sText = sText.substring (PREFIX_SHORT_OPT.length ());

    if (sText.length () == 0)
    {
      // Just "--" or "-" without a name
      return null;
    }

    Option aOption = aStrToOptionMap.get (sText);
    if (aOption != null)
      return new MatchedOption (aOption, sText);

    // No direct match - try something like -Dversion=1.0 or -Xmx512m
    while (true)
    {
      // Remove last char and try
      sText = sText.substring (0, sText.length () - 1);
      if (sText.length () == 0)
        break;

      aOption = aStrToOptionMap.get (sText);
      if (aOption != null)
        return new MatchedOption (aOption, sText);
    }
    return null;
  }

  private static void _unknownToken (@Nullable final String sArg)
  {
    s_aLogger.error ("Ignoring unknown token '" + sArg + "'");
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
        if (StringHelper.hasText (sArg))
        {
          final MatchedOption aMatchedOption = _findMatchingOption (aStrToOptionMap, sArg);
          if (aMatchedOption != null)
          {
            // Found option - read values
            s_aLogger.info ("Matched '" + aMatchedOption.m_sMatchedText + "' to " + aMatchedOption.m_aOption);
          }
          else
            _unknownToken (sArg);
        }
    return ret;
  }

  @Nonnull
  public ParsedCmdLine parse (@Nullable final String [] aArgs)
  {
    return parseStatic (m_aOptions, aArgs);
  }
}
