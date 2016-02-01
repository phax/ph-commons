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
package com.helger.commons.collection;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.CGlobal;
import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.PresentForCodeCoverage;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.annotation.ReturnsMutableObject;
import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.lang.ClassHelper;
import com.helger.commons.lang.GenericReflection;

/**
 * Provides additional helper methods for array handling.
 *
 * @author Philip Helger
 */
@Immutable
public final class ArrayHelper
{
  /** Constant empty boolean array */
  public static final boolean [] EMPTY_BOOLEAN_ARRAY = new boolean [0];
  /** Constant empty byte array */
  public static final byte [] EMPTY_BYTE_ARRAY = new byte [0];
  /** Constant empty char array */
  public static final char [] EMPTY_CHAR_ARRAY = new char [0];
  /** Constant empty double array */
  public static final double [] EMPTY_DOUBLE_ARRAY = new double [0];
  /** Constant empty float array */
  public static final float [] EMPTY_FLOAT_ARRAY = new float [0];
  /** Constant empty int array */
  public static final int [] EMPTY_INT_ARRAY = new int [0];
  /** Constant empty long array */
  public static final long [] EMPTY_LONG_ARRAY = new long [0];
  /** Constant empty short array */
  public static final short [] EMPTY_SHORT_ARRAY = new short [0];
  /** Constant empty boolean array */
  public static final Boolean [] EMPTY_BOOLEAN_OBJ_ARRAY = new Boolean [0];
  /** Constant empty byte array */
  public static final Byte [] EMPTY_BYTE_OBJ_ARRAY = new Byte [0];
  /** Constant empty char array */
  public static final Character [] EMPTY_CHAR_OBJ_ARRAY = new Character [0];
  /** Constant empty double array */
  public static final Double [] EMPTY_DOUBLE_OBJ_ARRAY = new Double [0];
  /** Constant empty float array */
  public static final Float [] EMPTY_FLOAT_OBJ_ARRAY = new Float [0];
  /** Constant empty int array */
  public static final Integer [] EMPTY_INT_OBJ_ARRAY = new Integer [0];
  /** Constant empty long array */
  public static final Long [] EMPTY_LONG_OBJ_ARRAY = new Long [0];
  /** Constant empty short array */
  public static final Short [] EMPTY_SHORT_OBJ_ARRAY = new Short [0];
  /** Constant empty Object array */
  public static final Object [] EMPTY_OBJECT_ARRAY = new Object [0];
  /** Constant empty String array */
  public static final String [] EMPTY_STRING_ARRAY = new String [0];

  @PresentForCodeCoverage
  private static final ArrayHelper s_aInstance = new ArrayHelper ();

  private ArrayHelper ()
  {}

  /**
   * Get the component type of the array (the type of which the array is made
   * up)
   *
   * @param <ELEMENTTYPE>
   *        The component type of the array
   * @param aArray
   *        The array to get the type from. May not be <code>null</code>.
   * @return The class that determines a single element of the array.
   */
  @Nonnull
  public static <ELEMENTTYPE> Class <? extends ELEMENTTYPE> getComponentType (@Nonnull final ELEMENTTYPE [] aArray)
  {
    ValueEnforcer.notNull (aArray, "Array");
    final Class <?> aComponentType = aArray.getClass ().getComponentType ();
    return GenericReflection.<Class <?>, Class <? extends ELEMENTTYPE>> uncheckedCast (aComponentType);
  }

  /**
   * Check if the passed object is an array or not.
   *
   * @param aObject
   *        The object to be checked. May be <code>null</code>.
   * @return <code>true</code> if the passed object is not <code>null</code> and
   *         represents an array.
   */
  public static boolean isArray (@Nullable final Object aObject)
  {
    return aObject != null && ClassHelper.isArrayClass (aObject.getClass ());
  }

  /**
   * @param aArray
   *        The array who's size is to be queried. May be <code>null</code>.
   * @return 0 if the passed array is <code>null</code> - it's length otherwise.
   */
  @Nonnegative
  public static int getSize (@Nullable final boolean... aArray)
  {
    return aArray == null ? 0 : aArray.length;
  }

  /**
   * @param aArray
   *        The array who's size is to be queried. May be <code>null</code>.
   * @return 0 if the passed array is <code>null</code> - it's length otherwise.
   */
  @Nonnegative
  public static int getSize (@Nullable final byte... aArray)
  {
    return aArray == null ? 0 : aArray.length;
  }

  /**
   * @param aArray
   *        The array who's size is to be queried. May be <code>null</code>.
   * @return 0 if the passed array is <code>null</code> - it's length otherwise.
   */
  @Nonnegative
  public static int getSize (@Nullable final char... aArray)
  {
    return aArray == null ? 0 : aArray.length;
  }

  /**
   * @param aArray
   *        The array who's size is to be queried. May be <code>null</code>.
   * @return 0 if the passed array is <code>null</code> - it's length otherwise.
   */
  @Nonnegative
  public static int getSize (@Nullable final double... aArray)
  {
    return aArray == null ? 0 : aArray.length;
  }

  /**
   * @param aArray
   *        The array who's size is to be queried. May be <code>null</code>.
   * @return 0 if the passed array is <code>null</code> - it's length otherwise.
   */
  @Nonnegative
  public static int getSize (@Nullable final float... aArray)
  {
    return aArray == null ? 0 : aArray.length;
  }

  /**
   * @param aArray
   *        The array who's size is to be queried. May be <code>null</code>.
   * @return 0 if the passed array is <code>null</code> - it's length otherwise.
   */
  @Nonnegative
  public static int getSize (@Nullable final int... aArray)
  {
    return aArray == null ? 0 : aArray.length;
  }

  /**
   * @param aArray
   *        The array who's size is to be queried. May be <code>null</code>.
   * @return 0 if the passed array is <code>null</code> - it's length otherwise.
   */
  @Nonnegative
  public static int getSize (@Nullable final long... aArray)
  {
    return aArray == null ? 0 : aArray.length;
  }

  /**
   * @param aArray
   *        The array who's size is to be queried. May be <code>null</code>.
   * @return 0 if the passed array is <code>null</code> - it's length otherwise.
   */
  @Nonnegative
  public static int getSize (@Nullable final short... aArray)
  {
    return aArray == null ? 0 : aArray.length;
  }

  /**
   * @param <ELEMENTTYPE>
   *        Array element type
   * @param aArray
   *        The array who's size is to be queried. May be <code>null</code>.
   * @return 0 if the passed array is <code>null</code> - it's length otherwise.
   */
  @Nonnegative
  @SafeVarargs
  public static <ELEMENTTYPE> int getSize (@Nullable final ELEMENTTYPE... aArray)
  {
    return aArray == null ? 0 : aArray.length;
  }

  /**
   * @param aArray
   *        The array to be queried if it is empty. May be <code>null</code>.
   * @return <code>true</code> if the passed array is <code>null</code> or
   *         empty.
   */
  public static boolean isEmpty (@Nullable final boolean... aArray)
  {
    return getSize (aArray) == 0;
  }

  /**
   * @param aArray
   *        The array to be queried if it is empty. May be <code>null</code>.
   * @return <code>true</code> if the passed array is <code>null</code> or
   *         empty.
   */
  public static boolean isEmpty (@Nullable final byte... aArray)
  {
    return getSize (aArray) == 0;
  }

  /**
   * @param aArray
   *        The array to be queried if it is empty. May be <code>null</code>.
   * @return <code>true</code> if the passed array is <code>null</code> or
   *         empty.
   */
  public static boolean isEmpty (@Nullable final char... aArray)
  {
    return getSize (aArray) == 0;
  }

  /**
   * @param aArray
   *        The array to be queried if it is empty. May be <code>null</code>.
   * @return <code>true</code> if the passed array is <code>null</code> or
   *         empty.
   */
  public static boolean isEmpty (@Nullable final double... aArray)
  {
    return getSize (aArray) == 0;
  }

  /**
   * @param aArray
   *        The array to be queried if it is empty. May be <code>null</code>.
   * @return <code>true</code> if the passed array is <code>null</code> or
   *         empty.
   */
  public static boolean isEmpty (@Nullable final float... aArray)
  {
    return getSize (aArray) == 0;
  }

  /**
   * @param aArray
   *        The array to be queried if it is empty. May be <code>null</code>.
   * @return <code>true</code> if the passed array is <code>null</code> or
   *         empty.
   */
  public static boolean isEmpty (@Nullable final int... aArray)
  {
    return getSize (aArray) == 0;
  }

  /**
   * @param aArray
   *        The array to be queried if it is empty. May be <code>null</code>.
   * @return <code>true</code> if the passed array is <code>null</code> or
   *         empty.
   */
  public static boolean isEmpty (@Nullable final long... aArray)
  {
    return getSize (aArray) == 0;
  }

  /**
   * @param aArray
   *        The array to be queried if it is empty. May be <code>null</code>.
   * @return <code>true</code> if the passed array is <code>null</code> or
   *         empty.
   */
  public static boolean isEmpty (@Nullable final short... aArray)
  {
    return getSize (aArray) == 0;
  }

  /**
   * @param <ELEMENTTYPE>
   *        Array element type
   * @param aArray
   *        The array to be queried if it is empty. May be <code>null</code>.
   * @return <code>true</code> if the passed array is <code>null</code> or
   *         empty.
   */
  @SafeVarargs
  public static <ELEMENTTYPE> boolean isEmpty (@Nullable final ELEMENTTYPE... aArray)
  {
    return getSize (aArray) == 0;
  }

  /**
   * @param aArray
   *        The array to be queried if it is empty. May be <code>null</code>.
   * @return <code>false</code> if the passed array is <code>null</code> or
   *         empty.
   */
  public static boolean isNotEmpty (@Nullable final boolean... aArray)
  {
    return getSize (aArray) > 0;
  }

  /**
   * @param aArray
   *        The array to be queried if it is empty. May be <code>null</code>.
   * @return <code>false</code> if the passed array is <code>null</code> or
   *         empty.
   */
  public static boolean isNotEmpty (@Nullable final byte... aArray)
  {
    return getSize (aArray) > 0;
  }

  /**
   * @param aArray
   *        The array to be queried if it is empty. May be <code>null</code>.
   * @return <code>false</code> if the passed array is <code>null</code> or
   *         empty.
   */
  public static boolean isNotEmpty (@Nullable final char... aArray)
  {
    return getSize (aArray) > 0;
  }

  /**
   * @param aArray
   *        The array to be queried if it is empty. May be <code>null</code>.
   * @return <code>false</code> if the passed array is <code>null</code> or
   *         empty.
   */
  public static boolean isNotEmpty (@Nullable final double... aArray)
  {
    return getSize (aArray) > 0;
  }

  /**
   * @param aArray
   *        The array to be queried if it is empty. May be <code>null</code>.
   * @return <code>false</code> if the passed array is <code>null</code> or
   *         empty.
   */
  public static boolean isNotEmpty (@Nullable final float... aArray)
  {
    return getSize (aArray) > 0;
  }

  /**
   * @param aArray
   *        The array to be queried if it is empty. May be <code>null</code>.
   * @return <code>false</code> if the passed array is <code>null</code> or
   *         empty.
   */
  public static boolean isNotEmpty (@Nullable final int... aArray)
  {
    return getSize (aArray) > 0;
  }

  /**
   * @param aArray
   *        The array to be queried if it is empty. May be <code>null</code>.
   * @return <code>false</code> if the passed array is <code>null</code> or
   *         empty.
   */
  public static boolean isNotEmpty (@Nullable final long... aArray)
  {
    return getSize (aArray) > 0;
  }

  /**
   * @param aArray
   *        The array to be queried if it is empty. May be <code>null</code>.
   * @return <code>false</code> if the passed array is <code>null</code> or
   *         empty.
   */
  public static boolean isNotEmpty (@Nullable final short... aArray)
  {
    return getSize (aArray) > 0;
  }

  /**
   * @param <ELEMENTTYPE>
   *        Array element type
   * @param aArray
   *        The array to be queried if it is empty. May be <code>null</code>.
   * @return <code>false</code> if the passed array is <code>null</code> or
   *         empty.
   */
  @SafeVarargs
  public static <ELEMENTTYPE> boolean isNotEmpty (@Nullable final ELEMENTTYPE... aArray)
  {
    return getSize (aArray) > 0;
  }

  /**
   * Get the index of the passed search value in the passed value array.
   *
   * @param <ELEMENTTYPE>
   *        Array element type
   * @param aValues
   *        The value array to be searched. May be <code>null</code>.
   * @param aSearchValue
   *        The value to be searched. May be <code>null</code>.
   * @return <code>-1</code> if the searched value is not contained, a value
   *         &ge; 0 otherwise.
   */
  public static <ELEMENTTYPE> int getFirstIndex (@Nullable final ELEMENTTYPE [] aValues,
                                                 @Nullable final ELEMENTTYPE aSearchValue)
  {
    final int nLength = getSize (aValues);
    if (nLength > 0)
      for (int nIndex = 0; nIndex < nLength; ++nIndex)
        if (EqualsHelper.equals (aValues[nIndex], aSearchValue))
          return nIndex;
    return CGlobal.ILLEGAL_UINT;
  }

  /**
   * Get the index of the passed search value in the passed value array.
   *
   * @param aValues
   *        The value array to be searched. May be <code>null</code>.
   * @param aSearchValue
   *        The value to be searched. May be <code>null</code>.
   * @return <code>-1</code> if the searched value is not contained, a value
   *         &ge; 0 otherwise.
   */
  public static int getFirstIndex (@Nullable final boolean [] aValues, final boolean aSearchValue)
  {
    final int nLength = getSize (aValues);
    if (nLength > 0)
      for (int nIndex = 0; nIndex < nLength; ++nIndex)
        if (EqualsHelper.equals (aValues[nIndex], aSearchValue))
          return nIndex;
    return CGlobal.ILLEGAL_UINT;
  }

  /**
   * Get the index of the passed search value in the passed value array.
   *
   * @param aValues
   *        The value array to be searched. May be <code>null</code>.
   * @param aSearchValue
   *        The value to be searched. May be <code>null</code>.
   * @return <code>-1</code> if the searched value is not contained, a value
   *         &ge; 0 otherwise.
   */
  public static int getFirstIndex (@Nullable final byte [] aValues, final byte aSearchValue)
  {
    final int nLength = getSize (aValues);
    if (nLength > 0)
      for (int nIndex = 0; nIndex < nLength; ++nIndex)
        if (EqualsHelper.equals (aValues[nIndex], aSearchValue))
          return nIndex;
    return CGlobal.ILLEGAL_UINT;
  }

  /**
   * Get the index of the passed search value in the passed value array.
   *
   * @param aValues
   *        The value array to be searched. May be <code>null</code>.
   * @param aSearchValue
   *        The value to be searched. May be <code>null</code>.
   * @return <code>-1</code> if the searched value is not contained, a value
   *         &ge; 0 otherwise.
   */
  public static int getFirstIndex (@Nullable final char [] aValues, final char aSearchValue)
  {
    final int nLength = getSize (aValues);
    if (nLength > 0)
      for (int nIndex = 0; nIndex < nLength; ++nIndex)
        if (EqualsHelper.equals (aValues[nIndex], aSearchValue))
          return nIndex;
    return CGlobal.ILLEGAL_UINT;
  }

  /**
   * Get the index of the passed search value in the passed value array.
   *
   * @param aValues
   *        The value array to be searched. May be <code>null</code>.
   * @param aSearchValue
   *        The value to be searched. May be <code>null</code>.
   * @return <code>-1</code> if the searched value is not contained, a value
   *         &ge; 0 otherwise.
   */
  public static int getFirstIndex (@Nullable final double [] aValues, final double aSearchValue)
  {
    final int nLength = getSize (aValues);
    if (nLength > 0)
      for (int nIndex = 0; nIndex < nLength; ++nIndex)
        if (EqualsHelper.equals (aValues[nIndex], aSearchValue))
          return nIndex;
    return CGlobal.ILLEGAL_UINT;
  }

  /**
   * Get the index of the passed search value in the passed value array.
   *
   * @param aValues
   *        The value array to be searched. May be <code>null</code>.
   * @param aSearchValue
   *        The value to be searched. May be <code>null</code>.
   * @return <code>-1</code> if the searched value is not contained, a value
   *         &ge; 0 otherwise.
   */
  public static int getFirstIndex (@Nullable final float [] aValues, final float aSearchValue)
  {
    final int nLength = getSize (aValues);
    if (nLength > 0)
      for (int nIndex = 0; nIndex < nLength; ++nIndex)
        if (EqualsHelper.equals (aValues[nIndex], aSearchValue))
          return nIndex;
    return CGlobal.ILLEGAL_UINT;
  }

  /**
   * Get the index of the passed search value in the passed value array.
   *
   * @param aValues
   *        The value array to be searched. May be <code>null</code>.
   * @param aSearchValue
   *        The value to be searched. May be <code>null</code>.
   * @return <code>-1</code> if the searched value is not contained, a value
   *         &ge; 0 otherwise.
   */
  public static int getFirstIndex (@Nullable final int [] aValues, final int aSearchValue)
  {
    final int nLength = getSize (aValues);
    if (nLength > 0)
      for (int nIndex = 0; nIndex < nLength; ++nIndex)
        if (EqualsHelper.equals (aValues[nIndex], aSearchValue))
          return nIndex;
    return CGlobal.ILLEGAL_UINT;
  }

