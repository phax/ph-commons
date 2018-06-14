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

import java.io.Serializable;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.string.StringHelper;
import com.helger.commons.string.ToStringGenerator;

@Immutable
public class Option implements IOptionBase
{
  public enum EOptionMultiplicity
  {
    /** 0..1, <code>{0,1} or ?</code> */
    OPTIONAL_ONCE,
    /** 0..n, <code>{0,} or *</code> */
    OPTIONAL_MANY,
    /** 1..1, required */
    REQUIRED_ONCE,
    /** 1..n, <code>{1,} or +</code> */
    REQUIRED_MANY;

    public boolean isOptional ()
    {
      return this == OPTIONAL_ONCE || this == OPTIONAL_MANY;
    }

    public boolean isRequired ()
    {
      return this == REQUIRED_ONCE || this == REQUIRED_MANY;
    }

    public boolean isOnce ()
    {
      return this == OPTIONAL_ONCE || this == REQUIRED_ONCE;
    }

    public boolean isRepeatable ()
    {
      return this == OPTIONAL_MANY || this == REQUIRED_MANY;
    }

    @Nonnull
    public EOptionMultiplicity getAsRequired ()
    {
      if (this == OPTIONAL_ONCE)
        return REQUIRED_ONCE;
      if (this == OPTIONAL_MANY)
        return REQUIRED_MANY;
      return this;
    }

    @Nonnull
    public EOptionMultiplicity getAsOptional ()
    {
      if (this == REQUIRED_ONCE)
        return OPTIONAL_ONCE;
      if (this == REQUIRED_MANY)
        return OPTIONAL_MANY;
      return this;
    }

    @Nonnull
    public EOptionMultiplicity getAsRepeatable ()
    {
      if (this == OPTIONAL_ONCE)
        return OPTIONAL_MANY;
      if (this == REQUIRED_ONCE)
        return REQUIRED_MANY;
      return this;
    }

    @Nonnull
    public EOptionMultiplicity getAsOnce ()
    {
      if (this == OPTIONAL_MANY)
        return OPTIONAL_ONCE;
      if (this == REQUIRED_MANY)
        return REQUIRED_ONCE;
      return this;
    }
  }

  public static final char DEFAULT_VALUE_SEPARATOR = 0;

  /** constant that specifies the number of argument values is infinite */
  public static final int INFINITE_VALUES = -1;

  private static final Logger s_aLogger = LoggerFactory.getLogger (Option.class);

  /** the name of the option */
  private final String m_sShortOpt;

  /** the long representation of the option */
  private final String m_sLongOpt;

  /** description of the option */
  private final String m_sDescription;

  /** The minimum argument count. Must be &le; maxArgs */
  private final int m_nMinArgs;

  /** The maximum argument count. Must be &ge; minArgs */
  private final int m_nMaxArgs;

  /**
   * the name of the argument for this option. Makes only sense, if minArgs &gt;
   * 0
   */
  private final String m_sArgName;

  /**
   * Multiplicity to use.
   */
  private final EOptionMultiplicity m_eMultiplicity;

  /**
   * the character that is the value separator. Makes only sense if minArgs &gt;
   * 0
   */
  private final char m_cValueSep;

  /**
   * Private constructor used by the nested Builder class.
   *
   * @param aBuilder
   *        builder used to create this option
   */
  protected Option (@Nonnull final Builder aBuilder)
  {
    ValueEnforcer.notNull (aBuilder, "Builder");
    m_sShortOpt = aBuilder.m_sShortOpt;
    m_sLongOpt = aBuilder.m_sLongOpt;
    m_sDescription = aBuilder.m_sDescription;
    m_nMinArgs = aBuilder.m_nMinArgs;
    m_nMaxArgs = aBuilder.m_nMaxArgs;
    m_sArgName = aBuilder.m_sArgName;
    m_eMultiplicity = aBuilder.m_eMultiplicity;
    m_cValueSep = aBuilder.m_cValueSep;
  }

  /**
   * @return the 'unique' internal Option identifier. Either short or long
   *         option name.
   * @see #getShortOpt()
   * @see #getLongOpt()
   */
  @Nonnull
  @Nonempty
  public final String getKey ()
  {
    // if 'opt' is null, then it is a 'long' option
    return hasShortOpt () ? m_sShortOpt : m_sLongOpt;
  }

