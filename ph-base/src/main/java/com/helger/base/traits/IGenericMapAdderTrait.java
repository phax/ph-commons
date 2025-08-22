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
package com.helger.base.traits;

import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.Predicate;

import com.helger.base.string.StringHelper;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * Add arbitrary objects to this, where this is a Map based structure like HashMap or TreeMap.
 *
 * @author Philip Helger
 * @param <KEYTYPE>
 *        Map key type.
 * @param <VALUETYPE>
 *        The element type to be added. Must implement IAddableByTrait as a hack, so that the APIs
 *        <code>add(Object)</code> and <code>add(VALUETYPE)</code> can co-exist. Otherwise there
 *        would be a problem with type erasure.
 * @param <IMPLTYPE>
 *        The implementation type for chaining API
 * @since v12.0.0 RC2
 */
public interface IGenericMapAdderTrait <KEYTYPE, VALUETYPE extends IAddableByTrait, IMPLTYPE extends IGenericMapAdderTrait <KEYTYPE, VALUETYPE, IMPLTYPE>>
                                       extends
                                       IHasTypeConverterTo <VALUETYPE>,
                                       IGenericImplTrait <IMPLTYPE>
{
  @Nonnull
  IMPLTYPE add (@Nonnull KEYTYPE sName, @Nonnull VALUETYPE aValue);

  @Nonnull
  default IMPLTYPE addIf (@Nonnull final KEYTYPE sName,
                          @Nonnull final VALUETYPE aValue,
                          @Nonnull final Predicate <? super VALUETYPE> aFilter)
  {
    if (aFilter.test (aValue))
      add (sName, aValue);
    return thisAsT ();
  }

  @Nonnull
  default IMPLTYPE addIf (@Nonnull final KEYTYPE sName,
                          @Nonnull final VALUETYPE aValue,
                          @Nonnull final BooleanSupplier aFilter)
  {
    if (aFilter.getAsBoolean ())
      add (sName, aValue);
    return thisAsT ();
  }

  @Nonnull
  default IMPLTYPE addIfNotNull (@Nonnull final KEYTYPE sName, @Nullable final VALUETYPE aValue)
  {
    if (aValue != null)
      add (sName, aValue);
    return thisAsT ();
  }

  @Nonnull
  default IMPLTYPE add (@Nonnull final KEYTYPE sName, @Nullable final Object aValue)
  {
    return add (sName, getTypeConverterTo ().convert (aValue));
  }

  @Nonnull
  default IMPLTYPE addIf (@Nonnull final KEYTYPE sName,
                          @Nullable final Object aValue,
                          @Nonnull final BooleanSupplier aSupplier)
  {
    if (aSupplier.getAsBoolean ())
      add (sName, aValue);
    return thisAsT ();
  }

  @Nonnull
  default <T> IMPLTYPE addIf (@Nonnull final KEYTYPE sName,
                              @Nullable final T aValue,
                              @Nonnull final Predicate <? super T> aFilter)
  {
    if (aFilter.test (aValue))
      add (sName, aValue);
    return thisAsT ();
  }

  @Nonnull
  default IMPLTYPE addIfNotNull (@Nonnull final KEYTYPE sName, @Nullable final Object aValue)
  {
    if (aValue != null)
      add (sName, aValue);
    return thisAsT ();
  }

  @Nonnull
  default IMPLTYPE addIfNotEmpty (@Nonnull final KEYTYPE sName, @Nullable final String sValue)
  {
    if (StringHelper.isNotEmpty (sValue))
      add (sName, sValue);
    return thisAsT ();
  }

  @Nonnull
  default IMPLTYPE add (@Nonnull final Map.Entry <? extends KEYTYPE, ?> aEntry)
  {
    return add (aEntry.getKey (), aEntry.getValue ());
  }

  @Nonnull
  default IMPLTYPE add (@Nonnull final KEYTYPE sName, final boolean bValue)
  {
    return add (sName, getTypeConverterTo ().convert (bValue));
  }

  @Nonnull
  default IMPLTYPE add (@Nonnull final KEYTYPE sName, final char cValue)
  {
    return add (sName, getTypeConverterTo ().convert (cValue));
  }

  @Nonnull
  default IMPLTYPE add (@Nonnull final KEYTYPE sName, final double dValue)
  {
    return add (sName, getTypeConverterTo ().convert (dValue));
  }

  @Nonnull
  default IMPLTYPE add (@Nonnull final KEYTYPE sName, final int nValue)
  {
    return add (sName, getTypeConverterTo ().convert (nValue));
  }

  @Nonnull
  default IMPLTYPE add (@Nonnull final KEYTYPE sName, final long nValue)
  {
    return add (sName, getTypeConverterTo ().convert (nValue));
  }

  @Nonnull
  default IMPLTYPE addAll (@Nullable final Iterable <Map.Entry <KEYTYPE, VALUETYPE>> aIterable)
  {
    if (aIterable != null)
      for (final var aEntry : aIterable)
        add (aEntry.getKey (), aEntry.getValue ());
    return thisAsT ();
  }

  @Nonnull
  default IMPLTYPE addAll (@Nullable final Map <? extends KEYTYPE, ? extends VALUETYPE> aMap)
  {
    if (aMap != null)
      for (final var aEntry : aMap.entrySet ())
        add (aEntry);
    return thisAsT ();
  }

  @Nonnull
  default IMPLTYPE addAllAny (@Nullable final Map <? extends KEYTYPE, ?> aMap)
  {
    if (aMap != null)
      for (final var aEntry : aMap.entrySet ())
        add (aEntry);
    return thisAsT ();
  }

  @Nonnull
  default IMPLTYPE addAllAny (@Nullable final Iterable <Map.Entry <KEYTYPE, ?>> aIterable)
  {
    if (aIterable != null)
      for (final var aEntry : aIterable)
        add (aEntry.getKey (), aEntry.getValue ());
    return thisAsT ();
  }

  @Nonnull
  default <SRCVALUETYPE> IMPLTYPE addAllMapped (@Nullable final Map <? extends KEYTYPE, ? extends SRCVALUETYPE> aMap,
                                                @Nonnull final Function <? super SRCVALUETYPE, VALUETYPE> aValueMapper)
  {
    if (aMap != null)
      for (final var aEntry : aMap.entrySet ())
        add (aEntry.getKey (), aValueMapper.apply (aEntry.getValue ()));
    return thisAsT ();
  }

  @Nonnull
  default <SRCKEYTYPE, SRCVALUETYPE> IMPLTYPE addAllMapped (@Nullable final Map <? extends SRCKEYTYPE, ? extends SRCVALUETYPE> aMap,
                                                            @Nonnull final Function <? super SRCKEYTYPE, KEYTYPE> aKeyMapper,
                                                            @Nonnull final Function <? super SRCVALUETYPE, VALUETYPE> aValueMapper)
  {
    if (aMap != null)
      for (final var aEntry : aMap.entrySet ())
        add (aKeyMapper.apply (aEntry.getKey ()), aValueMapper.apply (aEntry.getValue ()));
    return thisAsT ();
  }
}
