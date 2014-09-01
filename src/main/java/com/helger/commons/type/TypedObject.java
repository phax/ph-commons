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
package com.helger.commons.type;

import java.io.Serializable;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.hash.HashCodeGenerator;
import com.helger.commons.string.ToStringGenerator;

/**
 * Base implementation of an object that has a type and an ID.<br>
 * This class is immutable if the type of the ID is immutable.
 * 
 * @author Philip Helger
 * @param <IDTYPE>
 *        The type of the ID.
 */
@Immutable
public final class TypedObject <IDTYPE extends Serializable> implements ITypedObject <IDTYPE>, Serializable
{
  private final ObjectType m_aTypeID;
  private final IDTYPE m_aID;

  public TypedObject (@Nonnull final ITypedObject <IDTYPE> aObj)
  {
    this (aObj.getTypeID (), aObj.getID ());
  }

  public TypedObject (@Nonnull final ObjectType aTypeID, @Nonnull final IDTYPE aID)
  {
    m_aTypeID = ValueEnforcer.notNull (aTypeID, "TypeID");
    m_aID = ValueEnforcer.notNull (aID, "ID");
  }

  @Nonnull
  public ObjectType getTypeID ()
  {
    return m_aTypeID;
  }

  @Nonnull
  public IDTYPE getID ()
  {
    return m_aID;
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (!(o instanceof TypedObject <?>))
      return false;
    final TypedObject <?> rhs = (TypedObject <?>) o;
    return m_aTypeID.equals (rhs.m_aTypeID) && m_aID.equals (rhs.m_aID);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aTypeID).append (m_aID).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("type", m_aTypeID).append ("id", m_aID).toString ();
  }

  @Nonnull
  public static <IDTYPE extends Serializable> TypedObject <IDTYPE> create (@Nonnull final ITypedObject <IDTYPE> aObj)
  {
    return new TypedObject <IDTYPE> (aObj);
  }

  @Nonnull
  public static <IDTYPE extends Serializable> TypedObject <IDTYPE> create (@Nonnull final ObjectType aTypeID,
                                                                           @Nonnull final IDTYPE aID)
  {
    return new TypedObject <IDTYPE> (aTypeID, aID);
  }
}