  /**
   * Get the index of the passed search value in the passed value array.
   *
   * @param aValues
   *        The value array to be searched. May be <code>null</code>.
   * @param aSearchValue
   *        The value to be searched. May be <code>null</code>.
   * @return <code>-1</code> if the searched value is not contained, a value
   *         &ge; 0 otherwise.
   */
  public static int getFirstIndex (@Nullable final long [] aValues, final long aSearchValue)
  {
    final int nLength = getSize (aValues);
    if (nLength > 0)
      for (int nIndex = 0; nIndex < nLength; ++nIndex)
        if (EqualsHelper.equals (aValues[nIndex], aSearchValue))
          return nIndex;
    return CGlobal.ILLEGAL_UINT;
  }

  /**
   * Get the index of the passed search value in the passed value array.
   *
   * @param aValues
   *        The value array to be searched. May be <code>null</code>.
   * @param aSearchValue
   *        The value to be searched. May be <code>null</code>.
   * @return <code>-1</code> if the searched value is not contained, a value
   *         &ge; 0 otherwise.
   */
  public static int getFirstIndex (@Nullable final short [] aValues, final short aSearchValue)
  {
    final int nLength = getSize (aValues);
    if (nLength > 0)
      for (int nIndex = 0; nIndex < nLength; ++nIndex)
        if (EqualsHelper.equals (aValues[nIndex], aSearchValue))
          return nIndex;
    return CGlobal.ILLEGAL_UINT;
  }

  /**
   * Get the index of the passed search value in the passed value array.
   *
   * @param <ELEMENTTYPE>
   *        Array element type
   * @param aValues
   *        The value array to be searched. May be <code>null</code>.
   * @param aSearchValue
   *        The value to be searched. May be <code>null</code>.
   * @return <code>-1</code> if the searched value is not contained, a value
   *         &ge; 0 otherwise.
   */
  public static <ELEMENTTYPE> int getLastIndex (@Nullable final ELEMENTTYPE [] aValues,
                                                @Nullable final ELEMENTTYPE aSearchValue)
  {
    final int nLength = getSize (aValues);
    if (nLength > 0)
      for (int nIndex = nLength - 1; nIndex >= 0; --nIndex)
        if (EqualsHelper.equals (aValues[nIndex], aSearchValue))
          return nIndex;
    return CGlobal.ILLEGAL_UINT;
  }

  /**
   * Get the index of the passed search value in the passed value array.
   *
   * @param aValues
   *        The value array to be searched. May be <code>null</code>.
   * @param aSearchValue
   *        The value to be searched. May be <code>null</code>.
   * @return <code>-1</code> if the searched value is not contained, a value
   *         &ge; 0 otherwise.
   */
  public static int getLastIndex (@Nullable final boolean [] aValues, final boolean aSearchValue)
  {
    final int nLength = getSize (aValues);
    if (nLength > 0)
      for (int nIndex = nLength - 1; nIndex >= 0; --nIndex)
        if (EqualsHelper.equals (aValues[nIndex], aSearchValue))
          return nIndex;
    return CGlobal.ILLEGAL_UINT;
  }

  /**
   * Get the index of the passed search value in the passed value array.
   *
   * @param aValues
   *        The value array to be searched. May be <code>null</code>.
   * @param aSearchValue
   *        The value to be searched. May be <code>null</code>.
   * @return <code>-1</code> if the searched value is not contained, a value
   *         &ge; 0 otherwise.
   */
  public static int getLastIndex (@Nullable final byte [] aValues, final byte aSearchValue)
  {
    final int nLength = getSize (aValues);
    if (nLength > 0)
      for (int nIndex = nLength - 1; nIndex >= 0; --nIndex)
        if (EqualsHelper.equals (aValues[nIndex], aSearchValue))
          return nIndex;
    return CGlobal.ILLEGAL_UINT;
  }

  /**
   * Get the index of the passed search value in the passed value array.
   *
   * @param aValues
   *        The value array to be searched. May be <code>null</code>.
   * @param aSearchValue
   *        The value to be searched. May be <code>null</code>.
   * @return <code>-1</code> if the searched value is not contained, a value
   *         &ge; 0 otherwise.
   */
  public static int getLastIndex (@Nullable final char [] aValues, final char aSearchValue)
  {
    final int nLength = getSize (aValues);
    if (nLength > 0)
      for (int nIndex = nLength - 1; nIndex >= 0; --nIndex)
        if (EqualsHelper.equals (aValues[nIndex], aSearchValue))
          return nIndex;
    return CGlobal.ILLEGAL_UINT;
  }

  /**
   * Get the index of the passed search value in the passed value array.
   *
   * @param aValues
   *        The value array to be searched. May be <code>null</code>.
   * @param aSearchValue
   *        The value to be searched. May be <code>null</code>.
   * @return <code>-1</code> if the searched value is not contained, a value
   *         &ge; 0 otherwise.
   */
  public static int getLastIndex (@Nullable final double [] aValues, final double aSearchValue)
  {
    final int nLength = getSize (aValues);
    if (nLength > 0)
      for (int nIndex = nLength - 1; nIndex >= 0; --nIndex)
        if (EqualsHelper.equals (aValues[nIndex], aSearchValue))
          return nIndex;
    return CGlobal.ILLEGAL_UINT;
  }

  /**
   * Get the index of the passed search value in the passed value array.
   *
   * @param aValues
   *        The value array to be searched. May be <code>null</code>.
   * @param aSearchValue
   *        The value to be searched. May be <code>null</code>.
   * @return <code>-1</code> if the searched value is not contained, a value
   *         &ge; 0 otherwise.
   */
  public static int getLastIndex (@Nullable final float [] aValues, final float aSearchValue)
  {
    final int nLength = getSize (aValues);
    if (nLength > 0)
      for (int nIndex = nLength - 1; nIndex >= 0; --nIndex)
        if (EqualsHelper.equals (aValues[nIndex], aSearchValue))
          return nIndex;
    return CGlobal.ILLEGAL_UINT;
  }

  /**
   * Get the index of the passed search value in the passed value array.
   *
   * @param aValues
   *        The value array to be searched. May be <code>null</code>.
   * @param aSearchValue
   *        The value to be searched. May be <code>null</code>.
   * @return <code>-1</code> if the searched value is not contained, a value
   *         &ge; 0 otherwise.
   */
  public static int getLastIndex (@Nullable final int [] aValues, final int aSearchValue)
  {
    final int nLength = getSize (aValues);
    if (nLength > 0)
      for (int nIndex = nLength - 1; nIndex >= 0; --nIndex)
        if (EqualsHelper.equals (aValues[nIndex], aSearchValue))
          return nIndex;
    return CGlobal.ILLEGAL_UINT;
  }

  /**
   * Get the index of the passed search value in the passed value array.
   *
   * @param aValues
   *        The value array to be searched. May be <code>null</code>.
   * @param aSearchValue
   *        The value to be searched. May be <code>null</code>.
   * @return <code>-1</code> if the searched value is not contained, a value
   *         &ge; 0 otherwise.
   */
  public static int getLastIndex (@Nullable final long [] aValues, final long aSearchValue)
  {
    final int nLength = getSize (aValues);
    if (nLength > 0)
      for (int nIndex = nLength - 1; nIndex >= 0; --nIndex)
        if (EqualsHelper.equals (aValues[nIndex], aSearchValue))
          return nIndex;
    return CGlobal.ILLEGAL_UINT;
  }

  /**
   * Get the index of the passed search value in the passed value array.
   *
   * @param aValues
   *        The value array to be searched. May be <code>null</code>.
   * @param aSearchValue
   *        The value to be searched. May be <code>null</code>.
   * @return <code>-1</code> if the searched value is not contained, a value
   *         &ge; 0 otherwise.
   */
  public static int getLastIndex (@Nullable final short [] aValues, final short aSearchValue)
  {
    final int nLength = getSize (aValues);
    if (nLength > 0)
      for (int nIndex = nLength - 1; nIndex >= 0; --nIndex)
        if (EqualsHelper.equals (aValues[nIndex], aSearchValue))
          return nIndex;
    return CGlobal.ILLEGAL_UINT;
  }

  /**
   * Check if the passed search value is contained in the passed value array.
   *
   * @param <ELEMENTTYPE>
   *        Array element type
   * @param aValues
   *        The value array to be searched. May be <code>null</code>.
   * @param aSearchValue
   *        The value to be searched. May be <code>null</code>.
   * @return <code>true</code> if the value array is not empty and the search
   *         value is contained - false otherwise.
   */
  public static <ELEMENTTYPE> boolean contains (@Nullable final ELEMENTTYPE [] aValues,
                                                @Nullable final ELEMENTTYPE aSearchValue)
  {
    return getFirstIndex (aValues, aSearchValue) >= 0;
  }

  /**
   * Check if the passed search value is contained in the passed value array.
   *
   * @param aValues
   *        The value array to be searched. May be <code>null</code>.
   * @param aSearchValue
   *        The value to be searched. May be <code>null</code>.
   * @return <code>true</code> if the value array is not empty and the search
   *         value is contained - false otherwise.
   */
  public static boolean contains (@Nullable final boolean [] aValues, final boolean aSearchValue)
  {
    return getFirstIndex (aValues, aSearchValue) >= 0;
  }

  /**
   * Check if the passed search value is contained in the passed value array.
   *
   * @param aValues
   *        The value array to be searched. May be <code>null</code>.
   * @param aSearchValue
   *        The value to be searched. May be <code>null</code>.
   * @return <code>true</code> if the value array is not empty and the search
   *         value is contained - false otherwise.
   */
  public static boolean contains (@Nullable final byte [] aValues, final byte aSearchValue)
  {
    return getFirstIndex (aValues, aSearchValue) >= 0;
  }

  /**
   * Check if the passed search value is contained in the passed value array.
   *
   * @param aValues
   *        The value array to be searched. May be <code>null</code>.
   * @param aSearchValue
   *        The value to be searched. May be <code>null</code>.
   * @return <code>true</code> if the value array is not empty and the search
   *         value is contained - false otherwise.
   */
  public static boolean contains (@Nullable final char [] aValues, final char aSearchValue)
  {
    return getFirstIndex (aValues, aSearchValue) >= 0;
  }

  /**
   * Check if the passed search value is contained in the passed value array.
   *
   * @param aValues
   *        The value array to be searched. May be <code>null</code>.
   * @param aSearchValue
   *        The value to be searched. May be <code>null</code>.
   * @return <code>true</code> if the value array is not empty and the search
   *         value is contained - false otherwise.
   */
  public static boolean contains (@Nullable final double [] aValues, final double aSearchValue)
  {
    return getFirstIndex (aValues, aSearchValue) >= 0;
  }

  /**
   * Check if the passed search value is contained in the passed value array.
   *
   * @param aValues
   *        The value array to be searched. May be <code>null</code>.
   * @param aSearchValue
   *        The value to be searched. May be <code>null</code>.
   * @return <code>true</code> if the value array is not empty and the search
   *         value is contained - false otherwise.
   */
  public static boolean contains (@Nullable final float [] aValues, final float aSearchValue)
  {
    return getFirstIndex (aValues, aSearchValue) >= 0;
  }

  /**
   * Check if the passed search value is contained in the passed value array.
   *
   * @param aValues
   *        The value array to be searched. May be <code>null</code>.
   * @param aSearchValue
   *        The value to be searched. May be <code>null</code>.
   * @return <code>true</code> if the value array is not empty and the search
   *         value is contained - false otherwise.
   */
  public static boolean contains (@Nullable final int [] aValues, final int aSearchValue)
  {
    return getFirstIndex (aValues, aSearchValue) >= 0;
  }

  /**
   * Check if the passed search value is contained in the passed value array.
   *
   * @param aValues
   *        The value array to be searched. May be <code>null</code>.
   * @param aSearchValue
   *        The value to be searched. May be <code>null</code>.
   * @return <code>true</code> if the value array is not empty and the search
   *         value is contained - false otherwise.
   */
  public static boolean contains (@Nullable final long [] aValues, final long aSearchValue)
  {
    return getFirstIndex (aValues, aSearchValue) >= 0;
  }

  /**
   * Check if the passed search value is contained in the passed value array.
   *
   * @param aValues
   *        The value array to be searched. May be <code>null</code>.
   * @param aSearchValue
   *        The value to be searched. May be <code>null</code>.
   * @return <code>true</code> if the value array is not empty and the search
   *         value is contained - false otherwise.
   */
  public static boolean contains (@Nullable final short [] aValues, final short aSearchValue)
  {
    return getFirstIndex (aValues, aSearchValue) >= 0;
  }

  /**
   * Get the first element of the array or the passed default if the passed
   * array is empty.
   *
   * @param aArray
   *        The array who's first element is to be retrieved. May be
   *        <code>null</code> or empty.
   * @param aDefaultValue
   *        The default value to be returned if the array is empty
   * @return the first element if the passed array is not empty, the default
   *         value if the passed array is empty.
   */
  public static boolean getFirst (@Nullable final boolean [] aArray, final boolean aDefaultValue)
  {
    return isEmpty (aArray) ? aDefaultValue : aArray[0];
  }

  /**
   * Get the first element of the array or the passed default if the passed
   * array is empty.
   *
   * @param aArray
   *        The array who's first element is to be retrieved. May be
   *        <code>null</code> or empty.
   * @param aDefaultValue
   *        The default value to be returned if the array is empty
   * @return the first element if the passed array is not empty, the default
   *         value if the passed array is empty.
   */
  public static byte getFirst (@Nullable final byte [] aArray, final byte aDefaultValue)
  {
    return isEmpty (aArray) ? aDefaultValue : aArray[0];
  }

  /**
   * Get the first element of the array or the passed default if the passed
   * array is empty.
   *
   * @param aArray
   *        The array who's first element is to be retrieved. May be
   *        <code>null</code> or empty.
   * @param aDefaultValue
   *        The default value to be returned if the array is empty
   * @return the first element if the passed array is not empty, the default
   *         value if the passed array is empty.
   */
  public static char getFirst (@Nullable final char [] aArray, final char aDefaultValue)
  {
    return isEmpty (aArray) ? aDefaultValue : aArray[0];
  }

  /**
   * Get the first element of the array or the passed default if the passed
   * array is empty.
   *
   * @param aArray
   *        The array who's first element is to be retrieved. May be
   *        <code>null</code> or empty.
   * @param aDefaultValue
   *        The default value to be returned if the array is empty
   * @return the first element if the passed array is not empty, the default
   *         value if the passed array is empty.
   */
  public static double getFirst (@Nullable final double [] aArray, final double aDefaultValue)
  {
    return isEmpty (aArray) ? aDefaultValue : aArray[0];
  }

  /**
   * Get the first element of the array or the passed default if the passed
   * array is empty.
   *
   * @param aArray
   *        The array who's first element is to be retrieved. May be
   *        <code>null</code> or empty.
   * @param aDefaultValue
   *        The default value to be returned if the array is empty
   * @return the first element if the passed array is not empty, the default
   *         value if the passed array is empty.
   */
  public static float getFirst (@Nullable final float [] aArray, final float aDefaultValue)
  {
    return isEmpty (aArray) ? aDefaultValue : aArray[0];
  }

  /**
   * Get the first element of the array or the passed default if the passed
   * array is empty.
   *
   * @param aArray
   *        The array who's first element is to be retrieved. May be
   *        <code>null</code> or empty.
   * @param aDefaultValue
   *        The default value to be returned if the array is empty
   * @return the first element if the passed array is not empty, the default
   *         value if the passed array is empty.
   */
  public static int getFirst (@Nullable final int [] aArray, final int aDefaultValue)
  {
    return isEmpty (aArray) ? aDefaultValue : aArray[0];
  }

  /**
   * Get the first element of the array or the passed default if the passed
   * array is empty.
   *
   * @param aArray
   *        The array who's first element is to be retrieved. May be
   *        <code>null</code> or empty.
   * @param aDefaultValue
   *        The default value to be returned if the array is empty
   * @return the first element if the passed array is not empty, the default
   *         value if the passed array is empty.
   */
  public static long getFirst (@Nullable final long [] aArray, final long aDefaultValue)
  {
    return isEmpty (aArray) ? aDefaultValue : aArray[0];
  }

  /**
   * Get the first element of the array or the passed default if the passed
   * array is empty.
   *
   * @param aArray
   *        The array who's first element is to be retrieved. May be
   *        <code>null</code> or empty.
   * @param aDefaultValue
   *        The default value to be returned if the array is empty
   * @return the first element if the passed array is not empty, the default
   *         value if the passed array is empty.
   */
  public static short getFirst (@Nullable final short [] aArray, final short aDefaultValue)
  {
    return isEmpty (aArray) ? aDefaultValue : aArray[0];
  }

  /**
   * Get the first element of the array or <code>null</code> if the passed array
   * is empty.
   *
   * @param <ELEMENTTYPE>
   *        Array element type
   * @param aArray
   *        The array who's first element is to be retrieved. May be
   *        <code>null</code> or empty.
   * @return <code>null</code> if the passed array is <code>null</code> or empty
   *         - the first element otherwise (may also be <code>null</code>).
   */
  @Nullable
  @SafeVarargs
  public static <ELEMENTTYPE> ELEMENTTYPE getFirst (@Nullable final ELEMENTTYPE... aArray)
  {
    return getFirst (aArray, null);
  }

