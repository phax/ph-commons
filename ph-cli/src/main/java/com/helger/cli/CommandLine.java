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

import java.util.Iterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsIterable;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.lang.NonBlockingProperties;
import com.helger.commons.string.StringHelper;
import com.helger.commons.traits.IGetterByKeyTrait;

/**
 * Represents list of arguments parsed against a {@link Options} descriptor.
 * <p>
 * It allows querying of a boolean {@link #hasOption(String opt)}, in addition
 * to retrieving the {@link #getOptionValue(String opt)} for options requiring
 * arguments.
 * <p>
 * Additionally, any left-over or unrecognized arguments, are available for
 * further processing.
 */
public class CommandLine implements ICommonsIterable <Option>
{
  /** the unrecognized options/arguments */
  private final ICommonsList <String> m_aArgs = new CommonsArrayList <> ();

  /** the processed options */
  private final ICommonsList <Option> m_aOptions = new CommonsArrayList <> ();

  /**
   * Creates a command line.
   */
  protected CommandLine ()
  {}

  /**
   * Query to see if an option has been set.
   *
   * @param sOpt
   *        Short name of the option
   * @return <code>true</code> if set, <code>false</code> if not
   */
  public boolean hasOption (@Nullable final String sOpt)
  {
    return m_aOptions.contains (_resolveOption (sOpt));
  }

  /**
   * Query to see if an option has been set.
   *
   * @param cOpt
   *        character name of the option
   * @return <code>true</code> if set, <code>false</code> if not
   */
  public boolean hasOption (final char cOpt)
  {
    return hasOption (Character.toString (cOpt));
  }

  /**
   * @return The values to be retrieved in different data types. Never
   *         <code>null</code>.
   */
  @Nonnull
  public IGetterByKeyTrait <String> values ()
  {
    return this::getOptionValue;
  }

  /**
   * Retrieves the array of values, if any, of an option.
   *
   * @param sOpt
   *        string name of the option
   * @return Values of the argument if option is set, and has an argument,
   *         otherwise null.
   */
  @Nonnull
  @ReturnsMutableCopy
  public ICommonsList <String> getAllOptionValues (@Nullable final String sOpt)
  {
    final ICommonsList <String> ret = new CommonsArrayList <> ();
    if (StringHelper.hasText (sOpt))
      for (final Option aOption : m_aOptions)
        if (sOpt.equals (aOption.getOpt ()) || sOpt.equals (aOption.getLongOpt ()))
          aOption.addAllValuesTo (ret);
    return ret;
  }

  /**
   * Retrieves the option object given the long or short option as a String
   *
   * @param sOpt
   *        short or long name of the option
   * @return Canonicalized option
   */
  @Nullable
  private Option _resolveOption (@Nullable final String sOpt)
  {
    final String sRealOpt = Util.stripLeadingHyphens (sOpt);
    if (sRealOpt != null)
      for (final Option sOption : m_aOptions)
      {
        if (sRealOpt.equals (sOption.getOpt ()))
          return sOption;

        if (sRealOpt.equals (sOption.getLongOpt ()))
          return sOption;
      }
    return null;
  }

  /**
   * Retrieves the array of values, if any, of an option.
   *
   * @param cOpt
   *        character name of the option
   * @return Values of the argument if option is set, and has an argument,
   *         otherwise null.
   */
  @Nonnull
  @ReturnsMutableCopy
  public ICommonsList <String> getAllOptionValues (final char cOpt)
  {
    return getAllOptionValues (Character.toString (cOpt));
  }

  /**
   * Retrieve the first argument, if any, of this option.
   *
   * @param sOpt
   *        the name of the option
   * @return Value of the argument if option is set, and has an argument,
   *         otherwise <code>null</code>.
   */
  @Nullable
  public String getOptionValue (@Nullable final String sOpt)
  {
    return getOptionValue (sOpt, null);
  }

  /**
   * Retrieve the first argument, if any, of this option.
   *
   * @param cOpt
   *        the character name of the option
   * @return Value of the argument if option is set, and has an argument,
   *         otherwise <code>null</code>.
   */
  @Nullable
  public String getOptionValue (final char cOpt)
  {
    return getOptionValue (cOpt, null);
  }

