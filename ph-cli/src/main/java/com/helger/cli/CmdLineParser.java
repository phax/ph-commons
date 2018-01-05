/**
 * Original copyright by Apache Software Foundation
 * Copyright (C) 2017-2018 Philip Helger (www.helger.com)
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
package com.helger.cli;

import java.util.Arrays;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.CommonsHashMap;
import com.helger.commons.collection.impl.CommonsHashSet;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.collection.impl.ICommonsMap;
import com.helger.commons.collection.impl.ICommonsSet;
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

  private final Options m_aOptions;

  public CmdLineParser (@Nonnull final Options aOptions)
  {
    ValueEnforcer.notNull (aOptions, "Options");
    m_aOptions = aOptions;
  }

  @Nullable
  private static MatchedOption _findMatchingOption (@Nonnull final ICommonsMap <String, Option> aStrToOptionMap,
                                                    @Nonnull @Nonempty final String sToken)
  {
    // Skip prefix (long before first)
    String sText = sToken;
    String sPrefix = "";
    int nPrefix = 0;
    if (sText.startsWith (PREFIX_LONG_OPT))
      nPrefix = PREFIX_LONG_OPT.length ();
    else
      if (sText.startsWith (PREFIX_SHORT_OPT))
        nPrefix = PREFIX_SHORT_OPT.length ();

    if (nPrefix > 0)
    {
      sPrefix = sText.substring (0, nPrefix);
      sText = sText.substring (nPrefix);
    }

    if (sText.length () == 0)
    {
      // Just "--" or "-" without a name
      return null;
    }

    Option aOption = aStrToOptionMap.get (sText);
    if (aOption != null)
      return new MatchedOption (aOption, sPrefix + sText);

    // No direct match - try something like -Dversion=1.0 or -Xmx512m
    while (true)
    {
      // Remove last char and try
      sText = sText.substring (0, sText.length () - 1);
      if (sText.length () == 0)
        break;

      aOption = aStrToOptionMap.get (sText);
      if (aOption != null)
      {
        return new MatchedOption (aOption, sPrefix + sText);
      }
    }
    return null;
  }

  @Nonnull
  private static String _getDisplayName (@Nonnull final Option aOption)
  {
    String ret = "";
    if (aOption.hasShortOpt ())
      ret += PREFIX_SHORT_OPT + aOption.getShortOpt ();
    if (aOption.hasLongOpt ())
    {
      if (ret.length () > 0)
        ret += "/";
      ret += PREFIX_LONG_OPT + aOption.getLongOpt ();
    }
    return ret;
  }

  @Nonnull
  private static String _getDisplayName (@Nonnull final OptionGroup aOptionGroup)
  {
    return "[" + StringHelper.getImplodedMapped (" | ", aOptionGroup, CmdLineParser::_getDisplayName) + "]";
  }

  @Nonnull
  public static ParsedCmdLine parseStatic (@Nonnull final Options aOptions,
                                           @Nullable final String [] aArgs) throws CmdLineParseException
  {
    ValueEnforcer.notNull (aOptions, "Options");

    final ICommonsList <Option> aAllOptions = aOptions.getAllResolvedOptions ();
    final ICommonsSet <OptionGroup> aParsedOptionGroups = new CommonsHashSet <> ();

    // Create map from option name to option definition
    final ICommonsMap <String, Option> aStrToOptionMap = new CommonsHashMap <> ();
    for (final Option aOption : aAllOptions)
    {
      if (aOption.hasShortOpt ())
        aStrToOptionMap.put (aOption.getShortOpt (), aOption);
      if (aOption.hasLongOpt ())
        aStrToOptionMap.put (aOption.getLongOpt (), aOption);
    }

    final ParsedCmdLine ret = new ParsedCmdLine ();
    if (aArgs != null)
      for (int nArgIndex = 0; nArgIndex < aArgs.length; ++nArgIndex)
      {
        final String sArg = StringHelper.trim (aArgs[nArgIndex]);
        if (StringHelper.hasText (sArg))
        {
          final MatchedOption aMatchedOption = _findMatchingOption (aStrToOptionMap, sArg);
          if (aMatchedOption != null)
          {
            // Found option - read values
            final Option aOption = aMatchedOption.m_aOption;

            // Is it part of an option group?
            final OptionGroup aOG = aOptions.getOptionGroup (aOption);
            if (aOG != null && !aParsedOptionGroups.add (aOG))
              throw new CmdLineParseException (ECmdLineParseError.ANOTHER_OPTION_OF_GROUP_ALREADY_PRESENT,
                                               aOG,
                                               "The option " +
                                                    _getDisplayName (aOption) +
                                                    " cannot be selected because another option of the same group is already present!");

            if (!aOption.isRepeatable () && ret.hasOption (aOption))
              throw new CmdLineParseException (ECmdLineParseError.NON_REPEATABLE_OPTION_OCCURS_MORE_THAN_ONCE,
                                               aOption,
                                               "The option " +
                                                        _getDisplayName (aOption) +
                                                        " was already specified but cannot be contained more than once!");

            final String sValueInArg = sArg.substring (aMatchedOption.m_sMatchedText.length ());

            if (s_aLogger.isDebugEnabled ())
              s_aLogger.debug ("Matched '" +
                               aMatchedOption.m_sMatchedText +
                               "' to " +
                               _getDisplayName (aOption) +
                               (sValueInArg.length () > 0 ? "; rest is '" + sValueInArg + "'" : ""));

            final int nMinArgs = aOption.getMinArgCount ();
            final int nMaxArgs = aOption.getMaxArgCount ();
            final boolean bUnlimitedArgs = aOption.hasInfiniteArgs ();
            final ICommonsList <String> aValues = new CommonsArrayList <> ();

            if (StringHelper.hasText (sValueInArg))
            {
              // As e.g. in "-Dtest=value"
              if (aOption.hasValueSeparator ())
                StringHelper.explode (aOption.getValueSeparator (),
                                      sValueInArg,
                                      bUnlimitedArgs ? -1 : nMaxArgs - aValues.size (),
                                      aValues::add);
              else
                aValues.add (sValueInArg);
            }

            // Find remaining values
            while (nArgIndex + 1 < aArgs.length && aOption.canHaveMoreValues (aValues.size ()))
            {
              final String sNextArg = StringHelper.trim (aArgs[nArgIndex + 1]);

              // Is the next argument also an option?
              if (_findMatchingOption (aStrToOptionMap, sNextArg) != null)
              {
                if (aValues.size () >= nMinArgs)
                {
                  // We already have all the required options - go to next
                  // option
                  break;
                }

                // It is an option, but the number of required arguments is too
                // little - use as value
              }

              // Lets consume this argument
              nArgIndex++;

              if (StringHelper.hasText (sNextArg))
              {
                // As e.g. in "-Dtest=value"
                if (aOption.hasValueSeparator ())
                {
                  StringHelper.explode (aOption.getValueSeparator (),
                                        sNextArg,
                                        bUnlimitedArgs ? -1 : nMaxArgs - aValues.size (),
                                        aValues::add);
                }
                else
                  aValues.add (sNextArg);
              }
            }

            if (aValues.size () < nMinArgs)
              throw new CmdLineParseException (ECmdLineParseError.TOO_LITTLE_REQUIRED_VALUES,
                                               aOption,
                                               _getDisplayName (aOption) +
                                                        " requires at least " +
                                                        nMinArgs +
                                                        " values but has only " +
                                                        aValues.size ());

            ret.internalAddValue (aOption, aValues);
            if (aOG != null)
              ret.internalAddValue (aOG, aValues);
          }
          else
          {
            ret.internalAddUnhandledToken (sArg);
          }
        }
      }

    // Check if all required options are present
    for (final IOptionBase aOptionBase : aOptions)
      if (aOptionBase.isRequired ())
      {
        if (aOptionBase instanceof Option)
        {
          final Option aOption = (Option) aOptionBase;
          if (!ret.hasOption (aOption))
            throw new CmdLineParseException (ECmdLineParseError.REQUIRED_OPTION_IS_MISSING,
                                             aOption,
                                             "The option " +
                                                      _getDisplayName (aOption) +
                                                      " is required but is missing!");
        }
        else
        {
          final OptionGroup aOptionGroup = (OptionGroup) aOptionBase;
          if (!ret.hasOption (aOptionGroup))
            throw new CmdLineParseException (ECmdLineParseError.REQUIRED_OPTION_IS_MISSING,
                                             aOptionGroup,
                                             "An option of " +
                                                           _getDisplayName (aOptionGroup) +
                                                           " is required but is missing!");
        }
      }

    if (s_aLogger.isDebugEnabled ())
      s_aLogger.debug ("Parsed command line args " + Arrays.toString (aArgs) + " to " + ret);
    return ret;
  }

  @Nonnull
  public ParsedCmdLine parse (@Nullable final String [] aArgs) throws CmdLineParseException
  {
    return parseStatic (m_aOptions, aArgs);
  }

  @Nullable
  public ParsedCmdLine parseOrNull (@Nullable final String [] aArgs)
  {
    try
    {
      return parse (aArgs);
    }
    catch (final CmdLineParseException ex)
    {
      return null;
    }
  }
}
