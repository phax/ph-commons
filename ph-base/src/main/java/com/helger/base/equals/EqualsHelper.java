/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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
package com.helger.base.equals;

import java.util.Objects;
import java.util.function.BiPredicate;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.style.UsedInGeneratedCode;

/**
 * Helper class providing various methods for comparing objects by identity, by value, and for
 * arrays of all primitive types.
 *
 * @author Philip Helger
 */
public class EqualsHelper
{
  protected EqualsHelper ()
  {}

  /**
   * The only place where objects are compared by identity.
   *
   * @param aObj1
   *        First object. May be <code>null</code>.
   * @param aObj2
   *        Second object. May be <code>null</code>.
   * @return <code>true</code> if both objects are <code>null</code> or reference the same object.
   * @param <T>
   *        Type to check.
   */
  public static <T> boolean identityEqual (@Nullable final T aObj1, @Nullable final T aObj2)
  {
    return aObj1 == aObj2;
  }

  /**
   * The only place where objects are compared by identity.
   *
   * @param aObj1
   *        First object. May be <code>null</code>.
   * @param aObj2
   *        Second object. May be <code>null</code>.
   * @return <code>true</code> if one object is <code>null</code> or if they reference a different
   *         object.
   * @param <T>
   *        Type to check.
   * @since 11.1.7
   */
  public static <T> boolean identityDifferent (@Nullable final T aObj1, @Nullable final T aObj2)
  {
    return aObj1 != aObj2;
  }

  /**
   * Check if two values are equal. This method only exists, so that no type differentiation is
   * needed.
   *
   * @param bObj1
   *        First value
   * @param bObj2
   *        Second value
   * @return <code>true</code> if they are equal, <code>false</code> otherwise.
   */
  @UsedInGeneratedCode
  public static boolean equals (final boolean bObj1, final boolean bObj2)
  {
    return bObj1 == bObj2;
  }

  /**
   * Check if two values are equal. This method only exists, so that no type differentiation is
   * needed.
   *
   * @param nObj1
   *        First value
   * @param nObj2
   *        Second value
   * @return <code>true</code> if they are equal, <code>false</code> otherwise.
   */
  @UsedInGeneratedCode
  public static boolean equals (final byte nObj1, final byte nObj2)
  {
    return nObj1 == nObj2;
  }

  /**
   * Check if two values are equal. This method only exists, so that no type differentiation is
   * needed.
   *
   * @param cObj1
   *        First value
   * @param cObj2
   *        Second value
   * @return <code>true</code> if they are equal, <code>false</code> otherwise.
   */
  @UsedInGeneratedCode
  public static boolean equals (final char cObj1, final char cObj2)
  {
    return cObj1 == cObj2;
  }

  /**
   * Check if two double values are equal. This is necessary, because in some cases, the "=="
   * operator returns wrong results.
   *
   * @param dObj1
   *        First double
   * @param dObj2
   *        Second double
   * @return <code>true</code> if they are equal, <code>false</code> otherwise.
   */
  @UsedInGeneratedCode
  public static boolean equals (final double dObj1, final double dObj2)
  {
    // Special overload for "double" required!
    return (dObj1 == dObj2) || (Double.doubleToLongBits (dObj1) == Double.doubleToLongBits (dObj2));
  }

  /**
   * Check if two float values are equal. This is necessary, because in some cases, the "=="
   * operator returns wrong results.
   *
   * @param fObj1
   *        First float
   * @param fObj2
   *        Second float
   * @return <code>true</code> if they are equal, <code>false</code> otherwise.
   */
  @UsedInGeneratedCode
  public static boolean equals (final float fObj1, final float fObj2)
  {
    // Special overload for "float" required!
    return (fObj1 == fObj2) || (Float.floatToIntBits (fObj1) == Float.floatToIntBits (fObj2));
  }

  /**
   * Check if two values are equal. This method only exists, so that no type differentiation is
   * needed.
   *
   * @param nObj1
   *        First value
   * @param nObj2
   *        Second value
   * @return <code>true</code> if they are equal, <code>false</code> otherwise.
   */
  @UsedInGeneratedCode
  public static boolean equals (final int nObj1, final int nObj2)
  {
    return nObj1 == nObj2;
  }

  /**
   * Check if two values are equal. This method only exists, so that no type differentiation is
   * needed.
   *
   * @param nObj1
   *        First value
   * @param nObj2
   *        Second value
   * @return <code>true</code> if they are equal, <code>false</code> otherwise.
   */
  @UsedInGeneratedCode
  public static boolean equals (final long nObj1, final long nObj2)
  {
    return nObj1 == nObj2;
  }

  /**
   * Check if two values are equal. This method only exists, so that no type differentiation is
   * needed.
   *
   * @param nObj1
   *        First value
   * @param nObj2
   *        Second value
   * @return <code>true</code> if they are equal, <code>false</code> otherwise.
   */
  @UsedInGeneratedCode
  public static boolean equals (final short nObj1, final short nObj2)
  {
    return nObj1 == nObj2;
  }

  /**
   * Check if two values are equal. This method only exists, so that no type differentiation is
   * needed.
   *
   * @param aObj1
   *        First value
   * @param aObj2
   *        Second value
   * @return <code>true</code> if they are equal, <code>false</code> otherwise.
   */
  @UsedInGeneratedCode
  public static boolean equals (@Nullable final Object aObj1, @Nullable final Object aObj2)
  {
    return Objects.deepEquals (aObj1, aObj2);
  }

  /**
   * Check if the passed strings are equals case insensitive handling <code>null</code>
   * appropriately.
   *
   * @param sObj1
   *        First object to compare
   * @param sObj2
   *        Second object to compare
   * @return <code>true</code> if they are equal case insensitive, <code>false</code> otherwise.
   */
  public static boolean equalsIgnoreCase (@Nullable final String sObj1, @Nullable final String sObj2)
  {
    return sObj1 == null ? sObj2 == null : sObj1.equalsIgnoreCase (sObj2);
  }

  /**
   * Perform an equals check with a custom predicate that is only invoked, if both objects are
   * non-<code>null</code>.
   *
   * @param <T>
   *        parameter type
   * @param aObj1
   *        The first object to be compared. May be <code>null</code>.
   * @param aObj2
   *        The second object to be compared. May be <code>null</code>.
   * @param aPredicate
   *        The predicate to be invoked, if both objects are non-<code>null</code>. May not be
   *        <code>null</code>.
   * @return <code>true</code> if the contents are equal, <code>false</code> otherwise
   * @since 9.4.5
   */
  public static <T> boolean equalsCustom (@Nullable final T aObj1,
                                          @Nullable final T aObj2,
                                          @NonNull final BiPredicate <T, T> aPredicate)
  {
    Objects.requireNonNull (aPredicate, "Predicate");

    // Same object - check first
    if (identityEqual (aObj1, aObj2))
      return true;

    // Is only one value null?
    if (aObj1 == null || aObj2 == null)
      return false;

    return aPredicate.test (aObj1, aObj2);
  }
}
