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
package com.helger.commons.microdom;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.string.ToStringGenerator;

/**
 * Default implementation of the {@link IMicroProcessingInstruction} interface.
 *
 * @author Philip Helger
 */
@Immutable
public final class MicroProcessingInstruction extends AbstractMicroNode implements IMicroProcessingInstruction
{
  private final String m_sTarget;
  private final String m_sData;

  public MicroProcessingInstruction (@Nonnull @Nonempty final String sTarget)
  {
    this (sTarget, null);
  }

  public MicroProcessingInstruction (@Nonnull @Nonempty final String sTarget, @Nullable final String sData)
  {
    m_sTarget = ValueEnforcer.notEmpty (sTarget, "Target");
    m_sData = sData;
  }

  @Nonnull
  public EMicroNodeType getType ()
  {
    return EMicroNodeType.PROCESSING_INSTRUCTION;
  }

  @Nonnull
  @Nonempty
  public String getNodeName ()
  {
    return "#pi";
  }

  @Override
  @Nonnull
  @Nonempty
  public String getNodeValue ()
  {
    return m_sTarget;
  }

  public String getTarget ()
  {
    return m_sTarget;
  }

  public String getData ()
  {
    return m_sData;
  }

  @Nonnull
  public IMicroProcessingInstruction getClone ()
  {
    return new MicroProcessingInstruction (m_sTarget, m_sData);
  }

  public boolean isEqualContent (@Nullable final IMicroNode o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final MicroProcessingInstruction rhs = (MicroProcessingInstruction) o;
    return m_sTarget.equals (rhs.m_sTarget) && EqualsHelper.equals (m_sData, rhs.m_sData);
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ())
                            .append ("target", m_sTarget)
                            .append ("data", m_sData)
                            .toString ();
  }
}
