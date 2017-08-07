/**
 * Original copyright by Apache Software Foundation
 * Copyright (C) 2017 Philip Helger (www.helger.com)
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.cli.Options.IRequiredOption;
import com.helger.cli.Options.RequiredArg;
import com.helger.cli.Options.RequiredGroup;
import com.helger.cli.ex.AlreadySelectedException;
import com.helger.cli.ex.AmbiguousOptionException;
import com.helger.cli.ex.CommandLineParseException;
import com.helger.cli.ex.MissingArgumentException;
import com.helger.cli.ex.MissingOptionException;
import com.helger.cli.ex.UnrecognizedOptionException;
import com.helger.commons.ValueEnforcer;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.lang.NonBlockingProperties;

/**
 * Default commandline parser.
 *
 * @since 1.3
 */
public class CommandLineParser implements ICommandLineParser
{
  public static final String PREFIX_SHORT_OPT = "-";
  public static final String PREFIX_LONG_OPT = "--";

  /** The current options. */
  private Options m_aOptions;

  /**
   * Flag indicating how unrecognized tokens are handled. <tt>true</tt> to stop
   * the parsing and add the remaining tokens to the args list. <tt>false</tt>
   * to throw an exception.
   */
  private boolean m_bStopAtNonOption;

  /** The token currently processed. */
  private String m_sCurrentToken;

  /** The last option parsed. */
  private Option m_aCurrentOption;

  /**
   * Flag indicating if tokens should no longer be analyzed and simply added as
   * arguments of the command line.
   */
  private boolean m_bSkipParsing;

  /**
   * The required options and groups expected to be found when parsing the
   * command line.
   */
  private ICommonsList <IRequiredOption> m_aExpectedOpts;

  public CommandLineParser ()
  {}

  /**
   * Handle any command line token.
   *
   * @param sToken
   *        the command line token to handle
   * @throws CommandLineParseException
   */
  private void _handleToken (@Nonnull final String sToken,
                             @Nonnull final CommandLine aCmdLine) throws CommandLineParseException
  {
    m_sCurrentToken = sToken;

    if (m_bSkipParsing)
    {
      aCmdLine.addArg (sToken);
    }
    else
      if (PREFIX_LONG_OPT.equals (sToken))
      {
        m_bSkipParsing = true;
      }
      else
        if (m_aCurrentOption != null && m_aCurrentOption.acceptsArg () && _isArgument (sToken))
        {
          m_aCurrentOption.addValueForProcessing (Util.stripLeadingAndTrailingQuotes (sToken));
        }
        else
          if (sToken.startsWith (PREFIX_LONG_OPT))
          {
            // long
            _handleLongOption (sToken, aCmdLine);
          }
          else
            if (sToken.startsWith (PREFIX_SHORT_OPT) && !PREFIX_SHORT_OPT.equals (sToken))
            {
              // short
              _handleShortAndLongOption (sToken, aCmdLine);
            }
            else
            {
              // neither long nor short
              _handleUnknownToken (sToken, aCmdLine);
            }

    if (m_aCurrentOption != null && !m_aCurrentOption.acceptsArg ())
    {
      // Reset
      m_aCurrentOption = null;
    }
  }

  @Nonnull
  public CommandLine parse (@Nonnull final Options aOptions,
                            @Nullable final String [] aArguments,
                            @Nullable final NonBlockingProperties aProperties,
                            final boolean bStopAtNonOption) throws CommandLineParseException
  {
    m_aOptions = aOptions;
    m_bStopAtNonOption = bStopAtNonOption;
    m_sCurrentToken = null;
    m_aCurrentOption = null;
    m_bSkipParsing = false;
    m_aExpectedOpts = aOptions.getAllRequiredOptions ();

    // clear the data from the groups
    for (final OptionGroup aGroup : aOptions.getAllOptionGroups ())
      aGroup.setSelected (null);

    final CommandLine aCmdLine = new CommandLine ();

    if (aArguments != null)
      for (final String sArgument : aArguments)
        _handleToken (sArgument, aCmdLine);

    // check the arguments of the last option
    _checkRequiredArgs ();

    // add the default options
    if (aProperties != null)
      _handleProperties (aProperties, aCmdLine);

    _checkRequiredOptions ();

    return aCmdLine;
  }