  /**
   * Get the first element of the array or the passed default if the passed
   * array is empty.
   *
   * @param <ELEMENTTYPE>
   *        Array element type
   * @param aArray
   *        The array who's first element is to be retrieved. May be
   *        <code>null</code> or empty.
   * @param aDefaultValue
   *        The default value to be returned if the array is empty
   * @return the first element if the passed array is not empty, the default
   *         value if the passed array is empty.
   */
  @Nullable
  public static <ELEMENTTYPE> ELEMENTTYPE getFirst (@Nullable final ELEMENTTYPE [] aArray,
                                                    @Nullable final ELEMENTTYPE aDefaultValue)
  {
    return isEmpty (aArray) ? aDefaultValue : aArray[0];
  }

  /**
   * Get the last element of the array or the passed default if the passed array
   * is empty.
   *
   * @param aArray
   *        The array who's last element is to be retrieved. May be
   *        <code>null</code> or empty.
   * @param aDefaultValue
   *        The default value to be returned if the array is empty
   * @return the last element if the passed array is not empty, the default
   *         value if the passed array is empty.
   */
  public static boolean getLast (@Nullable final boolean [] aArray, final boolean aDefaultValue)
  {
    final int nSize = getSize (aArray);
    return nSize == 0 ? aDefaultValue : aArray[nSize - 1];
  }

  /**
   * Get the last element of the array or the passed default if the passed array
   * is empty.
   *
   * @param aArray
   *        The array who's last element is to be retrieved. May be
   *        <code>null</code> or empty.
   * @param aDefaultValue
   *        The default value to be returned if the array is empty
   * @return the last element if the passed array is not empty, the default
   *         value if the passed array is empty.
   */
  public static byte getLast (@Nullable final byte [] aArray, final byte aDefaultValue)
  {
    final int nSize = getSize (aArray);
    return nSize == 0 ? aDefaultValue : aArray[nSize - 1];
  }

  /**
   * Get the last element of the array or the passed default if the passed array
   * is empty.
   *
   * @param aArray
   *        The array who's last element is to be retrieved. May be
   *        <code>null</code> or empty.
   * @param aDefaultValue
   *        The default value to be returned if the array is empty
   * @return the last element if the passed array is not empty, the default
   *         value if the passed array is empty.
   */
  public static char getLast (@Nullable final char [] aArray, final char aDefaultValue)
  {
    final int nSize = getSize (aArray);
    return nSize == 0 ? aDefaultValue : aArray[nSize - 1];
  }

  /**
   * Get the last element of the array or the passed default if the passed array
   * is empty.
   *
   * @param aArray
   *        The array who's last element is to be retrieved. May be
   *        <code>null</code> or empty.
   * @param aDefaultValue
   *        The default value to be returned if the array is empty
   * @return the last element if the passed array is not empty, the default
   *         value if the passed array is empty.
   */
  public static double getLast (@Nullable final double [] aArray, final double aDefaultValue)
  {
    final int nSize = getSize (aArray);
    return nSize == 0 ? aDefaultValue : aArray[nSize - 1];
  }

  /**
   * Get the last element of the array or the passed default if the passed array
   * is empty.
   *
   * @param aArray
   *        The array who's last element is to be retrieved. May be
   *        <code>null</code> or empty.
   * @param aDefaultValue
   *        The default value to be returned if the array is empty
   * @return the last element if the passed array is not empty, the default
   *         value if the passed array is empty.
   */
  public static float getLast (@Nullable final float [] aArray, final float aDefaultValue)
  {
    final int nSize = getSize (aArray);
    return nSize == 0 ? aDefaultValue : aArray[nSize - 1];
  }

  /**
   * Get the last element of the array or the passed default if the passed array
   * is empty.
   *
   * @param aArray
   *        The array who's last element is to be retrieved. May be
   *        <code>null</code> or empty.
   * @param aDefaultValue
   *        The default value to be returned if the array is empty
   * @return the last element if the passed array is not empty, the default
   *         value if the passed array is empty.
   */
  public static int getLast (@Nullable final int [] aArray, final int aDefaultValue)
  {
    final int nSize = getSize (aArray);
    return nSize == 0 ? aDefaultValue : aArray[nSize - 1];
  }

  /**
   * Get the last element of the array or the passed default if the passed array
   * is empty.
   *
   * @param aArray
   *        The array who's last element is to be retrieved. May be
   *        <code>null</code> or empty.
   * @param aDefaultValue
   *        The default value to be returned if the array is empty
   * @return the last element if the passed array is not empty, the default
   *         value if the passed array is empty.
   */
  public static long getLast (@Nullable final long [] aArray, final long aDefaultValue)
  {
    final int nSize = getSize (aArray);
    return nSize == 0 ? aDefaultValue : aArray[nSize - 1];
  }

  /**
   * Get the last element of the array or the passed default if the passed array
   * is empty.
   *
   * @param aArray
   *        The array who's last element is to be retrieved. May be
   *        <code>null</code> or empty.
   * @param aDefaultValue
   *        The default value to be returned if the array is empty
   * @return the last element if the passed array is not empty, the default
   *         value if the passed array is empty.
   */
  public static short getLast (@Nullable final short [] aArray, final short aDefaultValue)
  {
    final int nSize = getSize (aArray);
    return nSize == 0 ? aDefaultValue : aArray[nSize - 1];
  }

  /**
   * Get the last element of the array or <code>null</code> if the passed array
   * is empty.
   *
   * @param <ELEMENTTYPE>
   *        Array element type
   * @param aArray
   *        The array who's last element is to be retrieved. May be
   *        <code>null</code> or empty.
   * @return <code>null</code> if the passed array is <code>null</code> or empty
   *         - the last element otherwise (may also be <code>null</code>).
   */
  @Nullable
  @SafeVarargs
  public static <ELEMENTTYPE> ELEMENTTYPE getLast (@Nullable final ELEMENTTYPE... aArray)
  {
    return getLast (aArray, null);
  }

  /**
   * Get the last element of the array or the passed default if the passed array
   * is empty.
   *
   * @param <ELEMENTTYPE>
   *        Array element type
   * @param aArray
   *        The array who's last element is to be retrieved. May be
   *        <code>null</code> or empty.
   * @param aDefaultValue
   *        The default value to be returned if the array is empty
   * @return the last element if the passed array is not empty, the default
   *         value if the passed array is empty.
   */
  @Nullable
  public static <ELEMENTTYPE> ELEMENTTYPE getLast (@Nullable final ELEMENTTYPE [] aArray,
                                                   @Nullable final ELEMENTTYPE aDefaultValue)
  {
    final int nSize = getSize (aArray);
    return nSize == 0 ? aDefaultValue : aArray[nSize - 1];
  }

  /**
   * Get a 1:1 copy of the passed array. Nested elements are not deep-copied -
   * the references are re-used!
   *
   * @param aArray
   *        The array to be copied.
   * @return <code>null</code> if the passed array is <code>null</code> - a non-
   *         <code>null</code> copy otherwise.
   */
  @Nullable
  @ReturnsMutableCopy
  public static boolean [] getCopy (@Nullable final boolean... aArray)
  {
    return aArray == null ? null : getCopy (aArray, 0, aArray.length);
  }

  /**
   * Get a 1:1 copy of the passed array using the passed number of array
   * elements. Nested elements are not deep-copied - the references are re-used!
   *
   * @param aArray
   *        The array to be copied.
   * @param nLength
   *        The number of elements to be copied into the new array. May not be
   *        &lt; 0. If the passed number of elements exceeds the number of
   *        elements in the array, only the available number of elements in the
   *        source array are copied.
   * @return <code>null</code> if the passed array is <code>null</code> - a non-
   *         <code>null</code> copy otherwise.
   */
  @Nullable
  @ReturnsMutableCopy
  public static boolean [] getCopy (@Nullable final boolean [] aArray, @Nonnegative final int nLength)
  {
    return aArray == null ? null : getCopy (aArray, 0, Math.min (aArray.length, nLength));
  }

  /**
   * Get a 1:1 copy of the passed array using the passed number of array
   * elements starting at the specified index. Nested elements are not
   * deep-copied - the references are re-used!
   *
   * @param aArray
   *        The array to be copied.
   * @param nStartIndex
   *        The index where the copying should start. Must be &gt;= 0!
   * @param nLength
   *        The number of elements to be copied into the new array. May not be
   *        &lt; 0. If the passed number of elements exceeds the number of
   *        elements in the array, an exception is thrown.
   * @return <code>null</code> if the passed array is <code>null</code> - a non-
   *         <code>null</code> copy otherwise.
   * @see System#arraycopy(Object, int, Object, int, int)
   */
  @Nullable
  @ReturnsMutableCopy
  public static boolean [] getCopy (@Nullable final boolean [] aArray,
                                    @Nonnegative final int nStartIndex,
                                    @Nonnegative final int nLength)
  {
    if (aArray == null)
      return null;
    final boolean [] ret = new boolean [nLength];
    System.arraycopy (aArray, nStartIndex, ret, 0, nLength);
    return ret;
  }

  /**
   * Get a 1:1 copy of the passed array. Nested elements are not deep-copied -
   * the references are re-used!
   *
   * @param aArray
   *        The array to be copied.
   * @return <code>null</code> if the passed array is <code>null</code> - a non-
   *         <code>null</code> copy otherwise.
   */
  @Nullable
  @ReturnsMutableCopy
  public static byte [] getCopy (@Nullable final byte... aArray)
  {
    return aArray == null ? null : getCopy (aArray, 0, aArray.length);
  }

  /**
   * Get a 1:1 copy of the passed array using the passed number of array
   * elements. Nested elements are not deep-copied - the references are re-used!
   *
   * @param aArray
   *        The array to be copied.
   * @param nLength
   *        The number of elements to be copied into the new array. May not be
   *        &lt; 0. If the passed number of elements exceeds the number of
   *        elements in the array, only the available number of elements in the
   *        source array are copied.
   * @return <code>null</code> if the passed array is <code>null</code> - a non-
   *         <code>null</code> copy otherwise.
   */
  @Nullable
  @ReturnsMutableCopy
  public static byte [] getCopy (@Nullable final byte [] aArray, @Nonnegative final int nLength)
  {
    return aArray == null ? null : getCopy (aArray, 0, Math.min (aArray.length, nLength));
  }

  /**
   * Get a 1:1 copy of the passed array using the passed number of array
   * elements starting at the specified index. Nested elements are not
   * deep-copied - the references are re-used!
   *
   * @param aArray
   *        The array to be copied.
   * @param nStartIndex
   *        The index where the copying should start. Must be &gt;= 0!
   * @param nLength
   *        The number of elements to be copied into the new array. May not be
   *        &lt; 0. If the passed number of elements exceeds the number of
   *        elements in the array, an exception is thrown.
   * @return <code>null</code> if the passed array is <code>null</code> - a non-
   *         <code>null</code> copy otherwise.
   * @see System#arraycopy(Object, int, Object, int, int)
   */
  @Nullable
  @ReturnsMutableCopy
  public static byte [] getCopy (@Nullable final byte [] aArray,
                                 @Nonnegative final int nStartIndex,
                                 @Nonnegative final int nLength)
  {
    if (aArray == null)
      return null;
    final byte [] ret = new byte [nLength];
    System.arraycopy (aArray, nStartIndex, ret, 0, nLength);
    return ret;
  }

  /**
   * Get a 1:1 copy of the passed array. Nested elements are not deep-copied -
   * the references are re-used!
   *
   * @param aArray
   *        The array to be copied.
   * @return <code>null</code> if the passed array is <code>null</code> - a non-
   *         <code>null</code> copy otherwise.
   */
  @Nullable
  @ReturnsMutableCopy
  public static char [] getCopy (@Nullable final char... aArray)
  {
    return aArray == null ? null : getCopy (aArray, 0, aArray.length);
  }

  /**
   * Get a 1:1 copy of the passed array using the passed number of array
   * elements. Nested elements are not deep-copied - the references are re-used!
   *
   * @param aArray
   *        The array to be copied.
   * @param nLength
   *        The number of elements to be copied into the new array. May not be
   *        &lt; 0. If the passed number of elements exceeds the number of
   *        elements in the array, only the available number of elements in the
   *        source array are copied.
   * @return <code>null</code> if the passed array is <code>null</code> - a non-
   *         <code>null</code> copy otherwise.
   */
  @Nullable
  @ReturnsMutableCopy
  public static char [] getCopy (@Nullable final char [] aArray, @Nonnegative final int nLength)
  {
    return aArray == null ? null : getCopy (aArray, 0, Math.min (aArray.length, nLength));
  }

  /**
   * Get a 1:1 copy of the passed array using the passed number of array
   * elements starting at the specified index. Nested elements are not
   * deep-copied - the references are re-used!
   *
   * @param aArray
   *        The array to be copied.
   * @param nStartIndex
   *        The index where the copying should start. Must be &gt;= 0!
   * @param nLength
   *        The number of elements to be copied into the new array. May not be
   *        &lt; 0. If the passed number of elements exceeds the number of
   *        elements in the array, an exception is thrown.
   * @return <code>null</code> if the passed array is <code>null</code> - a non-
   *         <code>null</code> copy otherwise.
   * @see System#arraycopy(Object, int, Object, int, int)
   */
  @Nullable
  @ReturnsMutableCopy
  public static char [] getCopy (@Nullable final char [] aArray,
                                 @Nonnegative final int nStartIndex,
                                 @Nonnegative final int nLength)
  {
    if (aArray == null)
      return null;
    final char [] ret = new char [nLength];
    System.arraycopy (aArray, nStartIndex, ret, 0, nLength);
    return ret;
  }

  /**
   * Get a 1:1 copy of the passed array. Nested elements are not deep-copied -
   * the references are re-used!
   *
   * @param aArray
   *        The array to be copied.
   * @return <code>null</code> if the passed array is <code>null</code> - a non-
   *         <code>null</code> copy otherwise.
   */
  @Nullable
  @ReturnsMutableCopy
  public static double [] getCopy (@Nullable final double... aArray)
  {
    return aArray == null ? null : getCopy (aArray, 0, aArray.length);
  }

  /**
   * Get a 1:1 copy of the passed array using the passed number of array
   * elements. Nested elements are not deep-copied - the references are re-used!
   *
   * @param aArray
   *        The array to be copied.
   * @param nLength
   *        The number of elements to be copied into the new array. May not be
   *        &lt; 0. If the passed number of elements exceeds the number of
   *        elements in the array, only the available number of elements in the
   *        source array are copied.
   * @return <code>null</code> if the passed array is <code>null</code> - a non-
   *         <code>null</code> copy otherwise.
   */
  @Nullable
  @ReturnsMutableCopy
  public static double [] getCopy (@Nullable final double [] aArray, @Nonnegative final int nLength)
  {
    return aArray == null ? null : getCopy (aArray, 0, Math.min (aArray.length, nLength));
  }

  /**
   * Get a 1:1 copy of the passed array using the passed number of array
   * elements starting at the specified index. Nested elements are not
   * deep-copied - the references are re-used!
   *
   * @param aArray
   *        The array to be copied.
   * @param nStartIndex
   *        The index where the copying should start. Must be &gt;= 0!
   * @param nLength
   *        The number of elements to be copied into the new array. May not be
   *        &lt; 0. If the passed number of elements exceeds the number of
   *        elements in the array, an exception is thrown.
   * @return <code>null</code> if the passed array is <code>null</code> - a non-
   *         <code>null</code> copy otherwise.
   * @see System#arraycopy(Object, int, Object, int, int)
   */
  @Nullable
  @ReturnsMutableCopy
  public static double [] getCopy (@Nullable final double [] aArray,
                                   @Nonnegative final int nStartIndex,
                                   @Nonnegative final int nLength)
  {
    if (aArray == null)
      return null;
    final double [] ret = new double [nLength];
    System.arraycopy (aArray, nStartIndex, ret, 0, nLength);
    return ret;
  }

  /**
   * Get a 1:1 copy of the passed array. Nested elements are not deep-copied -
   * the references are re-used!
   *
   * @param aArray
   *        The array to be copied.
   * @return <code>null</code> if the passed array is <code>null</code> - a non-
   *         <code>null</code> copy otherwise.
   */
  @Nullable
  @ReturnsMutableCopy
  public static float [] getCopy (@Nullable final float... aArray)
  {
    return aArray == null ? null : getCopy (aArray, 0, aArray.length);
  }

  /**
   * Get a 1:1 copy of the passed array using the passed number of array
   * elements. Nested elements are not deep-copied - the references are re-used!
   *
   * @param aArray
   *        The array to be copied.
   * @param nLength
   *        The number of elements to be copied into the new array. May not be
   *        &lt; 0. If the passed number of elements exceeds the number of
   *        elements in the array, only the available number of elements in the
   *        source array are copied.
   * @return <code>null</code> if the passed array is <code>null</code> - a non-
   *         <code>null</code> copy otherwise.
   */
  @Nullable
  @ReturnsMutableCopy
  public static float [] getCopy (@Nullable final float [] aArray, @Nonnegative final int nLength)
  {
    return aArray == null ? null : getCopy (aArray, 0, Math.min (aArray.length, nLength));
  }

