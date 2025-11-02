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

import java.util.Iterator;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsIterable;
import com.helger.collection.commons.ICommonsList;

/**
 * Manager for {@link IOptionBase} objects which may be {@link Option} or {@link OptionGroup}.
 *
 * @author Philip Helger
 */
public class Options implements ICommonsIterable <IOptionBase>
{
  private final ICommonsList <IOptionBase> m_aOptions = new CommonsArrayList <> ();

  public Options ()
  {}

  @Nullable
  public Option _getFromName (final String s)
  {
    for (final IOptionBase aOB : m_aOptions)
    {
      if (aOB instanceof final Option aOption)
      {
        if (aOption.matches (s))
          return aOption;
      }
      else
      {
        for (final Option aOption : (OptionGroup) aOB)
          if (aOption.matches (s))
            return aOption;
      }
    }
    return null;
  }

  @NonNull
  public Options addOption (@NonNull final OptionBuilder aBuilder)
  {
    return addOption (aBuilder.build ());
  }

  private void _validateOption (@NonNull final Option aOption)
  {
    ValueEnforcer.notNull (aOption, "Option");
    if (aOption.hasShortOpt ())
      ValueEnforcer.isNull (_getFromName (aOption.getShortOpt ()),
                            () -> "Another option with the short name '" +
                                  aOption.getShortOpt () +
                                  "' is already contained!");
    if (aOption.hasLongOpt ())
      ValueEnforcer.isNull (_getFromName (aOption.getLongOpt ()),
                            () -> "Another option with the longs name '" +
                                  aOption.getLongOpt () +
                                  "' is already contained!");
  }

  @NonNull
  public Options addOption (@NonNull final Option aOption)
  {
    _validateOption (aOption);
    m_aOptions.add (aOption);
    return this;
  }

  @NonNull
  public Options addOptionGroup (@NonNull final OptionGroup aOptionGroup)
  {
    ValueEnforcer.notNull (aOptionGroup, "OptionGroup");
    for (final Option aOption : aOptionGroup)
      _validateOption (aOption);
    m_aOptions.add (aOptionGroup);
    return this;
  }

  @NonNull
  @ReturnsMutableCopy
  public ICommonsList <Option> getAllOptions ()
  {
    return m_aOptions.getAllInstanceOf (Option.class);
  }

  @NonNull
  public Iterator <IOptionBase> iterator ()
  {
    return m_aOptions.iterator ();
  }

  @NonNull
  @ReturnsMutableCopy
  public ICommonsList <Option> getAllResolvedOptions ()
  {
    final ICommonsList <Option> ret = new CommonsArrayList <> ();
    for (final IOptionBase aOptionBase : m_aOptions)
      if (aOptionBase instanceof final Option aOption)
        ret.add (aOption);
      else
        ret.addAll (((OptionGroup) aOptionBase).getAllOptions ());
    return ret;
  }

  @Nullable
  public OptionGroup getOptionGroup (@Nullable final Option aOption)
  {
    if (aOption != null)
      for (final IOptionBase aOptionBase : m_aOptions)
        if (aOptionBase instanceof final OptionGroup aOptionGroup)
          if (aOptionGroup.contains (aOption))
            return aOptionGroup;
    return null;
  }
}
