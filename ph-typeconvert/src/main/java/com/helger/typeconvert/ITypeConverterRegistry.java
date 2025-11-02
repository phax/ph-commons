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
package com.helger.typeconvert;

import java.util.function.Function;

import org.jspecify.annotations.NonNull;

/**
 * Callback interface for registering new type converters.
 *
 * @author Philip Helger
 */
public interface ITypeConverterRegistry
{
  /**
   * Register a type converter.
   *
   * @param aSrcClass
   *        A non-<code>null</code> source class to convert from. Must be an instancable class.
   * @param aDstClass
   *        A non-<code>null</code> destination class to convert to. Must be an instancable class.
   *        May not equal the source class.
   * @param aConverter
   *        The convert to use. May not be <code>null</code>.
   * @param <SRC>
   *        Source type
   * @param <DST>
   *        Destination type
   */
  <SRC, DST> void registerTypeConverter (@NonNull Class <SRC> aSrcClass,
                                         @NonNull Class <DST> aDstClass,
                                         @NonNull ITypeConverter <SRC, DST> aConverter);

  /**
   * Register a type converter.
   *
   * @param aSrcClasses
   *        A non-<code>null</code> collection of source classes to convert from. Must be an
   *        instancable class.
   * @param aDstClass
   *        A non-<code>null</code> destination class to convert to. Must be an instancable class.
   *        May not equal the source class.
   * @param aConverter
   *        The convert to use. May not be <code>null</code>.
   * @param <DST>
   *        Destination type
   */
  <DST> void registerTypeConverter (@NonNull Class <?> [] aSrcClasses,
                                    @NonNull Class <DST> aDstClass,
                                    @NonNull ITypeConverter <?, DST> aConverter);

  /**
   * Register a flexible type converter rule.
   *
   * @param aTypeConverterRule
   *        The type converter rule to be registered. May not be <code>null</code>.
   */
  void registerTypeConverterRule (@NonNull ITypeConverterRule <?, ?> aTypeConverterRule);

  <DST> void registerTypeConverterRuleAnySourceFixedDestination (@NonNull final Class <DST> aDstClass,
                                                                 @NonNull final Function <? super Object, ? extends DST> aConverter);

  <SRC, DST> void registerTypeConverterRuleAssignableSourceFixedDestination (@NonNull final Class <SRC> aSrcClass,
                                                                             @NonNull final Class <DST> aDstClass,
                                                                             @NonNull final Function <? super SRC, ? extends DST> aConverter);

  <SRC> void registerTypeConverterRuleFixedSourceAnyDestination (@NonNull final Class <SRC> aSrcClass,
                                                                 @NonNull final Function <? super SRC, ? extends Object> aInBetweenConverter);

  <SRC, DST> void registerTypeConverterRuleFixedSourceAssignableDestination (@NonNull final Class <SRC> aSrcClass,
                                                                             @NonNull final Class <DST> aDstClass,
                                                                             @NonNull final Function <? super SRC, ? extends DST> aConverter);
}