  /**
   * Retrieve the first argument, if any, of an option.
   *
   * @param sOpt
   *        name of the option
   * @param sDefaultValue
   *        is the default value to be returned if the option is not specified
   * @return Value of the argument if option is set, and has an argument,
   *         otherwise <code>defaultValue</code>.
   */
  @Nullable
  public String getOptionValue (@Nullable final String sOpt, @Nullable final String sDefaultValue)
  {
    return getAllOptionValues (sOpt).getFirst (sDefaultValue);
  }

  /**
   * Retrieve the argument, if any, of an option.
   *
   * @param cOpt
   *        character name of the option
   * @param sDefaultValue
   *        is the default value to be returned if the option is not specified
   * @return Value of the argument if option is set, and has an argument,
   *         otherwise <code>defaultValue</code>.
   */
  @Nullable
  public String getOptionValue (final char cOpt, @Nullable final String sDefaultValue)
  {
    return getOptionValue (Character.toString (cOpt), sDefaultValue);
  }

  /**
   * Retrieve the map of values associated to the option. This is convenient for
   * options specifying Java properties like <tt>-Dparam1=value1
   * -Dparam2=value2</tt>. The first argument of the option is the key, and the
   * 2nd argument is the value. If the option has only one argument
   * (<tt>-Dfoo</tt>) it is considered as a boolean flag and the value is
   * <tt>"true"</tt>.
   *
   * @param sOpt
   *        name of the option
   * @return The Properties mapped by the option, never <tt>null</tt> even if
   *         the option doesn't exists
   * @since 1.2
   */
  @Nonnull
  @ReturnsMutableCopy
  public NonBlockingProperties getOptionProperties (@Nullable final String sOpt)
  {
    final NonBlockingProperties ret = new NonBlockingProperties ();

    if (sOpt != null)
      for (final Option aOption : m_aOptions)
        if (sOpt.equals (aOption.getOpt ()) || sOpt.equals (aOption.getLongOpt ()))
        {
          final ICommonsList <String> values = aOption.getAllValues ();
          if (values.size () >= 2)
          {
            // use the first 2 arguments as the key/value pair
            ret.put (values.get (0), values.get (1));
          }
          else
            if (values.size () == 1)
            {
              // no explicit value, handle it as a boolean
              ret.put (values.get (0), "true");
            }
        }

    return ret;
  }

  /**
   * Retrieve any left-over non-recognized options and arguments
   *
   * @return remaining items passed in but not parsed as a list
   */
  @Nonnull
  @ReturnsMutableCopy
  public ICommonsList <String> getAllArgs ()
  {
    return m_aArgs.getClone ();
  }

  @Nonnull
  @ReturnsMutableCopy
  public String [] getArgs ()
  {
    return m_aArgs.toArray (new String [m_aArgs.size ()]);
  }

  /**
   * Add left-over unrecognized option/argument.
   *
   * @param sArg
   *        the unrecognized option/argument.
   */
  protected void addArg (@Nonnull final String sArg)
  {
    ValueEnforcer.notNull (sArg, "Arg");
    m_aArgs.add (sArg);
  }

  /**
   * Add an option to the command line. The values of the option are stored.
   *
   * @param aOption
   *        the processed option
   */
  protected void addOption (@Nonnull final Option aOption)
  {
    ValueEnforcer.notNull (aOption, "Option");
    m_aOptions.add (aOption);
  }

  /**
   * Returns an iterator over the Option members of CommandLine.
   *
   * @return an <code>Iterator</code> over the processed {@link Option} members
   *         of this {@link CommandLine}
   */
  @Nonnull
  public Iterator <Option> iterator ()
  {
    return m_aOptions.iterator ();
  }

  /**
   * Returns a list of the processed {@link Option}s.
   *
   * @return a list of the processed {@link Option}s.
   */
  @Nonnull
  @ReturnsMutableCopy
  public ICommonsList <Option> getAllOptions ()
  {
    return m_aOptions.getClone ();
  }
}
