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
package com.helger.base.wrapper;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.clone.ICloneable;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.equals.EqualsHelper;
import com.helger.base.hashcode.HashCodeGenerator;
import com.helger.base.state.EChange;
import com.helger.base.tostring.ToStringGenerator;

/**
 * This is a stupid wrapper around any object. Its original purpose was the encapsulation of return
 * values from within an anonymous class.
 *
 * @author Philip Helger
 * @param <DATATYPE>
 *        The type of object to be wrapped.
 */
@NotThreadSafe
public class Wrapper <DATATYPE> implements IMutableWrapper <DATATYPE>, ICloneable <Wrapper <DATATYPE>>
{
  private DATATYPE m_aObj;

  /**
   * Default constructor. Wraps a <code>null</code> values.
   */
  public Wrapper ()
  {}

  /**
   * Constructor with an existing object.
   *
   * @param aObj
   *        The existing object. May be <code>null</code>.
   */
  public Wrapper (@Nullable final DATATYPE aObj)
  {
    m_aObj = aObj;
  }

  /**
   * Copy constructor. Only takes wrappers of the same type.
   *
   * @param aRhs
   *        The other wrapper to use. May not be <code>null</code>.
   */
  public Wrapper (@NonNull final IWrapper <DATATYPE> aRhs)
  {
    ValueEnforcer.notNull (aRhs, "Wrapper");
    m_aObj = aRhs.get ();
  }

  @Nullable
  public DATATYPE get ()
  {
    return m_aObj;
  }

  @NonNull
  public EChange set (@Nullable final DATATYPE aObj)
  {
    if (EqualsHelper.equals (m_aObj, aObj))
      return EChange.UNCHANGED;
    m_aObj = aObj;
    return EChange.CHANGED;
  }

  @NonNull
  @ReturnsMutableCopy
  public Wrapper <DATATYPE> getClone ()
  {
    return new Wrapper <> (m_aObj);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final Wrapper <?> rhs = (Wrapper <?>) o;
    return EqualsHelper.equals (m_aObj, rhs.m_aObj);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aObj).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("Obj", m_aObj).getToString ();
  }

  /**
   * Create a new Wrapper object that contains a <code>null</code> value.
   *
   * @param <T>
   *        The type to be wrapped.
   * @return A non-<code>null</code> {@link Wrapper} instance.
   * @since 12.1.2
   */
  @NonNull
  public static <T> Wrapper <T> empty ()
  {
    return new Wrapper <> ();
  }

  /**
   * Create a new Wrapper object that contains the provided value.
   *
   * @param aValue
   *        The value to be wrapped. May be <code>null</code>.
   * @param <T>
   *        The type to be wrapped.
   * @return A non-<code>null</code> {@link Wrapper} instance.
   * @since 12.1.2
   */
  @NonNull
  public static <T> Wrapper <T> of (@Nullable final T aValue)
  {
    return new Wrapper <> (aValue);
  }

  /**
   * Create a new Wrapper object that contains the value of the provided {@link Wrapper}.
   *
   * @param aValue
   *        The wrapped value to be wrapped again. May not be <code>null</code>.
   * @param <T>
   *        The type to be wrapped.
   * @return A non-<code>null</code> {@link Wrapper} instance.
   * @since 12.1.2
   */
  @NonNull
  public static <T> Wrapper <T> of (@NonNull final IWrapper <T> aValue)
  {
    return new Wrapper <> (aValue);
  }
}
