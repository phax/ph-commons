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

import java.io.Serializable;
import java.util.Collection;

import javax.annotation.CheckForSigned;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.annotation.ReturnsMutableObject;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.lang.ICloneable;
import com.helger.commons.string.StringHelper;
import com.helger.commons.string.ToStringGenerator;

/**
 * Describes a single command-line option. It maintains information regarding
 * the short-name of the option, the long-name, if any exists, a flag indicating
 * if an argument is required for this option, and a self-documenting
 * description of the option.
 * <p>
 * An Option is not created independently, but is created through an instance of
 * {@link Options}. An Option is required to have at least a short or a
 * long-name.
 * <p>
 * <b>Note:</b> once an {@link Option} has been added to an instance of
 * {@link Options}, it's required flag may not be changed anymore.
 *
 * @see com.helger.cli.Options
 * @see com.helger.cli.CommandLine
 */
public class Option implements ICloneable <Option>, Serializable
{
  public static final Class <?> DEFAULT_TYPE = String.class;
  public static final char DEFAULT_VALUE_SEPARATOR = 0;

  /**
   * constant that specifies the number of argument values has not been
   * specified
   */
  public static final int UNINITIALIZED = -1;

  /** constant that specifies the number of argument values is infinite */
  public static final int UNLIMITED_VALUES = -2;

  /** the name of the option */
  private final String m_sOpt;

  /** the long representation of the option */
  private final String m_sLongOpt;

  /** description of the option */
  private final String m_sDescription;

  /** specifies whether this option is required to be present */
  private boolean m_bRequired;

  /** specifies whether the argument value of this Option is optional */
  private final boolean m_bOptionalArg;

  /** the number of argument values this option can have */
  private int m_nNumberOfArgs = UNINITIALIZED;

  /** the name of the argument for this option */
  private final String m_sArgName;

  /** the character that is the value separator */
  private char m_cValueSep = DEFAULT_VALUE_SEPARATOR;

  /** the list of argument values **/
  private final ICommonsList <String> m_aValues = new CommonsArrayList <> ();

  /**
   * Private constructor used by the nested Builder class.
   *
   * @param aBuilder
   *        builder used to create this option
   */
  protected Option (@Nonnull final Builder aBuilder)
  {
    ValueEnforcer.notNull (aBuilder, "Builder");
    m_sOpt = aBuilder.m_sOpt;
    m_sLongOpt = aBuilder.m_sLongOpt;
    m_sDescription = aBuilder.m_sDescription;
    m_bRequired = aBuilder.m_bRequired;
    m_bOptionalArg = aBuilder.m_bOptionalArg;
    m_nNumberOfArgs = aBuilder.m_nNumberOfArgs;
    m_sArgName = aBuilder.m_sArgName;
    m_cValueSep = aBuilder.m_cValueSep;
    // No values
  }

  /**
   * Constructor for cloning
   *
   * @param aOther
   *        The object to clone from.
   */
  protected Option (@Nonnull final Option aOther)
  {
    ValueEnforcer.notNull (aOther, "Other");
    m_sOpt = aOther.m_sOpt;
    m_sLongOpt = aOther.m_sLongOpt;
    m_sDescription = aOther.m_sDescription;
    m_bRequired = aOther.m_bRequired;
    m_bOptionalArg = aOther.m_bOptionalArg;
    m_nNumberOfArgs = aOther.m_nNumberOfArgs;
    m_sArgName = aOther.m_sArgName;
    m_cValueSep = aOther.m_cValueSep;
    m_aValues.addAll (aOther.m_aValues);
  }

  void setRequired (final boolean bRequired)
  {
    m_bRequired = bRequired;
  }

  /**
   * @return the 'unique' internal Option identifier. Either short or long
   *         option name.
   * @see #getOpt()
   * @see #getLongOpt()
   */
  @Nonnull
  public final String getKey ()
  {
    // if 'opt' is null, then it is a 'long' option
    return hasOpt () ? m_sOpt : m_sLongOpt;
  }

