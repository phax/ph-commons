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

import javax.annotation.Nonnull;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.string.ToStringGenerator;
import com.helger.commons.typeconvert.ITypeConverterRule;

/**
 * Abstract type converter rule implementation
 *
 * @author Philip Helger
 * @param <SRC>
 *        source type
 * @param <DST>
 *        destination type
 */
public abstract class AbstractTypeConverterRule <SRC, DST> implements ITypeConverterRule <SRC, DST>
{
  private final ESubType m_eSubType;

  public AbstractTypeConverterRule (@Nonnull final ITypeConverterRule.ESubType eSubType)
  {
    m_eSubType = ValueEnforcer.notNull (eSubType, "SubType");
  }

  @Nonnull
  public final ESubType getSubType ()
  {
    return m_eSubType;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("subType", m_eSubType).toString ();
  }
}
