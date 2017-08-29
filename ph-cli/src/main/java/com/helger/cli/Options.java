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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.MustImplementEqualsAndHashcode;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.CommonsLinkedHashMap;
import com.helger.commons.collection.impl.CommonsLinkedHashSet;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.collection.impl.ICommonsOrderedMap;
import com.helger.commons.collection.impl.ICommonsOrderedSet;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.string.ToStringGenerator;

/**
 * Main entry-point into the library.
 * <p>
 * Options represents a collection of {@link Option} objects, which describe the
 * possible options for a command-line.
 * <p>
 * It may flexibly parse long and short options, with or without values.
 * Additionally, it may parse only a portion of a commandline, allowing for
 * flexible multi-stage parsing.
 *
 * @see com.helger.cli.CommandLine
 */
public class Options implements Serializable
{
  @MustImplementEqualsAndHashcode
  public static interface IRequiredOption extends Serializable
  {
    // Marker interface
  }

  public static final class RequiredArg implements IRequiredOption
  {
    private final String m_sName;

    RequiredArg (@Nonnull @Nonempty final String sName)
    {
      m_sName = sName;
    }

    @Nonnull
    @Nonempty
    public String getName ()
    {
      return m_sName;
    }

    @Override
    public boolean equals (final Object o)
    {
      if (o == this)
        return true;
      if (o == null || !getClass ().equals (o.getClass ()))
        return false;
      final RequiredArg rhs = (RequiredArg) o;
      return m_sName.equals (rhs.m_sName);
    }

    @Override
    public int hashCode ()
    {
      return new HashCodeGenerator (this).append (m_sName).getHashCode ();
    }

    @Override
    public String toString ()
    {
      return new ToStringGenerator (this).append ("Name", m_sName).getToString ();
    }
  }

  public static final class RequiredGroup implements IRequiredOption
  {
    private final OptionGroup m_aGroup;

    RequiredGroup (@Nonnull final OptionGroup aGroup)
    {
      m_aGroup = aGroup;
    }

    @Nonnull
    public OptionGroup getGroup ()
    {
      return m_aGroup;
    }

    @Override
    public boolean equals (final Object o)
    {
      if (o == this)
        return true;
      if (o == null || !getClass ().equals (o.getClass ()))
        return false;
      final RequiredGroup rhs = (RequiredGroup) o;
      return m_aGroup.equals (rhs.m_aGroup);
    }

    @Override
    public int hashCode ()
    {
      return new HashCodeGenerator (this).append (m_aGroup).getHashCode ();
    }

    @Override
    public String toString ()
    {
      return new ToStringGenerator (this).append ("Group", m_aGroup).getToString ();
    }
  }

  /** a map of the options with the character key */
  private final ICommonsOrderedMap <String, Option> m_aShortOpts = new CommonsLinkedHashMap <> ();

  /** a map of the options with the long key */
  private final ICommonsOrderedMap <String, Option> m_aLongOpts = new CommonsLinkedHashMap <> ();

  /** a map of the required options */
  private final ICommonsList <IRequiredOption> m_aRequiredOpts = new CommonsArrayList <> ();

  /** a map of the option groups */
  private final ICommonsOrderedMap <String, OptionGroup> m_aOptionGroups = new CommonsLinkedHashMap <> ();

  /**
   * Add the specified option group.
   *
   * @param aGroup
   *        the OptionGroup that is to be added
   * @return this for chaining
   */
  @Nonnull
  public Options addOptionGroup (@Nonnull final OptionGroup aGroup)
  {
    ValueEnforcer.notNull (aGroup, "group");

    if (aGroup.isRequired ())
    {
      // Ensure order is correct
      final RequiredGroup aKey = new RequiredGroup (aGroup);
      m_aRequiredOpts.remove (aKey);
      m_aRequiredOpts.add (aKey);
    }

    for (final Option aOption : aGroup.getAllOptions ())
    {
      // an Option cannot be required if it is in an
      // OptionGroup, either the group is required or
      // nothing is required
      aOption.setRequired (false);
      addOption (aOption);
      m_aOptionGroups.put (aOption.getKey (), aGroup);
    }

    return this;
  }

  /**
   * Lists the OptionGroups that are members of this Options instance.
   *
   * @return a Collection of OptionGroup instances.
   */
  @Nonnull
  @ReturnsMutableCopy
  ICommonsOrderedSet <OptionGroup> getAllOptionGroups ()
  {
    return new CommonsLinkedHashSet <> (m_aOptionGroups.values ());
  }

