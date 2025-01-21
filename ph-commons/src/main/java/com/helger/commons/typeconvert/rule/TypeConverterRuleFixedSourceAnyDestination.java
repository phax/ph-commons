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
package com.helger.commons.typeconvert.rule;

import java.util.function.Function;

import javax.annotation.Nonnull;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.string.ToStringGenerator;
import com.helger.commons.typeconvert.TypeConverter;

/**
 * Abstract type converter than can convert from a base source class to a
 * destination class. Example from Number.class to String.class
 *
 * @author Philip Helger
 * @param <SRC>
 *        Source type
 */
public class TypeConverterRuleFixedSourceAnyDestination <SRC> extends AbstractTypeConverterRule <SRC, Object>
{
  private final Class <SRC> m_aSrcClass;
  private final Function <? super SRC, ? extends Object> m_aInBetweenConverter;
  // Status vars
  private Class <?> m_aEffectiveDstClass;

  public TypeConverterRuleFixedSourceAnyDestination (@Nonnull final Class <SRC> aSrcClass,
                                                     @Nonnull final Function <? super SRC, ? extends Object> aInBetweenConverter)
  {
    super (ESubType.FIXED_SRC_ANY_DST);
    m_aSrcClass = ValueEnforcer.notNull (aSrcClass, "SrcClass");
    m_aInBetweenConverter = ValueEnforcer.notNull (aInBetweenConverter, "InBetweenConverter");
  }

  public final boolean canConvert (@Nonnull final Class <?> aSrcClass, @Nonnull final Class <?> aDstClass)
  {
    // destination class can be anything
    if (!m_aSrcClass.equals (aSrcClass))
      return false;
    // Remember destination class for target conversion
    m_aEffectiveDstClass = aDstClass;
    return true;
  }

  @Nonnull
  protected Object getInBetweenValue (@Nonnull final SRC aSource)
  {
    return m_aInBetweenConverter.apply (aSource);
  }

  public final Object apply (@Nonnull final SRC aSource)
  {
    final Object aInBetweenValue = getInBetweenValue (aSource);
    return TypeConverter.convert (aInBetweenValue, m_aEffectiveDstClass);
  }

  @Nonnull
  public final Class <?> getSourceClass ()
  {
    return m_aSrcClass;
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ())
                            .append ("SrcClass", m_aSrcClass)
                            .append ("InBetweenConverter", m_aInBetweenConverter)
                            .getToString ();
  }
}
