/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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

import javax.annotation.Nonnull;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.string.ToStringGenerator;

/**
 * Abstract type converter than can convert from a base source class to a
 * destination class. Example from Number.class to String.class
 *
 * @author Philip Helger
 */
public abstract class AbstractTypeConverterRuleAssignableSourceFixedDestination extends AbstractTypeConverterRule
{
  private final Class <?> m_aSrcClass;
  private final Class <?> m_aDstClass;

  public AbstractTypeConverterRuleAssignableSourceFixedDestination (@Nonnull final Class <?> aSrcClass,
                                                                    @Nonnull final Class <?> aDstClass)
  {
    super (ESubType.ASSIGNABLE_SRC_FIXED_DST);
    m_aSrcClass = ValueEnforcer.notNull (aSrcClass, "SrcClass");
    m_aDstClass = ValueEnforcer.notNull (aDstClass, "DestClass");
  }

  public final boolean canConvert (@Nonnull final Class <?> aSrcClass, @Nonnull final Class <?> aDstClass)
  {
    return m_aDstClass.equals (aDstClass) && m_aSrcClass.isAssignableFrom (aSrcClass);
  }

  @Nonnull
  public final Class <?> getSourceClass ()
  {
    return m_aSrcClass;
  }

  @Nonnull
  public final Class <?> getDestinationClass ()
  {
    return m_aDstClass;
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ())
                            .append ("srcClass", m_aSrcClass.getName ())
                            .append ("dstClass", m_aDstClass.getName ())
                            .toString ();
  }
}
