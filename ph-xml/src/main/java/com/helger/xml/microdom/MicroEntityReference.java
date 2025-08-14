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
package com.helger.xml.microdom;

import com.helger.annotation.Nonempty;
import com.helger.annotation.concurrent.Immutable;
import com.helger.base.equals.ValueEnforcer;
import com.helger.base.tostring.ToStringGenerator;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * Default implementation of the {@link IMicroEntityReference} interface.
 *
 * @author Philip Helger
 */
@Immutable
public final class MicroEntityReference extends AbstractMicroNode implements IMicroEntityReference
{
  private final String m_sName;

  public MicroEntityReference (@Nonnull @Nonempty final String sName)
  {
    m_sName = ValueEnforcer.notEmpty (sName, "EntityReferenceName");
  }

  @Nonnull
  public EMicroNodeType getType ()
  {
    return EMicroNodeType.ENTITY_REFERENCE;
  }

  @Nonnull
  @Nonempty
  public String getNodeName ()
  {
    return '&' + m_sName + ';';
  }

  @Override
  @Nonnull
  @Nonempty
  public String getNodeValue ()
  {
    return getName ();
  }

  @Nonnull
  @Nonempty
  public String getName ()
  {
    return m_sName;
  }

  @Nonnull
  public IMicroEntityReference getClone ()
  {
    return new MicroEntityReference (m_sName);
  }

  public boolean isEqualContent (@Nullable final IMicroNode o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final MicroEntityReference rhs = (MicroEntityReference) o;
    return m_sName.equals (rhs.m_sName);
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ()).append ("name", m_sName).getToString ();
  }
}