  /**
   * Get a 1:1 copy of the passed array using the passed number of array
   * elements starting at the specified index. Nested elements are not
   * deep-copied - the references are re-used!
   *
   * @param aArray
   *        The array to be copied.
   * @param nStartIndex
   *        The index where the copying should start. Must be &gt;= 0!
   * @param nLength
   *        The number of elements to be copied into the new array. May not be
   *        &lt; 0. If the passed number of elements exceeds the number of
   *        elements in the array, an exception is thrown.
   * @return <code>null</code> if the passed array is <code>null</code> - a non-
   *         <code>null</code> copy otherwise.
   * @see System#arraycopy(Object, int, Object, int, int)
   */
  @Nullable
  @ReturnsMutableCopy
  public static float [] getCopy (@Nullable final float [] aArray,
                                  @Nonnegative final int nStartIndex,
                                  @Nonnegative final int nLength)
  {
    if (aArray == null)
      return null;
    final float [] ret = new float [nLength];
    System.arraycopy (aArray, nStartIndex, ret, 0, nLength);
    return ret;
  }

  /**
   * Get a 1:1 copy of the passed array. Nested elements are not deep-copied -
   * the references are re-used!
   *
   * @param aArray
   *        The array to be copied.
   * @return <code>null</code> if the passed array is <code>null</code> - a non-
   *         <code>null</code> copy otherwise.
   */

  @Nullable
  @ReturnsMutableCopy
  public static int [] getCopy (@Nullable final int... aArray)
  {
    return aArray == null ? null : getCopy (aArray, 0, aArray.length);
  }

  /**
   * Get a 1:1 copy of the passed array using the passed number of array
   * elements. Nested elements are not deep-copied - the references are re-used!
   *
   * @param aArray
   *        The array to be copied.
   * @param nLength
   *        The number of elements to be copied into the new array. May not be
   *        &lt; 0. If the passed number of elements exceeds the number of
   *        elements in the array, only the available number of elements in the
   *        source array are copied.
   * @return <code>null</code> if the passed array is <code>null</code> - a non-
   *         <code>null</code> copy otherwise.
   */
  @Nullable
  @ReturnsMutableCopy
  public static int [] getCopy (@Nullable final int [] aArray, @Nonnegative final int nLength)
  {
    return aArray == null ? null : getCopy (aArray, 0, Math.min (aArray.length, nLength));
  }

  /**
   * Get a 1:1 copy of the passed array using the passed number of array
   * elements starting at the specified index. Nested elements are not
   * deep-copied - the references are re-used!
   *
   * @param aArray
   *        The array to be copied.
   * @param nStartIndex
   *        The index where the copying should start. Must be &gt;= 0!
   * @param nLength
   *        The number of elements to be copied into the new array. May not be
   *        &lt; 0. If the passed number of elements exceeds the number of
   *        elements in the array, an exception is thrown.
   * @return <code>null</code> if the passed array is <code>null</code> - a non-
   *         <code>null</code> copy otherwise.
   * @see System#arraycopy(Object, int, Object, int, int)
   */
  /**
   * Get a 1:1 copy of the passed array. Nested elements are not deep-copied -
   * the references are re-used!
   *
   * @param aArray
   *        The array to be copied.
   * @param nStartIndex
   *        The index where the copying should start. Must be &gt;= 0!
   * @param nLength
   *        The number of elements to be copied into the new array. May not be
   *        &lt; 0. If the passed number of elements exceeds the number of
   *        elements in the array, an exception is thrown.
   * @return <code>null</code> if the passed array is <code>null</code> - a non-
   *         <code>null</code> copy otherwise.
   */
  @Nullable
  @ReturnsMutableCopy
  public static int [] getCopy (@Nullable final int [] aArray,
                                @Nonnegative final int nStartIndex,
                                @Nonnegative final int nLength)
  {
    if (aArray == null)
      return null;
    final int [] ret = new int [nLength];
    System.arraycopy (aArray, nStartIndex, ret, 0, nLength);
    return ret;
  }

  @Nullable
  @ReturnsMutableCopy
  public static long [] getCopy (@Nullable final long... aArray)
  {
    return aArray == null ? null : getCopy (aArray, 0, aArray.length);
  }

  /**
   * Get a 1:1 copy of the passed array using the passed number of array
   * elements. Nested elements are not deep-copied - the references are re-used!
   *
   * @param aArray
   *        The array to be copied.
   * @param nLength
   *        The number of elements to be copied into the new array. May not be
   *        &lt; 0. If the passed number of elements exceeds the number of
   *        elements in the array, only the available number of elements in the
   *        source array are copied.
   * @return <code>null</code> if the passed array is <code>null</code> - a non-
   *         <code>null</code> copy otherwise.
   */
  @Nullable
  @ReturnsMutableCopy
  public static long [] getCopy (@Nullable final long [] aArray, @Nonnegative final int nLength)
  {
    return aArray == null ? null : getCopy (aArray, 0, Math.min (aArray.length, nLength));
  }

  /**
   * Get a 1:1 copy of the passed array using the passed number of array
   * elements starting at the specified index. Nested elements are not
   * deep-copied - the references are re-used!
   *
   * @param aArray
   *        The array to be copied.
   * @param nStartIndex
   *        The index where the copying should start. Must be &gt;= 0!
   * @param nLength
   *        The number of elements to be copied into the new array. May not be
   *        &lt; 0. If the passed number of elements exceeds the number of
   *        elements in the array, an exception is thrown.
   * @return <code>null</code> if the passed array is <code>null</code> - a non-
   *         <code>null</code> copy otherwise.
   * @see System#arraycopy(Object, int, Object, int, int)
   */
  @Nullable
  @ReturnsMutableCopy
  public static long [] getCopy (@Nullable final long [] aArray,
                                 @Nonnegative final int nStartIndex,
                                 @Nonnegative final int nLength)
  {
    if (aArray == null)
      return null;
    final long [] ret = new long [nLength];
    System.arraycopy (aArray, nStartIndex, ret, 0, nLength);
    return ret;
  }

  /**
   * Get a 1:1 copy of the passed array. Nested elements are not deep-copied -
   * the references are re-used!
   *
   * @param aArray
   *        The array to be copied.
   * @return <code>null</code> if the passed array is <code>null</code> - a non-
   *         <code>null</code> copy otherwise.
   */
  @Nullable
  @ReturnsMutableCopy
  public static short [] getCopy (@Nullable final short... aArray)
  {
    return aArray == null ? null : getCopy (aArray, 0, aArray.length);
  }

  /**
   * Get a 1:1 copy of the passed array using the passed number of array
   * elements. Nested elements are not deep-copied - the references are re-used!
   *
   * @param aArray
   *        The array to be copied.
   * @param nLength
   *        The number of elements to be copied into the new array. May not be
   *        &lt; 0. If the passed number of elements exceeds the number of
   *        elements in the array, only the available number of elements in the
   *        source array are copied.
   * @return <code>null</code> if the passed array is <code>null</code> - a non-
   *         <code>null</code> copy otherwise.
   */
  @Nullable
  @ReturnsMutableCopy
  public static short [] getCopy (@Nullable final short [] aArray, @Nonnegative final int nLength)
  {
    return aArray == null ? null : getCopy (aArray, 0, Math.min (aArray.length, nLength));
  }

  /**
   * Get a 1:1 copy of the passed array using the passed number of array
   * elements starting at the specified index. Nested elements are not
   * deep-copied - the references are re-used!
   *
   * @param aArray
   *        The array to be copied.
   * @param nStartIndex
   *        The index where the copying should start. Must be &gt;= 0!
   * @param nLength
   *        The number of elements to be copied into the new array. May not be
   *        &lt; 0. If the passed number of elements exceeds the number of
   *        elements in the array, an exception is thrown.
   * @return <code>null</code> if the passed array is <code>null</code> - a non-
   *         <code>null</code> copy otherwise.
   * @see System#arraycopy(Object, int, Object, int, int)
   */
  @Nullable
  @ReturnsMutableCopy
  public static short [] getCopy (@Nullable final short [] aArray,
                                  @Nonnegative final int nStartIndex,
                                  @Nonnegative final int nLength)
  {
    if (aArray == null)
      return null;
    final short [] ret = new short [nLength];
    System.arraycopy (aArray, nStartIndex, ret, 0, nLength);
    return ret;
  }

  /**
   * Get a 1:1 copy of the passed array. Nested elements are not deep-copied -
   * the references are re-used!
   *
   * @param <ELEMENTTYPE>
   *        Array element type
   * @param aArray
   *        The array to be copied.
   * @return <code>null</code> if the passed array is <code>null</code> - a non-
   *         <code>null</code> copy otherwise.
   */
  @Nullable
  @ReturnsMutableCopy
  @SafeVarargs
  public static <ELEMENTTYPE> ELEMENTTYPE [] getCopy (@Nullable final ELEMENTTYPE... aArray)
  {
    return aArray == null ? null : getCopy (aArray, 0, aArray.length);
  }

  /**
   * Get a 1:1 copy of the passed array using the passed number of array
   * elements. Nested elements are not deep-copied - the references are re-used!
   *
   * @param <ELEMENTTYPE>
   *        Array element type
   * @param aArray
   *        The array to be copied.
   * @param nLength
   *        The number of elements to be copied into the new array. May not be
   *        &lt; 0. If the passed number of elements exceeds the number of
   *        elements in the array, only the available number of elements in the
   *        source array are copied.
   * @return <code>null</code> if the passed array is <code>null</code> - a non-
   *         <code>null</code> copy otherwise.
   */
  @Nullable
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> ELEMENTTYPE [] getCopy (@Nullable final ELEMENTTYPE [] aArray,
                                                      @Nonnegative final int nLength)
  {
    return aArray == null ? null : getCopy (aArray, 0, Math.min (aArray.length, nLength));
  }

  /**
   * Get a 1:1 copy of the passed array using the passed number of array
   * elements starting at the specified index. Nested elements are not
   * deep-copied - the references are re-used!
   *
   * @param <ELEMENTTYPE>
   *        Array element type
   * @param aArray
   *        The array to be copied.
   * @param nStartIndex
   *        The index where the copying should start. Must be &gt;= 0!
   * @param nLength
   *        The number of elements to be copied into the new array. May not be
   *        &lt; 0. If the passed number of elements exceeds the number of
   *        elements in the array, an exception is thrown.
   * @return <code>null</code> if the passed array is <code>null</code> - a non-
   *         <code>null</code> copy otherwise.
   * @see System#arraycopy(Object, int, Object, int, int)
   */
  @Nullable
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> ELEMENTTYPE [] getCopy (@Nullable final ELEMENTTYPE [] aArray,
                                                      @Nonnegative final int nStartIndex,
                                                      @Nonnegative final int nLength)
  {
    if (aArray == null)
      return null;
    final ELEMENTTYPE [] ret = newArraySameType (aArray, nLength);
    System.arraycopy (aArray, nStartIndex, ret, 0, nLength);
    return ret;
  }

  /**
   * Get a new array that combines the passed two arrays, maintaining the order.
   *
   * @param <ELEMENTTYPE>
   *        Array element type
   * @param aHeadArray
   *        The first array. May be <code>null</code>.
   * @param aTailArray
   *        The second array. May be <code>null</code>.
   * @return <code>null</code> if both array parameters are <code>null</code> -
   *         a non-<code>null</code> array with all elements in the correct
   *         order otherwise.
   */
  @Nullable
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> ELEMENTTYPE [] getConcatenated (@Nullable final ELEMENTTYPE [] aHeadArray,
                                                              @Nullable final ELEMENTTYPE [] aTailArray)
  {
    // If first array is invalid, simply
    if (isEmpty (aHeadArray))
      return getCopy (aTailArray);
    if (isEmpty (aTailArray))
      return getCopy (aHeadArray);

    // Start concatenating
    final ELEMENTTYPE [] ret = newArraySameType (aHeadArray, aHeadArray.length + aTailArray.length);
    System.arraycopy (aHeadArray, 0, ret, 0, aHeadArray.length);
    System.arraycopy (aTailArray, 0, ret, aHeadArray.length, aTailArray.length);
    return ret;
  }

