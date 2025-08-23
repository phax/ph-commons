/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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
package com.helger.url;

import java.math.BigInteger;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;

import com.helger.annotation.Nonempty;
import com.helger.base.numeric.MathHelper;
import com.helger.base.state.EChange;
import com.helger.base.string.StringHelper;
import com.helger.typeconvert.impl.TypeConverter;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * Mutable interface for URL parameters
 *
 * @author Philip Helger
 * @param <IMPLTYPE>
 *        Implementation type
 */
public interface IMutableURLParameterList <IMPLTYPE extends IMutableURLParameterList <IMPLTYPE>> extends
                                          IURLParameterList <IMPLTYPE>
{
  @Nonnull
  IMPLTYPE add (@Nonnull URLParameter aURLParam);

  @Nonnull
  default IMPLTYPE add (@Nonnull @Nonempty final String sName, @Nullable final String sValue)
  {
    // Ensure the parameter is kept - maybe without a value
    add (new URLParameter (sName, StringHelper.getNotNull (sValue, "")));
    return thisAsT ();
  }

  @Nonnull
  default IMPLTYPE addIf (@Nonnull @Nonempty final String sName,
                          @Nullable final String sValue,
                          @Nonnull final BooleanSupplier aTest)
  {
    if (aTest.getAsBoolean ())
      add (sName, sValue);
    return thisAsT ();
  }

  @Nonnull
  default IMPLTYPE addIf (@Nonnull @Nonempty final String sName,
                          @Nullable final String sValue,
                          @Nonnull final Predicate <? super String> aTest)
  {
    if (aTest.test (sValue))
      add (sName, sValue);
    return thisAsT ();
  }

  @Nonnull
  default IMPLTYPE addIfNotNull (@Nonnull @Nonempty final String sName, @Nullable final String sValue)
  {
    if (sValue != null)
      add (new URLParameter (sName, sValue));
    return thisAsT ();
  }

  @Nonnull
  default IMPLTYPE add (@Nonnull final String sName, final boolean bValue)
  {
    return add (sName, TypeConverter.convert (bValue, String.class));
  }

  @Nonnull
  default IMPLTYPE add (@Nonnull final String sName, final char cValue)
  {
    return add (sName, TypeConverter.convert (cValue, String.class));
  }

  @Nonnull
  default IMPLTYPE add (@Nonnull final String sName, final double dValue)
  {
    return add (sName, TypeConverter.convert (dValue, String.class));
  }

  @Nonnull
  default IMPLTYPE add (@Nonnull final String sName, final float fValue)
  {
    return add (sName, TypeConverter.convert (fValue, String.class));
  }

  @Nonnull
  default IMPLTYPE add (@Nonnull final String sName, final int nValue)
  {
    return add (sName, TypeConverter.convert (nValue, String.class));
  }

  @Nonnull
  default IMPLTYPE add (@Nonnull final String sName, final long nValue)
  {
    return add (sName, TypeConverter.convert (nValue, String.class));
  }

  @Nonnull
  default IMPLTYPE add (@Nonnull final String sName, final short nValue)
  {
    return add (sName, TypeConverter.convert (nValue, String.class));
  }

  @Nonnull
  default IMPLTYPE add (@Nonnull @Nonempty final String sName, @Nullable final BigInteger aValue)
  {
    return add (sName, aValue == null ? null : aValue.toString ());
  }

  /**
   * Add a parameter without a value
   *
   * @param sName
   *        The name of the parameter. May neither be <code>null</code> nor empty.
   * @return this
   */
  @Nonnull
  default IMPLTYPE add (@Nonnull @Nonempty final String sName)
  {
    return add (sName, (String) null);
  }

  /**
   * Add a parameter with a random long value
   *
   * @param sName
   *        The name of the parameter. May neither be <code>null</code> nor empty.
   * @return this
   * @since 9.0.0
   */
  @Nonnull
  default IMPLTYPE addRandom (@Nonnull @Nonempty final String sName)
  {
    return add (sName, MathHelper.abs (ThreadLocalRandom.current ().nextLong ()));
  }

  @Nonnull
  default IMPLTYPE addAll (@Nullable final Iterable <? extends URLParameter> aParams)
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

  @Nonnull
  default IMPLTYPE addAll (@Nullable final Map <String, String> aMap)
  {
    if (aMap != null)
      for (final var e : aMap.entrySet ())
        add (e.getKey (), e.getValue ());
    return thisAsT ();
  }

  /**
   * Remove all parameters with the given name.
   *
   * @param sName
   *        The key to remove
   * @return {@link EChange}
   */
  @Nonnull
  EChange remove (@Nullable String sName);

  /**
   * Remove all parameter with the given name and value.
   *
   * @param sName
   *        The key to remove. May be <code>null</code>.
   * @param sValue
   *        The value to be removed. May be <code>null</code>.
   * @return {@link EChange}
   */
  @Nonnull
  EChange remove (@Nullable String sName, @Nullable String sValue);

  @Nonnull
  EChange removeAll ();
}