  /**
   * Sets the values of Options using the values in <code>properties</code>.
   *
   * @param aProperties
   *        The value properties to be processed.
   */
  private void _handleProperties (@Nonnull final NonBlockingProperties aProperties,
                                  @Nonnull final CommandLine aCmdLine) throws CommandLineParseException
  {
    for (final String option : aProperties.keySet ())
    {
      final Option opt = m_aOptions.getOption (option);
      if (opt == null)
        throw new UnrecognizedOptionException ("Default option wasn't defined", option);

      // if the option is part of a group, check if another option of the group
      // has been selected
      final OptionGroup aGroup = m_aOptions.getOptionGroup (opt);
      final boolean selected = aGroup != null && aGroup.getSelected () != null;

      if (!aCmdLine.hasOption (option) && !selected)
      {
        // get the value from the properties
        final String sValue = aProperties.getProperty (option);

        if (opt.hasArg ())
        {
          if (opt.hasNoValues ())
          {
            opt.addValueForProcessing (sValue);
          }
        }
        else
          if (!("yes".equalsIgnoreCase (sValue) || "true".equalsIgnoreCase (sValue) || "1".equalsIgnoreCase (sValue)))
          {
            // if the value is not yes, true or 1 then don't add the option to
            // the CommandLine
            continue;
          }

        _handleOption (opt, aCmdLine);
        m_aCurrentOption = null;
      }
    }
  }

  /**
   * Throws a {@link MissingOptionException} if all of the required options are
   * not present.
   *
   * @throws MissingOptionException
   *         if any of the required Options are not present.
   */
  private void _checkRequiredOptions () throws MissingOptionException
  {
    // if there are required options that have not been processed
    if (m_aExpectedOpts.isNotEmpty ())
      throw new MissingOptionException (m_aExpectedOpts);
  }

  /**
   * Throw a {@link MissingArgumentException} if the current option didn't
   * receive the number of arguments expected.
   */
  private void _checkRequiredArgs () throws CommandLineParseException
  {
    if (m_aCurrentOption != null && m_aCurrentOption.requiresArg ())
    {
      throw new MissingArgumentException (m_aCurrentOption);
    }
  }

  /**
   * Returns true is the token is a valid argument.
   *
   * @param sToken
   */
  private boolean _isArgument (final String sToken)
  {
    return !_isOption (sToken) || _isNegativeNumber (sToken);
  }

  /**
   * Check if the token is a negative number.
   *
   * @param sToken
   */
  private boolean _isNegativeNumber (final String sToken)
  {
    try
    {
      Double.parseDouble (sToken);
      return true;
    }
    catch (final NumberFormatException e)
    {
      return false;
    }
  }

  /**
   * Tells if the token looks like an option.
   *
   * @param sToken
   */
  private boolean _isOption (final String sToken)
  {
    return _isLongOption (sToken) || _isShortOption (sToken);
  }

  /**
   * Tells if the token looks like a short option.
   *
   * @param sToken
   */
  private boolean _isShortOption (@Nonnull final String sToken)
  {
    // short options (-S, -SV, -S=V, -SV1=V2, -S1S2)
    if (!sToken.startsWith (PREFIX_SHORT_OPT) || sToken.length () <= 1)
    {
      return false;
    }

    // remove leading "-" and "=value"
    // TODO value separator usage?
    final int nPos = sToken.indexOf ("=");

    final String sOptName = nPos == -1 ? sToken.substring (1) : sToken.substring (1, nPos);
    if (m_aOptions.hasShortOption (sOptName))
    {
      return true;
    }

    // check for several concatenated short options
    return sOptName.length () > 0 && m_aOptions.hasShortOption (sOptName.substring (0, 1));
  }

