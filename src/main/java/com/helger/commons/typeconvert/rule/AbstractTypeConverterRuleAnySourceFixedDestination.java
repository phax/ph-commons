/**
 * Copyright (C) 2014 Philip Helger (www.helger.com)
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
 * destination class. Example from Object.class to String.class
 * 
 * @author Philip Helger
 */
public abstract class AbstractTypeConverterRuleAnySourceFixedDestination extends AbstractTypeConverterRule
{
  private final Class <?> m_aDstClass;

  public AbstractTypeConverterRuleAnySourceFixedDestination (@Nonnull final Class <?> aDstClass)
  {
    super (ESubType.ANY_SRC_FIXED_DST);
    m_aDstClass = ValueEnforcer.notNull (aDstClass, "DestClass");
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

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ()).append ("dstClass", m_aDstClass.getName ()).toString ();
  }
}