  /**
   * Retrieve the name of this Option. It is this String which can be used with
   * {@link CommandLine#hasOption(String opt)} and
   * {@link CommandLine#getOptionValue(String opt)} to check for existence and
   * argument.
   *
   * @return The name of this option. May be <code>null</code> if this instance
   *         only has a "long option".
   * @see #hasOpt()
   * @see #getLongOpt()
   */
  @Nullable
  public String getOpt ()
  {
    return m_sOpt;
  }

  public boolean hasOpt ()
  {
    return StringHelper.hasText (m_sOpt);
  }

  public boolean hasOpt (@Nullable final String sOpt)
  {
    return sOpt != null && sOpt.equals (m_sOpt);
  }

  /**
   * Retrieve the long name of this Option.
   *
   * @return Long name of this option, or <code>null</code> if there is no long
   *         name.
   * @see #hasLongOpt()
   * @see #getOpt()
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
   * Query to see if this {@link Option} is mandatory
   *
   * @return boolean flag indicating whether this Option is mandatory
   */
  public boolean isRequired ()
  {
    return m_bRequired;
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
   * @since 1.1
   */
  public boolean hasValueSeparator ()
  {
    return m_cValueSep > 0;
  }

  /**
   * Returns the number of argument values this Option can take.
   * <p>
   * A value equal to the constant {@link #UNINITIALIZED} (= -1) indicates the
   * number of arguments has not been specified. A value equal to the constant
   * {@link #UNLIMITED_VALUES} (= -2) indicates that this options takes an
   * unlimited amount of values.
   * </p>
   *
   * @return num the number of argument values
   * @see #UNINITIALIZED
   * @see #UNLIMITED_VALUES
   */
  @CheckForSigned
  public int getNumberOfArgs ()
  {
    return m_nNumberOfArgs;
  }

  public boolean hasUnlimitedNumberOfArgs ()
  {
    return getNumberOfArgs () == UNLIMITED_VALUES;
  }

  /**
   * @return whether this Option can have an optional argument
   */
  public boolean hasOptionalArg ()
  {
    return m_bOptionalArg;
  }

  /**
   * Query to see if this Option requires an argument
   *
   * @return boolean flag indicating if an argument is required
   */
  public boolean hasArg ()
  {
    return m_nNumberOfArgs > 0 || m_nNumberOfArgs == UNLIMITED_VALUES;
  }

  /**
   * Query to see if this Option can take many values.
   *
   * @return boolean flag indicating if multiple values are allowed
   */
  public boolean hasArgs ()
  {
    return m_nNumberOfArgs > 1 || m_nNumberOfArgs == UNLIMITED_VALUES;
  }

  /**
   * Adds the specified value to this Option.
   *
   * @param sValue
   *        is a/the value of this Option
   */
  void addValueForProcessing (final String sValue)
  {
    if (m_nNumberOfArgs == UNINITIALIZED)
      throw new IllegalStateException ("NO_ARGS_ALLOWED");

    _processValue (sValue);
  }

  /**
   * Processes the value. If this Option has a value separator the value will
   * have to be parsed into individual tokens. When n-1 tokens have been
   * processed and there are more value separators in the value, parsing is
   * ceased and the remaining characters are added as a single token.
   *
   * @param value
   *        The String to be processed.
   * @since 1.0.1
   */
  private void _processValue (@Nonnull final String sValue)
  {
    String sRest = sValue;

    // this Option has a separator character
    if (hasValueSeparator ())
    {
      // get the separator character
      final char cSep = getValueSeparator ();

      // store the index for the value separator
      int nIndex = sRest.indexOf (cSep);

      // while there are more value separators
      while (nIndex != -1)
      {
        // next value to be added
        if (m_aValues.size () == m_nNumberOfArgs - 1)
        {
          break;
        }

        // store
        _addValue (sRest.substring (0, nIndex));

        // parse
        sRest = sRest.substring (nIndex + 1);

        // get new index
        nIndex = sRest.indexOf (cSep);
      }
    }

    // store the actual value or the last value that has been parsed
    _addValue (sRest);
  }

  /**
   * Add the value to this Option. If the number of arguments is greater than
   * zero and there is enough space in the list then add the value. Otherwise,
   * throw a runtime exception.
   *
   * @param sValue
   *        The value to be added to this Option
   * @since 1.0.1
   */
  private void _addValue (final String sValue)
  {
    if (!acceptsArg ())
      throw new IllegalStateException ("Cannot add value, list full.");

    // store value
    m_aValues.add (sValue);
  }

  /**
   * Returns the specified value of this Option or <code>null</code> if there is
   * no value.
   *
   * @return the value/first value of this Option or <code>null</code> if there
   *         is no value.
   */
  @Nullable
  public String getValue ()
  {
    return m_aValues.getFirst ();
  }

  /**
   * Returns the specified value of this Option or <code>null</code> if there is
   * no value.
   *
   * @param nIndex
   *        The index of the value to be returned.
   * @return the specified value of this Option or <code>null</code> if there is
   *         no value.
   */
  @Nullable
  public String getValue (final int nIndex)
  {
    return m_aValues.getAtIndex (nIndex, null);
  }

  /**
   * Return all values of this Option.
   *
   * @return the values of this Option. Never <code>null</code> but maybe empty.
   */
  @Nonnull
  @ReturnsMutableCopy
  public ICommonsList <String> getAllValues ()
  {
    return m_aValues.getClone ();
  }

  @Nonnull
  @ReturnsMutableObject
  public ICommonsList <String> values ()
  {
    return m_aValues;
  }

  void addAllValuesTo (@Nonnull final Collection <? super String> aTarget)
  {
    ValueEnforcer.notNull (aTarget, "Target");
    aTarget.addAll (m_aValues);
  }

  /**
   * Clear the Option values. After a parse is complete, these are left with
   * data in them and they need clearing if another parse is done. See:
   * <a href="https://issues.apache.org/jira/browse/CLI-71">CLI-71</a>
   */
  void clearValues ()
  {
    m_aValues.clear ();
  }

  /**
   * Tells if the option can accept more arguments.
   *
   * @return false if the maximum number of arguments is reached
   * @since 1.3
   */
  boolean acceptsArg ()
  {
    if (m_nNumberOfArgs == UNLIMITED_VALUES)
      return true;
    return m_nNumberOfArgs > 0 && m_aValues.size () < m_nNumberOfArgs;
  }

  /**
   * Tells if the option requires more arguments to be valid.
   *
   * @return false if the option doesn't require more arguments
   * @since 1.3
   */
  boolean requiresArg ()
  {
    if (m_bOptionalArg)
      return false;
    if (m_nNumberOfArgs == UNLIMITED_VALUES)
      return m_aValues.isEmpty ();
    return acceptsArg ();
  }

  @Nonnull
  @ReturnsMutableCopy
  public Option getClone ()
  {
    return new Option (this);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (this == o)
      return true;
    if (o == null || getClass () != o.getClass ())
      return false;

    final Option rhs = (Option) o;
    return EqualsHelper.equals (m_sOpt, rhs.m_sOpt) && EqualsHelper.equals (m_sLongOpt, rhs.m_sLongOpt);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_sOpt).append (m_sLongOpt).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("Opt", m_sOpt)
                                       .append ("LongOpt", m_sLongOpt)
                                       .append ("Description", m_sDescription)
                                       .append ("Required", m_bRequired)
                                       .append ("OptionalArg", m_bOptionalArg)
                                       .append ("NumberOfArgs", m_nNumberOfArgs)
                                       .append ("ArgName", m_sArgName)
                                       .append ("ValueSep", m_cValueSep)
                                       .append ("Values", m_aValues)
                                       .getToString ();
  }