  /**
   * Get a new array that combines the passed head and the array. The head
   * element will be the first element of the created array.
   *
   * @param <ELEMENTTYPE>
   *        Array element type
   * @param aHead
   *        The first element of the result array. If this element is
   *        <code>null</code> it will be inserted as such into the array!
   * @param aTailArray
   *        The tail array. May be <code>null</code>.
   * @param aClass
   *        The element class. Must be present, because in case both elements
   *        are <code>null</code> there would be no way to create a new array.
   *        May not be <code>null</code>.
   * @return <code>null</code> if both array parameters are <code>null</code> -
   *         a non-<code>null</code> array with all elements in the correct
   *         order otherwise.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> ELEMENTTYPE [] getConcatenated (@Nullable final ELEMENTTYPE aHead,
                                                              @Nullable final ELEMENTTYPE [] aTailArray,
                                                              @Nonnull final Class <ELEMENTTYPE> aClass)
  {
    if (isEmpty (aTailArray))
      return newArraySingleElement (aHead, aClass);

    // Start concatenating
    final ELEMENTTYPE [] ret = newArray (aClass, 1 + aTailArray.length);
    ret[0] = aHead;
    System.arraycopy (aTailArray, 0, ret, 1, aTailArray.length);
    return ret;
  }

  /**
   * Get a new array that combines the passed array and the tail element. The
   * tail element will be the last element of the created array.
   *
   * @param <ELEMENTTYPE>
   *        Array element type
   * @param aHeadArray
   *        The head array. May be <code>null</code>.
   * @param aTail
   *        The last element of the result array. If this element is
   *        <code>null</code> it will be inserted as such into the array!
   * @param aClass
   *        The element class. Must be present, because in case both elements
   *        are <code>null</code> there would be no way to create a new array.
   *        May not be <code>null</code>.
   * @return <code>null</code> if both array parameters are <code>null</code> -
   *         a non-<code>null</code> array with all elements in the correct
   *         order otherwise.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> ELEMENTTYPE [] getConcatenated (@Nullable final ELEMENTTYPE [] aHeadArray,
                                                              @Nullable final ELEMENTTYPE aTail,
                                                              @Nonnull final Class <ELEMENTTYPE> aClass)
  {
    if (isEmpty (aHeadArray))
      return newArraySingleElement (aTail, aClass);

    // Start concatenating
    final ELEMENTTYPE [] ret = newArray (aClass, aHeadArray.length + 1);
    System.arraycopy (aHeadArray, 0, ret, 0, aHeadArray.length);
    ret[aHeadArray.length] = aTail;
    return ret;
  }

  /**
   * Get a new array that combines the passed two arrays, maintaining the order.
   *
   * @param aHeadArray
   *        The first array. May be <code>null</code>.
   * @param aTailArray
   *        The second array. May be <code>null</code>.
   * @return <code>null</code> if both array parameters are <code>null</code> -
   *         a non-<code>null</code> array with all elements in the correct
   *         order otherwise.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static boolean [] getConcatenated (@Nullable final boolean [] aHeadArray,
                                            @Nullable final boolean... aTailArray)
  {
    // If first array is invalid, simply
    if (isEmpty (aHeadArray))
      return getCopy (aTailArray);
    if (isEmpty (aTailArray))
      return getCopy (aHeadArray);

    // Start concatenating
    final boolean [] ret = new boolean [aHeadArray.length + aTailArray.length];
    System.arraycopy (aHeadArray, 0, ret, 0, aHeadArray.length);
    System.arraycopy (aTailArray, 0, ret, aHeadArray.length, aTailArray.length);
    return ret;
  }

  /**
   * Get a new array that combines the passed head element and the array. The
   * head element will be the first element of the created array.
   *
   * @param aHead
   *        The first element of the result array.
   * @param aTailArray
   *        The tail array. May be <code>null</code>.
   * @return A non-<code>null</code> array with all elements in the correct
   *         order.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static boolean [] getConcatenated (final boolean aHead, @Nullable final boolean... aTailArray)
  {
    if (isEmpty (aTailArray))
      return new boolean [] { aHead };

    final boolean [] ret = new boolean [1 + aTailArray.length];
    ret[0] = aHead;
    System.arraycopy (aTailArray, 0, ret, 1, aTailArray.length);
    return ret;
  }

  /**
   * Get a new array that combines the passed array and the tail element. The
   * tail element will be the last element of the created array.
   *
   * @param aHeadArray
   *        The head array. May be <code>null</code>.
   * @param aTail
   *        The last element of the result array.
   * @return A non-<code>null</code> array with all elements in the correct
   *         order.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static boolean [] getConcatenated (@Nullable final boolean [] aHeadArray, final boolean aTail)
  {
    if (isEmpty (aHeadArray))
      return new boolean [] { aTail };

    final boolean [] ret = new boolean [aHeadArray.length + 1];
    System.arraycopy (aHeadArray, 0, ret, 0, aHeadArray.length);
    ret[aHeadArray.length] = aTail;
    return ret;
  }

  /**
   * Get a new array that combines the passed two arrays, maintaining the order.
   *
   * @param aHeadArray
   *        The first array. May be <code>null</code>.
   * @param aTailArray
   *        The second array. May be <code>null</code>.
   * @return <code>null</code> if both array parameters are <code>null</code> -
   *         a non-<code>null</code> array with all elements in the correct
   *         order otherwise.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static byte [] getConcatenated (@Nullable final byte [] aHeadArray, @Nullable final byte... aTailArray)
  {
    // If first array is invalid, simply
    if (isEmpty (aHeadArray))
      return getCopy (aTailArray);
    if (isEmpty (aTailArray))
      return getCopy (aHeadArray);

    // Start concatenating
    final byte [] ret = new byte [aHeadArray.length + aTailArray.length];
    System.arraycopy (aHeadArray, 0, ret, 0, aHeadArray.length);
    System.arraycopy (aTailArray, 0, ret, aHeadArray.length, aTailArray.length);
    return ret;
  }

  /**
   * Get a new array that combines the passed head element and the array. The
   * head element will be the first element of the created array.
   *
   * @param aHead
   *        The first element of the result array.
   * @param aTailArray
   *        The tail array. May be <code>null</code>.
   * @return A non-<code>null</code> array with all elements in the correct
   *         order.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static byte [] getConcatenated (final byte aHead, @Nullable final byte... aTailArray)
  {
    if (isEmpty (aTailArray))
      return new byte [] { aHead };

    final byte [] ret = new byte [1 + aTailArray.length];
    ret[0] = aHead;
    System.arraycopy (aTailArray, 0, ret, 1, aTailArray.length);
    return ret;
  }

  /**
   * Get a new array that combines the passed array and the tail element. The
   * tail element will be the last element of the created array.
   *
   * @param aHeadArray
   *        The head array. May be <code>null</code>.
   * @param aTail
   *        The last element of the result array.
   * @return A non-<code>null</code> array with all elements in the correct
   *         order.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static byte [] getConcatenated (@Nullable final byte [] aHeadArray, final byte aTail)
  {
    if (isEmpty (aHeadArray))
      return new byte [] { aTail };

    final byte [] ret = new byte [aHeadArray.length + 1];
    System.arraycopy (aHeadArray, 0, ret, 0, aHeadArray.length);
    ret[aHeadArray.length] = aTail;
    return ret;
  }

  /**
   * Get a new array that combines the passed two arrays, maintaining the order.
   *
   * @param aHeadArray
   *        The first array. May be <code>null</code>.
   * @param aTailArray
   *        The second array. May be <code>null</code>.
   * @return <code>null</code> if both array parameters are <code>null</code> -
   *         a non-<code>null</code> array with all elements in the correct
   *         order otherwise.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static char [] getConcatenated (@Nullable final char [] aHeadArray, @Nullable final char... aTailArray)
  {
    // If first array is invalid, simply
    if (isEmpty (aHeadArray))
      return getCopy (aTailArray);
    if (isEmpty (aTailArray))
      return getCopy (aHeadArray);

    // Start concatenating
    final char [] ret = new char [aHeadArray.length + aTailArray.length];
    System.arraycopy (aHeadArray, 0, ret, 0, aHeadArray.length);
    System.arraycopy (aTailArray, 0, ret, aHeadArray.length, aTailArray.length);
    return ret;
  }

  /**
   * Get a new array that combines the passed two arrays, maintaining the order.
   *
   * @param aHeadArray
   *        The first array. May be <code>null</code>.
   * @param aTailArray
   *        The second array. May be <code>null</code>.
   * @return <code>null</code> if both array parameters are <code>null</code> -
   *         a non-<code>null</code> array with all elements in the correct
   *         order otherwise.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static char [] [] getConcatenated (@Nullable final char [] [] aHeadArray,
                                            @Nullable final char []... aTailArray)
  {
    // If first array is invalid, simply
    if (isEmpty (aHeadArray))
      return getCopy (aTailArray);
    if (isEmpty (aTailArray))
      return getCopy (aHeadArray);

    // Start concatenating
    final char [] [] ret = new char [aHeadArray.length + aTailArray.length] [];
    System.arraycopy (aHeadArray, 0, ret, 0, aHeadArray.length);
    System.arraycopy (aTailArray, 0, ret, aHeadArray.length, aTailArray.length);
    return ret;
  }

  /**
   * Get a new array that combines the passed head element and the array. The
   * head element will be the first element of the created array.
   *
   * @param aHead
   *        The first element of the result array.
   * @param aTailArray
   *        The tail array. May be <code>null</code>.
   * @return A non-<code>null</code> array with all elements in the correct
   *         order.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static char [] getConcatenated (final char aHead, @Nullable final char... aTailArray)
  {
    if (isEmpty (aTailArray))
      return new char [] { aHead };

    final char [] ret = new char [1 + aTailArray.length];
    ret[0] = aHead;
    System.arraycopy (aTailArray, 0, ret, 1, aTailArray.length);
    return ret;
  }

  /**
   * Get a new array that combines the passed array and the tail element. The
   * tail element will be the last element of the created array.
   *
   * @param aHeadArray
   *        The head array. May be <code>null</code>.
   * @param aTail
   *        The last element of the result array.
   * @return A non-<code>null</code> array with all elements in the correct
   *         order.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static char [] getConcatenated (@Nullable final char [] aHeadArray, final char aTail)
  {
    if (isEmpty (aHeadArray))
      return new char [] { aTail };

    final char [] ret = new char [aHeadArray.length + 1];
    System.arraycopy (aHeadArray, 0, ret, 0, aHeadArray.length);
    ret[aHeadArray.length] = aTail;
    return ret;
  }

  /**
   * Get a new array that combines the passed two arrays, maintaining the order.
   *
   * @param aHeadArray
   *        The first array. May be <code>null</code>.
   * @param aTailArray
   *        The second array. May be <code>null</code>.
   * @return <code>null</code> if both array parameters are <code>null</code> -
   *         a non-<code>null</code> array with all elements in the correct
   *         order otherwise.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static double [] getConcatenated (@Nullable final double [] aHeadArray, @Nullable final double... aTailArray)
  {
    // If first array is invalid, simply
    if (isEmpty (aHeadArray))
      return getCopy (aTailArray);
    if (isEmpty (aTailArray))
      return getCopy (aHeadArray);

    // Start concatenating
    final double [] ret = new double [aHeadArray.length + aTailArray.length];
    System.arraycopy (aHeadArray, 0, ret, 0, aHeadArray.length);
    System.arraycopy (aTailArray, 0, ret, aHeadArray.length, aTailArray.length);
    return ret;
  }

  /**
   * Get a new array that combines the passed head element and the array. The
   * head element will be the first element of the created array.
   *
   * @param aHead
   *        The first element of the result array.
   * @param aTailArray
   *        The tail array. May be <code>null</code>.
   * @return A non-<code>null</code> array with all elements in the correct
   *         order.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static double [] getConcatenated (final double aHead, @Nullable final double... aTailArray)
  {
    if (isEmpty (aTailArray))
      return new double [] { aHead };

    final double [] ret = new double [1 + aTailArray.length];
    ret[0] = aHead;
    System.arraycopy (aTailArray, 0, ret, 1, aTailArray.length);
    return ret;
  }

  /**
   * Get a new array that combines the passed array and the tail element. The
   * tail element will be the last element of the created array.
   *
   * @param aHeadArray
   *        The head array. May be <code>null</code>.
   * @param aTail
   *        The last element of the result array.
   * @return A non-<code>null</code> array with all elements in the correct
   *         order.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static double [] getConcatenated (@Nullable final double [] aHeadArray, final double aTail)
  {
    if (isEmpty (aHeadArray))
      return new double [] { aTail };

    final double [] ret = new double [aHeadArray.length + 1];
    System.arraycopy (aHeadArray, 0, ret, 0, aHeadArray.length);
    ret[aHeadArray.length] = aTail;
    return ret;
  }

  /**
   * Get a new array that combines the passed two arrays, maintaining the order.
   *
   * @param aHeadArray
   *        The first array. May be <code>null</code>.
   * @param aTailArray
   *        The second array. May be <code>null</code>.
   * @return <code>null</code> if both array parameters are <code>null</code> -
   *         a non-<code>null</code> array with all elements in the correct
   *         order otherwise.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static float [] getConcatenated (@Nullable final float [] aHeadArray, @Nullable final float... aTailArray)
  {
    // If first array is invalid, simply
    if (isEmpty (aHeadArray))
      return getCopy (aTailArray);
    if (isEmpty (aTailArray))
      return getCopy (aHeadArray);

    // Start concatenating
    final float [] ret = new float [aHeadArray.length + aTailArray.length];
    System.arraycopy (aHeadArray, 0, ret, 0, aHeadArray.length);
    System.arraycopy (aTailArray, 0, ret, aHeadArray.length, aTailArray.length);
    return ret;
  }

  /**
   * Get a new array that combines the passed head element and the array. The
   * head element will be the first element of the created array.
   *
   * @param aHead
   *        The first element of the result array.
   * @param aTailArray
   *        The tail array. May be <code>null</code>.
   * @return A non-<code>null</code> array with all elements in the correct
   *         order.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static float [] getConcatenated (final float aHead, @Nullable final float... aTailArray)
  {
    if (isEmpty (aTailArray))
      return new float [] { aHead };

    final float [] ret = new float [1 + aTailArray.length];
    ret[0] = aHead;
    System.arraycopy (aTailArray, 0, ret, 1, aTailArray.length);
    return ret;
  }

  /**
   * Get a new array that combines the passed array and the tail element. The
   * tail element will be the last element of the created array.
   *
   * @param aHeadArray
   *        The head array. May be <code>null</code>.
   * @param aTail
   *        The last element of the result array.
   * @return A non-<code>null</code> array with all elements in the correct
   *         order.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static float [] getConcatenated (@Nullable final float [] aHeadArray, final float aTail)
  {
    if (isEmpty (aHeadArray))
      return new float [] { aTail };

    final float [] ret = new float [aHeadArray.length + 1];
    System.arraycopy (aHeadArray, 0, ret, 0, aHeadArray.length);
    ret[aHeadArray.length] = aTail;
    return ret;
  }

  /**
   * Get a new array that combines the passed two arrays, maintaining the order.
   *
   * @param aHeadArray
   *        The first array. May be <code>null</code>.
   * @param aTailArray
   *        The second array. May be <code>null</code>.
   * @return <code>null</code> if both array parameters are <code>null</code> -
   *         a non-<code>null</code> array with all elements in the correct
   *         order otherwise.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static int [] getConcatenated (@Nullable final int [] aHeadArray, @Nullable final int... aTailArray)
  {
    // If first array is invalid, simply
    if (isEmpty (aHeadArray))
      return getCopy (aTailArray);
    if (isEmpty (aTailArray))
      return getCopy (aHeadArray);

    // Start concatenating
    final int [] ret = new int [aHeadArray.length + aTailArray.length];
    System.arraycopy (aHeadArray, 0, ret, 0, aHeadArray.length);
    System.arraycopy (aTailArray, 0, ret, aHeadArray.length, aTailArray.length);
    return ret;
  }

  /**
   * Get a new array that combines the passed head element and the array. The
   * head element will be the first element of the created array.
   *
   * @param aHead
   *        The first element of the result array.
   * @param aTailArray
   *        The tail array. May be <code>null</code>.
   * @return A non-<code>null</code> array with all elements in the correct
   *         order.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static int [] getConcatenated (final int aHead, @Nullable final int... aTailArray)
  {
    if (isEmpty (aTailArray))
      return new int [] { aHead };

    final int [] ret = new int [1 + aTailArray.length];
    ret[0] = aHead;
    System.arraycopy (aTailArray, 0, ret, 1, aTailArray.length);
    return ret;
  }

  /**
   * Get a new array that combines the passed array and the tail element. The
   * tail element will be the last element of the created array.
   *
   * @param aHeadArray
   *        The head array. May be <code>null</code>.
   * @param aTail
   *        The last element of the result array.
   * @return A non-<code>null</code> array with all elements in the correct
   *         order.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static int [] getConcatenated (@Nullable final int [] aHeadArray, final int aTail)
  {
    if (isEmpty (aHeadArray))
      return new int [] { aTail };

    final int [] ret = new int [aHeadArray.length + 1];
    System.arraycopy (aHeadArray, 0, ret, 0, aHeadArray.length);
    ret[aHeadArray.length] = aTail;
    return ret;
  }

  /**
   * Get a new array that combines the passed two arrays, maintaining the order.
   *
   * @param aHeadArray
   *        The first array. May be <code>null</code>.
   * @param aTailArray
   *        The second array. May be <code>null</code>.
   * @return <code>null</code> if both array parameters are <code>null</code> -
   *         a non-<code>null</code> array with all elements in the correct
   *         order otherwise.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static long [] getConcatenated (@Nullable final long [] aHeadArray, @Nullable final long... aTailArray)
  {
    // If first array is invalid, simply
    if (isEmpty (aHeadArray))
      return getCopy (aTailArray);
    if (isEmpty (aTailArray))
      return getCopy (aHeadArray);

    // Start concatenating
    final long [] ret = new long [aHeadArray.length + aTailArray.length];
    System.arraycopy (aHeadArray, 0, ret, 0, aHeadArray.length);
    System.arraycopy (aTailArray, 0, ret, aHeadArray.length, aTailArray.length);
    return ret;
  }

  /**
   * Get a new array that combines the passed head element and the array. The
   * head element will be the first element of the created array.
   *
   * @param aHead
   *        The first element of the result array.
   * @param aTailArray
   *        The tail array. May be <code>null</code>.
   * @return A non-<code>null</code> array with all elements in the correct
   *         order.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static long [] getConcatenated (final long aHead, @Nullable final long... aTailArray)
  {
    if (isEmpty (aTailArray))
      return new long [] { aHead };

    final long [] ret = new long [1 + aTailArray.length];
    ret[0] = aHead;
    System.arraycopy (aTailArray, 0, ret, 1, aTailArray.length);
    return ret;
  }

  /**
   * Get a new array that combines the passed array and the tail element. The
   * tail element will be the last element of the created array.
   *
   * @param aHeadArray
   *        The head array. May be <code>null</code>.
   * @param aTail
   *        The last element of the result array.
   * @return A non-<code>null</code> array with all elements in the correct
   *         order.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static long [] getConcatenated (@Nullable final long [] aHeadArray, final long aTail)
  {
    if (isEmpty (aHeadArray))
      return new long [] { aTail };

    final long [] ret = new long [aHeadArray.length + 1];
    System.arraycopy (aHeadArray, 0, ret, 0, aHeadArray.length);
    ret[aHeadArray.length] = aTail;
    return ret;
  }

  /**
   * Get a new array that combines the passed two arrays, maintaining the order.
   *
   * @param aHeadArray
   *        The first array. May be <code>null</code>.
   * @param aTailArray
   *        The second array. May be <code>null</code>.
   * @return <code>null</code> if both array parameters are <code>null</code> -
   *         a non-<code>null</code> array with all elements in the correct
   *         order otherwise.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static short [] getConcatenated (@Nullable final short [] aHeadArray, @Nullable final short... aTailArray)
  {
    // If first array is invalid, simply
    if (isEmpty (aHeadArray))
      return getCopy (aTailArray);
    if (isEmpty (aTailArray))
      return getCopy (aHeadArray);

    // Start concatenating
    final short [] ret = new short [aHeadArray.length + aTailArray.length];
    System.arraycopy (aHeadArray, 0, ret, 0, aHeadArray.length);
    System.arraycopy (aTailArray, 0, ret, aHeadArray.length, aTailArray.length);
    return ret;
  }

  /**
   * Get a new array that combines the passed head element and the array. The
   * head element will be the first element of the created array.
   *
   * @param aHead
   *        The first element of the result array.
   * @param aTailArray
   *        The tail array. May be <code>null</code>.
   * @return A non-<code>null</code> array with all elements in the correct
   *         order.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static short [] getConcatenated (final short aHead, @Nullable final short... aTailArray)
  {
    if (isEmpty (aTailArray))
      return new short [] { aHead };

    final short [] ret = new short [1 + aTailArray.length];
    ret[0] = aHead;
    System.arraycopy (aTailArray, 0, ret, 1, aTailArray.length);
    return ret;
  }

  /**
   * Get a new array that combines the passed array and the tail element. The
   * tail element will be the last element of the created array.
   *
   * @param aHeadArray
   *        The head array. May be <code>null</code>.
   * @param aTail
   *        The last element of the result array.
   * @return A non-<code>null</code> array with all elements in the correct
   *         order.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static short [] getConcatenated (@Nullable final short [] aHeadArray, final short aTail)
  {
    if (isEmpty (aHeadArray))
      return new short [] { aTail };

    final short [] ret = new short [aHeadArray.length + 1];
    System.arraycopy (aHeadArray, 0, ret, 0, aHeadArray.length);
    ret[aHeadArray.length] = aTail;
    return ret;
  }

  /**
   * Get a new array that combines the passed two arrays, maintaining the order.
   *
   * @param aHeadArray
   *        The first array. May be <code>null</code>.
   * @param aTailArray
   *        The second array. May be <code>null</code>.
   * @return <code>null</code> if both array parameters are <code>null</code> -
   *         a non-<code>null</code> array with all elements in the correct
   *         order otherwise.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static String [] getConcatenated (@Nullable final String [] aHeadArray, @Nullable final String... aTailArray)
  {
    // If first array is invalid, simply
    if (isEmpty (aHeadArray))
      return getCopy (aTailArray);
    if (isEmpty (aTailArray))
      return getCopy (aHeadArray);

    // Start concatenating
    final String [] ret = new String [aHeadArray.length + aTailArray.length];
    System.arraycopy (aHeadArray, 0, ret, 0, aHeadArray.length);
    System.arraycopy (aTailArray, 0, ret, aHeadArray.length, aTailArray.length);
    return ret;
  }

  /**
   * Get a new array that combines the passed head element and the array. The
   * head element will be the first element of the created array.
   *
   * @param aHead
   *        The first element of the result array.
   * @param aTailArray
   *        The tail array. May be <code>null</code>.
   * @return A non-<code>null</code> array with all elements in the correct
   *         order.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static String [] getConcatenated (final String aHead, @Nullable final String... aTailArray)
  {
    if (isEmpty (aTailArray))
      return new String [] { aHead };

    final String [] ret = new String [1 + aTailArray.length];
    ret[0] = aHead;
    System.arraycopy (aTailArray, 0, ret, 1, aTailArray.length);
    return ret;
  }

  /**
   * Get a new array that combines the passed array and the tail element. The
   * tail element will be the last element of the created array.
   *
   * @param aHeadArray
   *        The head array. May be <code>null</code>.
   * @param aTail
   *        The last element of the result array.
   * @return A non-<code>null</code> array with all elements in the correct
   *         order.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static String [] getConcatenated (@Nullable final String [] aHeadArray, final String aTail)
  {
    if (isEmpty (aHeadArray))
      return new String [] { aTail };

    final String [] ret = new String [aHeadArray.length + 1];
    System.arraycopy (aHeadArray, 0, ret, 0, aHeadArray.length);
    ret[aHeadArray.length] = aTail;
    return ret;
  }

  /**
   * Get an array that contains all elements, except for the first element.
   *
   * @param <ELEMENTTYPE>
   *        Array element type
   * @param aArray
   *        The source array. May be <code>null</code>.
   * @return <code>null</code> if the passed array is <code>null</code> or has
   *         less than one element. A non-<code>null</code> copy of the array
   *         without the first element otherwise.
   */
  @Nullable
  @ReturnsMutableCopy
  @SafeVarargs
  public static <ELEMENTTYPE> ELEMENTTYPE [] getAllExceptFirst (@Nullable final ELEMENTTYPE... aArray)
  {
    return getAllExceptFirst (aArray, 1);
  }