  /**
   * Retrieve the name of this Option.
   *
   * @return The name of this option. May be <code>null</code> if this instance
   *         only has a "long option".
   * @see #hasShortOpt()
   * @see #getLongOpt()
   */
  @Nullable
  public String getShortOpt ()
  {
    return m_sShortOpt;
  }

  public boolean hasShortOpt ()
  {
    return StringHelper.hasText (m_sShortOpt);
  }

  public boolean hasShortOpt (@Nullable final String sShortOpt)
  {
    return sShortOpt != null && sShortOpt.equals (m_sShortOpt);
  }

  /**
   * Retrieve the long name of this Option.
   *
   * @return Long name of this option, or <code>null</code> if there is no long
   *         name.
   * @see #hasLongOpt()
   * @see #getShortOpt()
   */
  @Nullable
  public String getLongOpt ()
  {
    return m_sLongOpt;
  }

  /**
   * Query to see if this Option has a long name
   *
   * @return boolean flag indicating existence of a long name
   */
  public boolean hasLongOpt ()
  {
    return m_sLongOpt != null;
  }

  public boolean hasLongOpt (@Nullable final String sLongOpt)
  {
    return sLongOpt != null && sLongOpt.equals (m_sLongOpt);
  }

  public boolean matches (@Nullable final String sOptName)
  {
    return sOptName != null && (sOptName.equals (m_sShortOpt) || sOptName.equals (m_sLongOpt));
  }

  /**
   * Retrieve the self-documenting description of this Option
   *
   * @return The string description of this option
   */
  @Nullable
  public String getDescription ()
  {
    return m_sDescription;
  }

  /**
   * @return <code>true</code> if a description is present, <code>false</code>
   *         if not.
   */
  public boolean hasDescription ()
  {
    return StringHelper.hasText (m_sDescription);
  }

  /**
   * @return Minimum number of (required) arguments. Always &ge; 0.
   */
  @Nonnegative
  public int getMinArgCount ()
  {
    return m_nMinArgs;
  }

  public boolean hasMinArgs ()
  {
    return m_nMinArgs != 0;
  }

  /**
   * @return Maximum number of arguments. Is always &ge; 0 or
   *         {@link #INFINITE_VALUES} for unlimited arguments.
   */
  @Nonnegative
  public int getMaxArgCount ()
  {
    return m_nMaxArgs;
  }

  public boolean hasInfiniteArgs ()
  {
    return m_nMaxArgs == INFINITE_VALUES;
  }

  public boolean canHaveMoreValues (final int nSize)
  {
    return hasInfiniteArgs () || nSize < m_nMaxArgs;
  }

  public boolean canHaveArgs ()
  {
    return hasInfiniteArgs () || m_nMaxArgs > 0;
  }

  /**
   * Gets the display name for the argument value.
   *
   * @return the display name for the argument value.
   */
  @Nullable
  public String getArgName ()
  {
    return m_sArgName;
  }

  /**
   * @return <code>true</code> if this option can appear multiple times on a
   *         commandline, <code>false</code> if it can occur at maximum once.
   */
  public boolean isRepeatable ()
  {
    return m_eMultiplicity.isRepeatable ();
  }

  /**
   * Query to see if this {@link Option} is mandatory
   *
   * @return boolean flag indicating whether this Option is mandatory
   */
  public boolean isRequired ()
  {
    return m_eMultiplicity.isRequired ();
  }

  /**
   * Returns whether the display name for the argument value has been set.
   *
   * @return if the display name for the argument value has been set.
   */
  public boolean hasArgName ()
  {
    return StringHelper.hasText (m_sArgName);
  }

  /**
   * Returns the value separator character.
   *
   * @return the value separator character.
   */
  public char getValueSeparator ()
  {
    return m_cValueSep;
  }

  /**
   * Return whether this Option has specified a value separator.
   *
   * @return whether this Option has specified a value separator.
   */
  public boolean hasValueSeparator ()
  {
    return m_cValueSep != DEFAULT_VALUE_SEPARATOR;
  }

