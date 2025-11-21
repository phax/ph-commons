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
package com.helger.base.array;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.ObjIntConsumer;
import java.util.function.Predicate;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.PresentForCodeCoverage;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.annotation.style.ReturnsMutableObject;
import com.helger.annotation.style.UsedInGeneratedCode;
import com.helger.base.CGlobal;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.equals.EqualsHelper;
import com.helger.base.reflection.GenericReflection;

/**
 * Provides additional helper methods for array handling.
 *
 * @author Philip Helger
 */
@Immutable
public final class ArrayHelper
{
  @PresentForCodeCoverage
  private static final ArrayHelper INSTANCE = new ArrayHelper ();

  private ArrayHelper ()
  {}

  /**
   * Get the component type of the array (the type of which the array is made up)
   *
   * @param <ELEMENTTYPE>
   *        The component type of the array
   * @param aArray
   *        The array to get the type from. May not be <code>null</code>.
   * @return The class that determines a single element of the array.
   */
  @NonNull
  public static <ELEMENTTYPE> Class <? extends ELEMENTTYPE> getComponentType (@NonNull final ELEMENTTYPE [] aArray)
  {
    ValueEnforcer.notNull (aArray, "Array");
    final Class <?> aComponentType = aArray.getClass ().getComponentType ();
    return GenericReflection.uncheckedCast (aComponentType);
  }

  /**
   * @param aArray
   *        The array who's size is to be queried. May be <code>null</code>.
   * @return 0 if the passed array is <code>null</code> - it's length otherwise.
   */
  @Nonnegative
  public static int getSize (final boolean... aArray)
  {
    return aArray == null ? 0 : aArray.length;
  }

  /**
   * @param aArray
   *        The array who's size is to be queried. May be <code>null</code>.
   * @return 0 if the passed array is <code>null</code> - it's length otherwise.
   */
  @Nonnegative
  public static int getSize (final byte... aArray)
  {
    return aArray == null ? 0 : aArray.length;
  }

  /**
   * @param aArray
   *        The array who's size is to be queried. May be <code>null</code>.
   * @return 0 if the passed array is <code>null</code> - it's length otherwise.
   */
  @Nonnegative
  public static int getSize (final char... aArray)
  {
    return aArray == null ? 0 : aArray.length;
  }

  /**
   * @param aArray
   *        The array who's size is to be queried. May be <code>null</code>.
   * @return 0 if the passed array is <code>null</code> - it's length otherwise.
   */
  @Nonnegative
  public static int getSize (final double... aArray)
  {
    return aArray == null ? 0 : aArray.length;
  }

  /**
   * @param aArray
   *        The array who's size is to be queried. May be <code>null</code>.
   * @return 0 if the passed array is <code>null</code> - it's length otherwise.
   */
  @Nonnegative
  public static int getSize (final float... aArray)
  {
    return aArray == null ? 0 : aArray.length;
  }

  /**
   * @param aArray
   *        The array who's size is to be queried. May be <code>null</code>.
   * @return 0 if the passed array is <code>null</code> - it's length otherwise.
   */
  @Nonnegative
  public static int getSize (final int... aArray)
  {
    return aArray == null ? 0 : aArray.length;
  }

  /**
   * @param aArray
   *        The array who's size is to be queried. May be <code>null</code>.
   * @return 0 if the passed array is <code>null</code> - it's length otherwise.
   */
  @Nonnegative
  public static int getSize (final long... aArray)
  {
    return aArray == null ? 0 : aArray.length;
  }

  /**
   * @param aArray
   *        The array who's size is to be queried. May be <code>null</code>.
   * @return 0 if the passed array is <code>null</code> - it's length otherwise.
   */
  @Nonnegative
  public static int getSize (final short... aArray)
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
   * @return <code>true</code> if the passed array is <code>null</code> or empty.
   */
  public static boolean isEmpty (final boolean... aArray)
  {
    return getSize (aArray) == 0;
  }

  /**
   * @param aArray
   *        The array to be queried if it is empty. May be <code>null</code>.
   * @return <code>true</code> if the passed array is <code>null</code> or empty.
   */
  public static boolean isEmpty (final byte... aArray)
  {
    return getSize (aArray) == 0;
  }

  /**
   * @param aArray
   *        The array to be queried if it is empty. May be <code>null</code>.
   * @return <code>true</code> if the passed array is <code>null</code> or empty.
   */
  public static boolean isEmpty (final char... aArray)
  {
    return getSize (aArray) == 0;
  }

  /**
   * @param aArray
   *        The array to be queried if it is empty. May be <code>null</code>.
   * @return <code>true</code> if the passed array is <code>null</code> or empty.
   */
  public static boolean isEmpty (final double... aArray)
  {
    return getSize (aArray) == 0;
  }

  /**
   * @param aArray
   *        The array to be queried if it is empty. May be <code>null</code>.
   * @return <code>true</code> if the passed array is <code>null</code> or empty.
   */
  public static boolean isEmpty (final float... aArray)
  {
    return getSize (aArray) == 0;
  }

  /**
   * @param aArray
   *        The array to be queried if it is empty. May be <code>null</code>.
   * @return <code>true</code> if the passed array is <code>null</code> or empty.
   */
  public static boolean isEmpty (final int... aArray)
  {
    return getSize (aArray) == 0;
  }

  /**
   * @param aArray
   *        The array to be queried if it is empty. May be <code>null</code>.
   * @return <code>true</code> if the passed array is <code>null</code> or empty.
   */
  public static boolean isEmpty (final long... aArray)
  {
    return getSize (aArray) == 0;
  }

  /**
   * @param aArray
   *        The array to be queried if it is empty. May be <code>null</code>.
   * @return <code>true</code> if the passed array is <code>null</code> or empty.
   */
  public static boolean isEmpty (final short... aArray)
  {
    return getSize (aArray) == 0;
  }

  /**
   * @param <ELEMENTTYPE>
   *        Array element type
   * @param aArray
   *        The array to be queried if it is empty. May be <code>null</code>.
   * @return <code>true</code> if the passed array is <code>null</code> or empty.
   */
  @SafeVarargs
  public static <ELEMENTTYPE> boolean isEmpty (@Nullable final ELEMENTTYPE... aArray)
  {
    return getSize (aArray) == 0;
  }

  /**
   * @param aArray
   *        The array to be queried if it is empty. May be <code>null</code>.
   * @return <code>false</code> if the passed array is <code>null</code> or empty.
   */
  public static boolean isNotEmpty (final boolean... aArray)
  {
    return getSize (aArray) > 0;
  }

  /**
   * @param aArray
   *        The array to be queried if it is empty. May be <code>null</code>.
   * @return <code>false</code> if the passed array is <code>null</code> or empty.
   */
  public static boolean isNotEmpty (final byte... aArray)
  {
    return getSize (aArray) > 0;
  }

  /**
   * @param aArray
   *        The array to be queried if it is empty. May be <code>null</code>.
   * @return <code>false</code> if the passed array is <code>null</code> or empty.
   */
  public static boolean isNotEmpty (final char... aArray)
  {
    return getSize (aArray) > 0;
  }

  /**
   * @param aArray
   *        The array to be queried if it is empty. May be <code>null</code>.
   * @return <code>false</code> if the passed array is <code>null</code> or empty.
   */
  public static boolean isNotEmpty (final double... aArray)
  {
    return getSize (aArray) > 0;
  }

  /**
   * @param aArray
   *        The array to be queried if it is empty. May be <code>null</code>.
   * @return <code>false</code> if the passed array is <code>null</code> or empty.
   */
  public static boolean isNotEmpty (final float... aArray)
  {
    return getSize (aArray) > 0;
  }

  /**
   * @param aArray
   *        The array to be queried if it is empty. May be <code>null</code>.
   * @return <code>false</code> if the passed array is <code>null</code> or empty.
   */
  public static boolean isNotEmpty (final int... aArray)
  {
    return getSize (aArray) > 0;
  }

  /**
   * @param aArray
   *        The array to be queried if it is empty. May be <code>null</code>.
   * @return <code>false</code> if the passed array is <code>null</code> or empty.
   */
  public static boolean isNotEmpty (final long... aArray)
  {
    return getSize (aArray) > 0;
  }

  /**
   * @param aArray
   *        The array to be queried if it is empty. May be <code>null</code>.
   * @return <code>false</code> if the passed array is <code>null</code> or empty.
   */
  public static boolean isNotEmpty (final short... aArray)
  {
    return getSize (aArray) > 0;
  }

  /**
   * @param <ELEMENTTYPE>
   *        Array element type
   * @param aArray
   *        The array to be queried if it is empty. May be <code>null</code>.
   * @return <code>false</code> if the passed array is <code>null</code> or empty.
   */
  @SafeVarargs
  public static <ELEMENTTYPE> boolean isNotEmpty (@Nullable final ELEMENTTYPE... aArray)
  {
    return getSize (aArray) > 0;
  }

  /**
   * Helper method to easily create an array from constant values.
   *
   * @param aArray
   *        The list of values for the array. May be <code>null</code>.
   * @return The passed array. <code>null</code> if the passed array was <code>null</code>.
   */
  @ReturnsMutableObject ("use getCopy otherwise")
  public static boolean @Nullable [] createBooleanArray (final boolean... aArray)
  {
    return aArray;
  }

  /**
   * Helper method to easily create an array from constant values.
   *
   * @param aArray
   *        The list of values for the array. May be <code>null</code>.
   * @return The passed array. <code>null</code> if the passed array was <code>null</code>.
   */
  @ReturnsMutableObject ("use getCopy otherwise")
  public static byte @Nullable [] createByteArray (final byte... aArray)
  {
    return aArray;
  }

  /**
   * Helper method to easily create an array from constant values.
   *
   * @param aArray
   *        The list of values for the array. May be <code>null</code>.
   * @return The passed array. <code>null</code> if the passed array was <code>null</code>.
   */
  @ReturnsMutableObject ("use getCopy otherwise")
  public static char @Nullable [] createCharArray (final char... aArray)
  {
    return aArray;
  }

  /**
   * Helper method to easily create an array from constant values.
   *
   * @param aArray
   *        The list of values for the array. May be <code>null</code>.
   * @return The passed array. <code>null</code> if the passed array was <code>null</code>.
   */
  @ReturnsMutableObject ("use getCopy otherwise")
  public static double @Nullable [] createDoubleArray (final double... aArray)
  {
    return aArray;
  }

  /**
   * Helper method to easily create an array from constant values.
   *
   * @param aArray
   *        The list of values for the array. May be <code>null</code>.
   * @return The passed array. <code>null</code> if the passed array was <code>null</code>.
   */
  @ReturnsMutableObject ("use getCopy otherwise")
  public static float @Nullable [] createFloatArray (final float... aArray)
  {
    return aArray;
  }

  /**
   * Helper method to easily create an array from constant values.
   *
   * @param aArray
   *        The list of values for the array. May be <code>null</code>.
   * @return The passed array. <code>null</code> if the passed array was <code>null</code>.
   */
  @ReturnsMutableObject ("use getCopy otherwise")
  public static int @Nullable [] createIntArray (final int... aArray)
  {
    return aArray;
  }

  /**
   * Helper method to easily create an array from constant values.
   *
   * @param aArray
   *        The list of values for the array. May be <code>null</code>.
   * @return The passed array. <code>null</code> if the passed array was <code>null</code>.
   */
  @ReturnsMutableObject ("use getCopy otherwise")
  public static long @Nullable [] createLongArray (final long... aArray)
  {
    return aArray;
  }

  /**
   * Helper method to easily create an array from constant values.
   *
   * @param aArray
   *        The list of values for the array. May be <code>null</code>.
   * @return The passed array. <code>null</code> if the passed array was <code>null</code>.
   */
  @ReturnsMutableObject ("use getCopy otherwise")
  public static short @Nullable [] createShortArray (final short... aArray)
  {
    return aArray;
  }

