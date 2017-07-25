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
import java.util.Iterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.cli.ex.AlreadySelectedException;
import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.MustImplementEqualsAndHashcode;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.impl.CommonsLinkedHashMap;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.collection.impl.ICommonsOrderedMap;
import com.helger.commons.collection.impl.ICommonsSet;
import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.string.ToStringGenerator;

/**
 * A group of mutually exclusive options.
 */
@MustImplementEqualsAndHashcode
public class OptionGroup implements Serializable
{
  /** hold the options */
  private final ICommonsOrderedMap <String, Option> m_aOptions = new CommonsLinkedHashMap <> ();

  /** the name of the selected option */
  private String m_sSelected;

  /** specified whether this group is required */
  private boolean m_bRequired;

  public OptionGroup ()
  {}

  /**
   * Add the specified <code>Option</code> to this group.
   *
   * @param aOption
   *        the option to add to this group
   * @return this option group with the option added
   */
  @Nonnull
  public OptionGroup addOption (@Nonnull final Option aOption)
  {
    ValueEnforcer.notNull (aOption, "Option");

    // key - option name
    // value - the option
    m_aOptions.put (aOption.getKey (), aOption);

    return this;
  }

  /**
   * @return the names of the options in this group as a <code>Collection</code>
   */
  @Nonnull
  @ReturnsMutableCopy
  public ICommonsSet <String> getAllNames ()
  {
    // the key set is the collection of names
    return m_aOptions.copyOfKeySet ();
  }

  /**
   * @return the options in this group as a <code>Collection</code>
   */
  @Nonnull
  @ReturnsMutableCopy
  public ICommonsList <Option> getAllOptions ()
  {
    // the values are the collection of options
    return m_aOptions.copyOfValues ();
  }

  /**
   * Set the selected option of this group to <code>name</code>.
   *
   * @param aOption
   *        the option that is selected
   * @throws AlreadySelectedException
   *         if an option from this group has already been selected.
   */
  public void setSelected (@Nullable final Option aOption) throws AlreadySelectedException
  {
    if (aOption == null)
    {
      // reset the option previously selected
      m_sSelected = null;
      return;
    }

    // if no option has already been selected or the
    // same option is being reselected then set the
    // selected member variable
    final String sKey = aOption.getKey ();
    if (m_sSelected == null || m_sSelected.equals (sKey))
    {
      m_sSelected = sKey;
    }
    else
    {
      throw new AlreadySelectedException (this, aOption);
    }
  }

  /**
   * @return the selected option name
   */
  @Nullable
  public String getSelected ()
  {
    return m_sSelected;
  }

  /**
   * @return <code>true</code> if a selected element is present,
   *         <code>false</code> if not.
   */
  public boolean hasSelected ()
  {
    return m_sSelected != null;
  }

  /**
   * @param bRequired
   *        specifies if this group is required
   */
  public void setRequired (final boolean bRequired)
  {
    m_bRequired = bRequired;
  }

  /**
   * Returns whether this option group is required.
   *
   * @return whether this option group is required
   */
  public boolean isRequired ()
  {
    return m_bRequired;
  }

  /**
   * Returns the stringified version of this OptionGroup.
   *
   * @return the stringified representation of this group
   */
  @Nonnull
  @Nonempty
  public String getAsString ()
  {
    final StringBuilder aSB = new StringBuilder ();
    aSB.append ('[');

    final Iterator <Option> it = m_aOptions.values ().iterator ();
    while (it.hasNext ())
    {
      final Option aOption = it.next ();

      if (aOption.hasOpt ())
        aSB.append (CommandLineParser.PREFIX_SHORT_OPT).append (aOption.getOpt ());
      else
        aSB.append (CommandLineParser.PREFIX_LONG_OPT).append (aOption.getLongOpt ());

      if (aOption.hasDescription ())
        aSB.append (' ').append (aOption.getDescription ());

      if (it.hasNext ())
        aSB.append (", ");
    }

    aSB.append (']');
    return aSB.toString ();
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final OptionGroup rhs = (OptionGroup) o;
    return m_aOptions.equals (rhs.m_aOptions) &&
           m_bRequired == rhs.m_bRequired &&
           EqualsHelper.equals (m_sSelected, rhs.m_sSelected);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aOptions).append (m_bRequired).append (m_sSelected).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("Options", m_aOptions)
                                       .append ("Required", m_bRequired)
                                       .append ("Selected", m_sSelected)
                                       .getToString ();
  }
}