  /**
   * Get an array that contains all elements, except for the first <em>n</em>
   * elements.
   *
   * @param <ELEMENTTYPE>
   *        Array element type
   * @param aArray
   *        The source array. May be <code>null</code>.
   * @param nElementsToSkip
   *        The number of elements to skip. Must be &gt;= 0!
   * @return <code>null</code> if the passed array is <code>null</code> or has
   *         &le; elements than elements to be skipped. A non-<code>null</code>
   *         copy of the array without the first elements otherwise.
   */
  @Nullable
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> ELEMENTTYPE [] getAllExceptFirst (@Nullable final ELEMENTTYPE [] aArray,
                                                                @Nonnegative final int nElementsToSkip)
  {
    ValueEnforcer.isGE0 (nElementsToSkip, "ElementsToSkip");

    if (nElementsToSkip == 0)
      return aArray;
    if (aArray == null || nElementsToSkip >= aArray.length)
      return null;
    return getCopy (aArray, nElementsToSkip, aArray.length - nElementsToSkip);
  }

  /**
   * Get an array that contains all elements, except for the passed elements.
   *
   * @param <ELEMENTTYPE>
   *        Array element type
   * @param aArray
   *        The source array. May be <code>null</code>.
   * @param aElementsToRemove
   *        The elements to skip.
   * @return <code>null</code> if the passed array is <code>null</code>. The
   *         original array, if no elements need to be skipped. A non-
   *         <code>null</code> copy of the array without the passed elements
   *         otherwise.
   */
  @Nullable
  @ReturnsMutableCopy
  @SafeVarargs
  public static <ELEMENTTYPE> ELEMENTTYPE [] getAllExcept (@Nullable final ELEMENTTYPE [] aArray,
                                                           @Nullable final ELEMENTTYPE... aElementsToRemove)
  {
    if (isEmpty (aArray) || isEmpty (aElementsToRemove))
      return aArray;

    final ELEMENTTYPE [] tmp = getCopy (aArray);
    int nDst = 0;
    for (int nSrc = 0; nSrc < tmp.length; ++nSrc)
      if (!contains (aElementsToRemove, tmp[nSrc]))
        tmp[nDst++] = tmp[nSrc];
    return getCopy (tmp, 0, nDst);
  }

  /**
   * Get an array that contains all elements, except for the passed elements.
   *
   * @param aArray
   *        The source array. May be <code>null</code>.
   * @param aElementsToRemove
   *        The elements to skip.
   * @return <code>null</code> if the passed array is <code>null</code>. The
   *         original array, if no elements need to be skipped. A non-
   *         <code>null</code> copy of the array without the passed elements
   *         otherwise.
   */
  @Nullable
  @ReturnsMutableCopy
  public static boolean [] getAllExcept (@Nullable final boolean [] aArray,
                                         @Nullable final boolean... aElementsToRemove)
  {
    if (isEmpty (aArray) || isEmpty (aElementsToRemove))
      return aArray;

    final boolean [] tmp = getCopy (aArray);
    int nDst = 0;
    for (int nSrc = 0; nSrc < tmp.length; ++nSrc)
      if (!contains (aElementsToRemove, tmp[nSrc]))
        tmp[nDst++] = tmp[nSrc];
    return getCopy (tmp, 0, nDst);
  }

  /**
   * Get an array that contains all elements, except for the passed elements.
   *
   * @param aArray
   *        The source array. May be <code>null</code>.
   * @param aElementsToRemove
   *        The elements to skip.
   * @return <code>null</code> if the passed array is <code>null</code>. The
   *         original array, if no elements need to be skipped. A non-
   *         <code>null</code> copy of the array without the passed elements
   *         otherwise.
   */
  @Nullable
  @ReturnsMutableCopy
  public static byte [] getAllExcept (@Nullable final byte [] aArray, @Nullable final byte... aElementsToRemove)
  {
    if (isEmpty (aArray) || isEmpty (aElementsToRemove))
      return aArray;

    final byte [] tmp = getCopy (aArray);
    int nDst = 0;
    for (int nSrc = 0; nSrc < tmp.length; ++nSrc)
      if (!contains (aElementsToRemove, tmp[nSrc]))
        tmp[nDst++] = tmp[nSrc];
    return getCopy (tmp, 0, nDst);
  }

  /**
   * Get an array that contains all elements, except for the passed elements.
   *
   * @param aArray
   *        The source array. May be <code>null</code>.
   * @param aElementsToRemove
   *        The elements to skip.
   * @return <code>null</code> if the passed array is <code>null</code>. The
   *         original array, if no elements need to be skipped. A non-
   *         <code>null</code> copy of the array without the passed elements
   *         otherwise.
   */
  @Nullable
  @ReturnsMutableCopy
  public static char [] getAllExcept (@Nullable final char [] aArray, @Nullable final char... aElementsToRemove)
  {
    if (isEmpty (aArray) || isEmpty (aElementsToRemove))
      return aArray;

    final char [] tmp = getCopy (aArray);
    int nDst = 0;
    for (int nSrc = 0; nSrc < tmp.length; ++nSrc)
      if (!contains (aElementsToRemove, tmp[nSrc]))
        tmp[nDst++] = tmp[nSrc];
    return getCopy (tmp, 0, nDst);
  }

  /**
   * Get an array that contains all elements, except for the passed elements.
   *
   * @param aArray
   *        The source array. May be <code>null</code>.
   * @param aElementsToRemove
   *        The elements to skip.
   * @return <code>null</code> if the passed array is <code>null</code>. The
   *         original array, if no elements need to be skipped. A non-
   *         <code>null</code> copy of the array without the passed elements
   *         otherwise.
   */
  @Nullable
  @ReturnsMutableCopy
  public static double [] getAllExcept (@Nullable final double [] aArray, @Nullable final double... aElementsToRemove)
  {
    if (isEmpty (aArray) || isEmpty (aElementsToRemove))
      return aArray;

    final double [] tmp = getCopy (aArray);
    int nDst = 0;
    for (int nSrc = 0; nSrc < tmp.length; ++nSrc)
      if (!contains (aElementsToRemove, tmp[nSrc]))
        tmp[nDst++] = tmp[nSrc];
    return getCopy (tmp, 0, nDst);
  }

  /**
   * Get an array that contains all elements, except for the passed elements.
   *
   * @param aArray
   *        The source array. May be <code>null</code>.
   * @param aElementsToRemove
   *        The elements to skip.
   * @return <code>null</code> if the passed array is <code>null</code>. The
   *         original array, if no elements need to be skipped. A non-
   *         <code>null</code> copy of the array without the passed elements
   *         otherwise.
   */
  @Nullable
  @ReturnsMutableCopy
  public static float [] getAllExcept (@Nullable final float [] aArray, @Nullable final float... aElementsToRemove)
  {
    if (isEmpty (aArray) || isEmpty (aElementsToRemove))
      return aArray;

    final float [] tmp = getCopy (aArray);
    int nDst = 0;
    for (int nSrc = 0; nSrc < tmp.length; ++nSrc)
      if (!contains (aElementsToRemove, tmp[nSrc]))
        tmp[nDst++] = tmp[nSrc];
    return getCopy (tmp, 0, nDst);
  }

  /**
   * Get an array that contains all elements, except for the passed elements.
   *
   * @param aArray
   *        The source array. May be <code>null</code>.
   * @param aElementsToRemove
   *        The elements to skip.
   * @return <code>null</code> if the passed array is <code>null</code>. The
   *         original array, if no elements need to be skipped. A non-
   *         <code>null</code> copy of the array without the passed elements
   *         otherwise.
   */
  @Nullable
  @ReturnsMutableCopy
  public static int [] getAllExcept (@Nullable final int [] aArray, @Nullable final int... aElementsToRemove)
  {
    if (isEmpty (aArray) || isEmpty (aElementsToRemove))
      return aArray;

    final int [] tmp = getCopy (aArray);
    int nDst = 0;
    for (int nSrc = 0; nSrc < tmp.length; ++nSrc)
      if (!contains (aElementsToRemove, tmp[nSrc]))
        tmp[nDst++] = tmp[nSrc];
    return getCopy (tmp, 0, nDst);
  }

  /**
   * Get an array that contains all elements, except for the passed elements.
   *
   * @param aArray
   *        The source array. May be <code>null</code>.
   * @param aElementsToRemove
   *        The elements to skip.
   * @return <code>null</code> if the passed array is <code>null</code>. The
   *         original array, if no elements need to be skipped. A non-
   *         <code>null</code> copy of the array without the passed elements
   *         otherwise.
   */
  @Nullable
  @ReturnsMutableCopy
  public static long [] getAllExcept (@Nullable final long [] aArray, @Nullable final long... aElementsToRemove)
  {
    if (isEmpty (aArray) || isEmpty (aElementsToRemove))
      return aArray;

    final long [] tmp = getCopy (aArray);
    int nDst = 0;
    for (int nSrc = 0; nSrc < tmp.length; ++nSrc)
      if (!contains (aElementsToRemove, tmp[nSrc]))
        tmp[nDst++] = tmp[nSrc];
    return getCopy (tmp, 0, nDst);
  }

  /**
   * Get an array that contains all elements, except for the passed elements.
   *
   * @param aArray
   *        The source array. May be <code>null</code>.
   * @param aElementsToRemove
   *        The elements to skip.
   * @return <code>null</code> if the passed array is <code>null</code>. The
   *         original array, if no elements need to be skipped. A non-
   *         <code>null</code> copy of the array without the passed elements
   *         otherwise.
   */
  @Nullable
  @ReturnsMutableCopy
  public static short [] getAllExcept (@Nullable final short [] aArray, @Nullable final short... aElementsToRemove)
  {
    if (isEmpty (aArray) || isEmpty (aElementsToRemove))
      return aArray;

    final short [] tmp = getCopy (aArray);
    int nDst = 0;
    for (int nSrc = 0; nSrc < tmp.length; ++nSrc)
      if (!contains (aElementsToRemove, tmp[nSrc]))
        tmp[nDst++] = tmp[nSrc];
    return getCopy (tmp, 0, nDst);
  }

  /**
   * Get an array that contains all elements, except for the first element.
   *
   * @param aArray
   *        The source array. May be <code>null</code>.
   * @return <code>null</code> if the passed array is <code>null</code> or has
   *         less than one element. A non-<code>null</code> copy of the array
   *         without the first element otherwise.
   */
  @Nullable
  @ReturnsMutableCopy
  public static boolean [] getAllExceptFirst (@Nullable final boolean... aArray)
  {
    return getAllExceptFirst (aArray, 1);
  }

  /**
   * Get an array that contains all elements, except for the first <em>n</em>
   * elements.
   *
   * @param aArray
   *        The source array. May be <code>null</code>.
   * @param nElementsToSkip
   *        The number of elements to skip. Must be &gt;= 0!
   * @return <code>null</code> if the passed array is <code>null</code> or has
   *         &le; elements than elements to be skipped. A non-<code>null</code>
   *         copy of the array without the first elements otherwise.
   */
  @Nullable
  @ReturnsMutableCopy
  public static boolean [] getAllExceptFirst (@Nullable final boolean [] aArray, @Nonnegative final int nElementsToSkip)
  {
    ValueEnforcer.isGE0 (nElementsToSkip, "ElementsToSkip");

    if (nElementsToSkip == 0)
      return aArray;
    if (aArray == null || nElementsToSkip >= aArray.length)
      return null;
    return getCopy (aArray, nElementsToSkip, aArray.length - nElementsToSkip);
  }

  /**
   * Get an array that contains all elements, except for the first element.
   *
   * @param aArray
   *        The source array. May be <code>null</code>.
   * @return <code>null</code> if the passed array is <code>null</code> or has
   *         less than one element. A non-<code>null</code> copy of the array
   *         without the first element otherwise.
   */
  @Nullable
  @ReturnsMutableCopy
  public static byte [] getAllExceptFirst (@Nullable final byte... aArray)
  {
    return getAllExceptFirst (aArray, 1);
  }

  /**
   * Get an array that contains all elements, except for the first <em>n</em>
   * elements.
   *
   * @param aArray
   *        The source array. May be <code>null</code>.
   * @param nElementsToSkip
   *        The number of elements to skip. Must be &gt;= 0!
   * @return <code>null</code> if the passed array is <code>null</code> or has
   *         &le; elements than elements to be skipped. A non-<code>null</code>
   *         copy of the array without the first elements otherwise.
   */
  @Nullable
  @ReturnsMutableCopy
  public static byte [] getAllExceptFirst (@Nullable final byte [] aArray, @Nonnegative final int nElementsToSkip)
  {
    ValueEnforcer.isGE0 (nElementsToSkip, "ElementsToSkip");

    if (nElementsToSkip == 0)
      return aArray;
    if (aArray == null || nElementsToSkip >= aArray.length)
      return null;
    return getCopy (aArray, nElementsToSkip, aArray.length - nElementsToSkip);
  }

  /**
   * Get an array that contains all elements, except for the first element.
   *
   * @param aArray
   *        The source array. May be <code>null</code>.
   * @return <code>null</code> if the passed array is <code>null</code> or has
   *         less than one element. A non-<code>null</code> copy of the array
   *         without the first element otherwise.
   */
  @Nullable
  @ReturnsMutableCopy
  public static char [] getAllExceptFirst (@Nullable final char... aArray)
  {
    return getAllExceptFirst (aArray, 1);
  }

  /**
   * Get an array that contains all elements, except for the first <em>n</em>
   * elements.
   *
   * @param aArray
   *        The source array. May be <code>null</code>.
   * @param nElementsToSkip
   *        The number of elements to skip. Must be &gt;= 0!
   * @return <code>null</code> if the passed array is <code>null</code> or has
   *         &le; elements than elements to be skipped. A non-<code>null</code>
   *         copy of the array without the first elements otherwise.
   */
  @Nullable
  @ReturnsMutableCopy
  public static char [] getAllExceptFirst (@Nullable final char [] aArray, @Nonnegative final int nElementsToSkip)
  {
    ValueEnforcer.isGE0 (nElementsToSkip, "ElementsToSkip");

    if (nElementsToSkip == 0)
      return aArray;
    if (aArray == null || nElementsToSkip >= aArray.length)
      return null;
    return getCopy (aArray, nElementsToSkip, aArray.length - nElementsToSkip);
  }

  /**
   * Get an array that contains all elements, except for the first element.
   *
   * @param aArray
   *        The source array. May be <code>null</code>.
   * @return <code>null</code> if the passed array is <code>null</code> or has
   *         less than one element. A non-<code>null</code> copy of the array
   *         without the first element otherwise.
   */
  @Nullable
  @ReturnsMutableCopy
  public static double [] getAllExceptFirst (@Nullable final double... aArray)
  {
    return getAllExceptFirst (aArray, 1);
  }

