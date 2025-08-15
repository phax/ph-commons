/*
 * Original copyright by Apache Software Foundation
 * Copyright (C) 2017-2025 Philip Helger (www.helger.com)
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

import com.helger.annotation.Nonnegative;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.builder.IBuilder;
import com.helger.base.equals.ValueEnforcer;
import com.helger.base.string.StringHelper;
import com.helger.cli.Option.EOptionMultiplicity;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * A nested builder class to create <code>Option</code> instances using descriptive methods.
 * <p>
 * Example usage:
 *
 * <pre>
 * Option option = Option.builder ("a").required (true).longOpt ("arg-name").build ();
 * </pre>
 */
public class OptionBuilder implements IBuilder <Option>
{
  /** the name of the option */
  final String m_sShortOpt;

  /** the long representation of the option */
  String m_sLongOpt;

  /** description of the option */
  String m_sDescription;

  /** The minimum argument count. Must be &le; maxArgs */
  int m_nMinArgs = 0;

  /** The maximum argument count. Must be &ge; minArgs */
  int m_nMaxArgs = 0;

  /**
   * the name of the argument for this option. Makes only sense, if minArgs &gt; 0
   */
  String m_sArgName;

  /**
   * specifies whether this option is required to be present. Makes only sense if minArgs &gt; 0
   */
  EOptionMultiplicity m_eMultiplicity = EOptionMultiplicity.OPTIONAL_ONCE;

  /**
   * the character that is the value separator in case the value. Makes only sense if minArgs &gt; 0
   */
  char m_cValueSep = Option.DEFAULT_VALUE_SEPARATOR;

  /**
   * Constructs a new <code>Builder</code> with the minimum required parameters for an
   * <code>Option</code> instance.
   *
   * @param sShortOpt
   *        short representation of the option
   * @throws IllegalArgumentException
   *         if there are any non valid Option characters in {@code opt}
   */
  protected OptionBuilder (@Nullable final String sShortOpt)
  {
    if (sShortOpt != null)
      OptionValidator.validateShortOption (sShortOpt);
    m_sShortOpt = sShortOpt;
  }

  /**
   * Sets the long name of the Option.
   *
   * @param sLongOpt
   *        the long name of the Option
   * @return this builder, to allow method chaining
   */
  @Nonnull
  public OptionBuilder longOpt (@Nullable final String sLongOpt)
  {
    m_sLongOpt = sLongOpt;
    return this;
  }

  /**
   * Sets the description for this option.
   *
   * @param sDescription
   *        the description of the option.
   * @return this builder, to allow method chaining
   */
  @Nonnull
  public OptionBuilder desc (@Nullable final String sDescription)
  {
    m_sDescription = sDescription;
    return this;
  }

  /**
   * Set the minimum number of arguments that must be present. By default it is 0. This is the
   * number of required arguments. If the option is repeatable, this value is per occurrence.
   *
   * @param nMinArgs
   *        Number of minimum arguments. Must be &ge; 0.
   * @return this for chaining
   */
  @Nonnull
  public OptionBuilder minArgs (@Nonnegative final int nMinArgs)
  {
    ValueEnforcer.isGE0 (nMinArgs, "MinArgs");
    m_nMinArgs = nMinArgs;
    return this;
  }

  /**
   * Set the maximum number of arguments that can be present. By default it is 0. The difference
   * between minimum and maximum arguments are the optional arguments. If the option is repeatable,
   * this value is per occurrence.
   *
   * @param nMaxArgs
   *        Number of maximum arguments. Must be &ge; 0 or {@link Option#INFINITE_VALUES}
   * @return this for chaining
   * @see #maxArgsInfinite()
   */
  @Nonnull
  public OptionBuilder maxArgs (final int nMaxArgs)
  {
    ValueEnforcer.isTrue (nMaxArgs == Option.INFINITE_VALUES || nMaxArgs >= 0,
                          () -> "MaxArgs must be " + Option.INFINITE_VALUES + " or >= 0!");
    m_nMaxArgs = nMaxArgs;
    return this;
  }

  /**
   * Shortcut for <code>maxArgs (UNLIMITED_VALUES);</code>
   *
   * @return this for chaining
   * @see #maxArgs(int)
   */
  @Nonnull
  public OptionBuilder maxArgsInfinite ()
  {
    return maxArgs (Option.INFINITE_VALUES);
  }