  /**
   * Tells if the token looks like a long option.
   *
   * @param sToken
   */
  private boolean _isLongOption (@Nonnull final String sToken)
  {
    if (!sToken.startsWith (PREFIX_LONG_OPT) || sToken.length () <= 2)
      return false;

    // TODO value separator usage?
    final int nPos = sToken.indexOf ("=");
    final String sTokenName = nPos < 0 ? sToken : sToken.substring (0, nPos);
    if (m_aOptions.getAllMatchingOptions (sTokenName).isNotEmpty ())
    {
      // long or partial long options (--L, -L, --L=V, -L=V, --l, --l=V)
      return true;
    }

    if (_getLongPrefix (sToken) != null && !sToken.startsWith (PREFIX_LONG_OPT))
    {
      // -LV
      return true;
    }

    return false;
  }

  /**
   * Handles an unknown token. If the token starts with a dash an
   * UnrecognizedOptionException is thrown. Otherwise the token is added to the
   * arguments of the command line. If the stopAtNonOption flag is set, this
   * stops the parsing and the remaining tokens are added as-is in the arguments
   * of the command line.
   *
   * @param sToken
   *        the command line token to handle
   */
  private void _handleUnknownToken (@Nonnull final String sToken,
                                    @Nonnull final CommandLine aCmdLine) throws CommandLineParseException
  {
    if (sToken.startsWith ("-") && sToken.length () > 1 && !m_bStopAtNonOption)
      throw new UnrecognizedOptionException ("Unrecognized option: " + sToken, sToken);

    aCmdLine.addArg (sToken);
    if (m_bStopAtNonOption)
    {
      m_bSkipParsing = true;
    }
  }

  /**
   * Handles the following tokens: --L --L=V --L V --l
   *
   * @param sToken
   *        the command line token to handle
   */
  private void _handleLongOption (@Nonnull final String sToken,
                                  @Nonnull final CommandLine aCmdLine) throws CommandLineParseException
  {
    if (sToken.indexOf ('=') == -1)
      _handleLongOptionWithoutEqual (sToken, aCmdLine);
    else
      _handleLongOptionWithEqual (sToken, aCmdLine);
  }

  /**
   * Handles the following tokens: --L -L --l -l
   *
   * @param sToken
   *        the command line token to handle
   */
  private void _handleLongOptionWithoutEqual (final String sToken,
                                              @Nonnull final CommandLine aCmdLine) throws CommandLineParseException
  {
    final ICommonsList <String> aMatchingOpts = m_aOptions.getAllMatchingOptions (sToken);
    if (aMatchingOpts.isEmpty ())
    {
      _handleUnknownToken (m_sCurrentToken, aCmdLine);
    }
    else
    {
      if (aMatchingOpts.size () > 1)
        throw new AmbiguousOptionException (sToken, aMatchingOpts);
      _handleOption (m_aOptions.getOption (aMatchingOpts.get (0)), aCmdLine);
    }
  }

  /**
   * Handles the following tokens: --L=V -L=V --l=V -l=V
   *
   * @param sToken
   *        the command line token to handle
   */
  private void _handleLongOptionWithEqual (@Nonnull final String sToken,
                                           @Nonnull final CommandLine aCmdLine) throws CommandLineParseException
  {
    final int pos = sToken.indexOf ('=');
    final String value = sToken.substring (pos + 1);
    final String opt = sToken.substring (0, pos);

    final ICommonsList <String> aMatchingOpts = m_aOptions.getAllMatchingOptions (opt);
    if (aMatchingOpts.isEmpty ())
    {
      _handleUnknownToken (m_sCurrentToken, aCmdLine);
    }
    else
      if (aMatchingOpts.size () > 1)
        throw new AmbiguousOptionException (opt, aMatchingOpts);
      else
      {
        final Option aOption = m_aOptions.getOption (aMatchingOpts.getFirst ());
        if (aOption.acceptsArg ())
        {
          _handleOption (aOption, aCmdLine);
          m_aCurrentOption.addValueForProcessing (value);
          m_aCurrentOption = null;
        }
        else
        {
          _handleUnknownToken (m_sCurrentToken, aCmdLine);
        }
      }
  }

