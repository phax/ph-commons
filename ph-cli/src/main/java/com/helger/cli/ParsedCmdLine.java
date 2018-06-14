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
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.ReturnsMutableObject;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.CommonsLinkedHashMap;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.collection.impl.ICommonsOrderedMap;
import com.helger.commons.string.StringHelper;
import com.helger.commons.string.ToStringGenerator;
import com.helger.commons.traits.IGetterByKeyTrait;

/**
 * This class represents a parsed command line. Parsing happens in class
 * {@link CmdLineParser}.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class ParsedCmdLine implements Serializable, IGetterByKeyTrait <String>
{
  private final ICommonsOrderedMap <IOptionBase, ICommonsList <String>> m_aParams = new CommonsLinkedHashMap <> ();
  private final ICommonsList <String> m_aUnknownTokens = new CommonsArrayList <> ();

  public ParsedCmdLine ()
  {}

  void internalAddValue (@Nonnull final IOptionBase aOption, @Nonnull final ICommonsList <String> aValues)
  {
    ValueEnforcer.notNull (aOption, "Option");
    ValueEnforcer.notNull (aValues, "Values");

    m_aParams.computeIfAbsent (aOption, k -> new CommonsArrayList <> ()).addAll (aValues);
  }

  void internalAddUnhandledToken (@Nonnull @Nonempty final String sUnknownToken)
  {
    ValueEnforcer.notEmpty (sUnknownToken, "UnknownToken");
    m_aUnknownTokens.add (sUnknownToken);
  }

  @Nullable
  private ICommonsList <String> _find (@Nullable final IOptionBase aOption)
  {
    return aOption == null ? null : m_aParams.get (aOption);
  }

  @Nullable
  private ICommonsList <String> _find (@Nullable final String sOption)
  {
    if (StringHelper.hasNoText (sOption))
      return null;

    for (final Map.Entry <IOptionBase, ICommonsList <String>> aEntry : m_aParams.entrySet ())
      if (aEntry.getKey () instanceof Option)
      {
        if (((Option) aEntry.getKey ()).matches (sOption))
          return aEntry.getValue ();
      }
      else
        // Do not resolve option groups, as the resolution happens on insertion!
        if (false)
        {
          for (final Option aOption : (OptionGroup) aEntry.getKey ())
            if (aOption.matches (sOption))
              return aEntry.getValue ();
        }
    return null;
  }

  public boolean hasOption (@Nullable final IOptionBase aOption)
  {
    return _find (aOption) != null;
  }

  public boolean hasOption (@Nullable final String sOption)
  {
    return _find (sOption) != null;
  }

  @Nullable
  public String getValue (@Nonnull final IOptionBase aOption)
  {
    final ICommonsList <String> aValues = _find (aOption);
    return aValues == null ? null : aValues.getFirst ();
  }

  @Nullable
  public String getValue (@Nullable final String sOption)
  {
    final ICommonsList <String> aValues = _find (sOption);
    return aValues == null ? null : aValues.getFirst ();
  }

  @Nullable
  @ReturnsMutableObject
  public ICommonsList <String> values (@Nonnull final IOptionBase aOption)
  {
    final ICommonsList <String> aValues = _find (aOption);
    return aValues == null ? null : aValues.getClone ();
  }

  @Nullable
  @ReturnsMutableObject
  public ICommonsList <String> values (@Nonnull final String sOption)
  {
    final ICommonsList <String> aValues = _find (sOption);
    return aValues == null ? null : aValues.getClone ();
  }

  @Nonnull
  @ReturnsMutableObject
  public ICommonsList <String> unknownTokens ()
  {
    return m_aUnknownTokens;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (null).append ("Params", m_aParams)
                                       .append ("UnknownTokens", m_aUnknownTokens)
                                       .getToString ();
  }
}