  @NonNull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> ELEMENTTYPE [] createArray (@NonNull final Class <? extends ELEMENTTYPE> aClass,
                                                          @Nonnegative final int nSize)
  {
    ValueEnforcer.notNull (aClass, "class");
    if (aClass.isPrimitive ())
      throw new IllegalArgumentException ("Argument cannot be primitive: " + aClass);
    ValueEnforcer.isGE0 (nSize, "Size");

    final Object aArray = Array.newInstance (aClass, nSize);
    return GenericReflection.uncheckedCast (aArray);
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
  @NonNull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> ELEMENTTYPE [] createArraySameType (@NonNull final ELEMENTTYPE [] aArray,
                                                                  @Nonnegative final int nSize)
  {
    return createArray (getComponentType (aArray), nSize);
  }

  /**
   * Create a new array with the elements in the passed collection..
   *
   * @param <ELEMENTTYPE>
   *        Type of element
   * @param aCollection
   *        The collection to be converted to an array. May be <code>null</code> .
   * @param aClass
   *        The class of the elements inside the collection. May not be <code>null</code>.
   * @return <code>null</code> if the passed collection is empty, a non- <code>null</code> array
   *         with all elements of the collection otherwise.
   */
  @NonNull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> ELEMENTTYPE [] createArray (@Nullable final Collection <? extends ELEMENTTYPE> aCollection,
                                                          @NonNull final Class <ELEMENTTYPE> aClass)
  {
    ValueEnforcer.notNull (aClass, "class");

    if (aCollection == null || aCollection.isEmpty ())
      return createArray (aClass, 0);

    final ELEMENTTYPE [] ret = createArray (aClass, aCollection.size ());
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
   *        The class of the element. May not be <code>null</code>. Must be present because in case
   *        the passed element is <code>null</code> there is no way to determine the array component
   *        type!
   * @return The created array and never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> ELEMENTTYPE [] createArraySingleElement (@Nullable final ELEMENTTYPE aElement,
                                                                       @NonNull final Class <ELEMENTTYPE> aClass)
  {
    ValueEnforcer.notNull (aClass, "class");

    final ELEMENTTYPE [] ret = createArray (aClass, 1);
    ret[0] = aElement;
    return ret;
  }

  /**
   * Wrapper that allows vararg arguments and returns the array. <br>
   * Note: this implementation is not available for basic types, because the Eclipse compiler seems
   * to have difficulties resolving vararg types correctly.
   *
   * @param <ELEMENTTYPE>
   *        Type of element
   * @param aArray
   *        The vararg array
   * @return The wrapped array
   */
  @NonNull
  @ReturnsMutableObject ("use getCopy otherwise")
  @SafeVarargs
  public static <ELEMENTTYPE> ELEMENTTYPE [] createArray (@NonNull final ELEMENTTYPE... aArray)
  {
    ValueEnforcer.notNull (aArray, "Array");
    return aArray;
  }

  /**
   * Create a new array with a predefined number of elements containing the passed value.
   *
   * @param <ELEMENTTYPE>
   *        The type of the array to be created.
   * @param nArraySize
   *        The size of the array to be created.
   * @param aValue
   *        The value to be set into each array element. May be <code>null</code>.
   * @param aClass
   *        The value class. May not be <code>null</code>. Must be present in case the passed value
   *        is <code>null</code>.
   * @return The created array filled with the given value.
   */
  @NonNull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> ELEMENTTYPE [] createArray (@Nonnegative final int nArraySize,
                                                          @NonNull final ELEMENTTYPE aValue,
                                                          @NonNull final Class <ELEMENTTYPE> aClass)
  {
    ValueEnforcer.isGE0 (nArraySize, "ArraySize");
    ValueEnforcer.notNull (aClass, "class");

    final ELEMENTTYPE [] ret = createArray (aClass, nArraySize);
    Arrays.fill (ret, aValue);
    return ret;
  }

  @NonNull
  @ReturnsMutableCopy
  public static <SRCTYPE, DSTTYPE> DSTTYPE [] createArrayMapped (@NonNull final Collection <? extends SRCTYPE> aCollection,
                                                                 @NonNull final Function <? super SRCTYPE, ? extends DSTTYPE> aMapper,
                                                                 @NonNull final Class <DSTTYPE> aDstClass)
  {
    ValueEnforcer.notNull (aCollection, "Collection");
    ValueEnforcer.notNull (aMapper, "Converter");
    ValueEnforcer.notNull (aDstClass, "DestClass");

    final DSTTYPE [] ret = createArray (aDstClass, aCollection.size ());
    int i = 0;
    for (final SRCTYPE aObj : aCollection)
      ret[i++] = aMapper.apply (aObj);
    return ret;
  }

  @NonNull
  @ReturnsMutableCopy
  public static <SRCTYPE, DSTTYPE> DSTTYPE [] createArrayMapped (@Nullable final SRCTYPE [] aArray,
                                                                 @NonNull final Function <? super SRCTYPE, ? extends DSTTYPE> aMapper,
                                                                 @NonNull final Class <DSTTYPE> aDstClass)
  {
    ValueEnforcer.notNull (aMapper, "Converter");
    ValueEnforcer.notNull (aDstClass, "DestClass");

    final DSTTYPE [] ret = createArray (aDstClass, getSize (aArray));
    if (aArray != null)
    {
      int i = 0;
      for (final SRCTYPE aObj : aArray)
        ret[i++] = aMapper.apply (aObj);
    }
    return ret;
  }

  /**
   * Get a 1:1 copy of the passed array. Nested elements are not deep-copied - the references are
   * re-used!
   *
   * @param aArray
   *        The array to be copied.
   * @return <code>null</code> if the passed array is <code>null</code> - a non- <code>null</code>
   *         copy otherwise.
   */
  @ReturnsMutableCopy
  public static boolean @Nullable [] getCopy (final boolean... aArray)
  {
    return aArray == null ? null : getCopy (aArray, 0, aArray.length);
  }

  /**
   * Get a 1:1 copy of the passed array using the passed number of array elements. Nested elements
   * are not deep-copied - the references are re-used!
   *
   * @param aArray
   *        The array to be copied.
   * @param nLength
   *        The number of elements to be copied into the new array. May not be &lt; 0. If the passed
   *        number of elements exceeds the number of elements in the array, only the available
   *        number of elements in the source array are copied.
   * @return <code>null</code> if the passed array is <code>null</code> - a non- <code>null</code>
   *         copy otherwise.
   */
  @ReturnsMutableCopy
  public static boolean @Nullable [] getCopy (final boolean @Nullable [] aArray, @Nonnegative final int nLength)
  {
    return aArray == null ? null : getCopy (aArray, 0, Math.min (aArray.length, nLength));
  }

  /**
   * Get a 1:1 copy of the passed array using the passed number of array elements starting at the
   * specified index. Nested elements are not deep-copied - the references are re-used!
   *
   * @param aArray
   *        The array to be copied.
   * @param nStartIndex
   *        The index where the copying should start. Must be &gt;= 0!
   * @param nLength
   *        The number of elements to be copied into the new array. May not be &lt; 0. If the passed
   *        number of elements exceeds the number of elements in the array, an exception is thrown.
   * @return <code>null</code> if the passed array is <code>null</code> - a non- <code>null</code>
   *         copy otherwise.
   * @see System#arraycopy(Object, int, Object, int, int)
   */
  @ReturnsMutableCopy
  public static boolean @Nullable [] getCopy (final boolean @Nullable [] aArray,
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
   * Get a 1:1 copy of the passed array. Nested elements are not deep-copied - the references are
   * re-used!
   *
   * @param aArray
   *        The array to be copied.
   * @return <code>null</code> if the passed array is <code>null</code> - a non- <code>null</code>
   *         copy otherwise.
   */
  @ReturnsMutableCopy
  public static byte @Nullable [] getCopy (final byte... aArray)
  {
    return aArray == null ? null : getCopy (aArray, 0, aArray.length);
  }

  /**
   * Get a 1:1 copy of the passed array using the passed number of array elements. Nested elements
   * are not deep-copied - the references are re-used!
   *
   * @param aArray
   *        The array to be copied.
   * @param nLength
   *        The number of elements to be copied into the new array. May not be &lt; 0. If the passed
   *        number of elements exceeds the number of elements in the array, only the available
   *        number of elements in the source array are copied.
   * @return <code>null</code> if the passed array is <code>null</code> - a non- <code>null</code>
   *         copy otherwise.
   */
  @ReturnsMutableCopy
  public static byte @Nullable [] getCopy (final byte @Nullable [] aArray, @Nonnegative final int nLength)
  {
    return aArray == null ? null : getCopy (aArray, 0, Math.min (aArray.length, nLength));
  }

  /**
   * Get a 1:1 copy of the passed array using the passed number of array elements starting at the
   * specified index. Nested elements are not deep-copied - the references are re-used!
   *
   * @param aArray
   *        The array to be copied.
   * @param nStartIndex
   *        The index where the copying should start. Must be &gt;= 0!
   * @param nLength
   *        The number of elements to be copied into the new array. May not be &lt; 0. If the passed
   *        number of elements exceeds the number of elements in the array, an exception is thrown.
   * @return <code>null</code> if the passed array is <code>null</code> - a non- <code>null</code>
   *         copy otherwise.
   * @see System#arraycopy(Object, int, Object, int, int)
   */
  @ReturnsMutableCopy
  public static byte @Nullable [] getCopy (final byte @Nullable [] aArray,
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
   * Get a 1:1 copy of the passed array. Nested elements are not deep-copied - the references are
   * re-used!
   *
   * @param aArray
   *        The array to be copied.
   * @return <code>null</code> if the passed array is <code>null</code> - a non- <code>null</code>
   *         copy otherwise.
   */
  @ReturnsMutableCopy
  public static char @Nullable [] getCopy (final char... aArray)
  {
    return aArray == null ? null : getCopy (aArray, 0, aArray.length);
  }

  /**
   * Get a 1:1 copy of the passed array using the passed number of array elements. Nested elements
   * are not deep-copied - the references are re-used!
   *
   * @param aArray
   *        The array to be copied.
   * @param nLength
   *        The number of elements to be copied into the new array. May not be &lt; 0. If the passed
   *        number of elements exceeds the number of elements in the array, only the available
   *        number of elements in the source array are copied.
   * @return <code>null</code> if the passed array is <code>null</code> - a non- <code>null</code>
   *         copy otherwise.
   */
  @ReturnsMutableCopy
  public static char @Nullable [] getCopy (final char @Nullable [] aArray, @Nonnegative final int nLength)
  {
    return aArray == null ? null : getCopy (aArray, 0, Math.min (aArray.length, nLength));
  }

  /**
   * Get a 1:1 copy of the passed array using the passed number of array elements starting at the
   * specified index. Nested elements are not deep-copied - the references are re-used!
   *
   * @param aArray
   *        The array to be copied.
   * @param nStartIndex
   *        The index where the copying should start. Must be &gt;= 0!
   * @param nLength
   *        The number of elements to be copied into the new array. May not be &lt; 0. If the passed
   *        number of elements exceeds the number of elements in the array, an exception is thrown.
   * @return <code>null</code> if the passed array is <code>null</code> - a non- <code>null</code>
   *         copy otherwise.
   * @see System#arraycopy(Object, int, Object, int, int)
   */
  @ReturnsMutableCopy
  public static char @Nullable [] getCopy (final char @Nullable [] aArray,
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
   * Get a 1:1 copy of the passed array. Nested elements are not deep-copied - the references are
   * re-used!
   *
   * @param aArray
   *        The array to be copied.
   * @return <code>null</code> if the passed array is <code>null</code> - a non- <code>null</code>
   *         copy otherwise.
   */
  @ReturnsMutableCopy
  public static double @Nullable [] getCopy (final double... aArray)
  {
    return aArray == null ? null : getCopy (aArray, 0, aArray.length);
  }

  /**
   * Get a 1:1 copy of the passed array using the passed number of array elements. Nested elements
   * are not deep-copied - the references are re-used!
   *
   * @param aArray
   *        The array to be copied.
   * @param nLength
   *        The number of elements to be copied into the new array. May not be &lt; 0. If the passed
   *        number of elements exceeds the number of elements in the array, only the available
   *        number of elements in the source array are copied.
   * @return <code>null</code> if the passed array is <code>null</code> - a non- <code>null</code>
   *         copy otherwise.
   */
  @ReturnsMutableCopy
  public static double @Nullable [] getCopy (final double @Nullable [] aArray, @Nonnegative final int nLength)
  {
    return aArray == null ? null : getCopy (aArray, 0, Math.min (aArray.length, nLength));
  }

  /**
   * Get a 1:1 copy of the passed array using the passed number of array elements starting at the
   * specified index. Nested elements are not deep-copied - the references are re-used!
   *
   * @param aArray
   *        The array to be copied.
   * @param nStartIndex
   *        The index where the copying should start. Must be &gt;= 0!
   * @param nLength
   *        The number of elements to be copied into the new array. May not be &lt; 0. If the passed
   *        number of elements exceeds the number of elements in the array, an exception is thrown.
   * @return <code>null</code> if the passed array is <code>null</code> - a non- <code>null</code>
   *         copy otherwise.
   * @see System#arraycopy(Object, int, Object, int, int)
   */
  @ReturnsMutableCopy
  public static double @Nullable [] getCopy (final double @Nullable [] aArray,
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
   * Get a 1:1 copy of the passed array. Nested elements are not deep-copied - the references are
   * re-used!
   *
   * @param aArray
   *        The array to be copied.
   * @return <code>null</code> if the passed array is <code>null</code> - a non- <code>null</code>
   *         copy otherwise.
   */
  @ReturnsMutableCopy
  public static float @Nullable [] getCopy (final float... aArray)
  {
    return aArray == null ? null : getCopy (aArray, 0, aArray.length);
  }

  /**
   * Get a 1:1 copy of the passed array using the passed number of array elements. Nested elements
   * are not deep-copied - the references are re-used!
   *
   * @param aArray
   *        The array to be copied.
   * @param nLength
   *        The number of elements to be copied into the new array. May not be &lt; 0. If the passed
   *        number of elements exceeds the number of elements in the array, only the available
   *        number of elements in the source array are copied.
   * @return <code>null</code> if the passed array is <code>null</code> - a non- <code>null</code>
   *         copy otherwise.
   */
  @ReturnsMutableCopy
  public static float @Nullable [] getCopy (final float @Nullable [] aArray, @Nonnegative final int nLength)
  {
    return aArray == null ? null : getCopy (aArray, 0, Math.min (aArray.length, nLength));
  }

  /**
   * Get a 1:1 copy of the passed array using the passed number of array elements starting at the
   * specified index. Nested elements are not deep-copied - the references are re-used!
   *
   * @param aArray
   *        The array to be copied.
   * @param nStartIndex
   *        The index where the copying should start. Must be &gt;= 0!
   * @param nLength
   *        The number of elements to be copied into the new array. May not be &lt; 0. If the passed
   *        number of elements exceeds the number of elements in the array, an exception is thrown.
   * @return <code>null</code> if the passed array is <code>null</code> - a non- <code>null</code>
   *         copy otherwise.
   * @see System#arraycopy(Object, int, Object, int, int)
   */
  @ReturnsMutableCopy
  public static float @Nullable [] getCopy (final float @Nullable [] aArray,
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
   * Get a 1:1 copy of the passed array. Nested elements are not deep-copied - the references are
   * re-used!
   *
   * @param aArray
   *        The array to be copied.
   * @return <code>null</code> if the passed array is <code>null</code> - a non- <code>null</code>
   *         copy otherwise.
   */

  @ReturnsMutableCopy
  public static int @Nullable [] getCopy (final int... aArray)
  {
    return aArray == null ? null : getCopy (aArray, 0, aArray.length);
  }

  /**
   * Get a 1:1 copy of the passed array using the passed number of array elements. Nested elements
   * are not deep-copied - the references are re-used!
   *
   * @param aArray
   *        The array to be copied.
   * @param nLength
   *        The number of elements to be copied into the new array. May not be &lt; 0. If the passed
   *        number of elements exceeds the number of elements in the array, only the available
   *        number of elements in the source array are copied.
   * @return <code>null</code> if the passed array is <code>null</code> - a non- <code>null</code>
   *         copy otherwise.
   */
  @ReturnsMutableCopy
  public static int @Nullable [] getCopy (final int @Nullable [] aArray, @Nonnegative final int nLength)
  {
    return aArray == null ? null : getCopy (aArray, 0, Math.min (aArray.length, nLength));
  }

  /**
   * Get a 1:1 copy of the passed array using the passed number of array elements starting at the
   * specified index. Nested elements are not deep-copied - the references are re-used!
   *
   * @param aArray
   *        The array to be copied.
   * @param nStartIndex
   *        The index where the copying should start. Must be &gt;= 0!
   * @param nLength
   *        The number of elements to be copied into the new array. May not be &lt; 0. If the passed
   *        number of elements exceeds the number of elements in the array, an exception is thrown.
   * @return <code>null</code> if the passed array is <code>null</code> - a non- <code>null</code>
   *         copy otherwise.
   * @see System#arraycopy(Object, int, Object, int, int)
   */
  /**
   * Get a 1:1 copy of the passed array. Nested elements are not deep-copied - the references are
   * re-used!
   *
   * @param aArray
   *        The array to be copied.
   * @param nStartIndex
   *        The index where the copying should start. Must be &gt;= 0!
   * @param nLength
   *        The number of elements to be copied into the new array. May not be &lt; 0. If the passed
   *        number of elements exceeds the number of elements in the array, an exception is thrown.
   * @return <code>null</code> if the passed array is <code>null</code> - a non- <code>null</code>
   *         copy otherwise.
   */
  @ReturnsMutableCopy
  public static int @Nullable [] getCopy (final int @Nullable [] aArray,
                                @Nonnegative final int nStartIndex,
                                @Nonnegative final int nLength)
  {
    if (aArray == null)
      return null;
    final int [] ret = new int [nLength];
    System.arraycopy (aArray, nStartIndex, ret, 0, nLength);
    return ret;
  }

  @ReturnsMutableCopy
  public static long @Nullable [] getCopy (final long... aArray)
  {
    return aArray == null ? null : getCopy (aArray, 0, aArray.length);
  }

  /**
   * Get a 1:1 copy of the passed array using the passed number of array elements. Nested elements
   * are not deep-copied - the references are re-used!
   *
   * @param aArray
   *        The array to be copied.
   * @param nLength
   *        The number of elements to be copied into the new array. May not be &lt; 0. If the passed
   *        number of elements exceeds the number of elements in the array, only the available
   *        number of elements in the source array are copied.
   * @return <code>null</code> if the passed array is <code>null</code> - a non- <code>null</code>
   *         copy otherwise.
   */
  @ReturnsMutableCopy
  public static long @Nullable [] getCopy (final long @Nullable [] aArray, @Nonnegative final int nLength)
  {
    return aArray == null ? null : getCopy (aArray, 0, Math.min (aArray.length, nLength));
  }

  /**
   * Get a 1:1 copy of the passed array using the passed number of array elements starting at the
   * specified index. Nested elements are not deep-copied - the references are re-used!
   *
   * @param aArray
   *        The array to be copied.
   * @param nStartIndex
   *        The index where the copying should start. Must be &gt;= 0!
   * @param nLength
   *        The number of elements to be copied into the new array. May not be &lt; 0. If the passed
   *        number of elements exceeds the number of elements in the array, an exception is thrown.
   * @return <code>null</code> if the passed array is <code>null</code> - a non- <code>null</code>
   *         copy otherwise.
   * @see System#arraycopy(Object, int, Object, int, int)
   */
  @ReturnsMutableCopy
  public static long @Nullable [] getCopy (final long @Nullable [] aArray,
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
   * Get a 1:1 copy of the passed array. Nested elements are not deep-copied - the references are
   * re-used!
   *
   * @param aArray
   *        The array to be copied.
   * @return <code>null</code> if the passed array is <code>null</code> - a non- <code>null</code>
   *         copy otherwise.
   */
  @ReturnsMutableCopy
  public static short @Nullable [] getCopy (final short... aArray)
  {
    return aArray == null ? null : getCopy (aArray, 0, aArray.length);
  }

  /**
   * Get a 1:1 copy of the passed array using the passed number of array elements. Nested elements
   * are not deep-copied - the references are re-used!
   *
   * @param aArray
   *        The array to be copied.
   * @param nLength
   *        The number of elements to be copied into the new array. May not be &lt; 0. If the passed
   *        number of elements exceeds the number of elements in the array, only the available
   *        number of elements in the source array are copied.
   * @return <code>null</code> if the passed array is <code>null</code> - a non- <code>null</code>
   *         copy otherwise.
   */
  @ReturnsMutableCopy
  public static short @Nullable [] getCopy (final short @Nullable [] aArray, @Nonnegative final int nLength)
  {
    return aArray == null ? null : getCopy (aArray, 0, Math.min (aArray.length, nLength));
  }

  /**
   * Get a 1:1 copy of the passed array using the passed number of array elements starting at the
   * specified index. Nested elements are not deep-copied - the references are re-used!
   *
   * @param aArray
   *        The array to be copied.
   * @param nStartIndex
   *        The index where the copying should start. Must be &gt;= 0!
   * @param nLength
   *        The number of elements to be copied into the new array. May not be &lt; 0. If the passed
   *        number of elements exceeds the number of elements in the array, an exception is thrown.
   * @return <code>null</code> if the passed array is <code>null</code> - a non- <code>null</code>
   *         copy otherwise.
   * @see System#arraycopy(Object, int, Object, int, int)
   */
  @ReturnsMutableCopy
  public static short @Nullable [] getCopy (final short @Nullable [] aArray,
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
   * Get a 1:1 copy of the passed array. Nested elements are not deep-copied - the references are
   * re-used!
   *
   * @param <ELEMENTTYPE>
   *        Array element type
   * @param aArray
   *        The array to be copied.
   * @return <code>null</code> if the passed array is <code>null</code> - a non- <code>null</code>
   *         copy otherwise.
   */
  @Nullable
  @ReturnsMutableCopy
  @SafeVarargs
  public static <ELEMENTTYPE> ELEMENTTYPE [] getCopy (@Nullable final ELEMENTTYPE... aArray)
  {
    return aArray == null ? null : getCopy (aArray, 0, aArray.length);
  }

  /**
   * Get a 1:1 copy of the passed array using the passed number of array elements. Nested elements
   * are not deep-copied - the references are re-used!
   *
   * @param <ELEMENTTYPE>
   *        Array element type
   * @param aArray
   *        The array to be copied.
   * @param nLength
   *        The number of elements to be copied into the new array. May not be &lt; 0. If the passed
   *        number of elements exceeds the number of elements in the array, only the available
   *        number of elements in the source array are copied.
   * @return <code>null</code> if the passed array is <code>null</code> - a non- <code>null</code>
   *         copy otherwise.
   */
  @Nullable
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> ELEMENTTYPE [] getCopy (@Nullable final ELEMENTTYPE [] aArray,
                                                      @Nonnegative final int nLength)
  {
    return aArray == null ? null : getCopy (aArray, 0, Math.min (aArray.length, nLength));
  }

  /**
   * Get a 1:1 copy of the passed array using the passed number of array elements starting at the
   * specified index. Nested elements are not deep-copied - the references are re-used!
   *
   * @param <ELEMENTTYPE>
   *        Array element type
   * @param aArray
   *        The array to be copied.
   * @param nStartIndex
   *        The index where the copying should start. Must be &gt;= 0!
   * @param nLength
   *        The number of elements to be copied into the new array. May not be &lt; 0. If the passed
   *        number of elements exceeds the number of elements in the array, an exception is thrown.
   * @return <code>null</code> if the passed array is <code>null</code> - a non- <code>null</code>
   *         copy otherwise.
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
    final ELEMENTTYPE [] ret = createArraySameType (aArray, nLength);
    System.arraycopy (aArray, nStartIndex, ret, 0, nLength);
    return ret;
  }

  public static boolean startsWith (final byte @NonNull [] aArray, final byte @Nullable [] aSearch)
  {
    if (aSearch == null)
      return false;
    return startsWith (aArray, 0, aArray.length, aSearch, 0, aSearch.length);
  }

  public static boolean startsWith (final byte @NonNull [] aArray,
                                    @Nonnegative final int nArrayLen,
                                    final byte @Nullable [] aSearch)
  {
    if (aSearch == null)
      return false;
    return startsWith (aArray, 0, nArrayLen, aSearch, 0, aSearch.length);
  }

  public static boolean startsWith (final byte @NonNull [] aArray,
                                    final byte @Nullable [] aSearch,
                                    @Nonnegative final int nSearchOfs,
                                    @Nonnegative final int nSearchLen)
  {
    return startsWith (aArray, 0, aArray.length, aSearch, nSearchOfs, nSearchLen);
  }

  public static boolean startsWith (final byte @NonNull [] aArray,
                                    @Nonnegative final int nArrayLen,
                                    final byte @Nullable [] aSearch,
                                    @Nonnegative final int nSearchOfs,
                                    @Nonnegative final int nSearchLen)
  {
    return startsWith (aArray, 0, nArrayLen, aSearch, nSearchOfs, nSearchLen);
  }

  public static boolean startsWith (final byte @NonNull [] aArray,
                                    @Nonnegative final int nArrayOfs,
                                    @Nonnegative final int nArrayLen,
                                    final byte @Nullable [] aSearch,
                                    @Nonnegative final int nSearchOfs,
                                    @Nonnegative final int nSearchLen)
  {
    if (aSearch == null || nArrayLen <= 0 || nArrayLen < nSearchLen)
      return false;

    for (int i = 0; i < nSearchLen; i++)
      if (aArray[nArrayOfs + i] != aSearch[nSearchOfs + i])
        return false;
    return true;
  }

  public static boolean startsWith (final char @NonNull [] aArray, final char @Nullable [] aSearch)
  {
    if (aSearch == null)
      return false;
    return startsWith (aArray, 0, aArray.length, aSearch, 0, aSearch.length);
  }

  public static boolean startsWith (final char @NonNull [] aArray,
                                    @Nonnegative final int nArrayLen,
                                    final char @Nullable [] aSearch)
  {
    if (aSearch == null)
      return false;
    return startsWith (aArray, 0, nArrayLen, aSearch, 0, aSearch.length);
  }

  public static boolean startsWith (final char @NonNull [] aArray,
                                    final char @Nullable [] aSearch,
                                    @Nonnegative final int nSearchOfs,
                                    @Nonnegative final int nSearchLen)
  {
    return startsWith (aArray, 0, aArray.length, aSearch, nSearchOfs, nSearchLen);
  }

  public static boolean startsWith (final char @NonNull [] aArray,
                                    @Nonnegative final int nArrayLen,
                                    final char @Nullable [] aSearch,
                                    @Nonnegative final int nSearchOfs,
                                    @Nonnegative final int nSearchLen)
  {
    return startsWith (aArray, 0, nArrayLen, aSearch, nSearchOfs, nSearchLen);
  }

  public static boolean startsWith (final char @NonNull [] aArray,
                                    @Nonnegative final int nArrayOfs,
                                    @Nonnegative final int nArrayLen,
                                    final char @Nullable [] aSearch,
                                    @Nonnegative final int nSearchOfs,
                                    @Nonnegative final int nSearchLen)
  {
    if (aSearch == null || nArrayLen <= 0 || nArrayLen < nSearchLen)
      return false;

    for (int i = 0; i < nSearchLen; i++)
      if (aArray[nArrayOfs + i] != aSearch[nSearchOfs + i])
        return false;
    return true;
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
   * @return <code>-1</code> if the searched value is not contained, a value &ge; 0 otherwise.
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
   * @return <code>-1</code> if the searched value is not contained, a value &ge; 0 otherwise.
   */
  public static int getFirstIndex (final boolean @Nullable [] aValues, final boolean aSearchValue)
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
   * @return <code>-1</code> if the searched value is not contained, a value &ge; 0 otherwise.
   */
  public static int getFirstIndex (final byte @Nullable [] aValues, final byte aSearchValue)
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
   * @return <code>-1</code> if the searched value is not contained, a value &ge; 0 otherwise.
   */
  public static int getFirstIndex (final char @Nullable [] aValues, final char aSearchValue)
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
   * @return <code>-1</code> if the searched value is not contained, a value &ge; 0 otherwise.
   */
  public static int getFirstIndex (final double @Nullable [] aValues, final double aSearchValue)
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
   * @return <code>-1</code> if the searched value is not contained, a value &ge; 0 otherwise.
   */
  public static int getFirstIndex (final float @Nullable [] aValues, final float aSearchValue)
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
   * @return <code>-1</code> if the searched value is not contained, a value &ge; 0 otherwise.
   */
  public static int getFirstIndex (final int @Nullable [] aValues, final int aSearchValue)
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
   * @return <code>-1</code> if the searched value is not contained, a value &ge; 0 otherwise.
   */
  public static int getFirstIndex (final long @Nullable [] aValues, final long aSearchValue)
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
   * @return <code>-1</code> if the searched value is not contained, a value &ge; 0 otherwise.
   */
  public static int getFirstIndex (final short @Nullable [] aValues, final short aSearchValue)
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
   * @return <code>-1</code> if the searched value is not contained, a value &ge; 0 otherwise.
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
   * @return <code>-1</code> if the searched value is not contained, a value &ge; 0 otherwise.
   */
  public static int getLastIndex (final boolean @Nullable [] aValues, final boolean aSearchValue)
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
   * @return <code>-1</code> if the searched value is not contained, a value &ge; 0 otherwise.
   */
  public static int getLastIndex (final byte @Nullable [] aValues, final byte aSearchValue)
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
   * @return <code>-1</code> if the searched value is not contained, a value &ge; 0 otherwise.
   */
  public static int getLastIndex (final char @Nullable [] aValues, final char aSearchValue)
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
   * @return <code>-1</code> if the searched value is not contained, a value &ge; 0 otherwise.
   */
  public static int getLastIndex (final double @Nullable [] aValues, final double aSearchValue)
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
   * @return <code>-1</code> if the searched value is not contained, a value &ge; 0 otherwise.
   */
  public static int getLastIndex (final float @Nullable [] aValues, final float aSearchValue)
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
   * @return <code>-1</code> if the searched value is not contained, a value &ge; 0 otherwise.
   */
  public static int getLastIndex (final int @Nullable [] aValues, final int aSearchValue)
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
   * @return <code>-1</code> if the searched value is not contained, a value &ge; 0 otherwise.
   */
  public static int getLastIndex (final long @Nullable [] aValues, final long aSearchValue)
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
   * @return <code>-1</code> if the searched value is not contained, a value &ge; 0 otherwise.
   */
  public static int getLastIndex (final short @Nullable [] aValues, final short aSearchValue)
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
   * @return <code>true</code> if the value array is not empty and the search value is contained -
   *         false otherwise.
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
   * @return <code>true</code> if the value array is not empty and the search value is contained -
   *         false otherwise.
   */
  public static boolean contains (final boolean @Nullable [] aValues, final boolean aSearchValue)
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
   * @return <code>true</code> if the value array is not empty and the search value is contained -
   *         false otherwise.
   */
  public static boolean contains (final byte @Nullable [] aValues, final byte aSearchValue)
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
   * @return <code>true</code> if the value array is not empty and the search value is contained -
   *         false otherwise.
   */
  public static boolean contains (final char @Nullable [] aValues, final char aSearchValue)
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
   * @return <code>true</code> if the value array is not empty and the search value is contained -
   *         false otherwise.
   */
  public static boolean contains (final double @Nullable [] aValues, final double aSearchValue)
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
   * @return <code>true</code> if the value array is not empty and the search value is contained -
   *         false otherwise.
   */
  public static boolean contains (final float @Nullable [] aValues, final float aSearchValue)
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
   * @return <code>true</code> if the value array is not empty and the search value is contained -
   *         false otherwise.
   */
  @UsedInGeneratedCode
  public static boolean contains (final int @Nullable [] aValues, final int aSearchValue)
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
   * @return <code>true</code> if the value array is not empty and the search value is contained -
   *         false otherwise.
   */
  public static boolean contains (final long @Nullable [] aValues, final long aSearchValue)
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
   * @return <code>true</code> if the value array is not empty and the search value is contained -
   *         false otherwise.
   */
  public static boolean contains (final short @Nullable [] aValues, final short aSearchValue)
  {
    return getFirstIndex (aValues, aSearchValue) >= 0;
  }

  /**
   * Get the first element of the array or the passed default if the passed array is empty.
   *
   * @param aArray
   *        The array who's first element is to be retrieved. May be <code>null</code> or empty.
   * @param aDefaultValue
   *        The default value to be returned if the array is empty
   * @return the first element if the passed array is not empty, the default value if the passed
   *         array is empty.
   */
  public static boolean getFirst (final boolean @Nullable [] aArray, final boolean aDefaultValue)
  {
    return isEmpty (aArray) ? aDefaultValue : aArray[0];
  }

  /**
   * Get the first element of the array or the passed default if the passed array is empty.
   *
   * @param aArray
   *        The array who's first element is to be retrieved. May be <code>null</code> or empty.
   * @param aDefaultValue
   *        The default value to be returned if the array is empty
   * @return the first element if the passed array is not empty, the default value if the passed
   *         array is empty.
   */
  public static byte getFirst (final byte @Nullable [] aArray, final byte aDefaultValue)
  {
    return isEmpty (aArray) ? aDefaultValue : aArray[0];
  }

  /**
   * Get the first element of the array or the passed default if the passed array is empty.
   *
   * @param aArray
   *        The array who's first element is to be retrieved. May be <code>null</code> or empty.
   * @param aDefaultValue
   *        The default value to be returned if the array is empty
   * @return the first element if the passed array is not empty, the default value if the passed
   *         array is empty.
   */
  public static char getFirst (final char @Nullable [] aArray, final char aDefaultValue)
  {
    return isEmpty (aArray) ? aDefaultValue : aArray[0];
  }

  /**
   * Get the first element of the array or the passed default if the passed array is empty.
   *
   * @param aArray
   *        The array who's first element is to be retrieved. May be <code>null</code> or empty.
   * @param aDefaultValue
   *        The default value to be returned if the array is empty
   * @return the first element if the passed array is not empty, the default value if the passed
   *         array is empty.
   */
  public static double getFirst (final double @Nullable [] aArray, final double aDefaultValue)
  {
    return isEmpty (aArray) ? aDefaultValue : aArray[0];
  }

  /**
   * Get the first element of the array or the passed default if the passed array is empty.
   *
   * @param aArray
   *        The array who's first element is to be retrieved. May be <code>null</code> or empty.
   * @param aDefaultValue
   *        The default value to be returned if the array is empty
   * @return the first element if the passed array is not empty, the default value if the passed
   *         array is empty.
   */
  public static float getFirst (final float @Nullable [] aArray, final float aDefaultValue)
  {
    return isEmpty (aArray) ? aDefaultValue : aArray[0];
  }

  /**
   * Get the first element of the array or the passed default if the passed array is empty.
   *
   * @param aArray
   *        The array who's first element is to be retrieved. May be <code>null</code> or empty.
   * @param aDefaultValue
   *        The default value to be returned if the array is empty
   * @return the first element if the passed array is not empty, the default value if the passed
   *         array is empty.
   */
  public static int getFirst (final int @Nullable [] aArray, final int aDefaultValue)
  {
    return isEmpty (aArray) ? aDefaultValue : aArray[0];
  }

  /**
   * Get the first element of the array or the passed default if the passed array is empty.
   *
   * @param aArray
   *        The array who's first element is to be retrieved. May be <code>null</code> or empty.
   * @param aDefaultValue
   *        The default value to be returned if the array is empty
   * @return the first element if the passed array is not empty, the default value if the passed
   *         array is empty.
   */
  public static long getFirst (final long @Nullable [] aArray, final long aDefaultValue)
  {
    return isEmpty (aArray) ? aDefaultValue : aArray[0];
  }

  /**
   * Get the first element of the array or the passed default if the passed array is empty.
   *
   * @param aArray
   *        The array who's first element is to be retrieved. May be <code>null</code> or empty.
   * @param aDefaultValue
   *        The default value to be returned if the array is empty
   * @return the first element if the passed array is not empty, the default value if the passed
   *         array is empty.
   */
  public static short getFirst (final short @Nullable [] aArray, final short aDefaultValue)
  {
    return isEmpty (aArray) ? aDefaultValue : aArray[0];
  }

  /**
   * Get the first element of the array or <code>null</code> if the passed array is empty.
   *
   * @param <ELEMENTTYPE>
   *        Array element type
   * @param aArray
   *        The array who's first element is to be retrieved. May be <code>null</code> or empty.
   * @return <code>null</code> if the passed array is <code>null</code> or empty - the first element
   *         otherwise (may also be <code>null</code>).
   */
  @Nullable
  @SafeVarargs
  public static <ELEMENTTYPE> ELEMENTTYPE getFirst (@Nullable final ELEMENTTYPE... aArray)
  {
    return getFirst (aArray, null);
  }

  /**
   * Get the first element of the array or the passed default if the passed array is empty.
   *
   * @param <ELEMENTTYPE>
   *        Array element type
   * @param aArray
   *        The array who's first element is to be retrieved. May be <code>null</code> or empty.
   * @param aDefaultValue
   *        The default value to be returned if the array is empty
   * @return the first element if the passed array is not empty, the default value if the passed
   *         array is empty.
   */
  @Nullable
  public static <ELEMENTTYPE> ELEMENTTYPE getFirst (@Nullable final ELEMENTTYPE [] aArray,
                                                    @Nullable final ELEMENTTYPE aDefaultValue)
  {
    return isEmpty (aArray) ? aDefaultValue : aArray[0];
  }

  /**
   * Get the last element of the array or the passed default if the passed array is empty.
   *
   * @param aArray
   *        The array who's last element is to be retrieved. May be <code>null</code> or empty.
   * @param aDefaultValue
   *        The default value to be returned if the array is empty
   * @return the last element if the passed array is not empty, the default value if the passed
   *         array is empty.
   */
  public static boolean getLast (final boolean @Nullable [] aArray, final boolean aDefaultValue)
  {
    final int nSize = getSize (aArray);
    return nSize == 0 ? aDefaultValue : aArray[nSize - 1];
  }

  /**
   * Get the last element of the array or the passed default if the passed array is empty.
   *
   * @param aArray
   *        The array who's last element is to be retrieved. May be <code>null</code> or empty.
   * @param aDefaultValue
   *        The default value to be returned if the array is empty
   * @return the last element if the passed array is not empty, the default value if the passed
   *         array is empty.
   */
  public static byte getLast (final byte @Nullable [] aArray, final byte aDefaultValue)
  {
    final int nSize = getSize (aArray);
    return nSize == 0 ? aDefaultValue : aArray[nSize - 1];
  }

  /**
   * Get the last element of the array or the passed default if the passed array is empty.
   *
   * @param aArray
   *        The array who's last element is to be retrieved. May be <code>null</code> or empty.
   * @param aDefaultValue
   *        The default value to be returned if the array is empty
   * @return the last element if the passed array is not empty, the default value if the passed
   *         array is empty.
   */
  public static char getLast (final char @Nullable [] aArray, final char aDefaultValue)
  {
    final int nSize = getSize (aArray);
    return nSize == 0 ? aDefaultValue : aArray[nSize - 1];
  }

  /**
   * Get the last element of the array or the passed default if the passed array is empty.
   *
   * @param aArray
   *        The array who's last element is to be retrieved. May be <code>null</code> or empty.
   * @param aDefaultValue
   *        The default value to be returned if the array is empty
   * @return the last element if the passed array is not empty, the default value if the passed
   *         array is empty.
   */
  public static double getLast (final double @Nullable [] aArray, final double aDefaultValue)
  {
    final int nSize = getSize (aArray);
    return nSize == 0 ? aDefaultValue : aArray[nSize - 1];
  }

  /**
   * Get the last element of the array or the passed default if the passed array is empty.
   *
   * @param aArray
   *        The array who's last element is to be retrieved. May be <code>null</code> or empty.
   * @param aDefaultValue
   *        The default value to be returned if the array is empty
   * @return the last element if the passed array is not empty, the default value if the passed
   *         array is empty.
   */
  public static float getLast (final float @Nullable [] aArray, final float aDefaultValue)
  {
    final int nSize = getSize (aArray);
    return nSize == 0 ? aDefaultValue : aArray[nSize - 1];
  }

  /**
   * Get the last element of the array or the passed default if the passed array is empty.
   *
   * @param aArray
   *        The array who's last element is to be retrieved. May be <code>null</code> or empty.
   * @param aDefaultValue
   *        The default value to be returned if the array is empty
   * @return the last element if the passed array is not empty, the default value if the passed
   *         array is empty.
   */
  public static int getLast (final int @Nullable [] aArray, final int aDefaultValue)
  {
    final int nSize = getSize (aArray);
    return nSize == 0 ? aDefaultValue : aArray[nSize - 1];
  }

  /**
   * Get the last element of the array or the passed default if the passed array is empty.
   *
   * @param aArray
   *        The array who's last element is to be retrieved. May be <code>null</code> or empty.
   * @param aDefaultValue
   *        The default value to be returned if the array is empty
   * @return the last element if the passed array is not empty, the default value if the passed
   *         array is empty.
   */
  public static long getLast (final long @Nullable [] aArray, final long aDefaultValue)
  {
    final int nSize = getSize (aArray);
    return nSize == 0 ? aDefaultValue : aArray[nSize - 1];
  }

  /**
   * Get the last element of the array or the passed default if the passed array is empty.
   *
   * @param aArray
   *        The array who's last element is to be retrieved. May be <code>null</code> or empty.
   * @param aDefaultValue
   *        The default value to be returned if the array is empty
   * @return the last element if the passed array is not empty, the default value if the passed
   *         array is empty.
   */
  public static short getLast (final short @Nullable [] aArray, final short aDefaultValue)
  {
    final int nSize = getSize (aArray);
    return nSize == 0 ? aDefaultValue : aArray[nSize - 1];
  }

  /**
   * Get the last element of the array or <code>null</code> if the passed array is empty.
   *
   * @param <ELEMENTTYPE>
   *        Array element type
   * @param aArray
   *        The array who's last element is to be retrieved. May be <code>null</code> or empty.
   * @return <code>null</code> if the passed array is <code>null</code> or empty - the last element
   *         otherwise (may also be <code>null</code>).
   */
  @Nullable
  @SafeVarargs
  public static <ELEMENTTYPE> ELEMENTTYPE getLast (@Nullable final ELEMENTTYPE... aArray)
  {
    return getLast (aArray, null);
  }

  /**
   * Get the last element of the array or the passed default if the passed array is empty.
   *
   * @param <ELEMENTTYPE>
   *        Array element type
   * @param aArray
   *        The array who's last element is to be retrieved. May be <code>null</code> or empty.
   * @param aDefaultValue
   *        The default value to be returned if the array is empty
   * @return the last element if the passed array is not empty, the default value if the passed
   *         array is empty.
   */
  @Nullable
  public static <ELEMENTTYPE> ELEMENTTYPE getLast (@Nullable final ELEMENTTYPE [] aArray,
                                                   @Nullable final ELEMENTTYPE aDefaultValue)
  {
    final int nSize = getSize (aArray);
    return nSize == 0 ? aDefaultValue : aArray[nSize - 1];
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
   * @return <code>null</code> if both array parameters are <code>null</code> - a
   *         non-<code>null</code> array with all elements in the correct order otherwise.
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
    final ELEMENTTYPE [] ret = createArraySameType (aHeadArray, aHeadArray.length + aTailArray.length);
    System.arraycopy (aHeadArray, 0, ret, 0, aHeadArray.length);
    System.arraycopy (aTailArray, 0, ret, aHeadArray.length, aTailArray.length);
    return ret;
  }

  /**
   * Get a new array that combines the passed head and the array. The head element will be the first
   * element of the created array.
   *
   * @param <ELEMENTTYPE>
   *        Array element type
   * @param aHead
   *        The first element of the result array. If this element is <code>null</code> it will be
   *        inserted as such into the array!
   * @param aTailArray
   *        The tail array. May be <code>null</code>.
   * @param aClass
   *        The element class. Must be present, because in case both elements are <code>null</code>
   *        there would be no way to create a new array. May not be <code>null</code>.
   * @return <code>null</code> if both array parameters are <code>null</code> - a
   *         non-<code>null</code> array with all elements in the correct order otherwise.
   */
  @NonNull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> ELEMENTTYPE [] getConcatenated (@Nullable final ELEMENTTYPE aHead,
                                                              @Nullable final ELEMENTTYPE [] aTailArray,
                                                              @NonNull final Class <ELEMENTTYPE> aClass)
  {
    if (isEmpty (aTailArray))
      return createArraySingleElement (aHead, aClass);

    // Start concatenating
    final ELEMENTTYPE [] ret = createArray (aClass, 1 + aTailArray.length);
    ret[0] = aHead;
    System.arraycopy (aTailArray, 0, ret, 1, aTailArray.length);
    return ret;
  }

  /**
   * Get a new array that combines the passed array and the tail element. The tail element will be
   * the last element of the created array.
   *
   * @param <ELEMENTTYPE>
   *        Array element type
   * @param aHeadArray
   *        The head array. May be <code>null</code>.
   * @param aTail
   *        The last element of the result array. If this element is <code>null</code> it will be
   *        inserted as such into the array!
   * @param aClass
   *        The element class. Must be present, because in case both elements are <code>null</code>
   *        there would be no way to create a new array. May not be <code>null</code>.
   * @return <code>null</code> if both array parameters are <code>null</code> - a
   *         non-<code>null</code> array with all elements in the correct order otherwise.
   */
  @NonNull
  @ReturnsMutableCopy
  public static <ELEMENTTYPE> ELEMENTTYPE [] getConcatenated (@Nullable final ELEMENTTYPE [] aHeadArray,
                                                              @Nullable final ELEMENTTYPE aTail,
                                                              @NonNull final Class <ELEMENTTYPE> aClass)
  {
    if (isEmpty (aHeadArray))
      return createArraySingleElement (aTail, aClass);

    // Start concatenating
    final ELEMENTTYPE [] ret = createArray (aClass, aHeadArray.length + 1);
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
   * @return <code>null</code> if both array parameters are <code>null</code> - a
   *         non-<code>null</code> array with all elements in the correct order otherwise.
   */
  @ReturnsMutableCopy
  public static boolean @Nullable [] getConcatenated (final boolean @Nullable [] aHeadArray,
                                            final boolean... aTailArray)
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
   * Get a new array that combines the passed head element and the array. The head element will be
   * the first element of the created array.
   *
   * @param aHead
   *        The first element of the result array.
   * @param aTailArray
   *        The tail array. May be <code>null</code>.
   * @return A non-<code>null</code> array with all elements in the correct order.
   */
  @ReturnsMutableCopy
  public static boolean @NonNull [] getConcatenated (final boolean aHead, final boolean... aTailArray)
  {
    if (isEmpty (aTailArray))
      return new boolean [] { aHead };

    final boolean [] ret = new boolean [1 + aTailArray.length];
    ret[0] = aHead;
    System.arraycopy (aTailArray, 0, ret, 1, aTailArray.length);
    return ret;
  }

  /**
   * Get a new array that combines the passed array and the tail element. The tail element will be
   * the last element of the created array.
   *
   * @param aHeadArray
   *        The head array. May be <code>null</code>.
   * @param aTail
   *        The last element of the result array.
   * @return A non-<code>null</code> array with all elements in the correct order.
   */
  @ReturnsMutableCopy
  public static boolean @NonNull [] getConcatenated (final boolean @Nullable [] aHeadArray, final boolean aTail)
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
   * @return <code>null</code> if both array parameters are <code>null</code> - a
   *         non-<code>null</code> array with all elements in the correct order otherwise.
   */
  @ReturnsMutableCopy
  public static byte @Nullable [] getConcatenated (final byte @Nullable [] aHeadArray, final byte... aTailArray)
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
   * Get a new array that combines the passed head element and the array. The head element will be
   * the first element of the created array.
   *
   * @param aHead
   *        The first element of the result array.
   * @param aTailArray
   *        The tail array. May be <code>null</code>.
   * @return A non-<code>null</code> array with all elements in the correct order.
   */
  @ReturnsMutableCopy
  public static byte @NonNull [] getConcatenated (final byte aHead, final byte... aTailArray)
  {
    if (isEmpty (aTailArray))
      return new byte [] { aHead };

    final byte [] ret = new byte [1 + aTailArray.length];
    ret[0] = aHead;
    System.arraycopy (aTailArray, 0, ret, 1, aTailArray.length);
    return ret;
  }

  /**
   * Get a new array that combines the passed array and the tail element. The tail element will be
   * the last element of the created array.
   *
   * @param aHeadArray
   *        The head array. May be <code>null</code>.
   * @param aTail
   *        The last element of the result array.
   * @return A non-<code>null</code> array with all elements in the correct order.
   */
  @ReturnsMutableCopy
  public static byte @NonNull [] getConcatenated (final byte @Nullable [] aHeadArray, final byte aTail)
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
   * @return <code>null</code> if both array parameters are <code>null</code> - a
   *         non-<code>null</code> array with all elements in the correct order otherwise.
   */
  @ReturnsMutableCopy
  public static char @Nullable [] getConcatenated (final char @Nullable [] aHeadArray, final char... aTailArray)
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
   * @return <code>null</code> if both array parameters are <code>null</code> - a
   *         non-<code>null</code> array with all elements in the correct order otherwise.
   */
  @ReturnsMutableCopy
  public static char @Nullable [] [] getConcatenated (final char @Nullable [] [] aHeadArray,
                                            final char @Nullable []... aTailArray)
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
   * Get a new array that combines the passed head element and the array. The head element will be
   * the first element of the created array.
   *
   * @param aHead
   *        The first element of the result array.
   * @param aTailArray
   *        The tail array. May be <code>null</code>.
   * @return A non-<code>null</code> array with all elements in the correct order.
   */
  @ReturnsMutableCopy
  public static char @NonNull [] getConcatenated (final char aHead, final char... aTailArray)
  {
    if (isEmpty (aTailArray))
      return new char [] { aHead };

    final char [] ret = new char [1 + aTailArray.length];
    ret[0] = aHead;
    System.arraycopy (aTailArray, 0, ret, 1, aTailArray.length);
    return ret;
  }

  /**
   * Get a new array that combines the passed array and the tail element. The tail element will be
   * the last element of the created array.
   *
   * @param aHeadArray
   *        The head array. May be <code>null</code>.
   * @param aTail
   *        The last element of the result array.
   * @return A non-<code>null</code> array with all elements in the correct order.
   */
  @ReturnsMutableCopy
  public static char @NonNull [] getConcatenated (final char @Nullable [] aHeadArray, final char aTail)
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
   * @return <code>null</code> if both array parameters are <code>null</code> - a
   *         non-<code>null</code> array with all elements in the correct order otherwise.
   */
  @ReturnsMutableCopy
  public static double @Nullable [] getConcatenated (final double @Nullable [] aHeadArray, final double... aTailArray)
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
   * Get a new array that combines the passed head element and the array. The head element will be
   * the first element of the created array.
   *
   * @param aHead
   *        The first element of the result array.
   * @param aTailArray
   *        The tail array. May be <code>null</code>.
   * @return A non-<code>null</code> array with all elements in the correct order.
   */
  @ReturnsMutableCopy
  public static double @NonNull [] getConcatenated (final double aHead, final double... aTailArray)
  {
    if (isEmpty (aTailArray))
      return new double [] { aHead };

    final double [] ret = new double [1 + aTailArray.length];
    ret[0] = aHead;
    System.arraycopy (aTailArray, 0, ret, 1, aTailArray.length);
    return ret;
  }

  /**
   * Get a new array that combines the passed array and the tail element. The tail element will be
   * the last element of the created array.
   *
   * @param aHeadArray
   *        The head array. May be <code>null</code>.
   * @param aTail
   *        The last element of the result array.
   * @return A non-<code>null</code> array with all elements in the correct order.
   */
  @ReturnsMutableCopy
  public static double @NonNull [] getConcatenated (final double @Nullable [] aHeadArray, final double aTail)
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
   * @return <code>null</code> if both array parameters are <code>null</code> - a
   *         non-<code>null</code> array with all elements in the correct order otherwise.
   */
  @ReturnsMutableCopy
  public static float @Nullable [] getConcatenated (final float @Nullable [] aHeadArray, final float... aTailArray)
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
   * Get a new array that combines the passed head element and the array. The head element will be
   * the first element of the created array.
   *
   * @param aHead
   *        The first element of the result array.
   * @param aTailArray
   *        The tail array. May be <code>null</code>.
   * @return A non-<code>null</code> array with all elements in the correct order.
   */
  @ReturnsMutableCopy
  public static float @NonNull [] getConcatenated (final float aHead, final float... aTailArray)
  {
    if (isEmpty (aTailArray))
      return new float [] { aHead };

    final float [] ret = new float [1 + aTailArray.length];
    ret[0] = aHead;
    System.arraycopy (aTailArray, 0, ret, 1, aTailArray.length);
    return ret;
  }

  /**
   * Get a new array that combines the passed array and the tail element. The tail element will be
   * the last element of the created array.
   *
   * @param aHeadArray
   *        The head array. May be <code>null</code>.
   * @param aTail
   *        The last element of the result array.
   * @return A non-<code>null</code> array with all elements in the correct order.
   */
  @ReturnsMutableCopy
  public static float @NonNull [] getConcatenated (final float @Nullable [] aHeadArray, final float aTail)
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
   * @return <code>null</code> if both array parameters are <code>null</code> - a
   *         non-<code>null</code> array with all elements in the correct order otherwise.
   */
  @ReturnsMutableCopy
  public static int @Nullable [] getConcatenated (final int @Nullable [] aHeadArray, final int... aTailArray)
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
   * Get a new array that combines the passed head element and the array. The head element will be
   * the first element of the created array.
   *
   * @param aHead
   *        The first element of the result array.
   * @param aTailArray
   *        The tail array. May be <code>null</code>.
   * @return A non-<code>null</code> array with all elements in the correct order.
   */
  @ReturnsMutableCopy
  public static int @NonNull [] getConcatenated (final int aHead, final int... aTailArray)
  {
    if (isEmpty (aTailArray))
      return new int [] { aHead };

    final int [] ret = new int [1 + aTailArray.length];
    ret[0] = aHead;
    System.arraycopy (aTailArray, 0, ret, 1, aTailArray.length);
    return ret;
  }

  /**
   * Get a new array that combines the passed array and the tail element. The tail element will be
   * the last element of the created array.
   *
   * @param aHeadArray
   *        The head array. May be <code>null</code>.
   * @param aTail
   *        The last element of the result array.
   * @return A non-<code>null</code> array with all elements in the correct order.
   */
  @ReturnsMutableCopy
  public static int @NonNull [] getConcatenated (final int @Nullable [] aHeadArray, final int aTail)
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
   * @return <code>null</code> if both array parameters are <code>null</code> - a
   *         non-<code>null</code> array with all elements in the correct order otherwise.
   */
  @ReturnsMutableCopy
  public static long @Nullable [] getConcatenated (final long @Nullable [] aHeadArray, final long... aTailArray)
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
   * Get a new array that combines the passed head element and the array. The head element will be
   * the first element of the created array.
   *
   * @param aHead
   *        The first element of the result array.
   * @param aTailArray
   *        The tail array. May be <code>null</code>.
   * @return A non-<code>null</code> array with all elements in the correct order.
   */
  @ReturnsMutableCopy
  public static long @NonNull [] getConcatenated (final long aHead, final long... aTailArray)
  {
    if (isEmpty (aTailArray))
      return new long [] { aHead };

    final long [] ret = new long [1 + aTailArray.length];
    ret[0] = aHead;
    System.arraycopy (aTailArray, 0, ret, 1, aTailArray.length);
    return ret;
  }

  /**
   * Get a new array that combines the passed array and the tail element. The tail element will be
   * the last element of the created array.
   *
   * @param aHeadArray
   *        The head array. May be <code>null</code>.
   * @param aTail
   *        The last element of the result array.
   * @return A non-<code>null</code> array with all elements in the correct order.
   */
  @ReturnsMutableCopy
  public static long @NonNull [] getConcatenated (final long @Nullable [] aHeadArray, final long aTail)
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
   * @return <code>null</code> if both array parameters are <code>null</code> - a
   *         non-<code>null</code> array with all elements in the correct order otherwise.
   */
  @ReturnsMutableCopy
  public static short @Nullable [] getConcatenated (final short @Nullable [] aHeadArray, final short... aTailArray)
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
   * Get a new array that combines the passed head element and the array. The head element will be
   * the first element of the created array.
   *
   * @param aHead
   *        The first element of the result array.
   * @param aTailArray
   *        The tail array. May be <code>null</code>.
   * @return A non-<code>null</code> array with all elements in the correct order.
   */
  @ReturnsMutableCopy
  public static short @NonNull [] getConcatenated (final short aHead, final short... aTailArray)
  {
    if (isEmpty (aTailArray))
      return new short [] { aHead };

    final short [] ret = new short [1 + aTailArray.length];
    ret[0] = aHead;
    System.arraycopy (aTailArray, 0, ret, 1, aTailArray.length);
    return ret;
  }

  /**
   * Get a new array that combines the passed array and the tail element. The tail element will be
   * the last element of the created array.
   *
   * @param aHeadArray
   *        The head array. May be <code>null</code>.
   * @param aTail
   *        The last element of the result array.
   * @return A non-<code>null</code> array with all elements in the correct order.
   */
  @ReturnsMutableCopy
  public static short @NonNull [] getConcatenated (final short @Nullable [] aHeadArray, final short aTail)
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
   * @return <code>null</code> if both array parameters are <code>null</code> - a
   *         non-<code>null</code> array with all elements in the correct order otherwise.
   */
  @NonNull
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
   * Get a new array that combines the passed head element and the array. The head element will be
   * the first element of the created array.
   *
   * @param aHead
   *        The first element of the result array.
   * @param aTailArray
   *        The tail array. May be <code>null</code>.
   * @return A non-<code>null</code> array with all elements in the correct order.
   */
  @NonNull
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
   * Get a new array that combines the passed array and the tail element. The tail element will be
   * the last element of the created array.
   *
   * @param aHeadArray
   *        The head array. May be <code>null</code>.
   * @param aTail
   *        The last element of the result array.
   * @return A non-<code>null</code> array with all elements in the correct order.
   */
  @NonNull
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
   * @return <code>null</code> if the passed array is <code>null</code> or has less than one
   *         element. A non-<code>null</code> copy of the array without the first element otherwise.
   */
  @Nullable
  @ReturnsMutableCopy
  @SafeVarargs
  public static <ELEMENTTYPE> ELEMENTTYPE [] getAllExceptFirst (@Nullable final ELEMENTTYPE... aArray)
  {
    return getAllExceptFirst (aArray, 1);
  }

  /**
   * Get an array that contains all elements, except for the first <em>n</em> elements.
   *
   * @param <ELEMENTTYPE>
   *        Array element type
   * @param aArray
   *        The source array. May be <code>null</code>.
   * @param nElementsToSkip
   *        The number of elements to skip. Must be &gt;= 0!
   * @return <code>null</code> if the passed array is <code>null</code> or has &le; elements than
   *         elements to be skipped. A non-<code>null</code> copy of the array without the first
   *         elements otherwise.
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
   * @return <code>null</code> if the passed array is <code>null</code>. The original array, if no
   *         elements need to be skipped. A non- <code>null</code> copy of the array without the
   *         passed elements otherwise.
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
   * @return <code>null</code> if the passed array is <code>null</code>. The original array, if no
   *         elements need to be skipped. A non- <code>null</code> copy of the array without the
   *         passed elements otherwise.
   */
  @ReturnsMutableCopy
  public static boolean @Nullable [] getAllExcept (final boolean @Nullable [] aArray,
                                         final boolean... aElementsToRemove)
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
   * @return <code>null</code> if the passed array is <code>null</code>. The original array, if no
   *         elements need to be skipped. A non- <code>null</code> copy of the array without the
   *         passed elements otherwise.
   */
  @ReturnsMutableCopy
  public static byte @Nullable [] getAllExcept (final byte @Nullable [] aArray, final byte... aElementsToRemove)
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
   * @return <code>null</code> if the passed array is <code>null</code>. The original array, if no
   *         elements need to be skipped. A non- <code>null</code> copy of the array without the
   *         passed elements otherwise.
   */
  @ReturnsMutableCopy
  public static char @Nullable [] getAllExcept (final char @Nullable [] aArray, final char... aElementsToRemove)
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
   * @return <code>null</code> if the passed array is <code>null</code>. The original array, if no
   *         elements need to be skipped. A non- <code>null</code> copy of the array without the
   *         passed elements otherwise.
   */
  @ReturnsMutableCopy
  public static double @Nullable [] getAllExcept (final double @Nullable [] aArray, final double... aElementsToRemove)
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
   * @return <code>null</code> if the passed array is <code>null</code>. The original array, if no
   *         elements need to be skipped. A non- <code>null</code> copy of the array without the
   *         passed elements otherwise.
   */
  @ReturnsMutableCopy
  public static float @Nullable [] getAllExcept (final float @Nullable [] aArray, final float... aElementsToRemove)
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
   * @return <code>null</code> if the passed array is <code>null</code>. The original array, if no
   *         elements need to be skipped. A non- <code>null</code> copy of the array without the
   *         passed elements otherwise.
   */
  @ReturnsMutableCopy
  public static int @Nullable [] getAllExcept (final int @Nullable [] aArray, final int... aElementsToRemove)
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
   * @return <code>null</code> if the passed array is <code>null</code>. The original array, if no
   *         elements need to be skipped. A non- <code>null</code> copy of the array without the
   *         passed elements otherwise.
   */
  @ReturnsMutableCopy
  public static long @Nullable [] getAllExcept (final long @Nullable [] aArray, final long... aElementsToRemove)
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
   * @return <code>null</code> if the passed array is <code>null</code>. The original array, if no
   *         elements need to be skipped. A non- <code>null</code> copy of the array without the
   *         passed elements otherwise.
   */
  @ReturnsMutableCopy
  public static short @Nullable [] getAllExcept (final short @Nullable [] aArray, final short... aElementsToRemove)
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
   * @return <code>null</code> if the passed array is <code>null</code> or has less than one
   *         element. A non-<code>null</code> copy of the array without the first element otherwise.
   */
  @ReturnsMutableCopy
  public static boolean @Nullable [] getAllExceptFirst (final boolean... aArray)
  {
    return getAllExceptFirst (aArray, 1);
  }

  /**
   * Get an array that contains all elements, except for the first <em>n</em> elements.
   *
   * @param aArray
   *        The source array. May be <code>null</code>.
   * @param nElementsToSkip
   *        The number of elements to skip. Must be &gt;= 0!
   * @return <code>null</code> if the passed array is <code>null</code> or has &le; elements than
   *         elements to be skipped. A non-<code>null</code> copy of the array without the first
   *         elements otherwise.
   */
  @ReturnsMutableCopy
  public static boolean @Nullable [] getAllExceptFirst (final boolean @Nullable [] aArray, @Nonnegative final int nElementsToSkip)
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
   * @return <code>null</code> if the passed array is <code>null</code> or has less than one
   *         element. A non-<code>null</code> copy of the array without the first element otherwise.
   */
  @ReturnsMutableCopy
  public static byte @Nullable [] getAllExceptFirst (final byte... aArray)
  {
    return getAllExceptFirst (aArray, 1);
  }

  /**
   * Get an array that contains all elements, except for the first <em>n</em> elements.
   *
   * @param aArray
   *        The source array. May be <code>null</code>.
   * @param nElementsToSkip
   *        The number of elements to skip. Must be &gt;= 0!
   * @return <code>null</code> if the passed array is <code>null</code> or has &le; elements than
   *         elements to be skipped. A non-<code>null</code> copy of the array without the first
   *         elements otherwise.
   */
  @ReturnsMutableCopy
  public static byte @Nullable [] getAllExceptFirst (final byte @Nullable [] aArray, @Nonnegative final int nElementsToSkip)
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
   * @return <code>null</code> if the passed array is <code>null</code> or has less than one
   *         element. A non-<code>null</code> copy of the array without the first element otherwise.
   */
  @ReturnsMutableCopy
  public static char @Nullable [] getAllExceptFirst (final char... aArray)
  {
    return getAllExceptFirst (aArray, 1);
  }

  /**
   * Get an array that contains all elements, except for the first <em>n</em> elements.
   *
   * @param aArray
   *        The source array. May be <code>null</code>.
   * @param nElementsToSkip
   *        The number of elements to skip. Must be &gt;= 0!
   * @return <code>null</code> if the passed array is <code>null</code> or has &le; elements than
   *         elements to be skipped. A non-<code>null</code> copy of the array without the first
   *         elements otherwise.
   */
  @ReturnsMutableCopy
  public static char @Nullable [] getAllExceptFirst (final char @Nullable [] aArray, @Nonnegative final int nElementsToSkip)
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
   * @return <code>null</code> if the passed array is <code>null</code> or has less than one
   *         element. A non-<code>null</code> copy of the array without the first element otherwise.
   */
  @ReturnsMutableCopy
  public static double @Nullable [] getAllExceptFirst (final double... aArray)
  {
    return getAllExceptFirst (aArray, 1);
  }

  /**
   * Get an array that contains all elements, except for the first <em>n</em> elements.
   *
   * @param aArray
   *        The source array. May be <code>null</code>.
   * @param nElementsToSkip
   *        The number of elements to skip. Must be &gt;= 0!
   * @return <code>null</code> if the passed array is <code>null</code> or has &le; elements than
   *         elements to be skipped. A non-<code>null</code> copy of the array without the first
   *         elements otherwise.
   */
  @ReturnsMutableCopy
  public static double @Nullable [] getAllExceptFirst (final double @Nullable [] aArray, @Nonnegative final int nElementsToSkip)
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
   * @return <code>null</code> if the passed array is <code>null</code> or has less than one
   *         element. A non-<code>null</code> copy of the array without the first element otherwise.
   */
  @ReturnsMutableCopy
  public static float @Nullable [] getAllExceptFirst (final float... aArray)
  {
    return getAllExceptFirst (aArray, 1);
  }

  /**
   * Get an array that contains all elements, except for the first <em>n</em> elements.
   *
   * @param aArray
   *        The source array. May be <code>null</code>.
   * @param nElementsToSkip
   *        The number of elements to skip. Must be &gt;= 0!
   * @return <code>null</code> if the passed array is <code>null</code> or has &le; elements than
   *         elements to be skipped. A non-<code>null</code> copy of the array without the first
   *         elements otherwise.
   */
  @ReturnsMutableCopy
  public static float @Nullable [] getAllExceptFirst (final float @Nullable [] aArray, @Nonnegative final int nElementsToSkip)
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
   * @return <code>null</code> if the passed array is <code>null</code> or has less than one
   *         element. A non-<code>null</code> copy of the array without the first element otherwise.
   */
  @ReturnsMutableCopy
  public static int @Nullable [] getAllExceptFirst (final int... aArray)
  {
    return getAllExceptFirst (aArray, 1);
  }

  /**
   * Get an array that contains all elements, except for the first <em>n</em> elements.
   *
   * @param aArray
   *        The source array. May be <code>null</code>.
   * @param nElementsToSkip
   *        The number of elements to skip. Must be &gt;= 0!
   * @return <code>null</code> if the passed array is <code>null</code> or has &le; elements than
   *         elements to be skipped. A non-<code>null</code> copy of the array without the first
   *         elements otherwise.
   */
  @ReturnsMutableCopy
  public static int @Nullable [] getAllExceptFirst (final int @Nullable [] aArray, @Nonnegative final int nElementsToSkip)
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
   * @return <code>null</code> if the passed array is <code>null</code> or has less than one
   *         element. A non-<code>null</code> copy of the array without the first element otherwise.
   */
  @ReturnsMutableCopy
  public static long @Nullable [] getAllExceptFirst (final long... aArray)
  {
    return getAllExceptFirst (aArray, 1);
  }

  /**
   * Get an array that contains all elements, except for the first <em>n</em> elements.
   *
   * @param aArray
   *        The source array. May be <code>null</code>.
   * @param nElementsToSkip
   *        The number of elements to skip. Must be &gt;= 0!
   * @return <code>null</code> if the passed array is <code>null</code> or has &le; elements than
   *         elements to be skipped. A non-<code>null</code> copy of the array without the first
   *         elements otherwise.
   */
  @ReturnsMutableCopy
  public static long @Nullable [] getAllExceptFirst (final long @Nullable [] aArray, @Nonnegative final int nElementsToSkip)
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
   * @return <code>null</code> if the passed array is <code>null</code> or has less than one
   *         element. A non-<code>null</code> copy of the array without the first element otherwise.
   */
  @ReturnsMutableCopy
  public static short @Nullable [] getAllExceptFirst (final short... aArray)
  {
    return getAllExceptFirst (aArray, 1);
  }

  /**
   * Get an array that contains all elements, except for the first <em>n</em> elements.
   *
   * @param aArray
   *        The source array. May be <code>null</code>.
   * @param nElementsToSkip
   *        The number of elements to skip. Must be &gt;= 0!
   * @return <code>null</code> if the passed array is <code>null</code> or has &le; elements than
   *         elements to be skipped. A non-<code>null</code> copy of the array without the first
   *         elements otherwise.
   */
  @ReturnsMutableCopy
  public static short @Nullable [] getAllExceptFirst (final short @Nullable [] aArray, @Nonnegative final int nElementsToSkip)
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
   * @return <code>null</code> if the passed array is <code>null</code> or has less than one
   *         element. A non-<code>null</code> copy of the array without the last element otherwise.
   */
  @Nullable
  @ReturnsMutableCopy
  @SafeVarargs
  public static <ELEMENTTYPE> ELEMENTTYPE [] getAllExceptLast (@Nullable final ELEMENTTYPE... aArray)
  {
    return getAllExceptLast (aArray, 1);
  }

  /**
   * Get an array that contains all elements, except for the last <em>n</em> elements.
   *
   * @param <ELEMENTTYPE>
   *        Type of element
   * @param aArray
   *        The source array. May be <code>null</code>.
   * @param nElementsToSkip
   *        The number of elements to skip. Must be &gt;= 0!
   * @return <code>null</code> if the passed array is <code>null</code> or has &le; elements than
   *         elements to be skipped. A non-<code>null</code> copy of the array without the last
   *         elements otherwise.
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
   * @return <code>null</code> if the passed array is <code>null</code> or has less than one
   *         element. A non-<code>null</code> copy of the array without the last element otherwise.
   */
  @ReturnsMutableCopy
  public static boolean @Nullable [] getAllExceptLast (final boolean... aArray)
  {
    return getAllExceptLast (aArray, 1);
  }

  /**
   * Get an array that contains all elements, except for the last <em>n</em> elements.
   *
   * @param aArray
   *        The source array. May be <code>null</code>.
   * @param nElementsToSkip
   *        The number of elements to skip. Must be &gt;= 0!
   * @return <code>null</code> if the passed array is <code>null</code> or has &le; elements than
   *         elements to be skipped. A non-<code>null</code> copy of the array without the last
   *         elements otherwise.
   */
  @ReturnsMutableCopy
  public static boolean @Nullable [] getAllExceptLast (final boolean @Nullable [] aArray, @Nonnegative final int nElementsToSkip)
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
   * @return <code>null</code> if the passed array is <code>null</code> or has less than one
   *         element. A non-<code>null</code> copy of the array without the last element otherwise.
   */
  @ReturnsMutableCopy
  public static byte @Nullable [] getAllExceptLast (final byte... aArray)
  {
    return getAllExceptLast (aArray, 1);
  }

  /**
   * Get an array that contains all elements, except for the last <em>n</em> elements.
   *
   * @param aArray
   *        The source array. May be <code>null</code>.
   * @param nElementsToSkip
   *        The number of elements to skip. Must be &gt;= 0!
   * @return <code>null</code> if the passed array is <code>null</code> or has &le; elements than
   *         elements to be skipped. A non-<code>null</code> copy of the array without the last
   *         elements otherwise.
   */
  @ReturnsMutableCopy
  public static byte @Nullable [] getAllExceptLast (final byte @Nullable [] aArray, @Nonnegative final int nElementsToSkip)
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
   * @return <code>null</code> if the passed array is <code>null</code> or has less than one
   *         element. A non-<code>null</code> copy of the array without the last element otherwise.
   */
  @ReturnsMutableCopy
  public static char @Nullable [] getAllExceptLast (final char... aArray)
  {
    return getAllExceptLast (aArray, 1);
  }

  /**
   * Get an array that contains all elements, except for the last <em>n</em> elements.
   *
   * @param aArray
   *        The source array. May be <code>null</code>.
   * @param nElementsToSkip
   *        The number of elements to skip. Must be &gt;= 0!
   * @return <code>null</code> if the passed array is <code>null</code> or has &le; elements than
   *         elements to be skipped. A non-<code>null</code> copy of the array without the last
   *         elements otherwise.
   */
  @ReturnsMutableCopy
  public static char @Nullable [] getAllExceptLast (final char @Nullable [] aArray, @Nonnegative final int nElementsToSkip)
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
   * @return <code>null</code> if the passed array is <code>null</code> or has less than one
   *         element. A non-<code>null</code> copy of the array without the last element otherwise.
   */
  @ReturnsMutableCopy
  public static double @Nullable [] getAllExceptLast (final double... aArray)
  {
    return getAllExceptLast (aArray, 1);
  }

  /**
   * Get an array that contains all elements, except for the last <em>n</em> elements.
   *
   * @param aArray
   *        The source array. May be <code>null</code>.
   * @param nElementsToSkip
   *        The number of elements to skip. Must be &gt;= 0!
   * @return <code>null</code> if the passed array is <code>null</code> or has &le; elements than
   *         elements to be skipped. A non-<code>null</code> copy of the array without the last
   *         elements otherwise.
   */
  @ReturnsMutableCopy
  public static double @Nullable [] getAllExceptLast (final double @Nullable [] aArray, @Nonnegative final int nElementsToSkip)
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
   * @return <code>null</code> if the passed array is <code>null</code> or has less than one
   *         element. A non-<code>null</code> copy of the array without the last element otherwise.
   */
  @ReturnsMutableCopy
  public static float @Nullable [] getAllExceptLast (final float... aArray)
  {
    return getAllExceptLast (aArray, 1);
  }

  /**
   * Get an array that contains all elements, except for the last <em>n</em> elements.
   *
   * @param aArray
   *        The source array. May be <code>null</code>.
   * @param nElementsToSkip
   *        The number of elements to skip. Must be &gt;= 0!
   * @return <code>null</code> if the passed array is <code>null</code> or has &le; elements than
   *         elements to be skipped. A non-<code>null</code> copy of the array without the last
   *         elements otherwise.
   */
  @ReturnsMutableCopy
  public static float @Nullable [] getAllExceptLast (final float @Nullable [] aArray, @Nonnegative final int nElementsToSkip)
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
   * @return <code>null</code> if the passed array is <code>null</code> or has less than one
   *         element. A non-<code>null</code> copy of the array without the last element otherwise.
   */
  @ReturnsMutableCopy
  public static int @Nullable [] getAllExceptLast (final int... aArray)
  {
    return getAllExceptLast (aArray, 1);
  }

  /**
   * Get an array that contains all elements, except for the last <em>n</em> elements.
   *
   * @param aArray
   *        The source array. May be <code>null</code>.
   * @param nElementsToSkip
   *        The number of elements to skip. Must be &gt;= 0!
   * @return <code>null</code> if the passed array is <code>null</code> or has &le; elements than
   *         elements to be skipped. A non-<code>null</code> copy of the array without the last
   *         elements otherwise.
   */
  @ReturnsMutableCopy
  public static int @Nullable [] getAllExceptLast (final int @Nullable [] aArray, @Nonnegative final int nElementsToSkip)
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
   * @return <code>null</code> if the passed array is <code>null</code> or has less than one
   *         element. A non-<code>null</code> copy of the array without the last element otherwise.
   */
  @ReturnsMutableCopy
  public static long @Nullable [] getAllExceptLast (final long... aArray)
  {
    return getAllExceptLast (aArray, 1);
  }

  /**
   * Get an array that contains all elements, except for the last <em>n</em> elements.
   *
   * @param aArray
   *        The source array. May be <code>null</code>.
   * @param nElementsToSkip
   *        The number of elements to skip. Must be &gt;= 0!
   * @return <code>null</code> if the passed array is <code>null</code> or has &le; elements than
   *         elements to be skipped. A non-<code>null</code> copy of the array without the last
   *         elements otherwise.
   */
  @ReturnsMutableCopy
  public static long @Nullable [] getAllExceptLast (final long @Nullable [] aArray, @Nonnegative final int nElementsToSkip)
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
   * @return <code>null</code> if the passed array is <code>null</code> or has less than one
   *         element. A non-<code>null</code> copy of the array without the last element otherwise.
   */
  @ReturnsMutableCopy
  public static short @Nullable [] getAllExceptLast (final short... aArray)
  {
    return getAllExceptLast (aArray, 1);
  }

  /**
   * Get an array that contains all elements, except for the last <em>n</em> elements.
   *
   * @param aArray
   *        The source array. May be <code>null</code>.
   * @param nElementsToSkip
   *        The number of elements to skip. Must be &gt;= 0!
   * @return <code>null</code> if the passed array is <code>null</code> or has &le; elements than
   *         elements to be skipped. A non-<code>null</code> copy of the array without the last
   *         elements otherwise.
   */
  @ReturnsMutableCopy
  public static short @Nullable [] getAllExceptLast (final short @Nullable [] aArray, @Nonnegative final int nElementsToSkip)
  {
    ValueEnforcer.isGE0 (nElementsToSkip, "ElementsToSkip");

    if (nElementsToSkip == 0)
      return aArray;
    if (aArray == null || nElementsToSkip >= aArray.length)
      return null;
    return getCopy (aArray, 0, aArray.length - nElementsToSkip);
  }

  /**
   * Get the passed collection as an array of Object. If the passed collection is <code>null</code>
   * or empty, an empty array is returned.
   *
   * @param aCollection
   *        The collection to be converted. May be <code>null</code>.
   * @return Never <code>null</code>.
   */
  @Nullable
  @ReturnsMutableCopy
  public static Object [] getAsObjectArray (@Nullable final Collection <?> aCollection)
  {
    if (aCollection == null || aCollection.isEmpty ())
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
   * Check if the passed object is an array or not.
   *
   * @param aObject
   *        The object to be checked. May be <code>null</code>.
   * @return <code>true</code> if the passed object is not <code>null</code> and represents an
   *         array.
   */
  public static boolean isArray (@Nullable final Object aObject)
  {
    return aObject != null && aObject.getClass ().isArray ();
  }

  /**
   * Recursive equal comparison for arrays.
   *
   * @param aHeadArray
   *        First array. May be <code>null</code>.
   * @param aTailArray
   *        Second array. May be <code>null</code>.
   * @return <code>true</code> only if the arrays and all contained elements are recursively equal.
   */
  public static boolean isArrayEquals (@Nullable final Object aHeadArray, @Nullable final Object aTailArray)
  {
    // Same objects?
    if (EqualsHelper.identityEqual (aHeadArray, aTailArray))
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
   * @return <code>true</code> only if the passed array is neither <code>null</code> nor empty and
   *         if at least one <code>null</code> element is contained.
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
   * @return <code>true</code> only if the passed array is neither <code>null</code> nor empty and
   *         if at least one <code>null</code> element is contained.
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

  @Nullable
  public static <ELEMENTTYPE, RETTYPE> RETTYPE findFirstMapped (@Nullable final ELEMENTTYPE [] aArray,
                                                                @Nullable final Predicate <? super ELEMENTTYPE> aFilter,
                                                                @NonNull final Function <? super ELEMENTTYPE, RETTYPE> aMapper)
  {
    return findFirstMapped (aArray, aFilter, aMapper, (RETTYPE) null);
  }

  @Nullable
  public static <ELEMENTTYPE, RETTYPE> RETTYPE findFirstMapped (@Nullable final ELEMENTTYPE [] aArray,
                                                                @Nullable final Predicate <? super ELEMENTTYPE> aFilter,
                                                                @NonNull final Function <? super ELEMENTTYPE, RETTYPE> aMapper,
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

  public static <ELEMENTTYPE> void forEach (@Nullable final ELEMENTTYPE [] aArray,
                                            @NonNull final Consumer <? super ELEMENTTYPE> aConsumer)
  {
    if (isNotEmpty (aArray))
      for (final ELEMENTTYPE aElement : aArray)
        aConsumer.accept (aElement);
  }

  public static <ELEMENTTYPE> void forEach (@Nullable final ELEMENTTYPE [] aArray,
                                            @NonNull final ObjIntConsumer <? super ELEMENTTYPE> aConsumer)
  {
    if (isNotEmpty (aArray))
    {
      int nIndex = 0;
      for (final ELEMENTTYPE aElement : aArray)
      {
        aConsumer.accept (aElement, nIndex);
        ++nIndex;
      }
    }
  }

  public static <ELEMENTTYPE> void forEach (@Nullable final ELEMENTTYPE [] aArray,
                                            @Nullable final Predicate <? super ELEMENTTYPE> aFilter,
                                            @NonNull final Consumer <? super ELEMENTTYPE> aConsumer)
  {
    if (aFilter == null)
      forEach (aArray, aConsumer);
    else
    {
      if (isNotEmpty (aArray))
        for (final ELEMENTTYPE aElement : aArray)
          if (aFilter.test (aElement))
            aConsumer.accept (aElement);
    }
  }
}
