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
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.string.ToStringGenerator;

/**
 * Base implementation of an object that has an {@link ObjectType} and an ID.
 * <br>
 * This class is immutable if the type of the ID is immutable.
 *
 * @author Philip Helger
 * @param <IDTYPE>
 *        The type of the ID.
 */
@Immutable
public final class TypedObject <IDTYPE extends Serializable> implements ITypedObject <IDTYPE>, Serializable
{
  private ObjectType m_aObjectType;
  private IDTYPE m_aID;

  /**
   * @param aObj
   *        The typed object to copy the stuff from. May not be
   *        <code>null</code>.
   */
  public TypedObject (@Nonnull final ITypedObject <IDTYPE> aObj)
  {
    this (aObj.getObjectType (), aObj.getID ());
  }

  /**
   * Constructor.
   *
   * @param aObjectType
   *        Object type to use. May not be <code>null</code>.
   * @param aID
   *        ID to be used. May not be <code>null</code>.
   */
  public TypedObject (@Nonnull final ObjectType aObjectType, @Nonnull final IDTYPE aID)
  {
    m_aObjectType = ValueEnforcer.notNull (aObjectType, "ObjectType");
    m_aID = ValueEnforcer.notNull (aID, "ID");
  }

  @Nonnull
  public ObjectType getObjectType ()
  {
    return m_aObjectType;
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
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final TypedObject <?> rhs = (TypedObject <?>) o;
    return m_aObjectType.equals (rhs.m_aObjectType) && m_aID.equals (rhs.m_aID);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aObjectType).append (m_aID).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("ObjectType", m_aObjectType).append ("ID", m_aID).toString ();
  }

  /**
   * Factory method
   *
   * @param <IDTYPE>
   *        The type of the ID.
   * @param aObj
   *        The typed object to copy the stuff from. May not be
   *        <code>null</code>.
   * @return new {@link TypedObject}
   */
  @Nonnull
  public static <IDTYPE extends Serializable> TypedObject <IDTYPE> create (@Nonnull final ITypedObject <IDTYPE> aObj)
  {
    return new TypedObject <IDTYPE> (aObj);
  }

  /**
   * Factory method
   *
   * @param <IDTYPE>
   *        The type of the ID.
   * @param aObjectType
   *        Object type to use. May not be <code>null</code>.
   * @param aID
   *        ID to be used. May not be <code>null</code>.
   * @return new {@link TypedObject}
   */
  @Nonnull
  public static <IDTYPE extends Serializable> TypedObject <IDTYPE> create (@Nonnull final ObjectType aObjectType,
                                                                           @Nonnull final IDTYPE aID)
  {
    return new TypedObject <IDTYPE> (aObjectType, aID);
  }
}