  @Override
  public boolean equals (final Object o)
  {
    if (this == o)
      return true;
    if (o == null || getClass () != o.getClass ())
      return false;

    final Option rhs = (Option) o;
    return EqualsHelper.equals (m_sShortOpt, rhs.m_sShortOpt) && EqualsHelper.equals (m_sLongOpt, rhs.m_sLongOpt);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_sShortOpt).append (m_sLongOpt).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).appendIfNotNull ("Opt", m_sShortOpt)
                                       .appendIfNotNull ("LongOpt", m_sLongOpt)
                                       .appendIfNotNull ("Description", m_sDescription)
                                       .appendIf ("MinArgs", m_nMinArgs, this::hasMinArgs)
                                       .appendIf ("MaxArgs",
                                                  m_nMaxArgs == INFINITE_VALUES ? "infinite"
                                                                                : Integer.toString (m_nMaxArgs),
                                                  this::canHaveArgs)
                                       .appendIfNotNull ("ArgName", m_sArgName)
                                       .append ("Multiplicity", m_eMultiplicity)
                                       .appendIf ("ValueSep", m_cValueSep, this::hasValueSeparator)
                                       .getToString ();
  }

  /**
   * Returns a {@link Builder} to create an {@link Option} using descriptive
   * methods.
   *
   * @param sShortOpt
   *        short representation of the option
   * @return a new {@link Builder} instance
   * @throws IllegalArgumentException
   *         if there are any non valid Option characters in {@code opt}
   */
  @Nonnull
  public static Builder builder (@Nullable final String sShortOpt)
  {
    return new Builder (sShortOpt);
  }

  /**
   * A nested builder class to create <code>Option</code> instances using
   * descriptive methods.
   * <p>
   * Example usage:
   *
   * <pre>
   * Option option = Option.builder ("a").required (true).longOpt ("arg-name").build ();
   * </pre>
   */
  public static class Builder implements Serializable
  {
    /** the name of the option */
    private final String m_sShortOpt;

    /** the long representation of the option */
    private String m_sLongOpt;

    /** description of the option */
    private String m_sDescription;

    /** The minimum argument count. Must be &le; maxArgs */
    private int m_nMinArgs = 0;

    /** The maximum argument count. Must be &ge; minArgs */
    private int m_nMaxArgs = 0;

    /**
     * the name of the argument for this option. Makes only sense, if minArgs
     * &gt; 0
     */
    private String m_sArgName;

    /**
     * specifies whether this option is required to be present. Makes only sense
     * if minArgs &gt; 0
     */
    private EOptionMultiplicity m_eMultiplicity = EOptionMultiplicity.OPTIONAL_ONCE;

    /**
     * the character that is the value separator in case the value. Makes only
     * sense if minArgs &gt; 0
     */
    private char m_cValueSep = DEFAULT_VALUE_SEPARATOR;

    /**
     * Constructs a new <code>Builder</code> with the minimum required
     * parameters for an <code>Option</code> instance.
     *
     * @param sShortOpt
     *        short representation of the option
     * @throws IllegalArgumentException
     *         if there are any non valid Option characters in {@code opt}
     */
    protected Builder (@Nullable final String sShortOpt)
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
    public Builder longOpt (@Nullable final String sLongOpt)
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
    public Builder desc (@Nullable final String sDescription)
    {
      m_sDescription = sDescription;
      return this;
    }

    /**
     * Set the minimum number of arguments that must be present. By default it
     * is 0. This is the number of required arguments. If the option is
     * repeatable, this value is per occurrence.
     *
     * @param nMinArgs
     *        Number of minimum arguments. Must be &ge; 0.
     * @return this for chaining
     */
    @Nonnull
    public Builder minArgs (@Nonnegative final int nMinArgs)
    {
      ValueEnforcer.isGE0 (nMinArgs, "MinArgs");
      m_nMinArgs = nMinArgs;
      return this;
    }

    /**
     * Set the maximum number of arguments that can be present. By default it is
     * 0. The difference between minimum and maximum arguments are the optional
     * arguments. If the option is repeatable, this value is per occurrence.
     *
     * @param nMaxArgs
     *        Number of maximum arguments. Must be &ge; 0 or
     *        {@link #INFINITE_VALUES}
     * @return this for chaining
     * @see #maxArgsInfinite()
     */
    @Nonnull
    public Builder maxArgs (final int nMaxArgs)
    {
      ValueEnforcer.isTrue (nMaxArgs == INFINITE_VALUES || nMaxArgs >= 0,
                            () -> "MaxArgs must be " + INFINITE_VALUES + " or >= 0!");
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
    public Builder maxArgsInfinite ()
    {
      return maxArgs (INFINITE_VALUES);
    }

    /**
     * Shortcut for setting minArgs and maxArgs at once. If the option is
     * repeatable, this value is per occurrence.
     *
     * @param nMinArgs
     *        Number of minimum arguments. Must be &ge; 0.
     * @param nMaxArgs
     *        Number of maximum arguments. Must be &ge; 0 or
     *        {@link #INFINITE_VALUES}
     * @return this for chaining
     * @see #minArgs(int)
     * @see #maxArgs(int)
     * @see #args(int)
     */
    @Nonnull
    public Builder args (@Nonnegative final int nMinArgs, final int nMaxArgs)
    {
      return minArgs (nMinArgs).maxArgs (nMaxArgs);
    }

    /**
     * Shortcut for setting minArgs and maxArgs to the same value, making this
     * the number of required arguments. If the option is repeatable, this value
     * is per occurrence.
     *
     * @param nArgs
     *        Number of arguments. Must be &ge; 0.
     * @return this for chaining
     * @see #minArgs(int)
     * @see #maxArgs(int)
     * @see #args(int, int)
     */
    @Nonnull
    public Builder args (@Nonnegative final int nArgs)
    {
      return minArgs (nArgs).maxArgs (nArgs);
    }

    /**
     * Sets the display name for the argument value. May only be used if at
     * least one argument is present.
     *
     * @param sArgName
     *        the display name for the argument value.
     * @return this builder, to allow method chaining
     */
    @Nonnull
    public Builder argName (@Nullable final String sArgName)
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
    public Builder required (final boolean bRequired)
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
     *        <code>true</code> if this option can be repeated,
     *        <code>false</code> if not.
     * @return this builder, to allow method chaining
     */
    @Nonnull
    public Builder repeatable (final boolean bRepeatable)
    {
      if (bRepeatable)
        m_eMultiplicity = m_eMultiplicity.getAsRepeatable ();
      else
        m_eMultiplicity = m_eMultiplicity.getAsOnce ();
      return this;
    }

    /**
     * The Option will use <code>sep</code> as a means to separate argument
     * values.
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
    public Builder valueSeparator (final char cValueSep)
    {
      m_cValueSep = cValueSep;
      return this;
    }

    /**
     * Constructs an Option with the values declared by this {@link Builder}.
     *
     * @return the new {@link Option}
     * @throws IllegalArgumentException
     *         if neither {@code opt} or {@code longOpt} has been set
     */
    @Nonnull
    @ReturnsMutableCopy
    public Option build ()
    {
      if (StringHelper.hasNoText (m_sShortOpt) && StringHelper.hasNoText (m_sLongOpt))
        throw new IllegalStateException ("Either opt or longOpt must be specified");
      if (m_nMaxArgs != INFINITE_VALUES && m_nMaxArgs < m_nMinArgs)
        throw new IllegalStateException ("MinArgs (" + m_nMinArgs + ") must be <= MaxArgs (" + m_nMaxArgs + ")");
      if (m_nMinArgs == 0 && m_nMaxArgs == 0)
      {
        if (m_sArgName != null)
          throw new IllegalStateException ("ArgName may only be provided if at least one argument is present");
        if (m_eMultiplicity.isRequired ())
          s_aLogger.warn ("Having a required option without an argument may not be what is desired.");
        if (m_eMultiplicity.isRepeatable ())
          s_aLogger.warn ("Having a repeatable option without an argument may not be what is desired.");
        if (m_cValueSep != DEFAULT_VALUE_SEPARATOR)
          throw new IllegalStateException ("ValueSeparator may only be provided if at least one argument is present");
      }

      return new Option (this);
    }
  }
}
