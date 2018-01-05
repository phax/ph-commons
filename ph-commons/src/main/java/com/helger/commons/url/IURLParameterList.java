/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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
package com.helger.commons.url;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.math.MathHelper;
import com.helger.commons.random.RandomHelper;
import com.helger.commons.traits.IGenericImplTrait;

/**
 * Internal helper interface for objects handling URL parameters
 *
 * @author Philip Helger
 * @param <IMPLTYPE>
 *        Implementation type
 */
public interface IURLParameterList <IMPLTYPE extends IURLParameterList <IMPLTYPE>> extends IGenericImplTrait <IMPLTYPE>
{
  boolean add (@Nonnull URLParameter aURLParam);

  /**
   * Add a parameter without a value
   *
   * @param sName
   *        The name of the parameter. May neither be <code>null</code> nor
   *        empty.
   * @return this
   */
  @Nonnull
  default IMPLTYPE add (@Nonnull @Nonempty final String sName)
  {
    return add (sName, (String) null);
  }

  @Nonnull
  default IMPLTYPE add (@Nonnull final Map.Entry <String, String> aEntry)
  {
    return add (aEntry.getKey (), aEntry.getValue ());
  }

  @Nonnull
  default IMPLTYPE add (@Nonnull @Nonempty final String sName, final boolean bValue)
  {
    return add (sName, Boolean.toString (bValue));
  }

  @Nonnull
  default IMPLTYPE add (@Nonnull @Nonempty final String sName, final int nValue)
  {
    return add (sName, Integer.toString (nValue));
  }

  @Nonnull
  default IMPLTYPE add (@Nonnull @Nonempty final String sName, final long nValue)
  {
    return add (sName, Long.toString (nValue));
  }

  @Nonnull
  default IMPLTYPE add (@Nonnull @Nonempty final String sName, @Nullable final BigInteger aValue)
  {
    return add (sName, aValue == null ? null : aValue.toString ());
  }

  @Nonnull
  default IMPLTYPE add (@Nonnull @Nonempty final String sName, @Nullable final String sValue)
  {
    // Ensure the parameter is kept - maybe without a value
    add (new URLParameter (sName, sValue != null ? sValue : ""));
    return thisAsT ();
  }

  /**
   * Add a parameter with a random long value
   *
   * @param sName
   *        The name of the parameter. May neither be <code>null</code> nor
   *        empty.
   * @return this
   * @since 9.0.0
   */
  @Nonnull
  default IMPLTYPE addRandom (@Nonnull @Nonempty final String sName)
  {
    return add (sName, MathHelper.abs (RandomHelper.getRandom ().nextLong ()));
  }

  /**
   * Add the parameter of the passed value predicate evaluates to true.
   *
   * @param sName
   *        Parameter name. May neither be <code>null</code> nor empty.
   * @param sValue
   *        Parameter value. May not be <code>null</code> if the predicate
   *        evaluates to <code>true</code>.
   * @param aFilter
   *        The predicate to be evaluated on the value. May not be
   *        <code>null</code>.
   * @return this for chaining
   */
  @Nonnull
  default IMPLTYPE addIf (@Nonnull @Nonempty final String sName,
                          @Nullable final String sValue,
                          @Nonnull final Predicate <? super String> aFilter)
  {
    if (aFilter.test (sValue))
      add (sName, sValue);
    return thisAsT ();
  }

  /**
   * Add the parameter of the passed value if it is not null.
   *
   * @param sName
   *        Parameter name. May neither be <code>null</code> nor empty.
   * @param sValue
   *        Parameter value. May be <code>null</code>.
   * @return this for chaining
   */
  @Nonnull
  default IMPLTYPE addIfNotNull (@Nonnull @Nonempty final String sName, @Nullable final String sValue)
  {
    if (sValue != null)
      add (sName, sValue);
    return thisAsT ();
  }

  @Nonnull
  default IMPLTYPE addAll (@Nullable final Map <String, String> aParams)
  {
    if (aParams != null)
      for (final Map.Entry <String, String> aEntry : aParams.entrySet ())
        add (aEntry);
    return thisAsT ();
  }

  @Nonnull
  default IMPLTYPE addAll (@Nullable final List <? extends URLParameter> aParams)
  {
    if (aParams != null)
      for (final URLParameter aParam : aParams)
        add (aParam);
    return thisAsT ();
  }

  @Nonnull
  default IMPLTYPE addAll (@Nonnull @Nonempty final String sName, @Nullable final String... aValues)
  {
    if (aValues != null)
      for (final String sValue : aValues)
        add (sName, sValue);
    return thisAsT ();
  }
}