  /**
   * Handles the following tokens: -S -SV -S V -S=V -S1S2 -S1S2 V -SV1=V2 -L -LV
   * -L V -L=V -l
   *
   * @param sToken
   *        the command line token to handle
   */
  private void _handleShortAndLongOption (final String sToken,
                                          @Nonnull final CommandLine aCmdLine) throws CommandLineParseException
  {
    final String t = Util.stripLeadingHyphens (sToken);
    final int pos = t.indexOf ('=');

    if (t.length () == 1)
    {
      // -S
      if (m_aOptions.hasShortOption (t))
        _handleOption (m_aOptions.getOption (t), aCmdLine);
      else
        _handleUnknownToken (sToken, aCmdLine);
    }
    else
      if (pos == -1)
      {
        // no equal sign found (-xxx)
        if (m_aOptions.hasShortOption (t))
        {
          _handleOption (m_aOptions.getOption (t), aCmdLine);
        }
        else
          if (!m_aOptions.getAllMatchingOptions (t).isEmpty ())
          {
            // -L or -l
            _handleLongOptionWithoutEqual (sToken, aCmdLine);
          }
          else
          {
            // look for a long prefix (-Xmx512m)
            final String opt = _getLongPrefix (t);

            if (opt != null && m_aOptions.getOption (opt).acceptsArg ())
            {
              _handleOption (m_aOptions.getOption (opt), aCmdLine);
              m_aCurrentOption.addValueForProcessing (t.substring (opt.length ()));
              m_aCurrentOption = null;
            }
            else
              if (_isJavaProperty (t))
              {
                // -SV1 (-Dflag)
                _handleOption (m_aOptions.getOption (t.substring (0, 1)), aCmdLine);
                m_aCurrentOption.addValueForProcessing (t.substring (1));
                m_aCurrentOption = null;
              }
              else
              {
                // -S1S2S3 or -S1S2V
                handleConcatenatedOptions (sToken, aCmdLine);
              }
          }
      }
      else
      {
        // equal sign found (-xxx=yyy)
        final String opt = t.substring (0, pos);
        final String value = t.substring (pos + 1);

        if (opt.length () == 1)
        {
          // -S=V
          final Option aOption = m_aOptions.getOption (opt);
          if (aOption != null && aOption.acceptsArg ())
          {
            _handleOption (aOption, aCmdLine);
            m_aCurrentOption.addValueForProcessing (value);
            m_aCurrentOption = null;
          }
          else
          {
            _handleUnknownToken (sToken, aCmdLine);
          }
        }
        else
          if (_isJavaProperty (opt))
          {
            // -SV1=V2 (-Dkey=value)
            _handleOption (m_aOptions.getOption (opt.substring (0, 1)), aCmdLine);
            m_aCurrentOption.addValueForProcessing (opt.substring (1));
            m_aCurrentOption.addValueForProcessing (value);
            m_aCurrentOption = null;
          }
          else
          {
            // -L=V or -l=V
            _handleLongOptionWithEqual (sToken, aCmdLine);
          }
      }
  }

  /**
   * Search for a prefix that is the long name of an option (-Xmx512m)
   *
   * @param sToken
   */
  @Nullable
  private String _getLongPrefix (final String sToken)
  {
    final String t = Util.stripLeadingHyphens (sToken);

    int i;
    String opt = null;
    for (i = t.length () - 2; i > 1; i--)
    {
      final String sPrefix = t.substring (0, i);
      if (m_aOptions.hasLongOption (sPrefix))
      {
        opt = sPrefix;
        break;
      }
    }

    return opt;
  }