  /**
   * Adds an option instance
   *
   * @param aOpt
   *        the option that is to be added
   * @return this for chaining
   */
  @Nonnull
  public Options addOption (@Nonnull final Option aOpt)
  {
    ValueEnforcer.notNull (aOpt, "opt");

    final String sKey = aOpt.getKey ();
    m_aShortOpts.put (sKey, aOpt);

    // add it to the long option list
    if (aOpt.hasLongOpt ())
    {
      m_aLongOpts.put (aOpt.getLongOpt (), aOpt);
    }

    // if the option is required add it to the required list
    if (aOpt.isRequired ())
    {
      // Ensure order is correct
      final RequiredArg aKey = new RequiredArg (sKey);
      m_aRequiredOpts.remove (aKey);
      m_aRequiredOpts.add (aKey);
    }

    return this;
  }

  /**
   * Retrieve a read-only list of options in this set
   *
   * @return read-only Collection of {@link Option} objects in this descriptor
   */
  @Nonnull
  @ReturnsMutableCopy
  public ICommonsList <Option> getAllOptions ()
  {
    return m_aShortOpts.copyOfValues ();
  }

  /**
   * Returns the required options.
   *
   * @return read-only List of required options
   */
  @Nonnull
  @ReturnsMutableCopy
  public ICommonsList <IRequiredOption> getAllRequiredOptions ()
  {
    return m_aRequiredOpts.getClone ();
  }

  /**
   * Retrieve the {@link Option} matching the long or short name specified.
   * <p>
   * The leading hyphens in the name are ignored (up to 2).
   * </p>
   *
   * @param sOpt
   *        short or long name of the {@link Option}
   * @return the option represented by opt
   */
  @Nullable
  public Option getOption (@Nullable final String sOpt)
  {
    final String sRealOpt = Util.stripLeadingHyphens (sOpt);

    Option ret = m_aShortOpts.get (sRealOpt);
    if (ret == null)
      ret = m_aLongOpts.get (sRealOpt);
    return ret;
  }

  /**
   * Returns the options with a long name starting with the name specified.
   *
   * @param sOpt
   *        the partial name of the option
   * @return the options matching the partial name specified, or an empty list
   *         if none matches
   * @since 1.3
   */
  @Nonnull
  @ReturnsMutableCopy
  public ICommonsList <String> getAllMatchingOptions (@Nullable final String sOpt)
  {
    final String sRealOpt = Util.stripLeadingHyphens (sOpt);

    if (m_aLongOpts.containsKey (sRealOpt))
    {
      // for a perfect match return the single option only
      return new CommonsArrayList <> (sRealOpt);
    }

    return CollectionHelper.newList (m_aLongOpts.keySet (), x -> x.startsWith (sRealOpt));
  }

  /**
   * Returns whether the named {@link Option} is a member of this
   * {@link Options}.
   *
   * @param sOpt
   *        short or long name of the {@link Option}
   * @return true if the named {@link Option} is a member of this
   *         {@link Options}
   */
  public boolean hasOption (@Nullable final String sOpt)
  {
    final String sRealOpt = Util.stripLeadingHyphens (sOpt);

    return m_aShortOpts.containsKey (sRealOpt) || m_aLongOpts.containsKey (sRealOpt);
  }

  /**
   * Returns whether the named {@link Option} is a member of this
   * {@link Options}.
   *
   * @param sOpt
   *        long name of the {@link Option}
   * @return true if the named {@link Option} is a member of this
   *         {@link Options}
   * @since 1.3
   */
  public boolean hasLongOption (@Nullable final String sOpt)
  {
    final String sRealOpt = Util.stripLeadingHyphens (sOpt);

    return m_aLongOpts.containsKey (sRealOpt);
  }

  /**
   * Returns whether the named {@link Option} is a member of this
   * {@link Options}.
   *
   * @param sOpt
   *        short name of the {@link Option}
   * @return true if the named {@link Option} is a member of this
   *         {@link Options}
   * @since 1.3
   */
  public boolean hasShortOption (@Nullable final String sOpt)
  {
    final String sRealOpt = Util.stripLeadingHyphens (sOpt);

    return m_aShortOpts.containsKey (sRealOpt);
  }

  /**
   * Returns the OptionGroup the <code>opt</code> belongs to.
   *
   * @param aOption
   *        the option whose OptionGroup is being queried.
   * @return the OptionGroup if <code>opt</code> is part of an OptionGroup,
   *         otherwise return null
   */
  @Nullable
  public OptionGroup getOptionGroup (@Nonnull final Option aOption)
  {
    ValueEnforcer.notNull (aOption, "Option");
    return m_aOptionGroups.get (aOption.getKey ());
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("ShortOpts", m_aShortOpts)
                                       .append ("LongOpts", m_aLongOpts)
                                       .append ("RequiredOpts", m_aRequiredOpts)
                                       .append ("OptionGroups", m_aOptionGroups)
                                       .getToString ();
  }
}
