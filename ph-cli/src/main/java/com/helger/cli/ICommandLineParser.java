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

import com.helger.cli.ex.CommandLineParseException;
import com.helger.commons.lang.NonBlockingProperties;

/**
 * A class that implements the <code>CommandLineParser</code> interface can
 * parse a String array according to the {@link Options} specified and return a
 * {@link CommandLine}.
 */
public interface ICommandLineParser
{
  /**
   * Parse the arguments according to the specified options.
   *
   * @param aOptions
   *        the specified Options
   * @param aArguments
   *        the command line arguments
   * @return the list of atomic option and value tokens
   * @throws CommandLineParseException
   *         if there are any problems encountered while parsing the command
   *         line tokens.
   */
  @Nonnull
  default CommandLine parse (@Nonnull final Options aOptions,
                             @Nullable final String [] aArguments) throws CommandLineParseException
  {
    return parse (aOptions, aArguments, false);
  }

  /**
   * Parse the arguments according to the specified options and properties.
   *
   * @param aOptions
   *        the specified Options
   * @param aArguments
   *        the command line arguments
   * @param aProperties
   *        command line option name-value pairs
   * @return the list of atomic option and value tokens
   * @throws CommandLineParseException
   *         if there are any problems encountered while parsing the command
   *         line tokens.
   */
  @Nonnull
  default CommandLine parse (@Nonnull final Options aOptions,
                             @Nullable final String [] aArguments,
                             @Nullable final NonBlockingProperties aProperties) throws CommandLineParseException
  {
    return parse (aOptions, aArguments, aProperties, false);
  }

  /**
   * Parse the arguments according to the specified options.
   *
   * @param aOptions
   *        the specified Options
   * @param aArguments
   *        the command line arguments
   * @param bStopAtNonOption
   *        if <tt>true</tt> an unrecognized argument stops the parsing and the
   *        remaining arguments are added to the {@link CommandLine}s args list.
   *        If <tt>false</tt> an unrecognized argument triggers a
   *        ParseException.
   * @return the list of atomic option and value tokens
   * @throws CommandLineParseException
   *         if there are any problems encountered while parsing the command
   *         line tokens.
   */
  @Nonnull
  default CommandLine parse (@Nonnull final Options aOptions,
                             @Nullable final String [] aArguments,
                             final boolean bStopAtNonOption) throws CommandLineParseException
  {
    return parse (aOptions, aArguments, null, bStopAtNonOption);
  }

  /**
   * Parse the arguments according to the specified options and properties.
   *
   * @param aOptions
   *        the specified Options
   * @param aArguments
   *        the command line arguments
   * @param aProperties
   *        command line option name-value pairs
   * @param bStopAtNonOption
   *        if <tt>true</tt> an unrecognized argument stops the parsing and the
   *        remaining arguments are added to the {@link CommandLine}s args list.
   *        If <tt>false</tt> an unrecognized argument triggers a
   *        ParseException.
   * @return the list of atomic option and value tokens
   * @throws CommandLineParseException
   *         if there are any problems encountered while parsing the command
   *         line tokens.
   */
  @Nonnull
  CommandLine parse (@Nonnull final Options aOptions,
                     @Nullable final String [] aArguments,
                     @Nullable final NonBlockingProperties aProperties,
                     final boolean bStopAtNonOption) throws CommandLineParseException;

}