  /**
   * Check if the specified token is a Java-like property (-Dkey=value).
   */
  private boolean _isJavaProperty (@Nonnull final String sToken)
  {
    final String sOpt = sToken.substring (0, 1);
    final Option aOption = m_aOptions.getOption (sOpt);

    return aOption != null && (aOption.getNumberOfArgs () >= 2 || aOption.hasUnlimitedNumberOfArgs ());
  }

  private void _handleOption (@Nonnull final Option aOption,
                              @Nonnull final CommandLine aCmdLine) throws CommandLineParseException
  {
    // check the previous option before handling the next one
    _checkRequiredArgs ();

    final Option aRealOption = aOption.getClone ();
    _updateRequiredOptions (aRealOption);
    aCmdLine.addOption (aRealOption);
    if (aRealOption.hasArg ())
      m_aCurrentOption = aRealOption;
    else
      m_aCurrentOption = null;
  }

  /**
   * Removes the option or its group from the list of expected elements.
   *
   * @param aOption
   */
  private void _updateRequiredOptions (@Nonnull final Option aOption) throws AlreadySelectedException
  {
    ValueEnforcer.notNull (aOption, "Option");

    if (aOption.isRequired ())
      m_aExpectedOpts.remove (new RequiredArg (aOption.getKey ()));

    // if the option is in an OptionGroup make that option the selected option
    // of the group
    final OptionGroup aGroup = m_aOptions.getOptionGroup (aOption);
    if (aGroup != null)
    {
      if (aGroup.isRequired ())
        m_aExpectedOpts.remove (new RequiredGroup (aGroup));

      aGroup.setSelected (aOption);
    }
  }

  /**
   * Breaks <code>token</code> into its constituent parts using the following
   * algorithm.
   * <ul>
   * <li>ignore the first character ("<b>-</b>")</li>
   * <li>for each remaining character check if an {@link Option} exists with
   * that id.</li>
   * <li>if an {@link Option} does exist then add that character prepended with
   * "<b>-</b>" to the list of processed tokens.</li>
   * <li>if the {@link Option} can have an argument value and there are
   * remaining characters in the token then add the remaining characters as a
   * token to the list of processed tokens.</li>
   * <li>if an {@link Option} does <b>NOT</b> exist <b>AND</b>
   * <code>stopAtNonOption</code> <b>IS</b> set then add the special token
   * "<b>--</b>" followed by the remaining characters and also the remaining
   * tokens directly to the processed tokens list.</li>
   * <li>if an {@link Option} does <b>NOT</b> exist <b>AND</b>
   * <code>stopAtNonOption</code> <b>IS NOT</b> set then add that character
   * prepended with "<b>-</b>".</li>
   * </ul>
   *
   * @param sToken
   *        The current token to be <b>burst</b> at the first non-Option
   *        encountered.
   * @param aCmdLine
   *        The command line object to fill. May not be <code>null</code>.
   * @throws CommandLineParseException
   *         if there are any problems encountered while parsing the command
   *         line token.
   */
  protected void handleConcatenatedOptions (@Nonnull final String sToken,
                                            @Nonnull final CommandLine aCmdLine) throws CommandLineParseException
  {
    for (int i = 1; i < sToken.length (); i++)
    {
      final String ch = sToken.substring (i, i + 1);

      final Option aOption = m_aOptions.getOption (ch);
      if (aOption != null)
      {
        _handleOption (aOption, aCmdLine);

        if (m_aCurrentOption != null && sToken.length () != i + 1)
        {
          // add the trail as an argument of the option
          m_aCurrentOption.addValueForProcessing (sToken.substring (i + 1));
          break;
        }
      }
      else
      {
        _handleUnknownToken (m_bStopAtNonOption && i > 1 ? sToken.substring (i) : sToken, aCmdLine);
        break;
      }
    }
  }
}