  /**
   * Get an array that contains all elements, except for the first <em>n</em>
   * elements.
   *
   * @param aArray
   *        The source array. May be <code>null</code>.
   * @param nElementsToSkip
   *        The number of elements to skip. Must be &gt;= 0!
   * @return <code>null</code> if the passed array is <code>null</code> or has
   *         &le; elements than elements to be skipped. A non-<code>null</code>
   *         copy of the array without the first elements otherwise.
   */
  @Nullable
  @ReturnsMutableCopy
  public static double [] getAllExceptFirst (@Nullable final double [] aArray, @Nonnegative final int nElementsToSkip)
  {
    ValueEnforcer.isGE0 (nElementsToSkip, "ElementsToSkip");

    if (nElementsToSkip == 0)
      return aArray;
    if (aArray == null || nElementsToSkip >= aArray.length)
      return null;
    return getCopy (aArray, nElementsToSkip, aArray.length - nElementsToSkip);
  }

  /**
   * Get an array that contains all elements, except for the first element.
   *
   * @param aArray
   *        The source array. May be <code>null</code>.
   * @return <code>null</code> if the passed array is <code>null</code> or has
   *         less than one element. A non-<code>null</code> copy of the array
   *         without the first element otherwise.
   */
  @Nullable
  @ReturnsMutableCopy
  public static float [] getAllExceptFirst (@Nullable final float... aArray)
  {
    return getAllExceptFirst (aArray, 1);
  }

  /**
   * Get an array that contains all elements, except for the first <em>n</em>
   * elements.
   *
   * @param aArray
   *        The source array. May be <code>null</code>.
   * @param nElementsToSkip
   *        The number of elements to skip. Must be &gt;= 0!
   * @return <code>null</code> if the passed array is <code>null</code> or has
   *         &le; elements than elements to be skipped. A non-<code>null</code>
   *         copy of the array without the first elements otherwise.
   */
  @Nullable
  @ReturnsMutableCopy
  public static float [] getAllExceptFirst (@Nullable final float [] aArray, @Nonnegative final int nElementsToSkip)
  {
    ValueEnforcer.isGE0 (nElementsToSkip, "ElementsToSkip");

    if (nElementsToSkip == 0)
      return aArray;
    if (aArray == null || nElementsToSkip >= aArray.length)
      return null;
    return getCopy (aArray, nElementsToSkip, aArray.length - nElementsToSkip);
  }

  /**
   * Get an array that contains all elements, except for the first element.
   *
   * @param aArray
   *        The source array. May be <code>null</code>.
   * @return <code>null</code> if the passed array is <code>null</code> or has
   *         less than one element. A non-<code>null</code> copy of the array
   *         without the first element otherwise.
   */
  @Nullable
  @ReturnsMutableCopy
  public static int [] getAllExceptFirst (@Nullable final int... aArray)
  {
    return getAllExceptFirst (aArray, 1);
  }

  /**
   * Get an array that contains all elements, except for the first <em>n</em>
   * elements.
   *
   * @param aArray
   *        The source array. May be <code>null</code>.
   * @param nElementsToSkip
   *        The number of elements to skip. Must be &gt;= 0!
   * @return <code>null</code> if the passed array is <code>null</code> or has
   *         &le; elements than elements to be skipped. A non-<code>null</code>
   *         copy of the array without the first elements otherwise.
   */
  @Nullable
  @ReturnsMutableCopy
  public static int [] getAllExceptFirst (@Nullable final int [] aArray, @Nonnegative final int nElementsToSkip)
  {
    ValueEnforcer.isGE0 (nElementsToSkip, "ElementsToSkip");

    if (nElementsToSkip == 0)
      return aArray;
    if (aArray == null || nElementsToSkip >= aArray.length)
      return null;
    return getCopy (aArray, nElementsToSkip, aArray.length - nElementsToSkip);
  }

  /**
   * Get an array that contains all elements, except for the first element.
   *
   * @param aArray
   *        The source array. May be <code>null</code>.
   * @return <code>null</code> if the passed array is <code>null</code> or has
   *         less than one element. A non-<code>null</code> copy of the array
   *         without the first element otherwise.
   */
  @Nullable
  @ReturnsMutableCopy
  public static long [] getAllExceptFirst (@Nullable final long... aArray)
  {
    return getAllExceptFirst (aArray, 1);
  }

  /**
   * Get an array that contains all elements, except for the first <em>n</em>
   * elements.
   *
   * @param aArray
   *        The source array. May be <code>null</code>.
   * @param nElementsToSkip
   *        The number of elements to skip. Must be &gt;= 0!
   * @return <code>null</code> if the passed array is <code>null</code> or has
   *         &le; elements than elements to be skipped. A non-<code>null</code>
   *         copy of the array without the first elements otherwise.
   */
  @Nullable
  @ReturnsMutableCopy
  public static long [] getAllExceptFirst (@Nullable final long [] aArray, @Nonnegative final int nElementsToSkip)
  {
    ValueEnforcer.isGE0 (nElementsToSkip, "ElementsToSkip");

    if (nElementsToSkip == 0)
      return aArray;
    if (aArray == null || nElementsToSkip >= aArray.length)
      return null;
    return getCopy (aArray, nElementsToSkip, aArray.length - nElementsToSkip);
  }

  /**
   * Get an array that contains all elements, except for the first element.
   *
   * @param aArray
   *        The source array. May be <code>null</code>.
   * @return <code>null</code> if the passed array is <code>null</code> or has
   *         less than one element. A non-<code>null</code> copy of the array
   *         without the first element otherwise.
   */
  @Nullable
  @ReturnsMutableCopy
  public static short [] getAllExceptFirst (@Nullable final short... aArray)
  {
    return getAllExceptFirst (aArray, 1);
  }

  /**
   * Get an array that contains all elements, except for the first <em>n</em>
   * elements.
   *
   * @param aArray
   *        The source array. May be <code>null</code>.
   * @param nElementsToSkip
   *        The number of elements to skip. Must be &gt;= 0!
   * @return <code>null</code> if the passed array is <code>null</code> or has
   *         &le; elements than elements to be skipped. A non-<code>null</code>
   *         copy of the array without the first elements otherwise.
   */
  @Nullable
  @ReturnsMutableCopy
  public static short [] getAllExceptFirst (@Nullable final short [] aArray, @Nonnegative final int nElementsToSkip)
  {
    ValueEnforcer.isGE0 (nElementsToSkip, "ElementsToSkip");

    if (nElementsToSkip == 0)
      return aArray;
    if (aArray == null || nElementsToSkip >= aArray.length)
      return null;
    return getCopy (aArray, nElementsToSkip, aArray.length - nElementsToSkip);
  }

  /**
   * Get an array that contains all elements, except for the last element.
   *
   * @param <ELEMENTTYPE>
   *        Array element type
   * @param aArray
   *        The source array. May be <code>null</code>.
   * @return <code>null</code> if the passed array is <code>null</code> or has
   *         less than one element. A non-<code>null</code> copy of the array
   *         without the last element otherwise.
   */
  @Nullable
  @ReturnsMutableCopy
  @SafeVarargs
  public static <ELEMENTTYPE> ELEMENTTYPE [] getAllExceptLast (@Nullable final ELEMENTTYPE... aArray)
  {
    return getAllExceptLast (aArray, 1);
  }

  /**
   * Get an array that contains all elements, except for the last <em>n</em>
   * elements.
   *
   * @param <ELEMENTTYPE>
   *        Type of element
   * @param aArray
   *        The source array. May be <code>null</code>.
   * @param nElementsToSkip
   *        The number of elements to skip. Must be &gt;= 0!
   * @return <code>null</code> if the passed array is <code>null</code> or has
   *         &le; elements than elements to be skipped. A non-<code>null</code>
   *         copy of the array without the last elements otherwise.
   */
  @Nullable
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> ELEMENTTYPE [] getAllExceptLast (@Nullable final ELEMENTTYPE [] aArray,
                                                               @Nonnegative final int nElementsToSkip)
  {
    ValueEnforcer.isGE0 (nElementsToSkip, "ElementsToSkip");

    if (nElementsToSkip == 0)
      return aArray;
    if (aArray == null || nElementsToSkip >= aArray.length)
      return null;
    return getCopy (aArray, 0, aArray.length - nElementsToSkip);
  }

  /**
   * Get an array that contains all elements, except for the last element.
   *
   * @param aArray
   *        The source array. May be <code>null</code>.
   * @return <code>null</code> if the passed array is <code>null</code> or has
   *         less than one element. A non-<code>null</code> copy of the array
   *         without the last element otherwise.
   */
  @Nullable
  @ReturnsMutableCopy
  public static boolean [] getAllExceptLast (@Nullable final boolean... aArray)
  {
    return getAllExceptLast (aArray, 1);
  }

  /**
   * Get an array that contains all elements, except for the last <em>n</em>
   * elements.
   *
   * @param aArray
   *        The source array. May be <code>null</code>.
   * @param nElementsToSkip
   *        The number of elements to skip. Must be &gt;= 0!
   * @return <code>null</code> if the passed array is <code>null</code> or has
   *         &le; elements than elements to be skipped. A non-<code>null</code>
   *         copy of the array without the last elements otherwise.
   */
  @Nullable
  @ReturnsMutableCopy
  public static boolean [] getAllExceptLast (@Nullable final boolean [] aArray, @Nonnegative final int nElementsToSkip)
  {
    ValueEnforcer.isGE0 (nElementsToSkip, "ElementsToSkip");

    if (nElementsToSkip == 0)
      return aArray;
    if (aArray == null || nElementsToSkip >= aArray.length)
      return null;
    return getCopy (aArray, 0, aArray.length - nElementsToSkip);
  }

  /**
   * Get an array that contains all elements, except for the last element.
   *
   * @param aArray
   *        The source array. May be <code>null</code>.
   * @return <code>null</code> if the passed array is <code>null</code> or has
   *         less than one element. A non-<code>null</code> copy of the array
   *         without the last element otherwise.
   */
  @Nullable
  @ReturnsMutableCopy
  public static byte [] getAllExceptLast (@Nullable final byte... aArray)
  {
    return getAllExceptLast (aArray, 1);
  }

  /**
   * Get an array that contains all elements, except for the last <em>n</em>
   * elements.
   *
   * @param aArray
   *        The source array. May be <code>null</code>.
   * @param nElementsToSkip
   *        The number of elements to skip. Must be &gt;= 0!
   * @return <code>null</code> if the passed array is <code>null</code> or has
   *         &le; elements than elements to be skipped. A non-<code>null</code>
   *         copy of the array without the last elements otherwise.
   */
  @Nullable
  @ReturnsMutableCopy
  public static byte [] getAllExceptLast (@Nullable final byte [] aArray, @Nonnegative final int nElementsToSkip)
  {
    ValueEnforcer.isGE0 (nElementsToSkip, "ElementsToSkip");

    if (nElementsToSkip == 0)
      return aArray;
    if (aArray == null || nElementsToSkip >= aArray.length)
      return null;
    return getCopy (aArray, 0, aArray.length - nElementsToSkip);
  }

  /**
   * Get an array that contains all elements, except for the last element.
   *
   * @param aArray
   *        The source array. May be <code>null</code>.
   * @return <code>null</code> if the passed array is <code>null</code> or has
   *         less than one element. A non-<code>null</code> copy of the array
   *         without the last element otherwise.
   */
  @Nullable
  @ReturnsMutableCopy
  public static char [] getAllExceptLast (@Nullable final char... aArray)
  {
    return getAllExceptLast (aArray, 1);
  }

  /**
   * Get an array that contains all elements, except for the last <em>n</em>
   * elements.
   *
   * @param aArray
   *        The source array. May be <code>null</code>.
   * @param nElementsToSkip
   *        The number of elements to skip. Must be &gt;= 0!
   * @return <code>null</code> if the passed array is <code>null</code> or has
   *         &le; elements than elements to be skipped. A non-<code>null</code>
   *         copy of the array without the last elements otherwise.
   */
  @Nullable
  @ReturnsMutableCopy
  public static char [] getAllExceptLast (@Nullable final char [] aArray, @Nonnegative final int nElementsToSkip)
  {
    ValueEnforcer.isGE0 (nElementsToSkip, "ElementsToSkip");

    if (nElementsToSkip == 0)
      return aArray;
    if (aArray == null || nElementsToSkip >= aArray.length)
      return null;
    return getCopy (aArray, 0, aArray.length - nElementsToSkip);
  }

  /**
   * Get an array that contains all elements, except for the last element.
   *
   * @param aArray
   *        The source array. May be <code>null</code>.
   * @return <code>null</code> if the passed array is <code>null</code> or has
   *         less than one element. A non-<code>null</code> copy of the array
   *         without the last element otherwise.
   */
  @Nullable
  @ReturnsMutableCopy
  public static double [] getAllExceptLast (@Nullable final double... aArray)
  {
    return getAllExceptLast (aArray, 1);
  }

  /**
   * Get an array that contains all elements, except for the last <em>n</em>
   * elements.
   *
   * @param aArray
   *        The source array. May be <code>null</code>.
   * @param nElementsToSkip
   *        The number of elements to skip. Must be &gt;= 0!
   * @return <code>null</code> if the passed array is <code>null</code> or has
   *         &le; elements than elements to be skipped. A non-<code>null</code>
   *         copy of the array without the last elements otherwise.
   */
  @Nullable
  @ReturnsMutableCopy
  public static double [] getAllExceptLast (@Nullable final double [] aArray, @Nonnegative final int nElementsToSkip)
  {
    ValueEnforcer.isGE0 (nElementsToSkip, "ElementsToSkip");

    if (nElementsToSkip == 0)
      return aArray;
    if (aArray == null || nElementsToSkip >= aArray.length)
      return null;
    return getCopy (aArray, 0, aArray.length - nElementsToSkip);
  }

  /**
   * Get an array that contains all elements, except for the last element.
   *
   * @param aArray
   *        The source array. May be <code>null</code>.
   * @return <code>null</code> if the passed array is <code>null</code> or has
   *         less than one element. A non-<code>null</code> copy of the array
   *         without the last element otherwise.
   */
  @Nullable
  @ReturnsMutableCopy
  public static float [] getAllExceptLast (@Nullable final float... aArray)
  {
    return getAllExceptLast (aArray, 1);
  }

  /**
   * Get an array that contains all elements, except for the last <em>n</em>
   * elements.
   *
   * @param aArray
   *        The source array. May be <code>null</code>.
   * @param nElementsToSkip
   *        The number of elements to skip. Must be &gt;= 0!
   * @return <code>null</code> if the passed array is <code>null</code> or has
   *         &le; elements than elements to be skipped. A non-<code>null</code>
   *         copy of the array without the last elements otherwise.
   */
  @Nullable
  @ReturnsMutableCopy
  public static float [] getAllExceptLast (@Nullable final float [] aArray, @Nonnegative final int nElementsToSkip)
  {
    ValueEnforcer.isGE0 (nElementsToSkip, "ElementsToSkip");

    if (nElementsToSkip == 0)
      return aArray;
    if (aArray == null || nElementsToSkip >= aArray.length)
      return null;
    return getCopy (aArray, 0, aArray.length - nElementsToSkip);
  }

  /**
   * Get an array that contains all elements, except for the last element.
   *
   * @param aArray
   *        The source array. May be <code>null</code>.
   * @return <code>null</code> if the passed array is <code>null</code> or has
   *         less than one element. A non-<code>null</code> copy of the array
   *         without the last element otherwise.
   */
  @Nullable
  @ReturnsMutableCopy
  public static int [] getAllExceptLast (@Nullable final int... aArray)
  {
    return getAllExceptLast (aArray, 1);
  }

  /**
   * Get an array that contains all elements, except for the last <em>n</em>
   * elements.
   *
   * @param aArray
   *        The source array. May be <code>null</code>.
   * @param nElementsToSkip
   *        The number of elements to skip. Must be &gt;= 0!
   * @return <code>null</code> if the passed array is <code>null</code> or has
   *         &le; elements than elements to be skipped. A non-<code>null</code>
   *         copy of the array without the last elements otherwise.
   */
  @Nullable
  @ReturnsMutableCopy
  public static int [] getAllExceptLast (@Nullable final int [] aArray, @Nonnegative final int nElementsToSkip)
  {
    ValueEnforcer.isGE0 (nElementsToSkip, "ElementsToSkip");

    if (nElementsToSkip == 0)
      return aArray;
    if (aArray == null || nElementsToSkip >= aArray.length)
      return null;
    return getCopy (aArray, 0, aArray.length - nElementsToSkip);
  }

  /**
   * Get an array that contains all elements, except for the last element.
   *
   * @param aArray
   *        The source array. May be <code>null</code>.
   * @return <code>null</code> if the passed array is <code>null</code> or has
   *         less than one element. A non-<code>null</code> copy of the array
   *         without the last element otherwise.
   */
  @Nullable
  @ReturnsMutableCopy
  public static long [] getAllExceptLast (@Nullable final long... aArray)
  {
    return getAllExceptLast (aArray, 1);
  }

  /**
   * Get an array that contains all elements, except for the last <em>n</em>
   * elements.
   *
   * @param aArray
   *        The source array. May be <code>null</code>.
   * @param nElementsToSkip
   *        The number of elements to skip. Must be &gt;= 0!
   * @return <code>null</code> if the passed array is <code>null</code> or has
   *         &le; elements than elements to be skipped. A non-<code>null</code>
   *         copy of the array without the last elements otherwise.
   */
  @Nullable
  @ReturnsMutableCopy
  public static long [] getAllExceptLast (@Nullable final long [] aArray, @Nonnegative final int nElementsToSkip)
  {
    ValueEnforcer.isGE0 (nElementsToSkip, "ElementsToSkip");

    if (nElementsToSkip == 0)
      return aArray;
    if (aArray == null || nElementsToSkip >= aArray.length)
      return null;
    return getCopy (aArray, 0, aArray.length - nElementsToSkip);
  }

