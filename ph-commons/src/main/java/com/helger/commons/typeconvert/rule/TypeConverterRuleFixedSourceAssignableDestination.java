/**
 * Copyright (C) 2014-2016 Philip Helger (www.helger.com)
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
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.string.ToStringGenerator;

/**
 * Abstract type converter than can convert from a base source class to a
 * destination class. Example from String.class to specific Enum.class
 *
 * @author Philip Helger
 * @param <SRC>
 *        Source type
 * @param <DST>
 *        Destination type
 */
public class TypeConverterRuleFixedSourceAssignableDestination <SRC, DST> extends AbstractTypeConverterRule <SRC, DST>
{
  private final Class <SRC> m_aSrcClass;
  private final Class <DST> m_aDstClass;
  private final Function <SRC, DST> m_aConverter;

  public TypeConverterRuleFixedSourceAssignableDestination (@Nonnull final Class <SRC> aSrcClass,
                                                            @Nonnull final Class <DST> aDstClass,
                                                            @Nonnull final Function <SRC, DST> aConverter)
  {
    super (ESubType.FIXED_SRC_ASSIGNABLE_DST);
    m_aSrcClass = ValueEnforcer.notNull (aSrcClass, "SrcClass");
    m_aDstClass = ValueEnforcer.notNull (aDstClass, "DestClass");
    m_aConverter = ValueEnforcer.notNull (aConverter, "Converter");
  }

  public final boolean canConvert (@Nonnull final Class <?> aSrcClass, @Nonnull final Class <?> aDstClass)
  {
    return m_aSrcClass.equals (aSrcClass) && m_aDstClass.isAssignableFrom (aDstClass);
  }

  @Nonnull
  public final Class <SRC> getSourceClass ()
  {
    return m_aSrcClass;
  }

  @Nonnull
  public final Class <DST> getDestinationClass ()
  {
    return m_aDstClass;
  }

  @Nullable
  public DST apply (@Nonnull final SRC aSource)
  {
    return m_aConverter.apply (aSource);
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ())
                            .append ("srcClass", m_aSrcClass.getName ())
                            .append ("dstClass", m_aDstClass.getName ())
                            .append ("converter", m_aConverter)
                            .toString ();
  }
}