  /**
   * Returns a {@link Builder} to create an {@link Option} using descriptive
   * methods.
   *
   * @return a new {@link Builder} instance
   * @since 1.3
   */
  @Nonnull
  public static Builder builder ()
  {
    return builder (null);
  }

  /**
   * Returns a {@link Builder} to create an {@link Option} using descriptive
   * methods.
   *
   * @param cOpt
   *        short representation of the option
   * @return a new {@link Builder} instance
   * @throws IllegalArgumentException
   *         if there are any non valid Option characters in {@code opt}
   */
  @Nonnull
  public static Builder builder (final char cOpt)
  {
    return builder (Character.toString (cOpt));
  }

  /**
   * Returns a {@link Builder} to create an {@link Option} using descriptive
   * methods.
   *
   * @param sOpt
   *        short representation of the option
   * @return a new {@link Builder} instance
   * @throws IllegalArgumentException
   *         if there are any non valid Option characters in {@code opt}
   * @since 1.3
   */
  @Nonnull
  public static Builder builder (@Nullable final String sOpt)
  {
    return new Builder (sOpt);
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
   *
   * @since 1.3
   */
  public static class Builder implements Serializable
  {
    /** the name of the option */
    private final String m_sOpt;

    /** the long representation of the option */
    private String m_sLongOpt;

    /** description of the option */
    private String m_sDescription;

    /** specifies whether this option is required to be present */
    private boolean m_bRequired;

    /** specifies whether the argument value of this Option is optional */
    private boolean m_bOptionalArg;

    /** the number of argument values this option can have */
    private int m_nNumberOfArgs = UNINITIALIZED;

    /** the name of the argument for this option */
    private String m_sArgName;

    /** the character that is the value separator */
    private char m_cValueSep = DEFAULT_VALUE_SEPARATOR;

    /**
     * Constructs a new <code>Builder</code> with the minimum required
     * parameters for an <code>Option</code> instance.
     *
     * @param sOpt
     *        short representation of the option
     * @throws IllegalArgumentException
     *         if there are any non valid Option characters in {@code opt}
     */
    protected Builder (@Nullable final String sOpt) throws IllegalArgumentException
    {
      if (sOpt != null)
        OptionValidator.validateShortOption (sOpt);
      m_sOpt = sOpt;
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
     * Marks this Option as required.
     *
     * @return this builder, to allow method chaining
     */
    @Nonnull
    public Builder required ()
    {
      return required (true);
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
      m_bRequired = bRequired;
      return this;
    }

    /**
     * Sets the display name for the argument value.
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

    @Nonnull
    public Builder oneOptionalArg ()
    {
      return optionalNumberOfArgs (1);
    }

    @Nonnull
    public Builder unlimitedOptionalArgs ()
    {
      return optionalNumberOfArgs (UNLIMITED_VALUES);
    }

    @Nonnull
    public Builder optionalNumberOfArgs (final int nNumberOfArgs)
    {
      m_bOptionalArg = true;
      m_nNumberOfArgs = nNumberOfArgs;
      return this;
    }

    /**
     * The Option will use '=' as a means to separate argument value.
     *
     * @return this builder, to allow method chaining
     */
    @Nonnull
    public Builder valueSeparator ()
    {
      return valueSeparator ('=');
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
     * @param sep
     *        The value separator.
     * @return this builder, to allow method chaining
     */
    @Nonnull
    public Builder valueSeparator (final char sep)
    {
      m_cValueSep = sep;
      return this;
    }

    /**
     * Indicates that the Option will require an argument.
     *
     * @return this builder, to allow method chaining
     */
    @Nonnull
    public Builder oneArg ()
    {
      return numberOfArgs (1);
    }

    /**
     * Indicates that the Option can have unlimited argument values.
     *
     * @return this builder, to allow method chaining
     */
    @Nonnull
    public Builder unlimitedArgs ()
    {
      return numberOfArgs (Option.UNLIMITED_VALUES);
    }

    /**
     * Sets the number of argument values the Option can take.
     *
     * @param nNumberOfArgs
     *        the number of argument values
     * @return this builder, to allow method chaining
     */
    @Nonnull
    public Builder numberOfArgs (final int nNumberOfArgs)
    {
      m_bOptionalArg = false;
      m_nNumberOfArgs = nNumberOfArgs;
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
      if (m_sOpt == null && m_sLongOpt == null)
        throw new IllegalArgumentException ("Either opt or longOpt must be specified");
      return new Option (this);
    }
  }
}
