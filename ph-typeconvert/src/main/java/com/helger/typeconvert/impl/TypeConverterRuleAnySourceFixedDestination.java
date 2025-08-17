/*
 * Copyright (C) 2015-2025 Philip Helger (www.helger.com)
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
package com.helger.typeconvert.impl;

import java.util.function.Function;

import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.tostring.ToStringGenerator;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * Type converter than can convert from a base source class to a destination
 * class. Example from Object.class to String.class
 *
 * @author Philip Helger
 * @param <DST>
 *        Destination type
 */
public class TypeConverterRuleAnySourceFixedDestination <DST> extends AbstractTypeConverterRule <Object, DST>
{
  private final Class <DST> m_aDstClass;
  private final Function <? super Object, ? extends DST> m_aConverter;

  public TypeConverterRuleAnySourceFixedDestination (@Nonnull final Class <DST> aDstClass,
                                                     @Nonnull final Function <? super Object, ? extends DST> aConverter)
  {
    super (ESubType.ANY_SRC_FIXED_DST);
    m_aDstClass = ValueEnforcer.notNull (aDstClass, "DestClass");
    m_aConverter = ValueEnforcer.notNull (aConverter, "Converter");
  }

  public final boolean canConvert (@Nonnull final Class <?> aSrcClass, @Nonnull final Class <?> aDstClass)
  {
    // source class can be anything
    return m_aDstClass.equals (aDstClass);
  }

  @Nonnull
  public final Class <?> getDestinationClass ()
  {
    return m_aDstClass;
  }

  @Nullable
  public DST apply (@Nonnull final Object aSource)
  {
    return m_aConverter.apply (aSource);
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ())
                            .append ("DstClass", m_aDstClass)
                            .append ("Converter", m_aConverter)
                            .getToString ();
  }
}