  /**
   * Get an array that contains all elements, except for the last element.
   *
   * @param aArray
   *        The source array. May be <code>null</code>.
   * @return <code>null</code> if the passed array is <code>null</code> or has
   *         less than one element. A non-<code>null</code> copy of the array
   *         without the last element otherwise.
   */
  @Nullable
  @ReturnsMutableCopy
  public static short [] getAllExceptLast (@Nullable final short... aArray)
  {
    return getAllExceptLast (aArray, 1);
  }

  /**
   * Get an array that contains all elements, except for the last <em>n</em>
   * elements.
   *
   * @param aArray
   *        The source array. May be <code>null</code>.
   * @param nElementsToSkip
   *        The number of elements to skip. Must be &gt;= 0!
   * @return <code>null</code> if the passed array is <code>null</code> or has
   *         &le; elements than elements to be skipped. A non-<code>null</code>
   *         copy of the array without the last elements otherwise.
   */
  @Nullable
  @ReturnsMutableCopy
  public static short [] getAllExceptLast (@Nullable final short [] aArray, @Nonnegative final int nElementsToSkip)
  {
    ValueEnforcer.isGE0 (nElementsToSkip, "ElementsToSkip");

    if (nElementsToSkip == 0)
      return aArray;
    if (aArray == null || nElementsToSkip >= aArray.length)
      return null;
    return getCopy (aArray, 0, aArray.length - nElementsToSkip);
  }

  /**
   * Helper method to easily create an array from constant values.
   *
   * @param aArray
   *        The list of values for the array. May be <code>null</code>.
   * @return The passed array. <code>null</code> if the passed array was
   *         <code>null</code>.
   */
  @Nullable
  @ReturnsMutableObject ("use getCopy otherwise")
  public static boolean [] newBooleanArray (@Nullable final boolean... aArray)
  {
    return aArray;
  }

  /**
   * Helper method to easily create an array from constant values.
   *
   * @param aArray
   *        The list of values for the array. May be <code>null</code>.
   * @return The passed array. <code>null</code> if the passed array was
   *         <code>null</code>.
   */
  @Nullable
  @ReturnsMutableObject ("use getCopy otherwise")
  public static byte [] newByteArray (@Nullable final byte... aArray)
  {
    return aArray;
  }

  /**
   * Helper method to easily create an array from constant values.
   *
   * @param aArray
   *        The list of values for the array. May be <code>null</code>.
   * @return The passed array. <code>null</code> if the passed array was
   *         <code>null</code>.
   */
  @Nullable
  @ReturnsMutableObject ("use getCopy otherwise")
  public static char [] newCharArray (@Nullable final char... aArray)
  {
    return aArray;
  }

  /**
   * Helper method to easily create an array from constant values.
   *
   * @param aArray
   *        The list of values for the array. May be <code>null</code>.
   * @return The passed array. <code>null</code> if the passed array was
   *         <code>null</code>.
   */
  @Nullable
  @ReturnsMutableObject ("use getCopy otherwise")
  public static double [] newDoubleArray (@Nullable final double... aArray)
  {
    return aArray;
  }

  /**
   * Helper method to easily create an array from constant values.
   *
   * @param aArray
   *        The list of values for the array. May be <code>null</code>.
   * @return The passed array. <code>null</code> if the passed array was
   *         <code>null</code>.
   */
  @Nullable
  @ReturnsMutableObject ("use getCopy otherwise")
  public static float [] newFloatArray (@Nullable final float... aArray)
  {
    return aArray;
  }

  /**
   * Helper method to easily create an array from constant values.
   *
   * @param aArray
   *        The list of values for the array. May be <code>null</code>.
   * @return The passed array. <code>null</code> if the passed array was
   *         <code>null</code>.
   */
  @Nullable
  @ReturnsMutableObject ("use getCopy otherwise")
  public static int [] newIntArray (@Nullable final int... aArray)
  {
    return aArray;
  }

  /**
   * Helper method to easily create an array from constant values.
   *
   * @param aArray
   *        The list of values for the array. May be <code>null</code>.
   * @return The passed array. <code>null</code> if the passed array was
   *         <code>null</code>.
   */
  @Nullable
  @ReturnsMutableObject ("use getCopy otherwise")
  public static long [] newLongArray (@Nullable final long... aArray)
  {
    return aArray;
  }

  /**
   * Helper method to easily create an array from constant values.
   *
   * @param aArray
   *        The list of values for the array. May be <code>null</code>.
   * @return The passed array. <code>null</code> if the passed array was
   *         <code>null</code>.
   */
  @Nullable
  @ReturnsMutableObject ("use getCopy otherwise")
  public static short [] newShortArray (@Nullable final short... aArray)
  {
    return aArray;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> ELEMENTTYPE [] newArray (@Nonnull final Class <? extends ELEMENTTYPE> aClass,
                                                       @Nonnegative final int nSize)
  {
    ValueEnforcer.notNull (aClass, "class");
    if (aClass.isPrimitive ())
      throw new IllegalArgumentException ("Argument cannot be primitive: " + aClass);
    ValueEnforcer.isGE0 (nSize, "Size");

    final Object aArray = Array.newInstance (aClass, nSize);
    return GenericReflection.<Object, ELEMENTTYPE []> uncheckedCast (aArray);
  }

  /**
   * Create a new empty array with the same type as the passed array.
   *
   * @param <ELEMENTTYPE>
   *        Type of element
   * @param aArray
   *        Source array. May not be <code>null</code>.
   * @param nSize
   *        Destination size. Must be &ge; 0.
   * @return Never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> ELEMENTTYPE [] newArraySameType (@Nonnull final ELEMENTTYPE [] aArray,
                                                               @Nonnegative final int nSize)
  {
    return newArray (getComponentType (aArray), nSize);
  }

  /**
   * Create a new array with the elements in the passed collection..
   *
   * @param <ELEMENTTYPE>
   *        Type of element
   * @param aCollection
   *        The collection to be converted to an array. May be <code>null</code>
   *        .
   * @param aClass
   *        The class of the elements inside the collection. May not be
   *        <code>null</code>.
   * @return <code>null</code> if the passed collection is empty, a non-
   *         <code>null</code> array with all elements of the collection
   *         otherwise.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> ELEMENTTYPE [] newArray (@Nullable final Collection <? extends ELEMENTTYPE> aCollection,
                                                       @Nonnull final Class <ELEMENTTYPE> aClass)
  {
    ValueEnforcer.notNull (aClass, "class");

    if (CollectionHelper.isEmpty (aCollection))
      return newArray (aClass, 0);

    final ELEMENTTYPE [] ret = newArray (aClass, aCollection.size ());
    return aCollection.toArray (ret);
  }

  /**
   * Wrapper that allows a single argument to be treated as an array.
   *
   * @param <ELEMENTTYPE>
   *        Type of element
   * @param aElement
   *        The element to be converted to an array. May be <code>null</code>.
   * @param aClass
   *        The class of the element. May not be <code>null</code>. Must be
   *        present because in case the passed element is <code>null</code>
   *        there is no way to determine the array component type!
   * @return The created array and never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> ELEMENTTYPE [] newArraySingleElement (@Nullable final ELEMENTTYPE aElement,
                                                                    @Nonnull final Class <ELEMENTTYPE> aClass)
  {
    ValueEnforcer.notNull (aClass, "class");

    final ELEMENTTYPE [] ret = newArray (aClass, 1);
    ret[0] = aElement;
    return ret;
  }

  /**
   * Wrapper that allows vararg arguments and returns the array. <br>
   * Note: this implementation is not available for basic types, because the
   * Eclipse compiler seems to have difficulties resolving vararg types
   * correctly.
   *
   * @param <ELEMENTTYPE>
   *        Type of element
   * @param aArray
   *        The vararg array
   * @return The wrapped array
   */
  @Nonnull
  @ReturnsMutableObject ("use getCopy otherwise")
  @SafeVarargs
  public static <ELEMENTTYPE> ELEMENTTYPE [] newArray (@Nonnull final ELEMENTTYPE... aArray)
  {
    ValueEnforcer.notNull (aArray, "Array");
    return aArray;
  }

  /**
   * Create a new array with a predefined number of elements containing the
   * passed value.
   *
   * @param <ELEMENTTYPE>
   *        The type of the array to be created.
   * @param nArraySize
   *        The size of the array to be created.
   * @param aValue
   *        The value to be set into each array element. May be
   *        <code>null</code>.
   * @param aClass
   *        The value class. May not be <code>null</code>. Must be present in
   *        case the passed value is <code>null</code>.
   * @return The created array filled with the given value.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> ELEMENTTYPE [] newArray (@Nonnegative final int nArraySize,
                                                       @Nonnull final ELEMENTTYPE aValue,
                                                       @Nonnull final Class <ELEMENTTYPE> aClass)
  {
    ValueEnforcer.isGE0 (nArraySize, "ArraySize");
    ValueEnforcer.notNull (aClass, "class");

    final ELEMENTTYPE [] ret = newArray (aClass, nArraySize);
    Arrays.fill (ret, aValue);
    return ret;
  }

  /**
   * Get the passed collection as an array of Object. If the passed collection
   * is <code>null</code> or empty, an empty array is returned.
   *
   * @param aCollection
   *        The collection to be converted. May be <code>null</code>.
   * @return Never <code>null</code>.
   */
  @Nullable
  @ReturnsMutableCopy
  public static Object [] getAsObjectArray (@Nullable final Collection <?> aCollection)
  {
    if (CollectionHelper.isEmpty (aCollection))
      return null;

    final Object [] ret = new Object [aCollection.size ()];
    return aCollection.toArray (ret);
  }

  @Nullable
  public static <ELEMENTTYPE> ELEMENTTYPE getSafeElement (@Nullable final ELEMENTTYPE [] aItems, final int nIndex)
  {
    return getSafeElement (aItems, nIndex, null);
  }

  @Nullable
  public static <ELEMENTTYPE> ELEMENTTYPE getSafeElement (@Nullable final ELEMENTTYPE [] aItems,
                                                          final int nIndex,
                                                          @Nullable final ELEMENTTYPE aDefault)
  {
    return aItems != null && nIndex >= 0 && nIndex < aItems.length ? aItems[nIndex] : aDefault;
  }

  /**
   * Recursive equal comparison for arrays.
   *
   * @param aHeadArray
   *        First array. May be <code>null</code>.
   * @param aTailArray
   *        Second array. May be <code>null</code>.
   * @return <code>true</code> only if the arrays and all contained elements are
   *         recursively equal.
   */
  public static boolean isArrayEquals (@Nullable final Object aHeadArray, @Nullable final Object aTailArray)
  {
    // Same objects?
    if (aHeadArray == aTailArray)
      return true;

    // Any of the null -> different because they are not both null
    if (aHeadArray == null || aTailArray == null)
      return false;

    // If any of the passed object is not an array -> not equal as an array,
    // even if they are equal!
    if (!isArray (aHeadArray) || !isArray (aTailArray))
      return false;

    // Different component type?
    if (!aHeadArray.getClass ().getComponentType ().equals (aTailArray.getClass ().getComponentType ()))
      return false;

    // Different length?
    final int nLength = Array.getLength (aHeadArray);
    if (nLength != Array.getLength (aTailArray))
      return false;

    // Compare step by step
    for (int i = 0; i < nLength; i++)
    {
      final Object aItem1 = Array.get (aHeadArray, i);
      final Object aItem2 = Array.get (aTailArray, i);
      if (isArray (aItem1) && isArray (aItem2))
      {
        // Recursive call
        if (!isArrayEquals (aItem1, aItem2))
          return false;
      }
      else
      {
        // Use equals implementation
        if (!EqualsHelper.equals (aItem1, aItem2))
          return false;
      }
    }

    // No differences found!
    return true;
  }

  /**
   * Check if the passed array contains at least one <code>null</code> element.
   *
   * @param <T>
   *        element type
   * @param aArray
   *        The array to check. May be <code>null</code>.
   * @return <code>true</code> only if the passed array is neither
   *         <code>null</code> nor empty and if at least one <code>null</code>
   *         element is contained.
   */
  public static <T> boolean containsAnyNullElement (@Nullable final T [] aArray)
  {
    if (aArray != null)
      for (final T aObj : aArray)
        if (aObj == null)
          return true;
    return false;
  }

  /**
   * Check if the passed array contains only <code>null</code> element.
   *
   * @param <T>
   *        element type
   * @param aArray
   *        The array to check. May be <code>null</code>.
   * @return <code>true</code> only if the passed array is neither
   *         <code>null</code> nor empty and if at least one <code>null</code>
   *         element is contained.
   */
  public static <T> boolean containsOnlyNullElements (@Nullable final T [] aArray)
  {
    if (isEmpty (aArray))
      return false;

    for (final Object aObj : aArray)
      if (aObj != null)
        return false;
    return true;
  }

  @SuppressWarnings ("null")
  @Nullable
  public static <ELEMENTTYPE> ELEMENTTYPE findFirst (@Nullable final ELEMENTTYPE [] aArray,
                                                     @Nullable final Predicate <? super ELEMENTTYPE> aFilter)
  {
    return findFirst (aArray, aFilter, (ELEMENTTYPE) null);
  }

  @Nullable
  public static <ELEMENTTYPE> ELEMENTTYPE findFirst (@Nullable final ELEMENTTYPE [] aArray,
                                                     @Nullable final Predicate <? super ELEMENTTYPE> aFilter,
                                                     @Nullable final ELEMENTTYPE aDefault)
  {
    if (aFilter == null)
      return getFirst (aArray);

    if (isNotEmpty (aArray))
      for (final ELEMENTTYPE aElement : aArray)
        if (aFilter.test (aElement))
          return aElement;

    return aDefault;
  }

  @SuppressWarnings ("null")
  @Nullable
  public static <ELEMENTTYPE, RETTYPE> RETTYPE findFirst (@Nullable final ELEMENTTYPE [] aArray,
                                                          @Nullable final Predicate <? super ELEMENTTYPE> aFilter,
                                                          @Nonnull final Function <? super ELEMENTTYPE, RETTYPE> aMapper)
  {
    return findFirst (aArray, aFilter, aMapper, (RETTYPE) null);
  }

  @Nullable
  public static <ELEMENTTYPE, RETTYPE> RETTYPE findFirst (@Nullable final ELEMENTTYPE [] aArray,
                                                          @Nullable final Predicate <? super ELEMENTTYPE> aFilter,
                                                          @Nonnull final Function <? super ELEMENTTYPE, RETTYPE> aMapper,
                                                          @Nullable final RETTYPE aDefault)
  {
    ValueEnforcer.notNull (aMapper, "Mapper");

    if (isNotEmpty (aArray))
    {
      if (aFilter == null)
        return aMapper.apply (aArray[0]);

      for (final ELEMENTTYPE aElement : aArray)
        if (aFilter.test (aElement))
          return aMapper.apply (aElement);
    }

    return aDefault;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> List <ELEMENTTYPE> getAll (@Nullable final ELEMENTTYPE [] aArray,
                                                         @Nullable final Predicate <? super ELEMENTTYPE> aFilter)
  {
    if (aFilter == null)
      return CollectionHelper.newList (aArray);

    final List <ELEMENTTYPE> ret = new ArrayList <> ();
    if (isNotEmpty (aArray))
      for (final ELEMENTTYPE aElement : aArray)
        if (aFilter.test (aElement))
          ret.add (aElement);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE, RETTYPE> List <RETTYPE> getAll (@Nullable final ELEMENTTYPE [] aArray,
                                                              @Nullable final Predicate <? super ELEMENTTYPE> aFilter,
                                                              @Nonnull final Function <? super ELEMENTTYPE, RETTYPE> aMapper)
  {
    ValueEnforcer.notNull (aMapper, "Mapper");
    if (aFilter == null)
      return CollectionHelper.newListMapped (aArray, aMapper);

    final List <RETTYPE> ret = new ArrayList <> ();
    if (isNotEmpty (aArray))
      for (final ELEMENTTYPE aElement : aArray)
        if (aFilter.test (aElement))
          ret.add (aMapper.apply (aElement));
    return ret;
  }

  @Nonnegative
  public static <ELEMENTTYPE> int getCount (@Nullable final ELEMENTTYPE [] aArray,
                                            @Nullable final Predicate <? super ELEMENTTYPE> aFilter)
  {
    if (aFilter == null)
      return getSize (aArray);

    int ret = 0;
    if (isNotEmpty (aArray))
      for (final ELEMENTTYPE aElement : aArray)
        if (aFilter.test (aElement))
          ret++;
    return ret;
  }

  public static <ELEMENTTYPE> boolean containsAny (@Nullable final ELEMENTTYPE [] aArray,
                                                   @Nullable final Predicate <? super ELEMENTTYPE> aFilter)
  {
    if (aFilter == null)
      return isNotEmpty (aArray);

    if (isNotEmpty (aArray))
      for (final ELEMENTTYPE aElement : aArray)
        if (aFilter.test (aElement))
          return true;
    return false;
  }
}