  /**
   * Shortcut for setting minArgs and maxArgs at once. If the option is repeatable, this value is
   * per occurrence.
   *
   * @param nMinArgs
   *        Number of minimum arguments. Must be &ge; 0.
   * @param nMaxArgs
   *        Number of maximum arguments. Must be &ge; 0 or {@link Option#INFINITE_VALUES}
   * @return this for chaining
   * @see #minArgs(int)
   * @see #maxArgs(int)
   * @see #args(int)
   */
  @Nonnull
  public OptionBuilder args (@Nonnegative final int nMinArgs, final int nMaxArgs)
  {
    return minArgs (nMinArgs).maxArgs (nMaxArgs);
  }

  /**
   * Shortcut for setting minArgs and maxArgs to the same value, making this the number of required
   * arguments. If the option is repeatable, this value is per occurrence.
   *
   * @param nArgs
   *        Number of arguments. Must be &ge; 0.
   * @return this for chaining
   * @see #minArgs(int)
   * @see #maxArgs(int)
   * @see #args(int, int)
   */
  @Nonnull
  public OptionBuilder args (@Nonnegative final int nArgs)
  {
    return minArgs (nArgs).maxArgs (nArgs);
  }

  /**
   * Sets the display name for the argument value. May only be used if at least one argument is
   * present.
   *
   * @param sArgName
   *        the display name for the argument value.
   * @return this builder, to allow method chaining
   */
  @Nonnull
  public OptionBuilder argName (@Nullable final String sArgName)
  {
    m_sArgName = sArgName;
    return this;
  }

  /**
   * Sets whether the Option is mandatory.
   *
   * @param bRequired
   *        specifies whether the Option is mandatory
   * @return this builder, to allow method chaining
   */
  @Nonnull
  public OptionBuilder required (final boolean bRequired)
  {
    if (bRequired)
      m_eMultiplicity = m_eMultiplicity.getAsRequired ();
    else
      m_eMultiplicity = m_eMultiplicity.getAsOptional ();
    return this;
  }

  /**
   * Mark this option as repeatable or not. By default it is not repeatable.
   *
   * @param bRepeatable
   *        <code>true</code> if this option can be repeated, <code>false</code> if not.
   * @return this builder, to allow method chaining
   */
  @Nonnull
  public OptionBuilder repeatable (final boolean bRepeatable)
  {
    if (bRepeatable)
      m_eMultiplicity = m_eMultiplicity.getAsRepeatable ();
    else
      m_eMultiplicity = m_eMultiplicity.getAsOnce ();
    return this;
  }

  /**
   * The Option will use <code>sep</code> as a means to separate argument values.
   * <p>
   * <b>Example:</b>
   *
   * <pre>
   * Option opt = Option.builder ("D").hasArgs ().valueSeparator ('=').build ();
   * Options options = new Options ();
   * options.addOption (opt);
   * String [] args = { "-Dkey=value" };
   * CommandLineParser parser = new DefaultParser ();
   * CommandLine line = parser.parse (options, args);
   * String propertyName = line.getOptionValues ("D")[0]; // will be "key"
   * String propertyValue = line.getOptionValues ("D")[1]; // will be "value"
   * </pre>
   *
   * @param cValueSep
   *        The value separator.
   * @return this builder, to allow method chaining
   */
  @Nonnull
  public OptionBuilder valueSeparator (final char cValueSep)
  {
    m_cValueSep = cValueSep;
    return this;
  }

  /**
   * Constructs an Option with the values declared by this {@link OptionBuilder}.
   *
   * @return the new {@link Option}
   * @throws IllegalArgumentException
   *         if neither {@code opt} or {@code longOpt} has been set
   */
  @Nonnull
  @ReturnsMutableCopy
  public Option build ()
  {
    if (StringHelper.isEmpty (m_sShortOpt) && StringHelper.isEmpty (m_sLongOpt))
      throw new IllegalStateException ("Either opt or longOpt must be specified");
    if (m_nMaxArgs != Option.INFINITE_VALUES && m_nMaxArgs < m_nMinArgs)
      throw new IllegalStateException ("MinArgs (" + m_nMinArgs + ") must be <= MaxArgs (" + m_nMaxArgs + ")");
    if (m_nMinArgs == 0 && m_nMaxArgs == 0)
    {
      if (m_sArgName != null)
        throw new IllegalStateException ("ArgName may only be provided if at least one argument is present");
      if (m_eMultiplicity.isRequired ())
        Option.LOGGER.warn ("Having a required option without an argument may not be what is desired.");
      if (m_eMultiplicity.isRepeatable ())
        Option.LOGGER.warn ("Having a repeatable option without an argument may not be what is desired.");
      if (m_cValueSep != Option.DEFAULT_VALUE_SEPARATOR)
        throw new IllegalStateException ("ValueSeparator may only be provided if at least one argument is present");
    }

    return new Option (this);
  }
}
