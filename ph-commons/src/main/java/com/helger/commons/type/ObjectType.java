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
package com.helger.commons.type;

import java.io.Serializable;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.name.IHasName;
import com.helger.commons.string.ToStringGenerator;

/**
 * This class encapsulates an internal object type.<br>
 * Note: it is not based on an interface, because this may lead to difficulties
 * comparing different object type implementations of the same interface. By not
 * using an interface, implementers are forced to use exactly this
 * implementation class.
 *
 * @author Philip Helger
 */
@Immutable
public class ObjectType implements Comparable <ObjectType>, Serializable, IHasName
{
  private final String m_sName;
  // The mutable m_aHashCode does not contradict thread safety
  private Integer m_aHashCode;

  public ObjectType (@Nonnull @Nonempty final String sName)
  {
    m_sName = ValueEnforcer.notEmpty (sName, "Name");
  }

  @Nonnull
  @Nonempty
  public String getName ()
  {
    return m_sName;
  }

  public int compareTo (@Nonnull final ObjectType aObjType)
  {
    return m_sName.compareTo (aObjType.m_sName);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final ObjectType rhs = (ObjectType) o;
    return m_sName.equals (rhs.m_sName);
  }

  @Override
  public int hashCode ()
  {
    // We want a cached one!
    if (m_aHashCode == null)
      m_aHashCode = new HashCodeGenerator (this).append (m_sName).getHashCodeObj ();
    return m_aHashCode.intValue ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("Name", m_sName).toString ();
  }
}
